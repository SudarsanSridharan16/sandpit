package com.example.projects.domain;

import com.example.projects.domain.enums.CurrencyPairEnum;
import com.example.projects.domain.enums.TradeTransactionTypeEnum;
import com.example.projects.domain.enums.TradeTypeEnum;
import com.google.gson.Gson;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by oliverbuckley-salmon on 10/08/2016.
 */

public class Trade implements Portable{

    public static final int ID = 4;

    private String tradeId;
    private HashMap<String,String> altTradeIds = new HashMap<String, String>();
    private TradeTransactionTypeEnum tradeTransactionType;
    private TradeTypeEnum tradeType;
    private TradeTypeEnum secondaryTradeTypeEnum;
    private DateTime originalTradeDate;
    private HashMap<String, Party> parties = new HashMap<String, Party>(); //N.B. The key of the HashMap is the party's dimension Id
    private String marketId;
    private double quantity
    ,              price;
    private CurrencyPairEnum currencyPair;
    private Venue executionVenue;
    private Instrument instrument;
    // Dimension Ids
    private String  instrumentDimId
    ,               executionVenueDimId
    ,               counterPartyDimId;

    public Trade() {
    }

    public Trade(String tradeId, HashMap<String, String> altTradeIds, TradeTransactionTypeEnum tradeTransactionType, TradeTypeEnum tradeType, TradeTypeEnum secondaryTradeTypeEnum, DateTime originalTradeDate, HashMap<String, Party> parties, String marketId, double quantity, double price, CurrencyPairEnum currencyPair, Venue executionVenue, Instrument instrument) {
        this.tradeId = tradeId;
        this.altTradeIds = altTradeIds;
        this.tradeTransactionType = tradeTransactionType;
        this.tradeType = tradeType;
        this.secondaryTradeTypeEnum = secondaryTradeTypeEnum;
        this.originalTradeDate = originalTradeDate;
        this.parties = parties;
        this.marketId = marketId;
        this.quantity = quantity;
        this.price = price;
        this.currencyPair = currencyPair;
        this.executionVenue = executionVenue;
        this.instrument = instrument;
    }

    public int getFactoryId() {
        return 1;
    }

    public int getClassId() {
        return ID;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public HashMap<String, String> getAltTradeIds() {
        return altTradeIds;
    }

    public void setAltTradeIds(HashMap<String, String> altTradeIds) {
        this.altTradeIds = altTradeIds;
    }

    public TradeTransactionTypeEnum getTradeTransactionType() {
        return tradeTransactionType;
    }

    public void setTradeTransactionType(TradeTransactionTypeEnum tradeTransactionType) {
        this.tradeTransactionType = tradeTransactionType;
    }

    public TradeTypeEnum getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeTypeEnum tradeType) {
        this.tradeType = tradeType;
    }

    public TradeTypeEnum getSecondaryTradeTypeEnum() {
        return secondaryTradeTypeEnum;
    }

    public void setSecondaryTradeTypeEnum(TradeTypeEnum secondaryTradeTypeEnum) {
        this.secondaryTradeTypeEnum = secondaryTradeTypeEnum;
    }

    public DateTime getOriginalTradeDate() {
        return originalTradeDate;
    }

    public void setOriginalTradeDate(DateTime originalTradeDate) {
        this.originalTradeDate = originalTradeDate;
    }

    public HashMap<String, Party> getParties() {
        return parties;
    }

    public void setParties(HashMap<String, Party> parties) {
        this.parties = parties;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public CurrencyPairEnum getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(CurrencyPairEnum currencyPair) {
        this.currencyPair = currencyPair;
    }

    public Venue getExecutionVenue() {
        return executionVenue;
    }

    public void setExecutionVenue(Venue executionVenue) {
        this.executionVenue = executionVenue;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public String getInstrumentDimId() {
        return instrumentDimId;
    }

    public void setInstrumentDimId(String instrumentDimId) {
        this.instrumentDimId = instrumentDimId;
    }

    public String getExecutionVenueDimId() {
        return executionVenueDimId;
    }

    public void setExecutionVenueDimId(String executionVenueDimId) {
        this.executionVenueDimId = executionVenueDimId;
    }

    public String getCounterPartyDimId() {
        return counterPartyDimId;
    }

    public void setCounterPartyDimId(String counterPartyDimId) {
        this.counterPartyDimId = counterPartyDimId;
    }





    public String toJSON(){
        Gson gson = new Gson();
        //String json = gson.toJson(obj);
        return gson.toJson(this);

    }


    public void writePortable(PortableWriter out) throws IOException {
        out.writeUTF("tradeId", tradeId);
        out.writeUTF("marketId", marketId);
        out.writeDouble("quantity", quantity);
        out.writeDouble("price", price);
        out.writePortable("executionVenue", executionVenue);

        out.writeUTF("instrumentDimId", instrumentDimId);
        out.writeUTF("executionVenueDimId", executionVenueDimId);
        out.writeUTF("counterPartyDimId", counterPartyDimId);
        ObjectDataOutput rawDataOutput = out.getRawDataOutput();
        rawDataOutput.writeObject(altTradeIds);
        rawDataOutput.writeObject(tradeTransactionType);
        rawDataOutput.writeObject(tradeType);
        rawDataOutput.writeObject(secondaryTradeTypeEnum);
        rawDataOutput.writeObject(originalTradeDate);
        rawDataOutput.writeObject(parties);
        rawDataOutput.writeObject(currencyPair);
        rawDataOutput.writeObject(instrument);
    }


    public void readPortable(PortableReader in) throws IOException {
        this.tradeId = in.readUTF("tradeId");
        this.marketId = in.readUTF("marketId");
        this.quantity = in.readDouble("quantity");
        this.price = in.readDouble("price");
        this.executionVenue = in.readPortable("executionVenue");

        this.instrumentDimId = in.readUTF("instrumentDimId");
        this.executionVenueDimId = in.readUTF("executionVenueDimId");
        this.counterPartyDimId = in.readUTF("counterPartyDimId");
        ObjectDataInput rawDataInput = in.getRawDataInput();
        this.altTradeIds = rawDataInput.readObject();
        this.tradeTransactionType = rawDataInput.readObject();
        this.tradeType = rawDataInput.readObject();
        this.secondaryTradeTypeEnum = rawDataInput.readObject();
        this.originalTradeDate = rawDataInput.readObject();
        this.parties = rawDataInput.readObject();
        this.currencyPair = rawDataInput.readObject();
        this.instrument = rawDataInput.readObject();
    }
}
