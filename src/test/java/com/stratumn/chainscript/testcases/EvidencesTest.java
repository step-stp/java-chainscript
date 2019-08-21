package com.stratumn.chainscript.testcases;

import java.util.Base64;

import com.google.gson.Gson;
import com.stratumn.chainscript.Evidence;
import com.stratumn.chainscript.LinkBuilder;
import com.stratumn.chainscript.Segment; 

/**
 * Test a segment with custom data and metadata but no references, evidences
 * or signatures.
 */
public class EvidencesTest implements ITestCase
{
   public static final String id = "segment-evidences";

   @Override
   public String getId()
   {
     return EvidencesTest.id;
   }
   
   @Override
   public String generate() throws Exception  
   {
      Segment segment = new LinkBuilder("test_process", "test_map") 
      .build()
      .segmentify();
      
      segment.addEvidence(
         new Evidence("0.1.0", "bitcoin", "testnet",  new byte[]{42} )
       );
      segment.addEvidence(
         new Evidence("1.0.3", "ethereum", "mainnet",  new byte[]{24} )
       );
 
      return Base64.getEncoder().encodeToString(segment.serialize());
   }

   @Override
   public void validate(String encodedSegment) throws Exception
   {
       Segment segment = Segment.deserialize(Base64.getDecoder().decode(encodedSegment));
       segment.validate();
       
       if (segment.evidences().length != 2)
          throw new Exception("Invalid evidences count: " + segment.evidences().length );
       
       Evidence btc = segment.getEvidence("bitcoin", "testnet");
       if (btc==null)
          throw new Exception("Missing bitcoin evidence");
       if (
          !btc.getVersion().equals("0.1.0") ||
          !btc.getBackend().equals( "bitcoin") ||
          !btc.getProvider().equals( "testnet") ||
           btc.getProof()[0] !=  42)
        {
          throw new Exception("Invalid bitcoin evidence:" + new Gson().toJson(btc));
        }
       
       
       Evidence eth = segment.getEvidence("ethereum", "mainnet");
       if (eth==null)
          throw new Exception("Missing ethereum evidence");
       if (
          !eth.getVersion().equals("1.0.3") ||
          !eth.getBackend().equals( "ethereum") ||
          !eth.getProvider().equals( "mainnet") ||
           eth.getProof()[0] != 24)
        {
          throw new Exception("Invalid ethereum evidence:" + new Gson().toJson(eth));
        }
   }  
}