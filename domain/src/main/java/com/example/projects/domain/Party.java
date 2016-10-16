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
    private String partyId;
    private PartyRoleEnum partyRole;
    private HashMap<String,Party> subParties;

    public Party() {
    }

    public Party(String partyId, PartyRoleEnum partyRole, HashMap<String, Party> subParties) {
        this.partyId = partyId;
        this.partyRole = partyRole;
        this.subParties = subParties;
    }

    public int getFactoryId() {
        return 0;
    }

    public int getClassId() {
        return 3;
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



    public void writePortable(PortableWriter out) throws IOException {
        out.writeUTF("partyId", partyId);
        ObjectDataOutput rawDataOutput = out.getRawDataOutput();
        rawDataOutput.writeObject(partyRole);
        rawDataOutput.writeObject(subParties);
    }


    public void readPortable(PortableReader in) throws IOException {
        this.partyId = in.readUTF("partyId");
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
