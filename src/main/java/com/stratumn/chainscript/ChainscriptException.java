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
/***
 *  Custom exception to hold the internal error messages 
 **/
public class ChainscriptException extends Exception
{

   /**
    * 
    */
   private static final long serialVersionUID = 1L;

   private Error error = Error.InternalError;
   public ChainscriptException()
   {
      super(); 
   }

  
   public ChainscriptException(Error error)
   {
      
      super(error.toString()); 
      this.error = error;
   }


   public ChainscriptException(String message)
   {
      super(message); 
   }


   public ChainscriptException(Throwable cause)
   {
      super(cause); 
   }


   public ChainscriptException(String message, Throwable cause)
   {
      super(message, cause); 
   }


   public Error getError()
   {
      return error;
   }


   public void setError(Error error)
   {
      this.error = error;
   }

    
   
}
