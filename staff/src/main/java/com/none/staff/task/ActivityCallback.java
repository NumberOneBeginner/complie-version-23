
package com.none.staff.task;

public interface ActivityCallback {
	public static int NO_REF = -1;
	@SuppressWarnings("rawtypes")
	public void handleCallback(final AsyncTaskWithCallback task, final int ref);
}
