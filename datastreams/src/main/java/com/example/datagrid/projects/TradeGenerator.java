package com.example.datagrid.projects;

import com.example.datagrid.projects.domain.Instrument;
import com.example.datagrid.projects.domain.Party;
import com.example.datagrid.projects.domain.Trade;
import com.example.datagrid.projects.domain.Venue;
import com.example.datagrid.projects.domain.enums.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.hazelcast.internal.util.ThreadLocalRandom.current;


/**
 * Hello world!
 *
 */
public class TradeGenerator
{



    public static Instrument getInstrumnet(Instrument[] instruments){
        int rndInt = current().nextInt(0, instruments.length);
        return instruments[rndInt];

    }

    public static Venue getVenue(Venue[] venues){
        int rndInt = current().nextInt(0, venues.length);
        return venues[rndInt];

    }

    public static Party getCounterParty(Party[] counterParties){
        int rndInt = current().nextInt(0, counterParties.length);
        return counterParties[rndInt];

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


    public static void main( String[] args )
    {

        Logger LOG = LoggerFactory.getLogger(TradeGenerator.class);


        Venue[] venues = getVenues();
        Party[] parties = getParties();

        Instrument[] instruments = getInstruments();

        final int numTrades = 10;

        // Generate the test data
        LOG.info("Connecting to Kafka");
        // Coonect to Kafka
       KafkaProducer<Integer, String> producer;
        final String topic = "trades";
        Properties props = new Properties();
        props.put("bootstrap.servers", "138.68.172.212:2181");
        props.put("client.id", "TradeProducer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<Integer, String>(props);
        long start = System.currentTimeMillis();
        LOG.info("Creating trades");
        for(int i=0;i<numTrades;i++) {
            Trade trade = new Trade();
            trade.setTradeId(UUID.randomUUID().toString());
            trade.setAltTradeIds(new HashMap<String, String>());
            trade.setExecutionVenue(getVenue(venues));
            trade.setCurrencyPair(getInstrumnet(instruments).getCurrencyPair());
            trade.setInstrument(getInstrumnet(instruments));
            trade.setMarketId(getVenue(venues).getName());
            trade.setOriginalTradeDate(new DateTime(new Date()));
            trade.setPrice(getPrice());
            trade.setQuantity(getSize());
            HashMap<String, Party> partyHashMap = new HashMap<String,Party>();
            Party party = getCounterParty(parties);
            partyHashMap.put("COUNTERPARTY", party);
            trade.setParties(partyHashMap);
            trade.setTradeType(TradeTypeEnum.REGULAR_TRADE);


            LOG.info(trade.toJSON());

            try {
                producer.send(new ProducerRecord<Integer, String>(topic, i, trade.toJSON())).get();

            }
            catch (Exception e) {
                LOG.error("Error putting trades on Kafka",e);
            }
        }
        long end = System.currentTimeMillis();
        LOG.info(numTrades+" Trades created and queued in " +(end-start)/1000+"seconds");

        }

        private static Instrument[] getInstruments(){
            final String[] currencyPairs =   {"EURUSD", "USDJPY", "GBPUSD", "USDCHF", "USDCAD", "AUDUSD", "NZDUSD",
                    "EURJPY", "GBPJPY", "CHFJPY", "CADJPY", "AUDJPY", "NZDJPY", "GBPCHF", "GBPAUD",
                    "GBPCAD", "GBPNZD", "AUDCHF", "AUDCAD", "AUDNZD"};
            final String[] instrumentTypes =   {"SPOT", "FORWARD", "SWAP", "NDF"};
            final String[] tenors =  {"SPOT", "OneM", "TwoM", "ThreeM", "SixM"};
            ArrayList<Instrument> result = new ArrayList<Instrument>();
            Instrument instrument = new Instrument();

            for (String type : instrumentTypes) {

                for (String pair : currencyPairs) {
                    for (String tenor : tenors) {
                        if (type == FXInstrumentEnum.SPOT.toString()) {
                            instrument = new Instrument(type + " " + pair + " " + TenorEnum.SPOT.toString(), FXInstrumentEnum.valueOf(type),
                                    TenorEnum.SPOT, CurrencyPairEnum.valueOf(pair));
                        } else {
                            instrument = new Instrument(type + " " + pair + " " + tenor, FXInstrumentEnum.valueOf(type),
                                    TenorEnum.valueOf(tenor), CurrencyPairEnum.valueOf(pair));
                        }
                        result.add(instrument);

                    }
                }
            }

            return result.toArray(new Instrument[result.size()]);
        }

        private static Party[] getParties(){
            ArrayList<Party> result = new ArrayList<Party>();
            final String[] counterParties = {"Blackrock", "GSAM", "UBSGAM", "Fidelity", "Bloomberg", "ICAP", "Tullet"};
            for (int i = 0; i < counterParties.length; i++) {
                Party party = new Party(UUID.randomUUID().toString(), counterParties[i], PartyRoleEnum.valueOf("CLIENT"), new HashMap<String, Party>());
                result.add(party);
            }
            return result.toArray(new Party[result.size()]);
        }

        private static Venue[] getVenues(){
            ArrayList<Venue> result = new ArrayList<Venue>();
            final String[] executionVenues = {"FXALL", "LMAX", "", "Currenex", "Bloomberg", "ICAP", "Tullet", "360T", "EBS", "CME"};

            for (int i = 0; i < executionVenues.length; i++) {
                Venue venue = new Venue(UUID.randomUUID().toString(), executionVenues[i], VenueTypeEnum.valueOf("ELECTRONIC"));
                result.add(venue);
            }

            return result.toArray(new Venue[result.size()]);
        }

    }

