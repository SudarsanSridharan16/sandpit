#!/usr/bin/env bash


java -server -classpath /opt/hazelcast/dimCacheLoader-1.0-SNAPSHOT-jar-with-dependencies.jar -Dhazelcast.rest.enabled=true -Dhazelcast.config=sandpit-cache-config.xml -Dhazelcast.logging.type=log4j -Dlog4j.configurationFile=log4j.properties com.hazelcast.core.server.StartServer