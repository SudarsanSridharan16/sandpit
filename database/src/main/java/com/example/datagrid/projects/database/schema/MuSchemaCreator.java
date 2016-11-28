package com.example.datagrid.projects.database.schema;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by oliverbuckley-salmon on 16/11/2016.
 */
public class MuSchemaCreator {
    //Instrument Table
    private static final String TABLE_INSTRUMENT = "INSTRUMENT";
    private static final String CF_FX = "FX";
    private static final String CF_LISTED_DERIV = "LISTED_DERIV";
    private static final String CF_OTC_RATES = "OTC_RATES";
    private static final String CF_OTC_CREDIT = "OTC_CREDIT";
    private static final String CF_CASH_EQ = "CASH_EQ";
    // Party Table
    private static final String TABLE_PARTY = "PARTY";
    private static final String CF_PARTY_DETAILS = "PARTY_DETAILS";
    private static final String CF_PARTY_ADDITIONAL_INFO = "ADD_INFO";
    //Trade Table
    private static final String TABLE_TRADE = "TRADE";
    private static final String CF_TRADE_IDS = "TRADE_IDS";
    private static final String CF_TRADE_TYPES = "TRADE_TYPES";
    private static final String CF_TRADE_DATES = "TRADE_DATES";
    private static final String CF_TRADE_DIMS = "TRADE_DIMS";
    private static final String CF_TRADE_FACTS = "TRADE_FACTS";
    //Venue Table
    private static final String TABLE_VENUE = "VENUE";
    private static final String CF_VENUE_DETAILS = "VENUE_DETAILS";
    private static final String CF_VENUE_ADDITIONAL_INFO = "ADD_INFO";
    // HBase servers
    private static final String hbaseHost = "138.68.147.208";
    private static final String zookeeperHost = "138.68.147.208";

    private static final Logger logger = Logger.getLogger(MuSchemaCreator.class);

    private static void createOrOverwrite(Admin admin, HTableDescriptor table) throws IOException {
        if (admin.tableExists(table.getTableName())) {
            admin.disableTable(table.getTableName());
            admin.deleteTable(table.getTableName());
        }
        logger.info("Trying to create "+table.getTableName()+" table");
        admin.createTable(table);
        logger.info("Created "+table.getTableName()+" table");
    }

    private static void createSchemaTables(Configuration config) throws IOException {
        Connection connection = ConnectionFactory.createConnection(config);
        Admin admin = connection.getAdmin();

        logger.info("Creating INSTRUMENT table");
        logger.info("Creating table descriptor");
        HTableDescriptor table = new HTableDescriptor(TableName.valueOf(TABLE_INSTRUMENT));
        logger.info("Adding column families");
        table.addFamily(new HColumnDescriptor(CF_FX).setCompressionType(Compression.Algorithm.NONE));
        table.addFamily(new HColumnDescriptor(CF_LISTED_DERIV).setCompressionType(Compression.Algorithm.NONE));
        table.addFamily(new HColumnDescriptor(CF_OTC_CREDIT).setCompressionType(Compression.Algorithm.NONE));
        table.addFamily(new HColumnDescriptor(CF_OTC_RATES).setCompressionType(Compression.Algorithm.NONE));
        table.addFamily(new HColumnDescriptor(CF_CASH_EQ).setCompressionType(Compression.Algorithm.NONE));
        logger.info("Creating table. ");
        createOrOverwrite(admin, table);
        logger.info(" Done.");

        logger.info("Creating PARTY table");
        logger.info("Creating table descriptor");
        table = new HTableDescriptor(TableName.valueOf(TABLE_PARTY));
        logger.info("Adding column families");
        table.addFamily(new HColumnDescriptor(CF_PARTY_DETAILS).setCompressionType(Compression.Algorithm.NONE));
        table.addFamily(new HColumnDescriptor(CF_PARTY_ADDITIONAL_INFO).setCompressionType(Compression.Algorithm.NONE));
        logger.info("Creating table. ");
        createOrOverwrite(admin, table);
        logger.info(" Done.");

        logger.info("Creating TRADE table");
        logger.info("Creating table descriptor");
        table = new HTableDescriptor(TableName.valueOf(TABLE_TRADE));
        logger.info("Adding column families");
        table.addFamily(new HColumnDescriptor(CF_TRADE_IDS).setCompressionType(Compression.Algorithm.NONE));
        table.addFamily(new HColumnDescriptor(CF_TRADE_TYPES).setCompressionType(Compression.Algorithm.NONE));
        table.addFamily(new HColumnDescriptor(CF_TRADE_DATES).setCompressionType(Compression.Algorithm.NONE));
        table.addFamily(new HColumnDescriptor(CF_TRADE_DIMS).setCompressionType(Compression.Algorithm.NONE));
        table.addFamily(new HColumnDescriptor(CF_TRADE_FACTS).setCompressionType(Compression.Algorithm.NONE));
        logger.info("Creating table. ");
        createOrOverwrite(admin, table);
        logger.info(" Done.");

        logger.info("Creating VENUE Table");
        logger.info("Creating table descriptor");
        table = new HTableDescriptor(TableName.valueOf(TABLE_VENUE));
        logger.info("Adding column families");
        table.addFamily(new HColumnDescriptor(CF_VENUE_DETAILS).setCompressionType(Compression.Algorithm.NONE));
        table.addFamily(new HColumnDescriptor(CF_VENUE_ADDITIONAL_INFO).setCompressionType(Compression.Algorithm.NONE));


        logger.info("Creating table. ");
        createOrOverwrite(admin, table);
        logger.info(" Done.");


    }

    public static void main(String... args) throws IOException {
        logger.info("Creating config");
        Configuration config =  HBaseConfiguration.create();
        config.setInt("timeout", 120000);
        config.set("hbase.master", hbaseHost + ":60000");
        config.set("hbase.zookeeper.quorum",zookeeperHost);
        config.set("hbase.zookeeper.property.clientPort", "2181");
        logger.info("Created config");
        createSchemaTables(config);

    }


}
