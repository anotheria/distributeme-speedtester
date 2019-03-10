package org.distributeme.speedtester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.distributeme.core.ServiceDescriptor;
import org.distributeme.core.ServiceDescriptor.Protocol;
import org.distributeme.speedtester.generated.RemoteTestingServiceStub;

public class MultiThreadedRemoteClient {
	public static void main(String[] args) throws Exception {
		//RemoteTesti
		Protocol aProtocol = Protocol.RMI;
		String aServiceId = "org_distributeme_speedtester_TestingService";
		String anInstanceId = "xxx";
		String aHost = "localhost";
		int numberOfThreads = 10;
		if (args.length>0)
			numberOfThreads = Integer.parseInt(args[0]);
		if (args.length>1)
			aHost = args[1];
		int aPort = 9249;
		if (args.length>2)
			aPort = Integer.parseInt(args[2]);
		ServiceDescriptor remote = new ServiceDescriptor(aProtocol, aServiceId, anInstanceId, aHost, aPort);
		System.out.println("Testing with " + remote);
		
		//--
		RemoteTestingServiceStub stub = new RemoteTestingServiceStub(remote);
		performTestRun(numberOfThreads, stub);
		
	}
	
	public static final int CALL_LIMIT = 1000;
	
	public static final int SMALL_PACKET_SIZE = 1000;
	public static final int MEDIUM_PACKET_SIZE = 10000;
	public static final int LARGE_PACKET_SIZE = 50000;

	private	static Random rnd = new Random(System.currentTimeMillis());
	private static List<TestResult> results = new ArrayList<TestResult>();
	private static long testStartTime, testEndTime, serverSideTestDuration;

	protected static void performTestRun(int numberOfThreads, TestingService service) throws Exception{
		testStartTime = System.nanoTime();
		System.out.println("Testing instance of "+service);
		service.firstCall();
		System.out.println("connection established");

		runTest(numberOfThreads, new EchoExecutor(), service);

		runTest(numberOfThreads, new InCallNewPacketExecutor(SMALL_PACKET_SIZE), service);
		runTest(numberOfThreads, new InCallSamePacketExecutor(SMALL_PACKET_SIZE), service);
		runTest(numberOfThreads, new InCallNewPacketExecutor(MEDIUM_PACKET_SIZE), service);
		runTest(numberOfThreads, new InCallSamePacketExecutor(MEDIUM_PACKET_SIZE), service);
		runTest(numberOfThreads, new InCallNewPacketExecutor(LARGE_PACKET_SIZE), service);
		runTest(numberOfThreads, new InCallSamePacketExecutor(LARGE_PACKET_SIZE), service);

		runTest(numberOfThreads, new OutCallExecutor(SMALL_PACKET_SIZE), service);
		runTest(numberOfThreads, new OutCallExecutor(MEDIUM_PACKET_SIZE), service);
		runTest(numberOfThreads, new OutCallExecutor(LARGE_PACKET_SIZE), service);

		runTest(numberOfThreads, new InOutCallExecutor(SMALL_PACKET_SIZE), service);
		runTest(numberOfThreads, new InOutCallExecutor(MEDIUM_PACKET_SIZE), service);
		runTest(numberOfThreads, new InOutCallExecutor(LARGE_PACKET_SIZE), service);

		serverSideTestDuration = service.lastCall();
		testEndTime = System.nanoTime();
		printTestResults(numberOfThreads);
	}
	
	private static void addTestResult(String message, int calls, long duration){
		TestResult newR = new TestResult(message, calls, duration); 
		results.add(newR);
		System.out.println("\t"+newR);
	}
	
	
	private static void printTestResults(int numberOfThreads){
		long totalTestDuration = testEndTime - testStartTime;
		System.out.println("==========================");
		System.out.println("Finished Test In "+TimeUnit.MILLISECONDS.convert(totalTestDuration, TimeUnit.NANOSECONDS)+" ServerSide: "+serverSideTestDuration+" with "+numberOfThreads);
		System.out.println("==========================");
		for (TestResult result : results){
			System.out.println(result);
		}
	}
	
