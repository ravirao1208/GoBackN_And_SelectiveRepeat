package app;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import app.ClientHelpers.ClientHelper;

public class Client {

    private static Client client;
    // the last packet which was received
    public static int CURRENTACKNOWLEDGEDPACKETNUMBER;
    // a pointer to the beginning of the window
    public static int CURRENTWINDOWPOINTER = 0;
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
            int maximumSegmentSize) throws IOException {
        WINDOWSIZE = windowSize;
        Path path = Paths.get("src\\app\\resources\\" + fileName + ".txt");
        byte[] byteArray = Files.readAllBytes(path);
        byte[][] byteArray2 = ClientHelper.chunkArray(byteArray, maximumSegmentSize);
        System.out.println("Total no of packets :" + byteArray2.length);
        InetAddress serverAdress = InetAddress.getByName(serverHostName);
        while (CURRENTWINDOWPOINTER <= byteArray2.length - 1 && CURRENTACKNOWLEDGEDSTATUS.equals(Boolean.TRUE)) {
            goBackNProtocol(CURRENTWINDOWPOINTER, windowSize - 1, byteArray2, Client.getInstane(), maximumSegmentSize,
                    serverAdress, serverPortNumber);
        }
        System.out.println("The process is over.");
    }

    private void goBackNProtocol(int sendingWindowPointer, int windowSize, byte[][] byteArray, Client instance,
            int maximumSegmentSize, InetAddress serverAdress, int serverPortNumber)
            throws SocketException, IOException {
        int endOfWindow;
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
                PACKETSENDTIME.put(System.currentTimeMillis() + 10000, i);
            }

        }

    }

    public static void printInfoLostPackets(int currentAckPointer, int lastPacketSend) {
        for (int i = currentAckPointer; i < lastPacketSend; i++) {
            System.out.println("Timeout,Sequence Number : " + i);
        }

    }

}