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
/**
 * 
 * Common library constants
 */
public class Constants
{
   /**
    * ClientID allows segment receivers to figure out how the segment was
    * encoded and can be decoded.
    */
   public static final String ClientId = "github.com/stratumn/java-chainscript";

   public static final String[] COMPATIBLE_CLIENTS = {Constants.ClientId, 
      "github.com/stratumn/go-chainscript", "github.com/stratumn/js-chainscript" ,
      "github.com/stratumn/csharp-chainscript"};
   /**
    * LinkVersion_1_0_0 is the first version of the link encoding.
    * In that version we encode interfaces (link.data and link.meta.data) with
    * canonical JSON and hash the protobuf-encoded link bytes with SHA-256.
    */
   public static final String LINK_VERSION_1_0_0 = "1.0.0";
   /** The current link version. */
   public static final String LINK_VERSION = LINK_VERSION_1_0_0;

   /**
    * SignatureVersion_1_0_0 is the first version of the link signature.
    * In that version we use canonical JSON to encode the link parts.
    * We use JMESPATH to select what parts of the link need to be signed.
    * We use SHA-256 on the JSON-encoded bytes and sign the resulting hash.
    * We use github.com/stratumn/js-crypto's 1.0.0 release to produce the
    * signature (which uses PEM-encoded private keys).
    */
   public static final String SIGNATURE_VERSION_1_0_0 = "1.0.0";
   /** The current signature version. */
   public static final String SIGNATURE_VERSION = SIGNATURE_VERSION_1_0_0;
}
