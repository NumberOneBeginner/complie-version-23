package com.hsbc.greenpacket.util.actions;

public class HookException extends Exception {
	private static final long serialVersionUID = 1L;
	public HookException(){
		super();
	}
	
	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public HookException(final String detailMessage, final Throwable throwable) {
		super(detailMessage, throwable);
	}

	/**
	 * @param detailMessage
	 */
	public HookException(final String detailMessage) {
		super(detailMessage);
	}

	/**
	 * @param throwable
	 */
	public HookException(final Throwable throwable) {
		super(throwable);
	}
}
