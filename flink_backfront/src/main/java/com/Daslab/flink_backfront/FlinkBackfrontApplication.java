package com.Daslab.flink_backfront;

import com.Daslab.flink_backfront.ws.WordsCountWebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

@SpringBootApplication
public class FlinkBackfrontApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(FlinkBackfrontApplication.class, args);
        WordsCountWebSocket.client = new Socket(InetAddress.getLocalHost(),8889);

        WordsCountWebSocket.boardCast();
    }

}
