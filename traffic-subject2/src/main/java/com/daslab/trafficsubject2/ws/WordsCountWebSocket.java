package com.daslab.trafficsubject2.ws;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.HashSet;

@ServerEndpoint(value = "/WordsCloudWebSocket")
@Component
public class WordsCountWebSocket implements Serializable {


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

    public static void boardCast() throws IOException, InterruptedException {
        // SpringBoot不能使用getResource获取文件，可以使用getResourceAsStream流式获取。
        // SpringBoot最好使用ClassPathResource类从类路径下获取文件。其他还有很多种方式获取文件。
        ClassPathResource classPathResource = new ClassPathResource("avgDelayJson.txt");
        // 当文件打进jar包时，不能使用classPathResource.getFile()获取文件，因为他会使用绝对路径，jar包中的文件是找不到的。
        // 可以使用classPathResource.getInputStream()流式获取jar包中的文件。
        InputStream inputStream = classPathResource.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
        String buffer = null;
        in.mark((int)inputStream.available() + 1);
        int window = 5;
        while (true){
            buffer = in.readLine();
            if (buffer != null){
                System.out.println(buffer);
                for (Session session : webSocketSet){
                    session.getBasicRemote().sendText(buffer);
                }
                window--;
                if (window == 0){
                    Thread.sleep(5000);
                    window = 5;
                }
            } else {
                in.reset();
            }
        }
    }
}
