package com.java.standard.tcp.client;

import java.io.IOException;
import java.net.SocketException;

import com.java.standard.tcp.client.server.FileClient;

public class ClientApp {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Syntax: Server <IP Address> <port>");
            return;
        }
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        try {
            FileClient client = new FileClient(hostname, port);
            client.service();
        } catch (SocketException e) {
            System.out.println("Error - Server refused to connect!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}