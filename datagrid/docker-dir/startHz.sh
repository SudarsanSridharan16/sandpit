#!/usr/bin/env bash


java -server -classpath /opt/hazelcast/hazelcast-all-3.7.1.jar:/opt/hazelcast/datagrid-1.0-SNAPSHOT.jar:/opt/hazelcast/domain-1.0-SNAPSHOT.jar:/opt/hazelcast/lib/* -Dhazelcast.rest.enabled=true -Dhazelcast.config=/opt/hazelcast/sandpit-cache-config.xml com.hazelcast.core.server.StartServer