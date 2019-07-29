package com.stratumn.chainscript;

/**
 * ILinkBuilder makes it easy to create links that adhere to the ChainScript spec.
 * It provides valid default values for required fields and allows the user to set fields to valid values.
 */
public interface ILinkBuilder
{
   /**
    * Set the link action.
    * The action is what caused the link to be created.
    * @param action friendly name of the action.
    */
   public ILinkBuilder withAction(String action);

   /**
    * Set the link data (custom object containing business logic details).
    * @param data link details.
    */
   public ILinkBuilder withData(Object data);

   /**
    * Set the maximum number of children a link is allowed to have.
    * By default this is set to -1 to allow any number of children.
    * @param d degree of the link.
    */
   public ILinkBuilder withDegree(int d);

   /**
    * Set the link metadata (custom object containing business logic details).
    * @param data link metadata.
    */
   public ILinkBuilder withMetadata(Object data);

   /**
    * Set the link's parent.
    * @param linkHash parent's link hash.
    * @throws Exception 
    */
   public ILinkBuilder withParent(byte[] linkHash) throws Exception;

   /**
    * Set the link's priority. The priority is used to order links.
    * @param priority a positive float.
    * @throws Exception 
    */
   public ILinkBuilder withPriority(double priority) throws Exception;

   /**
    * (Optional) Set the link process' state.
    * The process can be in a specific state depending on the actions taken.
    * @param state process state after the link action.
    */
   public ILinkBuilder withProcessState(String state);

   /**
    * (Optional) A link can reference other links, even if they are from other
    * processes.
    * @param refs references to relevant links.
    * @throws Exception 
    */
   public ILinkBuilder withRefs(LinkReference[] refs) throws Exception;

   /**
    * (Optional) Set the link's process step.
    * It can be used to help deserialize link data or filter link search results.
    * @param step link process step.
    */
   public ILinkBuilder withStep(String step);

   /**
    * (Optional) A link can be tagged.
    * Tags are useful to filter link search results.
    * @param tags link tags.
    */
   public ILinkBuilder withTags(String[] tags);

   /** build the link. */
   public Link build();
}
