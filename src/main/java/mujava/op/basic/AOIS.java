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

import mujava.op.util.ContextInfo;
import mujava.op.util.LogReduction;
import openjava.mop.FileEnvironment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Stack;

/**
 * <p>
 * Generate AOIS (Arithmetic Operator Insertion (Short-cut)) mutants -- insert
 * unary operators (increment ++, decrement --) before and after each variable
 * of an arithmetic type
 * </p>
 *
 * @author Yu-Seung Ma
 * @version 1.0
 */

public class AOIS extends Arithmetic_OP {

  // Leo: Esse enum sera usado para distinguir a mutação a esta sendo feita.
  // e assim eliminar mutacoes duplicadas.
  public enum AOISMutations {
	POSINCREMENT_INSERTION, POSDECREMENT_INSERTION, PREINCREMENT_INSERTION, PREDECREMENT_INSERTION;
  }


  private ContextInfo contextInfo;
  boolean isPrePostEQ = true;
  private boolean isInsideAReturnStatement;
  private boolean isInsideAMethod;
  private Stack<Boolean> isInsideALoop = new Stack<Boolean>();

  ArrayList<Variable> lastReferenceOfAVariableInReturn = new ArrayList<Variable>();
  ArrayList<Variable> lastReferenceOfAVariableInMethod = new ArrayList<Variable>();

  private java.util.List<String> allOperatorsSelected;

  public AOIS(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit) {
	super(file_env, comp_unit);
	contextInfo = new ContextInfo();
	contextInfo.setMutationOperatorGroup("AOIS");
  }

  public AOIS(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit,
			  java.util.List<String> allOperators) {
	super(file_env, comp_unit);
	this.allOperatorsSelected = allOperators;
	contextInfo = new ContextInfo();
	contextInfo.setMutationOperatorGroup("AOIS");
  }

  public void visit(UnaryExpression p) throws ParseTreeException {
	// NO OPERATION
  }

  public void visit(Variable p) throws ParseTreeException {
	if (isArithmeticType(p)) {
	  if (isPrePostEQ) {
		if (!isEquivalent(p, new Variable(p.toString() + "++"), AOISMutations.POSINCREMENT_INSERTION)) {
		  outputToFile(p, p.toString() + "++");
		}
		if (!isEquivalent(p, new Variable(p.toString() + "--"), AOISMutations.POSDECREMENT_INSERTION)) {
		  outputToFile(p, p.toString() + "--");
		}
	  } else {
		if (!isEquivalent(p, new Variable("++" + p.toString()), AOISMutations.PREINCREMENT_INSERTION)) {
		  outputToFile(p, "++" + p.toString());
		}
		if (!isEquivalent(p, new Variable("--" + p.toString()), AOISMutations.PREDECREMENT_INSERTION)) {
		  outputToFile(p, "--" + p.toString());
		}
		if (!isEquivalent(p, new Variable(p.toString() + "++"), AOISMutations.POSINCREMENT_INSERTION)) {
		  outputToFile(p, p.toString() + "++");
		}
		if (!isEquivalent(p, new Variable(p.toString() + "--"), AOISMutations.POSDECREMENT_INSERTION)) {
		  outputToFile(p, p.toString() + "--");
		}
	  }
	}
  }

  public void visit(FieldAccess p) throws ParseTreeException {
	if (isArithmeticType(p)) {
	  if (isPrePostEQ) {
		outputToFile(p, p.toString() + "++");
		outputToFile(p, p.toString() + "--");
	  } else {
		outputToFile(p, "++" + p.toString());
		outputToFile(p, "--" + p.toString());
		outputToFile(p, p.toString() + "++");
		outputToFile(p, p.toString() + "--");
	  }
	}
  }

  private void checkVariables(Object[] contents, ArrayList<Variable> variables) {
	for (Object obj : contents) {
	  if (obj instanceof Variable) {
		variables.add((Variable) obj);
	  } else {
		if (obj instanceof NonLeaf) {
		  NonLeaf nl = (NonLeaf) obj;
		  if (nl instanceof VariableDeclaration) {
			this.localVariables.add(((VariableDeclaration) nl).getVariable());
		  }
		  checkVariables(nl.getContents(), variables);
		} else if (obj instanceof List) {
		  List l = (List) obj;
		  if (l.elements().hasMoreElements()) {
			java.util.List<Objects> stmts = Collections.list(l.elements());
			checkVariables(stmts.toArray(), variables);
		  }
		}

	  }
	}
  }

  @Override
  public void visit(ReturnStatement p) throws ParseTreeException {
	isInsideAReturnStatement = true;

	ArrayList<Variable> variables = new ArrayList<Variable>();
	lastReferenceOfAVariableInReturn = new ArrayList<Variable>();
	checkVariables(p.getContents(), variables);

	// ArrayList<String> uniqueVars = new ArrayList<String>();
	// for (Variable var : variables) {
	// if (!uniqueVars.contains(var.toString())) {
	// uniqueVars.add(var.toString());
	// }
	// }
	if (localVariables != null) {
	  for (String unique : localVariables) {
		Variable lastReference = null;
		for (Variable var : variables) {
		  if (var.toString().equals(unique)) {
			lastReference = var;
		  }
		}
		lastReferenceOfAVariableInReturn.add(lastReference);
	  }
	}

	super.visit(p);
	isInsideAReturnStatement = false;
  }

  ArrayList<String> localVariables = new ArrayList<String>();

