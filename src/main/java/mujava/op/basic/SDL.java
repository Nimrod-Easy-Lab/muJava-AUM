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
import mujava.util.drule.DRuleUtils;
import openjava.mop.FileEnvironment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * <p>
 * Generate SDL (Statement DeLetion) mutants -- delete each statement from
 * source code
 * </p>
 *
 * @author Lin Deng
 * @version 1.0
 */

public class SDL extends MethodLevelMutator {

  // Leo: Esse enum sera usado para distinguir a mutação a esta sendo feita.
  // e assim eliminar mutacoes duplicadas.
  public enum SDLMutations {
	STATEMENT_DELETION, CONDITIONAL_EXPRESSION_AFFIRMATION;
  }

  MethodDeclaration md;
  private List<String> allOperatorsSelected;

  public SDL(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit) {
	super(file_env, comp_unit);
	allOperatorsSelected = DRuleUtils.access().getAllOperatorsSelected();

  }

  public SDL(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit, List<String> allOperators) {
	super(file_env, comp_unit);
	allOperatorsSelected = allOperators;
  }

  @Override
  public void visit(MethodDeclaration p) throws ParseTreeException {
	md = p;
	super.visit(p);
  }

  public void visit(StatementList p) throws ParseTreeException {

	if (p.getParent() instanceof MethodDeclaration) {
	  md = (MethodDeclaration) p.getParent();
	}

	// System.out.println(md.getReturnType().getName());
	// System.out.println(p);

	if (p.size() > 0) {
	  // remove each statement and big block like while/for/if
	  genMutantSingleStatement(p);

	  genMutantBlockStatement(p);
	}
  }

  private void genMutantBlockStatement(StatementList stmtList) throws ParseTreeException {
	StatementList mutant = new StatementList();
	// for each statement block, check their type and generate mutants
	for (int i = 0; i < stmtList.size(); i++) {
	  mutant.removeAll();
	  mutant.addAll(stmtList);
	  if (isWhileStatement(mutant.get(i)))
		generateWhileMutants(mutant.get(i));
	  else if (isIfStatement(mutant.get(i)))
		generateIfMutants(mutant.get(i));
	  else if (isForStatement(mutant.get(i)))
		generateForMutants(mutant.get(i));
	  else if (isSwitchStatement(mutant.get(i)))
		generateSwitchMutants(mutant.get(i));
	  else if (isTryStatement(mutant.get(i)))
		generateTryMutants(mutant.get(i));
	  else if (isReturnStatement(mutant.get(i)))
		generateReturnMutants(mutant.get(i), md.getReturnType());
	}
  }

  private void genMutantSingleStatement(StatementList p) throws ParseTreeException {
	StatementList mutant = new StatementList();
	for (int i = 0; i < p.size(); i++) {
	  mutant.removeAll();
	  mutant.addAll(p);
	  if (!isVariableDeclaration(mutant.get(i)) && !isReturnStatement(mutant.get(i))
		  && !isTryStatement(mutant.get(i)) && !isEmptyStatement(mutant.get(i))) {
		if (!isDuplicated(mutant.get(i), SDLMutations.STATEMENT_DELETION)) {
		  mutant.remove(i);
		  outputToFile(p, mutant);
		}
	  }
	}
  }

  public void generateWhileMutants(Statement statement) throws ParseTreeException {

	WhileStatement whileStatement = (WhileStatement) statement;
	StatementList whileStatementList = whileStatement.getStatements();
	StatementList whileMutant = new StatementList();

	// Delete each statement inside the WHILE
	if (!isDuplicated(whileStatementList)) {
	  genMutantSingleStatement(whileStatementList);
	}

	Literal literalTrue = Literal.makeLiteral(true);
	// Literal literalFalse = Literal.makeLiteral(false);
	Expression expressionTrue = literalTrue;
	// Expression expressionFalse = literalFalse;
	WhileStatement whileMutantStatement = new WhileStatement(whileStatement.getExpression(),
		whileStatement.getStatements());
	whileMutantStatement.setExpression(expressionTrue);
	if (!whileStatement.toString().equalsIgnoreCase(whileMutantStatement.toString())) {
	  if (!isDuplicated(whileStatement, SDLMutations.CONDITIONAL_EXPRESSION_AFFIRMATION)) {
		outputToFile(whileStatement, whileMutantStatement);
	  }
	}
	// whileMutantStatement.setExpression(expressionFalse);
	// outputToFile(whileStatement, whileMutantStatement);

	// Generate mutants to block of statements
	genMutantBlockStatement(whileStatementList);

  }

