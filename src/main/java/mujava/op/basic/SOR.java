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
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>Generate SOR (Shift Operator Replacement) mutants --
 * replace each occurrence of one of the shift operators <<, >>, and >>>
 * by each of the other operators
 * </p>
 *
 * @author Yu-Seung Ma
 * @version 1.0
 */

public class SOR extends MethodLevelMutator {
  private java.util.List<String> allOperatorsSelected;

  public SOR(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit) {
	super(file_env, comp_unit);
	allOperatorsSelected = new java.util.ArrayList<>();
  }

  public SOR(FileEnvironment file_env, ClassDeclaration cdecl,
			 CompilationUnit comp_unit, java.util.List<String> allOperatorsSelected) {
	this(file_env, cdecl, comp_unit);
	this.allOperatorsSelected = allOperatorsSelected;
  }

  public void visit(BinaryExpression p) throws ParseTreeException {
	Expression left = p.getLeft();
	left.accept(this);

	int op_type = p.getOperator();
	if ((op_type == BinaryExpression.SHIFT_L) || (op_type == BinaryExpression.SHIFT_R)
		|| (op_type == BinaryExpression.SHIFT_RR)) {
	  sorMutantGen(p, op_type);
	}

	Expression right = p.getRight();
	right.accept(this);
  }

  private void sorMutantGen(BinaryExpression exp, int op) {
	BinaryExpression mutant;

	if (op != BinaryExpression.SHIFT_L) {
	  mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	  if (!isDuplicated(exp, BinaryExpression.SHIFT_L)) {
		mutant.setOperator(BinaryExpression.SHIFT_L);
		outputToFile(exp, mutant);
	  }
	}

	if (op != BinaryExpression.SHIFT_R) {
	  mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	  if (!isDuplicated(exp, BinaryExpression.SHIFT_R)) {
		mutant.setOperator(BinaryExpression.SHIFT_R);
		outputToFile(exp, mutant);
	  }
	}

	if (op != BinaryExpression.SHIFT_RR) {
	  mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	  if (!isDuplicated(exp, BinaryExpression.SHIFT_RR)) {
		mutant.setOperator(BinaryExpression.SHIFT_RR);
		outputToFile(exp, mutant);
	  }
	}
  }

  /**
   * Output SOR mutants to files
   *
   * @param original
   * @param mutant
   */
  public void outputToFile(BinaryExpression original, BinaryExpression mutant) {
	if (comp_unit == null)
	  return;

	String f_name;
	num++;
	f_name = getSourceName("SOR");
	String mutant_dir = getMuantID("SOR");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  SOR_Writer writer = new SOR_Writer(mutant_dir, out);
	  writer.setMutant(original, mutant);
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

  public boolean isDuplicated(BinaryExpression binaryExpression, int op2) {
	boolean d_sor46 = false;
	if ((binaryExpression.getOperator() == BinaryExpression.SHIFT_L)
		&& ((op2 == BinaryExpression.SHIFT_R) || (op2 == BinaryExpression.SHIFT_RR))) {
	  d_sor46 = LogReduction.AVOID;
	  String desc = binaryExpression.toFlattenString() + " => "
		  + (op2 == BinaryExpression.SHIFT_R ? ">>" : ">>>");
	  logReduction("SOR", "SOR", desc);
	}
	return d_sor46;
  }
}
