#!/usr/bin/env bash
export GEARPUMP_HOME=/Users/oliverbuckley-salmon/devApps/gearpump-2.11-0.8.1

docker run -d \
 -h master0 --name master0 \
 -v $GEARPUMP_HOME:/opt/gearpump \
 -e JAVA_OPTS=-Dgearpump.cluster.masters.0=master0:3000 \
 -p 8090:8090 \
 stanleyxu2005/gearpump-launcher \
 master -ip master0 -port 3000

docker run -d \
 --link master0 \
 -v $GEARPUMP_HOME:/opt/gearpump \
 -e JAVA_OPTS=-Dgearpump.cluster.masters.0=master0:3000 \
 stanleyxu2005/gearpump-launcher \
 worker

 docker run -d \
 --link master0 \
 -v $GEARPUMP_HOME:/opt/gearpump \
 -e JAVA_OPTS=-Dgearpump.cluster.masters.0=master0:3000 \
 stanleyxu2005/gearpump-launcher \
 worker

docker exec master0 gear info
