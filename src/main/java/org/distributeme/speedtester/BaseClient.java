package org.distributeme.speedtester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.distributeme.core.exception.DistributemeRuntimeException;

public abstract class BaseClient {
	
	public static final int CALL_LIMIT = 1000;
	
	public static final int SMALL_PACKET_SIZE = 1000;
	public static final int MEDIUM_PACKET_SIZE = 10000;
	public static final int LARGE_PACKET_SIZE = 50000;

	private	static Random rnd = new Random(System.currentTimeMillis());
	private static List<TestResult> results = new ArrayList<TestResult>();
	private static long testStartTime, testEndTime, serverSideTestDuration;

	protected static void performTestRun(TestingService service){
		long startTime, endTime;
		testStartTime = System.nanoTime();
		try{
			System.out.println("Testing instance of "+service);
			service.firstCall();
			System.out.println("connection established");
			
			System.out.println("Testing echo calls");
			startTime = System.nanoTime();
			for (int i=0; i<CALL_LIMIT; i++){
				long r = rnd.nextLong();
				long rr = service.echo(r);
				if (r!=rr)
					throw new RuntimeException("Return value mismatch " + r+ " expected, "+rr+" received");
			}
			endTime = System.nanoTime();
			addTestResult("echo calls", CALL_LIMIT, endTime - startTime);
			
			testInCallStaticPacket(service, SMALL_PACKET_SIZE);
			testInCallRandomPacket(service, SMALL_PACKET_SIZE);
			testInCallStaticPacket(service, MEDIUM_PACKET_SIZE);
			testInCallRandomPacket(service, MEDIUM_PACKET_SIZE);
			testInCallStaticPacket(service, LARGE_PACKET_SIZE);
			testInCallRandomPacket(service, LARGE_PACKET_SIZE);

			testOutCall(service, SMALL_PACKET_SIZE);
			testOutCall(service, MEDIUM_PACKET_SIZE);			
			testOutCall(service, LARGE_PACKET_SIZE);

			testInOutCall(service, SMALL_PACKET_SIZE);
			testInOutCall(service, MEDIUM_PACKET_SIZE);			
			testInOutCall(service, LARGE_PACKET_SIZE);

			
			serverSideTestDuration = service.lastCall();
			testEndTime = System.nanoTime();
			printTestResults();
			
		}catch(TestingServiceException e){
			System.out.println("Aborting due to "+e.getMessage());
			e.printStackTrace();
		}catch(DistributemeRuntimeException e){
			System.out.println("Aborting due to "+e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	private static void testInCallRandomPacket(TestingService service, int size) throws TestingServiceException{
		System.out.println("Testing inbound packets(new packets), size "+size);
		long startTime = System.nanoTime();
		for (int i=0; i<CALL_LIMIT; i++){
			byte[] data = new byte[size];
			rnd.nextBytes(data);
			service.inCall(data);
		}
		long endTime = System.nanoTime();
		addTestResult("inbound new packets with size "+size, CALL_LIMIT, endTime - startTime);
	}

	private static void testInCallStaticPacket(TestingService service, int size) throws TestingServiceException{
		System.out.println("Testing inbound packets (one packet), size "+size);
		byte[] data = new byte[size];
		rnd.nextBytes(data);
		long startTime = System.nanoTime();
		for (int i=0; i<CALL_LIMIT; i++){
			service.inCall(data);
		}
		long endTime = System.nanoTime();
		addTestResult("inbound same packet with size "+size, CALL_LIMIT, endTime - startTime);
	}

	private static void testOutCall(TestingService service, int size) throws TestingServiceException{
		System.out.println("Testing outbound packets, size "+size);
		long startTime = System.nanoTime();
		for (int i=0; i<CALL_LIMIT; i++){
			byte[] data = service.outCall(size);
		}
		long endTime = System.nanoTime();
		addTestResult("outbound packets with size "+size, CALL_LIMIT, endTime - startTime);
	}

	private static void testInOutCall(TestingService service, int size) throws TestingServiceException{
		System.out.println("Testing in and out bound packets, size "+size);
		long startTime = System.nanoTime();
		for (int i=0; i<CALL_LIMIT; i++){
			byte[] data = new byte[size];
			rnd.nextBytes(data);
			byte[] ret = service.inOutCall(data);
		}
		long endTime = System.nanoTime();
		addTestResult("in-and-out bound packets with size "+size, CALL_LIMIT, endTime - startTime);
	}
	
	private static void addTestResult(String message, int calls, long duration){
		TestResult newR = new TestResult(message, calls, duration); 
		results.add(newR);
		System.out.println("\t"+newR);
	}
	
	
	private static void printTestResults(){
		long totalTestDuration = testEndTime - testStartTime;
		System.out.println("==========================");
		System.out.println("Finished Test In "+TimeUnit.MILLISECONDS.convert(totalTestDuration, TimeUnit.NANOSECONDS)+" ServerSide: "+serverSideTestDuration);
		System.out.println("==========================");
		for (TestResult result : results){
			System.out.println(result);
		}
	}

}
