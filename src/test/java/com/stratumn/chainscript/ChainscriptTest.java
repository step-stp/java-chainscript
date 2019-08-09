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
*/package com.stratumn.chainscript;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stratumn.chainscript.testcases.EvidencesTest;
import com.stratumn.chainscript.testcases.ITestCase;
import com.stratumn.chainscript.testcases.ReferencesTest;
import com.stratumn.chainscript.testcases.SignaturesTest;
import com.stratumn.chainscript.testcases.SimpleSegmentTest;
import com.stratumn.chainscript.testcases.TestCaseResult; 

class ChainscriptTest
{

   static List<ITestCase> TestCases =  
      Arrays.asList(new ITestCase[] { 
         new  SimpleSegmentTest(),
         new ReferencesTest(),
         new EvidencesTest(),
         new SignaturesTest() 
         
      }) ; 
   
   @Test
   void runTests()
   {
      String path = "1.0.0_T.json";
      File file = new File(path);
     
      try
      {
         generate(file); 
         validate(file); 
      }
      catch(Exception e)
      { 
         e.printStackTrace();
      }
      
   }
  
   public static void main(String[] args) throws Exception
   {
      if (args.length != 2)
      { 
         System.out.println("Usage: ChainscriptTest action[generate|validate] <file Path> ");
         System.out.println("For generate, filePath is output file.");
         System.out.println("For validate, filePath is input file.");
 
         System.exit(-1);
      }
      String action = args[0];
      String path = args[1];
      
      File file = new File(path);
      
      if (action.equalsIgnoreCase("generate"))
         generate(file);
      else
         if (action.equalsIgnoreCase("validate"))
         { 
            validate(file);
         }
         else
            System.err.println("Unknown action " + action);
        
   }

   /**
    * Validate encoded test segments.
    * @param path to the file containing the test segments.
    * @throws Exception 
    */
   private static void validate(File inputFile) throws Exception
   { 
      if (!inputFile.exists())
      {
         throw new Exception("File not found " + inputFile.getAbsolutePath());
      }
      System.out.println("Loading encoded segments from " + inputFile.getAbsolutePath());
 
      String jsonData = new String(Files.readAllBytes(inputFile.toPath()));
      TestCaseResult[] resultArr = new Gson().fromJson(jsonData, TestCaseResult[].class);
      
      ITestCase testCase ;
      for ( TestCaseResult result: resultArr)
      { 
         if (result.getId().equalsIgnoreCase(SimpleSegmentTest.id))
            testCase = new SimpleSegmentTest();
         else if (result.getId().equalsIgnoreCase(SignaturesTest.id))
            testCase = new SignaturesTest();
         else if (result.getId().equalsIgnoreCase(ReferencesTest.id))
            testCase = new ReferencesTest();
         else if (result.getId().equalsIgnoreCase(EvidencesTest.id))
            testCase = new EvidencesTest();
         else
         {   System.err.println("Unknown test case : " + result.getId());
           
           continue;
         }
          
         try {
          testCase.validate( result.getData());
          System.out.println(result.getId() + " SUCCESS "  );
         }catch (Exception e) {
            System.err.println(result.getId() + " FAILED " + e.getMessage());
          
         } 
      }
       
   }
   
   /**
    * Generate encoded test segments.
    * @param path to the file where test segments will be written.
    * @throws  Exception 
    */
   private static void generate(File outputFile) throws Exception
   {
       List<TestCaseResult> results = new ArrayList<TestCaseResult>();
       for (ITestCase tcase: TestCases)
       {
          try
         {
            results.add(new TestCaseResult(tcase.getId(), tcase.generate()));
         }
         catch(Exception e)
         {  e.printStackTrace();
            results.add(new TestCaseResult(tcase.getId(),  e.getMessage()));
         }
       }
       
       System.out.println ("Saving encoded segments to " + outputFile.getAbsolutePath()  );
       String resultStr = (new GsonBuilder().setPrettyPrinting().create()).toJson(results);
       Files.write(outputFile.toPath(), resultStr.getBytes() );
   }
   
   
   

}
