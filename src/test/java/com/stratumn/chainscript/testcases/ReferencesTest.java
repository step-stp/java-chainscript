package com.stratumn.chainscript.testcases;

import java.util.Arrays;
import java.util.Base64;

import com.stratumn.chainscript.Link;
import com.stratumn.chainscript.LinkBuilder;
import com.stratumn.chainscript.LinkReference;
import com.stratumn.chainscript.Segment;
import com.stratumn.chainscript.TestUtil; 

/**
 * Test a segment with custom data and metadata but no references, evidences
 * or signatures.
 */
public class ReferencesTest implements ITestCase
{
   public static final String id = "segment-references";

   @Override
   public String getId()
   {
     return ReferencesTest.id;
   }
   
   @Override
   public String generate() throws Exception  
   {
      Segment segment = new LinkBuilder("test_process", "test_map")
         .withRefs(new LinkReference[] { 
            new LinkReference( new byte[] {42} , "p1"),
            new LinkReference( new byte[]  {24} , "p2")
         }).build().segmentify(); 
      return Base64.getEncoder().encodeToString(segment.serialize());
   }

   @Override
   public void validate(String encodedSegment) throws Exception
   {
      Segment segment = Segment.deserialize(Base64.getDecoder().decode(encodedSegment));
      segment.validate();
      Link link = segment.link();
      LinkReference[] linkRefs = link.refs();

      if(linkRefs.length != 2)
      {
         throw new Exception("Invalid references count: " + linkRefs.length);
      }
      if(!linkRefs[0].getProcess().equals("p1"))
      {
         throw new Exception("Invalid first reference process: " + linkRefs[0].getProcess());
      }

      if(!Arrays.equals(linkRefs[0].getLinkHash(),new byte[] {42}))
      {
         throw new Exception("Invalid first reference link hash: " + TestUtil.convertByteArrToIntArr(linkRefs[0].getLinkHash()));
      }

      if(!linkRefs[1].getProcess().equals("p2"))
      {
         throw new Exception("Invalid second reference process: " + linkRefs[1].getProcess());
      }
      
      if(!Arrays.equals(linkRefs[1].getLinkHash(), new byte[] {24}))
      {
         throw new Exception("Invalid second reference link hash: " + TestUtil.convertByteArrToIntArr(linkRefs[1].getLinkHash()));
      } 
   }  
}