package ca.mcgill.mcb.pcingola.bigDataScript.lang;

import org.antlr.v4.runtime.tree.ParseTree;

import ca.mcgill.mcb.pcingola.bigDataScript.run.BdsThread;

/**
 * for( ForInit ; ForCondition ; ForEnd ) Statements
 *
 * @author pcingola
 */
public class ForEnd extends ExpressionList {

	public ForEnd(BigDataScriptNode parent, ParseTree tree) {
		super(parent, tree);
	}

	@Override
	public boolean isStopDebug() {
		return true;
	}

	@Override
	public void runStep(BdsThread bdsThread) {
		for (Expression expr : expressions) {
			bdsThread.run(expr);
			bdsThread.pop(); // Remove from stack, nobody is reading the results
		}
	}

}
