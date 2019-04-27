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

package openjava.test.stringPlay;


/**
 * Holds a String representation and provides some common operations
 *
 */
public class StringTools
{
   private char[] value;
   private int length;
   
   /**
    * creates a StringTools object with the given String
    * @param input
    */
   public StringTools (String input)
   {
      value = new char[1000];
      length = input.length();
      for (int i = 0; i < input.length(); i++)
      {
         value[i] = input.charAt(i);
      }
   }
   
   /**
    * @return the length of the char[]
    */
   public int length()
   {
      return length;
   }
   
   /**
    * @param i is the index of the char to get.
    * @return the char at the selected index.
    */
   public char getCharAt (int i)
   {
      return value[i];
   }
}