  public void generateIfMutants(Statement statement) throws ParseTreeException {
	IfStatement ifStatement = (IfStatement) statement;
	StatementList ifStatementList = ifStatement.getStatements();
	StatementList ifMutant = new StatementList();
	StatementList elseStatementList = ifStatement.getElseStatements();
	StatementList elseMutant = new StatementList();

	// Delete each statement inside the IF
	if (!isDuplicated(ifStatementList)) {
	  genMutantSingleStatement(ifStatementList);
	}

	// Delete the if expression. Eg.: if(x>10) -> if(true)
	Literal literalTrue = Literal.makeLiteral(true);
	// Literal literalFalse = Literal.makeLiteral(false);
	Expression expressionTrue = literalTrue;
	// Expression expressionFalse = literalFalse;
	IfStatement ifMutantStatement = new IfStatement(ifStatement.getExpression(), ifStatement.getStatements());
	ifMutantStatement.setElseStatements(ifStatement.getElseStatements());
	ifMutantStatement.setExpression(expressionTrue);
	boolean d_ror_sdl51 = isDuplicated(ifStatement, ifMutantStatement);
	if (!isDuplicated(ifStatement, SDLMutations.CONDITIONAL_EXPRESSION_AFFIRMATION) && !d_ror_sdl51) {
	  outputToFile(ifStatement, ifMutantStatement);
	}
	// ifMutantStatement.setExpression(expressionFalse);
	// outputToFile(ifStatement, ifMutantStatement);

	// Delete each statement inside the else clause
	genMutantSingleStatement(elseStatementList);

	// Verify if inside the IF there is another block
	genMutantBlockStatement(ifStatementList);

	// Verify if inside the ELSE there is another block
	genMutantBlockStatement(elseStatementList);
  }

  public void generateForMutants(Statement statement) throws ParseTreeException {
	ForStatement forStatement = (ForStatement) statement;
	StatementList forStatementList = forStatement.getStatements();
	StatementList forMutant = new StatementList();

	// Delete each statement inside FOR
	if (!isDuplicated(forStatementList)) {
	  genMutantSingleStatement(forStatementList);
	}

	// ForStatement forMutantStatement = new
	// ForStatement(forStatement.getInit(), forStatement.getCondition(),
	// forStatement.getIncrement(), forStatement.getStatements());
	ForStatement forMutantStatement1 = new ForStatement(forStatement.getInitDeclType(), forStatement.getInitDecls(),
		forStatement.getCondition(), forStatement.getIncrement(), forStatement.getStatements());
	ForStatement forMutantStatement2 = new ForStatement(forStatement.getInitDeclType(), forStatement.getInitDecls(),
		forStatement.getCondition(), forStatement.getIncrement(), forStatement.getStatements());

	forMutantStatement1.setCondition(null);
	outputToFile(forStatement, forMutantStatement1);
	forMutantStatement2.setIncrement(null);
	outputToFile(forStatement, forMutantStatement2);
	// ForStatement forMutantStatement = new
	// ForStatement(forStatement.getInit(), null,
	// forStatement.getIncrement(), forStatement.getStatements());
	// System.out.println(forMutantStatement);
	// outputToFile(forStatement, forMutantStatement);
	// forMutantStatement = new ForStatement(forStatement.getInit(),
	// forStatement.getCondition(), null, forStatement.getStatements());
	// System.out.println(forMutantStatement);
	// System.out.println(forStatement);
	// outputToFile(forStatement, forMutantStatement);

	genMutantBlockStatement(forStatementList);
  }

