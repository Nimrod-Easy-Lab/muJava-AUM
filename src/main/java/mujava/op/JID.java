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

import mujava.op.util.LogReduction;
import openjava.mop.FileEnvironment;
import openjava.mop.OJSystem;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>
 * Generate JID (Java-specific member variable initialization deletion) --
 * remove initialization of each member variable
 * </p>
 *
 * @author Yu-Seung Ma
 * @version 1.0
 */

public class JID extends mujava.op.util.Mutator {
  public JID(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit) {
	super(file_env, comp_unit);
  }

  /**
   * If an instance variable is not final, delete its initialization
   */
  public void visit(FieldDeclaration p) throws ParseTreeException {
	VariableInitializer initializer = p.getInitializer();
	if (p.getModifiers().contains(ModifierList.FINAL))
	  return;
	// Leo: Adicionei a chamada para equalsToDefaultValue
	if (initializer != null && !isEquivalent(initializer, p))
	  outputToFile(p);
  }

  // Leo: Metodo para verificar se o Field foi inicializado com o valor
  // default.
  private boolean isEquivalent(VariableInitializer initializer, FieldDeclaration p) {
	String type = p.getTypeSpecifier().getName();
	boolean result = false;
	if (type.equals(OJSystem.INT.toString())) {
	  result = (initializer.equals(Literal.constantZero()) || initializer.equals(Literal.constantNull()));
	} else if (type.equals(OJSystem.SHORT.toString())) {
	  result = (initializer.equals(Literal.constantZero()) || initializer.toString().contains(("(short) 0")));
	} else if (type.equals(OJSystem.BYTE.toString())) {
	  result = (initializer.equals(Literal.constantZero()) || initializer.toString().contains(("(byte) 0")));
	} else if (type.equals(OJSystem.LONG.toString())) {
	  result = (initializer.equals(Literal.constantZero()) || initializer.toString().contains(("0L"))
		  || initializer.toString().contains(("0l")));
	} else if (type.equals(OJSystem.CHAR.toString())) {
	  result = (initializer.equals(Literal.constantZero()) || initializer.toString().contains(("\u0000")));
	} else if (type.equals(OJSystem.DOUBLE.toString())) {
	  result = (initializer.equals(Literal.constantZero()) || initializer.toString().contains(("0.0"))
		  || initializer.toString().contains(("0D")) || initializer.toString().contains(("0d")));
	} else if (type.equals(OJSystem.FLOAT.toString())) {
	  result = (initializer.equals(Literal.constantZero()) || initializer.toString().contains(("0.0"))
		  || initializer.toString().contains(("0F")) || initializer.toString().contains(("0f")));
	} else if (type.equals(OJSystem.BOOLEAN.toString())) {
	  result = (initializer.equals(Literal.constantFalse()));
	} else { // Se for Object, verifica se inicialuzou com null
	  result = (initializer.equals(Literal.constantNull()));
	}

	if (result) {
	  String desc = p.toFlattenString() + " => " + "Delete Initialization";
	  logReduction("JID", desc);
	}
	return (result && LogReduction.AVOID);

	// return result;
  }

  /**
   * Output JID mutants to files
   *
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
	  JID_Writer writer = new JID_Writer(mutant_dir, out);
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
}
