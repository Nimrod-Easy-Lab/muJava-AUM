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

import mujava.op.util.ExpressionAnalyzer;
import mujava.op.util.LogReduction;
import mujava.util.Debug;
import mujava.util.drule.DRuleUtils;
import openjava.mop.FileEnvironment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * <p>
 * Generate ROR (Rational Operator Replacement) mutants -- replace each
 * occurrence of one of the relational operators (<, <=, >, >=, =, <>) by each
 * of the other operators and by <i>falseOp</i> and <i>trueOp</i> where
 * <i>falseOp</i> always returns <i>false</i> and <i>trueOp</i> always returns
 * <i>true</i>
 * </p>
 *
 * @author Yu-Seung Ma
 * @version 1.0
 */

public class ROR extends Arithmetic_OP {

  private boolean d_ror66_flag = false;

  public enum RORMutations {
	CONDITIONAL_EXPRESSION_AFFIRMATION, CONDITIONAL_EXPRESSION_NEGATION;
  }

  private List<String> allOperatorsSelected;

  public ROR(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit) {
	super(file_env, comp_unit);
	allOperatorsSelected = DRuleUtils.access().getAllOperatorsSelected();
  }

  public ROR(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit, List<String> allOperators) {
	super(file_env, comp_unit);
	allOperatorsSelected = allOperators;
  }

  public void visit(BinaryExpression p) throws ParseTreeException {
	Expression left = p.getLeft();
	left.accept(this);
	Expression right = p.getRight();
	right.accept(this);

	int op_type = p.getOperator();

	if (isArithmeticType(p.getLeft()) && isArithmeticType(p.getRight())) {
	  // fix the fault that missed <, Lin, 050814
	  if ((op_type == BinaryExpression.GREATER) || (op_type == BinaryExpression.GREATEREQUAL)
		  || (op_type == BinaryExpression.LESSEQUAL) || (op_type == BinaryExpression.EQUAL)
		  || (op_type == BinaryExpression.NOTEQUAL) || (op_type == BinaryExpression.LESS)) {
		primitiveRORMutantGen(p, op_type);
	  }
	} else if ((op_type == BinaryExpression.EQUAL) || (op_type == BinaryExpression.NOTEQUAL)) {
	  objectRORMutantGen(p, op_type);
	}
  }


