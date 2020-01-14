package com.Daslab.flink;

import com.Daslab.source.Server;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.io.IOException;

public class MySink extends RichSinkFunction<FlinkExecutor.WordWithCount> {

    public Server server1 = null;
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        if (server1 == null){
            server1 = new Server(1);
            server1.start();
        }
    }

    @Override
    public void invoke(FlinkExecutor.WordWithCount wordWithCount) throws IOException {
        server1.result = MyProcessWindowFunction.flinkResult;
        System.out.println("invoke");
    }

    @Override
    public void close() throws Exception {
        super.close();
        server1.killSocket();
    }
}
