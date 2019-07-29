package com.stratumn.chainscript;


public class SegmentFactory
{
   /**
    * Deserialize a segment.
    * @param segmentBytes encoded bytes.
    * @throws Exception 
    * @returns the deserialized segment.
    */
   public static Segment deserialize(byte[] segmentBytes) throws Exception
   {
      // TODO Original code, not sure if we need to decode the original bytes array
      //      const segment = stratumn.chainscript.Segment.decode(segmentBytes);
      stratumn.chainscript.Chainscript.Segment segment = stratumn.chainscript.Chainscript.Segment.parseFrom(segmentBytes);
      return new Segment(segment);
   }

   /**
    * Convert an plain object to a segment.
    * @param segment plain object.
    * @throws Exception 
    */
   public static Segment fromObject(stratumn.chainscript.Chainscript.Segment any) throws Exception
   {
      return new Segment(stratumn.chainscript.Chainscript.Segment.newBuilder(any).build());
   }

}
