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

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.google.protobuf.ByteString;

import stratumn.chainscript.Chainscript.SegmentMeta;

/***
 *  A segment describes an atomic step in your process.  
 */
public class Segment
{
   private stratumn.chainscript.Chainscript.Link pbLink;
   private stratumn.chainscript.Chainscript.Segment pbSegment;

   /**
    * @param pbSegment
    * @throws Exception 
    */
   public Segment(stratumn.chainscript.Chainscript.Segment pbSegment) throws ChainscriptException
   {
      if(!pbSegment.hasLink())
      {
         throw new ChainscriptException(Error.LinkMissing);
      }

      this.pbLink = pbSegment.getLink();
      this.pbSegment = pbSegment;
      if (pbSegment.getMeta()==null)
         this.pbSegment = this.pbSegment.toBuilder().setMeta(SegmentMeta.getDefaultInstance()).build();
      
      Link link = new Link(this.pbLink);
      
      stratumn.chainscript.Chainscript.SegmentMeta segmentMeta = this.pbSegment.getMeta().toBuilder()
         .setLinkHash(ByteString.copyFrom(link.hash())).build();
      this.pbSegment = this.pbSegment.toBuilder().setMeta(segmentMeta).build();
   }

   /**
    * The segment can be enriched with evidence that the link was saved
    * immutably somewhere.
    * @param e evidence.
    * @throws ChainscriptException 
    */
   public void addEvidence(Evidence e) throws ChainscriptException
   {
      e.validate();

      if(this.getEvidence(e.getBackend(), e.getProvider()) != null)
      {
         throw new ChainscriptException(Error.DuplicateEvidence);
      }

      stratumn.chainscript.Chainscript.Evidence pbEvidence = stratumn.chainscript.Chainscript.Evidence.newBuilder().setVersion(e.getVersion()).setBackend(e.getBackend())
         .setProvider(e.getProvider()).setProof(ByteString.copyFrom(e.getProof())).build();
      this.pbSegment = this.pbSegment.toBuilder().setMeta(this.pbSegment.getMeta().toBuilder().addEvidences(pbEvidence).build()).build();
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
         result.add(Evidence.fromProto(evidence));
      }
      return result.toArray(new Evidence[result.size()]);
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
            result.add(Evidence.fromProto(evidence));
         }
      }
      return result.toArray(new Evidence[result.size()]);
   }

   /**
    * Retrieve the evidence for the given backend and provider (if one exists).
    * @param backend evidence backend.
    * @param provider evidence backend instance.
    * @throws ChainscriptException 
    * @returns the evidence or null.
    */
   public Evidence getEvidence(String backend, String provider) throws ChainscriptException
   {
      List<stratumn.chainscript.Chainscript.Evidence> evidences = this.pbSegment.getMeta().getEvidencesList();

      for(stratumn.chainscript.Chainscript.Evidence evidence : evidences)
      {
         if(evidence.getBackend().equals(backend) && evidence.getProvider().equals(provider))
         {
            return Evidence.fromProto(evidence);
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
   public byte[] linkHash()
   {
      return (this.pbSegment.getMeta().getLinkHash().toByteArray());
   }

   /**
    * Serialize the segment.
    * @returns segment bytes.
    */
   public byte[] serialize()
   {
      return stratumn.chainscript.Chainscript.Segment.newBuilder(pbSegment).build().toByteArray();
   }

   /**
    * Validate checks for errors in a segment.
    * @throws ChainscriptException 
    */
   public void validate() throws ChainscriptException
   {
      if(!this.pbSegment.hasMeta())
      {
         throw new ChainscriptException(Error.SegmentMetaMissing);
      }

      if(this.linkHash() == null || this.linkHash().length == 0)
      {
         throw new ChainscriptException(Error.LinkHashMissing);
      }

      if(!Base64.getEncoder().encodeToString(this.linkHash()).equals(Base64.getEncoder().encodeToString(this.link().hash())))
      {
         throw new ChainscriptException(Error.LinkHashMismatch);
      }

      this.link().validate();
   }

   
 

   /**
    * Deserialize a segment.
    * @param segmentBytes encoded bytes.
    * @throws Exception 
    * @returns the deserialized segment.
    */
   public static Segment deserialize(byte[] segmentBytes) throws Exception
   {
     
      stratumn.chainscript.Chainscript.Segment segment = stratumn.chainscript.Chainscript.Segment.parseFrom(segmentBytes);
      return new Segment(segment);
   }

}
