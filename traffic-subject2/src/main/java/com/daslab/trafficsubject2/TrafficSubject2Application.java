package com.daslab.trafficsubject2;

import com.daslab.trafficsubject2.ws.WordsCountWebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class TrafficSubject2Application {

    public static void main(String[] args) throws IOException, InterruptedException {
        SpringApplication.run(TrafficSubject2Application.class, args);
        WordsCountWebSocket.boardCast();
    }

}
