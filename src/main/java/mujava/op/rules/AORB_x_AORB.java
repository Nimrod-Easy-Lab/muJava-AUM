package mujava.op.rules;

import openjava.ptree.BinaryExpression;
import openjava.ptree.Expression;
import openjava.ptree.Literal;

public class AORB_x_AORB extends DRule {


  private boolean isDuplicated(BinaryExpression original, BinaryExpression mutant) {
	int op = original.getOperator();
	if (op == BinaryExpression.PLUS || op == BinaryExpression.MINUS) {
	  Expression right = original.getRight();
	  if (right instanceof Literal && ((Literal) right).equals(Literal.constantOne())) {
		if (mutant.getOperator() == BinaryExpression.DIVIDE) {
		  return true;
		}
	  }
	}
	return false;
  }


  private int methodId_1() {
	int x = 0;
	int y;

	isDuplicated(null, null);

	return x + 1;


  }


}