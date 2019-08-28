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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.stratumn.canonicaljson.CanonicalJson;
import com.stratumn.chainscript.utils.CryptoUtils;
import com.stratumn.chainscript.utils.JsonHelper;

import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.gson.GsonRuntime;
import stratumn.chainscript.Chainscript.LinkMeta;

/**
 * A link is the immutable part of a segment. A link contains all the data that
 * represents a process' step.
 */
public class Link {
   private stratumn.chainscript.Chainscript.Link link;

   /**
    * @param link
    */
   public Link(stratumn.chainscript.Chainscript.Link link) {
      this.link = link;
   }

   /**
    * A link is usually created as a result of an action.
    * 
    * @throws ChainscriptException
    * @return the link's action.
    */
   public String action() throws ChainscriptException {

      return getLinkMeta().getAction() == null ? "" : getLinkMeta().getAction();
   }

   /**
    * Add a signature to the link. This will validate the signature before adding
    * it.
    * 
    * @param signature link signature.
    * @throws ChainscriptException
    */
   public void addSignature(Signature signature) throws ChainscriptException {
      signature.validate(this);

      stratumn.chainscript.Chainscript.Signature sig = stratumn.chainscript.Chainscript.Signature.newBuilder()
            .setVersion(signature.version()).setPayloadPath(signature.payloadPath())
            .setPublicKey(ByteString.copyFrom(signature.publicKey()))
            .setSignature(ByteString.copyFrom(signature.signature())).build();

      this.link.getSignaturesList().add(sig);
   }

   /**
    * The client id allows segment receivers to figure out how the segment was
    * encoded and can be decoded.
    * 
    * @throws ChainscriptException
    * @return the link's client id.
    */
   public String clientId() throws ChainscriptException {
      return getLinkMeta().getClientId() == null ? "" : getLinkMeta().getClientId();
   }

   /**
    * The link data (business logic details about the execution of a process step).
    * 
    * @throws ChainscriptException
    * @return the object containing the link details.
    */
   public Object data() throws ChainscriptException {
      this.verifyCompatibility();

      if (this.link.getData() == null || this.link.getData().isEmpty()) {
         return null;
      }
      switch (this.version()) {
      case Constants.LINK_VERSION_1_0_0:
         try {
            return CanonicalJson.parse(this.link.getData().toStringUtf8());
         } catch (IOException e) {
            throw new ChainscriptException("Failed to parse link data");
         }

      default:
         throw new ChainscriptException(Error.LinkVersionUnknown);
      }
   }

   /***
    * Returns custom class
    * 
    * @param clazzOfT
    * @return
    * @throws ChainscriptException
    */
   public <T> T data(Class<T> clazzOfT) throws ChainscriptException {
      this.verifyCompatibility();

      if (this.link.getData() == null || this.link.getData().isEmpty()) {
         return null;
      }
      switch (this.version()) {
      case Constants.LINK_VERSION_1_0_0:
         return JsonHelper.fromJson(this.link.getData().toStringUtf8(), clazzOfT);
      default:
         throw new ChainscriptException(Error.LinkVersionUnknown);
      }
   }

   /**
    * Serialize the link and compute a hash of the resulting bytes. The
    * serialization and hashing algorithm used depend on the link version.
    * 
    * @throws ChainscriptException
    * @return the hash bytes.
    */
   public byte[] hash() throws ChainscriptException {
      switch (this.version()) {
      case Constants.LINK_VERSION_1_0_0:

         byte[] linkBytes = this.link.toByteArray();
         return CryptoUtils.sha256(linkBytes);
      default:
         throw new ChainscriptException(Error.LinkVersionUnknown);
      }
   }

   /**
    * A link always belongs to a specific process map.
    * 
    * @throws ChainscriptException
    * @return the link's map id.
    */
   public String mapId() throws ChainscriptException {
      LinkMeta meta = getLinkMeta();
      return StringUtils.isEmpty(meta.getMapId()) ? "" : meta.getMapId();
   }