  private void primitiveRORMutantGen(BinaryExpression exp, int op) {

	BinaryExpression mutant;

	/**
	 * the traditional ROR implementation
	 */

	if (op != BinaryExpression.GREATER) {
	  mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	  mutant.setOperator(BinaryExpression.GREATER);
	  int op2 = BinaryExpression.GREATER;
	  if (!isDuplicated(exp, mutant) && !isEquivalent(exp, op, op2)) {
		outputToFile(exp, mutant);
	  }
	}

	if (op != BinaryExpression.GREATEREQUAL) {
	  mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	  mutant.setOperator(BinaryExpression.GREATEREQUAL);
	  int op2 = BinaryExpression.GREATEREQUAL;
	  if (!isDuplicated(exp, mutant) && !isEquivalent(exp, op, op2)) {
		outputToFile(exp, mutant);
	  }
	}

	if (op != BinaryExpression.LESS) {
	  mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	  mutant.setOperator(BinaryExpression.LESS);
	  int op2 = BinaryExpression.LESS;
	  if (!isDuplicated(exp, mutant) && !isEquivalent(exp, op, op2)) {
		outputToFile(exp, mutant);
	  }
	}

	if (op != BinaryExpression.LESSEQUAL) {
	  mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	  mutant.setOperator(BinaryExpression.LESSEQUAL);
	  int op2 = BinaryExpression.LESSEQUAL;
	  if (!isDuplicated(exp, mutant) && !isEquivalent(exp, op, op2)) {
		outputToFile(exp, mutant);
	  }
	}

	if (op != BinaryExpression.EQUAL) {
	  mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	  mutant.setOperator(BinaryExpression.EQUAL);
	  int op2 = BinaryExpression.EQUAL;
	  if (!isDuplicated(exp, mutant) && !isEquivalent(exp, op, op2)) {
		outputToFile(exp, mutant);
	  }
	}

	if (op != BinaryExpression.NOTEQUAL) {
	  mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	  mutant.setOperator(BinaryExpression.NOTEQUAL);
	  int op2 = BinaryExpression.NOTEQUAL;
	  if (!isDuplicated(exp, mutant) && !isEquivalent(exp, op, op2)) {
		outputToFile(exp, mutant);
	  }
	}

	// Complete the full implementation of ROR
	// Note here the mutant is a type of Literal not a binary expression
	// Updated by Nan Li
	// Dec 6 2011
	boolean drule_70 = isDuplicated_70(exp, Literal.constantTrue());
	boolean drule_70_2 = isDuplicated_70(exp, Literal.constantFalse());
	if (!isDuplicated(exp, Literal.makeLiteral(true)) && !isDuplicated_49(exp, Literal.makeLiteral(true))
		&& !drule_70) {
	  // Change the expression to true
	  outputToFile(exp, Literal.makeLiteral(true));
	}
	if (!isDuplicated(exp, Literal.makeLiteral(false)) && !isDuplicated_49(exp, Literal.makeLiteral(false))
		&& !drule_70_2) {
	  // Change the expression to false
	  outputToFile(exp, Literal.makeLiteral(false));
	}

	/**
	 * New implementation of ROR based on the fault hierarchies fewer ROR
	 * mutants are generated For details, see the paper "Better predicate
	 * testing" by Kaminski, Ammann, and Offutt at AST'11 This part is
	 * currently experimental, which means, users will not see this part
	 * during the new release
	 */

	// // mutant >=
	// if (op == BinaryExpression.GREATER) {
	// mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	// mutant.setOperator(BinaryExpression.GREATEREQUAL);
	// outputToFile(exp, mutant);
	//
	// mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	// mutant.setOperator(BinaryExpression.NOTEQUAL);
	// outputToFile(exp, mutant);
	// if (!isDuplicated(exp, Literal.makeLiteral(false))) {
	// outputToFile(exp, Literal.makeLiteral(false));
	// }
	// }
	//
	// if (op == BinaryExpression.GREATEREQUAL) { // mutant true
	// if (!isDuplicated(exp, Literal.makeLiteral(true))) {
	// outputToFile(exp, Literal.makeLiteral(true));
	// }
	//
	// mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	// mutant.setOperator(BinaryExpression.GREATER);
	// outputToFile(exp, mutant);
	//
	// mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	// mutant.setOperator(BinaryExpression.EQUAL);
	// outputToFile(exp, mutant);
	//
	// }
	//
	// if (op == BinaryExpression.LESS) { // mutant false
	// if (!isDuplicated(exp, Literal.makeLiteral(false))) {
	// outputToFile(exp, Literal.makeLiteral(false));
	// }
	//
	// mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	// mutant.setOperator(BinaryExpression.LESSEQUAL);
	// outputToFile(exp, mutant);
	//
	// mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	// mutant.setOperator(BinaryExpression.NOTEQUAL);
	// outputToFile(exp, mutant);
	//
	// }
	//
	// if (op == BinaryExpression.LESSEQUAL) {
	// if (!isDuplicated(exp, Literal.makeLiteral(true))) {
	// outputToFile(exp, Literal.makeLiteral(true));
	// }
	//
	// mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	// mutant.setOperator(BinaryExpression.LESS);
	// outputToFile(exp, mutant);
	//
	// mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	// mutant.setOperator(BinaryExpression.EQUAL);
	// outputToFile(exp, mutant);
	// }
	//
	// if (op == BinaryExpression.EQUAL) {
	// if (!isDuplicated(exp, Literal.makeLiteral(false))) {
	// outputToFile(exp, Literal.makeLiteral(false));
	// }
	//
	// mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	// mutant.setOperator(BinaryExpression.LESSEQUAL);
	// outputToFile(exp, mutant);
	//
	// mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	// mutant.setOperator(BinaryExpression.GREATEREQUAL);
	// outputToFile(exp, mutant);
	// }
	//
	// if (op == BinaryExpression.NOTEQUAL) {
	// if (!isDuplicated(exp, Literal.makeLiteral(true))) {
	// outputToFile(exp, Literal.makeLiteral(true));
	// }
	//
	// mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	// mutant.setOperator(BinaryExpression.LESS);
	// outputToFile(exp, mutant);
	//
	// mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	// mutant.setOperator(BinaryExpression.GREATER);
	// outputToFile(exp, mutant);
	// }
  }

  private void objectRORMutantGen(BinaryExpression exp, int op) {
	BinaryExpression mutant;
	if (op != BinaryExpression.EQUAL) {
	  mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	  mutant.setOperator(BinaryExpression.EQUAL);
	  outputToFile(exp, mutant);
	}

	if (op != BinaryExpression.NOTEQUAL) {
	  mutant = (BinaryExpression) (exp.makeRecursiveCopy());
	  mutant.setOperator(BinaryExpression.NOTEQUAL);
	  outputToFile(exp, mutant);
	}
  }

