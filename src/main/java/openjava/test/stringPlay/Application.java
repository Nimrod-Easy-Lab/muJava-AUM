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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

/**
 * Contains methods for string manipulation through the console
 *
 */
public class Application
{
   /**
    * Starts the console driven application.
    */
   public void go()
   {
      BufferedReader br = new BufferedReader (new InputStreamReader (System.in));
      String input;
      try
      {
         while (true)
         {
            System.out.println ("Enter an option:\n" +
                  "0: Quit.\n" +
                  "1: Break the string into substrings (words) using spaces as separators.\n" +
                  "2: Count the number of characters, numeric digits, alphabetic characters, and other characters.\n" +
                  "3: Invert the String.\n" +
                  "4: Count the unique occurances of each word using punctuation and spaces as separators.\n" +
                  "5: Convert to uppercase.\n" +
                  "6: Convert to lowercase.");
            input = br.readLine();

            if (input.equals ("0"))
            {
               break;
            } else if (input.equals ("1")) {
               subString (askForString (br));
            } else if (input.equals ("2")) {
               charCount (askForString (br));
            } else if (input.equals ("3")) {
               invert (askForString (br));
            } else if (input.equals ("4")) {
               uniqueOccurrences (askForString (br));
            } else if (input.equals ("5")) {
               // vowelCase (askForString (br));
               toUppercase (askForString (br));
            } else if (input.equals ("6")) {
               toLowercase (askForString (br));
            } else {
               System.out.println ("Enter option 1-6, or 0 to quit.");
            }
            System.out.println();
         }
         br.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   /**
    * Requests a String from the console.
    *
    * @param br is the console's input.
    * @return a string for what was entered.
    */
   protected String askForString (BufferedReader br)
   {
      System.out.println ("Enter a string up to 1000 characters:");
      String result = "";
      try
      {
         result = br.readLine();
         if (result.length() > 1000)
         {
            throw new Exception ("String must be less than 1000 characters.");
         }
      } catch (IOException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return result;
   }

   /**
    * Prints the input with a new line after each space.
    *
    * @param val is the input String.
    */
   protected void subString (String val)
   {
      StringTools input = new StringTools (val);
      String word = "";

      for (int i = 0; i < input.length(); i++)
      {
         if (input.getCharAt(i) == ' ')
         {
            System.out.println (word);
            word = "";
         } else {
            word += input.getCharAt (i);
         }
      }
   }

   /**
    * Prints the count of alphabetic, numeric and other characters.
    *
    * @param val is the input String.
    */
   protected void charCount (String val)
   {
      StringTools input = new StringTools (val);

      int alphabeticCount = 0;
      int numericCount = 0;
      int otherCount = 0;

      for (int i = 0; i < input.length(); i++)
      {
         if (String.valueOf (input.getCharAt(i)).matches ("\\d"))
         {
            numericCount++;
         } else if (String.valueOf (input.getCharAt(i)).matches ("[a-z]")) {
            alphabeticCount++;
         } else if (String.valueOf (input.getCharAt(i)).matches ("\\S")) {
            otherCount++;
         }
      }
      System.out.println ("Alphabetic: " + alphabeticCount);
      System.out.println ("Numeric: " + numericCount);
      System.out.println ("Other: " + otherCount);
   }

   /**
    * Prints the input in reverse.
    *
    * @param val  is the input String.
    */
   protected void invert (String val)
   {
      StringTools input = new StringTools (val);

      for (int i = input.length(); i >= 0; i--)
      {
         System.out.print (input.getCharAt(i));
      }
   }

   /**
    * Prints the input in upper case letters.
    *
    * @param val is the input String.
    */
   protected void toUppercase (String val)
   {
      StringTools input = new StringTools (val);

      for (int i = 0; i < input.length(); i++)
      {
         char letter = input.getCharAt(i);
         if (String.valueOf (letter).matches ("[A-Za-z0-9 ]"))
         {
            // System.out.print ((char) (letter - 32));
            System.out.print ((char) (Character.toUpperCase(letter)));
         } else {
            // System.out.print (letter);
         }
      }
   }

   /**
    * Prints every unique word in the input and the number of occurrences.
    *
    * @param val is the input String.
    */
   protected void uniqueOccurrences (String val)
   {
      LinkedHashMap<String, Integer> seen = new LinkedHashMap<String, Integer>();

      for (String word : val.split ("([.,!?:;'\"-]|\\s)"))
      {
         if (seen.containsKey (word))
         {
            seen.put (word, seen.get (word)+1);
         } else {
            seen.put (word, 1);
         }
      }
      for (String w : seen.keySet())
      {
         System.out.println (seen.get(w)+ " " + w);
      }
   }

   /**
    * Prints the input in lower case letters.
    *
    * @param val is the input String.
    */
   protected void toLowercase (String val)
   {
      StringTools input = new StringTools (val);

      for (int i = 0; i < input.length(); i++)
      {
         char letter = input.getCharAt(i);
         if (String.valueOf (letter).matches ("[A-Z]"))
         {
            System.out.print ((char) (letter + 32));
         } else {
            System.out.print (letter);
         }
      }
   }
}
