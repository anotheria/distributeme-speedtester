package org.distributeme.speedtester;

public class TestingServiceException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TestingServiceException (String message){
		super(message);
	}
	public TestingServiceException (String message, Exception cause){
		super(message, cause);
	}
}
