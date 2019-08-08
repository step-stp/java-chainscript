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

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Evidences can be used to externally verify a link's existence at a given
 * moment in time.
 * An evidence can be a proof of inclusion in a public blockchain, a timestamp
 * signed by a trusted authority or anything that you trust to provide an
 * immutable ordering of your process' steps.
 */
public class Evidence
{
   /** Evidence version. Useful to correctly deserialize proof bytes. */
   private String version;
   /** Identifier of the evidence type. */
   private String backend;
   /** Instance of the backend used. */
   private String provider;
   /** Serialized proof. */
   private byte[] proof;

   /**
    * @param version
    * @param backend
    * @param provider
    * @param proof
    * @throws ChainscriptException 
    */
   public Evidence(String version, String backend, String provider, byte[] proof) throws ChainscriptException
   {
      this.version = version;
      this.backend = backend;
      this.provider = provider;
      this.proof = proof;
      this.validate();
   }

   /**
    * Validate that the evidence is well-formed.
    * The proof is opaque bytes so it isn't validated here.
    * @throws ChainscriptException 
    */
   public void validate() throws ChainscriptException
   {
      if(StringUtils.isEmpty(this.version))
      {
         throw new ChainscriptException(Error.EvidenceVersionMissing);
      }

      if(StringUtils.isEmpty(this.backend))
      {
         throw new ChainscriptException(Error.EvidenceBackendMissing);

      }

      if(StringUtils.isEmpty(this.provider))
      {
         throw new ChainscriptException(Error.EvidenceProviderMissing);
      }

      if(this.proof == null || this.proof.length == 0)
      {
         throw new ChainscriptException(Error.EvidenceProofMissing);
      }
   }

   /**
    * Serialize the evidence.
    * @returns evidence bytes.
    */
   public byte[] serialize()
   {
      return stratumn.chainscript.Chainscript.Evidence.newBuilder()
         .setVersion(this.version)
         .setBackend(this.backend)
         .setProvider(this.provider)
         .setProof(ByteString.copyFrom(this.proof)).build().toByteArray();

   }

   /**
    * @return the version
    */
   public String getVersion()
   {
      return version;
   }

   /**
    * @param version the version to set
    */
   public void setVersion(String version)
   {
      this.version = version;
   }

   /**
    * @return the backend
    */
   public String getBackend()
   {
      return backend;
   }

   /**
    * @param backend the backend to set
    */
   public void setBackend(String backend)
   {
      this.backend = backend;
   }

   /**
    * @return the provider
    */
   public String getProvider()
   {
      return provider;
   }

   /**
    * @param provider the provider to set
    */
   public void setProvider(String provider)
   {
      this.provider = provider;
   }

   /**
    * @return the proof
    */
   public byte[] getProof()
   {
      return proof;
   }

   /**
    * @param proof the proof to set
    */
   public void setProof(byte[] proof)
   {
      this.proof = proof;
   }
   
   
   /**
    * Create an evidence from a protobuf object.
    * @param e protobuf evidence.
    * @throws ChainscriptException 
    */
   public static Evidence fromProto(stratumn.chainscript.Chainscript.Evidence object) throws ChainscriptException  
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
    * @throws ChainscriptException  
    * @returns the deserialized evidence.
    */
   public static Evidence deserialize(byte[] evidenceBytes) throws ChainscriptException  
   {
      stratumn.chainscript.Chainscript.Evidence pbEvidence;
      try
      {
         pbEvidence = stratumn.chainscript.Chainscript.Evidence.parseFrom(evidenceBytes);
      }
      catch(InvalidProtocolBufferException e)
      {
         throw new ChainscriptException(e);
      }
      return fromProto(pbEvidence);
   }
}
