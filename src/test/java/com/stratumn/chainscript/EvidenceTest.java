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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import com.google.protobuf.ByteString;

class EvidenceTest
{

   @Test
   @DisplayName("converts from valid proto")
   void testEvidence()
   {

      stratumn.chainscript.Chainscript.Evidence e = stratumn.chainscript.Chainscript.Evidence.newBuilder().setVersion("1.0.0").setBackend("bitcoin").setProvider("testnet")
         .setProof(ByteString.copyFrom(new byte[]{42 })).build();

      Evidence evidence = assertDoesNotThrow(new ThrowingSupplier<Evidence>()
      {
         @Override
         public Evidence get() throws Throwable
         {
            return Evidence.fromProto(e);
         }
      });
      assertEquals(evidence.getVersion(), e.getVersion());
      assertEquals(evidence.getBackend(), e.getBackend());
      assertEquals(evidence.getProvider(), e.getProvider());
      assertTrue(Arrays.equals(evidence.getProof(), e.getProof().toByteArray()));
   }

   @Test
   @DisplayName("evidence")
   void testValidate()
   {

      assertThrows(ChainscriptException.class, () -> {
         new Evidence("", "bitcoin", "testnet", new byte[]{42 });
      }, "rejects missing version");

      assertThrows(ChainscriptException.class, () -> {
         new Evidence("1.0.0", "", "testnet", new byte[]{42 });

      }, "rejects missing backend");

      assertThrows(ChainscriptException.class, () -> {

         new Evidence("1.0.0", "bitcoin", "", new byte[]{42 });

      }, "rejects missing provider");

      assertThrows(ChainscriptException.class, () -> {
         new Evidence("1.0.0", "bitcoin", "testnet", new byte[]{});
      }, "rejects missing proof");

      assertThrows(ChainscriptException.class, () -> {
         Evidence.fromProto(stratumn.chainscript.Chainscript.Evidence.newBuilder().build());
      }, "rejects invalid proto");
   }

   @Test
   @DisplayName("serializes and deserializes")
   void testSerialize() throws Exception
   {
      byte[] bytArr = new byte[]{42, 24 };
      Evidence e = new Evidence("1.0.0", "btc", "mainnet", bytArr);
      byte[] bytes = e.serialize();
      Evidence d = Evidence.deserialize(bytes);
      assertTrue(Arrays.equals( d.getProof() ,  e.getProof() ));
      assertEquals(d.getVersion(), e.getVersion());
      assertEquals(d.getBackend(), e.getBackend());
      assertEquals(d.getProvider(), e.getProvider());
   }
 
}
