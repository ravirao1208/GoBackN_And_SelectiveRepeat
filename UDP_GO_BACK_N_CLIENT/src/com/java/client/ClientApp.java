package com.java.client;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.java.client.service.Client;

public class ClientApp {
    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        String serverHostName;
        int serverPortNumber;
        String fileName;
        int windowSize;
        int maximumSegmentSize;
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the server hostname");
        serverHostName = input.next();
        System.out.println("Enter the server port number");
        serverPortNumber = input.nextInt();
        fileName = "clientsidefile";
        System.out.println("Enter the window size");
        windowSize = input.nextInt();
        System.out.println("Enter the Max Segment size");
        maximumSegmentSize = input.nextInt();
        Client client = Client.getInstane();
        client.sendpacketToServer(serverHostName, serverPortNumber, fileName, windowSize,
                maximumSegmentSize);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total time in seconds : " + TimeUnit.MILLISECONDS.toSeconds(totalTime));
    }

}
