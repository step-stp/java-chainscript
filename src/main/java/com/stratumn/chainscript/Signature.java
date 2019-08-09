/*
  Copyright 2017 Stratumn SAS. All rights reserved.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/ 
package com.stratumn.chainscript;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.stratumn.chainscript.utils.CryptoUtils;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;

/**
 * A signature of configurable parts of a link.
 * Different signature types and versions are allowed to sign different
 * encodings of the data, but we recommend signing a hash of the
 * protobuf-encoded bytes.
 */
public class Signature
{
   private stratumn.chainscript.Chainscript.Signature signature;

   /**
    * @param s
    */
   public Signature(stratumn.chainscript.Chainscript.Signature signature)
   {
      this.signature = signature;
   }

   /**
    * @return the signature
    */
   public stratumn.chainscript.Chainscript.Signature getSignature()
   {
      return signature;
   }

   /**
    * @param signature the signature to set
    */
   public void setSignature(stratumn.chainscript.Chainscript.Signature signature)
   {
      this.signature = signature;
   }

   /**
    * @returns the version of the signature scheme.
    */
   public String version()
   {
      return StringUtils.isEmpty(this.signature.getVersion()) ? "" : this.signature.getVersion();
   }

   /**
    * @returns the algorithm used (for example, "EdDSA").
    */
   public String type()
   {
      return StringUtils.isEmpty(this.signature.getType()) ? "" : this.signature.getType();
   }

   /**
    * @returns a description of the parts of the link that are signed.
    */
   public String payloadPath()
   {
      return StringUtils.isEmpty(this.signature.getPayloadPath()) ? "" : this.signature.getPayloadPath();
   }

   /**
    * @returns the public key of the signer.
    */
   public byte[] publicKey()
   {
      return (this.signature.getPublicKey() != null ? this.signature.getPublicKey() : ByteString.EMPTY).toByteArray();
   }

   /**
    * @returns the signature bytes.
    */
   public byte[] signature()
   {
      return (this.signature.getSignature() != null ? this.signature.getSignature() : ByteString.EMPTY).toByteArray();
   }

   /**
    * Validate the signature and throw an exception if invalid.
    * @param link the link signed.
    * @throws ChainscriptException 
    */
   public void validate(Link link) throws ChainscriptException
   {
      if(this.publicKey() == null || this.publicKey().length  == 0)
      {
         throw new ChainscriptException(Error.SignaturePublicKeyMissing );
      }

      if(this.signature() == null || this.signature().length == 0)
      {
         throw new ChainscriptException(Error.SignatureMissing );
      }

      switch(this.version())
      {
         case Constants.SIGNATURE_VERSION_1_0_0:
            byte[] signed = link.signedBytes(this.version(), this.payloadPath());
           
            String publicKeyString  =   Base64.getEncoder().encodeToString(  this.publicKey() )  ;
                
            try {
               EdDSAPublicKey publicKey = CryptoUtils.decodeEd25519PublicKey( publicKeyString ); 
               if(!CryptoUtils.verify(publicKey,   signed  ,new String(  signature()) ))
               { 
                  throw new ChainscriptException(Error.SignatureInvalid);
               }
            }catch (InvalidKeySpecException | InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
               throw new ChainscriptException(e);
            }
            return;
         default:
            throw new ChainscriptException(Error.SignatureVersionUnknown );
      }
   }
   
   
   /**
    * Sign bytes with the current signature version.
    * @param key private key in PEM format (generated by @stratumn/js-crypto).
    * @param toSign bytes that should be signed.
    * @throws Exception 
    */
   public static Signature sign(byte[] key, byte[] toSign) throws  ChainscriptException
   {
      String signedMessage;
      EdDSAPublicKey publicKey =null;
      try
      {  
           
         EdDSAPrivateKey privateKey =  new EdDSAPrivateKey(new PKCS8EncodedKeySpec(key));
         
         signedMessage = CryptoUtils.sign(privateKey, toSign);
         publicKey = CryptoUtils.recoverEd25519PublicKey(privateKey);
      }
      catch(InvalidKeyException | NoSuchAlgorithmException | SignatureException | InvalidKeySpecException e)
      {
         throw new  ChainscriptException( "Could not create the private key / signature",e);
      }

      stratumn.chainscript.Chainscript.Signature sig =
         stratumn.chainscript.Chainscript.Signature.newBuilder()
         .setVersion(Constants.SIGNATURE_VERSION)
         .setSignature(ByteString.copyFrom(signedMessage.getBytes()))
         .setPublicKey(ByteString.copyFrom(publicKey.getEncoded()))
         .build(); 
      return new Signature(sig);
   }

   /**
    * Sign configurable parts of the given link with the current signature
    * version.
    * The payloadPath is used to select what parts of the link need to be signed
    * with the given private key. If no payloadPath is provided, the whole link
    * is signed.
    * @param key private key in PEM format (generated by @stratumn/js-crypto).
    * @param link that should be signed.
    * @param payloadPath link parts that should be signed.
    * @throws Exception 
    */
   public static Signature signLink(byte[] key, Link link, String payloadPath) throws Exception
   {
      // We want to make it explicit when we're signing the whole link.
      if(StringUtils.isEmpty(payloadPath))
      {
         payloadPath = "[version,data,meta]";
      }
      byte[] toSign = link.signedBytes(Constants.SIGNATURE_VERSION, payloadPath);
      Signature signature = sign(key, toSign);

      stratumn.chainscript.Chainscript.Signature sig = stratumn.chainscript.Chainscript.Signature.newBuilder()
         .setVersion(signature.version())
         .setPayloadPath(payloadPath)
         .setPublicKey(ByteString.copyFrom(signature.publicKey()))
         .setSignature(ByteString.copyFrom(signature.signature())).build();

      return new Signature(sig);
   }

}