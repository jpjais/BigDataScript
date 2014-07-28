package ca.mcgill.mcb.pcingola.bigDataScript.lang;

import java.util.HashMap;

import org.antlr.v4.runtime.tree.ParseTree;

import ca.mcgill.mcb.pcingola.bigDataScript.compile.CompilerMessage.MessageType;
import ca.mcgill.mcb.pcingola.bigDataScript.compile.CompilerMessages;
import ca.mcgill.mcb.pcingola.bigDataScript.run.BigDataScriptThread;
import ca.mcgill.mcb.pcingola.bigDataScript.scope.Scope;
import ca.mcgill.mcb.pcingola.bigDataScript.scope.ScopeSymbol;

/**
 * A reference to a list/array variable. E.g. list[3]
 *
 * @author pcingola
 */
public class VarReferenceMap extends Reference {

	VarReference variable;
	Expression expressionKey;

	public VarReferenceMap(BigDataScriptNode parent, ParseTree tree) {
		super(parent, tree);
	}

	/**
	 * Evaluate an expression
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object eval(BigDataScriptThread bdsThread) {
		String key = evalKey(bdsThread);
		HashMap map = getMap(bdsThread.getScope());
		Object ret = map.get(key);
		if (ret == null) throw new RuntimeException("Map '" + getVariableName() + "' does not have key '" + key + "'.");
		return ret;
	}

	/**
	 * Return index evaluation
	 */
	public String evalKey(BigDataScriptThread bdsThread) {
		return expressionKey.evalString(bdsThread);
	}

	@SuppressWarnings("rawtypes")
	public HashMap getMap(Scope scope) {
		ScopeSymbol ss = getScopeSymbol(scope);
		return (HashMap) ss.getValue();
	}

	/**
	 * Get symbol from scope
	 */
	@Override
	public ScopeSymbol getScopeSymbol(Scope scope) {
		return variable.getScopeSymbol(scope);
	}

	public Type getType(Scope scope) {
		ScopeSymbol ss = getScopeSymbol(scope);
		return ss.getType();
	}

	@Override
	public String getVariableName() {
		return variable.getVariableName();
	}

	@Override
	protected boolean isReturnTypesNotNull() {
		return returnType != null;
	}

	@Override
	protected void parse(ParseTree tree) {
		variable = (VarReference) factory(tree, 0);
		// child[1] = '{'
		expressionKey = (Expression) factory(tree, 2);
		// child[3] = '}'
	}

	@Override
	public void parse(String str) {
		int idx1 = str.indexOf('{');
		int idx2 = str.indexOf('}');
		if ((idx1 <= 0) || (idx2 <= idx1)) throw new RuntimeException("Cannot parse list reference '" + str + "'");

		// Create VarReference
		String varName = str.substring(0, idx1);
		variable = new VarReference(this, null);
		variable.parse(varName);

		// Create index expression
		LiteralString exprIdx = new LiteralString(this, null);
		String idxStr = str.substring(idx1 + 1, idx2);
		if (idxStr.startsWith("'")) idxStr = idxStr.substring(1);
		if (idxStr.endsWith("'")) idxStr = idxStr.substring(0, idxStr.length() - 1);
		exprIdx.setValue(idxStr);
		expressionKey = exprIdx;
	}

	@Override
	public Type returnType(Scope scope) {
		if (returnType != null) return returnType;

		expressionKey.returnType(scope);
		Type nameType = variable.returnType(scope);

		if (nameType == null) return null;
		if (nameType.isMap()) returnType = ((TypeMap) nameType).getBaseType();

		return returnType;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setValue(BigDataScriptThread bdsThread, Object value) {
		String key = evalKey(bdsThread);
		HashMap map = getMap(bdsThread.getScope());
		map.put(key, value);
	}

	@Override
	public String toString() {
		return variable + "{'" + expressionKey + "'}";
	}

	@Override
	protected void typeCheck(Scope scope, CompilerMessages compilerMessages) {
		// Calculate return type
		returnType(scope);

		// Note: We do not perform type checking on 'key' since everything can be cast to a string
		if ((variable.getReturnType() != null) && !variable.getReturnType().isMap()) compilerMessages.add(this, "Symbol '" + variable + "' is not a map", MessageType.ERROR);
	}

	@Override
	protected void typeCheckNotNull(Scope scope, CompilerMessages compilerMessages) {
		throw new RuntimeException("This method should never be called!");
	}

}
