#!/usr/bin/env bash


java -server -classpath opt/hazelcast/lib/hazelcast-all-3.7.2.jar:/opt/hazelcast/lib/* -Dhazelcast.rest.enabled=true -Dhazelcast.config=/opt/hazelcast/sandpit-cache-config.xml -Dhazelcast.logging.type=log4j -Dlog4j.configurationFile=/opt/hazelcast/log4j.properties com.hazelcast.core.server.StartServer

