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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.stratumn.canonicaljson.CanonicalJson;
 
 
class LinkTest
{

   public static final String customData = "{ \"name\": \"Sponge\", \"surname\": \"Capture dÃ¢â‚¬â„¢eÃŒï¿½cran.png\" }";

   public static final String customMetadata = "{ \"name\": \"Batman\", age: 42 }";

   /**
    * Create a valid test link for version 1.0.0.
    */
   private Link createLinkV1()
   {
      stratumn.chainscript.Chainscript.Process process = stratumn.chainscript.Chainscript.Process.newBuilder().setName("test_process").build();

      stratumn.chainscript.Chainscript.LinkMeta meta =
         stratumn.chainscript.Chainscript.LinkMeta.newBuilder().setClientId("github.com/stratumn/go-chainscript").setMapId("test_map").setProcess(process).build();

      stratumn.chainscript.Chainscript.Link link = stratumn.chainscript.Chainscript.Link.newBuilder().setVersion("1.0.0").setMeta(meta).build();

      return new Link(link);
   }

   
   
   @SuppressWarnings("unchecked")
   @Test
   void testLinkData() throws IOException, Exception
   {
      Link link = createLinkV1();
         Object dataMap= CanonicalJson.parse(customData);
         link.setData(dataMap);
         Object data = link.data();
         assertTrue(TestUtil.compareMaps((Map<String, ?>)data, (Map<String, ?>)dataMap));

   }

   @Test
   void testLinkMetaData() throws Exception
   {
      Link link = createLinkV1();
      
         link.setMetadata(customMetadata);
         Object metadata = link.metadata();
         assertEquals(metadata, customMetadata);
      
   }

   @Test
   void testLinkHash() throws Exception
   {
      Link link = createLinkV1();
     
         byte[] h1 = link.hash();
         assertEquals(32, h1.length);
      
   }
 
   
   
   @Test
   void testLinkSignedBytes() throws Exception
   {

      String version = Constants.SIGNATURE_VERSION_1_0_0;

         Link link = new LinkBuilder("p", "m").withData("batman").build();
         byte[] b1 = link.signedBytes(version, "[version,data,meta]");
         byte[] b2 = link.signedBytes(version, "");
         assertArrayEquals( b2, b1);
   }

   @Test
   void testLinkReferences()
   {
      try
      {
         LinkReference ref1 = new LinkReference(new byte[]{42 }, "p1");
         LinkReference ref2 = new LinkReference(new byte[]{42 }, "p2");

         Link link = new LinkBuilder("p1", "m1").withRefs(new LinkReference[]{ref1, ref2 }).build();
         link.validate();
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
   }
   

}
