package mujava.op.util;

import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.mop.OJSystem;
import openjava.ptree.*;


public class ExpressionAnalyzer {
  private Expression expression;
  private Environment environment;
  private boolean insideIf;
  private boolean insideFor;
  private boolean containsZeroLiteral;
  private boolean containsString;
  private boolean forIteratorStartsAtZero;
  private boolean forIteratorIncrements;
  private boolean containsArray;
  private boolean containsLengthMethodCall;
  private boolean containsMethodCall;
  private boolean containsBinaryOperator;
  private BinaryOperator rootOperator;
  private Expression right;
  private Expression left;

  public static DebugLevel DbgLevel = DebugLevel.NONE;

  private final void Debug(String str, DebugLevel level) {
	if (level.ordinal() <= DbgLevel.ordinal()) {
	  System.out.println(str);
	}
  }

  public BinaryExpression getExpression() {
	return (BinaryExpression) expression;
  }

  public boolean isInsideIf() {
	return insideIf;
  }

  public boolean isInsideFor() {
	return insideFor;
  }

  public boolean containsMethodCall() {
	return containsMethodCall;
  }

  public boolean setContainsZeroLiteralIfTrue(Expression exp) {
	Debug("\nsetContainsZeroLiteralIfTrue >>>>> " + exp.toString(), DebugLevel.BASIC);
	if (exp instanceof Literal) {
	  Debug("Expression is Literal.", DebugLevel.DETAILED);
	  if (exp.toString().equals("0")) {
		containsZeroLiteral = true;
		Debug("Expression is zero literal", DebugLevel.DETAILED);
	  } else {
		Debug("containsZeroLiteral = false", DebugLevel.DETAILED);
	  }
	} else {
	  Debug("Expression is not Literal", DebugLevel.DETAILED);
	}
	return containsZeroLiteral;
  }

  public boolean containsString() {
	return containsString;
  }

  public boolean containsArray() {
	return containsArray;
  }

  public boolean containsLengthMethodCall() {
	return containsLengthMethodCall;
  }

  public boolean containsBinaryOperator() {
	return containsBinaryOperator;
  }

  public boolean isContainsZeroLiteral() {
	return containsZeroLiteral;
  }

  public BinaryOperator getRootOperator() {
	return rootOperator;
  }

  public Expression getRight() {
	return right;
  }

  public Expression getLeft() {
	return left;
  }

  public boolean isForIteratorStartsAtZero() {
	return forIteratorStartsAtZero;
  }

  public boolean isForIteratorIncrements() {
	return forIteratorIncrements;
  }

  private void setExpression(BinaryExpression expression) {
	this.expression = expression;
  }

  private void setEnvironment(Environment environment) {
	this.environment = environment;
	Debug("Environment was set.", DebugLevel.DETAILED);
  }

  private void setInsideIf(boolean insideIf) {
	this.insideIf = insideIf;
	Debug("\nInsideIf: " + insideIf, DebugLevel.BASIC);
  }

  private void setInsideFor(boolean insideFor) {
	this.insideFor = insideFor;
	Debug("\nInsideFor: " + insideFor, DebugLevel.BASIC);
  }

  private void setContainsString(Expression exp) {
	Debug("\nsetContainsString >>>> " + exp, DebugLevel.BASIC);
	try {
	  OJClass ojc = null;
	  if (exp instanceof MethodCall) {
		Debug("Exp is a method call so i'll check using MethodCall properties", DebugLevel.DETAILED);
		Object[] contents = ((MethodCall) exp).getContents();
		Variable rootVariable = null;
		if (contents != null && contents[0] != null) {
		  rootVariable = (Variable) contents[0];
		  ojc = rootVariable.getType(this.environment);
		}
	  } else {
		ojc = exp.getType(this.environment);
	  }
	  if (ojc == OJSystem.STRING) {
		Debug("Is String", DebugLevel.BASIC);
		this.containsString = true;
	  } else {
		Debug("Is not String", DebugLevel.DETAILED);
	  }
	} catch (Exception e) {
	  Debug("Cannot parse. Probably not a string.\nReason: " + e.getMessage(), DebugLevel.DETAILED);
	}
  }

