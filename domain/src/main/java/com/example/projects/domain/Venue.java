package com.example.projects.domain;

import com.example.projects.domain.enums.VenueTypeEnum;
import com.google.gson.Gson;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

import java.io.IOException;


/**
 * Created by oliverbuckley-salmon on 10/08/2016.
 */
public class Venue implements Portable{

    private String venueId
    ,              name;
    private VenueTypeEnum venueType;

    public Venue(){

    }

    public Venue(String venueId, String name, VenueTypeEnum venueType) {
        this.venueId = venueId;
        this.name = name;
        this.venueType = venueType;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VenueTypeEnum getVenueType() {
        return venueType;
    }

    public void setVenueType(VenueTypeEnum venueType) {
        this.venueType = venueType;
    }

    public int getFactoryId() {
        return 0;
    }

    public int getClassId() {
        return 2;
    }



    public void writePortable(PortableWriter out) throws IOException {
        out.writeUTF("venueId", venueId);
        out.writeUTF("name", name);
        ObjectDataOutput rawDataOutput = out.getRawDataOutput();
        rawDataOutput.writeObject(venueType);
    }


    public void readPortable(PortableReader in) throws IOException {
        this.venueId = in.readUTF("venueId");
        this.name = in.readUTF("name");
        ObjectDataInput rawDataInput = in.getRawDataInput();
        this.venueType = rawDataInput.readObject();
    }

    public String toJSON(){
        Gson gson = new Gson();
        //String json = gson.toJson(obj);
        return gson.toJson(this);

    }
}
