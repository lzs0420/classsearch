#!/bin/sh

LPDIR=`dirname $0`
echo $LPDIR
cd $LPDIR
java -jar ClassSearcher.jar -gui 

