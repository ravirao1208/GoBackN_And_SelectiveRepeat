package com.java.standard.tcp.client;

import java.io.IOException;
import java.net.SocketException;

import com.java.standard.tcp.client.server.FileClient;

public class ClientApp {

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Syntax: Server <IP Address> <port> <file_name_with_extension>");
            return;
        }
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        String fileName = args[2];
        try {
            FileClient client = new FileClient(hostname, port, fileName);
            client.service();
        } catch (SocketException e) {
            System.out.println("Error - Server refused to connect!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}