   /**
    * The link metadata can contain a custom object.
    * 
    * @throws ChainscriptException
    * @return the object containing the link metadata details.
    */
   public Object metadata() throws ChainscriptException {
      this.verifyCompatibility();
      Object result = null;
      ByteString linkMetadata = getLinkMeta().getData();
      if (linkMetadata == null || linkMetadata.isEmpty()) {
         return result;
      }
      switch (this.version()) {
      case Constants.LINK_VERSION_1_0_0:
         try {
            return CanonicalJson.parse(linkMetadata.toStringUtf8());
         } catch (IOException e) {
            throw new ChainscriptException("Failed to parse link Metadata");
         }

      default:
         throw new ChainscriptException(Error.LinkVersionUnknown);
      }
   }

   /***
    * Returns an instance of the custom object of data type clazz
    * 
    * @return
    * @throws ChainscriptException
    */

   public <T> T metadata(Class<T> clazzOfT) throws ChainscriptException {
      this.verifyCompatibility();
      ByteString linkMetadata = getLinkMeta().getData();
      if (linkMetadata == null || linkMetadata.isEmpty()) {
         return null;
      }
      switch (this.version()) {
      case Constants.LINK_VERSION_1_0_0:
         return JsonHelper.fromJson(linkMetadata.toStringUtf8(), clazzOfT);

      default:
         throw new ChainscriptException(Error.LinkVersionUnknown);
      }
   }

   /**
    * Maximum number of children a link is allowed to have. This is set to -1 if
    * the link is allowed to have as many children as it wants.
    * 
    * @throws ChainscriptException
    * @return the maximum number of children allowed.
    */
   public int outDegree() throws ChainscriptException {
      return getLinkMeta().getOutDegree();
   }

   /**
    * A link can have a parent, referenced by its link hash.
    * 
    * @throws ChainscriptException
    * @return the parent link hash.
    */
   public byte[] prevLinkHash() throws ChainscriptException {
      if (getLinkMeta().getPrevLinkHash() == null) {
         return new byte[0];
      }
      return getLinkMeta().getPrevLinkHash().toByteArray();
   }

   /**
    * The priority can be used to order links.
    * 
    * @throws ChainscriptException
    * @return the link's priority.
    */
   public double priority() throws ChainscriptException {
      return getLinkMeta().getPriority();
   }

   /**
    * A link always belong to an instance of a process.
    * 
    * @throws ChainscriptException
    * @return the link's process name.
    */
   public Process process() throws ChainscriptException {
      stratumn.chainscript.Chainscript.Process process = getLinkMeta().getProcess();
      if (process == null) {
         throw new ChainscriptException(Error.LinkProcessMissing);
      }
      return new Process(StringUtils.isEmpty(process.getName()) ? "" : process.getName(),
            StringUtils.isEmpty(process.getState()) ? "" : process.getState());
   }

   /**
    * A link can contain references to other links.
    * 
    * @throws ChainscriptException
    * @return referenced links.
    */
   public LinkReference[] refs() throws ChainscriptException {
      List<stratumn.chainscript.Chainscript.LinkReference> refList = getLinkMeta().getRefsList();

      if (refList == null) {
         return new LinkReference[0];
      }

      List<LinkReference> refListOut = new ArrayList<LinkReference>();
      for (stratumn.chainscript.Chainscript.LinkReference ref : refList) {
         LinkReference linkReference;
         linkReference = new LinkReference(ref.getLinkHash() != null ? ref.getLinkHash().toByteArray() : new byte[0],
               ref.getProcess() != null ? ref.getProcess() : "");
         refListOut.add(linkReference);
      }
      return refListOut.toArray(new LinkReference[refListOut.size()]);
   }

   /**
    * Create a segment from the link.
    * 
    * @throws ChainscriptException
    * @return the segment wrapping the link.
    */
   public Segment segmentify() throws ChainscriptException {
      stratumn.chainscript.Chainscript.Segment segment = stratumn.chainscript.Chainscript.Segment.newBuilder()
            .setLink(this.link).build();
      return new Segment(segment);
   }

