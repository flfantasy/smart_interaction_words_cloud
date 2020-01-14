package com.Daslab.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket cs = new Socket(InetAddress.getLocalHost(), 8889);

        BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
        String buffer = null;
        while ((buffer = in.readLine()) != null){
            System.out.println(buffer);
        }
    }
}
