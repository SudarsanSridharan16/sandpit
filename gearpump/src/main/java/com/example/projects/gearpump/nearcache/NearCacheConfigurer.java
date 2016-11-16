package com.example.projects.gearpump.nearcache;

import com.example.projects.domain.Instrument;
import com.example.projects.domain.Party;
import com.example.projects.domain.Venue;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.log4j.Logger;

import java.io.IOException;


/**
 * Created by oliverbuckley-salmon on 12/11/2016.
 */
public class NearCacheConfigurer {

    private final Logger logger = Logger.getLogger(NearCacheConfigurer.class);
    HazelcastInstance client;
    private String cacheName;
    private ClientConfig clientConfig;


    public NearCacheConfigurer(){

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

    }

    public IMap<String, Venue> getVenueNC(){
        IMap<String, Venue> venues;
        venues = client.getMap("venue");
        logger.info("Venue Near Cache");
        return venues;
    }

    public IMap<String, Party> getPartyNC(){
        IMap<String, Party> parties;
        parties = client.getMap("party");
        logger.info("Party Near Cache");
        return parties;
    }

    public IMap<String, Instrument> getInstrumentNC(){
        IMap<String, Instrument> instruments;
        instruments = client.getMap("instrument");
        logger.info("Instrument Near Cache");
        return instruments;
    }


}
