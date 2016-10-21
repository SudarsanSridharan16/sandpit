package com.example.projects.dimensionLoaders;

import com.example.projects.domain.Venue;
import com.example.projects.domain.enums.VenueTypeEnum;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.Map;
import java.util.UUID;

/**
 * Created by oliverbuckley-salmon on 16/10/2016.
 * Loads the Venue cache with exceution venues as part of the static data load
 */
public class VenueLoader {
    static final String[] executionVenues = {"FXALL", "LMAX", "", "Currenex", "Bloomberg", "ICAP", "Tullet", "360T", "EBS", "CME"};

    public static void main(String[] argv) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        Map<String, Venue> venuesCache = hazelcastInstance.getReplicatedMap("venue");
        for (int i = 0; i < executionVenues.length; i++) {
            Venue venue = new Venue(UUID.randomUUID().toString(), executionVenues[i], VenueTypeEnum.valueOf("ELECTRONIC"));
            venuesCache.put(venue.getVenueId(), venue);
        }

    }
}
