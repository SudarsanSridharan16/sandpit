package com.example.projects.database.loaders;

import com.example.projects.domain.Party;
import com.example.projects.domain.enums.PartyRoleEnum;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by oliverbuckley-salmon on 16/10/2016.
 * Loads the Party cache with counterparties as part of the static data load
 */
public class PartyLoader {

    static final Logger logger = Logger.getLogger(PartyLoader.class);
    private static final String TABLE_PARTY = "PARTY";
    private static final String CF_PARTY_DETAILS = "PARTY_DETAILS";
    private static final String CF_PARTY_ADDITIONAL_INFO = "ADD_INFO";
    private static final byte[] cfPartyDetails = Bytes.toBytes(CF_PARTY_DETAILS);
    private static final byte[] partyIdQual = Bytes.toBytes("id");
    private static final byte[] partyNameQual = Bytes.toBytes("name");
    private static final byte[] partyRoleQual = Bytes.toBytes("partyRole");
    private static final String hbaseHost = "138.68.147.208";
    private static final String zookeeperHost = "138.68.147.208";

    public static void main(String[] argv) {
        Party[] parties = getParties();


        Configuration hBaseConfig =  HBaseConfiguration.create();
        hBaseConfig.setInt("timeout", 120000);
        hBaseConfig.set("hbase.master", hbaseHost + ":60000");
        hBaseConfig.set("hbase.zookeeper.quorum",zookeeperHost);
        hBaseConfig.set("hbase.zookeeper.property.clientPort", "2181");


        try{
            logger.info("Trying to connect to HBase");
            Connection connection = ConnectionFactory.createConnection(hBaseConfig);

            //Create the Party dimension static
            Table t = connection.getTable(TableName.valueOf(TABLE_PARTY));

            for (Party p: parties) {
                byte[] pk = Bytes.toBytes(UUID.randomUUID().toString());
                byte[] partyId = Bytes.toBytes(p.getPartyId());
                byte[] name = Bytes.toBytes(p.getPartyName());
                byte[] partyRole = Bytes.toBytes(p.getPartyRole().toString());

                Put put = new Put(pk);
                put.addImmutable(cfPartyDetails,partyIdQual,partyId);
                put.addImmutable(cfPartyDetails,partyNameQual,name);
                put.addImmutable(cfPartyDetails,partyRoleQual,partyRole);

                logger.info("Created immutable record " + p.toJSON());
                t.put(put);
                logger.info("Inserted immutable record" + p.toJSON());

            }

        }
        catch(IOException e){
            logger.info("Error loading data into PARTY Table");
        }



    }



    private static Party[] getParties(){
        ArrayList<Party> result = new ArrayList<Party>();
        final String[] counterParties = {"Blackrock", "GSAM", "UBSGAM", "Fidelity", "Aberdeen Asset Management", "PIMCO", "State Street", "Allianz"};
        for (int i = 0; i < counterParties.length; i++) {
            Party party = new Party(UUID.randomUUID().toString(), counterParties[i], PartyRoleEnum.valueOf("CLIENT"), new HashMap<String, Party>());
            result.add(party);
        }
        return result.toArray(new Party[result.size()]);
    }

}
