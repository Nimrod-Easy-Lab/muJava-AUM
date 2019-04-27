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


package openjava.test;
// A Java implementation of VendingMachine.
// Original in C++ by Liu Ling,
// Taken from a paper by Mary Jean Harrold.
// Translated to Java by Jeff Offutt, January 2004
// Updated for Java 1.5, 2006 (probably imperfectly), Jeff Offutt.
// Main added later for testing.  
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

public class VendingMachine1
{
private int credit;
private LinkedList stock;

// Maximum size of vendingMachine
private static final int MAX = 10;

// In the C++ version, Ling used a C++ list<string>
// with operations like push(), pop(), etc to look
// a lot like a queue.
// list<string> stock; ??
// In the Java version I'm emulating a queue on top of the
// LinkedList. Ugly, but I don't want to redesign.

//************************************************
// Constructor
// vendingmachine starts empty.
//************************************************
VendingMachine1()
{
   credit = 0;
   stock  = new LinkedList(); // Empty stock.
}

//************************************************
// A coin is given to the vendingMachine.
// Must be a dime, quarter or dollar.
// Ignores invalid input
//************************************************
public void coin (int coin)
{
   if (coin != 10 && coin != 25 && coin != 100)
      return;
   if (credit >= 90)
      return;
   credit = credit + coin;
   return;
}

//************************************************
// User asks for a chocolate.
// Returns the change and the sets the
// parameter StringBuffer variable Choc.
// If not enough money or no chocolates,
// returns 0 and a blank string.
//************************************************
// Necessary because strings are immutable
// C++ version returned both choc and change as parameters
public int getChoc (StringBuffer choc)
{
   int change;

   if (credit < 90 || stock.size() <= 0)
   {
      change = 0;
      choc.replace (0, choc.length(), "");
      return (change);
   }
   change = credit - 90;
   credit = 0;

   choc.replace (0, choc.length(), (String) stock.removeFirst());

   return (change);
}

//************************************************
// Adds one new piece of chocolate to the machine
// If machine is full, nothing happens
//************************************************
public void addChoc (String choc)
{
   if (stock.size() >= MAX)
      return;
   stock.add (choc);
   return;
}

//Needed for testing -- increases observability
public int getCredit ()
{
	return (credit);
}

//Needed for testing -- increases observability
public LinkedList getStock ()
{
	Integer i = new Integer(1);
	return (stock);
}

/*

//************************************************
// mainV1() for initial testing.
//************************************************
public static void main (String[] argv) throws Exception
{
	/*
   StringBuffer choc = new StringBuffer ("xx");
   VendingMachine v = new VendingMachine ();
   v.addChoc ("c1");
   v.addChoc ("c2");
   v.addChoc ("c3");
   v.coin (10);
   v.coin (25);
   v.coin (100);
   int ch = v.getChoc (choc);
   System.out.println ("First get, c: " + choc + ", ch: " + ch);

   ch = v.getChoc (choc);
   v.coin (100);
   ch = v.getChoc (choc);
   System.out.println ("Second get, c: " + choc + ", ch: " + ch);
   */
/*
   VMTEST1 vmTest = new VMTEST1();
   vmTest.setUp();
   vmTest.test1();
   
   /*
   Class vmClass = vmTest.getClass();
   Method methods[] = vmClass.getDeclaredMethods();
   for (int i = 0;i < methods.length; i++){
	   System.out.println("method name: " + methods[i].getName());
	   System.out.println("Return type: " + methods[i].getReturnType());
   }
   
}*/

} // End class vendingMachine
