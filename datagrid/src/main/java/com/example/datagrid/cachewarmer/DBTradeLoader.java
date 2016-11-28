package com.example.datagrid.cachewarmer;

import com.example.datagrid.mapstore.InstrumentMapStore;
import com.example.datagrid.mapstore.PartyMapStore;
import com.example.datagrid.mapstore.TradeMapStore;
import com.example.datagrid.mapstore.VenueMapStore;
import com.example.datagrid.projects.domain.Party;
import com.example.datagrid.projects.domain.Trade;
import com.example.datagrid.projects.domain.enums.CurrencyPairEnum;
import com.example.datagrid.projects.domain.enums.TradeTransactionTypeEnum;
import com.example.datagrid.projects.domain.enums.TradeTypeEnum;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static com.hazelcast.internal.util.ThreadLocalRandom.current;

/**
 * Created by oliverbuckley-salmon on 27/11/2016.
 */
public class DBTradeLoader {

    private static final Logger logger = Logger.getLogger(DBTradeLoader.class);

    public static void main(String[] args){

        String[] instruments = getInstrumentIds();
        String[] parties = getPartyIds();
        String[] venues = getVenueIds();
        TradeMapStore tms = new TradeMapStore();


        while(true) {
            Trade trade = new Trade();
            trade.setTradeId(UUID.randomUUID().toString());
            logger.info("Generating Trade with Id " + trade.getTradeId());
            trade.setAltTradeIds(new HashMap<String, String>());
            trade.setExecutionVenueDimId(getVenue(venues));
            trade.setInstrumentDimId(getInstrument(instruments));
            trade.setTradeTransactionType(TradeTransactionTypeEnum.NEW);
            trade.setSecondaryTradeTypeEnum(TradeTypeEnum.WAP_TRADE);
            trade.setCurrencyPair(CurrencyPairEnum.EURUSD);
            trade.setCounterPartyDimId(getCounterParty(parties));
            trade.setMarketId(getVenue(venues));
            trade.setOriginalTradeDate(new DateTime(new Date()));
            trade.setPrice(getPrice());
            trade.setQuantity(getSize());
            HashMap<String, Party> partyHashMap = new HashMap<String, Party>();
            trade.setParties(partyHashMap);
            trade.setTradeType(TradeTypeEnum.REGULAR_TRADE);
            logger.info("Inserting Trade: " + trade.toJSON());
            tms.store(trade.getTradeId(), trade);
        }

    }


    private static String getInstrument(String[] instruments){
        int rndInt = current().nextInt(0, instruments.length);
        return instruments[rndInt];

    }

    private static String getVenue(String[] venues){
        int rndInt = current().nextInt(0, venues.length);
        return venues[rndInt];

    }

    private static String getCounterParty(String[] counterParties){
        int rndInt = current().nextInt(0, counterParties.length);
        return counterParties[rndInt];

    }

    private static String getCptyBuyorSell() {
        String bsInd;
        if ((current().nextBoolean())) {
            bsInd = "SELL";
        } else {
            bsInd = "BUY";
        }
        return bsInd;

    }

    private static String getAccount(){
        return ""+(current().nextInt(1, 4))+"";
    }

    private static long getSize(){
        return current().nextLong(1000000L, 10000000L);
    }

    private static double getPrice(){
        return Math.random();
    }


    /* *********** Methods to get dimension data from cluster******* */
    private static String[] getInstrumentIds(){
        logger.info("Getting Instruments from db");
        HashSet<String> keys = (HashSet<String>)(new InstrumentMapStore().loadAllKeys());
        return keys.toArray(new String[keys.size()]);
    }

    private static String[] getPartyIds(){
        logger.info("Getting Parties from db");
        HashSet<String> keys = (HashSet<String>)(new PartyMapStore().loadAllKeys());
        return keys.toArray(new String[keys.size()]);
    }

    private static String[] getVenueIds(){
        logger.info("Getting Venues from db");
        HashSet<String> keys = (HashSet<String>)(new VenueMapStore().loadAllKeys());
        return keys.toArray(new String[keys.size()]);
    }
}
