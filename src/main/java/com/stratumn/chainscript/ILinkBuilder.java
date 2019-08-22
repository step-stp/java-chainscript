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
 * ILinkBuilder makes it easy to create links that adhere to the ChainScript spec.
 * It provides valid default values for required fields and allows the user to set fields to valid values.
 */
public interface ILinkBuilder<T extends ILinkBuilder<T > >
{
   /**
    * Set the link action.
    * The action is what caused the link to be created.
    * @param action friendly name of the action.
    */
   public T withAction(String action);

   /**
    * Set the link data (custom object containing business logic details).
    * @param data link details.
    */
   public T withData(Object data);

   /**
    * Set the maximum number of children a link is allowed to have.
    * By default this is set to -1 to allow any number of children.
    * @param d degree of the link.
    */
   public T withDegree(int d);

   /**
    * Set the link metadata (custom object containing business logic details).
    * @param data link metadata.
    */
   public T withMetadata(Object data);

   /**
    * Set the link's parent.
    * @param linkHash parent's link hash.
    * @throws ChainscriptException  
    */
   public T withParent(byte[] linkHash) throws ChainscriptException  ;

   /**
    * Set the link's priority. The priority is used to order links.
    * @param priority a positive float.
    * @throws ChainscriptException  
    */
   public T withPriority(double priority) throws ChainscriptException ;

   /**
    * (Optional) Set the link process' state.
    * The process can be in a specific state depending on the actions taken.
    * @param state process state after the link action.
    */
   public T withProcessState(String state);

   /**
    * (Optional) A link can reference other links, even if they are from other
    * processes.
    * @param refs references to relevant links.
    * @throws ChainscriptException  
    */
   public T withRefs(LinkReference[] refs) throws ChainscriptException  ;

   /**
    * (Optional) Set the link's process step.
    * It can be used to help deserialize link data or filter link search results.
    * @param step link process step.
    */
   public T withStep(String step);

   /**
    * (Optional) A link can be tagged.
    * Tags are useful to filter link search results.
    * @param tags link tags.
    */
   public T withTags(String[] tags);

   /** build the link. 
    * @throws ChainscriptException */
   public Link build() throws ChainscriptException;
}
