package com.java.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import com.java.server.service.Server;

public class ServerApp {
    public static void main(String args[]) throws FileNotFoundException,
            UnsupportedEncodingException, IOException, InterruptedException {
        int portNumber;
        String fileName;
        Scanner input = new Scanner(System.in);
        System.out.println("Enter port number");
        portNumber = input.nextInt();
        System.out.println("Enter File name for storing the packet");
        fileName = input.next();
        Server server = Server.getInstance(portNumber, fileName); // Init UDP Socket
        server.receiveData(fileName);
    }
}
