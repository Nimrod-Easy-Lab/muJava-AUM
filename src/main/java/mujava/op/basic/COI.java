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
package mujava.op.basic;

import mujava.op.util.LogReduction;
import openjava.mop.FileEnvironment;
import openjava.mop.OJSystem;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * <p>
 * Generate COI (Conditional Operator Insertion) mutants -- insert logical
 * operators (and-&&, or-||, and with no conditional evaluation-&, or with no
 * conditional evaluation-|, not equivalent-^)
 * </p>
 *
 * @author Yu-Seung Ma
 * @version 1.0
 */

public class COI extends MethodLevelMutator {
  private List<String> allOperatorsSelected;

  public COI(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit) {
	super(file_env, comp_unit);
  }

  public COI(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit, List<String> allOperators) {
	super(file_env, comp_unit);
	allOperatorsSelected = allOperators;
  }

  public void visit(UnaryExpression p) {
	// NO OP
  }

  public void visit(Variable p) throws ParseTreeException {
	if (getType(p) == OJSystem.BOOLEAN) {
	  outputToFile(p);
	}
  }

  public void visit(FieldAccess p) throws ParseTreeException {
	if (getType(p) == OJSystem.BOOLEAN) {
	  outputToFile(p);
	}
  }

  public void visit(BinaryExpression p) throws ParseTreeException {
	Expression left = p.getLeft();
	left.accept(this);
	Expression right = p.getRight();
	right.accept(this);

	if (getType(p) == OJSystem.BOOLEAN) {
	  if (!isDuplicated(p)) {
		outputToFile(p);
	  }
	}
  }

  /**
   * Output COI mutants to files
   *
   * @param original
   */
  public void outputToFile(BinaryExpression original) {
	if (comp_unit == null)
	  return;

	String f_name;
	num++;
	f_name = getSourceName("COI");
	String mutant_dir = getMuantID("COI");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  COI_Writer writer = new COI_Writer(mutant_dir, out);
	  writer.setMutant(original);
	  writer.setMethodSignature(currentMethodSignature);
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
   * Output COI mutants to files
   *
   * @param original
   */
  public void outputToFile(Variable original) {
	if (comp_unit == null)
	  return;

	String f_name;
	num++;
	f_name = getSourceName("COI");
	String mutant_dir = getMuantID("COI");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  COI_Writer writer = new COI_Writer(mutant_dir, out);
	  writer.setMutant(original);
	  writer.setMethodSignature(currentMethodSignature);
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
   * Output COI mutants to files
   *
   * @param original
   */
  public void outputToFile(FieldAccess original) {
	if (comp_unit == null)
	  return;

	String f_name;
	num++;
	f_name = getSourceName("COI");
	String mutant_dir = getMuantID("COI");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  COI_Writer writer = new COI_Writer(mutant_dir, out);
	  writer.setMutant(original);
	  writer.setMethodSignature(currentMethodSignature);
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
   * Avoid generate duplicated mutants
   *
   */
  private boolean isDuplicated(BinaryExpression exp) {
	// #Rule 1: ROR x COI(1) (it's unnecessary use COI when ROR is selected)
	int op_type = exp.getOperator();
	if ((op_type == BinaryExpression.GREATER) || (op_type == BinaryExpression.GREATEREQUAL)
		|| (op_type == BinaryExpression.LESSEQUAL) || (op_type == BinaryExpression.EQUAL)
		|| (op_type == BinaryExpression.NOTEQUAL) || (op_type == BinaryExpression.LESS)) {

	  // It's only working with IF's. Need more investigation with While
	  // and others Java constructions
	  if (exp.getParent() instanceof IfStatement) {
		if (allOperatorsSelected.contains("ROR")) {
		  String desc = exp.toFlattenString() + " => " + "!(" + exp.toFlattenString() + ")";
		  logReduction("COI", "ROR", desc);
		  return LogReduction.AVOID;
		}
	  }
	}

	return false;
  }

}
