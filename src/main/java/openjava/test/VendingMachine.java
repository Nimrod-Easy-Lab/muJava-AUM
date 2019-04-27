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




// A Java implementation of VendingMachine.
// Original in C++ by Liu Ling,
// Taken from a paper by Mary Jean Harrold.
// Translated to Java by Jeff Offutt, January 2004
// Updated for Java 1.5, 2006 (probably imperfectly), Jeff Offutt.
// Main added later for testing.  

package openjava.test;

import java.util.*;
import java.io.*;
@RequestForEnhancement(
	    id       = 2868724,
	    synopsis = "Enable time-travel",
	    engineer = "Mr. Peabody",
	    date     = "4/1/3007"
	)
public class VendingMachine
{
	
	public class haha{
		
	}
public enum Season {Winter, Spring, SUMMER, FALL}

public enum Planet {
    MERCURY (3.303e+23, 2.4397e6),
    VENUS   (4.869e+24, 6.0518e6),
    EARTH   (5.976e+24, 6.37814e6),
    MARS    (6.421e+23, 3.3972e6),
    JUPITER (1.9e+27,   7.1492e7),
    SATURN  (5.688e+26, 6.0268e7),
    URANUS  (8.686e+25, 2.5559e7),
    NEPTUNE (1.024e+26, 2.4746e7);

    public enum SecondSeason {Winter, Spring, SUMMER, FALL}
    private final double mass;   // in kilograms
    private final double radius; // in meters
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }
    private double mass() { return mass; }
    private double radius() { return radius; }

    // universal gravitational constant  (m3 kg-1 s-2)
    public static final double G = 6.67300E-11;

    double surfaceGravity() {
        return G * mass / (radius * radius);
    }
    double surfaceWeight(double otherMass) {
        return otherMass * surfaceGravity();
    }
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Planet <earth_weight>");
            System.exit(-1);
        }
        double earthWeight = Double.parseDouble(args[0]);
        double mass = earthWeight/EARTH.surfaceGravity();
        for (Planet p : Planet.values())
           System.out.printf("Your weight on %s is %f%n",
                             p, p.surfaceWeight(mass));
    }
}
private int credit;
private LinkedList<String> stockArray;
private LinkedList<String> stock;


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
VendingMachine()
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
	for(final String s:stockArray)
		System.out.println(s.toString());
	
	for(int i = 0; i < stock.size();i++)
		System.out.println(stock.get(i).toString());
	
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
@Deprecated
@SuppressWarnings(value = "unchecked")
public LinkedList<? extends Object> getStock ()
{
	return (stock);
}

/*
public static void main(String... args) throws IOException, NotFoundException, CannotCompileException {
	
	System.out.println(System.getProperty("user.dir"));
	BufferedInputStream fin
    = new BufferedInputStream(new FileInputStream("bin/VendingMachine.class"));
    ClassFile cf = new ClassFile(new DataInputStream(fin));
    List<MethodInfo> list = cf.getMethods();
    for (MethodInfo each: list){
    	System.out.println(each.getName());
    }
    
    ClassPool pool = ClassPool.getDefault();
    CtClass cc = pool.get("VendingMachine");
    
    byte[] b = cc.toBytecode();
    System.out.println(b.length);
    File someFile = new File("java2.txt");
    FileOutputStream fos = new FileOutputStream(someFile);
    fos.write(b);
    //fos.flush();
    fos.close();

   

}*/

} // End class vendingMachine