  // public void generateDoWhileMutants(Statement statement)
  // {
  // DoWhileLoopTree doWhileStatement = (DoWhileLoopTree)statement;
  // StatementList doWhileStatementList = doWhileStatement.getStatements();
  // StatementList doWhileMutant = new StatementList();
  //
  // for(int j =0; j<doWhileStatementList.size(); j++)
  // {
  // doWhileMutant.removeAll();
  // doWhileMutant.addAll(doWhileStatementList);
  // if(!isVariableDeclaration(doWhileStatementList.get(j))
  // && !isReturnStatement(doWhileStatementList.get(j))
  // && !isTryStatement(doWhileStatementList.get(j))
  // && !isEmptyStatement(doWhileStatementList.get(j))
  // )
  // {
  // doWhileMutant.remove(j);
  // outputToFile(doWhileStatementList, doWhileMutant);
  // }
  // }
  // }
  public void generateReturnMutants(Statement statement, TypeName typeName) {
	ReturnStatement returnStatement = (ReturnStatement) statement;

	if (typeName.getName().equals("int")) {
	  Literal literal = Literal.makeLiteral(0);
	  Expression expr = literal;
	  ReturnStatement mutantStatement = new ReturnStatement(expr);
	  if (!returnStatement.toString().equalsIgnoreCase(mutantStatement.toString()))
		outputToFile(returnStatement, mutantStatement);
	} else if (typeName.getName().equals("boolean")) {
	  Literal literal = Literal.makeLiteral(true);
	  Expression expr = literal;
	  ReturnStatement mutantStatement = new ReturnStatement(expr);
	  if (!returnStatement.toString().equalsIgnoreCase(mutantStatement.toString()))
		outputToFile(returnStatement, mutantStatement);

	  literal = Literal.makeLiteral(false);
	  expr = literal;
	  mutantStatement = new ReturnStatement(expr);
	  if (!returnStatement.toString().equalsIgnoreCase(mutantStatement.toString()))
		outputToFile(returnStatement, mutantStatement);
	} else if (typeName.getName().equals("char")) {
	  Literal literal = Literal.makeLiteral(0);
	  Expression expr = literal;
	  ReturnStatement mutantStatement = new ReturnStatement(expr);
	  if (!returnStatement.toString().equalsIgnoreCase(mutantStatement.toString()))
		outputToFile(returnStatement, mutantStatement);
	} else if (typeName.getName().equals("double") || typeName.getName().equals("float")
		|| typeName.getName().equals("long") || typeName.getName().equals("short")) {
	  Literal literal = Literal.makeLiteral(0);
	  Expression expr = literal;
	  ReturnStatement mutantStatement = new ReturnStatement(expr);
	  if (!returnStatement.toString().equalsIgnoreCase(mutantStatement.toString()))
		outputToFile(returnStatement, mutantStatement);
	} else if (typeName.getName().equals("java.lang.String")) {
	  Literal literal = Literal.makeLiteral(new String());
	  Expression expr = literal;
	  ReturnStatement mutantStatement = new ReturnStatement(expr);
	  if (!returnStatement.toString().equalsIgnoreCase(mutantStatement.toString()))
		outputToFile(returnStatement, mutantStatement);
	}
	// else if(typeName.getName().equals("java.lang.Object"))
	// {
	// String string = new String("new Object()");
	// Literal literal = Literal.makeLiteral(string);
	// Expression expr = literal;
	// ReturnStatement mutantStatement = new ReturnStatement(expr);
	// mutantStatement.
	// outputToFile(returnStatement, mutantStatement);
	// }
  }

