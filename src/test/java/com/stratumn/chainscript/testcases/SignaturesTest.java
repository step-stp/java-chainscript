package com.stratumn.chainscript.testcases;

import java.security.PrivateKey;
import java.util.Base64;

import com.stratumn.chainscript.Link;
import com.stratumn.chainscript.LinkBuilder;
import com.stratumn.chainscript.Segment;
import com.stratumn.chainscript.Signature;
import com.stratumn.chainscript.utils.CryptoUtils;
 

/**
 * Test a segment with custom data and metadata but no references, evidences
 * or signatures.
 */
public class SignaturesTest implements ITestCase
{
   public static final String id = "segment-signatures";

   @Override
   public String getId()
   {
     return SignaturesTest.id;
   }
   
   @Override
   public String generate() throws Exception  
   {
      PrivateKey ed255Key =  CryptoUtils.generateKeyPair().getPrivate();
      
//      KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
//      kpg.initialize(1024 );  
//      KeyPair kp = kpg.generateKeyPair();
//      RSAPrivateKey rsaKey = (RSAPrivateKey) kp.getPrivate();
      
      Link link = new LinkBuilder("test_process", "test_map")
      .withAction("ʙᴀᴛᴍᴀɴ").build();
      link.sign(ed255Key.getEncoded(), "");
      link.sign(ed255Key.getEncoded(), "[version,meta.mapId]");
//      link.sign(rsaKey.getEncoded(), "[version,meta.mapId]");
      
      Segment segment = link.segmentify();
       
      return Base64.getEncoder().encodeToString(segment.serialize());
   }

   @Override
   public void validate(String encodedSegment) throws Exception
   {
       Segment segment = Segment.deserialize(Base64.getDecoder().decode(encodedSegment));
       segment.validate();
       Signature[] signatures  = segment.link().signatures();
       if (signatures.length != 2) {
          throw new Exception("Invalid number of signatures: " + signatures.length);
        }
       
       signatures[0].validate(segment.link());
       signatures[1].validate(segment.link());
        
       if (!signatures[0].payloadPath().equals("[version,data,meta]")) {
          throw new Exception ("Invalid first signature payload path: " + signatures[0].payloadPath() );
       }
       if (!signatures[1].payloadPath().equals("[version,meta.mapId]")) {
          throw new Exception ("Invalid second signature payload path: " + signatures[1].payloadPath() );
       }
       
   }  
}