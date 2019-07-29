package com.stratumn.chainscript;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.protobuf.ByteString;
import com.stratumn.canonicaljson.CanonicalJson;
import com.stratumn.chainscript.utils.SignatureUtil;

import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.gson.GsonRuntime;
import stratumn.chainscript.Chainscript.LinkMeta;

public class Link
{
   private stratumn.chainscript.Chainscript.Link link;

   /**
    * @param link the link to set
    */
   public void setLink(stratumn.chainscript.Chainscript.Link link)
   {
      this.link = link;
   }

   /**
    * A link is usually created as a result of an action.
    * @throws Exception 
    * @returns the link's action.
    */
   public String action() throws Exception
   {
      if(this.link.getMeta() != null)
      {
         throw new Exception("link meta is missing");
      }
      return StringUtils.isEmpty(this.link.getMeta().getAction()) ? "" : this.link.getMeta().getAction();
   }

   /**
    * Add a signature to the link.
    * This will validate the signature before adding it.
    * @param signature link signature.
    * @throws Exception 
    */
   public void addSignature(Signature signature) throws Exception
   {
      signature.validate(this);

      stratumn.chainscript.Chainscript.Signature sig = stratumn.chainscript.Chainscript.Signature.newBuilder().setVersion(signature.version())
         .setPayloadPath(signature.payloadPath()).setPublicKey(signature.publicKey()).setSignature(signature.signature()).build();

      this.link.getSignaturesList().add(sig);
   }

   /**
    * The client id allows segment receivers to figure out how the segment was
    * encoded and can be decoded.
    * @throws Exception 
    * @returns the link's client id.
    */
   public String clientId() throws Exception
   {
      if(this.link.getMeta() == null)
      {
         throw new Exception("link meta is missing");
      }
      return StringUtils.isEmpty(this.link.getMeta().getClientId()) ? "" : this.link.getMeta().getClientId();
   }

   /**
    * The link data (business logic details about the execution of a process step).
    * @throws Exception 
    * @returns the object containing the link details.
    */
   public Object data() throws Exception
   {
      this.verifyCompatibility();

      if(this.link.getData() == null || this.link.getData().isEmpty())
      {
         return null;
      }
      switch(this.version())
      {
         case Constants.LINK_VERSION_1_0_0:
            byte[] decodedData = Base64.getDecoder().decode(this.link.getData().toByteArray());
            return CanonicalJson.parse(new String(decodedData));
         default:
            throw new Exception("unknown link version");
      }
   }

   /**
    * Serialize the link and compute a hash of the resulting bytes.
    * The serialization and hashing algorithm used depend on the link version.
    * @throws Exception 
    * @returns the hash bytes.
    */
   public byte[] hash() throws Exception
   {
      switch(this.version())
      {
         case Constants.LINK_VERSION_1_0_0:
            byte[] linkBytes = Base64.getEncoder().encode(this.link.toByteArray());
            MessageDigest digest;
            try
            {
               digest = MessageDigest.getInstance("SHA-256");
            }
            catch(NoSuchAlgorithmException e)
            {
               throw new Exception("Internal error occurred");
            }
            byte[] hash = digest.digest(linkBytes);

            return hash;
         default:
            throw new Exception("unknown link version");
      }
   }

   /**
    * A link always belongs to a specific process map.
    * @throws Exception 
    * @returns the link's map id.
    */
   public String mapId() throws Exception
   {
      LinkMeta meta = this.link.getMeta();
      if(meta == null)
      {
         throw new Exception("link meta is missing");
      }

      return StringUtils.isEmpty(meta.getMapId()) ? "" : meta.getMapId();
   }

   /**
    * The link metadata can contain a custom object.
    * @throws Exception 
    * @returns the object containing the link metadata details.
    */
   public Object metadata() throws Exception
   {
      this.verifyCompatibility();
      Object result = null;
      ByteString linkMetadata = this.link.getMeta().getData();
      if(linkMetadata == null || linkMetadata.isEmpty())
      {
         return result;
      }

      switch(this.version())
      {
         case Constants.LINK_VERSION_1_0_0:
            byte[] decodedData = Base64.getDecoder().decode(linkMetadata.toByteArray());
            result = CanonicalJson.parse(new String(decodedData));
            return result;
         default:
            throw new Exception("unknown link version");
      }
   }

   /**
    * Maximum number of children a link is allowed to have.
    * This is set to -1 if the link is allowed to have as many children as it
    * wants.
    * @throws Exception 
    * @returns the maximum number of children allowed.
    */
   public int outDegree() throws Exception
   {
      if(this.link.getMeta() == null)
      {
         throw new Exception("link meta is missing");
      }

      return this.link.getMeta().getOutDegree();
   }

   /**
    * A link can have a parent, referenced by its link hash.
    * @throws Exception 
    * @returns the parent link hash.
    */
   public byte[] prevLinkHash() throws Exception
   {
      if(this.link.getMeta() == null)
      {
         throw new Exception("link meta is missing");
      }

      if(this.link.getMeta().getPrevLinkHash() == null)
      {
         return new byte[0];
      }

      return this.link.getMeta().getPrevLinkHash().toByteArray();
   }

