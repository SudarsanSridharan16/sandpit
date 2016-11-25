package com.example.project.datagrid.mapstore;

import com.example.projects.domain.Party;
import com.example.projects.domain.enums.PartyRoleEnum;
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
public class PartyMapStore implements MapStore<String, Party> {

    private static final byte[] TABLE_PARTY = Bytes.toBytes("PARTY");
    private static final byte[] CF_PARTY_DETAILS = Bytes.toBytes("PARTY_DETAILS");
    private static final byte[] CF_PARTY_ADDITIONAL_INFO = Bytes.toBytes("ADD_INFO");
    private static final byte[] CQ_PARTY_ID = Bytes.toBytes("id");
    private static final byte[] CQ_PARTY_NAME = Bytes.toBytes("name");
    private static final byte[] CQ_PARTY_ROLE = Bytes.toBytes("partyRole");

    private final String hbaseHost = "138.68.147.208";
    private final String zookeeperHost = "138.68.147.208";
    Configuration config;
    Admin admin;
    Table table;
    private Logger logger = Logger.getLogger(PartyMapStore.class);

    public PartyMapStore() {
        try{
            config =  HBaseConfiguration.create();
            config.setInt("timeout", 120000);
            config.set("hbase.master", hbaseHost + ":60000");
            config.set("hbase.zookeeper.quorum",zookeeperHost);
            config.set("hbase.zookeeper.property.clientPort", "2181");

            logger.info("Trying to connect to HBase");
            Connection connection = ConnectionFactory.createConnection(config);
            TableName tname = TableName.valueOf(TABLE_PARTY);
            table = connection.getTable(tname);
            logger.info("Connected to HBase");

        }
        catch(IOException e){
            logger.error("Error connecting to HBase Server" + e.toString());
        }
    }

    public void store(String s, Party party) {
        byte[] pk = Bytes.toBytes(s);
        byte[] partyId = Bytes.toBytes(party.getPartyId());
        byte[] partyName = Bytes.toBytes(party.getPartyName());
        byte[] partyRole = Bytes.toBytes(party.getPartyRole().toString());
        Put put = new Put(pk);
        put.addImmutable(CF_PARTY_DETAILS,CQ_PARTY_ID,partyId);
        put.addImmutable(CF_PARTY_DETAILS,CQ_PARTY_NAME,partyName);
        put.addImmutable(CF_PARTY_DETAILS,CQ_PARTY_ROLE,partyRole);
        logger.info("Created immutable record " + party.toJSON());
        try {
            table.put(put);
        }
        catch(IOException e){
            logger.error("Error writing to PARTY table" + e.toString());
        }
        logger.info("Inserted immutable record" + party.toJSON());

    }

    public void storeAll(Map<String, Party> map) {
        for (Map.Entry<String, Party> entry : map.entrySet())
            store(entry.getKey(), entry.getValue());
    }

    public void delete(String s) {

    }

    public void deleteAll(Collection<String> collection) {

    }

    public Party load(String s) {
        Party result = new Party();
        byte[] pk = Bytes.toBytes(s);

        Get get = new Get(pk);
        Result getResult;
        try {
            get.setMaxVersions(1);
            logger.info("Getting party with key "+s+" from HBase");
            getResult = table.get(get);
            logger.info("Got party with key "+s+" from HBase");
            result.setPartyId(Bytes.toString(getResult.getValue(CF_PARTY_DETAILS,CQ_PARTY_ID)));
            result.setPartyName(Bytes.toString(getResult.getValue(CF_PARTY_DETAILS,CQ_PARTY_NAME)));
            result.setPartyRole(PartyRoleEnum.valueOf(Bytes.toString(getResult.getValue(CF_PARTY_DETAILS,CQ_PARTY_ROLE))));
        }
        catch(IOException e){
            logger.error("Error reading from PARTY table" + e.toString());
        }
        return result;

    }

    public Map<String, Party> loadAll(Collection<String> keys) {
        Map<String, Party> result = new HashMap<String, Party>();
        logger.info("loadAll loading "+keys.size()+" parties from HBase");
        for (String key : keys) result.put(key, load(key));
        logger.info("Got "+result.size()+" parties from HBase");
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
            logger.error("Error reading all keys from PARTY table" + e.toString());
        }
        return keys;
    }
}
