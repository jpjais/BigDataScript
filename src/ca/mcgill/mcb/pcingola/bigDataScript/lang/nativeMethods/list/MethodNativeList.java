package ca.mcgill.mcb.pcingola.bigDataScript.lang.nativeMethods.list;

import java.util.ArrayList;

import ca.mcgill.mcb.pcingola.bigDataScript.lang.Type;
import ca.mcgill.mcb.pcingola.bigDataScript.lang.nativeMethods.MethodNative;
import ca.mcgill.mcb.pcingola.bigDataScript.run.BdsThread;

/**
 * Methods for list/array/stack
 *
 * @author pcingola
 */
public abstract class MethodNativeList extends MethodNative {

	public MethodNativeList(Type baseType) {
		super();
		if (baseType != null) initMethod(baseType);
	}

	@Override
	protected void initMethod() {
		// Nothing to do, we cannot initialize directly
	}

	protected abstract void initMethod(Type baseType);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Object runMethodNative(BdsThread csThread, Object objThis) {
		ArrayList list = (ArrayList) objThis;
		Object toPush = csThread.getObject("toPush");
		list.add(toPush);
		return toPush;
	}
}
