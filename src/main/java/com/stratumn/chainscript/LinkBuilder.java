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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;

import stratumn.chainscript.Chainscript.LinkMeta.Builder;

/**
 * LinkBuilder makes it easy to create links that adhere to the ChainScript
 * spec.
 * It provides valid default values for required fields and allows the user
 * to set fields to valid values.
 */
public class LinkBuilder implements ILinkBuilder<LinkBuilder>
{
   private stratumn.chainscript.Chainscript.Link link;
   private Object linkData;
   private Object linkMetadata;

   /**
    * @throws ChainscriptException 
    * 
    */
   public LinkBuilder(String process, String mapId) throws ChainscriptException
   {
      if(StringUtils.isEmpty(process))
      {
         throw new ChainscriptException(Error.LinkProcessMissing);
      }

      if(StringUtils.isEmpty(mapId))
      {
         throw new ChainscriptException(Error.LinkMapIdMissing);
      }

      stratumn.chainscript.Chainscript.Process processObj = stratumn.chainscript.Chainscript.Process.newBuilder()
         .setName(process).build();

      stratumn.chainscript.Chainscript.LinkMeta linkMeta = stratumn.chainscript.Chainscript.LinkMeta.newBuilder()
         .setClientId(Constants.ClientId)
         .setMapId(mapId).setOutDegree(-1)
         .setProcess(processObj).build();

      this.link = stratumn.chainscript.Chainscript.Link.newBuilder()
         .setVersion(Constants.LINK_VERSION)
         .setMeta(linkMeta).build();
   }

   @Override
   public  LinkBuilder withAction(String action)
   {
      this.link = this.link.toBuilder().setMeta(this.link.getMeta().toBuilder().setAction(action).build()).build();
      return this;
   }

   @Override
   public  LinkBuilder withData(Object data)
   {
      this.linkData = data;
      return this;
   }

   @Override
   public LinkBuilder withDegree(int d)
   {
      this.link = this.link.toBuilder().setMeta(this.link.getMeta().toBuilder().setOutDegree(d).build()).build();
      return this;
   }

   @Override
   public  LinkBuilder withMetadata(Object data)
   {
      this.linkMetadata = data;
      return this;
   }

   @Override
   public LinkBuilder withParent(byte[] linkHash) throws ChainscriptException
   {
      if(linkHash == null || linkHash.length == 0)
      {
         throw new ChainscriptException(Error.LinkHashMissing);
      }
      this.link = this.link.toBuilder().setMeta(this.link.getMeta().toBuilder().setPrevLinkHash(ByteString.copyFrom(linkHash)).build()).build();
      return this;
   }

   @Override
   public  LinkBuilder withPriority(double priority) throws Exception
   {
      if(priority < 0)
      {
         throw new ChainscriptException(Error.LinkPriorityNotPositive);
      }

      this.link = this.link.toBuilder().setMeta(this.link.getMeta().toBuilder().setPriority(priority).build()).build();
      return this;
   }

   @Override
   public LinkBuilder withProcessState(String state)
   {
      this.link = this.link.toBuilder()
         .setMeta(this.link.getMeta().toBuilder().setProcess(this.link.getMeta().getProcess().toBuilder().setState(state).build()).build()).build();
      return this;
   }

   @Override
   public LinkBuilder withRefs(LinkReference[] refs) throws Exception
   {
      if(refs != null)
      {
         Builder metaBuilder = this.link.getMeta().toBuilder();
         for(int i = 0; i < refs.length; i++)
         {
            LinkReference ref = refs[i]; 
            if(StringUtils.isEmpty(ref.getProcess()))
            {
               throw new  ChainscriptException(Error.LinkProcessMissing);
            }

            if(ref.getLinkHash() == null || ref.getLinkHash().length == 0)
            {
               throw new  ChainscriptException(Error.LinkHashMissing);
            }

            stratumn.chainscript.Chainscript.LinkReference reference = stratumn.chainscript.Chainscript.LinkReference.newBuilder()
               .setLinkHash(ByteString.copyFrom(ref.getLinkHash())).setProcess(ref.getProcess()).build();
            metaBuilder.addRefs(reference);
         }
         this.link = this.link.toBuilder().setMeta(metaBuilder.build()).build();
      }
      return this;
   }

   @Override
   public LinkBuilder withStep(String step)
   {
      this.link = this.link.toBuilder().setMeta(this.link.getMeta().toBuilder().setStep(step).build()).build();
      return this;
   }

   @Override
   public LinkBuilder withTags(String[] tags)
   {
      List<String> listOfTags = Arrays.asList(tags);
      List<String> filterTags = listOfTags.stream()        
         .filter(tag ->  StringUtils.isNotEmpty(tag))     
         .collect(Collectors.toList());    

      this.link = this.link.toBuilder().setMeta(this.link.getMeta().toBuilder().addAllTags(filterTags).build()).build();
      return this;
   }

   @Override
   public Link build() throws ChainscriptException
   {
      Link link = new Link(this.link);
     
         if(this.linkData != null)
         {
            link.setData(this.linkData);
         }
         if(this.linkMetadata != null)
         {
            link.setMetadata(this.linkMetadata);
         }
     
      return link;
   }
}
