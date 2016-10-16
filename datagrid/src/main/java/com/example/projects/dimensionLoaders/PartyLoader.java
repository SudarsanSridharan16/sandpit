package com.example.projects.dimensionLoaders;

import com.example.projects.domain.Party;

import java.util.HashMap;

/**
 * Created by oliverbuckley-salmon on 16/10/2016.
 */
public class PartyLoader {


    //argv[0] should be 'GENERATE' or 'LOAD' if load then argv[1] should contain the path to the file to load
    public static void main(String[] argv) {
        // Map to hold the parties after they are generated and written to cache and file
        HashMap<String, Party> parties = new HashMap<String, Party>();
        if (argv[0].equalsIgnoreCase("GENERATE")) {
            generateRandomParties();
        } else if (argv[0].equalsIgnoreCase("GENERATE")) {
            loadPartyFile(argv[1]);
        }


    }

    private static void generateRandomParties() {

    }

    private static void loadPartyFile(String path) {

    }

}
