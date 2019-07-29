package com.stratumn.chainscript;

import org.apache.commons.lang3.StringUtils;

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
    * @throws Exception 
    */
   public Evidence(String version, String backend, String provider, byte[] proof) throws Exception
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
    * @throws Exception 
    */
   public void validate() throws Exception
   {
      if(StringUtils.isEmpty(this.version))
      {
         throw new Exception("evidence version is missing");
      }

      if(StringUtils.isEmpty(this.backend))
      {
         throw new Exception("evidence backend is missing");

      }

      if(StringUtils.isEmpty(this.provider))
      {
         throw new Exception("evidence provider is missing");
      }

      if(this.proof == null || this.proof.length == 0)
      {
         throw new Exception("evidence proof is missing");
      }
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
}