	static interface TestExecutor{
		void executeTest(TestingService service) throws TestingServiceException;
		String describeTest();
	}
	
	static class EchoExecutor implements TestExecutor{
		Random rnd = new Random(System.currentTimeMillis());
		public void executeTest(TestingService service) throws TestingServiceException{
			for (int i=0; i<CALL_LIMIT; i++){
				long r = rnd.nextLong();
				long rr = service.echo(r);
				if (r!=rr)
					throw new RuntimeException("Return value mismatch " + r+ " expected, "+rr+" received");
			}
		}
		
		public String describeTest(){
			return "echo calls";			
		}
	}
	
	static class InOutCallExecutor implements TestExecutor{
		
		private int size;
		
		public InOutCallExecutor(int aSize){
			size = aSize;
		}

		@Override
		public void executeTest(TestingService service)
				throws TestingServiceException {
			for (int i=0; i<CALL_LIMIT; i++){
				byte[] data = new byte[size];
				rnd.nextBytes(data);
				byte[] ret = service.inOutCall(data);
			}
		}

		@Override
		public String describeTest() {
			return "in-and-out bound packets with size "+size;
		}
		
	}

	
	static class OutCallExecutor implements TestExecutor{
		
		private int size;
		
		public OutCallExecutor(int aSize){
			size = aSize;
		}

		@Override
		public void executeTest(TestingService service)
				throws TestingServiceException {
			for (int i=0; i<CALL_LIMIT; i++){
				byte[] data = service.outCall(size);
			}
		}

		@Override
		public String describeTest() {
			return "outbound packets with size "+size;
		}
		
	}

	static class InCallSamePacketExecutor implements TestExecutor{
		
		private byte[] data;

		public InCallSamePacketExecutor(int aSize){
			data = new byte[aSize];
		}

		@Override
		public void executeTest(TestingService service)
				throws TestingServiceException {
			for (int i=0; i<CALL_LIMIT; i++){
				service.inCall(data);
			}
			
		}

		@Override
		public String describeTest() {
			return "inbound same packet with size "+data.length;
		}
		
	}

	//same as
	static class InCallNewPacketExecutor implements TestExecutor{

		private int size;

		public InCallNewPacketExecutor(int aSize){
			size = aSize;
		}

		@Override
		public void executeTest(TestingService service)
				throws TestingServiceException {
			for (int i=0; i<CALL_LIMIT; i++){
				byte[] data = new byte[size];
				rnd.nextBytes(data);
				service.inCall(data);
			}

		}

		@Override
		public String describeTest() {
			return "inbound new packets with size "+size;
		}

	}

	private static void runTest(int numberOfThreads, final TestExecutor executor, final TestingService service) throws InterruptedException{
		
		System.out.println("-- testing "+executor.describeTest()+" with "+numberOfThreads+" threads");
		
		final CountDownLatch ready = new CountDownLatch(numberOfThreads);
		final CountDownLatch start = new CountDownLatch(1);
		final CountDownLatch finish = new CountDownLatch(numberOfThreads);
		
		for (int i=0; i<numberOfThreads; i++){
			Thread t = new Thread(){
				public void run(){
					ready.countDown();
					try{
						start.await();
					}catch(InterruptedException e){}
					try{
						executor.executeTest(service);
					}catch(TestingServiceException e){
						e.printStackTrace();
						throw new RuntimeException("blubl...");
					}
					finish.countDown();
				}
			};
			t.start();
		}
		
		ready.await();
		long startTime = System.nanoTime();
		start.countDown();
		finish.await();
		long endTime = System.nanoTime();
		addTestResult(executor.describeTest(), numberOfThreads*CALL_LIMIT, endTime-startTime);
		//System.out.println("Executor "+executor+" finished in "+(endTime-startTime)/1000/1000);
	}



}
