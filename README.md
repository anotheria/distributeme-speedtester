# distributeme-speedtester

This is a utility to measure speed of distributeme (hence RMI) connections in your environment.

To execute it you need to build distributeme-speedtest with maven, and copy the resulting artifact to both target server and client, being the instances that are gonna to colaborate.

After copy login to server and start the server command:

```
java -DlocalRmiRegistryPort=9249 -DskipCentralRegistry=true  -cp distributeme-speed-tester-1.0.1-SNAPSHOT-jar-with-dependencies.jar org.distributeme.speedtester.generated.TestingServer
```
Where 9249 is a port you want the server to listen to.

Then login to client and execute there:
```
java -cp distributeme-speed-tester-1.0.1-SNAPSHOT-jar-with-dependencies.jar org.distributeme.speedtester.RemoteClient <server> <port>
```
where port is the port you specified to the server for listening and host is the ip or hostname of the server.
After couple of seconds the speedtester will produce an output like this:

```
==========================
Finished Test In 9218 ServerSide: 9208
==========================
1 - echo calls, Performed 1000 in 816, 0.816 ms per call - ACCEPTABLE
2 - inbound same packet with size 1000, Performed 1000 in 656, 0.656 ms per call - ACCEPTABLE
3 - inbound new packets with size 1000, Performed 1000 in 533, 0.533 ms per call - ACCEPTABLE
4 - inbound same packet with size 10000, Performed 1000 in 398, 0.398 ms per call - OK
5 - inbound new packets with size 10000, Performed 1000 in 425, 0.425 ms per call - OK
6 - inbound same packet with size 50000, Performed 1000 in 642, 0.642 ms per call - ACCEPTABLE
7 - inbound new packets with size 50000, Performed 1000 in 856, 0.856 ms per call - ACCEPTABLE
8 - outbound packets with size 1000, Performed 1000 in 629, 0.629 ms per call - ACCEPTABLE
9 - outbound packets with size 10000, Performed 1000 in 653, 0.653 ms per call - ACCEPTABLE
10 - outbound packets with size 50000, Performed 1000 in 1484, 1.484 ms per call - LOUSY
11 - in-and-out bound packets with size 1000, Performed 1000 in 621, 0.621 ms per call - ACCEPTABLE
12 - in-and-out bound packets with size 10000, Performed 1000 in 508, 0.508 ms per call - ACCEPTABLE
13 - in-and-out bound packets with size 50000, Performed 1000 in 971, 0.971 ms per call - ACCEPTABLE
```

### Interpreting the results
as long as everything is within ACCEPTABLE, OK or TOP grades you good. 
If you have too many LOUSY or, god beware, DEAD HORSE you might reconsider your network configuration.
The above results are on the same network between 2 VMs running on same or different ESX. Now below are results of same test via network (Hamburg - Frankfurt) and couple of VPNs.

```
==========================
Finished Test In 302653 ServerSide: 302615
==========================
1 - echo calls, Performed 1000 in 19836, 19.836 ms per call - DEAD HORSE
2 - inbound same packet with size 1000, Performed 1000 in 20041, 20.041 ms per call - DEAD HORSE
3 - inbound new packets with size 1000, Performed 1000 in 21281, 21.281 ms per call - DEAD HORSE
4 - inbound same packet with size 10000, Performed 1000 in 20646, 20.646 ms per call - DEAD HORSE
5 - inbound new packets with size 10000, Performed 1000 in 20427, 20.427 ms per call - DEAD HORSE
6 - inbound same packet with size 50000, Performed 1000 in 22885, 22.885 ms per call - DEAD HORSE
7 - inbound new packets with size 50000, Performed 1000 in 22842, 22.842 ms per call - DEAD HORSE
8 - outbound packets with size 1000, Performed 1000 in 20135, 20.135 ms per call - DEAD HORSE
9 - outbound packets with size 10000, Performed 1000 in 21501, 21.501 ms per call - DEAD HORSE
10 - outbound packets with size 50000, Performed 1000 in 35250, 35.25 ms per call - DEAD HORSE
11 - in-and-out bound packets with size 1000, Performed 1000 in 20153, 20.153 ms per call - DEAD HORSE
12 - in-and-out bound packets with size 10000, Performed 1000 in 21757, 21.757 ms per call - DEAD HORSE
13 - in-and-out bound packets with size 50000, Performed 1000 in 35826, 35.826 ms per call - DEAD HORSE
```

Obviously you wouldn't want to run a distributed system over a network with 20ms call penalty.

