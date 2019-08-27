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

import static org.junit.jupiter.api.Assertions.fail;

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
        new  SimpleSegmentTest() ,
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
         fail(e.getMessage());
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
      String action = args[0].trim();
      String path = args[1].trim(); 
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
   private static boolean validate(File inputFile) throws Exception
   { 
      boolean result = true;
      if (!inputFile.exists())
      {
         throw new Exception("File not found " + inputFile.getAbsolutePath());
      }
      System.out.println("Loading encoded segments from " + inputFile.getAbsolutePath());
 
      String jsonData = new String(Files.readAllBytes(inputFile.toPath()));
      TestCaseResult[] resultArr = new Gson().fromJson(jsonData, TestCaseResult[].class);
      
      ITestCase testCase ;
      for ( TestCaseResult caseResult: resultArr)
      { 
         if (caseResult.getId().equalsIgnoreCase(SimpleSegmentTest.id))
            testCase = new SimpleSegmentTest();
         else if (caseResult.getId().equalsIgnoreCase(SignaturesTest.id))
            testCase = new SignaturesTest();
         else if (caseResult.getId().equalsIgnoreCase(ReferencesTest.id))
            testCase = new ReferencesTest();
         else if (caseResult.getId().equalsIgnoreCase(EvidencesTest.id))
            testCase = new EvidencesTest();
         else
         {   
            
            System.err.println("Unknown test case : " + caseResult.getId());
           result &=false;
            continue;
         }
          
         try {
          testCase.validate( caseResult.getData());
          System.out.println(caseResult.getId() + " SUCCESS "  );
         }catch (Exception e) {
            System.err.println(caseResult.getId() + " FAILED " + e.getMessage());
            result &= false;
         } 
      }
       
      return result;
   }
   
   /**
    * Generate encoded test segments.
    * @param path to the file where test segments will be written.
    * @throws  Exception 
    */
   private static boolean generate(File outputFile) throws Exception
   {
       System.out.println ("Saving encoded segments to " + outputFile.getAbsolutePath()  ); 
       if (!outputFile.getAbsoluteFile().getParentFile().exists())
       {  //create directory if doesn't exist.
          outputFile.getParentFile().mkdirs();
       }
       boolean result = true;
       List<TestCaseResult> results = new ArrayList<TestCaseResult>();
       for (ITestCase tcase: TestCases)
       {
          try
         {
            results.add(new TestCaseResult(tcase.getId(), tcase.generate()));
         }
         catch(Exception e)
         { 
            result &=false;
             e.printStackTrace();
            results.add(new TestCaseResult(tcase.getId(),  e.getMessage()));
         }
       }
        
       String resultStr = (new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()).toJson(results);
       Files.write(outputFile.toPath(), resultStr.getBytes() );
       return result;
   }
   
   
   

}
