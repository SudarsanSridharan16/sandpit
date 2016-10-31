package com.example.projects.dimensionLoaders;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.example.projects.domain.Instrument;
import com.example.projects.domain.enums.CurrencyPairEnum;
import com.example.projects.domain.enums.FXInstrumentEnum;
import com.example.projects.domain.enums.TenorEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by oliverbuckley-salmon on 16/10/2016.
 * Loads the Instrument cache with FX instruments as part of the static data load
 */
public class InstrumentLoader {

    private static final String[] currencyPairs =   {"EURUSD", "USDJPY", "GBPUSD", "USDCHF", "USDCAD", "AUDUSD", "NZDUSD",
                                                    "EURJPY", "GBPJPY", "CHFJPY", "CADJPY", "AUDJPY", "NZDJPY", "GBPCHF", "GBPAUD",
                                                    "GBPCAD", "GBPNZD", "AUDCHF", "AUDCAD", "AUDNZD"};
    private static final String[] instrumentTypes =   {"SPOT", "FORWARD", "SWAP", "NDF"};
    private static final String[] tenors =  {"SPOT", "OneM", "TwoM", "ThreeM", "SixM"};

    public static void main(String[] argv) {

        Logger LOG = LoggerFactory.getLogger(InstrumentLoader.class);
        //HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        //Map<String, Instrument> instrumentCache = hazelcastInstance.getReplicatedMap("instrument");



        Instrument instrument;
        try{
            // Initialize the Connection
            CouchbaseEnvironment env = DefaultCouchbaseEnvironment
                    .builder()
                    .connectTimeout(30000)
                    .socketConnectTimeout(50000)
                    .queryTimeout(3000)
                    .bootstrapHttpEnabled(false)
                    .build();
            Cluster cluster = CouchbaseCluster.create(env,"138.68.147.208:8091");
            Bucket bucket = cluster.openBucket("trade","XerxesUK11!");


        for (String type : instrumentTypes){
            for(String pair : currencyPairs) {
                for (String tenor : tenors) {
                    if (type == FXInstrumentEnum.SPOT.toString()) {
                        instrument = new Instrument(type + " " + pair + " " + TenorEnum.SPOT.toString(), FXInstrumentEnum.valueOf(type),
                                TenorEnum.SPOT, CurrencyPairEnum.valueOf(pair));
                    } else {
                        instrument = new Instrument(type + " " + pair + " " + tenor, FXInstrumentEnum.valueOf(type),
                                TenorEnum.valueOf(tenor), CurrencyPairEnum.valueOf(pair));
                    }
                    //instrumentCache.put(instrument.getSymbol(), instrument);
                    JsonObject inst = JsonObject.fromJson(instrument.toJSON());
                    bucket.insert(JsonDocument.create(instrument.getSymbol(), inst));
                    LOG.info("Instrument "+instrument.getSymbol()+" loaded into Couchbase and Hz");

                    }
                }

            }
            cluster.disconnect();

            }
        catch(Exception e){
            LOG.error("Error connecting to Couchbase Server", e);

        }
        //LOG.info(instrumentCache.size()+" Instruments loaded");
    }
}
