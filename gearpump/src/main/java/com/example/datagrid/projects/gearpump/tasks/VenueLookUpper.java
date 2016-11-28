package com.example.datagrid.projects.gearpump.tasks;

import com.example.datagrid.projects.domain.Trade;
import com.example.datagrid.projects.domain.Venue;
import com.example.datagrid.projects.gearpump.nearcache.NearCacheConfigurer;
import com.hazelcast.core.IMap;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import org.apache.gearpump.Message;
import org.apache.gearpump.cluster.UserConfig;
import org.apache.gearpump.streaming.javaapi.Task;
import org.apache.gearpump.streaming.task.StartTime;
import org.apache.gearpump.streaming.task.TaskContext;

import java.util.Set;

/**
 * Created by oliverbuckley-salmon on 10/11/2016.
 */
public class VenueLookUpper extends Task {
    NearCacheConfigurer cacheConfigurer = new NearCacheConfigurer();
    IMap<String,Venue> venues;

    public VenueLookUpper(TaskContext taskContext, UserConfig userConfig) {
        super(taskContext, userConfig);
        venues = cacheConfigurer.getVenueNC();

    }

    @Override
    public void onStart(StartTime startTime) {
        super.onStart(startTime);
    }

    @Override
    public void onNext(Message message) {
        Trade trade = (Trade) message.msg();
        String venueId = trade.getExecutionVenue().getVenueId();
        EntryObject e = new PredicateBuilder().getEntryObject();
        Predicate predicate = e.get( "venueId" ).equal(venueId);

        Set<String> venuesKeys =  venues.keySet(predicate);
        for (String v:venuesKeys) {
            if(!v.equalsIgnoreCase(null)) {
                trade.setInstrumentDimId(v);
            }
        }
        self().tell(new Message(trade, now()), self());
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private Long now() {
        return System.currentTimeMillis();
    }
}
