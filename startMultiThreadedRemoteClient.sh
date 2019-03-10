#!/bin/sh
source version.sh
java -cp target/dime-speed-tester-$VERSION-jar-with-dependencies.jar org.distributeme.speedtester.MultiThreadedRemoteClient $*