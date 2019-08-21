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
package com.stratumn.chainscript.utils;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.stratumn.canonicaljson.CanonicalJson; 
/***
 * 
 * Wrapper for Json Library 
 * Registers type adapter for chainscript types
 *
 */
public class JsonHelper
{
   private static Gson gson ;
   
   static { 
      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder = gsonBuilder
         .registerTypeAdapter(stratumn.chainscript.Chainscript.Link.class, new ProtoGsonAdapter<stratumn.chainscript.Chainscript.Link>(stratumn.chainscript.Chainscript.Link.newBuilder()))
         .registerTypeAdapter(stratumn.chainscript.Chainscript.Segment.class, new ProtoGsonAdapter<stratumn.chainscript.Chainscript.Link>(stratumn.chainscript.Chainscript.Segment.newBuilder()))
         .registerTypeAdapter(stratumn.chainscript.Chainscript.Signature.class, new ProtoGsonAdapter<stratumn.chainscript.Chainscript.Link>(stratumn.chainscript.Chainscript.Signature.newBuilder()))
         .registerTypeAdapter(stratumn.chainscript.Chainscript.Evidence.class, new ProtoGsonAdapter<stratumn.chainscript.Chainscript.Link>(stratumn.chainscript.Chainscript.Evidence.newBuilder()))
           ;
      gsonBuilder.serializeNulls();     
      gson =gsonBuilder.create();  
   }
   
   /***
    * converts the json object to an object of the type specified.
    * @param json
    * @param classOfT
    * @return
    */
   public  static <T>  T fromJson(String json ,  Class<T> classOfT)
   {
      return gson.fromJson(json, classOfT);
   }
   
   
   public  static <T>  T fromJson(JsonElement json , Class<T> typeOfT)
   {
      return gson.fromJson(json, typeOfT);
   }
   
   /***
    * produces a canonicalized json of the object
    * @param src
    * @return
    * @throws IOException
    */
   public static String toCanonicalJson( Object src) throws IOException
   {
      String json = gson.toJson(src);
      return CanonicalJson.canonizalize(json);
   }
   
   /***
    * Produces normal Json String
    * @param src
    * @return
    * @throws IOException
    */
   public static String toJson( Object src) throws IOException
   {
      String json = gson.toJson(src);
      return json;
   }
}
