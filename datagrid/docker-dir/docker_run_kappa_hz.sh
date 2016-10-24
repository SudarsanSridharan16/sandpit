#!/usr/bin/env bash

docker run -e JAVA_OPTS="-Dhazelcast.rest.enabled=true -Dhazelcast.config=/opt/hazelcast/sandpit-cache-config.xml" -t -i olibs/kappahz:v1