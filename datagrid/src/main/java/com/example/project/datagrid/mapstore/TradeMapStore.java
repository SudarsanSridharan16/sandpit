package com.example.project.datagrid.mapstore;

import com.example.projects.domain.Trade;
import com.example.projects.domain.enums.CurrencyPairEnum;
import com.example.projects.domain.enums.TradeTransactionTypeEnum;
import com.example.projects.domain.enums.TradeTypeEnum;
import com.hazelcast.core.MapStore;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by oliverbuckley-salmon on 17/11/2016.
 */
public class TradeMapStore implements MapStore<String, Trade> {

    private static final String TABLE_TRADE = "TRADE";
    private static final byte[] CF_TRADE_IDS = Bytes.toBytes("TRADE_IDS");
    private static final byte[] CF_TRADE_TYPES = Bytes.toBytes("TRADE_TYPES");
    private static final byte[] CF_TRADE_DATES = Bytes.toBytes("TRADE_DATES");
    private static final byte[] CF_TRADE_DIMS = Bytes.toBytes("TRADE_DIMS");
    private static final byte[] CF_TRADE_FACTS = Bytes.toBytes("TRADE_FACTS");

    private static final byte[] CQ_TRADE_ID = Bytes.toBytes("id");
    private static final byte[] CQ_TRADE_TRANS_TYPE = Bytes.toBytes("tradeTransType");
    private static final byte[] CQ_TRADE_TYPE = Bytes.toBytes("tradeType");
    private static final byte[] CQ_SEC_TRADE_TYPE = Bytes.toBytes("secTradeType");
    private static final byte[] CQ_ORIG_TRADE_DATE = Bytes.toBytes("origTradeDate");
    private static final byte[] CQ_MARKET  = Bytes.toBytes("marketId");
    private static final byte[] CQ_QUANTITY = Bytes.toBytes("qty");
    private static final byte[] CQ_PRICE = Bytes.toBytes("price");
    private static final byte[] CQ_CURRENCY_PAIR = Bytes.toBytes("currencyPair");
    // Dimension Ids
    private static final byte[]  CQ_INSTRUMENT_ID = Bytes.toBytes("instrumentId");
    private static final byte[]  CQ_EXEC_VENUE_ID = Bytes.toBytes("executionVenueId");
    private static final byte[]  CQ_CP_ID = Bytes.toBytes("counterpartyId");

    private final String hbaseHost = "138.68.147.208";
    private final String zookeeperHost = "138.68.147.208";
    Configuration config;
    Admin admin;
    Table table;

    private Logger logger = Logger.getLogger(TradeMapStore.class);

    public TradeMapStore() {
        try{
            config =  HBaseConfiguration.create();
            config.setInt("timeout", 120000);
            config.set("hbase.master", hbaseHost + ":60000");
            config.set("hbase.zookeeper.quorum",zookeeperHost);
            config.set("hbase.zookeeper.property.clientPort", "2181");

            logger.info("Trying to connect to HBase");
            Connection connection = ConnectionFactory.createConnection(config);
            TableName tname = TableName.valueOf(TABLE_TRADE);
            table = connection.getTable(tname);
            logger.info("Connected to HBase");

        }
        catch(IOException e){
            logger.error("Error connecting to HBase Server" + e.toString());
        }
    }

