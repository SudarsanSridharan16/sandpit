package com.example.datagrid.projects.database.loaders;

import com.example.datagrid.projects.domain.Venue;
import com.example.datagrid.projects.domain.enums.VenueTypeEnum;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by oliverbuckley-salmon on 16/10/2016.
 * Loads the Venue cache with exceution venues as part of the static data load
 */
public class VenueLoader {
    static final String[] executionVenues = {"FXALL", "LMAX", "", "Currenex", "Bloomberg", "ICAP", "Tullet", "360T", "EBS", "CME"};
    static final Logger logger = Logger.getLogger(VenueLoader.class);
    private static final String TABLE_VENUE = "VENUE";
    private static final String CF_VENUE_DETAILS = "VENUE_DETAILS";
    private static final byte[] cfVenueDetails = Bytes.toBytes(CF_VENUE_DETAILS);
    private static final byte[] venueIdQual = Bytes.toBytes("id");
    private static final byte[] nameQual = Bytes.toBytes("name");
    private static final byte[] typeQual = Bytes.toBytes("venueType");
    private static final String hbaseHost = "138.68.147.208";
    private static final String zookeeperHost = "138.68.147.208";

    public static void main(String[] argv) {

        try {
            Configuration hBaseConfig =  HBaseConfiguration.create();
            hBaseConfig.setInt("timeout", 120000);
            hBaseConfig.set("hbase.master", hbaseHost + ":60000");
            hBaseConfig.set("hbase.zookeeper.quorum",zookeeperHost);
            hBaseConfig.set("hbase.zookeeper.property.clientPort", "2181");

            logger.info("Trying to connect to HBase");
            Connection connection = ConnectionFactory.createConnection(hBaseConfig);
            logger.info("Connected to HBase");

            //Create the Party dimension static
            Table t = connection.getTable(TableName.valueOf(TABLE_VENUE));
            for (int i = 0; i < executionVenues.length; i++) {
                Venue venue = new Venue(UUID.randomUUID().toString(), executionVenues[i], VenueTypeEnum.valueOf("ELECTRONIC"));
                byte[] pk = Bytes.toBytes(venue.getVenueId());
                byte[] venueId = pk;
                byte[] name = Bytes.toBytes(venue.getName());
                byte[] type = Bytes.toBytes(venue.getVenueType().toString());
                Put put = new Put(pk);
                put.addImmutable(cfVenueDetails,venueIdQual,venueId);
                put.addImmutable(cfVenueDetails,nameQual,name);
                put.addImmutable(cfVenueDetails,typeQual,type);

                logger.info("Created immutable record " + venue.toJSON());
                t.put(put);
                logger.info("Inserted immutable record" + venue.toJSON());

            }
        }
        catch(IOException e){
            logger.error("Error connecting to HBase Server" + e.toString());
        }

    }
}
