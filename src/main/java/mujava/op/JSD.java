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

import mujava.MutationSystem;
import mujava.op.util.LogReduction;
import openjava.mop.FileEnvironment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * <p>Generate JSD (Java-specific static modifier deletion) --
 *    remove each instance of the <i>static</i> modifier 
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
 */

public class JSD extends mujava.op.util.Mutator {
  Vector staticFields;
  boolean isField;

  public JSD(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit) {
	super(file_env, comp_unit);
	staticFields = new Vector();
	isField = false;
  }

  public void visit(ClassDeclaration p) throws ParseTreeException {
	if (p.getName().equals(MutationSystem.CLASS_NAME)) {
	  super.visit(p);
	  for (int i = 0; i < staticFields.size(); i++) {
		outputToFile((FieldDeclaration) (staticFields.get(i)));
	  }
	}
  }

  public void visit(FieldDeclaration p) throws ParseTreeException {
	if (p.getModifiers().contains(ModifierList.STATIC) && !isEquivalent(p)) {
	  staticFields.add(p);
	}
  }

  public void visit(MethodDeclaration p) throws ParseTreeException {
	if (p.getModifiers().contains(ModifierList.STATIC)) {
	  super.visit(p);
	}
  }

  public void visit(Variable p) throws ParseTreeException {
	for (int i = 0; i < staticFields.size(); i++) {
	  FieldDeclaration fd = (FieldDeclaration) (staticFields.get(i));
	  String name = fd.getName();
	  if (name.equals(p.toString())) {
		staticFields.remove(fd);
		return;
	  }
	}
  }

  /**
   * Output JSD mutants to files
   * @param original
   */
  public void outputToFile(FieldDeclaration original) {
	if (comp_unit == null)
	  return;

	String f_name;
	num++;
	f_name = getSourceName(this);
	String mutant_dir = getMuantID();

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  JSD_Writer writer = new JSD_Writer(mutant_dir, out);
	  writer.setMutant(original);
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

  public boolean isEquivalent(FieldDeclaration fieldDeclaration) {
       /*
        "term = private static final type := value;
        transformations = {
          JSD(static) = ;
        }
        constraints = {

        }"
        */
	boolean e_rule_22 = false;
	if (fieldDeclaration.getModifiers().contains(ModifierList.PRIVATE) &&
		fieldDeclaration.getModifiers().contains(ModifierList.STATIC) &&
		fieldDeclaration.getModifiers().contains(ModifierList.FINAL) &&
		fieldDeclaration.getTypeSpecifier().equals(String.class)) {
	  e_rule_22 = LogReduction.AVOID;
	  System.out.println("JSD E22 rule for expression >>>>" + fieldDeclaration.toFlattenString());
	}
	return e_rule_22;
  }
}
