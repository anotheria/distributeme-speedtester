package org.distributeme.speedtester;

import java.util.Random;

public class TestingServiceImpl implements TestingService{

	private static final Random rnd = new Random(System.currentTimeMillis());
	private long firstCallTimestamp;
	
	@Override
	public long echo(long parameter) throws TestingServiceException {
		return parameter;
	}

	@Override
	public void firstCall() throws TestingServiceException {
		System.out.println("First call called");
		firstCallTimestamp = System.currentTimeMillis();
	}

	@Override
	public long lastCall() throws TestingServiceException {
		long duration = System.currentTimeMillis() - firstCallTimestamp;
		System.out.println("Last call called, duration between first and last call "+(duration) +" ms.");
		return duration;
	}

	@Override
	public int inCall(byte[] data) throws TestingServiceException {
		return data == null ? 0 : data.length;
	}

	@Override
	public byte[] outCall(int size) throws TestingServiceException {
		byte[] ret = new byte[size];
		for (int a = 0; a<ret.length; a++)
			ret[a] = (byte)(rnd.nextInt(126)+1);
		return ret;
	}

	@Override
	public byte[] inOutCall(byte[] data) throws TestingServiceException {
		byte[] ret = new byte[data.length];
		for (int i=0; i<data.length; i++)
			ret[i] = (byte)(data[i] << 2);
		return ret;
	}

}