  public void generateSwitchMutants(Statement statement) throws ParseTreeException {
	SwitchStatement switchStatement = (SwitchStatement) statement;
	CaseGroupList caseGroupList = switchStatement.getCaseGroupList();

	for (int j = 0; j < caseGroupList.size(); j++) // for each group
	{

	  CaseGroup caseGroup = caseGroupList.get(j);
	  StatementList caseGroupStatementList = caseGroup.getStatements();
	  // System.out.println("case "+j+" has
	  // "+caseGroupStatementList.size()+"statements");
	  StatementList caseGroupMutant = new StatementList();

	  // System.out.println(caseGroupStatementList.get(1).toString());

	  for (int k = 0; k < caseGroupStatementList.size(); k++) {
		// System.out.println("k is "+k);
		// System.out.println(caseGroupStatementList.get(k).toString());
		caseGroupMutant.removeAll();
		caseGroupMutant.addAll(caseGroupStatementList);
		if (!isVariableDeclaration(caseGroupStatementList.get(k))
			&& !isReturnStatement(caseGroupStatementList.get(k))
			&& !isTryStatement(caseGroupStatementList.get(k))
			&& !isEmptyStatement(caseGroupStatementList.get(k))) {
		  caseGroupMutant.remove(k);
		  outputToFile(caseGroupStatementList, caseGroupMutant);
		}
	  }

	  for (int k = 0; k < caseGroupStatementList.size(); k++) {
		caseGroupMutant.removeAll();
		caseGroupMutant.addAll(caseGroupStatementList);
		if (isWhileStatement(caseGroupMutant.get(k)))
		  generateWhileMutants(caseGroupMutant.get(k));
		else if (isIfStatement(caseGroupMutant.get(k)))
		  generateIfMutants(caseGroupMutant.get(k));
		else if (isForStatement(caseGroupMutant.get(k)))
		  generateForMutants(caseGroupMutant.get(k));
		else if (isSwitchStatement(caseGroupMutant.get(k)))
		  generateSwitchMutants(caseGroupMutant.get(k));
		else if (isTryStatement(caseGroupMutant.get(k)))
		  generateTryMutants(caseGroupMutant.get(k));
		else if (isReturnStatement(caseGroupMutant.get(k)))
		  generateReturnMutants(caseGroupMutant.get(k), md.getReturnType());
	  }
	}

	CaseGroupList caseGroupListMutant = new CaseGroupList();
	for (int i = 0; i < caseGroupList.size(); i++) {
	  caseGroupListMutant.removeAll();
	  caseGroupListMutant.addAll(caseGroupList);
	  caseGroupListMutant.remove(i);
	  outputToFile(caseGroupList, caseGroupListMutant);
	}
  }

  public void generateTryMutants(Statement statement) throws ParseTreeException {
	TryStatement tryStatement = (TryStatement) statement;

	StatementList tryStatementList = tryStatement.getBody();
	CatchList catchList = tryStatement.getCatchList();
	TryStatement tryMutant = new TryStatement(tryStatementList, catchList);
	CatchList mutantCatchList = new CatchList();

	// for (int i = 0; i < catchList.size(); i++)
	// {
	// StatementList catchStatementList = new StatementList();
	// catchStatementList.addAll(tryStatement.getCatchList().get(
	// i).getBody());
	// for (int j = 0; j < catchStatementList.size(); j++)
	// {
	// StatementList catchStatementMutantList = new StatementList();
	// catchStatementMutantList.addAll(catchStatementList);
	// catchStatementMutantList.remove(j);
	//
	// tryMutant = new TryStatement(tryStatement.getBody(),
	// tryStatement.getCatchList());
	// CatchList catchMutantList = new CatchList();
	// catchMutantList.addAll(tryStatement.getCatchList());
	// catchMutantList.get(i).setBody(catchStatementMutantList);
	//// tryMutant.setCatchList(catchMutantList);
	// outputToFile(tryStatement, tryMutant);
	//
	// }
	//
	// }
	tryStatement = (TryStatement) statement;
	for (int i = 0; i < catchList.size(); i++) {
	  mutantCatchList.removeAll();
	  mutantCatchList.addAll(catchList);
	  mutantCatchList.remove(i);

	  tryMutant.setCatchList(mutantCatchList);
	  StatementList tryMutantBodyList = new StatementList();

	  for (int j = 0; j < tryStatementList.size(); j++) {
		tryMutantBodyList.removeAll();
		tryMutantBodyList.addAll(tryStatementList);
		if (!isVariableDeclaration(tryStatementList.get(j)) && !isReturnStatement(tryStatementList.get(j))
			&& !isTryStatement(tryStatementList.get(j)) && !isEmptyStatement(tryStatementList.get(j))) {

		  if (!isDuplicated(tryStatementList.get(j), SDLMutations.STATEMENT_DELETION)) {
			tryMutantBodyList.remove(j);
			tryMutant.setBody(tryMutantBodyList);
			outputToFile(tryStatement, tryMutant);
		  }
		}
	  }

	  for (int j = 0; j < tryStatementList.size(); j++) {
		tryMutantBodyList.removeAll();
		tryMutantBodyList.addAll(tryStatementList);
		if (isWhileStatement(tryMutantBodyList.get(j)))
		  generateWhileMutants(tryMutantBodyList.get(j));
		else if (isIfStatement(tryMutantBodyList.get(j)))
		  generateIfMutants(tryMutantBodyList.get(j));
		else if (isForStatement(tryMutantBodyList.get(j)))
		  generateForMutants(tryMutantBodyList.get(j));
		else if (isSwitchStatement(tryMutantBodyList.get(j)))
		  generateSwitchMutants(tryMutantBodyList.get(j));
		else if (isTryStatement(tryMutantBodyList.get(j)))
		  generateTryMutants(tryMutantBodyList.get(j));
		else if (isReturnStatement(tryMutantBodyList.get(j)))
		  generateReturnMutants(tryMutantBodyList.get(j), md.getReturnType());
	  }
	}

	tryStatement = (TryStatement) statement;
	catchList = tryStatement.getCatchList();
	// System.out.println(catchList.size());
	for (int i = 0; i < catchList.size(); i++) {

	  StatementList catchListStatementList = new StatementList();
	  catchListStatementList.addAll(catchList.get(i).getBody());
	  // System.out.println(catchList);
	  for (int j = 0; j < catchListStatementList.size(); j++) {
		StatementList mCatchList = new StatementList();
		for (int k = 0; k < catchListStatementList.size(); k++) {
		  mCatchList.add(catchListStatementList.get(k));
		}
		// mCatchList.addAll(catchListStatementList);
		Statement st = mCatchList.get(j);
		mCatchList.remove(j);
		CatchList mutantCatch = new CatchList();
		for (int k = 0; k < catchList.size(); k++) {
		  mutantCatch.add(catchList.get(k));
		}
		// mutantCatch.addAll(catchList);
		// System.out.println(mCatchList);
		CatchBlock cBlock = mutantCatch.get(i);
		cBlock.setBody(mCatchList);
		for (int k = 0; k < catchList.size(); k++) {
		  mutantCatch.set(k, catchList.get(k));
		}
		mutantCatch.set(i, cBlock);
		tryMutant.setBody(tryStatement.getBody());
		tryMutant.setCatchList(mutantCatch);
		outputToFile(tryStatement, tryMutant);
		// TryStatement mTryStatement = new
		// TryStatement(tryStatement.getBody(), catchList);
		// System.out.println(mutantCatch);
		mCatchList.add(st);
	  }

	}

  }

