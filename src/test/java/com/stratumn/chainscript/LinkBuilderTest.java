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
package com.stratumn.chainscript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LinkBuilderTest
{

   @Test
   @DisplayName("link builder")
   void testLinkVersion() throws Exception
   {

      Link link = new LinkBuilder("p", "m").withAction("a").build();
      assertEquals(link.version(), "1.0.0");
      assertEquals(link.clientId(), Constants.ClientId);
      assertEquals(link.process().getName(), "p");
      assertEquals(link.mapId(), "m");
      assertEquals(link.outDegree(), -1);
      assertEquals(link.action(), "a");

   }

   @Test
   @DisplayName("process")
   void testMissingProcess()
   {
      final Exception thrown = assertThrows(Exception.class, () -> {
         new LinkBuilder("", "m").build();
      });
      assertEquals("link process is missing", thrown.getMessage());
   }

   @Test
   @DisplayName("action")
   void testWithAction() throws Exception
   {
      Link link;

      link = new LinkBuilder("p", "m").withAction("a").withDegree(1).build();
      assertEquals(link.action(), "a");
      assertEquals(link.outDegree(), 1);
   }

   @Test
   @DisplayName("data")
   void testWithData() throws Exception
   {
      Link link;

      link = new LinkBuilder("p", "m").withData(" score: 42 ").build();
      assertEquals(link.data(), " score: 42 ");
   }

   @Test
   @DisplayName("sets custom link metadata")
   void testWithMetaData() throws Exception
   {
      Link link = new LinkBuilder("p", "m").withMetadata(" updated_count: 24 ").build();
      assertEquals(link.metadata(), " updated_count: 24 ");
   }

   @Test
   @DisplayName("sets parent link hash")
   void testWithLinkHash() throws Exception
   {
      byte[] parent = new byte[]{42, 42 };

      Link link;

      link = new LinkBuilder("p", "m").withParent(parent).build();
      assertTrue(Arrays.equals(link.prevLinkHash(), new byte[]{42, 42 }));

   }

   @Test
   @DisplayName("sets tags multiple times")
   void testWithTags() throws Exception
   {
      Link link = new LinkBuilder("p", "m").withTags(new String[]{"tag1", "tag2" }).withTags(new String[]{"tag3" }).build();
      assertTrue(Arrays.equals(link.tags(), new String[]{"tag1", "tag2", "tag3" }));
   }

   @Test
   @DisplayName("filters empty tags")
   void testWithEmptyTags() throws Exception
   {
      Link link = new LinkBuilder("p", "m").withTags(new String[]{"tag", "" }).build();
      assertTrue(Arrays.equals(link.tags(), new String[]{"tag" }));
   }

   @Test
   @DisplayName("adds multiple references")
   void testWitRreferences() throws Exception
   {
      LinkReference ref1 = new LinkReference(new byte[]{42 }, "p1");
      LinkReference ref2 = new LinkReference(new byte[]{42 }, "p2");

      Link link = new LinkBuilder("p", "m").withRefs(new LinkReference[]{ref1 }).withRefs(new LinkReference[]{ref2 }).build();
      LinkReference[] refs = link.refs();
      assertEquals(refs.length, 2);
      assertEquals(refs[0].getProcess(), ref1.getProcess());
      assertTrue(Arrays.equals(refs[0].getLinkHash(), ref1.getLinkHash()));
      assertEquals(refs[1].getProcess(), ref2.getProcess());
      assertTrue(Arrays.equals(refs[1].getLinkHash(), ref2.getLinkHash()));
   }

   @Test
   @DisplayName("rejects empty link hash")
   void testEmptyParent()
   {
      final Exception thrown = assertThrows(ChainscriptException.class, () -> {
         new LinkBuilder("p", "m").withParent(new byte[0]).build();
      });
      assertEquals(Error.LinkHashMissing.toString(), thrown.getMessage());
   }

   @Test
   @DisplayName("rejects negative priority")
   void testNegativePriority()
   {
      final Exception thrown = assertThrows(ChainscriptException.class, () -> {
         new LinkBuilder("p", "m").withPriority(-0.42).build();
      });
      assertEquals(Error.LinkPriorityNotPositive.toString(), thrown.getMessage());
   }

   @Test
   @DisplayName("sets process state")
   void testSetProcessState() throws Exception
   {
      Link link = new LinkBuilder("p", "m").withProcessState("documents sent").build();
      assertEquals(link.process().getState(), "documents sent");
   }

   @Test
   @DisplayName("sets process step")
   void testSetProcessStep() throws Exception
   {
      Link link = new LinkBuilder("p", "m").withStep("signing documents").build();
      assertEquals(link.step(), "signing documents");
   }

}
