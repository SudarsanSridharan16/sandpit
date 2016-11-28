FROM java:openjdk-8-jre

ENV HZ_HOME /opt/hazelcast/
RUN mkdir -p $HZ_HOME
WORKDIR $HZ_HOME

# Add copy all dependencies
COPY lib $HZ_HOME/lib

# Add project jar
ADD datagrid-1.0-SNAPSHOT.jar $HZ_HOME/lib

# Add the cache config file, is in jar but covering all bases
ADD sandpit-cache-config.xml $HZ_HOME

# Add the log4j file, is in jar but covering all bases
ADD log4j.properties $HZ_HOME

# Add custom script to start Hz using all the jars and the config
ADD startHz.sh $HZ_HOME

RUN chmod 755 $HZ_HOME/startHz.sh

EXPOSE 5701

# Run hazelcast
CMD ./startHz.sh