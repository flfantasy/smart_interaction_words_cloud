package com.Daslab.flink_backfront.ws;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/WordsCloudWebSocket")
@Component
public class WordsCountWebSocket implements Serializable {

    public static Socket client = null;
    public static HashSet<Session> webSocketSet = new HashSet<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        webSocketSet.add(session);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        session.close();
        webSocketSet.remove(session);
    }

    public static void boardCast() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String buffer = null;
        while ((buffer = in.readLine()) != null){
            for (Session session : webSocketSet){
                session.getBasicRemote().sendText(buffer);
            }
        }
    }
}
