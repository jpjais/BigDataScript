package ca.mcgill.mcb.pcingola.bigDataScript.lang;

import org.antlr.v4.runtime.tree.ParseTree;

import ca.mcgill.mcb.pcingola.bigDataScript.run.BdsThread;

/**
 * Boolean OR
 *
 * @author pcingola
 */
public class ExpressionLogicOr extends ExpressionLogic {

	public ExpressionLogicOr(BigDataScriptNode parent, ParseTree tree) {
		super(parent, tree);
	}

	@Override
	protected String op() {
		return "||";
	}

	/**
	 * Evaluate an expression
	 */
	@Override
	public void runStep(BdsThread bdsThread) {
		bdsThread.run(left);

		if (!bdsThread.isCheckpointRecover()) {
			boolean ok = (Boolean) Type.BOOL.cast(bdsThread.peek());
			if (ok) return; // Already true? No need to evaluate the other expression
		}

		//  'OR' only depends on 'right' value (left was false)
		bdsThread.pop(); // Remove 'left' result from stack

		bdsThread.run(right);
	}

}
