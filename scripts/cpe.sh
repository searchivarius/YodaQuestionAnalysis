#!/bin/bash
DescPath=$1
if [ "$DescPath" = "" ] ; then
  echo "Specify a descriptor!"
  exit 1
fi
if [ ! -f "$DescPath" ] ; then
  echo "'$DescPath' is not a file!"
  exit 1
fi
MEM_SIZE_MX_KB=`free|grep Mem|awk '{print $2}'`
MEM_SIZE_MIN_KB=$((3*$MEM_SIZE_MX_KB/4))
export MAVEN_OPTS="-Xms${MEM_SIZE_MIN_KB}k -Xmx${MEM_SIZE_MX_KB}k -server"
mvn compile exec:java -Dexec.mainClass=org.apache.uima.examples.cpe.SimpleRunCPE -Dexec.args="$DescPath"
if [ "$?" != "0" ] ; then
  echo "Annotation process failed!"
  exit 1
fi
echo "Annotation seem to have finished successfully!"
exit 0
