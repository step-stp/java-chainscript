# ChainScript-java

To build/test the project and run one of the following.


**RunTest.cmd** 
Runs Tests only 
 
**RunTest.cmd Generate *[put path for JSON file]*  **
The generated data file  will contain an object of 4 test case ids and their corresponding encoded segment object.
or 
**RunTest.cmd Validate *[put path for JSON file]*    **
The validate will read the generated file, deserialize it and check all the data within against all the test cases.  
  
 
To clean a previous build run the following: 
mvnw.cmd clean 

