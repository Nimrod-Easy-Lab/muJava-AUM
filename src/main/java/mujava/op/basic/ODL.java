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
 * Generate ODL (Arithmetic Operator Replacement (Binary)) mutants -- replace an
 * arithmetic operator by each of the other operators (*, /, %, +, -)
 * </p>
 *
 * @author Lin Deng
 * @version 1.0
 */

public class ODL extends Arithmetic_OP {

  private List<String> allOperatorsSelected;
  private boolean mutantEliminated;

  public ODL(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit) {
	super(file_env, comp_unit);
  }

  public ODL(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit, List<String> allOperators) {
	super(file_env, comp_unit);
	allOperatorsSelected = allOperators;

  }

  // visit Binary Exp
  public void visit(BinaryExpression p) throws ParseTreeException {
	Expression left = p.getLeft();
	left.accept(this);

	Expression right = p.getRight();
	right.accept(this);

	aorMutantGen(p);
  }

  // visit Unary Exp
  public void visit(UnaryExpression p) throws ParseTreeException {
	// Expression mutant = p.getExpression();
	// aor_outputToFile(p, mutant);
	Expression expression = p.getExpression();
	expression.accept(this);
	aorMutantGen(p);
  }

  // visit assignment
  public void visit(AssignmentExpression p) throws ParseTreeException {
	Expression left = p.getLeft();
	left.accept(this);
	Expression right = p.getRight();
	right.accept(this);
	aorMutantGen(p);
  }

  // for assignment, remove += -= *= ... with =
  private void aorMutantGen(AssignmentExpression exp) {
	if (exp.getOperator() != 0) {
	  AssignmentExpression mutant = new AssignmentExpression(exp.getLeft(), exp.getOperator(), exp.getRight());
	  mutant.setOperator(0);
	  if (!isDuplicated(exp, mutant) && !isEquivalent(exp, mutant)) {
		aor_outputToFile(exp, mutant);
	  }
	}

  }

