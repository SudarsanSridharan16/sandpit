package com.example.projects.database.loaders;


import com.example.projects.domain.Instrument;
import com.example.projects.domain.enums.CurrencyPairEnum;
import com.example.projects.domain.enums.FXInstrumentEnum;
import com.example.projects.domain.enums.TenorEnum;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;

import java.io.IOException;

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
    private static final String tableName = "INSTRUMMENT";
    private static final String columnFamily = "FX_INSTRUMMENT";
    private static final String hbaseHost = "138.68.147.208";
    private static final String zookeeperHost = "138.68.147.208";

    public static void main(String[] argv) {






        Instrument instrument;
        try{

            Configuration hBaseConfig =  HBaseConfiguration.create();
            hBaseConfig.setInt("timeout", 120000);
            hBaseConfig.set("hbase.master", hbaseHost + ":60000");
            hBaseConfig.set("hbase.zookeeper.quorum",zookeeperHost);
            hBaseConfig.set("hbase.zookeeper.property.clientPort", "2181");

            createSchemaTables(hBaseConfig);


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

                    }
                }

            }


            }
        catch(IOException e){

            System.out.println("Error connecting to HBase Server" + e.toString());

        }

    }

    public static void createSchemaTables(Configuration config) throws IOException {
        Connection connection = ConnectionFactory.createConnection(config);
        Admin admin = connection.getAdmin();

        HTableDescriptor table = new HTableDescriptor(TableName.valueOf(tableName));
        table.addFamily(new HColumnDescriptor(columnFamily).setCompressionType(Algorithm.NONE));

        System.out.print("Creating table. ");
        admin.createTable(table);
        System.out.println(" Done.");

    }
}
