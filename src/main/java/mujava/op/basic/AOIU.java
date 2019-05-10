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
import openjava.mop.FileEnvironment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * <p>
 * Generate AOIU (Arithmetic Operator Insertion (Unary)) mutants -- insert a
 * unary operator (arithmetic -) before each variable or expression
 * </p>
 *
 * @author Yu-Seung Ma
 * @version 1.0
 * <p>
 * Took out aor_flag for not clear about the reason of using it.
 * Lin Deng, Aug 23
 * <p>
 * Added code to generate mutants for logical expressions.
 * E.g., a < b  =>  -a < b
 * Lin Deng, Aug 28
 */

public class AOIU extends Arithmetic_OP {
  // boolean aor_flag = false;

  private java.util.List<String> allOperatorsSelected;

  public AOIU(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit) {
	super(file_env, comp_unit);
	allOperatorsSelected = new java.util.ArrayList<>();
  }

  public AOIU(FileEnvironment file_env, ClassDeclaration cdecl
	  , CompilationUnit comp_unit, java.util.List<String> allOperatorsSelected) {
	this(file_env, cdecl, comp_unit);
	this.allOperatorsSelected = allOperatorsSelected;
  }

  /**
   * Set an AOR flag
   *
   * @param p
   */
  // public void setAORflag(boolean b)
  // {
  // aor_flag = b;
  // }
  public void visit(UnaryExpression p) throws ParseTreeException {
	// NO OPERATION
  }

  /**
   * Generate AOIU mutant
   */
  public void visit(Variable p) throws ParseTreeException {
	if (isArithmeticType(p)) {
	  outputToFile(p);
	}
  }

  /**
   * Generate AOIU mutant
   */
  public void visit(FieldAccess p) throws ParseTreeException {
	if (isArithmeticType(p)) {
	  outputToFile(p);
	}
  }

  /**
   * Generate AOIU mutant
   */
  public void visit(BinaryExpression p) throws ParseTreeException {
	// if (aor_flag && isArithmeticType(p))
	// not clear about the reason for using the flag.
	// take it out.

	// NOT SURE WHY IT IS SET TO ONLY ACCEPT ARITHMETIC TYPE,
	// HOW ABOUT a < b ?
	// Lin takes it out on 08/28
	// if (isArithmeticType(p))
	// {
	if ((p.getOperator() == BinaryExpression.MINUS) || (p.getOperator() == BinaryExpression.PLUS)
		|| (p.getOperator() == BinaryExpression.MOD)) {
	  Expression e1 = p.getLeft();
	  super.visit(e1);
	  // Ignore right expression because it produce equivalent mutants;
	  // Expression e2 = p.getRight();
	  //
	  // WHY??? (LIN 08/28)
	} else if ((p.getOperator() == BinaryExpression.DIVIDE) || (p.getOperator() == BinaryExpression.TIMES)) {
	  Expression e1 = p.getLeft();
	  Expression e2 = p.getRight();
	  if (((e1 instanceof Variable) || (e1 instanceof FieldAccess))
		  && ((e2 instanceof Variable) || (e2 instanceof FieldAccess))) {
		// Consider only one expression because it produces equivalent
		// mutants;
		//
		// WHY??? (LIN 08/28)
		super.visit(e1);
	  } else {
		super.visit(p);
	  }
	}
	// 08/28
	// Lin added to generate mutants for logical expressions
	// e.g.
	// a < b  => -a < b
	else if (((p.getOperator() == BinaryExpression.GREATER) || (p.getOperator() == BinaryExpression.GREATEREQUAL)
		|| (p.getOperator() == BinaryExpression.LESSEQUAL) || (p.getOperator() == BinaryExpression.EQUAL)
		|| (p.getOperator() == BinaryExpression.NOTEQUAL) || (p.getOperator() == BinaryExpression.LESS))
		&& !isEquivalent(p)) {
	  Expression e1 = p.getLeft();
	  Expression e2 = p.getRight();
	  super.visit(e1);
	  super.visit(e2);
	}
  }

