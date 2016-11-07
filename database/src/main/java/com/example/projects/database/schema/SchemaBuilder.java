package com.example.projects.database.schema;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by oliverbuckley-salmon on 07/11/2016.
 */
public class SchemaBuilder {

    private static final String TABLE_INSTRUMENT = "INSTRUMENT";
    private static final String CF_FX = "FX";
    private static final String CF_LISTED_DERIV = "LISTED_DERIV";
    private static final String CF_OTC_RATES = "OTC_RATES";
    private static final String CF_OTC_CREDIT = "OTC_CREDIT";
    private static final String hbaseHost = "138.68.147.208";
    private static final String zookeeperHost = "138.68.147.208";

    private static final Logger logger = Logger.getLogger(SchemaBuilder.class);


    public static void createOrOverwrite(Admin admin, HTableDescriptor table) throws IOException {
        if (admin.tableExists(table.getTableName())) {
            admin.disableTable(table.getTableName());
            admin.deleteTable(table.getTableName());
        }
        logger.info("Trying to create INSTRUMENT table");
        admin.createTable(table);
        logger.info("Created INSTRUMENT table");
    }

    public static void createSchemaTables(Configuration config) throws IOException {
        Connection connection = ConnectionFactory.createConnection(config);
             Admin admin = connection.getAdmin();
            logger.info("Creating table descriptor");
            HTableDescriptor table = new HTableDescriptor(TableName.valueOf(TABLE_INSTRUMENT));
            logger.info("Adding column families");
            table.addFamily(new HColumnDescriptor(CF_FX).setCompressionType(Algorithm.NONE));
            table.addFamily(new HColumnDescriptor(CF_LISTED_DERIV).setCompressionType(Algorithm.NONE));
            table.addFamily(new HColumnDescriptor(CF_OTC_CREDIT).setCompressionType(Algorithm.NONE));
            table.addFamily(new HColumnDescriptor(CF_OTC_RATES).setCompressionType(Algorithm.NONE));

            logger.info("Creating table. ");
            createOrOverwrite(admin, table);
            logger.info(" Done.");

    }

    public static void modifySchema (Configuration config) throws IOException {
        Connection connection = ConnectionFactory.createConnection(config);
             Admin admin = connection.getAdmin();
            logger.info("Checking if table exists");
            TableName tableName = TableName.valueOf(TABLE_INSTRUMENT);
            if (!admin.tableExists(tableName)) {
                logger.info("Table does not exist.");
                System.exit(-1000);
            }

            HTableDescriptor table = admin.getTableDescriptor(tableName);

            // Update existing table
            HColumnDescriptor newColumn = new HColumnDescriptor("CASH_EQ");
            newColumn.setCompactionCompressionType(Algorithm.GZ);
            newColumn.setMaxVersions(HConstants.ALL_VERSIONS);
            admin.addColumn(tableName, newColumn);

            // Update existing column family
            HColumnDescriptor existingColumn = new HColumnDescriptor(CF_FX);
            existingColumn.setCompactionCompressionType(Algorithm.GZ);
            existingColumn.setMaxVersions(HConstants.ALL_VERSIONS);
            table.modifyFamily(existingColumn);
            admin.modifyTable(tableName, table);



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
        modifySchema(config);
    }
}
