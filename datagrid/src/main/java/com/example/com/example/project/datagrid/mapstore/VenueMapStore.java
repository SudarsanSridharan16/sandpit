package com.example.com.example.project.datagrid.mapstore;

import com.example.projects.domain.Venue;
import com.example.projects.domain.enums.VenueTypeEnum;
import com.hazelcast.core.MapStore;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by oliverbuckley-salmon on 23/11/2016.
 */
public class VenueMapStore implements MapStore<String,Venue>{

    //Venue Table
    private static final byte[] TABLE_VENUE = Bytes.toBytes("VENUE");
    private static final byte[] CF_VENUE_DETAILS = Bytes.toBytes("VENUE_DETAILS");
    private static final byte[] CF_VENUE_ADDITIONAL_INFO = Bytes.toBytes("ADD_INFO");
    private static final byte[] CQ_VENUE_ID = Bytes.toBytes("id");
    private static final byte[] CQ_VENUE_NAME = Bytes.toBytes("name");
    private static final byte[] CQ_VENUE_TYPE = Bytes.toBytes("type");

    private final String hbaseHost = "138.68.147.208";
    private final String zookeeperHost = "138.68.147.208";
    Configuration config;
    Admin admin;
    Table table;
    private Logger logger = Logger.getLogger(InstrumentMapStore.class);

    public VenueMapStore() {
        try{
            config =  HBaseConfiguration.create();
            config.setInt("timeout", 120000);
            config.set("hbase.master", hbaseHost + ":60000");
            config.set("hbase.zookeeper.quorum",zookeeperHost);
            config.set("hbase.zookeeper.property.clientPort", "2181");

            logger.info("Trying to connect to HBase");
            Connection connection = ConnectionFactory.createConnection(config);
            TableName tname = TableName.valueOf(TABLE_VENUE);
            table = connection.getTable(tname);
            logger.info("Connected to HBase");

        }
        catch(IOException e){
            logger.error("Error connecting to HBase Server" + e.toString());
        }
    }

    public void store(String s, Venue venue) {
        byte[] pk = Bytes.toBytes(s);
        byte[] venueId = Bytes.toBytes(venue.getVenueId());
        byte[] venueName = Bytes.toBytes(venue.getName());
        byte[] venueType = Bytes.toBytes(venue.getVenueType().toString());
        Put put = new Put(pk);
        put.addImmutable(CF_VENUE_DETAILS,CQ_VENUE_ID,venueId);
        put.addImmutable(CF_VENUE_DETAILS,CQ_VENUE_NAME,venueName);
        put.addImmutable(CF_VENUE_DETAILS,CQ_VENUE_TYPE,venueType);
        logger.info("Created immutable record " + venue.toJSON());
        try {
            table.put(put);
        }
        catch(IOException e){
            logger.error("Error writing to PARTY table" + e.toString());
        }
        logger.info("Inserted immutable record" + venue.toJSON());

    }

    public void storeAll(Map<String, Venue> map) {
        for (Map.Entry<String, Venue> entry : map.entrySet())
            store(entry.getKey(), entry.getValue());

    }

    public void delete(String s) {

    }

    public void deleteAll(Collection<String> collection) {

    }

    public Venue load(String s) {
        Venue result = new Venue();
        byte[] pk = Bytes.toBytes(s);

        Get get = new Get(pk);
        Result getResult;
        try {
            get.setMaxVersions(1);
            logger.info("Getting venue with key "+s+" from HBase");
            getResult = table.get(get);
            logger.info("Got venue with key "+s+" from HBase");
            result.setVenueId(Bytes.toString(getResult.getValue(CF_VENUE_DETAILS,CQ_VENUE_ID)));
            result.setName(Bytes.toString(getResult.getValue(CF_VENUE_DETAILS,CQ_VENUE_NAME)));
            result.setVenueType(VenueTypeEnum.valueOf(Bytes.toString(getResult.getValue(CF_VENUE_DETAILS,CQ_VENUE_TYPE))));
        }
        catch(IOException e){
            logger.error("Error reading from VENUE table" + e.toString());
        }
        return result;
    }

    public Map<String, Venue> loadAll(Collection<String> keys) {
        Map<String, Venue> result = new HashMap<String, Venue>();
        logger.info("loadAll loading "+keys.size()+" venues from HBase");
        for (String key : keys) result.put(key, load(key));
        logger.info("Got "+result.size()+" venues from HBase");
        return result;
    }

    public Iterable<String> loadAllKeys() {
        Scan scan = new Scan();
        scan.setMaxVersions(1);
        HashSet<String> keys = new HashSet<String>();
        try {
            ResultScanner scanner = table.getScanner(scan);
            for (Result row : scanner) {
                keys.add(Bytes.toString(row.getRow()));
                logger.info("Getting Row "+Bytes.toString(row.getRow()));

            }
        }
        catch(IOException e){
            logger.error("Error reading all keys from VENUE table" + e.toString());
        }
        return keys;
    }
}
