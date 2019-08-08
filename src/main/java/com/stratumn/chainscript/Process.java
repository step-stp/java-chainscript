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
 * A process is a collection of maps (process instances).
 * A map is a collection of links that track the process' progress.
 */
public class Process
{
   private String name;
   private String state;

   /**
    * @param name
    * @param state
    */
   public Process(String name, String state)
   {
      super();
      this.name = name; 
      this.state =state!=null? state :"";  
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }
 

   /**
    * @return the state
    */
   public String getState()
   {
      return state;
   }
 
}
