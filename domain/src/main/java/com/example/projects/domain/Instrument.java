package com.example.projects.domain;

import com.google.gson.Gson;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

import java.io.IOException;

/**
 * Created by oliverbuckley-salmon on 10/08/2016.
 */
public class Instrument implements Portable{

    private String symbol;

    public int getFactoryId() {
        return 1;
    }

    public int getClassId() {
        return 1;
    }

    public Instrument(){

    }

    public Instrument(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }


    public void writePortable(PortableWriter out) throws IOException {
        out.writeUTF("symbol", symbol);
    }


    public void readPortable(PortableReader in) throws IOException {
        this.symbol = in.readUTF("symbol");
    }

    public String toJSON(){
        Gson gson = new Gson();
        //String json = gson.toJson(obj);
        return gson.toJson(this);

    }
}
