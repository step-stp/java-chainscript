package com.stratumn.chainscript;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;

import net.i2p.crypto.eddsa.EdDSAPublicKey;


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
   public ByteString publicKey()
   {
      return this.signature.getPublicKey() != null ? this.signature.getPublicKey() : ByteString.EMPTY;
   }

   /**
    * @returns the signature bytes.
    */
   public ByteString signature()
   {
      return this.signature.getSignature() != null ? this.signature.getSignature() : ByteString.EMPTY;
   }

   

   /**
    * Validate the signature and throw an exception if invalid.
    * @param link the link signed.
    * @throws Exception 
    */
   public void validate(Link link) throws Exception
   {
      if(this.publicKey() == null || this.publicKey().size() == 0)
      {
         throw new Exception("signature public key is missing");
      }

      if(this.signature() == null || this.signature().size() == 0)
      {
         throw new Exception("signature bytes are missing");
      }

      switch(this.version())
      {
         case Constants.SIGNATURE_VERSION_1_0_0:
            byte[] signed = link.signedBytes(this.version(), this.payloadPath());
            // TODO How to handle the double encoding with atob() JS function?
            byte[] publicKeyBytes = Base64.getDecoder().decode(this.publicKey().toByteArray());

            EdDSAPublicKey publicKey = CryptoUtils.decodeEd25519PublicKey(new String(publicKeyBytes));

            if(!CryptoUtils.verify(publicKey, signed, this.signature().toStringUtf8()))
            {
               throw new Exception("signature is invalid");
            }
            return;
         default:
            throw new Exception("unknown signature version");
      }
   }
}
