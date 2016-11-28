package com.example.datagrid.projects.gearpump.tasks;

import com.example.datagrid.projects.domain.Instrument;
import com.example.datagrid.projects.domain.Trade;
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
 * Created by oliverbuckley-salmon on 08/11/2016.
 */
public class InstrumentLookUpper extends Task {

    NearCacheConfigurer cacheConfigurer = new NearCacheConfigurer();
    IMap<String,Instrument> instruments;


    public InstrumentLookUpper(TaskContext taskContext, UserConfig userConfig) {
        super(taskContext, userConfig);


    }

    private Long now() {
        return System.currentTimeMillis();
    }

    @Override
    public void onStart(StartTime startTime) {
        instruments = cacheConfigurer.getInstrumentNC();

    }

    @Override
    public void onNext(Message message) {
        Trade trade = (Trade) message.msg();
        String symbol = trade.getInstrument().getSymbol();
        EntryObject e = new PredicateBuilder().getEntryObject();
        Predicate predicate = e.get( "symbol" ).equal(symbol);

        Set<String> instrumentKeys =  instruments.keySet(predicate);
        for (String i:instrumentKeys) {
            if(!i.equalsIgnoreCase(null)) {
                trade.setInstrumentDimId(i);
            }
        }
        self().tell(new Message(trade, now()), self());


    }

    @Override
    public void onStop() {
        super.onStop();
    }


}
