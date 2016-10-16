package com.example.projects;

import com.example.projects.domain.Trade;
import com.example.projects.domain.Venue;
import com.example.projects.domain.enums.CurrencyEnum;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;


import static com.hazelcast.internal.util.ThreadLocalRandom.current;


/**
 * Hello world!
 *
 */
public class TradeGenerator
{
    static final String[] currency = {"EUR", "GBP", "USD", "CAD", "AUD", "CNY"};
    static final String[] venues = {"FXALL", "LMAX", "", "Currenex", "Bloomberg", "ICAP", "Tullet"};
    static final String[] counterParties = {"Blackrock", "GSAM", "UBSGAM", "Fidelity", "Bloomberg", "ICAP", "Tullet"};


    public static String getCurrency(){
        int rndInt = current().nextInt(0, currency.length);
        return currency[rndInt];

    }

    public static String getVenue(){
        int rndInt = current().nextInt(0, venues.length);
        return venues[rndInt];

    }

    public static String getCounterParty(){
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

        // Generate the test data

        // Coonect to Kafka
       KafkaProducer<Integer, String> producer;
        String topic = "trades";
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("client.id", "TradeProducer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<Integer, String>(props);

        for(int i=0;i<10000;i++) {
            Trade trade = new Trade();
            trade.setTradeId(UUID.randomUUID().toString());
            trade.setCurrency(CurrencyEnum.valueOf(getCurrency()));
            trade.setAltTradeIds(new HashMap<String, String>());
            Venue venue = new Venue();
            venue.setName(getVenue());
            trade.setExecutionVenue(venue);

        /*


            try {
                producer.send(new ProducerRecord<Integer, String>(topic, 1, trade.toJSON())).get();

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("10K Trades created and queued in "+ ((end-start)/1000) +"seconds");
        */
        }

    }
}
