package com.example.projects.database.loaders;

import com.example.projects.domain.Party;
import com.example.projects.domain.enums.PartyRoleEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by oliverbuckley-salmon on 16/10/2016.
 * Loads the Party cache with counterparties as part of the static data load
 */
public class PartyLoader {

    private static final String[] counterParties = {"Blackrock", "GSAM", "UBSGAM", "Fidelity", "Bloomberg", "ICAP", "Tullet"};

    //argv[0] should be 'GENERATE' or 'LOAD' if load then argv[1] should contain the path to the file to load
    public static void main(String[] argv) {
        Party[] parties = getParties();



    }



    private static Party[] getParties(){
        ArrayList<Party> result = new ArrayList<Party>();
        final String[] counterParties = {"Blackrock", "GSAM", "UBSGAM", "Fidelity", "Bloomberg", "ICAP", "Tullet"};
        for (int i = 0; i < counterParties.length; i++) {
            Party party = new Party(UUID.randomUUID().toString(), PartyRoleEnum.valueOf("CLIENT"), new HashMap<String, Party>());
            result.add(party);
        }
        return result.toArray(new Party[result.size()]);
    }

}
