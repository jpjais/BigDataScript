package ca.mcgill.mcb.pcingola.bigDataScript.compile;

import ca.mcgill.mcb.pcingola.bigDataScript.lang.BigDataScriptNode;
import ca.mcgill.mcb.pcingola.bigDataScript.lang.GenericNode;

/**
 * Store compilation error / warning or just an info message
 *
 * @author pcingola
 */
public class CompilerMessageBdsNode extends CompilerMessage {

	final BigDataScriptNode node;

	public static CompilerMessageBdsNode createCompilerMessageBdsNode(BigDataScriptNode node, String message, MessageType type) {
		if (node == null) return new CompilerMessageBdsNode(new GenericNode(null, null), null, -1, -1, message, type);
		else {
			if (node.getFileName() != null) return new CompilerMessageBdsNode(node, node.getFileName(), node.getLineNum(), node.getCharPosInLine() + 1, message, type);
			return new CompilerMessageBdsNode(node, node.getClass().getSimpleName(), -1, -1, message, type);
		}
	}

	private CompilerMessageBdsNode(BigDataScriptNode node, String fileName, int lineNum, int charPosInLine, String message, MessageType type) {
		super(fileName, lineNum, charPosInLine, message, type);
		this.node = node;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	public BigDataScriptNode getNode() {
		return node;
	}
}
