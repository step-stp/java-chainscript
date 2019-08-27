package com.stratumn.chainscript.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import org.junit.jupiter.api.Test;

import com.stratumn.chainscript.Constants;
import com.stratumn.chainscript.utils.CryptoUtils;

public class TestCryptoUtils {

    String publicKeyPEM = "-----BEGIN ED25519 PUBLIC KEY-----\nMCowBQYDK2VwAyEAEIwjKUueKwu2s+ie5aFAsYBn8OEL7GHjEPML3JgxOEs=\n-----END ED25519 PUBLIC KEY-----\n";
    String privateKeyPEM = "-----BEGIN ED25519 PRIVATE KEY-----\nMFACAQAwBwYDK2VwBQAEQgRAG4bBxUz5/UFzaCCxlhmpbKtZE313fsfY+hviGNRr\n5RYQjCMpS54rC7az6J7loUCxgGfw4QvsYeMQ8wvcmDE4Sw==\n-----END ED25519 PRIVATE KEY-----\n";
    String signature = "-----BEGIN MESSAGE-----\ncGEkdtv4MEZerv5pHS3fjDFk2ZX9vJwydFbQFUhcKsP/jp+6PueDcCokKU7CuxyB3F3QMJ0YfMxh7eg7MQmdBA==\n-----END MESSAGE-----\n";
   
  byte[] msg = "This is a secret message".getBytes(Constants.UTF8);

  @Test
  public void testVerify() throws Exception {

    PublicKey pub = CryptoUtils.decodePublicKey(publicKeyPEM);
    assertTrue(CryptoUtils.verify(pub, msg, signature),"Verifying signature using public key failed.");
  }

  @Test
  public void testSign()
      throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

//    PublicKey pub = CryptoUtils.decodePublicKey(publicKeyPEM);
    PrivateKey priv = CryptoUtils.decodePrivateKey(privateKeyPEM);
    String encodedSig = CryptoUtils.sign(priv, msg);
    assertEquals(encodedSig.trim(),signature.trim());
  }
  
  @Test
  public void testSignwithEncodedKey() throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
    PrivateKey priv = CryptoUtils.decodePrivateKey(privateKeyPEM);
    PublicKey pub = CryptoUtils.decodePublicKey(publicKeyPEM);
    byte[] pkbytes = priv.getEncoded();
    //decode from bytes
    priv = CryptoUtils.decodePrivateKey(pkbytes);
    String encodedSig = CryptoUtils.sign(priv, msg);
    assertTrue(CryptoUtils.verify(pub, msg, encodedSig) , "Verifying signature of encoded/decoded using public key failed");
  }
  
//  String CryptoUtils.encodePrivateKey(priv);
  @Test
  public void testPublicKeyGeneration() throws InvalidKeySpecException, InvalidKeyException
  {
     PrivateKey priv = CryptoUtils.decodePrivateKey(privateKeyPEM);
     PublicKey pub = CryptoUtils.getPublicKeyFromPrivateKey(priv);
     String pubkey = CryptoUtils.encodePublicKey(pub);
     assertEquals(pubkey.trim(), publicKeyPEM.trim());
     
  }
  
  @Test
  public void testPrivateKeyDecodeEncode() throws InvalidKeySpecException, InvalidKeyException
  {
     PrivateKey privateKey = CryptoUtils.decodePrivateKey(privateKeyPEM);
     PublicKey publicKey=CryptoUtils.getPublicKeyFromPrivateKey(privateKey);
     
     String pubPem= Base64.getEncoder().encodeToString(publicKey.getEncoded());
     
     byte[] keyByte=privateKey.getEncoded();
     
     PrivateKey privateKey2=CryptoUtils.decodePrivateKey(keyByte);
     PublicKey publicKey2=CryptoUtils.getPublicKeyFromPrivateKey(privateKey2);
     
     String pubPem2= Base64.getEncoder().encodeToString(publicKey2.getEncoded());
     
     assertEquals(pubPem, pubPem2);
  }
  
  
}
