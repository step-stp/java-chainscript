package com.stratumn.chainscript;

import org.apache.commons.lang3.StringUtils;

public class LinkReference
{
   private byte[] linkHash;
   private String process;

   /**
    * @param linkHash
    * @param process
    * @throws Exception 
    */
   public LinkReference(byte[] linkHash, String process) throws Exception
   {
      if(StringUtils.isEmpty(process))
      {
         throw new Exception("unknown link version");
      }
      if(linkHash == null || linkHash.length == 0)
      {
         throw new Exception("unknown link version");
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