  private void setForIteratorStartsAtZero(boolean forIteratorStartsAtZero) {
	this.forIteratorStartsAtZero = forIteratorStartsAtZero;
	Debug("forIteratorStartsAtZero: " + forIteratorStartsAtZero, DebugLevel.DETAILED);

  }

  private void setForIteratorIncrements(boolean forIteratorIncrements) {
	this.forIteratorIncrements = forIteratorIncrements;
	Debug("forIteratorIncrements: " + forIteratorIncrements, DebugLevel.DETAILED);

  }

  private void setContainsArray(Expression expression) {
	Debug("\nsetContainsArray >>>> " + expression.toString(), DebugLevel.BASIC);
	try {
	  OJClass ojc = null;
	  if (expression instanceof MethodCall) {
		Debug("Expression is a method call so i'll check using MethodCall properties", DebugLevel.DETAILED);
		Object[] contents = ((MethodCall) expression).getContents();
		Variable rootVariable = null;
		if (contents != null && contents[0] != null) {
		  rootVariable = (Variable) contents[0];
		  ojc = rootVariable.getType(this.environment);
		}
	  } else if (expression instanceof FieldAccess) {
		Object[] contents = ((FieldAccess) expression).getContents();
		if (contents != null && contents[0] != null) {
		  Variable rootVariable = (Variable) contents[0];
		  ojc = rootVariable.getType(this.environment);
		}
	  } else {
		ojc = expression.getType(environment);
	  }
	  if (ojc.isArray()) {
		this.containsArray = true;
		Debug("Is array", DebugLevel.BASIC);
	  } else {
		Debug("Is not array", DebugLevel.DETAILED);
	  }
	} catch (Exception e) {
	  Debug("Cannot parse expression. Probably not a string.\nReason: " + e.getMessage(), DebugLevel.DETAILED);
	}
  }

  private void setContainsLengthMethodCall(boolean containsLengthMethodCall) {
	this.containsLengthMethodCall = containsLengthMethodCall;
	Debug("containsLengthMethodCall: " + containsLengthMethodCall, DebugLevel.DETAILED);
  }

  private void setContainsBinaryOperator(boolean containsBinaryOperator) {
	this.containsBinaryOperator = containsBinaryOperator;
	Debug("containsBinaryOperator: " + containsBinaryOperator, DebugLevel.DETAILED);
  }

  private void setRootOperator(BinaryOperator rootOperator) {
	this.rootOperator = rootOperator;
  }

  private void setRight(Expression right) {
	this.right = right;
	Debug("right: " + right, DebugLevel.BASIC);
  }

  private void setLeft(Expression left) {
	this.left = left;
	Debug("left: " + left, DebugLevel.BASIC);
  }

  public boolean containsZeroLiteral() {
	return this.containsZeroLiteral;
  }

  public enum DebugLevel {
	NONE,
	BASIC,
	DETAILED
  }

  public enum BinaryOperator {
	NONE {
	  @Override
	  public String toString() {
		return "None";
	  }
	},
	PLUS {
	  @Override
	  public String toString() {
		return "+";
	  }
	},
	MINUS {
	  @Override
	  public String toString() {
		return "-";
	  }
	},
	EQUALS {
	  @Override
	  public String toString() {
		return "==";
	  }
	},
	DIFFERENT {
	  @Override
	  public String toString() {
		return "!=";
	  }
	},
	GREATER {
	  @Override
	  public String toString() {
		return ">";
	  }
	},
	LESSER {
	  @Override
	  public String toString() {
		return "<";
	  }
	},
	GREATEREQUAL {
	  @Override
	  public String toString() {
		return ">=";
	  }
	},
	LESSEREQUAL {
	  @Override
	  public String toString() {
		return "<=";
	  }
	},
	MOD {
	  @Override
	  public String toString() {
		return "%";
	  }
	},
	NOTIMPLEMENTED {
	  @Override
	  public String toString() {
		return "Not implemented";
	  }
	}
  }

