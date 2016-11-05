FROM hazelcast/hazelcast:3.7.2
# Add custom hazelcast.xml
ADD sandpit-cache-config.xml $HZ_HOME
# Add application jars
ADD log4j.properties $HZ_HOME
ADD datagrid-1.0-SNAPSHOT.jar $HZ_HOME
# Add custom script to start Hz using all the jars and the config
ADD startHz.sh $HZ_HOME
# Add all the zookeeper and curator dependencies
COPY ./lib $HZ_HOME/lib
RUN chmod 755 $HZ_HOME/startHz.sh

# Run hazelcast
CMD ./startHz.sh