package com.example.projects;

import com.example.projects.domain.Instrument;
import com.example.projects.domain.Party;
import com.example.projects.domain.Trade;
import com.example.projects.domain.Venue;
import com.example.projects.domain.enums.TradeTypeEnum;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.joda.time.DateTime;

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
        // Get the dimension cached data
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        Map<String, Venue> venuesCache = hazelcastInstance.getReplicatedMap("venue");
        Map<String, Venue> partiesCache = hazelcastInstance.getReplicatedMap("party");
        Map<String, Venue> instrumentsCache = hazelcastInstance.getReplicatedMap("instrument");

        Venue[] venues = new Venue[venuesCache.size()];
        venues = venuesCache.values().toArray(venues);
        Party[] parties = new Party[partiesCache.size()];
        parties = partiesCache.values().toArray(parties);
        Instrument[] instruments = new Instrument[instrumentsCache.size()];
        instruments = instrumentsCache.values().toArray(instruments);

        // Generate the test data

        // Coonect to Kafka
       KafkaProducer<Integer, String> producer;
        String topic = "trades";
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:32769");
        props.put("client.id", "TradeProducer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<Integer, String>(props);

        for(int i=0;i<10;i++) {
            Trade trade = new Trade();
            trade.setTradeId(UUID.randomUUID().toString());
            trade.setAltTradeIds(new HashMap<String, String>());
            trade.setExecutionVenue(getVenue(venues));
            trade.setCurrencyPair(getInstrumnet(instruments).getCurrencyPair());
            trade.setInstrument(getInstrumnet(instruments).getSymbol());
            trade.setMarketId(getVenue(venues).getName());
            trade.setOriginalTradeDate(new DateTime(new Date()));
            trade.setPrice(getPrice());
            trade.setQuantity(getSize());
            HashMap<String, Party> partyHashMap = new HashMap<String,Party>();
            Party party = getCounterParty(parties);
            partyHashMap.put("COUNTERPARTY", party);
            trade.setParties(partyHashMap);
            trade.setTradeType(TradeTypeEnum.REGULAR_TRADE);

            System.out.println(trade.toJSON());

            try {
                producer.send(new ProducerRecord<Integer, String>(topic, i, trade.toJSON())).get();
                //Map<String, Trade> tradeCache = hazelcastInstance.getMap("trade");
                //tradeCache.put(trade.getTradeId(), trade);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        //long end = System.currentTimeMillis();
        System.out.println("10K Trades created and queued in " +"seconds");

        }

    }

