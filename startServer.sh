#!/bin/sh
source version.sh
java -DlocalRmiRegistryPort=9249 -DskipCentralRegistry=true -cp target/distributeme-speed-tester-$VERSION-jar-with-dependencies.jar org.distributeme.speedtester.generated.TestingServer