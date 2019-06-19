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

public enum Planet {
	    MERCURY (3.303e+23, 2.4397e6){
	    	public String haha="";
	    	public double mass(){
	    		return 0;
	    	}
	    	 final class class1{
	    		class haha{
	    		}
	    	}
	    },
	    VENUS   (4.869e+24, 6.0518e6),
	    EARTH   (5.976e+24, 6.37814e6),
	    MARS    (6.421e+23, 3.3972e6),
	    JUPITER (1.9e+27,   7.1492e7),
	    SATURN  (5.688e+26, 6.0268e7),
	    URANUS  (8.686e+25, 2.5559e7),
	    NEPTUNE (1.024e+26, 2.4746e7);

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
