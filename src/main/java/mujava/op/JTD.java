/**
 * Copyright (C) 2015  the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mujava.op;

import openjava.mop.FileEnvironment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * <p>Generate JTD (Java-specific this keyword deletion) --
 *    delete each occurrence of the keyword <i>this</i>
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
 */

public class JTD extends mujava.op.util.Mutator {
  Vector instanceVar = new Vector();
  Vector localVar = new Vector();
  boolean isJTDTarget = false;

  public JTD(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit) {
	super(file_env, comp_unit);
  }
	/*
   public void visit( AssignmentExpression p ) throws ParseTreeException
   {
   	System.err.println("Assign");
      Expression newp = this.evaluateDown( p );
      if (newp != p)
      {
      	System.err.println("newp != p (1)");
         p.replace( newp );
		 return;
	  }

	  p.childrenAccept( this );
	  newp = this.evaluateUp( p );
	  if (newp != p) {
	  	System.err.println("newp != p (2)");
		 p.replace( newp );
		}
   }*/

  void addTarget(Parameter p) throws ParseTreeException {
	// add parameter to local list, iff exists a instance variable with the same name
	for (int i = 0; i < instanceVar.size(); i++) {
	  String field_name = instanceVar.get(i).toString();
	  if (field_name.equals(p.getVariable())) {
		localVar.add(p.getVariable());
	  }
	}
  }

  public void visit(FieldDeclaration p) throws ParseTreeException {
	instanceVar.add(p.getName());
  }

  public void visit(VariableDeclaration p) throws ParseTreeException {
	localVar.add(p.getVariable());
  }

  public void visit(ConstructorDeclaration p) throws ParseTreeException {
	ParameterList plist = p.getParameters();
	localVar.removeAllElements();
	for (int i = 0; i < plist.size(); i++) {
	  addTarget(plist.get(i));
	}
	super.visit(p);
  }

  public void visit(MethodDeclaration p) throws ParseTreeException {
	ParameterList plist = p.getParameters();
	localVar.removeAllElements();
	for (int i = 0; i < plist.size(); i++) {
	  addTarget(plist.get(i));
	}
	super.visit(p);
  }

  public void visit(FieldAccess p) throws ParseTreeException {
	Expression ref_exp = p.getReferenceExpr();
	String var_name = p.getName();
	if (ref_exp instanceof SelfAccess) {
	  if (((SelfAccess) ref_exp).getAccessType() == SelfAccess.THIS) {
		for (int i = 0; i < localVar.size(); i++) {
		  if (var_name.equals(localVar.get(i).toString())) {
			FieldAccess mutant = (FieldAccess) p.makeRecursiveCopy();
			mutant.setReferenceExpr(null);
			outputToFile(p, mutant);
		  }
		}
	  }
	}
  }

  /**
   * Output JTD mutants to files
   * @param original
   * @param mutant
   */
  public void outputToFile(FieldAccess original, FieldAccess mutant) {
	if (comp_unit == null) return;

	String f_name;
	num++;
	f_name = getSourceName(this);
	String mutant_dir = getMuantID();

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  ISK_JTD_Writer writer = new ISK_JTD_Writer(mutant_dir, out);
	  writer.setMutant(original, mutant);
	  comp_unit.accept(writer);
	  out.flush();
	  out.close();
	} catch (IOException e) {
	  System.err.println("fails to create " + f_name);
	} catch (ParseTreeException e) {
	  System.err.println("errors during printing " + f_name);
	  e.printStackTrace();
	}
  }

  /**
   * Output JTD mutants to files
   * @param original
   * @param mutant
   */
  public void outputToFile(MethodCall original, MethodCall mutant) {
	if (comp_unit == null) return;

	String f_name;
	num++;
	f_name = getSourceName(this);
	String mutant_dir = getMuantID();

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  ISK_JTD_Writer writer = new ISK_JTD_Writer(mutant_dir, out);
	  writer.setMutant(original, mutant);
	  comp_unit.accept(writer);
	  out.flush();
	  out.close();
	} catch (IOException e) {
	  System.err.println("fails to create " + f_name);
	} catch (ParseTreeException e) {
	  System.err.println("errors during printing " + f_name);
	  e.printStackTrace();
	}
  }
}