  @Override
  public void visit(MethodDeclaration p) throws ParseTreeException {

	if (p.getName().contains("createStatement")) {
	  System.out.println();
	}

	isInsideAMethod = true;
	localVariables = new ArrayList<String>();
	ArrayList<Variable> variables = new ArrayList<Variable>();
	StatementList stmtList = p.getBody();
	// add parameters as local variables
	ParameterList paramList = p.getParameters();
	if (paramList != null && paramList.size() > 0) {
	  for (int i = 0; i < paramList.size(); i++) {
		Parameter param = paramList.get(0);
		localVariables.add(param.getVariable());
	  }
	}
	if (stmtList != null && stmtList.size() > 0) {
	  for (int i = 0; i < stmtList.size(); i++) {
		Statement stmt = stmtList.get(i);
		if (stmt instanceof VariableDeclaration) {
		  VariableDeclaration vd = (VariableDeclaration) stmt;
		  localVariables.add(vd.getVariable());
		}

		if (stmt instanceof NonLeaf) {
		  NonLeaf nl = (NonLeaf) stmt;
		  checkVariables(nl.getContents(), variables);
		}
	  }
	}

	for (String unique : localVariables) {
	  Variable lastReference = null;
	  for (Variable var : variables) {
		if (var.toString().equals(unique)) {
		  lastReference = var;
		}
	  }
	  lastReferenceOfAVariableInMethod.add(lastReference);
	}

	super.visit(p);
	localVariables = null;
	isInsideAMethod = false;
  }

  @Override
  public void visit(ForStatement p) throws ParseTreeException {
	isInsideALoop.push(true);
	super.visit(p);
	isInsideALoop.pop();
  }

  @Override
  public void visit(WhileStatement p) throws ParseTreeException {
	isInsideALoop.push(true);
	super.visit(p);
	isInsideALoop.pop();
  }

  @Override
  public void visit(DoWhileStatement p) throws ParseTreeException {
	isInsideALoop.push(true);
	super.visit(p);
	isInsideALoop.pop();
  }

  public void visit(BinaryExpression p) throws ParseTreeException {
	isPrePostEQ = false;
	super.visit(p);
	isPrePostEQ = true;
  }

  public void visit(AssignmentExpression p) throws ParseTreeException {
	isPrePostEQ = false;
	Expression rexp = p.getRight();
	rexp.accept(this);
	isPrePostEQ = true;
  }

  /**
   * Write AOIS mutants to files
   *
   * @param original_field
   * @param mutant
   */
  public void outputToFile(FieldAccess original_field, String mutant) {
	if (comp_unit == null)
	  return;

	String f_name;
	num++;
	f_name = getSourceName("AOIS");
	String mutant_dir = getMuantID("AOIS");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  AOIS_Writer writer = new AOIS_Writer(mutant_dir, out);
	  writer.setMutant(original_field, mutant);
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
   * Write AOIS mutants to files
   *
   * @param original_var
   * @param mutant
   */
  public void outputToFile(Variable original_var, String mutant) {
	if (comp_unit == null)
	  return;

	String f_name;
	num++;
	f_name = getSourceName("AOIS");
	String mutant_dir = getMuantID("AOIS");

	contextInfo.setBefore(original_var.toString());
	contextInfo.setAfter(mutant);

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  AOIS_Writer writer = new AOIS_Writer(mutant_dir, out);
	  writer.setMutant(original_var, mutant);
	  writer.setMethodSignature(currentMethodSignature);
	  writer.saveContextInfo(contextInfo);
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

  private void astContext() {

  }

  /**
   * Avoid generate equivalent mutants
   *
   * @param original
   * @param mutant
   * @param mutation
   * @return
   */
  private boolean isEquivalent(Expression original, Expression mutant, AOISMutations mutation) {
	// #Rule 1: If the variable's parent is a return statement.
	// No need to generate post-increment/decrement

	Expression e;


	if (original instanceof Variable) {
	  if (isInsideAReturnStatement) {
		if (mutation == AOISMutations.POSINCREMENT_INSERTION
			|| mutation == AOISMutations.POSDECREMENT_INSERTION) {
		  if (lastReferenceOfAVariableInReturn.contains(original)) {
			String desc = original.toFlattenString() + " => " + mutant.toFlattenString();
			logReduction("AOIS", desc);
			return LogReduction.AVOID;
		  }
		}
	  }
	  if (isInsideAMethod && isInsideALoop.isEmpty()) {
		if (mutation == AOISMutations.POSINCREMENT_INSERTION
			|| mutation == AOISMutations.POSDECREMENT_INSERTION) {
		  if (lastReferenceOfAVariableInMethod.contains(original)) {
			String desc = original.toFlattenString() + " => " + mutant.toFlattenString();
			logReduction("AOIS", desc);
			return LogReduction.AVOID;
		  }
		}
	  }

	  // #Rule 2: Is correct applying mutation operator in ThrowStatement?
	  // if(isInsideAThrowStatement){
	  // return true;
	  // }
	}

	return false;
  }

  /**
   * Avoid generate duplicated mutants
   *
   * @param original
   * @param mutant
   * @return
   * @throws ParseTreeException
   */
  private boolean isDuplicated(Expression original, Expression mutant) {
	// #Rule 1: AOIS x ROR - If there is a AOIS inside a ROR, and that ROR
	// transformation
	// change
	//

	return false;
  }

}
