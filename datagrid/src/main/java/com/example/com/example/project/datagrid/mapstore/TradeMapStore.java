package com.example.com.example.project.datagrid.mapstore;

import com.example.projects.domain.Trade;
import com.hazelcast.core.MapStore;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Collection;
import java.util.Map;

/**
 * Created by oliverbuckley-salmon on 17/11/2016.
 */
public class TradeMapStore implements MapStore<String, Trade> {

    private static final String TABLE_TRADE = "TRADE";
    private static final byte[] CF_TRADE_IDS = Bytes.toBytes("TRADE_IDS");
    private static final byte[] CQ_TRADE_ID = Bytes.toBytes("tradeId");
    private static final byte[] CF_TRADE_TYPES = Bytes.toBytes("TRADE_TYPES");
    private static final byte[] CF_TRADE_DATES = Bytes.toBytes("TRADE_DATES");
    private static final byte[] CF_TRADE_DIMS = Bytes.toBytes("TRADE_DIMS");
    private static final byte[] CF_TRADE_FACTS = Bytes.toBytes("TRADE_FACTS");

    public void store(String s, Trade trade) {

    }

    public void storeAll(Map<String, Trade> map) {

    }

    public void delete(String s) {

    }

    public void deleteAll(Collection<String> collection) {

    }

    public Trade load(String s) {
        return null;
    }

    public Map<String, Trade> loadAll(Collection<String> collection) {
        return null;
    }

    public Iterable<String> loadAllKeys() {
        return null;
    }
}
