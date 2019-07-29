package com.stratumn.chainscript;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.google.protobuf.ByteString;


public class Segment
{
   private stratumn.chainscript.Chainscript.Link pbLink;
   private stratumn.chainscript.Chainscript.Segment pbSegment;

   /**
    * @param pbSegment
    * @throws Exception 
    */
   public Segment(stratumn.chainscript.Chainscript.Segment pbSegment) throws Exception
   {
      if(pbSegment.getLink() == null)
      {
         throw new Exception("link is missing");
      }

      this.pbLink = pbSegment.getLink();
      this.pbSegment = pbSegment;
      Link link = new Link(this.pbLink);

      if(this.pbSegment.getMeta() == null)
      {
         stratumn.chainscript.Chainscript.SegmentMeta segmentMeta = stratumn.chainscript.Chainscript.SegmentMeta.newBuilder().setLinkHash(ByteString.copyFrom(link.hash())).build();
         this.pbSegment.toBuilder().setMeta(segmentMeta);
      }
   }

   /**
    * The segment can be enriched with evidence that the link was saved
    * immutably somewhere.
    * @param e evidence.
    * @throws Exception 
    */
   public void addEvidence(Evidence e) throws Exception
   {
      e.validate();

      if(this.getEvidence(e.getBackend(), e.getProvider()) != null)
      {
         throw new Exception("evidence already exists for the given backend and provider");
      }

      stratumn.chainscript.Chainscript.Evidence pbEvidence = stratumn.chainscript.Chainscript.Evidence.newBuilder().setVersion(e.getVersion()).setBackend(e.getBackend())
         .setProvider(e.getProvider()).setProof(ByteString.copyFrom(e.getProof())).build();
      this.pbSegment.getMeta().getEvidencesList().add(pbEvidence);
   }

   /**
    * Return all the evidences in this segment.
    * @throws Exception 
    * @returns evidences.
    */
   public Evidence[] evidences() throws Exception
   {
      List<stratumn.chainscript.Chainscript.Evidence> evidences = this.pbSegment.getMeta().getEvidencesList();

      List<Evidence> result = new ArrayList<Evidence>();
      for(stratumn.chainscript.Chainscript.Evidence evidence : evidences)
      {
         result.add(EvidenceFactory.fromProto(evidence));
      }
      return (Evidence[]) result.toArray();
   }

   /**
    * Return all the evidences of a specific backend.
    * @param backend of the expected evidences.
    * @throws Exception 
    * @returns evidences.
    */
   public Evidence[] findEvidences(String backend) throws Exception
   {
      List<stratumn.chainscript.Chainscript.Evidence> evidences = this.pbSegment.getMeta().getEvidencesList();

      List<Evidence> result = new ArrayList<Evidence>();
      for(stratumn.chainscript.Chainscript.Evidence evidence : evidences)
      {
         if(evidence.getBackend().equals(backend))
         {
            result.add(EvidenceFactory.fromProto(evidence));
         }
      }
      return (Evidence[]) result.toArray();
   }

   /**
    * Retrieve the evidence for the given backend and provider (if one exists).
    * @param backend evidence backend.
    * @param provider evidence backend instance.
    * @throws Exception 
    * @returns the evidence or null.
    */
   public Evidence getEvidence(String backend, String provider) throws Exception
   {
      List<stratumn.chainscript.Chainscript.Evidence> evidences = this.pbSegment.getMeta().getEvidencesList();

      for(stratumn.chainscript.Chainscript.Evidence evidence : evidences)
      {
         if(evidence.getBackend().equals(backend) && evidence.getProvider().equals(provider))
         {
            return EvidenceFactory.fromProto(evidence);
         }
      }
      return null;
   }

   /**
    * The segment's link is its immutable part.
    * @returns the segment's link.
    */
   public Link link()
   {
      return new Link(this.pbLink);
   }

   /**
    * Get the hash of the segment's link.
    * @returns the link's hash.
    */
   public ByteString linkHash()
   {
      return (this.pbSegment.getMeta().getLinkHash());
   }

  

   /**
    * Validate checks for errors in a segment.
    * @throws Exception 
    */
   public void validate() throws Exception
   {
      if(this.pbSegment.getMeta() == null)
      {
         throw new Exception("segment meta is missing");
      }

      if(this.linkHash() == null || this.linkHash().isEmpty())
      {
         throw new Exception("link hash is missing");
      }

      if(!Base64.getEncoder().encodeToString(this.linkHash().toByteArray()).equals(Base64.getEncoder().encodeToString(this.link().hash())))
      {
         throw new Exception("link hash mismatch");
      }

      this.link().validate();
   }
}
