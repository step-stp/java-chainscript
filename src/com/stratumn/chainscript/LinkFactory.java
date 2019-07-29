package com.stratumn.chainscript;

import com.google.protobuf.InvalidProtocolBufferException;


public class LinkFactory
{
   /**
    * Deserialize a link.
    * @param linkBytes encoded bytes.
    * @returns the deserialized link.
    */
   public static Link deserialize(byte[] linkBytes)
   {
      stratumn.chainscript.Chainscript.Link pbLink = null;
      try
      {
         pbLink = stratumn.chainscript.Chainscript.Link.parseFrom(linkBytes);
        
      }
      catch(InvalidProtocolBufferException e)
      {
        
         e.printStackTrace();
      }
      return new Link(pbLink);
   }

   /**
    * Convert a plain object to a link.
    * @param link plain object.
    */
   public static Link fromObject(stratumn.chainscript.Chainscript.Link any)
   {
      return new Link(stratumn.chainscript.Chainscript.Link.newBuilder(any).build());
   }
}
