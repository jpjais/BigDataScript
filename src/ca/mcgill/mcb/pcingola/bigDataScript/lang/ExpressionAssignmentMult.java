package ca.mcgill.mcb.pcingola.bigDataScript.lang;

import org.antlr.v4.runtime.tree.ParseTree;

/**
 * Expression
 * 
 * @author pcingola
 */
public class ExpressionAssignmentMult extends ExpressionAssignmentBinary {

	public ExpressionAssignmentMult(BigDataScriptNode parent, ParseTree tree) {
		super(parent, tree);
	}

	@Override
	protected ExpressionBinary createSubExpression() {
		return new ExpressionTimes(this, null);
	}

	@Override
	protected String op() {
		return "*=";
	}

}
