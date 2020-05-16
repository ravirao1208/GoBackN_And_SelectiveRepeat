package com.java.standard.tcp.server;

import java.io.IOException;
import java.net.SocketException;

import com.java.standard.tcp.server.service.FileServer;

public class ServerApp {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Syntax: Main <port>");
            return;
        } else {
            int port = Integer.parseInt(args[0]);
            try {
                FileServer server = new FileServer(port);
                server.service();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}