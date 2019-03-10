package org.distributeme.speedtester;

public class LocalClient {
	public static void main(String[] args) {
		TestingService service = new TestingServiceImpl();
		BaseClient.performTestRun(service);
	}
}
