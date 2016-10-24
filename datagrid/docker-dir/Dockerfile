FROM hazelcast/hazelcast:latest
# Add your custom hazelcast.xml
ADD sandpit-cache-config.xml $HZ_HOME
ADD domain-1.0-SNAPSHOT.jar $HZ_HOME
# Run hazelcast
CMD ./server.sh