    public void store(String s, Trade trade) {
        byte[] pk = Bytes.toBytes(s);
        byte[] tradeId = Bytes.toBytes(trade.getTradeId());
        byte[] tradeTransType = Bytes.toBytes(trade.getTradeTransactionType().toString());
        byte[] tradeType = Bytes.toBytes(trade.getTradeType().toString());
        byte[] secTradeType = Bytes.toBytes(trade.getSecondaryTradeTypeEnum().toString());
        byte[] tradeDate = Bytes.toBytes(trade.getOriginalTradeDate().getMillis());
        byte[] market = Bytes.toBytes(trade.getMarketId());
        byte[] quantity = Bytes.toBytes(trade.getQuantity());
        byte[] price = Bytes.toBytes(trade.getPrice());
        byte[] currencyPair = Bytes.toBytes(trade.getCurrencyPair().toString());
        byte[] instrumentId = Bytes.toBytes(trade.getInstrumentDimId());
        byte[] venueId = Bytes.toBytes(trade.getExecutionVenueDimId());
        byte[] partyId = Bytes.toBytes(trade.getCounterPartyDimId());

        Put put = new Put(pk);
        put.addImmutable(CF_TRADE_IDS,CQ_TRADE_ID,tradeId);
        put.addImmutable(CF_TRADE_TYPES, CQ_TRADE_TYPE,tradeType);
        put.addImmutable(CF_TRADE_TYPES,CQ_SEC_TRADE_TYPE, secTradeType);
        put.addImmutable(CF_TRADE_TYPES, CQ_TRADE_TRANS_TYPE,tradeTransType);
        put.addImmutable(CF_TRADE_DATES, CQ_ORIG_TRADE_DATE,tradeDate);
        put.addImmutable(CF_TRADE_IDS, CQ_MARKET, market);
        put.addImmutable(CF_TRADE_FACTS, CQ_QUANTITY,quantity);
        put.addImmutable(CF_TRADE_FACTS,CQ_PRICE, price);
        put.addImmutable(CF_TRADE_IDS, CQ_CURRENCY_PAIR, currencyPair);
        put.addImmutable(CF_TRADE_DIMS, CQ_INSTRUMENT_ID, instrumentId);
        put.addImmutable(CF_TRADE_DIMS, CQ_EXEC_VENUE_ID, venueId);
        put.addImmutable(CF_TRADE_DIMS, CQ_CP_ID, partyId);

        logger.info("Created immutable record " + trade.toJSON());
        try {
            table.put(put);
        }
        catch(IOException e){
            logger.error("Error writing to TRADE table" + e.toString());
        }
        logger.info("Inserted immutable record" + trade.toJSON());

    }

    public void storeAll(Map<String, Trade> map) {
        for (Map.Entry<String, Trade> entry : map.entrySet())
            store(entry.getKey(), entry.getValue());

    }

    public void delete(String s) {

    }

    public void deleteAll(Collection<String> collection) {

    }

    public Trade load(String s) {

        Trade result = new Trade();
        byte[] pk = Bytes.toBytes(s);

        Get get = new Get(pk);
        Result getResult;
        try {
            get.setMaxVersions(1);
            logger.info("Getting trade with key "+s+" from HBase");
            getResult = table.get(get);
            logger.info("Got trade with key "+s+" from HBase");
            result.setTradeId(Bytes.toString(getResult.getValue(CF_TRADE_IDS,CQ_TRADE_ID)));
            result.setTradeTransactionType(TradeTransactionTypeEnum.valueOf(Bytes.toString(getResult.getValue(CF_TRADE_TYPES,CQ_TRADE_TRANS_TYPE))));
            result.setTradeType(TradeTypeEnum.valueOf(Bytes.toString(getResult.getValue(CF_TRADE_TYPES,CQ_TRADE_TYPE))));
            result.setSecondaryTradeTypeEnum(TradeTypeEnum.valueOf(Bytes.toString(getResult.getValue(CF_TRADE_TYPES,CQ_SEC_TRADE_TYPE))));
            result.setOriginalTradeDate(new DateTime(Bytes.toLong(getResult.getValue(CF_TRADE_DATES, CQ_ORIG_TRADE_DATE))));
            result.setMarketId(Bytes.toString(getResult.getValue(CF_TRADE_IDS, CQ_MARKET)));
            result.setQuantity(Bytes.toDouble(getResult.getValue(CF_TRADE_FACTS,CQ_QUANTITY)));
            result.setPrice(Bytes.toDouble(getResult.getValue(CF_TRADE_FACTS, CQ_PRICE)));
            result.setCounterPartyDimId(Bytes.toString(getResult.getValue(CF_TRADE_DIMS,CQ_CP_ID)));
            result.setExecutionVenueDimId(Bytes.toString(getResult.getValue(CF_TRADE_DIMS,CQ_EXEC_VENUE_ID)));
            result.setInstrumentDimId(Bytes.toString(getResult.getValue(CF_TRADE_DIMS,CQ_INSTRUMENT_ID)));
            result.setCurrencyPair(CurrencyPairEnum.valueOf(Bytes.toString(getResult.getValue(CF_TRADE_DIMS, CQ_CURRENCY_PAIR))));


        }
        catch(IOException e){
            logger.error("Error reading from VENUE table" + e.toString());
        }
        return result;
    }

    public Map<String, Trade> loadAll(Collection<String> keys) {

        Map<String, Trade> result = new HashMap<String, Trade>();
        logger.info("loadAll loading "+keys.size()+" venues from HBase");
        for (String key : keys) result.put(key, load(key));
        logger.info("Got "+result.size()+" Trades from HBase");
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
            logger.error("Error reading all keys from TRADE table" + e.toString());
        }
        return keys;
    }
}
