#!/bin/bash
export VERSION=1.0.1-SNAPSHOT

CLASSPATH=target/classes:etc/appdata:target/helloworld-$VERSION-jar-with-dependencies.jar
echo CLASSPATH: $CLASSPATH
java -Xmx256M -Xms64M -classpath $CLASSPATH -Dmsk.config.prefix=dev-  -Dconfigureme.defaultEnvironment=dev $*
