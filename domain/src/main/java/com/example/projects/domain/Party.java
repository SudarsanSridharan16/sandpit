package com.example.projects.domain;


import com.example.projects.domain.enums.PartyRoleEnum;
import com.google.gson.Gson;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by oliverbuckley-salmon on 10/08/2016.
 */
public class Party implements Portable{

    public static final int ID = 3;

    private String partyId;
    private String partyName;
    private PartyRoleEnum partyRole;
    private HashMap<String,Party> subParties;

    public Party() {
    }

    public Party(String partyId, String partyName, PartyRoleEnum partyRole, HashMap<String, Party> subParties) {
        this.partyId = partyId;
        this.partyName = partyName;
        this.partyRole = partyRole;
        this.subParties = subParties;
    }

    public int getFactoryId() {
        return 1;
    }

    public int getClassId() {
        return ID;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public PartyRoleEnum getPartyRole() {
        return partyRole;
    }

    public void setPartyRole(PartyRoleEnum partyRole) {
        this.partyRole = partyRole;
    }

    public HashMap<String, Party> getSubParties() {
        return subParties;
    }

    public void setSubParties(HashMap<String, Party> subParties) {
        this.subParties = subParties;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public void writePortable(PortableWriter out) throws IOException {
        out.writeUTF("partyId", partyId);
        out.writeUTF("partyName", partyName);
        ObjectDataOutput rawDataOutput = out.getRawDataOutput();
        rawDataOutput.writeObject(partyRole);
        rawDataOutput.writeObject(subParties);
    }


    public void readPortable(PortableReader in) throws IOException {
        this.partyId = in.readUTF("partyId");
        this.partyName = in.readUTF("partyName");
        ObjectDataInput rawDataInput = in.getRawDataInput();
        this.partyRole = rawDataInput.readObject();
        this.subParties = rawDataInput.readObject();
    }

    public String toJSON(){
        Gson gson = new Gson();
        //String json = gson.toJson(obj);
        return gson.toJson(this);

    }
}
