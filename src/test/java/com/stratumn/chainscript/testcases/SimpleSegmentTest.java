package com.stratumn.chainscript.testcases;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Map;

import com.stratumn.canonicaljson.CanonicalJson;
import com.stratumn.chainscript.Link;
import com.stratumn.chainscript.LinkBuilder;
import com.stratumn.chainscript.Segment;

/**
 * Test a segment with custom data and metadata but no references, evidences
 * or signatures.
 */
public class SimpleSegmentTest implements ITestCase
{
   public static final String id = "simple-segment";

   @Override
   public String getId()
   {
      return SimpleSegmentTest.id;
   }

   @Override
   public String generate() throws Exception
   {
      Segment segment = new LinkBuilder("test_process", "test_map").withAction("init")
         .withData(CanonicalJson.parse("{ \"name\": \"ʙᴀᴛᴍᴀɴ\", \"age\": 42 }")).withDegree(3).withMetadata("bruce wayne")
         .withParent(new byte[]{42, 42 }).withPriority(42).withProcessState("started").withStep("setup").withTags(new String[]{"tag1", "tag2" })
         .build().segmentify();
      return Base64.getEncoder().encodeToString(segment.serialize());
   }

   @Override
   public void validate(String encodedSegment) throws Exception
   {
      Segment segment = Segment.deserialize(Base64.getDecoder().decode(encodedSegment));
      segment.validate();
      Link link = segment.link();
      if(!link.action().equals("init"))
      {
         throw new Exception(String.format("Invalid action: %s", link.action()));
      }
      @SuppressWarnings("unchecked")
      Map<String, Object> data = (Map<String, Object>) link.data();
      if(!data.get("age").equals(new BigDecimal(42)))
      {
         throw new Exception(String.format("Invalid data: %s", CanonicalJson.stringify(link.data())));
      }
      if(!data.get("name").equals("ʙᴀᴛᴍᴀɴ"))
      {
         throw new Exception(String.format("Invalid data: %s", CanonicalJson.stringify(link.data())));
      }
      if(link.outDegree() != 3)
      {
         throw new Exception(String.format("Invalid degree: %s", link.outDegree()));
      }
      if(!link.mapId().equals("test_map"))
      {
         throw new Exception(String.format("Invalid map id:  %s", link.mapId()));
      }
      if(!link.metadata().equals("bruce wayne"))
      {
         throw new Exception(String.format("Invalid metadata:  %s", CanonicalJson.stringify(link.metadata())));
      }
      if(link.prevLinkHash()[0] != 42 || link.prevLinkHash()[1] != 42)
      {
         throw new Exception(String.format("Invalid parent: %s", link.prevLinkHash().toString()));
      }
      if(link.priority() != 42)
      {
         throw new Exception(String.format("Invalid priority: %s", link.priority()));
      }
      if(!link.process().getName().equals("test_process"))
      {
         throw new Exception(String.format("Invalid process name: %s ", link.process().getName()));
      }
      if(!link.process().getState().equals("started"))
      {
         throw new Exception(String.format("Invalid process state: %s ", link.process().getState()));
      }
      if(!link.step().equals("setup"))
      {
         throw new Exception(String.format("Invalid step: %s", link.step()));
      }
      if(!"tag1".equals(link.tags()[0]) || !"tag2".equals(link.tags()[1]))
      {
         throw new Exception(String.format("Invalid tags: %s", (Object) link.tags()));
      }
   }
}