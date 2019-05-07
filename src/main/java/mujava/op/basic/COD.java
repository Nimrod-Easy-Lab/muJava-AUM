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
import java.util.List;

/**
 * <p>
 * Generate COD (Conditional Operator Deletion) mutants -- delete each
 * occurrence of logical operators (and-&&, or-||, and with no conditional
 * evaluation-&, or with no conditional evaluation-|, not equivalent-^)
 * </p>
 *
 * @author Yu-Seung Ma
 * @version 1.0
 */

public class COD extends MethodLevelMutator {
  private List<String> allOperatorsSelected;

  public COD(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit) {
	super(file_env, comp_unit);
  }

  public COD(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit, List<String> allOperators) {
	super(file_env, comp_unit);
	allOperatorsSelected = allOperators;
  }

  public void visit(UnaryExpression p) throws ParseTreeException {
	int op = p.getOperator();
	if (op == UnaryExpression.NOT) {
	  if (!isDuplicated(p)) {
		outputToFile(p);
	  }
	}
  }

  /**
   * Output COD mutants to files
   *
   * @param original
   */
  public void outputToFile(UnaryExpression original) {
	if (comp_unit == null)
	  return;

	String f_name;
	num++;
	f_name = getSourceName("COD");
	String mutant_dir = getMuantID("COD");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  COD_Writer writer = new COD_Writer(mutant_dir, out);
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
  private boolean isDuplicated(UnaryExpression exp) {
	// #Rule 1: ROR x COD (it's unnecessary use COD when ROR is selected)
	// It's only working with IF's. Need more investigation with While
	// and others Java constructions
	if (exp.getContents() != null && exp.getContents().length > 0) {
	  if ((exp.getContents()[0] instanceof BinaryExpression)) {
		BinaryExpression bexp = (BinaryExpression) exp.getContents()[0];
		int op_type = bexp.getOperator();
		if ((op_type == BinaryExpression.GREATER) || (op_type == BinaryExpression.GREATEREQUAL)
			|| (op_type == BinaryExpression.LESSEQUAL) || (op_type == BinaryExpression.EQUAL)
			|| (op_type == BinaryExpression.NOTEQUAL) || (op_type == BinaryExpression.LESS)) {

		  if (allOperatorsSelected.contains("ROR")) {
			String desc = exp.toFlattenString() + " => " + exp.getExpression();
			logReduction("COD", "ROR", desc);
			return LogReduction.AVOID;
		  }
		}
	  }
	}

	// #Rule 2: ODL x COD (So COD as ODL delete ! unary operator)
	if (allOperatorsSelected.contains("ODL")) {
	  String desc = exp.toFlattenString() + " => " + exp.getExpression();
	  logReduction("COD", "ODL", desc);
	  return LogReduction.AVOID;
	}

	return false;
  }
}