  public boolean isWhileStatement(Statement statement) {
	if (statement instanceof WhileStatement)
	  return true;
	else
	  return false;
  }

  public boolean isReturnStatement(Statement statement) {
	if (statement instanceof ReturnStatement)
	  return true;
	else
	  return false;
  }

  public boolean isIfStatement(Statement statement) {
	if (statement instanceof IfStatement)
	  return true;
	else
	  return false;
  }

  public boolean isExpressionStatement(Statement statement) {
	if (statement instanceof ExpressionStatement)
	  return true;
	else
	  return false;
  }

  public boolean isContinueStatement(Statement statement) {
	if (statement instanceof ContinueStatement)
	  return true;
	else
	  return false;
  }

  public boolean isSynchronizedStatement(Statement statement) {
	if (statement instanceof SynchronizedStatement)
	  return true;
	else
	  return false;
  }

  public boolean isLabeledStatement(Statement statement) {
	if (statement instanceof LabeledStatement)
	  return true;
	else
	  return false;
  }

  public boolean isDoWhileStatement(Statement statement) {
	if (statement instanceof DoWhileStatement)
	  return true;
	else
	  return false;
  }

  public boolean isTryStatement(Statement statement) {
	if (statement instanceof TryStatement)
	  return true;
	else
	  return false;
  }

  public boolean isSwitchStatement(Statement statement) {
	if (statement instanceof SwitchStatement)
	  return true;
	else
	  return false;
  }

  public boolean isForStatement(Statement statement) {
	if (statement instanceof ForStatement)
	  return true;
	else
	  return false;
  }

  public boolean isThrowStatement(Statement statement) {
	if (statement instanceof ThrowStatement)
	  return true;
	else
	  return false;
  }

  public boolean isBreakStatement(Statement statement) {
	if (statement instanceof BreakStatement)
	  return true;
	else
	  return false;
  }

  public boolean isVariableDeclaration(Statement statement) {
	if (statement instanceof VariableDeclaration)
	  return true;
	else
	  return false;
  }

  public boolean isEmptyStatement(Statement statement) {
	if (statement instanceof EmptyStatement)
	  return true;
	else
	  return false;
  }

  public boolean isConstructorDeclaration(Statement statement) {
	if (statement instanceof ConstructorDeclaration)
	  return true;
	else
	  return false;
  }

