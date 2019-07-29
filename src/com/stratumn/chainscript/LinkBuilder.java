package com.stratumn.chainscript;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;

public class LinkBuilder implements ILinkBuilder
{
   private stratumn.chainscript.Chainscript.Link link;
   private Object linkData;
   private Object linkMetadata;

   /**
    * @throws Exception 
    * 
    */
   public LinkBuilder(String process, String mapId) throws Exception
   {
      if(StringUtils.isEmpty(process))
      {
         throw new Exception("process is missing");
      }

      if(StringUtils.isEmpty(mapId))
      {
         throw new Exception("map id is missing");
      }

      stratumn.chainscript.Chainscript.Process processObj = stratumn.chainscript.Chainscript.Process.newBuilder().setName(process).build();

      stratumn.chainscript.Chainscript.LinkMeta linkMeta =
         stratumn.chainscript.Chainscript.LinkMeta.newBuilder().setClientId(Constants.ClientId).setMapId(mapId).setOutDegree(-1).setProcess(processObj).build();

      this.link = stratumn.chainscript.Chainscript.Link.newBuilder().setVersion(Constants.LINK_VERSION).setMeta(linkMeta).build();
   }

   @Override
   public ILinkBuilder withAction(String action)
   {
      this.link.getMeta().toBuilder().setAction(action).build();
      return this;
   }

   @Override
   public ILinkBuilder withData(Object data)
   {
      this.linkData = data;
      return this;
   }

   @Override
   public ILinkBuilder withDegree(int d)
   {
      this.link.getMeta().toBuilder().setOutDegree(d).build();
      return this;
   }

   @Override
   public ILinkBuilder withMetadata(Object data)
   {
      this.linkMetadata = data;
      return this;
   }

   @Override
   public ILinkBuilder withParent(byte[] linkHash) throws Exception
   {
      if(linkHash == null || linkHash.length == 0)
      {
         throw new Exception("link hash is missing");
      }
      this.link.getMeta().toBuilder().setPrevLinkHash(ByteString.copyFrom(linkHash)).build();
      return this;
   }

   @Override
   public ILinkBuilder withPriority(double priority) throws Exception
   {
      if(priority < 0)
      {
         throw new Exception("priority needs to be positive");
      }
      this.link.getMeta().toBuilder().setPriority(priority).build();
      return this;
   }

   @Override
   public ILinkBuilder withProcessState(String state)
   {
      this.link.getMeta().getProcess().toBuilder().setState(state).build();
      return this;
   }

   @Override
   public ILinkBuilder withRefs(LinkReference[] refs) throws Exception
   {
      if(refs != null)
      {
         for(int i = 0; i < refs.length; i++)
         {
            LinkReference ref = refs[i];

            if(StringUtils.isEmpty(ref.getProcess()))
            {
               throw new Exception("link process is missing");
            }

            if(ref.getLinkHash() == null || ref.getLinkHash().length == 0)
            {
               throw new Exception("link hash is missing");
            }

            stratumn.chainscript.Chainscript.LinkReference reference =
               stratumn.chainscript.Chainscript.LinkReference.newBuilder().setLinkHash(ByteString.copyFrom(ref.getLinkHash())).setProcess(ref.getProcess()).build();
            this.link.getMeta().getRefsList().add(reference);
         }
      }
      return this;
   }

   @Override
   public ILinkBuilder withStep(String step)
   {
      this.link.getMeta().toBuilder().setStep(step).build();
      return this;
   }

   @Override
   public ILinkBuilder withTags(String[] tags)
   {
      List<String> listOfTags = Arrays.asList(tags);
      this.link.getMeta().toBuilder().getTagsList().addAll(listOfTags);
      return this;
   }

   @Override
   public Link build()
   {
      Link link = new Link(this.link);
      try
      {
         if(this.linkData != null)
         {
            link.setData(this.linkData);
         }
         if(this.linkMetadata != null)
         {
            link.setMetadata(this.linkMetadata);
         }
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
      return link;
   }
}
