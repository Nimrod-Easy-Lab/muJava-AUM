/*
 * AssertStatement.java 1.0
 *
 * Aug 22, 2014 by Lin
 *
 * @see openjava.ptree.ParseTree
 * @version 1.0 last updated:  Aug 22, 2014
 * @author  Lin Deng
 */
package openjava.ptree;

import openjava.ptree.util.ParseTreeVisitor;

/**
 * The <code>AssertStatement</code> class represents a assert
 * statement node of parse tree.
 *
 * @see openjava.ptree.ParseTree
 * @see openjava.ptree.NonLeaf
 * @see openjava.ptree.Statement
 * @see openjava.ptree.Expression
 */
public class AssertStatement extends NonLeaf implements Statement, ParseTree {
	/**
	 * Allocates a new object.
	 *
	 * @param  expr  the expression of the assert condition.
	 * 
	 */
	public AssertStatement(Expression expr) {
		super();
		set((ParseTree) expr);
	}
	
	public AssertStatement(Expression expr1, Expression expr2) {
		super();
		set((ParseTree) expr1, (ParseTree) expr2);
	}

	AssertStatement() {
		super();
	}

	/**
	 * Gets the condition of this assert statement.
	 *
	 * @return  the expression of the condition.
	 */
	public Expression getExpression() {
		return (Expression) elementAt(0);
	}

	/**
	 * Sets the condition of this assert statement.
	 *
	 * @param  expr  the expression of the condition to set.
	 */
	public void setExpression(Expression expr) {
		setElementAt(expr, 0);
	}
	
	/**
	 * Gets the condition of this assert statement.
	 *
	 * @return  the expression of the condition.
	 */
	public Expression getExpression2() {
		return (Expression) elementAt(1);
	}

	/**
	 * Sets the condition of this assert statement.
	 *
	 * @param  expr  the expression of the condition to set.
	 */
	public void setExpression2(Expression expr) {
		setElementAt(expr, 1);
	}

	public void accept(ParseTreeVisitor v) throws ParseTreeException {
		v.visit(this);
	}

}
