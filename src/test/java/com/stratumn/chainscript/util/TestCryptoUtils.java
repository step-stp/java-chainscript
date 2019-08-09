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
