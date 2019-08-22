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
package com.stratumn.chainscript.utils;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.KeyPairGenerator;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

 
public class CryptoUtils
{
   static EdDSANamedCurveSpec ed25519Spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);

   public static String cleanToken(String token) {
      token = token.replaceAll("-----.*?-----\n", "");
      token = token.replaceAll("\n-----.*-----", "");
      return token;

  }
   public static String encodeSignature(byte[] sig)
   {
      return String.format("-----BEGIN MESSAGE-----\n%s\n-----END MESSAGE-----", Base64.getEncoder().encodeToString(sig));
   }

   public static byte[] decodeSignature(String sig)
   {
      String s = sig.replaceAll("\n", "").replace("-----BEGIN MESSAGE-----", "").replace("-----END MESSAGE-----", "");
      return Base64.getDecoder().decode(s);
   }

   public static String sign(PrivateKey key, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
   {
      Signature sgr = new EdDSAEngine(MessageDigest.getInstance(ed25519Spec.getHashAlgorithm()));
      sgr.initSign(key);
      sgr.update(message);
      byte[] sig = sgr.sign();
      return CryptoUtils.encodeSignature(sig);
   }

   
   public static boolean verify(PublicKey key, byte[] message, String encodedSig) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
   {
      byte[] sig = CryptoUtils.decodeSignature(encodedSig);

      Signature sgr = new EdDSAEngine(MessageDigest.getInstance(ed25519Spec.getHashAlgorithm()));
      sgr.initVerify(key);
      sgr.update(message);
      return sgr.verify(sig);
   }

   // In our systems, the PKCS#8 encoded ed25519 private key actually contains the
   // public key (cf tweetnacl.js and go implementations). The java library though
   // fails if the privateKey part of the ASN1 is more than 32 bytes.
   // The long term solution would be to implement a real PEM ASN1
   // serializer/deserializer.
   // In the mean time, we manually extract the seed from the encoded key, which is
   // the 32 first bytes  of the privateKey section of the ASN1 encoded data.
   public static PrivateKey decodePrivateKey(String pem) throws InvalidKeySpecException
   {
      String sk = pem.replaceAll("\n", "").replace("-----BEGIN ED25519 PRIVATE KEY-----", "").replace("-----END ED25519 PRIVATE KEY-----", "");

      byte[] skBytes = Base64.getDecoder().decode(sk);
      byte[] seed = Arrays.copyOfRange(skBytes, 18, 50); 
      
      EdDSAPrivateKeySpec key = new EdDSAPrivateKeySpec(seed, ed25519Spec); 
      return new EdDSAPrivateKey(key);
   }
   
   /***
    * Creates a private key from byte array.
    * @param keyBytes
    * @return
    * @throws InvalidKeySpecException
    */
   public static PrivateKey decodePrivateKey(byte[] keyBytes) throws InvalidKeySpecException
   {
      EdDSAPrivateKey privateKey =  new EdDSAPrivateKey(new PKCS8EncodedKeySpec(keyBytes));
      return privateKey;  
   }

/***
 * 
 * @param pem
 * @return
 * @throws InvalidKeySpecException
 */
   public static PublicKey decodePublicKey(String pem) throws InvalidKeySpecException
   {
      String pk = pem.replaceAll("\n", "").replace("-----BEGIN ED25519 PUBLIC KEY-----", "").replace("-----END ED25519 PUBLIC KEY-----", "");

      byte[] pkBytes = Base64.getDecoder().decode(pk);
      X509EncodedKeySpec encoded = new X509EncodedKeySpec(pkBytes);
      return new EdDSAPublicKey(encoded);
   }
   
   
   /***
    * Encodes a public key to PEM
    * @param key
    * @return
    * @throws InvalidKeySpecException
    */
   public static String encodePublicKey(PublicKey key) throws InvalidKeySpecException
   {
      EdDSAPublicKey edDSAPublicKey = (EdDSAPublicKey) key;
      String pem = Base64.getEncoder().encodeToString(edDSAPublicKey.getEncoded() );
      StringBuffer keyBuff = new StringBuffer();
      keyBuff.append("-----BEGIN ED25519 PUBLIC KEY-----\n")
      .append( pem  )
      .append("\n-----END ED25519 PUBLIC KEY-----");
      return keyBuff.toString();
   }
   
   public static String encodePrivateKey(PrivateKey key) throws InvalidKeySpecException
   {
      EdDSAPrivateKey edDSAPrivateKey = (EdDSAPrivateKey) key;
      String pem = Base64.getEncoder().encodeToString(edDSAPrivateKey.getEncoded() );
      StringBuffer keyBuff = new StringBuffer();
      keyBuff.append("-----BEGIN ED25519 PRIVATE KEY-----\n")
      .append(pem)
      .append("\n-----END ED25519 PRIVATE KEY-----\n");
      return keyBuff.toString();
   }
   
   /***
    * Recover public key from private key.
    * @param key
    * @return
    * @throws InvalidKeyException 
    * @throws GeneralSecurityException
    */
   public static PublicKey getPublicKeyFromPrivateKey(PrivateKey key) throws InvalidKeyException {
        
      EdDSAPrivateKey privKey = (EdDSAPrivateKey) key;
      EdDSAPublicKeySpec keySpec = new EdDSAPublicKeySpec(privKey.getA(), privKey.getParams());
         
      EdDSAPublicKey vKey = new EdDSAPublicKey(keySpec);
      return vKey; 
    }
   
   /***
    * Generates a key pair randomly.
    * @return
    * @throws InvalidAlgorithmParameterException
    */
    public static KeyPair generateKeyPair() throws InvalidAlgorithmParameterException
    { 
       KeyPairGenerator generator = new KeyPairGenerator();
       generator.initialize(ed25519Spec , new SecureRandom());  
       KeyPair pair =  generator.generateKeyPair();   
       return pair;
    }
     
    /***
     * provides a SHA256 hash of the the inputbytes
     * @param inputBytes
     * @return
     */
    public static byte[] sha256(byte[] inputBytes)
    {
       MessageDigest digest;
        try
        {
           digest = MessageDigest.getInstance("SHA-256");
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new RuntimeException("SHA-256 is not available",e);
        }
       byte[] resultBytes = digest.digest(inputBytes);
       return resultBytes;
    }
    
    /***
     * Provides a SHA516 has of inputbytes
     * @param inputBytes
     * @return
     */
    public static byte[]  sha512(byte[] inputBytes)
    {
       MessageDigest digest;
       try
       { 
          digest = MessageDigest.getInstance("SHA-512");
       }  
       catch(NoSuchAlgorithmException e)
       {
          throw new RuntimeException(e);
       }
       
       byte[] resultBytes = digest.digest(inputBytes);
       return resultBytes; 
    }

}
