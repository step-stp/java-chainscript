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
*/ package com.stratumn.chainscript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.protobuf.ByteString;
import com.stratumn.chainscript.utils.CryptoUtils;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.KeyPairGenerator;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import stratumn.chainscript.Chainscript.Signature.Builder;

class SignatureTest
{

   /***
    * "wraps a proto signature"
    */
   @Test
   @DisplayName("wraps a proto signature")
   void testSignature()
   {
      stratumn.chainscript.Chainscript.Signature sig = stratumn.chainscript.Chainscript.Signature.newBuilder().setVersion("0.1.0")
         .setPayloadPath("[data]").setPublicKey(ByteString.copyFrom( new byte[]{42 } ))
         .setSignature(ByteString.copyFrom( new byte[]{42 } )).build(); 
      
      Signature sigObj = new Signature(sig);
      assertEquals(sigObj.version(), sig.getVersion());
      assertEquals(sigObj.payloadPath(), sig.getPayloadPath());
      assertTrue(Arrays.equals( sigObj.publicKey(), sig.getPublicKey().toByteArray() ));
      assertTrue(Arrays.equals( sigObj.signature(), sig.getSignature().toByteArray() )); 
   }
   
   
   @Test
   @DisplayName("Signing simple message")
   void testSign() throws  Exception 
   {  
      String sk = "-----BEGIN ED25519 PRIVATE KEY-----\nInvalid key\n-----END ED25519 PRIVATE KEY-----\n";
      String msg = "This is a sample message";
      EdDSANamedCurveSpec edDsaSpec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
      KeyPairGenerator generator = new KeyPairGenerator();
      generator.initialize(edDsaSpec , new SecureRandom());
      
      KeyPair pair = (generator).generateKeyPair(); 
 
         assertThrows(Exception.class, ()->{
      CryptoUtils.sign( CryptoUtils.decodeEd25519PrivateKey(sk) 
         ,   new byte[] {42, 24, 24, 42} );
      });  
      String signed= CryptoUtils.sign(  (EdDSAPrivateKey) pair.getPrivate() ,  msg.getBytes()); 
      
      assertTrue(CryptoUtils.verify((EdDSAPublicKey)pair.getPublic(), msg.getBytes(), signed));
       
   }
 
   @Test
   @DisplayName("Can sign the whole link")
   void testSignLink () throws Exception
   {
      EdDSANamedCurveSpec edDsaSpec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519); 
      KeyPairGenerator generator = new KeyPairGenerator();
      generator.initialize(edDsaSpec , new SecureRandom());  
      KeyPair pair = (generator).generateKeyPair(); 
      byte[] privateKey = pair.getPrivate().getEncoded();
       
      
      Link lnk = new LinkBuilder("p", "m").build();
      Signature sig = Signature.signLink(privateKey, lnk , "");
      
      assertEquals( lnk.signatures().length,0);
      assertEquals(  sig.payloadPath(),"[version,data,meta]");
      
      sig.validate(lnk);
   }
      
   
   @Test
   @DisplayName("Can sign parts of the link")
   void testSignLinkParts () throws Exception
   {
      EdDSANamedCurveSpec edDsaSpec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
      KeyPairGenerator generator = new KeyPairGenerator();
      generator.initialize(edDsaSpec , new SecureRandom());
      
      KeyPair pair = (generator).generateKeyPair(); 
 
      Link lnk = new LinkBuilder("p", "m").build();
      Signature sig = Signature.signLink(pair.getPrivate().getEncoded(), lnk ,"[version,data,meta]");
      
      assertEquals( lnk.signatures().length,0);
      assertEquals(  sig.payloadPath(),"[version,data,meta]");
      
      sig.validate(lnk);
   }
      
   

   @Test
   @DisplayName("uses empty default values")
   void testSignatureDefault()
   {
      Signature sigObj = new Signature(stratumn.chainscript.Chainscript.Signature.newBuilder().build());
      assertEquals(sigObj.version(), "");
      assertEquals(sigObj.payloadPath(), "");

      assertTrue(sigObj.publicKey().length == 0);
      assertTrue(sigObj.signature().length == 0);

   }
 

   @Test
   void testValidate()
   {
      Builder sigBuilder = stratumn.chainscript.Chainscript.Signature.newBuilder();
       
      sigBuilder.setVersion("1.0.0")
      .setPayloadPath("[data]")
      .setPublicKey(ByteString.copyFrom( new byte[]{42 }) )
       .setSignature(ByteString.copyFrom( new byte[]{42 } ));
      
      Builder sig1 = sigBuilder.clone().setVersion("0.42.0");
      assertEquals(
         assertThrows(ChainscriptException.class, ()->{
            new Signature(sig1.build()).validate(new LinkBuilder("p", "m").build());
            },"unknown version")
         .getError(),Error.SignatureVersionUnknown);
      
      
      Builder sig2 = sigBuilder.clone().setPublicKey(ByteString.EMPTY); 
      assertEquals(
         assertThrows(ChainscriptException.class, ()->{ 
            new Signature(sig2.build()).validate(new LinkBuilder("p", "m").build());
            },"missing public key").getError(),Error.SignaturePublicKeyMissing);
      
      Builder sig3 = sigBuilder.clone().setSignature(ByteString.EMPTY); 
      assertEquals(
         assertThrows(ChainscriptException.class, ()->{ 
            new Signature(sig3.build()).validate(new LinkBuilder("p", "m").build());
            },"missing signature").getError(),Error.SignatureMissing);
      
      
   }

}
