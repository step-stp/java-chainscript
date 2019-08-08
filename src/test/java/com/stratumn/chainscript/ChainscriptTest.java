package com.stratumn.chainscript;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import com.stratumn.canonicaljson.CanonicalJson;
import com.stratumn.chainscript.testcases.EvidencesTest;
import com.stratumn.chainscript.testcases.ITestCase;
import com.stratumn.chainscript.testcases.ReferencesTest;
import com.stratumn.chainscript.testcases.SignaturesTest;
import com.stratumn.chainscript.testcases.SimpleSegmentTest; 

class ChainscriptTest
{

   static List<ITestCase> TestCases =  
      Arrays.asList(new ITestCase[] { 
         new ReferencesTest(),
         new EvidencesTest(),
         new SignaturesTest(),
         new  SimpleSegmentTest()
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
//         System.out.println("If Parameter is a folder, searches for all subfolders with input.json (and expected.json)."
//            + "\rReads/parses DataFolder/input.json then serializes it to canonical json and write output.json in same folder. "
//            + "\rCompares output to expected.json if found under same folder. ");
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
      @SuppressWarnings("unchecked")
      Map<String,Object> data = (Map<String, Object>) CanonicalJson.parse(jsonData);
      ITestCase testCase ;
      for ( Entry<String, Object> entry: data.entrySet())
      { 
         if (entry.getKey().equalsIgnoreCase(SimpleSegmentTest.id))
            testCase = new SimpleSegmentTest();
         else if (entry.getKey().equalsIgnoreCase(SignaturesTest.id))
            testCase = new SignaturesTest();
         else if (entry.getKey().equalsIgnoreCase(ReferencesTest.id))
            testCase = new ReferencesTest();
         else if (entry.getKey().equalsIgnoreCase(EvidencesTest.id))
            testCase = new EvidencesTest();
         else
         {   System.err.println("Unknown test case : " + entry.getKey());
           
           continue;
         }
          
         try {
          testCase.validate((String) entry.getValue());
          System.out.println(entry.getKey() + " SUCCESS "  );
         }catch (Exception e) {
            System.err.println(entry.getKey() + " FAILED " + e.getMessage());
          
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
       Map<String,String> results = new HashMap<String,String>();
       for (ITestCase tcase: TestCases)
       {
          try
         {
            results.put(tcase.getId(), tcase.generate());
         }
         catch(Exception e)
         {  e.printStackTrace();
            results.put(tcase.getId(), e.getMessage());
         }
       }
       
       System.out.println ("Saving encoded segments to " + outputFile.getAbsolutePath()  );
       String resultStr = CanonicalJson.stringify(results);
       Files.write(outputFile.toPath(), resultStr.getBytes() );
   }

}
