package com.example.projects.domain;

import com.example.projects.domain.enums.CurrencyPairEnum;
import com.example.projects.domain.enums.FXInstrumentEnum;
import com.example.projects.domain.enums.TenorEnum;
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
public class Instrument implements Portable{

    public static final int ID = 1;

    private String symbol;

    private FXInstrumentEnum instrumentType;

    private TenorEnum tenor;

    private CurrencyPairEnum currencyPair;





    public Instrument(){

    }

    public Instrument(String symbol, FXInstrumentEnum instrumentType, TenorEnum tenor, CurrencyPairEnum currencyPair) {
        this.symbol = symbol;
        this.instrumentType = instrumentType;
        this.tenor = tenor;
        this.currencyPair = currencyPair;
    }

    public int getFactoryId() {
        return 1;
    }

    public int getClassId() {
        return ID;
    }



    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public FXInstrumentEnum getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(FXInstrumentEnum instrumentType) {
        this.instrumentType = instrumentType;
    }

    public TenorEnum getTenor() {
        return tenor;
    }

    public void setTenor(TenorEnum tenor) {
        this.tenor = tenor;
    }

    public CurrencyPairEnum getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(CurrencyPairEnum currencyPair) {
        this.currencyPair = currencyPair;
    }



    public String toJSON(){
        Gson gson = new Gson();
        //String json = gson.toJson(obj);
        return gson.toJson(this);

    }


    public void writePortable(PortableWriter out) throws IOException {
        out.writeUTF("symbol", symbol);
        ObjectDataOutput rawDataOutput = out.getRawDataOutput();
        rawDataOutput.writeObject(instrumentType);
        rawDataOutput.writeObject(tenor);
        rawDataOutput.writeObject(currencyPair);
    }


    public void readPortable(PortableReader in) throws IOException {
        this.symbol = in.readUTF("symbol");
        ObjectDataInput rawDataInput = in.getRawDataInput();
        this.instrumentType = rawDataInput.readObject();
        this.tenor = rawDataInput.readObject();
        this.currencyPair = rawDataInput.readObject();
    }
}
