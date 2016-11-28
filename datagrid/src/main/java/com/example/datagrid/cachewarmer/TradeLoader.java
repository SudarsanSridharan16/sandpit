package com.example.datagrid.cachewarmer;


import com.example.datagrid.projects.domain.Instrument;
import com.example.datagrid.projects.domain.Party;
import com.example.datagrid.projects.domain.Trade;
import com.example.datagrid.projects.domain.Venue;
import com.example.datagrid.projects.domain.enums.CurrencyPairEnum;
import com.example.datagrid.projects.domain.enums.TradeTransactionTypeEnum;
import com.example.datagrid.projects.domain.enums.TradeTypeEnum;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static com.hazelcast.internal.util.ThreadLocalRandom.current;

/**
 * Created by oliverbuckley-salmon on 26/11/2016.
 */
public class TradeLoader implements Runnable{

    private final Logger logger = Logger.getLogger(TradeLoader.class);

    ClientConfig clientConfig;
    HazelcastInstance hi;

    IMap<String,Instrument> instruments;
    IMap<String,Party> parties;
    IMap<String,Venue> venues;
    IMap<String,Trade> trades;


    public TradeLoader() {

        try {
            logger.info("Connecting to Hz cluster");
            ClientConfig clientConfig = new XmlClientConfigBuilder("sandpit-client-config.xml").build();
            HazelcastInstance hi = HazelcastClient.newHazelcastClient(clientConfig);

            logger.info("Getting client instances of maps");
            instruments = hi.getMap("instrument");
            parties = hi.getMap("party");
            venues = hi.getMap("venue");
            trades = hi.getMap("trade");
        }
        catch(IOException e){
            logger.error("Error connecting to cluster and getting maps: "+e.getMessage());
        }
    }

    public static String getCptyBuyorSell() {
        String bsInd;
        if ((current().nextBoolean())) {
            bsInd = "SELL";
        } else {
            bsInd = "BUY";
        }
        return bsInd;

    }

    public static String getAccount(){
        return ""+(current().nextInt(1, 4))+"";
    }

    public static long getSize(){
        return current().nextLong(1000000L, 10000000L);
    }

    public static double getPrice(){
        return Math.random();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
        String[] instruments = getInstrumentIds();
        String[] parties = getPartyIds();
        String[] venues = getVenueIds();
        //TradeMapStore tms = new TradeMapStore();


        while(true) {
            Trade trade = new Trade();
            trade.setTradeId(UUID.randomUUID().toString());
            logger.info("Generating Trade with Id "+trade.getTradeId());
            trade.setAltTradeIds(new HashMap<String, String>());
            trade.setExecutionVenueDimId(getVenue(venues));
            trade.setTradeTransactionType(TradeTransactionTypeEnum.NEW);
            trade.setSecondaryTradeTypeEnum(TradeTypeEnum.WAP_TRADE);
            trade.setCurrencyPair(CurrencyPairEnum.EURUSD);
            trade.setInstrumentDimId(getInstrument(instruments));
            trade.setCounterPartyDimId(getCounterParty(parties));
            trade.setMarketId(getVenue(venues));
            trade.setOriginalTradeDate(new DateTime(new Date()));
            trade.setPrice(getPrice());
            trade.setQuantity(getSize());
            HashMap<String, Party> partyHashMap = new HashMap<String, Party>();
            trade.setParties(partyHashMap);
            trade.setTradeType(TradeTypeEnum.REGULAR_TRADE);
            logger.info("Inserting Trade: "+trade.toJSON());
            trades.put(trade.getTradeId(),trade);

            logger.info("TradeLoader Having a snooze");
            try {
                Thread.sleep(current().nextInt(10, 100));
            }
            catch(InterruptedException e){
                logger.error("Error while sleeping");
            }
        }

    }

    public String getInstrument(String[] instruments){
        int rndInt = current().nextInt(0, instruments.length);
        return instruments[rndInt];

    }

    public String getVenue(String[] venues){
        int rndInt = current().nextInt(0, venues.length);
        return venues[rndInt];

    }

    public String getCounterParty(String[] counterParties){
        int rndInt = current().nextInt(0, counterParties.length);
        return counterParties[rndInt];

    }

    /* *********** Methods to get dimension data from cluster******* */
    private String[] getInstrumentIds(){
        logger.info("Getting Instruments from cache");
        return instruments.keySet().toArray(new String[instruments.size()]);
    }

    private String[] getPartyIds(){
        logger.info("Getting Parties from cache");
        return parties.keySet().toArray(new String[parties.size()]);
    }

    private String[] getVenueIds(){
        logger.info("Getting Venues from cache");
        return venues.keySet().toArray(new String[venues.size()]);
    }
}
