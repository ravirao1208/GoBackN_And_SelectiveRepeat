package com.java.standard.udp.client.service;

import java.io.IOException;
import java.net.SocketException;
import java.io.File;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FileClient {
    private String IPAddress;
    private int Port;
    private DatagramSocket socket;
    private String fileName;

    public FileClient(String IPAddress, int port, String fileName) throws SocketException {
        this.IPAddress = IPAddress;
        this.fileName = fileName;
        Port = port;
        socket = new DatagramSocket();
    }

    public void service() throws IOException {
        try {
            byte[] buffer = fileName.getBytes();
            DatagramPacket request = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(IPAddress), Port);
            long startTime = System.currentTimeMillis();
            socket.send(request);
            byte[] responseBuffer = new byte[123456789];
            DatagramPacket response = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(response);

            if (response.getLength() != 0) {

                String classPath = System.getProperty("java.class.path");
                String receivedFileName = classPath.substring(0, classPath.length() - 3)
                        + "received\\received_file.txt";

                File receivedFile = new File(receivedFileName);
                receivedFile.createNewFile();
                FileOutputStream f = new FileOutputStream(receivedFile);
                f.write(responseBuffer, 0, response.getLength());
                f.close();
                System.out.println("File " + fileName + " downloaded successfully");
                long elapsedTime = System.currentTimeMillis() - startTime;
                System.out.println("Time taken:" + elapsedTime + "ms");
            } else {
                throw new ArrayIndexOutOfBoundsException("Invalid File or File not found!");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        } catch (SocketException e) {
            System.out.println(e);
        }
    }
}