  /**
   * Output SDL mutants to files
   *
   * @param original
   * @param mutant
   */
  public void outputToFile(StatementList original, StatementList mutant) {
	if (comp_unit == null)
	  return;
	if (original.toString().equalsIgnoreCase(mutant.toString()))
	  return;
	String f_name;
	num++;
	f_name = getSourceName("SDL");
	String mutant_dir = getMuantID("SDL");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  SDL_Writer writer = new SDL_Writer(mutant_dir, out);
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

  public void outputToFile(CaseGroupList original, CaseGroupList mutant) {
	if (comp_unit == null)
	  return;
	if (original.toString().equalsIgnoreCase(mutant.toString()))
	  return;
	String f_name;
	num++;
	f_name = getSourceName("SDL");
	String mutant_dir = getMuantID("SDL");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  SDL_Writer writer = new SDL_Writer(mutant_dir, out);
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

  public void outputToFile(TryStatement original, TryStatement mutant) {
	if (comp_unit == null)
	  return;
	if (original.toString().equalsIgnoreCase(mutant.toString()))
	  return;
	String f_name;
	num++;
	f_name = getSourceName("SDL");
	String mutant_dir = getMuantID("SDL");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  SDL_Writer writer = new SDL_Writer(mutant_dir, out);
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

  private void outputToFile(IfStatement original, IfStatement mutant) {
	if (comp_unit == null)
	  return;
	if (original.toString().equalsIgnoreCase(mutant.toString()))
	  return;
	String f_name;
	num++;
	f_name = getSourceName("SDL");
	String mutant_dir = getMuantID("SDL");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  SDL_Writer writer = new SDL_Writer(mutant_dir, out);
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

  private void outputToFile(WhileStatement original, WhileStatement mutant) {
	if (comp_unit == null)
	  return;
	if (original.toString().equalsIgnoreCase(mutant.toString()))
	  return;
	String f_name;
	num++;
	f_name = getSourceName("SDL");
	String mutant_dir = getMuantID("SDL");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  SDL_Writer writer = new SDL_Writer(mutant_dir, out);
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

  private void outputToFile(ForStatement original, ForStatement mutant) {
	if (comp_unit == null)
	  return;
	if (original.toString().equalsIgnoreCase(mutant.toString()))
	  return;
	String f_name;
	num++;
	f_name = getSourceName("SDL");
	String mutant_dir = getMuantID("SDL");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  SDL_Writer writer = new SDL_Writer(mutant_dir, out);
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
  // private void outputToFile(CaseGroupList original,
  // CaseGroupList mutant)
  // {
  // if (comp_unit == null)
  // return;
  //
  // String f_name;
  // num++;
  // f_name = getSourceName("ROR");
  // String mutant_dir = getMuantID("ROR");
  //
  // try
  // {
  // PrintWriter out = getPrintWriter(f_name);
  // ROR_Writer writer = new ROR_Writer(mutant_dir, out);
  // writer.setMutant(original, mutant);
  // writer.setMethodSignature(currentMethodSignature);
  // comp_unit.accept( writer );
  // out.flush();
  // out.close();
  // } catch ( IOException e )
  // {
  // System.err.println( "fails to create " + f_name );
  // } catch ( ParseTreeException e ) {
  // System.err.println( "errors during printing " + f_name );
  // e.printStackTrace();
  // }
  //
  // }

  /**
   * Output SDL mutants (true or false) to files
   *
   * @param original
   * @param mutant
   */
  public void outputToFile(StatementList original, Literal mutant) {
	if (comp_unit == null)
	  return;
	if (original.toString().equalsIgnoreCase(mutant.toString()))
	  return;
	String f_name;
	num++;
	f_name = getSourceName("SDL");
	String mutant_dir = getMuantID("SDL");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  SDL_Writer writer = new SDL_Writer(mutant_dir, out);
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

  private void outputToFile(ReturnStatement original, ReturnStatement mutant) {
	if (comp_unit == null)
	  return;
	if (original.toString().equalsIgnoreCase(mutant.toString()))
	  return;
	String f_name;
	num++;
	f_name = getSourceName("SDL");
	String mutant_dir = getMuantID("SDL");

	try {
	  PrintWriter out = getPrintWriter(f_name);
	  SDL_Writer writer = new SDL_Writer(mutant_dir, out);
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
   * @param statement
   * @param mutation
   * @return
   * @throws ParseTreeException
   */
  private boolean isDuplicated(Statement statement, SDLMutations mutation) {

	if (mutation == SDLMutations.STATEMENT_DELETION) {
	  // #Rule 1: SDL x VDL (Increment and Decrement alone in a statement)
	  if (statement instanceof ExpressionStatement) {
		Expression expression = ((ExpressionStatement) statement).getExpression();
		if (expression instanceof UnaryExpression) {
		  if (allOperatorsSelected.contains("VDL")) {
			String desc = statement.toFlattenString() + " => " + mutation.toString();
			logReduction("SDL", "VDL", desc);
			return LogReduction.AVOID;
		  }
		}
	  }
	}
	return false;
  }

  /**
   * DRULE 52 ROR_SDL
   * Avoid generating duplicated mutants given following conditions:
   * term = if(exp1 || exp2){ ... }
   * transformations = {
   *   ROR(exp1) = true,
   *   SDL(exp1 || exp2) = true
   * }
   * constraints = {
   *
   * }
   * @author Pedro Pinheiro
   * @param ifStatement IfStatement subject to mutation
   * @param mutant Literal that will replace IfStatement expression
   */
  private boolean isDuplicated(IfStatement ifStatement, IfStatement mutant){
    boolean d_cor_sdl52 = false;
    Object[] c = ifStatement.getContents();
    if (((c != null) && (c.length > 0)) && (c[0] instanceof BinaryExpression)) {
      BinaryExpression binaryExpression = (BinaryExpression) ifStatement.getExpression();
      if (binaryExpression.getOperator() == BinaryExpression.LOGICAL_OR) {
        boolean condition1 = mutant.getExpression().toString().equals("true");
		boolean condition2 = DRuleUtils.access().isOperatorSelected("ROR");
        if ( condition1 &&	condition2) {
          System.out.flush();
		  System.out.println("Triggered DRULE_COR_SDL 52 : " + ifStatement.toFlattenString()
		  + "=> " + mutant.toFlattenString());
		  d_cor_sdl52 = LogReduction.AVOID;
          logReduction("ROR", "SDL", "Triggered DRULE_COR_SDL 52 => "
			  + ifStatement.toFlattenString());
		}
	  }
	}
    return d_cor_sdl52;
  }

  /**
   * Avoid generate duplicated mutants
   *
   * @param statementList
   * @return
   */
  private boolean isDuplicated(StatementList statementList) {
	// #Rule 1: SDL x SDL (If there is only one Statement inside a statment
	// list (in blocks like OFr, While, If, Swicth))
	// so remove this statement or remove all block has the same effect
	// (with minor exceptions)
	if (statementList != null && statementList.size() == 1) {
	  if (statementList.getParent() instanceof IfStatement) {
		IfStatement ifStmt = (IfStatement) statementList.getParent();
		Expression exp = ifStmt.getExpression();
		if (ifStmt.getElseStatements() == null || ifStmt.getElseStatements().size() > 0) {
		  return false;
		}
		// If the expression is instance of MethodCall, it can change
		// program state
		if (exp instanceof MethodCall) {
		  return false;
		}
	  }
	  if (statementList.getParent() instanceof WhileStatement) {
		WhileStatement whStmt = (WhileStatement) statementList.getParent();
		Expression exp = whStmt.getExpression();
		// If the expression is instance of MethodCall, it can change
		// program state
		if (exp instanceof MethodCall) {
		  return false;
		}
	  }
	  if (statementList.getParent() instanceof ForStatement) {
		ForStatement forStmt = (ForStatement) statementList.getParent();
		Expression exp = forStmt.getCondition();
		// If the expression is instance of MethodCall, it can change
		// program state
		if (exp instanceof MethodCall) {
		  return false;
		}
	  }

	  if (!isVariableDeclaration(statementList.get(0)) && !isReturnStatement(statementList.get(0))
		  && !isTryStatement(statementList.get(0)) && !isEmptyStatement(statementList.get(0))) {
		String desc = statementList.get(0).toFlattenString() + " => ";
		logReduction("SDL", "SDL", desc);
		return LogReduction.AVOID;
	  }
	}
	return false;
  }

}
