FROM hazelcast/hazelcast:3.7.2

# Add the fat jar with all dependencies
ADD dimCacheLoader-1.0-SNAPSHOT-jar-with-dependencies.jar $HZ_HOME
# Add custom script to start Hz using all the jars and the config
ADD startHz.sh $HZ_HOME

RUN chmod 755 $HZ_HOME/startHz.sh

# Run hazelcast
CMD ./startHz.sh