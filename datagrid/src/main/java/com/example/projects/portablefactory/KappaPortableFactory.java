package com.example.projects.portablefactory;

import com.example.projects.domain.Instrument;
import com.example.projects.domain.Party;
import com.example.projects.domain.Trade;
import com.example.projects.domain.Venue;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;

/**
 * Created by oliverbuckley-salmon on 22/10/2016.
 */
public class KappaPortableFactory implements PortableFactory {

    public Portable create(int classId) {
        if ( Trade.ID == classId ) {
            return new Trade();
        }
        else if(Venue.ID == classId) {
            return new Venue();
        }
        else if(Party.ID == classId){
            return new Party();
        }
        else if(Instrument.ID == classId){
            return new Instrument();
        }
        else
            return null;

    }
}