  /**
   * Output ROR mutants to files
   *
   * @param original
   * @param mutant
   */
  public void outputToFile(BinaryExpression original, BinaryExpression mutant) {
	if (comp_unit == null)
	  return;

	String f_name;
	num++;
	f_name = getSourceName("ROR");
	String mutant_dir = getMuantID("ROR");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  ROR_Writer writer = new ROR_Writer(mutant_dir, out);
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
   * Output ROR mutants (true or false) to files
   *
   * @param original
   * @param mutant
   */
  public void outputToFile(BinaryExpression original, Literal mutant) {
	if (comp_unit == null)
	  return;

	String f_name;
	num++;
	f_name = getSourceName("ROR");
	String mutant_dir = getMuantID("ROR");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  ROR_Writer writer = new ROR_Writer(mutant_dir, out);
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
   * LOI_ROR 49 DRule implementation
   * "term = if(v.length op1 0){ ... }
   * transformations = {
   * LOI(v.length) = ~v.length,
   * ROR(op1) = op2
   * }
   * constraints = {
   * op1 ∈ {<, <=, ==} and op2 ∈ {false} or
   * op1 ∈ {>, >=, !=} and op2 ∈ {true},
   * The type of v is String or Array
   * }"
   *
   * @param original Original binary expression.
   * @param mutant   Mutated original boolean literal constant.
   * @author Pedro Pinheiro
   */
  private boolean isDuplicated_49(BinaryExpression original, Literal mutant) {
	boolean d_loi_ror_49 = false;
	int op1 = original.getOperator();

	boolean constraint1 = ((op1 == BinaryExpression.LESS) || (op1 == BinaryExpression.LESSEQUAL)
		|| (op1 == BinaryExpression.EQUAL)) && mutant.equals(Literal.makeLiteral(false));
	boolean constraint2 = ((op1 == BinaryExpression.GREATER) || (op1 == BinaryExpression.GREATEREQUAL)
		|| (op1 == BinaryExpression.NOTEQUAL)) && mutant.equals(Literal.makeLiteral(true));

	ExpressionAnalyzer aexp = new ExpressionAnalyzer(original, this.getEnvironment());
	//TODO: implement this feature in ExpressionAnalyzer
	boolean hasFieldAccess = (original.getLeft() instanceof FieldAccess)
		|| (original.getRight() instanceof FieldAccess);
	boolean hasZeroLiteral = aexp.containsZeroLiteral();

	if ((constraint1 || constraint2) && hasFieldAccess && hasZeroLiteral && DRuleUtils.access().isOperatorSelected("LOI")) {
	  d_loi_ror_49 = LogReduction.AVOID;
	  logReduction("LOI", "ROR", "DRULE49 Triggered => "
		  + original.toFlattenString());
	  System.out.println("Triggered DRule LOI_ROR 49 => " + original.toFlattenString());
	}
	return d_loi_ror_49;
  }

  /**
   * Avoid duplicated mutants given following conditions
   * "term = if(vArgs.length == 0){...}
   * transformations = {
   * ROR(==) = !=,
   * ROR(==) = >
   * }
   * constraints = {
   * the type of v is String or Array
   * }"
   *
   * @param original
   * @param mutant
   * @return
   * @author Pedro Pinheiro
   */
  private boolean isDuplicated_66(BinaryExpression original, BinaryExpression mutant) {
	boolean d_ror_ror66 = false;
	ParseTreeObject pto = mutant;
	for (; pto != null && !(pto instanceof IfStatement || pto instanceof MethodDeclaration); pto = pto.getParent()) {
	}
	if (pto instanceof IfStatement) {
	  ExpressionAnalyzer expressionAnalyzer = new ExpressionAnalyzer(original, this.getEnvironment());
	  if (expressionAnalyzer.containsZeroLiteral() && expressionAnalyzer.containsLengthMethodCall()
		  && (expressionAnalyzer.containsString() || expressionAnalyzer.containsArray()))
		switch (mutant.getOperator()) {
		  case BinaryExpression.NOTEQUAL://--let this one happen case BinaryExpression.GREATEREQUAL:
			d_ror_ror66 = LogReduction.AVOID;
			logReduction("ROR", "ROR", original.toFlattenString()
				+ " => " + mutant.toFlattenString());
			System.out.println("Triggered DRule 66 ROR_ROR" + original.toFlattenString()
				+ " => " + mutant.toFlattenString());
		}
	}
	return d_ror_ror66;
  }

  private boolean isDuplicated_70(BinaryExpression original, Expression mutant) {
	boolean d_ror_sdl70 = false;
	ParseTreeObject pto = original;
	for (; pto != null && !(pto instanceof ReturnStatement || pto instanceof MethodDeclaration); pto = pto.getParent()) {
	}
	if (pto instanceof ReturnStatement && DRuleUtils.access().isOperatorSelected("SDL")) {
	  switch (original.getOperator()) {
		case BinaryExpression.EQUAL:
		case BinaryExpression.NOTEQUAL:
		case BinaryExpression.LESS:
		case BinaryExpression.GREATER:
		case BinaryExpression.LESSEQUAL:
		case BinaryExpression.GREATEREQUAL:
		  if (mutant instanceof Literal &&
			  (((Literal) mutant).equals(Literal.constantTrue())
				  || (((Literal) mutant).equals(Literal.constantFalse())))) {
		    d_ror_sdl70 = LogReduction.AVOID;
		    logReduction("ROR", "SDL", "Triggered d_ror_sdl70:" +
				pto.toFlattenString() + "=>" + mutant.toFlattenString());
		    System.out.println("Triggered DRule ror_sdl70: " + original + " => " + mutant);
		  }
	  }
	}
	return d_ror_sdl70;
  }

  /**
   * Avoid generate duplicated mutants
   *
   * @param exp
   * @param mutant
   * @return
   * @throws ParseTreeException
   */
  private boolean isDuplicated(BinaryExpression exp, Expression mutant) {
	if (mutant instanceof Literal) {
	  // #Rule 1: SDL x ROR(1) (delete or negate a conditional)
	  // Eg.: if(x>10) => [SDL] Delete x [ROR] if(false)
	  if (mutant.equals(Literal.makeLiteral(false))) {
		if (exp.getParent() instanceof IfStatement) {
		  IfStatement ifStmt = (IfStatement) exp.getParent();
		  // Check weather the IF has ELSE clause
		  StatementList elseStmtList = ifStmt.getElseStatements();
		  if (elseStmtList != null && elseStmtList.size() > 0) {
			return false;
		  }
		  if (allOperatorsSelected.contains("SDL")) {
			String desc = exp.toFlattenString() + " => " + mutant.toFlattenString();
			logReduction("ROR", "SDL", desc);
			return LogReduction.AVOID;
		  }
		}
		// #Rule 2: SDL x ROR(2) (Affirm the CONDITIONAL EXPRESSION
		// expression)
		// Eg.: if(x>10) => [SDL] if(true) x [ROR] if(true)
	  } else if (mutant.equals(Literal.makeLiteral(true))) {
		if (exp.getParent() instanceof IfStatement || exp.getParent() instanceof WhileStatement) {
		  if (allOperatorsSelected.contains("SDL")) {
			String desc = exp.toFlattenString() + " => " + mutant.toFlattenString();
			logReduction("ROR", "SDL", desc);
			return LogReduction.AVOID;
		  }
		}
	  }
	} else if (mutant instanceof BinaryExpression) {
	  return isDuplicated_66(exp, (BinaryExpression) mutant);
	}
	return false;
  }

  private boolean isEquivalent(BinaryExpression exp, int op1, int op2) {
	boolean e_rule_13 = false;
	boolean e_rule_20 = false;
	boolean e_rule_23 = false;

		/*
			ROR E-Rule 13
			term = if (vArgs.length op1 0)
			vArgs is string or array
			ROR(op1) -> op2
			when op1 is != and op2 is <
			when op1 is != and op2 is >
			when op1 is > and op2 is !=
			when op1 is < and op2 is !=
			when op1 is == and op2 is <=

			>>>>>
		*/

	ExpressionAnalyzer aexp = new ExpressionAnalyzer(exp, this.getEnvironment());
	if (aexp.isInsideIf()) {
	  if (aexp.containsZeroLiteral() && (aexp.containsLengthMethodCall() &&
		  (aexp.containsString() || aexp.containsArray()))) {
		switch (aexp.getRootOperator()) {
		  case DIFFERENT:
			if (op2 == BinaryExpression.LESS || (op2 == BinaryExpression.GREATER)) {
			  e_rule_13 = LogReduction.AVOID;
			  System.out.println("E-Rule 13 >>>> " + exp.toString() + " op2: " + ExpressionAnalyzer.translateFromBinaryExpression(op2));
			}
			break;
		  case GREATER:
			if (op2 == BinaryExpression.NOTEQUAL) {
			  e_rule_13 = LogReduction.AVOID;
			  System.out.println("E-Rule 13 >>>> " + exp.toString() + " op2: " + ExpressionAnalyzer.translateFromBinaryExpression(op2));
			}
			break;
		  case LESSER:
			if (op2 == BinaryExpression.NOTEQUAL) {
			  e_rule_13 = LogReduction.AVOID;
			  System.out.println("E-Rule 13 >>>> " + exp.toString() + " op2: " + ExpressionAnalyzer.translateFromBinaryExpression(op2));
			}
			break;
		  case EQUALS:
			if (op2 == BinaryExpression.LESSEQUAL) {
			  e_rule_13 = LogReduction.AVOID;
			  System.out.println("E-Rule 13 >>>> " + exp.toString() + " op2: " + ExpressionAnalyzer.translateFromBinaryExpression(op2));
			}
			break;
		  default:
			break;
		}
	  }

	  /*    ERULE 20
	   *   "term = if (v1 op1 v2) { v1 := v2 };
	   *   transformations = {
	   *     ROR(op1) = op2
	   *   }
	   *   constraints = {
	   *      v1 and v2 hold a primitive data type,
	   *      op1 ∈ {<} and op2 ∈ {<=} or op1 ∈ {>} and op2 ∈ {>=},
	   *   }"
	   */
	  //TODO: test for activation
	  else if (aexp.getRight() instanceof Variable && (aexp.getLeft() instanceof Variable)) {
		IfStatement parent = (IfStatement) exp.getParent();
		StatementList statementList = parent.getStatements();
		for (int index = 0; index < statementList.size(); ++index) {
		  Statement statement = statementList.get(index);
		  if (statement instanceof ExpressionStatement) {
			ExpressionStatement es = (ExpressionStatement) statement;
			if (es.getExpression() instanceof AssignmentExpression) {
			  AssignmentExpression ase = (AssignmentExpression) es.getExpression();
			  if ((ase.getRight() instanceof Variable) && (ase.getLeft() instanceof Variable)) {
				if (ase.getRight().equals(aexp.getRight()) && ase.getLeft().equals(aexp.getLeft())) {
				  ExpressionAnalyzer.BinaryOperator top2 = ExpressionAnalyzer.translateFromBinaryExpression(op2);
				  if ((aexp.getRootOperator() == ExpressionAnalyzer.BinaryOperator.LESSER &&
					  top2 == ExpressionAnalyzer.BinaryOperator.LESSEREQUAL) ||
					  (aexp.getRootOperator() == ExpressionAnalyzer.BinaryOperator.GREATER) &&
						  top2 == ExpressionAnalyzer.BinaryOperator.GREATEREQUAL) {
					//ACTIVATE RULE
					System.out.println("[TOUCHDOWN] ROR ERULE 20 >>>>>> " + exp.toFlattenString());
					e_rule_20 = LogReduction.AVOID;
				  }
				}
			  }
			}
		  }
		}
	  }
			/* ROR E-Rule 23
                "term = if (v op1 value) { ... };
                transformations = {
                  ROR(op1) = op2
                }
                constraints = {
                   value == Integer.MAX_VALUE and op1 ∈ {==} and op2 ∈ {>=} or
                   value == Integer.MIN_VALUE and op1 ∈ {==} and op2 ∈ {<=}
                }"
			 */
	  else if (((aexp.getRight() instanceof Variable) && (aexp.getLeft() instanceof FieldAccess)) ||
		  ((aexp.getRight() instanceof FieldAccess) && (aexp.getLeft() instanceof Variable))) {
		Variable variable = null;
		FieldAccess fieldAccess = null;
		if (aexp.getLeft() instanceof Variable) {
		  variable = (Variable) aexp.getLeft();
		} else if (aexp.getLeft() instanceof FieldAccess) {
		  fieldAccess = (FieldAccess) aexp.getLeft();
		}
		if (aexp.getRight() instanceof Variable) {
		  variable = (Variable) aexp.getRight();
		} else if (aexp.getRight() instanceof FieldAccess) {
		  fieldAccess = (FieldAccess) aexp.getRight();
		}

		if ((variable != null) && (fieldAccess != null)) {
		  if (fieldAccess.getReferenceType() != null) {
			if (fieldAccess.getReferenceType().getName().equals("Integer")) {
			  if (fieldAccess.getName().equals("MAX_VALUE")) {
				if (((op1 == BinaryExpression.EQUAL) && (op2 == BinaryExpression.GREATEREQUAL)) ||
					((op1 == BinaryExpression.EQUAL) && (op2 == BinaryExpression.LESSEQUAL))) {
				  e_rule_23 = LogReduction.AVOID;
				  System.out.println("ROR E23 >>>>> " + exp.toFlattenString());
				}
			  }
			}
		  }
		}

	  }

	} else if (exp.getParent() instanceof ForStatement) {
	  ForStatement forStatement = (ForStatement) exp.getParent();
	  return isEquivalent(forStatement, op2);
	}


	return e_rule_13 || e_rule_20 || e_rule_23;
  }

  private boolean isEquivalent(ForStatement forStatement, int op2) {
	boolean e_rule_17 = false;
        /*
            ROR E-Rule 17
            term = for (int v1 := 0; v1 op1 vArray.length; v1++){ ... }
            transformations = {
              ROR(op1) = op2
            }
            constraints = {
               op1 ∈ {<} and op2 ∈ {!=} or op1 ∈ {!=} and op2 ∈ {<},
               There is no definition of v1 within the for body
            }
         */
	//ForStatement seems to not store information about declared variables in initiation expression
	//So we'll have to trust that the variable being incremented is the one we're looking for
	Object[] forStatementContents = forStatement.getContents();
	try {
	  //Check if initializer is int
	  TypeName typeName = (TypeName) forStatementContents[0];
	  boolean hasForStatementSpecs = false;
	  if (typeName.getName() == "int") {
		// Here, we look into for increment expression list for an unary increment
		// on the variable we're interested into
		ExpressionList expressionList = (ExpressionList) forStatementContents[4];
		// Unary increment expression
		UnaryExpression incremeterExpression = null;
		for (int i = 0; i < expressionList.size(); i++) {
		  if (expressionList.get(i) instanceof UnaryExpression) {
			incremeterExpression = (UnaryExpression) expressionList.get(i);
		  }
		}
		if (incremeterExpression != null) {
//					Variable unaryIncrementVariable =
		  if (incremeterExpression.getContents()[0] instanceof Variable) {
			Variable unaryIncrementVariable = (Variable) incremeterExpression.getContents()[0];
			BinaryExpression forConditionExpression = (BinaryExpression) forStatementContents[3];
			Variable binaryExpressionVariable = (Variable) forConditionExpression.getContents()[0];
			boolean hasFieldAccess = false;
			boolean hasSizeMethodCall = false;
			if (forConditionExpression.getLeft() instanceof FieldAccess) {
			  FieldAccess fa = (FieldAccess) forConditionExpression.getLeft();
			  if (fa.getName().equals("length") || fa.getName().equals("size")) {
				hasFieldAccess = true;
			  }
			}
			if (forConditionExpression.getRight() instanceof FieldAccess) {
			  FieldAccess fa = (FieldAccess) forConditionExpression.getRight();
			  if (fa.getName().equals("length") || fa.getName().equals("size")) {
				hasFieldAccess = true;
			  }
			}
			if (forConditionExpression.getLeft() instanceof MethodCall) {
			  MethodCall mc = (MethodCall) forConditionExpression.getRight();
			  if (mc.getName().equals("length") || mc.getName().equals("size")) {
				hasSizeMethodCall = true;
			  }
			}
			if (forConditionExpression.getLeft() instanceof MethodCall) {
			  MethodCall mc = (MethodCall) forConditionExpression.getLeft();
			  if (mc.getName().equals("length") || mc.getName().equals("size")) {
				hasSizeMethodCall = true;
			  }
			}

			if (unaryIncrementVariable.equals(binaryExpressionVariable)
				&& (hasFieldAccess || hasSizeMethodCall)) {
			  hasForStatementSpecs = true;
			}
		  }
		}
	  }
	  // If has e-rule specifications, we'll continue checking inside for loop for v1 use
	  if (hasForStatementSpecs) {
//				ExpressionList forStatementBlock = forStatement.
		//Here, we have to look into whatever changes v1
		//Possible solution is to not trigger the rule whenever v1 is used
		StatementList statementList = forStatement.getStatements();
	  }
	} catch (ClassCastException e) {
	}
	Expression condition = forStatement.getCondition();
	return e_rule_17;
  }
}
