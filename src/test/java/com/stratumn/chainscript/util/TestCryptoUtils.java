package com.stratumn.chainscript.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import org.junit.jupiter.api.Test;

import com.stratumn.chainscript.utils.CryptoUtils;

public class TestCryptoUtils {

  String pk = "-----BEGIN ED25519 PUBLIC KEY-----\nMCowBQYDK2VwAyEAEIwjKUueKwu2s+ie5aFAsYBn8OEL7GHjEPML3JgxOEs=\n-----END ED25519 PUBLIC KEY-----";
  String sk = "-----BEGIN ED25519 PRIVATE KEY-----\nMFACAQAwBwYDK2VwBQAEQgRAG4bBxUz5/UFzaCCxlhmpbKtZE313fsfY+hviGNRr\n5RYQjCMpS54rC7az6J7loUCxgGfw4QvsYeMQ8wvcmDE4Sw==\n-----END ED25519 PRIVATE KEY-----\n";
  String signature = "-----BEGIN MESSAGE-----\ncGEkdtv4MEZerv5pHS3fjDFk2ZX9vJwydFbQFUhcKsP/jp+6PueDcCokKU7CuxyB\n3F3QMJ0YfMxh7eg7MQmdBA==\n-----END MESSAGE-----\n";
  byte[] msg = "This is a secret message".getBytes(Charset.forName("UTF-8"));

  @Test
  public void testVerify() throws Exception {

    PublicKey pub = CryptoUtils.decodePublicKey(pk);
    assertTrue(CryptoUtils.verify(pub, msg, signature));
  }

  @Test
  public void testSign()
      throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

    PublicKey pub = CryptoUtils.decodePublicKey(pk);
    PrivateKey priv = CryptoUtils.decodePrivateKey(sk);

    String encodedSig = CryptoUtils.sign(priv, msg);
    assertTrue(CryptoUtils.verify(pub, msg, encodedSig));
  }
  
  @Test
  public void testPublicKeyGeneration() throws InvalidKeySpecException, InvalidKeyException
  {
     PrivateKey priv = CryptoUtils.decodePrivateKey(sk);
     PublicKey pub = CryptoUtils.getPublicKeyFromPrivateKey(priv);
     String pubkey = CryptoUtils.encodePublicKey(pub);
     System.out.println(pubkey);
     System.out.println(pk);
     assertEquals(pubkey, pk);
     
  }
  
  
}
