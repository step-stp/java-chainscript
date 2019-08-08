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

/**
 * A reference to a link that can be in another process.
 */
public class LinkReference
{
   private byte[] linkHash;
   private String process;

   /**
    * @param linkHash
    * @param process
    * @throws ChainscriptException 
    * @throws Exception 
    */
   public LinkReference(byte[] linkHash, String process) throws ChainscriptException 
   {
      if(StringUtils.isEmpty(process))
      {
         throw new ChainscriptException(Error.LinkProcessMissing);
      }
      if(linkHash == null || linkHash.length == 0)
      {
         throw new ChainscriptException(Error.LinkHashMissing);
      }

      this.linkHash = linkHash;
      this.process = process;
   }

   /**
    * @return the linkHash
    */
   public byte[] getLinkHash()
   {
      return linkHash;
   }

   /**
    * @param linkHash the linkHash to set
    */
   public void setLinkHash(byte[] linkHash)
   {
      this.linkHash = linkHash;
   }

   /**
    * @return the process
    */
   public String getProcess()
   {
      return process;
   }

   /**
    * @param process the process to set
    */
   public void setProcess(String process)
   {
      this.process = process;
   }
}
