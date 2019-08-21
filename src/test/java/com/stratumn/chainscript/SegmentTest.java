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
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.protobuf.ByteString;

class SegmentTest
{

   private Evidence createEvidence() throws Exception
   {
      return new Evidence("0.1.0", "bitcoin", "testnet", new byte[]{42 });
   }

   @Test
   void testCreateSegment()
   {
      stratumn.chainscript.Chainscript.LinkMeta linkMeta = stratumn.chainscript.Chainscript.LinkMeta.newBuilder().setClientId("github.com/stratumn/go-chainscript").build();
      stratumn.chainscript.Chainscript.Link link = stratumn.chainscript.Chainscript.Link.newBuilder().setVersion("1.0.0").setMeta(linkMeta).build();
      stratumn.chainscript.Chainscript.SegmentMeta segmentMeta =
         stratumn.chainscript.Chainscript.SegmentMeta.newBuilder().setLinkHash(ByteString.copyFrom(new byte[]{42, 42 })).build();
      stratumn.chainscript.Chainscript.Segment pbSegment = stratumn.chainscript.Chainscript.Segment.newBuilder().setLink(link).setMeta(segmentMeta).build();

      try
      {
         Segment segmentObj = new Segment(pbSegment);

         // assertEquals(segmentObj.linkHash().length, 32);
         assertEquals(segmentObj.link().clientId(), "github.com/stratumn/go-chainscript");

         // serializes and deserializes correctly
         Segment segment = new LinkBuilder("p", "m").withAction("init").withData("{ name: \"spongebob\" }").withPriority(42).withTags(new String[]{"tag" }).build().segmentify();

         Evidence btcEvidence = new Evidence("0.1.0", "bitcoin", "testnet", new byte[]{42 });

         segment.addEvidence(btcEvidence);

         byte[] serialized = segment.serialize();
         Segment segment2 = Segment.deserialize(serialized);
         segment2.validate();

         assertEquals(segment2.link().action(), "init");
         assertEquals(segment2.link().priority(), 42);
         assertTrue(Arrays.equals(segment2.link().tags(), new String[]{"tag" }));

         assertTrue(Arrays.equals(segment2.linkHash(), segment.linkHash()));
         assertTrue(Arrays.equals(segment2.linkHash(), segment.link().hash()));
         assertEquals(segment2.evidences().length, 1);

         assertEquals(segment2.evidences()[0].getVersion(), btcEvidence.getVersion());
         assertEquals(segment2.evidences()[0].getBackend(), btcEvidence.getBackend());
         assertEquals(segment2.evidences()[0].getProvider(), btcEvidence.getProvider());
         // Protobuf uses a buffer implementation that's portable between
         // browser and node to represent bytes.
         // We can't directly compare the objects because their type won't
         // match, so we compare the data inside.
         assertEquals(segment2.evidences()[0].getProof().length, 1);
         assertEquals(segment2.evidences()[0].getProof()[0], 42);
      }
      catch(Exception e)
      {
         fail();
         e.printStackTrace();
      }

   }

   @Test
   void testDuplicateEvidence()
   {
      try
      {
         Evidence e = createEvidence();

         Segment segment = new LinkBuilder("p", "m").build().segmentify();
         segment.addEvidence(e);
         final Exception thrown = assertThrows(Exception.class, () -> {
            segment.addEvidence(e);
         });
         assertEquals("evidence already exists for the given backend and provider", thrown.getMessage());
      }
      catch(Exception e1)
      {
         fail();
         e1.printStackTrace();
      }
   }

   @Test
   void testFindEvidence()
   {
      try
      {
         // finds valid evidences
         Evidence e1 = createEvidence();
         Evidence e2 = createEvidence();
         e2.setBackend("ethereum");

         Segment segment = new LinkBuilder("p", "m").build().segmentify();
         segment.addEvidence(e1);
         segment.addEvidence(e2);

         assertEquals(segment.findEvidences("ethereum")[0].getBackend(), e2.getBackend());
         assertEquals(segment.evidences().length, 2);
      }
      catch(Exception e1)
      {
         fail();
         e1.printStackTrace();
      }
   }

   @Test
   void testValidate() throws Exception
   {
      stratumn.chainscript.Chainscript.LinkMeta linkMeta = stratumn.chainscript.Chainscript.LinkMeta.newBuilder()
         .setAction("init")
         .setMapId("mapid") 
         .build();
      stratumn.chainscript.Chainscript.Link link = stratumn.chainscript.Chainscript.Link.newBuilder()
         .setVersion("1.0.0")
         .setMeta(linkMeta).build();

      stratumn.chainscript.Chainscript.Segment pbSegment = stratumn.chainscript.Chainscript.Segment.newBuilder().setLink(link).build();

      Segment segment;

      segment = new Segment(pbSegment);
      // Mutate the underlying link.
      link= link.toBuilder().setMeta(link.getMeta().toBuilder().setAction("override").build()).build();
      
      Field linkField = segment.getClass().getDeclaredField("pbLink");
      linkField.setAccessible(true);
      linkField.set(segment, link);
      linkField.setAccessible(false);
       
      final Exception thrown = assertThrows(Exception.class, () -> {
         segment.validate();
      });
      assertEquals("link hash mismatch", thrown.getMessage());

   }

   @Test
   @DisplayName("rejects unknown version")
   void testUnknownVersion()
   {
         @SuppressWarnings("unused")
         final Exception thrown = assertThrows(ChainscriptException.class, () -> {
   
            stratumn.chainscript.Chainscript.Segment pbSegment = stratumn.chainscript.Chainscript.Segment.newBuilder().build();
            Segment segment = new Segment(pbSegment);
         });
         assertEquals(Error.LinkMissing.toString(), thrown.getMessage());
      }
      
      @Test
      @DisplayName("rejects missing link")
      void tesMissingLink()
   {
      @SuppressWarnings("unused")
      final Exception thrown = assertThrows(ChainscriptException.class, () -> {

         stratumn.chainscript.Chainscript.Segment pbSegment = stratumn.chainscript.Chainscript.Segment.newBuilder().build();
         Segment segment = new Segment(pbSegment);
      });
      assertEquals(Error.LinkMissing.toString(), thrown.getMessage());
   }

   

}