  private void aor_outputToFile(AssignmentExpression original, AssignmentExpression mutant) {
	if (comp_unit == null)
	  return;

	String f_name;
	num++;
	f_name = getSourceName("ODL");
	String mutant_dir = getMuantID("ODL");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  ODL_Writer writer = new ODL_Writer(mutant_dir, out);
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

  //
  private void aorMutantGen(UnaryExpression exp) {
	Expression mutant = exp.getExpression();

	// System.out.println(exp+" => "+mutant);
	if (!isDuplicated(exp, mutant) && !isEquivalent(exp, mutant)) {
	  aor_outputToFile(exp, mutant);
	}

  }

  private void aorMutantGen(BinaryExpression exp) {
	Expression mutantLeft = exp.getLeft();
	Expression mutantRight = exp.getRight();

	// If mutantLeft equals to mutantRight, just need one
	// if (!mutantLeft.equals(mutantRight)) {
	if (!isDuplicated(exp, mutantLeft) && !isEquivalent(exp, mutantLeft)) {
	  aor_outputToFile(exp, mutantLeft);
	}
	// }

	if (!isDuplicated(exp, mutantRight) && !isEquivalent(exp, mutantRight)) {
	  aor_outputToFile(exp, mutantRight);
	}

  }

  /**
   * Output ODL mutants to file
   *
   * @param original
   * @param mutant
   */
  public void aor_outputToFile(BinaryExpression original, BinaryExpression mutant) {
	if (comp_unit == null)
	  return;

	String f_name;
	num++;
	f_name = getSourceName("ODL");
	String mutant_dir = getMuantID("ODL");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  ODL_Writer writer = new ODL_Writer(mutant_dir, out);
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

  public void aor_outputToFile(BinaryExpression original, Expression mutant) {
	if (comp_unit == null)
	  return;

	String f_name;
	num++;
	f_name = getSourceName("ODL");
	String mutant_dir = getMuantID("ODL");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  ODL_Writer writer = new ODL_Writer(mutant_dir, out);
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

  private void aor_outputToFile(UnaryExpression original, Expression mutant) {
	if (comp_unit == null)
	  return;

	String f_name;
	num++;
	f_name = getSourceName("ODL");
	String mutant_dir = getMuantID("ODL");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  ODL_Writer writer = new ODL_Writer(mutant_dir, out);
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

  /**
   * Avoid generate duplicated mutants
   *
   * @param exp
   * @param mutation
   * @return
   */
  private boolean isDuplicated(Expression exp, Expression mutation) {

	// #Rule 1: ODL x AODS (Increment and Decrement)
	if (exp instanceof UnaryExpression) {
	  int op = ((UnaryExpression) exp).getOperator();
	  if ((op == UnaryExpression.POST_DECREMENT) || (op == UnaryExpression.POST_INCREMENT)
		  || (op == UnaryExpression.PRE_DECREMENT) || (op == UnaryExpression.PRE_INCREMENT)) {
		if (!(((UnaryExpression) exp).getParent() instanceof ForStatement)) {
		  if (allOperatorsSelected.contains("AODS")) {
			String desc = exp.toFlattenString() + " => " + mutation.toFlattenString();
			logReduction("ODL", "AODS", desc);
			return LogReduction.AVOID;
		  }
		}
	  }
	  // #Rule 1: ODL x AODU (Increment and Decrement)
	  if ((op == UnaryExpression.MINUS) || (op == UnaryExpression.PLUS)) {
		if (allOperatorsSelected.contains("AODU")) {
		  String desc = exp.toFlattenString() + " => " + mutation.toFlattenString();
		  logReduction("ODL", "AODU", desc);
		  return LogReduction.AVOID;
		}
	  }
	}
	// #Rule 2: ODL x SDL (Binary Operation with PLUS or MINUS)
	// Eg. return x + f(); => (ODL) return x; (SDL) Change the return of f()
	// to zero.
	// if (exp instanceof BinaryExpression) {
	// Expression left = ((BinaryExpression) exp).getLeft();
	// Expression right = ((BinaryExpression) exp).getRight();
	// int op = ((BinaryExpression) exp).getOperator();
	// if (op == BinaryExpression.PLUS || op == BinaryExpression.MINUS) {
	// if (left instanceof MethodCall) {
	// if (((MethodCall) left).getObjectID() == mutation.getObjectID()) {
	// if (allOperatorsSelected.contains("SDL")) {
	// String desc = exp.toFlattenString() + " => " +
	// mutation.toFlattenString();
	// logReduction("ODL", "SDL", desc);
	// return LogReduction.AVOID;
	// }
	// }
	// } else if (right instanceof MethodCall) {
	// if (((MethodCall) right).getObjectID() == mutation.getObjectID()) {
	// if (allOperatorsSelected.contains("SDL")) {
	// String desc = exp.toFlattenString() + " => " +
	// mutation.toFlattenString();
	// logReduction("ODL", "SDL", desc);
	// return LogReduction.AVOID;
	// }
	// }
	// }
	// }
	// }

	return false;
  }

  private boolean isEquivalent(Expression exp, Expression mutation) {
	// // #Rule 1: In a assignment, if the right side
	// // has a variable equal to the left side.
	// // It will be equivalent.
	// // Eg.: y = y + x; => y = y;
	// if (mutation instanceof Variable) {
	// if (exp instanceof BinaryExpression) {
	// BinaryExpression binExp = (BinaryExpression) exp;
	// if (binExp.getParent() instanceof AssignmentExpression) {
	// AssignmentExpression assignment = (AssignmentExpression)
	// binExp.getParent();
	// if (assignment.getLeft() instanceof Variable) {
	// Variable leftVar = (Variable) assignment.getLeft();
	// if (mutation.equals(leftVar))
	// return true;
	// }
	// }
	// } else if (exp instanceof UnaryExpression) {
	// UnaryExpression binExp = (UnaryExpression) exp;
	// if (binExp.getParent() instanceof AssignmentExpression) {
	// AssignmentExpression assignment = (AssignmentExpression)
	// binExp.getParent();
	// if (assignment.getLeft() instanceof Variable) {
	// Variable leftVar = (Variable) assignment.getLeft();
	// if (mutation.equals(leftVar))
	// return true;
	// }
	// }
	//
	// }
	//
	// }
	// // #Rule 2: In a assignment expression(+=, -=, *=), if the right side
	// // is equal to the left side.
	// // It will be equivalent.
	// // Eg.: y *= y; => y = y;
	// else if (mutation instanceof AssignmentExpression) {
	// AssignmentExpression assignment = (AssignmentExpression) mutation;
	// if (assignment.getLeft() instanceof Variable && assignment.getRight()
	// instanceof Variable) {
	// if (assignment.getLeft().equals(assignment.getRight()))
	// return true;
	// }
	// }

	return false;
  }
}
