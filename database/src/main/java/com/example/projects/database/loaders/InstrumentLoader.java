package com.example.projects.database.loaders;


import com.example.projects.domain.Instrument;
import com.example.projects.domain.enums.CurrencyPairEnum;
import com.example.projects.domain.enums.FXInstrumentEnum;
import com.example.projects.domain.enums.TenorEnum;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.UUID;

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
    private static final byte[] tableName = Bytes.toBytes("INSTRUMENT");
    private static final byte[] columnFamily = Bytes.toBytes("FX");
    private static final byte[] instrumentIdQual = Bytes.toBytes("id");
    private static final byte[] symbolQual = Bytes.toBytes("sym");
    private static final byte[] typeQual = Bytes.toBytes("typ");
    private static final byte[] tenorQual = Bytes.toBytes("ten");
    private static final byte[] currencyPairQual = Bytes.toBytes("cp");
    private static final String hbaseHost = "138.68.147.208";
    private static final String zookeeperHost = "138.68.147.208";

    public static void main(String[] argv) {


        Logger logger = Logger.getLogger(InstrumentLoader.class);
        Instrument instrument = new Instrument();




        try{

            Configuration hBaseConfig =  HBaseConfiguration.create();
            hBaseConfig.setInt("timeout", 120000);
            hBaseConfig.set("hbase.master", hbaseHost + ":60000");
            hBaseConfig.set("hbase.zookeeper.quorum",zookeeperHost);
            hBaseConfig.set("hbase.zookeeper.property.clientPort", "2181");

            logger.info("Trying to connect to HBase");
            Connection connection = ConnectionFactory.createConnection(hBaseConfig);
            Admin admin = connection.getAdmin();
            logger.info("Master Info Port: "+admin.getMasterInfoPort());
            logger.info("Connected to HBase");

            //Create the instrument dimension static for FX
            TableName tname = TableName.valueOf(tableName);
            Table t = connection.getTable(tname);



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
                        byte[] pk = Bytes.toBytes(UUID.randomUUID().toString());
                        byte[] instrumentid = Bytes.toBytes(instrument.getSymbol());
                        byte[] instrumentType = Bytes.toBytes(instrument.getInstrumentType().toString());
                        byte[] tenors = Bytes.toBytes(instrument.getTenor().toString());
                        byte[] currencyPair = Bytes.toBytes(instrument.getCurrencyPair().toString());
                        Put put = new Put(pk);
                        put.addImmutable(columnFamily,instrumentIdQual,instrumentid);
                        put.addImmutable(columnFamily,typeQual,instrumentType);
                        put.addImmutable(columnFamily,tenorQual,tenors);
                        put.addImmutable(columnFamily,currencyPairQual,currencyPair);
                        logger.info("Created immutable record " + instrument.toJSON());
                        t.put(put);
                        logger.info("Inserted immutable record" + instrument.toJSON());

                    }
                }



            }


        }
        catch(IOException e){



            logger.error("Error connecting to HBase Server" + e.toString());

        }

    }




}
