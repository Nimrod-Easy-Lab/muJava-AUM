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

import java.util.Arrays;
import java.util.List;
import static java.lang.Math.*;

	public class Box1<T> {

	    private T t;    
	    List<? extends Number> ln ;

	    public void add(T t) {
	        this.t = t;
	    }

	    public T get() {
	        return t;
	    }

	    public <U extends Number> void inspect(U u){
	        System.out.println("T: " + t.getClass().getName());
	        System.out.println("U: " + u.getClass().getName());
	    }
	    
	    public static double sumOfList(List<? extends Number> list) {
	        double s = 0.0 + abs(9);
	        for (Number n : list)
	            s += n.doubleValue();
	        return s;
	    }
	    
	    public static void printList(List<?> list) {
	        for (Object elem: list)
	            System.out.print(elem + " ");
	        System.out.println(PI);
	    }
	    
	    public static void addNumbers(List<? super Integer> list) {
	        for (int i = 1; i <= list.size(); i++) {
	            list.add(i);
	        }
	    }
		
	    public static void printSpaced(@RequestForEnhancement( id       = 2868724,
	    	    synopsis = "Enable time-travel",
	    	    engineer = "Mr. Peabody",
	    	    date     = "4/1/3007") Character... objects) {
	    	   for (Object o : objects)
	    	     System.out.print(o + " ");
	    }
	    
	    public static void printSpaced1(final Character... objects) {
	    	   for (Object o : objects)
	    	     System.out.print(o + " ");
	    }
	    
	    //
	    public static void main(String[] args) {
	        Box1<Integer> integerBox = new Box1<Integer>();
	        integerBox.add(new Integer(10));
	        integerBox.inspect(new Integer(10));
	        assert 1<2;
	        List<Integer> li = Arrays.asList(1, 2, 3);
	        List<String>  ls = Arrays.asList("one", "two", "three");
	        printList(li);
	        printList(ls);
	        printSpaced('t');
	    }
	    
	    public class InnerBox<T extends Comparable<T>>{
		    public int countGreaterThan(T[] anArray, T elem) {
			   	int count = 0;
			    for (T e : anArray)
			        if (e.compareTo(elem) > 0)
			            ++count;
			        return count;
		    }
	    }
}
