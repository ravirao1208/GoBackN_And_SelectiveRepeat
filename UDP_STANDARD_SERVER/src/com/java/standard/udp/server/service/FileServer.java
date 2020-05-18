package com.java.standard.udp.server.service;

import java.io.*;
import java.io.IOException;
import java.net.SocketException;
import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FileServer {
    private DatagramSocket socket;
    private String path;
    private InetAddress clientAddress;
    private int clientPort;

    public FileServer(int port) throws Exception {
        try {
            String classPath = System.getProperty("java.class.path");
            this.path = classPath.substring(0, classPath.length() - 3) + "resources";
            System.out.println("Serving directory '" + path + "' on port " + port + "...");
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println(e);
        }
    }

    public void service() throws IOException {
        try {
            byte fileName[] = new byte[30];
            DatagramPacket request = new DatagramPacket(fileName, fileName.length);
            socket.receive(request);
            String basefileName = path + "\\" + new String(fileName, 0, request.getLength());

            File basefile = new File(basefileName);
            byte fileContent[] = new byte[(int) basefile.length()];

            clientAddress = request.getAddress();
            clientPort = request.getPort();

            if (!basefile.exists()) {
                DatagramPacket response = new DatagramPacket(fileContent, fileContent.length, clientAddress,
                        clientPort);
                socket.send(response);
                throw new FileNotFoundException("Requested File not found!");
            } else {
                System.out.println("Sending " + basefileName + "...");
                FileInputStream file = new FileInputStream(basefile);
                file.read(fileContent);
                file.close();
                DatagramPacket response = new DatagramPacket(fileContent, fileContent.length, clientAddress,
                        clientPort);
                socket.send(response);
                System.out.println("Done.");
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SocketException e) {
            System.out.println(e);
            DatagramPacket response = new DatagramPacket(new byte[0], 0, clientAddress, clientPort);
            socket.send(response);
            socket.close();
        }
    }
}