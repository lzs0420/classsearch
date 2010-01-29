#!/bin/sh
JAR=`dirname $0`
echo $JAR
java -jar "$JAR/ClassSearcher.jar" $*
