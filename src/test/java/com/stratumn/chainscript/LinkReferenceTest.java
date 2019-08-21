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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LinkReferenceTest
{

   @Test
   void testLinkReference()
   {
      byte[] linkHash =  new byte[]{42 } ;
      assertThrows(ChainscriptException.class, () -> {
         new LinkReference(linkHash, "");
      }, "rejects missing process");

      assertThrows(ChainscriptException.class, () -> {
         new LinkReference(new byte[]{}, "p");
      }, "rejects missing link hash");

      LinkReference l = assertDoesNotThrow(() -> {
         return new LinkReference(linkHash, "p");
      }, "creates a valid reference");

      assertEquals(l.getProcess(), "p");
      assertArrayEquals(l.getLinkHash(), new byte[]{42 });

      }

   }
