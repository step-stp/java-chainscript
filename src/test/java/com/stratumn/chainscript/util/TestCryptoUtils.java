package com.stratumn.chainscript.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import org.junit.jupiter.api.Test;

import com.stratumn.chainscript.utils.CryptoUtils;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;

public class TestCryptoUtils {

  String pk = "-----BEGIN ED25519 PUBLIC KEY-----\nMCowBQYDK2VwAyEAEIwjKUueKwu2s+ie5aFAsYBn8OEL7GHjEPML3JgxOEs=\n-----END ED25519 PUBLIC KEY-----\n";
  String sk = "-----BEGIN ED25519 PRIVATE KEY-----\nMFACAQAwBwYDK2VwBQAEQgRAG4bBxUz5/UFzaCCxlhmpbKtZE313fsfY+hviGNRr\n5RYQjCMpS54rC7az6J7loUCxgGfw4QvsYeMQ8wvcmDE4Sw==\n-----END ED25519 PRIVATE KEY-----\n";
  String signature = "-----BEGIN MESSAGE-----\ncGEkdtv4MEZerv5pHS3fjDFk2ZX9vJwydFbQFUhcKsP/jp+6PueDcCokKU7CuxyB\n3F3QMJ0YfMxh7eg7MQmdBA==\n-----END MESSAGE-----\n";
  byte[] msg = "This is a secret message".getBytes(Charset.forName("UTF-8"));

  @Test
  public void testVerify() throws Exception {

    EdDSAPublicKey pub = CryptoUtils.decodeEd25519PublicKey(pk);
    assertTrue(CryptoUtils.verify(pub, msg, signature));
  }

  @Test
  public void testSign()
      throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

    EdDSAPublicKey pub = CryptoUtils.decodeEd25519PublicKey(pk);
    EdDSAPrivateKey priv = CryptoUtils.decodeEd25519PrivateKey(sk);

    String encodedSig = CryptoUtils.sign(priv, msg);
    assertTrue(CryptoUtils.verify(pub, msg, encodedSig));
  }
  
  @Test
  public void testGenerate () throws InvalidAlgorithmParameterException, InvalidKeyException
  {
       KeyPair pair = CryptoUtils.generateED25519();
       EdDSAPrivateKey privkey = (EdDSAPrivateKey) pair.getPrivate();
       EdDSAPublicKey pubkey = (EdDSAPublicKey) pair.getPublic();
       
       EdDSAPublicKey pubkey2 = CryptoUtils.recoverEd25519PublicKey(privkey);
       
       assertEquals(pubkey , pubkey2 );    
  }
}
