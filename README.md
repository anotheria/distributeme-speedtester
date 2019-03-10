# distributeme-speedtester

This is a utility to measure speed of distributeme (hence RMI) connections in your environment.

To execute it you need to build distributeme-speedtest with maven, and copy the resulting artifact to both target server and client, being the instances that are gonna to colaborate.

After copy login to server and start the server command:

```
java -DlocalRmiRegistryPort=9249 -DskipCentralRegistry=true  -cp distributeme-speed-tester-1.0.1-SNAPSHOT-jar-with-dependencies.jar org.distributeme.speedtester.generated.TestingServer
```
Where 9249 is a port you want the server to listen to.

Then login to client and execute there:
´´´ 
java -cp distributeme-speed-tester-1.0.1-SNAPSHOT-jar-with-dependencies.jar org.distributeme.speedtester.RemoteClient <server> <port>
´´´
