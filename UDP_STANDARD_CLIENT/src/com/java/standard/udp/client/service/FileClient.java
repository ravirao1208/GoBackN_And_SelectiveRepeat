package com.java.standard.udp.client.service;

import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;
import java.io.File;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FileClient {
    private String IPAddress;
    private int Port;
    private DatagramSocket socket;

    public FileClient(String IPAddress, int port) throws SocketException {
        this.IPAddress = IPAddress;
        Port = port;
        socket = new DatagramSocket();
    }

    public void service() throws IOException {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("\n---------------------------------------------------------");
            System.out.println("      UDP Client to download the files from server");
            System.out.println("---------------------------------------------------------");
            System.out.println("Enter 1 : to download the file\nEnter 2 : to Exit \n");
            if (sc.nextInt() == 1) {

                System.out.println("Enter the filename with extension");
                String fileName = sc.next();
                byte[] buffer = fileName.getBytes();
                DatagramPacket request = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(IPAddress),
                        Port);
                socket.send(request);
                byte[] responseBuffer = new byte[123456789];
                DatagramPacket response = new DatagramPacket(responseBuffer, responseBuffer.length);
                socket.receive(response);

                if (response.getLength() != 0) {
                    File receivedFile = new File(fileName);
                    FileOutputStream f = new FileOutputStream(receivedFile);
                    f.write(responseBuffer, 0, response.getLength());
                    f.close();
                    sc.close();
                    System.out.println("File " + fileName + " downloaded successfully");
                } else {
                    sc.close();
                    throw new ArrayIndexOutOfBoundsException("Invalid File or File not found!");
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        } catch (SocketException e) {
            System.out.println(e);
        }
    }
}