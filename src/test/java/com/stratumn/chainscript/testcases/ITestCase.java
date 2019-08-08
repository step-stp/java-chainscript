package com.stratumn.chainscript.testcases;

public interface ITestCase
{ 
   /***
    * Test case id
    * @return
    */
   public String getId();
   
   /***
    * Generate encoded segment bytes.
    * @return
    * @throws Exception 
    */
   public   String generate() throws Exception;
   /***
    *   Validate encoded segment bytes. 
    * @param encodedSegment
    */
   public   void validate(String encodedSegment) throws Exception; 
   
   
    
}