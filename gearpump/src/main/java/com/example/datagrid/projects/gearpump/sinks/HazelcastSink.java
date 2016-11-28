package com.example.datagrid.projects.gearpump.sinks;

import com.example.datagrid.projects.domain.Trade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.gearpump.Message;
import org.apache.gearpump.streaming.sink.DataSink;
import org.apache.gearpump.streaming.task.TaskContext;
import org.apache.log4j.Logger;

import java.io.IOException;


/**
 * Created by oliverbuckley-salmon on 14/10/2016.
 */
public class HazelcastSink implements DataSink{

    private final Logger logger = Logger.getLogger(HazelcastSink.class);
    HazelcastInstance client;
    IMap<String, Trade> trades;
    private ClientConfig clientConfig;

    public void open(TaskContext taskContext) {
        logger.info("Creating Client Config");
        try {
            logger.info("Creating Client Config");
            clientConfig = new XmlClientConfigBuilder("sandpit-client-config.xml").build();
            clientConfig.setProperty("hazelcast.logging.type", "log4j");
            logger.info("Created Client Config");

            logger.info("Creating Client Instance");
            client = HazelcastClient.newHazelcastClient(clientConfig);
            logger.info("Creating Client Instance");
        }
        catch (IOException e){
            logger.error("Client Config not created" + e.getMessage());
        }
        logger.info("Created Client Config");

        trades = client.getMap("trade");
    }

    public void write(Message message) {
        Gson gson = new GsonBuilder().create();
        Trade trade = gson.fromJson(message.msg().toString(), Trade.class);
        trades.put(trade.getTradeId(),trade);
    }

    public void close() {
        client.shutdown();
    }
}
