package com.example.projects.gearpump.tasks;

import com.example.projects.domain.Trade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.gearpump.Message;
import org.apache.gearpump.cluster.UserConfig;
import org.apache.gearpump.streaming.javaapi.Task;
import org.apache.gearpump.streaming.task.StartTime;
import org.apache.gearpump.streaming.task.TaskContext;

/**
 * Created by oliverbuckley-salmon on 10/11/2016.
 */
public class TradeHydrator extends Task {

    Gson gson;

    public TradeHydrator(TaskContext taskContext, UserConfig userConfig) {
        super(taskContext, userConfig);
    }

    private Long now() {
        return System.currentTimeMillis();
    }

    @Override
    public void onStart(StartTime startTime) {
        gson = new GsonBuilder().create();
    }

    @Override
    public void onNext(Message message) {
        Trade trade;
        trade = gson.fromJson(message.msg().toString(), Trade.class);
        self().tell(new Message(trade, now()), self());

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
