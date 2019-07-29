package com.stratumn.chainscript;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;


public class EvidenceFactory
{
   /**
    * Create an evidence from a protobuf object.
    * @param e protobuf evidence.
    */
   public static Evidence fromProto(stratumn.chainscript.Chainscript.Evidence object) throws Exception
   {
      String version = StringUtils.isEmpty(object.getVersion()) ? "" : object.getVersion();
      String backend = StringUtils.isEmpty(object.getBackend()) ? "" : object.getBackend();
      String provider = StringUtils.isEmpty(object.getProvider()) ? "" : object.getProvider();
      byte[] proof = object.getProof() != null ? object.getProof().toByteArray() : new byte[0];

      return new Evidence(version, backend, provider, proof);
   }

   /**
    * Deserialize an evidence.
    * @param evidenceBytes encoded bytes.
    * @throws Exception 
    * @returns the deserialized evidence.
    */
   public static Evidence deserialize(byte[] evidenceBytes) throws Exception
   {
      stratumn.chainscript.Chainscript.Evidence pbEvidence = stratumn.chainscript.Chainscript.Evidence.parseFrom(evidenceBytes);
      return fromProto(pbEvidence);
   }

   /**
    * Convert a plain object to an evidence.
    * @param e plain object.
    * @throws Exception 
    */
   public static Evidence fromObject(stratumn.chainscript.Chainscript.Evidence e) throws Exception
   {
      if(e.getProof().isValidUtf8())
      {
         return new Evidence(e.getVersion(), e.getBackend(), e.getProvider(), Base64.getDecoder().decode(e.getProof().toByteArray()));
      }
      return new Evidence(e.getVersion(), e.getBackend(), e.getProvider(), e.getProof().toByteArray());
   }

}
