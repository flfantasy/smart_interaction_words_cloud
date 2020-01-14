package com.Daslab.source;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server extends Thread {

    private Socket socket = null;
    private Integer type = null; //0表示textSource，1表示flinkSource
    private ServerSocket ss = null;
    public String result = "hahaha";

    public Server(Integer type){
        this.type = type;
    }

    @Override
    public void run(){
        try {
            this.startSource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startSource() throws IOException{
        if (type == 0){
            ss = new ServerSocket(8888);
            while (true){
                socket = ss.accept();
                Thread thread0 = new SocketThread0(socket);
                thread0.start();
            }
        }else if (type == 1){
            ss = new ServerSocket(8889);
            while (true){
                socket = ss.accept();
                Thread thread1 = new SocketThread1(socket);
                thread1.start();
            }
        }
    }

    public void boardCast() throws IOException {
//        thread1.show(result);
    }

    public void killSocket() throws IOException {
        ss.close();
    }

    public class SocketThread0 extends Thread{

        private Socket socket = null;

        public SocketThread0(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run(){
            try {
                socket.setKeepAlive(true);
                while (true) {
                    FileInputStream inputStream = new FileInputStream("/usr/local/comments/taobao_home.json");
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), "utf8");
                    String str = null;
                    while ((str = bufferedReader.readLine()) != null) {
                        out.write((str + "\n").toCharArray());
                        System.out.println(out);
                        out.flush();
                        Thread.sleep(1000);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class SocketThread1 extends Thread {

        private Socket socket = null;

        public SocketThread1(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                socket.setKeepAlive(true);
                show("ok");
                Integer flag = 0;
                String oldResult = "";
                while (true){
                    flag++;
                    if(flag == 7){
                        if (oldResult.equals(result)){
                            break;
                        }else {
                            oldResult = result;
                        }
                        flag = 0;
                    }
                    Thread.sleep(1000);
                    show(result);
                }
                show("good");
                socket.close();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        public void show(String str) throws IOException {
            System.out.println("show");
            if (str != null){
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), "utf8");
                out.write((str + "\n").toCharArray());
                out.flush();
            }
        }
    }
}