   /**
    * The priority can be used to order links.
    * @throws Exception 
    * @returns the link's priority.
    */
   public double priority() throws Exception
   {
      if(this.link.getMeta() == null)
      {
         throw new Exception("link meta is missing");
      }

      return this.link.getMeta().getPriority();
   }

   /**
    * A link always belong to an instance of a process.
    * @throws Exception 
    * @returns the link's process name.
    */
   public Process process() throws Exception
   {
      if(this.link.getMeta() == null)
      {
         throw new Exception("link meta is missing");
      }

      stratumn.chainscript.Chainscript.Process process = this.link.getMeta().getProcess();
      if(process == null)
      {
         throw new Exception("link process is missing");
      }

      return new Process(StringUtils.isEmpty(process.getName()) ? "" : process.getName(), StringUtils.isEmpty(process.getState()) ? "" : process.getState());
   }

   /**
    * A link can contain references to other links.
    * @throws Exception 
    * @returns referenced links.
    */
   public LinkReference[] refs() throws Exception
   {
      if(this.link.getMeta() == null)
      {
         throw new Exception("link meta is missing");
      }

      List<stratumn.chainscript.Chainscript.LinkReference> refList = this.link.getMeta().getRefsList();

      if(refList == null)
      {
         return new LinkReference[0];
      }
      LinkReference[] linkReferenceArray = new LinkReference[refList.size()];
      for(int i = 0; i < refList.size(); i++)
      {
         LinkReference linkReference;
         try
         {
            linkReference = new LinkReference(refList.get(i).getLinkHash().toByteArray(), refList.get(i).getProcess());
            linkReferenceArray[i] = linkReference;
         }
         catch(Exception e)
         {
            // Couldn't create LinkReference object
            e.printStackTrace();
         }
      }
      return linkReferenceArray;
   }

   /**
    * Create a segment from the link.
    * @throws Exception 
    * @returns the segment wrapping the link.
    */
   public Segment segmentify() throws Exception
   {
      stratumn.chainscript.Chainscript.Segment segment = stratumn.chainscript.Chainscript.Segment.newBuilder().setLink(this.link).build();
      return new Segment(segment);
   }

   /**
    * Serialize the link.
    * @returns link bytes.
    */
   public byte[] serialize()
   {
      return null;     
   }

   /**
    * Set the given object as the link's data.
    * @param data custom data to save with the link.
    * @throws Exception 
    */
   public void setData(Object data) throws Exception
   {
      this.verifyCompatibility();

      switch(this.version())
      {
         case Constants.LINK_VERSION_1_0_0:
            String canonicalData = CanonicalJson.stringify(data);
            byte[] encodedData = Base64.getEncoder().encode(canonicalData.getBytes());
            this.link = this.link.toBuilder().setData(ByteString.copyFrom(encodedData)).build();
            return;
         default:
            throw new Exception("unknown link version");
      }
   }

   /**
    * Set the given object as the link's metadata.
    * @param data custom data to save with the link metadata.
    * @throws Exception 
    */
   public void setMetadata(Object data) throws Exception
   {
      this.verifyCompatibility();

      if(this.link.getMeta() == null)
      {
         throw new Exception("link meta is missing");
      }

      switch(this.version())
      {
         case Constants.LINK_VERSION_1_0_0:
            String canonicalData = CanonicalJson.stringify(data);
            byte[] encodedData = Base64.getEncoder().encode(canonicalData.getBytes());
            stratumn.chainscript.Chainscript.LinkMeta meta = this.link.getMeta().toBuilder().setData(ByteString.copyFrom(encodedData)).build();
            this.link = this.link.toBuilder().setMeta(meta).build();
            return;
         default:
            throw new Exception("unknown link version");
      }
   }

   /**
    * Sign configurable parts of the link with the current signature version.
    * The payloadPath is used to select what parts of the link need to be signed
    * with the given private key. If no payloadPath is provided, the whole link
    * is signed.
    * The signature is added to the link's signature list.
    * @param key private key in PEM format (generated by @stratumn/js-crypto).
    * @param payloadPath link parts that should be signed.
    */
   public void sign(byte[] key, String payloadPath)
   {
      Signature signature = SignatureUtil.signLink(key, this, payloadPath);

      stratumn.chainscript.Chainscript.Signature sig = stratumn.chainscript.Chainscript.Signature.newBuilder().setVersion(signature.version())
         .setPayloadPath(signature.payloadPath()).setPublicKey(signature.publicKey()).setSignature(signature.signature()).build();

      this.link.getSignaturesList().add(sig);
   }

   /**
    * @returns the link's signatures (if any).
    */
   public Signature[] signatures()
   {
      Signature[] signatures = new Signature[this.link.getSignaturesList().size()];
      for(int i = 0; i < this.link.getSignaturesList().size(); i++)
      {
         Signature signature = new Signature(this.link.getSignatures(i));
         signatures[i] = signature;
      }
      return signatures;
   }

