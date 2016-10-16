package com.example.projects.domain;

import com.example.projects.domain.enums.CurrencyEnum;
import com.example.projects.domain.enums.TradeTransactionTypeEnum;
import com.example.projects.domain.enums.TradeTypeEnum;
import com.google.gson.Gson;
import org.joda.time.DateTime;

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

public class Trade implements Portable{

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
    private CurrencyEnum currency;
    private Venue executionVenue;
    private String Instrument;
    // Dimension Ids
    private String  instrumentDimId
    ,               executionVenueDimId;

    public Trade() {
    }

    public Trade(String tradeId, HashMap<String, String> altTradeIds, TradeTransactionTypeEnum tradeTransactionType, TradeTypeEnum tradeType, TradeTypeEnum secondaryTradeTypeEnum, DateTime originalTradeDate, HashMap<String, Party> parties, String marketId, double quantity, double price, CurrencyEnum currency, Venue executionVenue, String instrument) {
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
        this.currency = currency;
        this.executionVenue = executionVenue;
        Instrument = instrument;
    }

    public int getFactoryId() {
        return 0;
    }

    public int getClassId() {
        return 4;
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

    public CurrencyEnum getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyEnum currency) {
        this.currency = currency;
    }

    public Venue getExecutionVenue() {
        return executionVenue;
    }

    public void setExecutionVenue(Venue executionVenue) {
        this.executionVenue = executionVenue;
    }

    public String getInstrument() {
        return Instrument;
    }

    public void setInstrument(String instrument) {
        Instrument = instrument;
    }

    public void writePortable(PortableWriter out) throws IOException {
        out.writeUTF("tradeId", tradeId);
        out.writeUTF("marketId", marketId);
        out.writeDouble("quantity", quantity);
        out.writeDouble("price", price);
        out.writePortable("executionVenue", executionVenue);
        out.writeUTF("Instrument", Instrument);
        ObjectDataOutput rawDataOutput = out.getRawDataOutput();
        rawDataOutput.writeObject(altTradeIds);
        rawDataOutput.writeObject(tradeTransactionType);
        rawDataOutput.writeObject(tradeType);
        rawDataOutput.writeObject(secondaryTradeTypeEnum);
        rawDataOutput.writeObject(originalTradeDate);
        rawDataOutput.writeObject(parties);
        rawDataOutput.writeObject(currency);
    }


    public void readPortable(PortableReader in) throws IOException {
        this.tradeId = in.readUTF("tradeId");
        this.marketId = in.readUTF("marketId");
        this.quantity = in.readDouble("quantity");
        this.price = in.readDouble("price");
        this.executionVenue = in.readPortable("executionVenue");
        this.Instrument = in.readUTF("Instrument");
        ObjectDataInput rawDataInput = in.getRawDataInput();
        this.altTradeIds = rawDataInput.readObject();
        this.tradeTransactionType = rawDataInput.readObject();
        this.tradeType = rawDataInput.readObject();
        this.secondaryTradeTypeEnum = rawDataInput.readObject();
        this.originalTradeDate = rawDataInput.readObject();
        this.parties = rawDataInput.readObject();
        this.currency = rawDataInput.readObject();
    }



    public String toJSON(){
        Gson gson = new Gson();
        //String json = gson.toJson(obj);
        return gson.toJson(this);

    }
}
