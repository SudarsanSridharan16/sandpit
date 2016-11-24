package com.example.com.example.project.datagrid.mapstore;

import com.example.projects.domain.Instrument;
import com.example.projects.domain.enums.CurrencyPairEnum;
import com.example.projects.domain.enums.FXInstrumentEnum;
import com.example.projects.domain.enums.TenorEnum;
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
 * Created by oliverbuckley-salmon on 17/11/2016.
 */
public class InstrumentMapStore implements MapStore<String, Instrument> {

    private final byte[] tableName = Bytes.toBytes("INSTRUMENT");
    private final byte[] columnFamily = Bytes.toBytes("FX");
    private final byte[] instrumentIdQual = Bytes.toBytes("id");
    private final byte[] symbolQual = Bytes.toBytes("sym");
    private final byte[] typeQual = Bytes.toBytes("typ");
    private final byte[] tenorQual = Bytes.toBytes("ten");
    private final byte[] currencyPairQual = Bytes.toBytes("cp");
    private final String hbaseHost = "138.68.147.208";
    private final String zookeeperHost = "138.68.147.208";
    Configuration config;
    Admin admin;
    Table table;
    private Logger logger = Logger.getLogger(InstrumentMapStore.class);

    public InstrumentMapStore() {
        try{
            config =  HBaseConfiguration.create();
            config.setInt("timeout", 120000);
            config.set("hbase.master", hbaseHost + ":60000");
            config.set("hbase.zookeeper.quorum",zookeeperHost);
            config.set("hbase.zookeeper.property.clientPort", "2181");

            logger.info("Trying to connect to HBase");
            Connection connection = ConnectionFactory.createConnection(config);
            TableName tname = TableName.valueOf(tableName);
            table = connection.getTable(tname);
            logger.info("Connected to HBase");

        }
        catch(IOException e){
            logger.error("Error connecting to HBase Server" + e.toString());
        }

    }

    public void store(String s, Instrument instrument) {
        byte[] pk = Bytes.toBytes(s);
        byte[] instrumentId = Bytes.toBytes(instrument.getSymbol());
        byte[] instrumentType = Bytes.toBytes(instrument.getInstrumentType().toString());
        byte[] tenors = Bytes.toBytes(instrument.getTenor().toString());
        byte[] currencyPair = Bytes.toBytes(instrument.getCurrencyPair().toString());
        Put put = new Put(pk);
        put.addImmutable(columnFamily,instrumentIdQual,instrumentId);
        put.addImmutable(columnFamily,typeQual,instrumentType);
        put.addImmutable(columnFamily,tenorQual,tenors);
        put.addImmutable(columnFamily,currencyPairQual,currencyPair);
        logger.info("Created immutable record " + instrument.toJSON());
        try {
            table.put(put);
        }
        catch(IOException e){
            logger.error("Error writing to INSTRUMENT table" + e.toString());
        }
        logger.info("Inserted immutable record" + instrument.toJSON());

    }

    public void storeAll(Map<String, Instrument> map) {
        for (Map.Entry<String, Instrument> entry : map.entrySet())
            store(entry.getKey(), entry.getValue());
    }

    public void delete(String s) {

    }

    public void deleteAll(Collection<String> collection) {

    }

    public Instrument load(String s) {
        Instrument result = new Instrument();
        byte[] pk = Bytes.toBytes(s);

        Get get = new Get(pk);
        Result getResult;
        try {
            get.setMaxVersions(1);
            logger.info("Getting instrument with key "+s+" from HBase");
            getResult = table.get(get);
            logger.info("Got instrument with key "+s+" from HBase");
            result.setCurrencyPair(CurrencyPairEnum.valueOf(Bytes.toString(getResult.getValue(columnFamily, currencyPairQual))));
            result.setSymbol(Bytes.toString(getResult.getValue(columnFamily, instrumentIdQual)));
            result.setInstrumentType(FXInstrumentEnum.valueOf(Bytes.toString(getResult.getValue(columnFamily, typeQual))));
            result.setTenor(TenorEnum.valueOf(Bytes.toString(getResult.getValue(columnFamily, tenorQual))));

        }
        catch(IOException e){
            logger.error("Error reading from INSTRUMENT table" + e.toString());
        }
        return result;

    }

    public Map<String, Instrument> loadAll(Collection<String> keys) {
        Map<String, Instrument> result = new HashMap<String, Instrument>();
        logger.info("loadAll loading "+keys.size()+" instruments from HBase");
        for (String key : keys) result.put(key, load(key));
        logger.info("Got "+result.size()+" instruments from HBase");
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
            logger.error("Error reading all keys from INSTRUMENT table" + e.toString());
        }
        return keys;
    }
}
