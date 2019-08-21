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
*/ package com.stratumn.chainscript;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class TestUtil
{
   /***
    * Converts and int Array to a Byte Array
    * @param intArray
    * @return
    */
   public static byte[] convertIntArrToByteArr(int[] intArray)
   {
      if (intArray==null) return null; 
      ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length * 4);        
      IntBuffer intBuffer = byteBuffer.asIntBuffer();
      intBuffer.put(intArray);

      byte[] array = byteBuffer.array();
      return array;
   }
   
   /***
    * Converts and Byte Array to a int Array
    * @param intArray
    * @return
    */
   public static int[] convertByteArrToIntArr(byte[] byteArray)
   {
      if (byteArray==null) return null; 
      int[] intArray ;
      
      ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
         
      intArray = new int[byteArray.length / 4];        
      IntBuffer intBuffer = byteBuffer.asIntBuffer();
       intBuffer.get( intArray);
 
      return intArray;
   }
   
   
   
   /***
    * Compares to maps
    * @param first
    * @param second
    * @return
    */
   public static boolean compareMaps(Map<String, ?> first, Map<String, ?> second) {
      if (first.size() != second.size()) {
         return false;
     }  
      Map<String, Boolean> com = first.entrySet().stream()
      .collect(Collectors.toMap(e -> e.getKey(), 
        e -> e.getValue().getClass().isArray()? 
           Arrays.equals((byte[]) e.getValue(), (byte[]) second.get(e.getKey())) 
           : e.getValue().equals(second.get(e.getKey())))); 
      return first.entrySet().stream()
         .allMatch(e ->  e.getValue().getClass().isArray()? 
            Arrays.equals((byte[]) e.getValue(), (byte[]) second.get(e.getKey())) :   
               e.getValue().equals(second.get(e.getKey()))); 
      
  }
}
