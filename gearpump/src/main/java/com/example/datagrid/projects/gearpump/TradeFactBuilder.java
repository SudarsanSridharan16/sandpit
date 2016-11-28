package com.example.datagrid.projects.gearpump;


import com.example.datagrid.projects.gearpump.sinks.HazelcastSink;
import com.example.datagrid.projects.gearpump.tasks.InstrumentLookUpper;
import com.example.datagrid.projects.gearpump.tasks.PartyLookUpper;
import com.example.datagrid.projects.gearpump.tasks.TradeHydrator;
import com.example.datagrid.projects.gearpump.tasks.VenueLookUpper;
import org.apache.gearpump.cluster.UserConfig;
import org.apache.gearpump.cluster.client.ClientContext;
import org.apache.gearpump.partitioner.Partitioner;
import org.apache.gearpump.partitioner.ShufflePartitioner;
import org.apache.gearpump.streaming.javaapi.Graph;
import org.apache.gearpump.streaming.javaapi.Processor;
import org.apache.gearpump.streaming.javaapi.StreamApplication;
import org.apache.gearpump.streaming.kafka.KafkaSource;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Hello world!
 *
 */
public class TradeFactBuilder
{


    public static void main( String[] args )
    {
        final Logger logger = Logger.getLogger(TradeFactBuilder.class);
        ClientContext context = ClientContext.apply();

        UserConfig appConfig = UserConfig.empty();

        // we will create two kafka reader task to read from kafka queue.
        int sourceNum = 2;

        final String zookeeperConnect = "138.68.172.212:2181";


        Properties props = new Properties();
        props.put("bootstrap.servers", zookeeperConnect);
        props.put("client.id", "TradeConsumer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        logger.info("Creating Kafka Source");
        KafkaSource source = new KafkaSource("trade",props);

        logger.info("Creating Kafka Source Processor");
        Processor sourceProcessor = Processor.source(source, sourceNum, "kafka_source", appConfig, context.system());

        logger.info("Creating Hz Sink");
        Processor sinkProcessor = new Processor(HazelcastSink.class, 2, "hz_sink", appConfig);

        logger.info("Creating Tasks");
        Processor partyProcessor = new Processor(PartyLookUpper.class,3);
        Processor venueProcessor = new Processor(VenueLookUpper.class,3);
        Processor instrumentProcessor = new Processor(InstrumentLookUpper.class,3);
        Processor tradeProcessor = new Processor(TradeHydrator.class,3);

        Partitioner shuffle = new ShufflePartitioner();

        logger.info("Creating graph");
        Graph graph = new Graph();
        graph.addVertex(sourceProcessor);
        graph.addVertex(sinkProcessor);
        graph.addVertex(partyProcessor);
        graph.addVertex(venueProcessor);
        graph.addVertex(instrumentProcessor);
        graph.addVertex(tradeProcessor);

        graph.addEdge(sourceProcessor, shuffle, tradeProcessor);
        graph.addEdge(tradeProcessor, shuffle, partyProcessor);
        graph.addEdge(partyProcessor, shuffle, venueProcessor);
        graph.addEdge(venueProcessor, shuffle, instrumentProcessor);
        graph.addEdge(instrumentProcessor, shuffle, sinkProcessor);

        logger.info("Creating streaming application");
        StreamApplication app = new StreamApplication("TradeServingLayerLoader", appConfig, graph);

        logger.info("Submitting app");
        context.submit(app);

        // clean resource
        context.close();


    }
}