  public static BinaryOperator translateFromBinaryExpression(int binaryExpressionOperator) {
	BinaryOperator op;
	switch (binaryExpressionOperator) {
	  case BinaryExpression.PLUS:
		op = BinaryOperator.PLUS;
		break;
	  case BinaryExpression.MINUS:
		op = BinaryOperator.MINUS;
		break;
	  case BinaryExpression.EQUAL:
		op = BinaryOperator.EQUALS;
		break;
	  case BinaryExpression.NOTEQUAL:
		op = BinaryOperator.DIFFERENT;
		break;
	  case BinaryExpression.GREATER:
		op = BinaryOperator.GREATER;
		break;
	  case BinaryExpression.LESS:
		op = BinaryOperator.LESSER;
		break;
	  case BinaryExpression.GREATEREQUAL:
		op = BinaryOperator.GREATEREQUAL;
		break;
	  case BinaryExpression.LESSEQUAL:
		op = BinaryOperator.LESSEREQUAL;
		break;
	  case BinaryExpression.MOD:
		op = BinaryOperator.MOD;
		break;
	  default:
		op = BinaryOperator.NOTIMPLEMENTED;
		break;
	}
	return op;
  }

  void init() {
	rootOperator = BinaryOperator.NONE;
	expression = null;
	environment = null;
	insideIf = false;
	insideFor = false;
	containsZeroLiteral = false;
	containsString = false;
	containsArray = false;
	containsLengthMethodCall = false;
	containsMethodCall = false;
	containsBinaryOperator = false;
	forIteratorIncrements = false;
	forIteratorStartsAtZero = false;
	right = null;
	left = null;
  }

  private void setContainsMethodCallIfTrue(Expression exp) {
	Debug("\nsetContainsMethodCallIfTrue >>>> " + exp.toString(), DebugLevel.BASIC);
	if (exp instanceof MethodCall) {
	  containsMethodCall = true;
	  MethodCall c = (MethodCall) exp;
	  Debug("Is a method call.", DebugLevel.DETAILED);
	  if (c.getName().equals("length")) {
		containsLengthMethodCall = true;
		Debug("Is length method call.", DebugLevel.BASIC);
	  } else {
		Debug("Is not length method call. >>>> " + c.getName(), DebugLevel.DETAILED);
	  }
	} else if (exp instanceof FieldAccess) {
	  //TODO: Maybe it is more apropiate to set as FieldAccess
	  containsMethodCall = true;
	  FieldAccess c = (FieldAccess) exp;
	  Debug("Is a method call.", DebugLevel.DETAILED);
	  if (c.getName().equals("length")) {
		containsLengthMethodCall = true;
		Debug("True.", DebugLevel.BASIC);
	  } else {
		Debug("False.", DebugLevel.DETAILED);
	  }
	} else {
	  Debug("Not a method call.", DebugLevel.DETAILED);
	}
  }

  private void setForStatementProperties(ForStatement exp) {
	Debug("Expression is ForStatement: " + exp.toString(), DebugLevel.DETAILED);
	setInsideFor(true);
  }

  private void setBinaryExpressionProperties(BinaryExpression exp) {
	ParseTreeObject pto = exp;

	setInsideIf(pto.getParent() instanceof IfStatement);
	setRootOperator(translateFromBinaryExpression(exp.getOperator()));
	setRight(exp.getRight());
	setLeft(exp.getLeft());
	setContainsMethodCallIfTrue(right);
	setContainsZeroLiteralIfTrue(right);
	setContainsMethodCallIfTrue(left);
	setContainsZeroLiteralIfTrue(left);
	setContainsArray(left);
	setContainsArray(right);
	setContainsString(left);
	setContainsString(right);

  }

  void parse() {
	if (expression instanceof BinaryExpression) {
	  Debug("Parsing as BinaryExpression", DebugLevel.BASIC);
	  setBinaryExpressionProperties((BinaryExpression) expression);
	} else if (expression instanceof ForStatement) {
	  Debug("Parsing as ForStatement", DebugLevel.BASIC);
	  setForStatementProperties((ForStatement) expression);
	} else {
	  Debug("Expression parsing not implemented for: ", DebugLevel.BASIC);
	}

  }

  private ExpressionAnalyzer() {
	init();
  }

  public ExpressionAnalyzer(Expression expression, Environment environment) {
	this();
	this.expression = expression;
	this.environment = environment;
	Debug("\nAnalyzing expression: " + expression.toString(), DebugLevel.BASIC);
	parse();
  }

  public ExpressionAnalyzer(BinaryExpression expression, Environment environment) {
	this((Expression) expression, environment);
  }
}