  /**
   * Generate AOIU mutant
   */
  public void visit(AssignmentExpression p) throws ParseTreeException {
	// [ Example ]
	// int a=0;int b=2;int c=4;
	// Right Expression : a = b = -c;
	// Wrong Expression : a = -b = c;
	// Ignore left expression
	if (isEquivalent(p)) return;
	Expression rexp = p.getRight();
	rexp.accept(this);
  }

  /***
   * Write AOIU mutants to files
   *
   * @param original_field
   */
  public void outputToFile(FieldAccess original_field) {
	if (comp_unit == null)
	  return;

	String f_name;
	num++;
	f_name = getSourceName("AOIU");
	String mutant_dir = getMuantID("AOIU");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  AOIU_Writer writer = new AOIU_Writer(mutant_dir, out);
	  writer.setMutant(original_field);
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
   * Write AOIU mutants to files
   *
   * @param original_var
   */
  public void outputToFile(Variable original_var) {
	if (comp_unit == null)
	  return;

	String f_name;
	num++;
	f_name = getSourceName("AOIU");
	String mutant_dir = getMuantID("AOIU");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  AOIU_Writer writer = new AOIU_Writer(mutant_dir, out);
	  writer.setMutant(original_var);
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

  boolean isEquivalent(AssignmentExpression exp) {
	boolean aoiu12 = false;
        /*
		AOIU 12
        "term = v1 %= v2;
        transformations = {
          AOIU(v2) = -v2
        }
        constraints = {

        }"
		*/
	if (exp.getOperator() == AssignmentExpression.MOD) {
	  aoiu12 = LogReduction.AVOID;
	  System.out.println("[TOUCHDOWN] ERULE AOIU12 >>>>> " + exp.toFlattenString());
	}
	return aoiu12;
  }

  boolean isEquivalent(BinaryExpression exp) {
	boolean aoiu15 = false;
	boolean aoiu12 = false;
		/*
		AOIU 15
		"term = if(exp op 0){...}
			transformations = {
			  AOIU(exp) = -exp
			}
			constraints = {
			  op ∈ {==, !=}
			}"
		 */
	ExpressionAnalyzer aexp = new ExpressionAnalyzer(exp, this.getEnvironment());
	if (aexp.containsZeroLiteral() && aexp.isInsideIf()) {
	  aoiu15 = LogReduction.AVOID;
	  switch (aexp.getRootOperator()) {
		case EQUALS:
		case DIFFERENT:
		  System.out.println("AOIU E15 >>>> " + exp.toFlattenString());
		  break;
		default:
		  aoiu15 = false;
		  break;
	  }
	}
        /*
		AOIU 12
        "term = v1 %= v2;
        transformations = {
          AOIU(v2) = -v2
        }
        constraints = {

        }"
		*/
	if (aexp.getRootOperator().equals(ExpressionAnalyzer.BinaryOperator.MOD)) {

	}
	return aoiu15;
  }

  /**
   * DRrule AOIU_AOIU57
   * Avoid duplicated mutants that matches the following conditions:
   * term = type v := exp; ... return v;
   * transformations = {
   *   AOIU(exp) = -exp,
   *   AOIU(v) = -v
   * }
   * constraints = {
   *   There is no definition of v between definition and the use in a return statement,
   *   v can be any primitive numeric type
   * }
   * @author Pedro Pinheiro
   */
  private boolean isDuplicated(Variable variable) {
    ParseTreeObject pto = variable;
	for(int limit = 3;pto != null && (limit >= 0 ) && !(pto instanceof ReturnStatement); limit--, pto=pto.getParent());
	if (pto instanceof ReturnStatement) {
	  ReturnStatement rts = (ReturnStatement) pto;

	  ParseTreeObject pto2 = rts;
	  for(;pto2 != null && !(pto2 instanceof MethodDeclaration); pto2=pto2.getParent());
	  if (pto2 instanceof MethodDeclaration) {
	    MethodDeclaration md = (MethodDeclaration) pto2;
	    StatementList sl = md.getBody();
		System.out.println(42);
		for (int i = 0; i < sl.size(); i++) {
		}
	  }
	}
    boolean d_aoiu_aoiu57;
  }

 /*"term = v1 += v2
  transformations = {
	AOIU(v2) = -v2 ,
		ASRS(+=) = -=;
  }
  constraints = {

  }"*/
//  private boolean isDuplicated(Expression expression) {
//        /*
//        * "term = type v := exp; ... return v;
//            transformations = {
//              AOIU(exp) = -exp,
//              AOIU(v) = -v
//            }
//            constraints = {
//              There is no definition of v between definition and the use in a return statement,
//              v can be any primitive numeric type
//            }"
//        * */
//	//TODO: there's another problem with using aoiu_57_flag : we need to check its lifetime
//	boolean d_aoiu_57 = false;
//	if (expression instanceof Variable) {
//	  if (this.aoiu_57_flag) {
//
//		d_aoiu_57 = LogReduction.AVOID;
//		logReduction("AOIU", "AOIU", "D Rule 57 => " + expression.toString());
//	  } else {
//		Variable v = (Variable) expression;
//		ParseTreeObject parseTreeObject = (ParseTreeObject) expression;
//		while ((parseTreeObject != null) && !(parseTreeObject instanceof MethodDeclaration)) {
//		  parseTreeObject = parseTreeObject.getParent();
//		}
//		if (parseTreeObject != null) {
//		  MethodDeclaration methodDeclaration = (MethodDeclaration) parseTreeObject;
//		  StatementList body = methodDeclaration.getBody();
//		  boolean redefinition_flag = false;
//		  for (int i = 0; i < body.size(); i++) {
//			ParseTree e = body.get(i);
//			if (!(e instanceof ReturnStatement)) {
//			  if (e.toFlattenString().contains(v.toFlattenString())) redefinition_flag = true;
//			}
//		  }
//
//		  if (!redefinition_flag) this.aoiu_57_flag = true;
//		}
//	  }
//
//	} else {
//	  ParseTreeObject parseTreeObject = (ParseTreeObject) expression;
//	  while ((parseTreeObject != null) && !(parseTreeObject instanceof AssignmentExpression)) {
//		parseTreeObject = parseTreeObject.getParent();
//	  }
//	  if (parseTreeObject != null) {
//		if (this.aoiu_57_flag) {
//		  d_aoiu_57 = LogReduction.AVOID;
//		  logReduction("AOIU", "AOIU", "D Rule 57 => " + expression.toString());
//		} else {
//		  this.aoiu_57_flag = true;
//		}
//	  }
//	}
//		/*
//		*   "term = BufferedArrayOutputStream v1; ... v1.write(..., ..., v2);
//            transformations = {
//              AOIU(v2) = -v2,
//              LOI(v2) = ~v2
//            }
//            constraints = {
//              v2 > 0,
//              v2 can be any primitive numeric type
//            }"
//		* */
//	boolean d_aoiu_56 = false;
//	int limit = 5;
//	ParseTreeObject parseTreeObject = (ParseTreeObject) expression;
//	while ((parseTreeObject != null) && !((parseTreeObject instanceof MethodCall)
//		|| (parseTreeObject instanceof MethodDeclaration)) && (limit > 0)) {
//	  limit--;
//	  parseTreeObject = parseTreeObject.getParent();
//	}
//	try {
//	  if (parseTreeObject instanceof MethodCall) {
//		MethodCall methodCall = (MethodCall) parseTreeObject;
//		if (methodCall.getName().equals("write")) {
//		  //TODO: check if expression > 0
//		  if (expression instanceof Literal) {
//			Literal literal = (Literal) expression;
//		  }
//		}
//
//	  }
//	} catch (ClassCastException ignored) {
//
//	}
//	return d_aoiu_56 || d_aoiu_57;
//  }

  private boolean isDuplicated(AssignmentExpression assignmentExpression) {
	boolean d_aoiu_43 = false;

	if (assignmentExpression.getOperator() == AssignmentExpression.ADD) {
	  if (allOperatorsSelected.contains("ASRS")) {
		String desc = assignmentExpression.toFlattenString();
		logReduction("AOIU", "ASRS", desc);
		d_aoiu_43 = LogReduction.AVOID;
	  }
	}
	return d_aoiu_43;
  }
}