   /**
    * Serialize the link.
    * 
    * @return link bytes.
    */
   public byte[] serialize() {
      return this.link.toByteArray();
   }

   /**
    * Set the given object as the link's data.
    * 
    * @param data custom data to save with the link.
    * @throws ChainscriptException
    */
   public void setData(Object data) throws ChainscriptException {
      this.verifyCompatibility();

      switch (this.version()) {
      case Constants.LINK_VERSION_1_0_0:
         try {
            String canonicalData = CanonicalJson.stringify(data);
            this.link = this.link.toBuilder().setData(ByteString.copyFrom(canonicalData, Constants.UTF8)).build();
            return;
         } catch (Exception e) {
            throw new ChainscriptException(e);
         }
      default:
         throw new ChainscriptException(Error.LinkVersionUnknown);
      }
   }

   /**
    * Set the given object as the link's metadata.
    * 
    * @param data custom data to save with the link metadata.
    * @throws ChainscriptException
    */
   public void setMetadata(Object data) throws ChainscriptException {
      this.verifyCompatibility();

      switch (this.version()) {
      case Constants.LINK_VERSION_1_0_0:
         try {
            String canonicalData = CanonicalJson.stringify(data);
            stratumn.chainscript.Chainscript.LinkMeta meta = getLinkMeta().toBuilder()
                  .setData(ByteString.copyFromUtf8(canonicalData)).build();
            this.link = this.link.toBuilder().setMeta(meta).build();
            return;
         } catch (Exception e) {
            throw new ChainscriptException(e);
         }
      default:
         throw new ChainscriptException(Error.LinkVersionUnknown);
      }
   }

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
   public void sign(byte[] key, String payloadPath) throws ChainscriptException {
      Signature signature = Signature.signLink(key, this, payloadPath);
      stratumn.chainscript.Chainscript.Signature sig = stratumn.chainscript.Chainscript.Signature.newBuilder()
            .setVersion(signature.version()).setPayloadPath(signature.payloadPath())
            .setPublicKey(ByteString.copyFrom(signature.publicKey()))
            .setSignature(ByteString.copyFrom(signature.signature())).build();

      this.link = this.link.toBuilder().addSignatures(sig).build();
   }

   /**
    * @return the link's signatures (if any).
    */
   public Signature[] signatures() {
      Signature[] signatures = new Signature[this.link.getSignaturesList().size()];
      for (int i = 0; i < this.link.getSignaturesList().size(); i++) {
         Signature signature = new Signature(this.link.getSignatures(i));
         signatures[i] = signature;
      }
      return signatures;
   }

   /**
    * Compute the bytes that should be signed.
    * 
    * @throws ChainscriptException
    * @param version     impacts how those bytes are computed.
    * @param payloadPath parts of the link that should be signed.
    * @return bytes to be signed.
    */
   public byte[] signedBytes(String version, String payloadPath) throws ChainscriptException {
      byte[] hashedResultBytes = null;
      switch (version) {
      case Constants.SIGNATURE_VERSION_1_0_0:
         if (StringUtils.isEmpty(payloadPath)) {
            payloadPath = "[version,data,meta]";
         }

         String linkJson = null;
         try {
            JmesPath<JsonElement> jmespath = new GsonRuntime();
            Expression<JsonElement> expression = jmespath.compile(payloadPath);

            linkJson = JsonFormat.printer().print(this.link);
            JsonElement payloadPathJson = new JsonParser().parse(linkJson);
            JsonElement result = expression.search(payloadPathJson);

            String canonicalResult = CanonicalJson.canonizalize(result.toString());
            byte[] payloadBytes = canonicalResult.getBytes(StandardCharsets.UTF_8);
            hashedResultBytes = CryptoUtils.sha256(payloadBytes);
         } catch (IOException e1) {
            throw new ChainscriptException(e1);
         }
         break;
      default:
         throw new ChainscriptException(Error.SignatureVersionUnknown);
      }
      return hashedResultBytes;
   }

