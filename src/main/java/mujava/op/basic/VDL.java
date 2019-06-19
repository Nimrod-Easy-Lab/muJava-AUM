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
 * Generate VDL (Variable DeLetion) mutants
 * </p>
 *
 * @author Lin Deng
 * @version 1.0
 */

public class VDL extends Arithmetic_OP {
  private List<String> allOperatorsSelected;

  public VDL(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit) {
	super(file_env, comp_unit);
  }

  // Leo: Criei este construtora para aceitar mais um parametro com todos
  // operadores selecionados
  public VDL(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit, List<String> allOperators) {
	super(file_env, comp_unit);
	this.allOperatorsSelected = allOperators;
  }

  public void visit(BinaryExpression p) throws ParseTreeException {
	Expression left = p.getLeft();
	left.accept(this);

	Expression right = p.getRight();
	right.accept(this);

	aorMutantGen(p);
  }

  public void visit(UnaryExpression p) throws ParseTreeException {
	// Expression mutant = p.getExpression();
	// aor_outputToFile(p, mutant);
	Expression expression = p.getExpression();
	expression.accept(this);
	aorMutantGen(p);
  }

  // public void visit(AssignmentExpression p) throws ParseTreeException
  // {
  // Expression left = p.getLeft();
  // left.accept(this);
  // Expression right = p.getRight();
  // right.accept(this);
  // aorMutantGen(p);
  // }
  //
  //
  //
  // private void aorMutantGen(AssignmentExpression exp) {
  // if(exp.getOperator()!=0)
  // {
  // AssignmentExpression mutant = new AssignmentExpression(exp.getLeft(),
  // exp.getOperator(), exp.getRight());
  // mutant.setOperator(0);
  // aor_outputToFile(exp, mutant);
  // }
  //
  //
  // }
  //
  // private void aor_outputToFile(AssignmentExpression original,
  // AssignmentExpression mutant) {
  // if (comp_unit == null)
  // return;
  //
  // String f_name;
  // num++;
  // f_name = getSourceName("VDL");
  // String mutant_dir = getMuantID("VDL");
  //
  // try
  // {
  // PrintWriter out = getPrintWriter(f_name);
  // ODL_Writer writer = new ODL_Writer(mutant_dir, out);
  // writer.setMutant(original, mutant);
  // writer.setMethodSignature(currentMethodSignature);
  // comp_unit.accept( writer );
  // out.flush(); out.close();
  // } catch ( IOException e )
  // {
  // System.err.println( "fails to create " + f_name );
  // } catch ( ParseTreeException e )
  // {
  // System.err.println( "errors during printing " + f_name );
  // e.printStackTrace();
  // }
  //
  // }

  private void aorMutantGen(UnaryExpression exp) {
	Expression mutant = exp.getExpression();
	Variable mutant2 = new Variable(" ");
	// System.out.println(exp+" => "+mutant2);
	// if it is var or an array access
	if (mutant instanceof Variable || mutant instanceof ArrayAccess) {
	  // System.out.println("u "+exp);
	  if (!isDuplicated(exp, mutant2) && !isEquivalent(exp, mutant2)) {
		aor_outputToFile(exp, mutant2);
	  }
	}
  }

  private void aorMutantGen(BinaryExpression exp) {
	Expression mutantLeft = exp.getLeft();
	Expression mutantRight = exp.getRight();
	// if left is variable or an array access
	// System.out.println("b"+exp);
	if (mutantLeft instanceof Variable || mutantLeft instanceof ArrayAccess) {
	  if (!isDuplicated(exp, mutantRight) && !isEquivalent(exp, mutantRight)) {
		// delete it, only keep right
		aor_outputToFile(exp, mutantRight);
	  }
	}
	// if right is variable or an array access
	// System.out.println("b"+exp);
	if (mutantRight instanceof Variable || mutantRight instanceof ArrayAccess) {
	  if (!isDuplicated(exp, mutantLeft) && !isEquivalent(exp, mutantLeft)) {
		// delete it, only keep left
		aor_outputToFile(exp, mutantLeft);
	  }

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
	f_name = getSourceName("VDL");
	String mutant_dir = getMuantID("VDL");

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
	f_name = getSourceName("VDL");
	String mutant_dir = getMuantID("VDL");

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
	f_name = getSourceName("VDL");
	String mutant_dir = getMuantID("VDL");

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
	// #Rule 1: VDL x ODL (binary or unary expression with a variable
	// involved)
	// Eg.: y = x + 10; => [VDL] y = x; [ODL] y = x;
	if (exp instanceof UnaryExpression) {
	  UnaryExpression ue = (UnaryExpression) exp;
	  if (ue.getOperator() != UnaryExpression.POST_DECREMENT && ue.getOperator() != UnaryExpression.POST_INCREMENT
		  && ue.getOperator() != UnaryExpression.PRE_DECREMENT && ue.getOperator() != UnaryExpression.PRE_INCREMENT) {
		if (allOperatorsSelected.contains("ODL")) {
		  String desc = exp.toFlattenString() + " => " + mutation.toFlattenString();
		  logReduction("VDL", "ODL", desc);
		  return LogReduction.AVOID;
		}
	  }
	}
	if (exp instanceof BinaryExpression) {
	  if (allOperatorsSelected.contains("ODL")) {
		String desc = exp.toFlattenString() + " => " + mutation.toFlattenString();
		logReduction("VDL", "ODL", desc);
		return LogReduction.AVOID;
	  }
	}
	return false;
  }

  private boolean isEquivalent(Expression exp, Expression mutation) {
	// #Rule 1: In a assignment expression, if the right side
	// (left or right) is the same variable variable of the assignment
	// It will be equivalent.
	// Eg.: y = y + x; => y = y;
	// if (mutation instanceof Variable) {
	// Variable rightVar = (Variable) mutation;
	// if (exp instanceof BinaryExpression) {
	// BinaryExpression binExp = (BinaryExpression) exp;
	// if (binExp.getParent() instanceof AssignmentExpression) {
	// AssignmentExpression assignment = (AssignmentExpression)
	// binExp.getParent();
	// if (assignment.getLeft() instanceof Variable) {
	// Variable leftVar = (Variable) assignment.getLeft();
	// if(rightVar.equals(leftVar))
	// return true;
	// }
	// }
	// }
	//
	// }

	return false;
  }
}
