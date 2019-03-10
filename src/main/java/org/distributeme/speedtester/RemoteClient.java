package org.distributeme.speedtester;

import org.distributeme.core.ServiceDescriptor;
import org.distributeme.core.ServiceDescriptor.Protocol;
import org.distributeme.speedtester.generated.RemoteTestingServiceStub;

public class RemoteClient {
	public static void main(String[] args) {
		//RemoteTesti
		Protocol aProtocol = Protocol.RMI;
		String aServiceId = "org_distributeme_speedtester_TestingService";
		String anInstanceId = "xxx";
		String aHost = "localhost";
		if (args.length>0)
			aHost = args[0];
		int aPort = 9249;
		if (args.length>1)
			aPort = Integer.parseInt(args[1]);
		ServiceDescriptor remote = new ServiceDescriptor(aProtocol, aServiceId, anInstanceId, aHost, aPort);
		System.out.println("Testing with " + remote);
		
		//--
		RemoteTestingServiceStub stub = new RemoteTestingServiceStub(remote);
		BaseClient.performTestRun(stub);
		
	}
}