   /**
    * (Optional) A link can be interpreted as a step in a process.
    * 
    * @throws ChainscriptException
    * @return the corresponding process step.
    */
   public String step() throws ChainscriptException {
      return StringUtils.isEmpty(getLinkMeta().getStep()) ? "" : getLinkMeta().getStep();
   }

   /**
    * (Optional) A link can be tagged. Tags are useful to filter link search
    * results.
    * 
    * @throws ChainscriptException
    * @return link tags.
    */
   public String[] tags() throws ChainscriptException {

      String[] result = getLinkMeta().getTagsList() != null
            ? getLinkMeta().getTagsList().toArray(new String[getLinkMeta().getTagsList().size()])
            : new String[0];

      return result;
   }

   /**
    * Validate checks for errors in a link.
    * 
    * @throws ChainscriptException
    */
   public void validate() throws ChainscriptException {
      if (StringUtils.isEmpty(this.link.getVersion())) {
         throw new ChainscriptException(Error.LinkVersionMissing);
      }

      LinkMeta meta = getLinkMeta();

      if (StringUtils.isEmpty(meta.getMapId())) {
         throw new ChainscriptException(Error.LinkMapIdMissing);
      }

      if (meta.getProcess() == null || StringUtils.isEmpty(meta.getProcess().getName())) {
         throw new ChainscriptException(Error.LinkProcessMissing);
      }

      this.verifyCompatibility();

      for (LinkReference ref : this.refs()) {
         if (StringUtils.isEmpty(ref.getProcess())) {
            throw new ChainscriptException(Error.LinkProcessMissing);

         }
         if (ref.getLinkHash() == null || ref.getLinkHash().length == 0) {
            throw new ChainscriptException(Error.LinkHashMissing);
         }
      }

      for (Signature sig : this.signatures()) {
         sig.validate(this);
      }
   }

   /**
    * The link version is used to properly serialize and deserialize it.
    * 
    * @return the link version.
    */
   public String version() {
      return this.link.getVersion();
   }

   /**
    * Check if the link is compatible with the current library. If not compatible,
    * will throw an exception.
    * 
    * @throws ChainscriptException
    */
   private void verifyCompatibility() throws ChainscriptException {

      if (StringUtils.isEmpty(getLinkMeta().getClientId())) {
         throw new ChainscriptException(Error.LinkClientIdUnkown);
      }

      if (!Arrays.asList(Constants.COMPATIBLE_CLIENTS).contains(getLinkMeta().getClientId())) {
         throw new ChainscriptException(Error.LinkClientIdUnkown);
      }
   }

   /**
    * @return the link
    */
   public stratumn.chainscript.Chainscript.Link getLink() {
      return link;
   }

   /***
    * Validates Link MetaData before returning it.
    * 
    * @return
    * @throws ChainscriptException
    */
   private LinkMeta getLinkMeta() throws ChainscriptException {
      if (this.link.getMeta() == null) {
         throw new ChainscriptException(Error.LinkMetaMissing);
      }
      return this.link.getMeta();
   }

   /***
    * Convert to a json object.
    * 
    * @return
    */
   public String toObject() {

      return JsonHelper.toJson(this.link);

   }

   /***
    * Convert a json object to a link.
    * 
    * @param jsonObject
    * @return
    */
   public static Link fromObject(String jsonObject) {
      return new Link(JsonHelper.fromJson(jsonObject, stratumn.chainscript.Chainscript.Link.class));
   }

   /**
    * Deserialize a link.
    * 
    * @param linkBytes encoded bytes.
    * @throws ChainscriptException
    * @return the deserialized link.
    */
   public static Link deserialize(byte[] linkBytes) throws ChainscriptException {
      try {
         return new Link(stratumn.chainscript.Chainscript.Link.parseFrom(linkBytes));
      } catch (InvalidProtocolBufferException e) {
         throw new ChainscriptException(e);
      }
   }
}
