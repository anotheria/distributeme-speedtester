package org.distributeme.speedtester;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestResult {
	private static final AtomicInteger counter = new AtomicInteger(0);
	private String message;
	private int calls;
	private long duration;
	private int resultNumber;
	
	public TestResult(String aMessage, int aCalls, long aDuration){
		this.message = aMessage;
		this.calls = aCalls;
		this.duration = TimeUnit.MILLISECONDS.convert(aDuration, TimeUnit.NANOSECONDS);
		resultNumber = counter.incrementAndGet();
	}
	
	@Override public String toString(){
		double avg = ((double)duration/calls);
		return resultNumber+" - "+message+", Performed "+calls+" in "+duration+", "+avg+ " ms per call - "+evaluateResult(avg);
	}
	
	private String evaluateResult(double aResult){
		if (aResult<0.1)
			return "SUPERB";
		if (aResult<0.3)
			return "TOP";
		if (aResult<0.5)
			return "OK";
		if (aResult<1.0)
			return "ACCEPTABLE";
		if (aResult<5.0)
			return "LOUSY";
		return "DEAD HORSE";
	}
	
	
}
