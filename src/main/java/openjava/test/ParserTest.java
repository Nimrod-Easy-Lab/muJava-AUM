/**
 * Copyright (C) 2015  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

 /**
 * <p>Description: </p>
 * @author Jeff Offutt and Yu-Seung Ma
 * @version 1.0
  */  
package openjava.test;

import openjava.mop.OJSystem;
import openjava.ptree.CompilationUnit;
import openjava.tools.parser.ParseException;
import openjava.tools.parser.Parser;

public class ParserTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		//String file = "src/openjava/test/Search.java";
		//String file = "src/openjava/test/Planet.java";
		//String file = "src/openjava/test/VendingMachine1.java";
		String file = "src/openjava/test/Flower.java";
		//String file = "e://muJavaJunit//src/junit/framework/TestSuite.java";
		//String file = "src/openjava/test/RequestForEnhancement.java";
		//String file = "src/openjava/test/stringPlay/StringTools.java";
//		String file = "src/openjava/test/Logic.java";
	      Parser parser = null;
	      try
	      {
	         parser = new Parser(new java.io.FileInputStream( file ) );
	      } 
	      catch ( java.io.FileNotFoundException e ) 
	      {
	         System.err.println( "File " + file + " not found." );
	      }
	      catch (Exception e)
	      {
	    	  e.printStackTrace();
	      }
	      
	      try
	      {
	    	 OJSystem.initConstants();
	         CompilationUnit result = parser.CompilationUnit( OJSystem.env );
	         //System.out.println("result: " + result);

	        // System.out.println("getComment: " + result.getComment());
	         /*
	         for(int i = 0; i < result.getDeclaredImports().length;i++)
	         {
	        	 System.out.println("getDeclaredImports: " + result.getDeclaredImports()[i]);
	         }
	         for(int i = 0; i < result.getContents().length;i++)
	         {
	        	 System.out.println("getContents: " + result.getContents()[i]);
	         }
	          */
	      } 
	      catch (ParseException e) 
	      {
	    	  e.printStackTrace();
	    	  System.out.println(" can't generate parse tree");
	      }
	      catch (Exception e)
	      {
	    	  System.out.println(e);
	    	  e.printStackTrace();
	      }

	}

}
