package com.Daslab.flink;

import com.Daslab.source.Server;

public class Start {
    private static Server textServer = new Server(0);
    private static FlinkExecutor flinkExecutor = new FlinkExecutor();

    public static void main(String[] args) throws Exception {
        textServer.start();
        flinkExecutor.startFlinkJob();
    }
}