   /**
    * Compute the bytes that should be signed.
    * @throws Exception 
    * @argument version impacts how those bytes are computed.
    * @argument payloadPath parts of the link that should be signed.
    * @returns bytes to be signed.
    */
   public byte[] signedBytes(String version, String payloadPath) throws Exception
   {
      byte[] hashedResultBytes = null;
      switch(version)
      {
         case Constants.SIGNATURE_VERSION_1_0_0:
            if(StringUtils.isEmpty(payloadPath))
            {
               payloadPath = "[version,data,meta]";
            }

            Gson gson = new Gson();
            String linkJson = gson.toJson(this.link);

            JmesPath<JsonElement> jmespath = new GsonRuntime();
            Expression<JsonElement> expression = jmespath.compile(linkJson);
            JsonElement payloadPathJson = new JsonParser().parse(payloadPath);
            JsonElement result = expression.search(payloadPathJson);

            String canonicalResult = CanonicalJson.stringify(result.getAsString());

            // TODO const jsonData = stringify(payloadData) as string;
            byte[] resultBytes = canonicalResult.getBytes(StandardCharsets.UTF_8);

            try
            {
               MessageDigest digest = MessageDigest.getInstance("SHA-256");
               hashedResultBytes = digest.digest(resultBytes);
            }
            catch(NoSuchAlgorithmException e)
            {
               e.printStackTrace();
            }
         break;
         default:
            throw new Exception("unknown signature version");
      }

      return hashedResultBytes;
   }

   /**
    * (Optional) A link can be interpreted as a step in a process.
    * @throws Exception 
    * @returns the corresponding process step.
    */
   public String step() throws Exception
   {
      if(this.link.getMeta() == null)
      {
         throw new Exception("link meta is missing");
      }
      return StringUtils.isEmpty(this.link.getMeta().getStep()) ? "" : this.link.getMeta().getStep();
   }

   /**
    * (Optional) A link can be tagged.
    * Tags are useful to filter link search results.
    * @throws Exception 
    * @returns link tags.
    */
   public String[] tags() throws Exception
   {
      if(this.link.getMeta() == null)
      {
         throw new Exception("link meta is missing");
      }

      String[] result = new String[this.link.getMeta().getTagsList().asByteStringList().size()];
      for(int i = 0; i < this.link.getMeta().getTagsList().asByteStringList().size(); i++)
      {
         result[i] = this.link.getMeta().getTagsList().get(i);
      }
      return result;
   }

   /**
    * Convert to a plain object.
    * @argument conversionOpts specify how to convert certain types.
    * @returns a plain object.
    */
   // TODO Not sure about it since IConversionOptions optional argument is unknown
   public Object toObject()
   {
      return stratumn.chainscript.Chainscript.Link.newBuilder(this.link).build();
   }

   /**
    * Validate checks for errors in a link.
    * @throws Exception 
    */
   public void validate() throws Exception
   {
      if(StringUtils.isEmpty(this.link.getVersion()))
      {
         throw new Exception("link version is missing");
      }

      LinkMeta meta = this.link.getMeta();
      if(meta == null)
      {
         throw new Exception("link meta is missing");
      }

      if(StringUtils.isEmpty(meta.getMapId()))
      {
         throw new Exception("link mapId is missing");
      }

      if(meta.getProcess() == null || StringUtils.isEmpty(meta.getProcess().getName()))
      {
         throw new Exception("link process is missing");
      }

      this.verifyCompatibility();

      LinkReference[] linkReferences = this.refs();
      for(int i = 0; i < linkReferences.length; i++)
      {
         if(StringUtils.isEmpty(linkReferences[i].getProcess()))
         {
            throw new Exception("link process is missing");

         }
         if(linkReferences[i].getLinkHash() == null || linkReferences[i].getLinkHash().length == 0)
         {
            throw new Exception("link hash is missing");
         }
      }

      Signature[] signatures = this.signatures();
      for(int i = 0; i < signatures.length; i++)
      {
         signatures[i].validate(this);
      }
   }

   /**
    * The link version is used to properly serialize and deserialize it.
    * @returns the link version.
    */
   public String version()
   {
      return this.link.getVersion();
   }

   /**
    * Check if the link is compatible with the current library.
    * If not compatible, will throw an exception.
    * @throws Exception 
    */
   private void verifyCompatibility() throws Exception
   {
      if(this.link.getMeta() == null)
      {
         throw new Exception("link meta is missing");
      }

      if(StringUtils.isEmpty(this.link.getMeta().getClientId()))
      {
         throw new Exception("link was created with an unknown client: can't deserialize it");
      }

      String[] compatibleClients = {Constants.ClientId, "github.com/stratumn/go-chainscript" };

      if(!Arrays.asList(compatibleClients).contains(this.link.getMeta().getClientId()))
      {
         throw new Exception("link was created with an unknown client: can't deserialize it");
      }
   }

   /**
    * @param link
    */
   public Link(stratumn.chainscript.Chainscript.Link link)
   {
      this.link = link;
   }

   /**
    * @return the link
    */
   public stratumn.chainscript.Chainscript.Link getLink()
   {
      return link;
   }
}
