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
import java.lang.reflect.Type;
import java.util.Map;

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
   private static GsonBuilder gsonBuilder ;
   
   private static GsonBuilder getGsonBuilder()
   {
      if(gsonBuilder == null)
      {
         gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(stratumn.chainscript.Chainscript.Link.class,
               new ProtoGsonAdapter<stratumn.chainscript.Chainscript.Link>(stratumn.chainscript.Chainscript.Link.class))
            .registerTypeAdapter(stratumn.chainscript.Chainscript.Segment.class,
               new ProtoGsonAdapter<stratumn.chainscript.Chainscript.Segment>(stratumn.chainscript.Chainscript.Segment.class))
            .registerTypeAdapter(stratumn.chainscript.Chainscript.Signature.class,
               new ProtoGsonAdapter<stratumn.chainscript.Chainscript.Signature>(stratumn.chainscript.Chainscript.Signature.class))
            .registerTypeAdapter(stratumn.chainscript.Chainscript.Evidence.class,
               new ProtoGsonAdapter<stratumn.chainscript.Chainscript.Evidence>(stratumn.chainscript.Chainscript.Evidence.class));
         ;
         gsonBuilder.serializeNulls().disableHtmlEscaping();
      }
      return gsonBuilder;
   }
   
   public static Gson getGson()
   { 
      
      return getGsonBuilder().create();
   }
   
   /***
    * Expose registeration of new type adapters
    * @param type
    * @param typeAdapter
    */
   public static void registerTypeAdapter (Type type, Object typeAdapter)
   {
       
      getGsonBuilder().registerTypeAdapter(type, typeAdapter);
      
   }
   
   /***
    * converts the json object to an object of the type specified.
    * @param json
    * @param classOfT
    * @return
    */
   public  static <T>  T fromJson(String json ,  Class<T> classOfT)
   {
      return getGson().fromJson(json, classOfT);
   }
   
   
   public  static <T>  T fromJson(JsonElement json , Class<T> typeOfT)
   {
      return  getGson().fromJson(json, typeOfT);
   }
   
   /***
    * produces a canonicalized json of the object
    * @param src
    * @return
    * @throws IOException
    */
   public static String toCanonicalJson( Object src) throws IOException
   {
      String json =  getGson().toJson(src);
      return CanonicalJson.canonizalize(json);
   }

   /***
    * Converts an object or JsonString to a map 
    * @param srcObject
    * @return tree map of all object properties
    */ 
   public static Map<String,Object> objectToMap(Object srcObject) 
   {   
       String json = null;
       if (!(srcObject instanceof String))
            json = getGson().toJson(srcObject);
       else
            json = (String) srcObject;
            
        @SuppressWarnings("unchecked")
        Map<String,Object> map =(Map<String,Object>) getGson().fromJson(json,  Map.class);
       return map;
   }
    
   /***
    * Converts a map to an object of type T 
    * @param srcMap
    * @param tClass 
    * @return an object deserialized from the properties on the map.
    */
   public static <T> T mapToObject(Map<String,Object> srcMap, Class<T> tClass) 
   {
       String json = getGson().toJson(srcMap);
       T tObject =  getGson().fromJson(json,  tClass);
       return tObject;
   }
   
   /***
    * Produces normal Json String
    * @param src
    * @return
    * @throws IOException
    */
   public static String toJson( Object src) throws IOException
   {
      String json =  getGson().toJson(src);
      return json;
   }
}
