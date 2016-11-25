package com.example.dimensioncacheloaders;

import com.example.project.datagrid.mapstore.InstrumentMapStore;
import com.example.project.datagrid.mapstore.PartyMapStore;
import com.example.project.datagrid.mapstore.VenueMapStore;
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
import java.util.HashSet;
import java.util.Map;

/**
 * Created by oliverbuckley-salmon on 22/11/2016.
 */
public class DimensionLoader {


    public static void main(String[] args){
        Logger logger = Logger.getLogger(DimensionLoader.class);
        try {
            logger.info("Connecting to Hz cluster");
            ClientConfig clientConfig = new XmlClientConfigBuilder("sandpit-client-config.xml").build();
            HazelcastInstance hi = HazelcastClient.newHazelcastClient(clientConfig);

            logger.info("Getting client instances of maps");
            IMap<String,Instrument> instruments = hi.getMap("instrument");
            IMap<String,Party> parties = hi.getMap("party");
            IMap<String,Venue> venues = hi.getMap("venue");

            logger.info("Clearing caches");
            instruments.clear();
            parties.clear();
            venues.clear();

            logger.info("Loading instruments into cache");
            InstrumentMapStore ims = new InstrumentMapStore();
            HashSet<String> iKeys = (HashSet<String>) ims.loadAllKeys();
            logger.info("Got "+iKeys.size()+" instrument keys");
            Map<String, Instrument> instToLoad = ims.loadAll(iKeys);
            instruments.putAll(instToLoad);

            logger.info("Loading parties into cache");
            PartyMapStore pms = new PartyMapStore();
            HashSet<String> pKeys = (HashSet<String>) pms.loadAllKeys();
            logger.info("Got "+pKeys.size()+" party keys");
            Map<String,Party> partToLoad = pms.loadAll(pKeys);
            parties.putAll(partToLoad);

            logger.info("Loading venues into cache");
            VenueMapStore vms = new VenueMapStore();
            HashSet<String> vKeys = (HashSet<String>) vms.loadAllKeys();
            logger.info("Got "+vKeys.size()+" venue keys");
            Map<String,Venue> venToLoad = vms.loadAll(vKeys);
            venues.putAll(venToLoad);

            logger.info("Having a snooze to let the near and cluster caches catch up ");
            try {
                Thread.sleep(60000);
            }
            catch(InterruptedException e){
                logger.error("Error while sleeping");
            }
            logger.info("All dimensions loaded, shutting down");
            hi.shutdown();
        }
        catch (IOException e){
            logger.error("Exception loading caches: "+e.getMessage());
        }
    }
}
