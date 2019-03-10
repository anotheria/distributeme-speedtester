package org.distributeme.speedtester;

import org.distributeme.annotation.DistributeMe;

import net.anotheria.anoprise.metafactory.Service;

@DistributeMe(agentsSupport=false, stripToEssential=true)
public interface TestingService extends Service{
	/**
	 * Simple echo method for short requests
	 * @param parameter
	 * @return
	 * @throws TestingServiceException
	 */
	long echo(long parameter) throws TestingServiceException;
	
	/**
	 * This is used for first call solely.
	 * @throws TestingServiceException
	 */
	void firstCall() throws TestingServiceException;
	/**
	 * Returns size of the given array. Used to emulate huge incoming requests.
	 * @param data
	 * @return
	 * @throws TestingServiceException
	 */
	int inCall(byte[] data) throws TestingServiceException;
	/**
	 * Returns a filled array of given size.
	 * @param size
	 * @return
	 * @throws TestingServiceException
	 */
	byte[] outCall(int size) throws TestingServiceException;
	
	byte[] inOutCall(byte[] data) throws TestingServiceException;

	long lastCall() throws TestingServiceException; 
}
