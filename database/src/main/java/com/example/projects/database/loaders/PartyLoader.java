package com.example.projects.database.loaders;

import com.example.projects.domain.Party;
import com.example.projects.domain.enums.PartyRoleEnum;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by oliverbuckley-salmon on 16/10/2016.
 * Loads the Party cache with counterparties as part of the static data load
 */
public class PartyLoader {

    private static final String[] counterParties = {"Blackrock", "GSAM", "UBSGAM", "Fidelity", "Bloomberg", "ICAP", "Tullet"};

    //argv[0] should be 'GENERATE' or 'LOAD' if load then argv[1] should contain the path to the file to load
    public static void main(String[] argv) {
        if (argv[0].equalsIgnoreCase("GENERATE")) {
            generateRandomParties();
        } else if (argv[0].equalsIgnoreCase("LOAD")) {
            loadPartyFile(argv[1]);
        }


    }

    private static void generateRandomParties() {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        Map<String, Party> partyCache = hazelcastInstance.getReplicatedMap("party");
        for (int i = 0; i < counterParties.length; i++) {
            Party party = new Party(UUID.randomUUID().toString(), PartyRoleEnum.valueOf("CLIENT"), new HashMap<String, Party>());
            partyCache.put(party.getPartyId(), party);
        }
        System.out.println(partyCache.size() + "Parties loaded");
    }

    private static void loadPartyFile(String path) {

    }

}
