package com.example.projects.dimensionLoaders;

import com.example.projects.domain.Instrument;
import com.example.projects.domain.enums.CurrencyPairEnum;
import com.example.projects.domain.enums.FXInstrumentEnum;
import com.example.projects.domain.enums.TenorEnum;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.Map;

/**
 * Created by oliverbuckley-salmon on 16/10/2016.
 * Loads the Instrument cache with FX instruments as part of the static data load
 */
public class InstrumentLoader {

    private static final String[] currencyPairs =   {"EURUSD", "USDJPY", "GBPUSD", "USDCHF", "USDCAD", "AUDUSD", "NZDUSD",
                                                    "EURJPY", "GBPJPY", "CHFJPY", "CADJPY", "AUDJPY", "NZDJPY", "GBPCHF", "GBPAUD",
                                                    "GBPCAD", "GBPNZD", "AUDCHF", "AUDCAD", "AUDNZD"};
    private static final String[] instrumentTypes =   {"SPOT", "FORWARD", "SWAP", "NDF"};
    private static final String[] tenors =  {"SPOT", "OneM", "TwoM", "ThreeM", "SixM"};

    public static void main(String[] argv) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        Map<String, Instrument> instrumentCache = hazelcastInstance.getReplicatedMap("instrument");
        Instrument instrument;
        for (String type : instrumentTypes){
            for(String pair : currencyPairs){
                for(String tenor : tenors){
                    if(type == FXInstrumentEnum.SPOT.toString()){
                        instrument = new Instrument(type+" "+pair+" "+TenorEnum.SPOT.toString(), FXInstrumentEnum.valueOf(type),
                                TenorEnum.SPOT, CurrencyPairEnum.valueOf(pair));
                    } else {
                        instrument = new Instrument(type + " " + pair + " " + tenor, FXInstrumentEnum.valueOf(type),
                                TenorEnum.valueOf(tenor), CurrencyPairEnum.valueOf(pair));
                    }
                    instrumentCache.put(instrument.getSymbol(), instrument);
                }


            }
        }
        System.out.println(instrumentCache.size()+" Instruments loaded");
    }
}
