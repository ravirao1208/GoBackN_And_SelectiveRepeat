package com.java.client.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.java.client.helper.ClientHelper;
import com.java.client.thread.CheckPacketTime;
import com.java.client.thread.ReceivePacket;

public class Client {

    private static Client client;
    // the last packet which was received
    public static int CURRENTACKNOWLEDGEDPACKETNUMBER;
    // a pointer to the beginning of the window
    public static volatile int CURRENTWINDOWPOINTER = 0;
    public static volatile Long CONNECTION_TIME_OUT = System.currentTimeMillis() + 600;
    public static Boolean isServerAvailable = true;
    public static int WINDOWSIZE = 0;
    public static Boolean CURRENTACKNOWLEDGEDSTATUS = Boolean.TRUE;
    public static Map<Long, Integer> PACKETSENDTIME = new HashMap<>();
    public static Map<Integer, Boolean> ACKHISTORY = new HashMap<>();
    private DatagramSocket datagramSocket;

    private Client() throws SocketException {
        datagramSocket = new DatagramSocket();
        new ReceivePacket().start();
        new CheckPacketTime().start();
    }

    public static Client getInstane() throws SocketException {
        if (client == null) {
            client = new Client();
        }
        return client;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public void setDatagramSocket(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    public void sendpacketToServer(String serverHostName, int serverPortNumber, String fileName, int windowSize,
            int maximumSegmentSize) throws IOException, InterruptedException {
        WINDOWSIZE = windowSize;
        try {
            Path path = Paths.get(System.getProperty("java.class.path") + "\\..\\resources\\" + fileName);
            byte[] byteArray = Files.readAllBytes(path); // reads file content
            // divides byteArray /MSS
            byte[][] byteArray2 = ClientHelper.chunkArray(byteArray, maximumSegmentSize);
            System.out.println("Total no of packets :" + byteArray2.length);
            InetAddress serverAdress = InetAddress.getByName(serverHostName);
            // eg 0 <= 100
            while (CURRENTWINDOWPOINTER <= byteArray2.length - 1) {
                // eq : Future time <= CurrentTime ::Connection time out exist since no response
                if (CONNECTION_TIME_OUT <= System.currentTimeMillis() && CONNECTION_TIME_OUT != null) {
                    System.out.println("Server couldn't respond: Please try again after some time.");
                    isServerAvailable = false;
                    break;
                }
                // eg 0 , 4, data , MMS, Host ,port
                goBackNProtocol(CURRENTWINDOWPOINTER, windowSize - 1, byteArray2, Client.getInstane(),
                        maximumSegmentSize, serverAdress, serverPortNumber);
            }
            if (!isServerAvailable) {
                System.out.println("Client program terminated!");
            } else
                System.out.println("The process is over.");
        } catch (Exception e) {
            if (e instanceof NoSuchFileException) {
                System.out.println("Client couldn't find the file that you mentioned.");
                System.out.println("Status : 404 Not found");
                System.out.println(e.getCause().getMessage());
            } else {
                System.out.println("Client ran into problem.");
                System.out.println("Status : 500 Internal error");
                System.out.println(e.getCause().getMessage());
            }
        }
    }

    private void goBackNProtocol(int sendingWindowPointer, int windowSize, byte[][] byteArray, Client instance,
            int maximumSegmentSize, InetAddress serverAdress, int serverPortNumber)
            throws SocketException, IOException, InterruptedException {
        int endOfWindow;
        // eg : eg tp = 53
        if (sendingWindowPointer + windowSize >= byteArray.length - 1) {
            endOfWindow = byteArray.length - 1;
        } else {
            endOfWindow = sendingWindowPointer + windowSize;
        }

        System.out.println("_______________________________________________________");
        System.out.println("Window Pointer start and end : " + sendingWindowPointer + " - " + endOfWindow);
        for (int i = sendingWindowPointer; i <= endOfWindow; i++) {
            byte[] udpPacket = ClientHelper.finalPacketFrames(byteArray[i], maximumSegmentSize, i);
            DatagramPacket dp = new DatagramPacket(udpPacket, udpPacket.length, serverAdress, serverPortNumber);
            if (Client.ACKHISTORY.get(i) == null && !PACKETSENDTIME.values().contains(i)) {
                Client.getInstane().getDatagramSocket().send(dp);
                System.out.println("Packet No: " + i + "\t Status : Sent \t Time : " + System.currentTimeMillis());
                // Noting down the time that is allowed to wait for ack before re-sending
                // formate time <-> Seq-no
                PACKETSENDTIME.put(System.currentTimeMillis() + 10000, i);
                Thread.sleep(30);
            }

        }

    }

    public static void printInfoLostPackets(int currentAckPointer, int lastPacketSend) {
        for (int i = currentAckPointer; i < lastPacketSend; i++) {
            System.out.println("Timeout,Sequence Number : " + i);
        }

    }

}
