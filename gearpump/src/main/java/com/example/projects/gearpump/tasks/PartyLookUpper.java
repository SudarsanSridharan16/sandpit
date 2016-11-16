package com.example.projects.gearpump.tasks;

import com.example.projects.domain.Party;
import com.example.projects.domain.Trade;
import com.example.projects.domain.enums.PartyRoleEnum;
import com.example.projects.gearpump.nearcache.NearCacheConfigurer;
import com.hazelcast.core.IMap;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import org.apache.gearpump.Message;
import org.apache.gearpump.cluster.UserConfig;
import org.apache.gearpump.streaming.javaapi.Task;
import org.apache.gearpump.streaming.task.StartTime;
import org.apache.gearpump.streaming.task.TaskContext;

import java.util.Map;
import java.util.Set;

/**
 * Created by oliverbuckley-salmon on 10/11/2016.
 */
public class PartyLookUpper extends Task{

    NearCacheConfigurer cacheConfigurer = new NearCacheConfigurer();
    IMap<String,Party> counterParties = null;
    public PartyLookUpper(TaskContext taskContext, UserConfig userConfig) {
        super(taskContext, userConfig);

        counterParties = cacheConfigurer.getPartyNC();
    }

    @Override
    public void onStart(StartTime startTime) {
        super.onStart(startTime);
    }

    @Override
    public void onNext(Message message) {
        Trade trade = (Trade) message.msg();
        Map<String,Party> parties = trade.getParties();
        final Party[] party = new Party[1];
        parties.forEach((k,v)->{
            if(v.getPartyRole().equals(PartyRoleEnum.CLIENT))
                party[0] = v;

        });

        String partyId = party[0].getPartyId();
        EntryObject e = new PredicateBuilder().getEntryObject();
        Predicate predicate = e.get( "partyId" ).equal(partyId);

        Set<String> partyKeys =  counterParties.keySet(predicate);
        for (String pk:partyKeys) {
            if(pk!=null) {
                trade.setCounterPartyDimId(pk);
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
