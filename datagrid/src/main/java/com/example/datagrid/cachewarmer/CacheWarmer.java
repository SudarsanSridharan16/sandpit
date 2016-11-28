package com.example.datagrid.cachewarmer;

import java.util.ArrayList;

/**
 * Created by oliverbuckley-salmon on 26/11/2016.
 */
public class CacheWarmer {

    public static void main(String[] args){

        // Create the Trade Loader threads
        ArrayList<Thread> loaders = new ArrayList<Thread>();
        for(int i = 0; i < 1;i++){
            loaders.add(new Thread(new TradeLoader()));
        }
        loaders.trimToSize();
        for (Thread t:loaders) {
            t.run();
        }

    }
}
