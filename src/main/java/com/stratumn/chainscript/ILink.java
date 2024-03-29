package com.stratumn.chainscript;

public interface ILink {

   /**
    * A link is usually created as a result of an action.
    * 
    * @throws ChainscriptException
    * @return the link's action.
    */
   String action() throws ChainscriptException;

   /**
    * Add a signature to the link. This will validate the signature before adding
    * it.
    * 
    * @param signature link signature.
    * @throws ChainscriptException
    */
   void addSignature(Signature signature) throws ChainscriptException;

   /**
    * The client id allows segment receivers to figure out how the segment was
    * encoded and can be decoded.
    * 
    * @throws ChainscriptException
    * @return the link's client id.
    */
   String clientId() throws ChainscriptException;

   /***
    * Returns custom class
    * 
    * @param clazzOfT
    * @return
    * @throws ChainscriptException
    */
   <DataType> DataType data(Class<DataType> clazzOfT) throws ChainscriptException;

   /**
    * Serialize the link and compute a hash of the resulting bytes. The
    * serialization and hashing algorithm used depend on the link version.
    * 
    * @throws ChainscriptException
    * @return the hash bytes.
    */
   byte[] hash() throws ChainscriptException;

   /**
    * A link always belongs to a specific process map.
    * 
    * @throws ChainscriptException
    * @return the link's map id.
    */
   String mapId() throws ChainscriptException;

   /***
    * Returns an instance of the custom object of data type clazz
    * 
    * @return
    * @throws ChainscriptException
    */

   <MetaDataType> MetaDataType metadata(Class<MetaDataType> clazzOfT) throws ChainscriptException;

   /**
    * Maximum number of children a link is allowed to have. This is set to -1 if
    * the link is allowed to have as many children as it wants.
    * 
    * @throws ChainscriptException
    * @return the maximum number of children allowed.
    */
   int outDegree() throws ChainscriptException;

   /**
    * A link can have a parent, referenced by its link hash.
    * 
    * @throws ChainscriptException
    * @return the parent link hash.
    */
   byte[] prevLinkHash() throws ChainscriptException;

   /**
    * The priority can be used to order links.
    * 
    * @throws ChainscriptException
    * @return the link's priority.
    */
   double priority() throws ChainscriptException;

   /**
    * A link always belong to an instance of a process.
    * 
    * @throws ChainscriptException
    * @return the link's process name.
    */
   Process process() throws ChainscriptException;

   /**
    * A link can contain references to other links.
    * 
    * @throws ChainscriptException
    * @return referenced links.
    */
   LinkReference[] refs() throws ChainscriptException;

   /**
    * Create a segment from the link.
    * 
    * @throws ChainscriptException
    * @return the segment wrapping the link.
    */
   Segment segmentify() throws ChainscriptException;

   /**
    * Serialize the link.
    * 
    * @return link bytes.
    */
   byte[] serialize();

   /**
    * Set the given object as the link's data.
    * 
    * @param data custom data to save with the link.
    * @throws ChainscriptException
    * @throws ChainscriptException
    */
   void setData(Object data) throws ChainscriptException;

   /**
    * Set the given object as the link's metadata.
    * 
    * @param data custom data to save with the link metadata.
    * @throws ChainscriptException
    * @throws ChainscriptException
    */
   void setMetadata(Object data) throws ChainscriptException;

   /**
    * Sign configurable parts of the link with the current signature version. The
    * payloadPath is used to select what parts of the link need to be signed with
    * the given private key. If no payloadPath is provided, the whole link is
    * signed. The signature is added to the link's signature list.
    * 
    * @param key         private key in PEM format (generated
    *                    by @stratumn/js-crypto).
    * @param payloadPath link parts that should be signed.
    * @throws ChainscriptException
    */
   void sign(byte[] key, String payloadPath) throws ChainscriptException;

   /**
    * @return the link's signatures (if any).
    */
   Signature[] signatures();

   /**
    * Compute the bytes that should be signed.
    * 
    * @throws ChainscriptException
    * @param version     impacts how those bytes are computed.
    * @param payloadPath parts of the link that should be signed.
    * @return bytes to be signed.
    */
   byte[] signedBytes(String version, String payloadPath) throws ChainscriptException;

   /**
    * (Optional) A link can be interpreted as a step in a process.
    * 
    * @throws ChainscriptException
    * @return the corresponding process step.
    */
   String step() throws ChainscriptException;

   /**
    * (Optional) A link can be tagged. Tags are useful to filter link search
    * results.
    * 
    * @throws ChainscriptException
    * @return link tags.
    */
   String[] tags() throws ChainscriptException;

   /**
    * Validate checks for errors in a link.
    * 
    * @throws ChainscriptException
    */
   void validate() throws ChainscriptException;

   /**
    * The link version is used to properly serialize and deserialize it.
    * 
    * @return the link version.
    */
   String version();

   /**
    * @return the link
    */
   stratumn.chainscript.Chainscript.Link getLink();

   /***
    * Convert to a json object.
    * 
    * @return
    */
   String toObject() throws ChainscriptException;

}
