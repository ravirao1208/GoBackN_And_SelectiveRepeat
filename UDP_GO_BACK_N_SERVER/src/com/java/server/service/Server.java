package com.java.server.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.java.server.entity.ServerPacket;
import com.java.server.helper.ServerHelper;

public class Server {
    public static Integer CURRENTSEQUENCENUMBER = 0;
    public static Integer TOTALPACKETLOSSCOUNT = 0;
    private static Server server;
    private DatagramSocket datagramSocket;

    private Server(int port) throws SocketException {
        datagramSocket = new DatagramSocket(port);
        System.out.println("Server listening.. .... .... .....");
    }

    public static Server getInstance(int port, String FILENAME) throws SocketException {
        if (server == null) {
            server = new Server(port);
        }
        return server;
    }

    public String createServerFile(String FILENAME) throws IOException {
        String fileName = System.getProperty("java.class.path") + "\\..\\resources\\serverfiles\\" + FILENAME.trim()
                + ".txt";
        File baseFile = new File(fileName);
        baseFile.createNewFile();
        return fileName;
    }

    public void receiveData(String fileName)
            throws FileNotFoundException, UnsupportedEncodingException, IOException, InterruptedException {
        byte[] b1 = new byte[1024]; // placeholder
        // creates file at server
        // creates outStreamWriter
        PrintWriter writer = new PrintWriter(this.createServerFile(fileName), "UTF-8");
        while (true) {
            DatagramPacket dp = new DatagramPacket(b1, b1.length);
            datagramSocket.receive(dp);
            ServerPacket currentPacket = ServerHelper.decipherPacket(b1); // deserializing 
            int sequenceNumber = currentPacket.getSequenceNumber(); // rec-packet seq-no
            // eg : 5 <= 5 // valid and write content to file and send ack
            // eg : 5 <= 6 // Duplicate packet recived :: we dont write but we send ack
            // eg : 5 <= 4 // will drop packet and wont send ack
            if (sequenceNumber <= CURRENTSEQUENCENUMBER) {
                Thread.sleep(10);
                if (sequenceNumber == CURRENTSEQUENCENUMBER) {
                    BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
                    writer.println(currentPacket.getData());
                    out.close();
                    System.out.println("Packet No : " + sequenceNumber + "\t Status : Received \t Time : "
                            + System.currentTimeMillis());
                    CURRENTSEQUENCENUMBER++;
                }
                byte[] acknowledgmentNumberBytes = ServerHelper.getAcknowledgmentNumber(sequenceNumber);
                DatagramPacket dp2 = new DatagramPacket(acknowledgmentNumberBytes, acknowledgmentNumberBytes.length,
                        dp.getAddress(), dp.getPort());
                datagramSocket.send(dp2);
                System.out.println("Packet No : " + sequenceNumber + "\t ACK : Sent");
            } else {
                System.out.println("Dropping packet : " + sequenceNumber);
            }
        }
    }

}
