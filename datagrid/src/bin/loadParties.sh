#!/usr/bin/env bash

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home
export CLASS_PATH=/Users/oliverbuckley-salmon/.m2/repository/com/hazelcast/hazelcast/3.6.1/hazelcast-3.6.1.jar:/Users/oliverbuckley-salmon/IdeaProjects/sandpit/domain/target/domain-1.0-SNAPSHOT.jar:/Users/oliverbuckley-salmon/IdeaProjects/sandpit/datagrid/target/datagrid-1.0-SNAPSHOT.jar
export JVM_OPTS="-server -XX:+UseG1GC"
export HZ_OPTS="-Dhazelcast.config=/Users/oliverbuckley-salmon/IdeaProjects/sandpit/datagrid/src/resources/sandpit-cache-config.xml"

$JAVA_HOME/bin/java $JVM_OPTS -cp $CLASS_PATH $HZ_OPTS com.example.projects.dimensionLoaders.PartyLoader

