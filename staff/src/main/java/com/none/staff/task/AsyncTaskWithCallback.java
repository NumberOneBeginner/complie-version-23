/*
 * COPYRIGHT. HSBC HOLDINGS PLC 2011. ALL RIGHTS RESERVED.
 *
 * This software is only to be used for the purpose for which it has been
 * provided. No part of it is to be reproduced, disassembled, transmitted,
 * stored in a retrieval system nor translated in any human or computer
 * language in any way or for any other purposes whatsoever without the
 * prior written consent of HSBC Holdings plc.
 */
package com.none.staff.task;

import android.os.AsyncTask;

/**
 * An {@link AsyncTask} which will call the given {@link ActivityCallback} when the task completes
 */
public abstract class AsyncTaskWithCallback<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	/**
	 * Code returned by {@link AsyncTaskWithCallback#getError()} if the task executed successfully.
	 */
	public final static int SUCCESS = 0;
	/**
	 * Generate failure code.
	 */
	public final static int FAILED = 1;
	/**
	 * Task fatal code such as Out of meomory error
	 */
	public final static int FATAL = 2;
	/**
	 * Callback for task completion.
	 */
	protected final ActivityCallback completionCallback;
	/**
	 * Tasks reference.
	 */
	protected final int taskReference;
	/**
	 * Should return a defined code for an error occurring in this task
	 */
	private int errorCode = AsyncTaskWithCallback.SUCCESS;
	/**
	 * Any stored error message
	 */
	private String error = null;
	/**
	 * Result of the task if any;
	 */
	private Result result;

	/**
	 * Creates and instance of {@link AsyncTaskWithCallback} which on completion will call the
	 * {@link ActivityCallback#handleCallback(AsyncTaskWithCallback, int)} with the given reference. The reference must be <code>>= 0</code>
	 * or {@link ActivityCallback#NO_REF}.
	 * 
	 * @param callback
	 *            a non <code>null</code> implementor of {@link ActivityCallback}.
	 * @param ref
	 *            where <code>ref >= 0</code>
	 */
	public AsyncTaskWithCallback(final ActivityCallback callback, final int ref) {
		if (callback == null) {
			throw new IllegalArgumentException("callback cannot be null");
		}
		if (ref != ActivityCallback.NO_REF && ref < 0) {
			throw new IllegalArgumentException("ref must either be ActivityCallback.NO_REF or >= 0");
		}
		this.completionCallback = callback;
		this.taskReference = ref;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(final Result result) {
		this.result = result;
		this.completionCallback.handleCallback(this, this.taskReference);
	}

	/**
	 * @return the reference this task was created with.
	 */
	public final int getRef() {
		return this.taskReference;
	}

	/**
	 * @return the result of the task, may be null if the task failed or has no result
	 */
	public final Result getResult() {
		return this.result;
	}

	/**
	 * This method should be called if an error has occurred during the execution of the task, which has cause the results (if any) to be
	 * invalid. Values <code>0</code> and <code>-1</code> are reserved for {@link #SUCCESS} and {@link #FAILED} respectively. To store a
	 * message with the error use {@link #setError(int, String)}.
	 * 
	 * @param errorCode
	 */
	protected final void setError(final int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * This method should be called if an error has occurred during the execution of the task, which has cause the results (if any) to be
	 * invalid. Values <code>0</code> and <code>1</code> are reserved for {@link #SUCCESS} and {@link #FAILED} respectively. The second
	 * argument is used to stored a error message which can be retrieved by the owning activity.
	 * 
	 * @param errorCode
	 * @param msg
	 */
	protected final void setError(final int errorCode, final String msg) {
		this.errorCode = errorCode;
		this.error = msg;
	}

	/**
	 * If this task has been executed correctly this will return {@link #SUCCESS}, otherwise it will return {@link #FAILED} or a custom
	 * error value used by the implementor of this class.
	 * 
	 * @return
	 */
	public final int getError() {
		return this.errorCode;
	}

	/**
	 * If an error occurred during the execution of this task, this method <b>may</b> return a user readable {@link String}, if no error has
	 * occurred, or no error message has been set, this method will return <code>null</code>
	 * 
	 * @return
	 */
	public final String getErrorMessage() {
		return this.error;
	}
}
