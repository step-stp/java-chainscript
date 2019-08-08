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
/***
 * Error codes thrown by the library
 */
public enum Error {

   /***** Evidence errors *****/
   EvidenceVersionMissing("evidence version is missing"),
   EvidenceBackendMissing("evidence backend is missing"),
    EvidenceProviderMissing("evidence provider is missing"),
   EvidenceProofMissing("evidence proof is missing"),
   DuplicateEvidence("evidence already exists for the given backend and provider"),

   /***** Link errors *****/

   LinkClientIdUnkown("link was created with an unknown client: can't deserialize it"),
   LinkHashMissing("link hash is missing"),
   LinkMapIdMissing("link map id is missing"),
   LinkMetaMissing("link meta is missing"),
   LinkProcessMissing("link process is missing"),
   LinkVersionMissing("link vern is missing"),
   LinkVersionUnknown("unknown link version"),
   LinkPriorityNotPositive("priority needs to be positive"),
   /***** Segment errors *****/

   LinkHashMismatch("link hash mismatch"),
   LinkMissing("link is missing"),
   SegmentMetaMissing("segment meta is missing"),

   /***** Signature errors *****/

   SignatureInvalid("signature is invalid"),
   SignatureMissing("signature bytes are missing"),
   SignaturePublicKeyMissing("signature public key is missing"),
   SignatureVersionUnknown("unknown signature version"),
   
   InternalError("Internal Chainscript Error");

   private String description;

   Error(String description)
   {
      this.description = description;
   }

   @Override
   public String toString()
   {
      return this.description;
   }

}
