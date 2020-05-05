package app;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;

public class ReceivePacket extends Thread {

    public void run() {
        try {
            System.out.println("Receiver program activated");
            while (true) {
                byte[] sequenceNumberBytes = new byte[4];
                DatagramPacket receivedDatagram = new DatagramPacket(sequenceNumberBytes, sequenceNumberBytes.length);
                Client.getInstane().getDatagramSocket().receive(receivedDatagram);
                int ack_No = new BigInteger(sequenceNumberBytes).intValue();
                if (Client.ACKHISTORY.get(ack_No - 1) == null) {
                    Client.ACKHISTORY.put(ack_No - 1, Boolean.TRUE);
                    System.out.println("ACK Received for packet :" + (ack_No - 1));
                } else
                    System.out.println("ACK Received for duplicate packet :" + (ack_No - 1));

                if (Client.CURRENTWINDOWPOINTER == ack_No - 1) {
                    Client.CURRENTWINDOWPOINTER = ack_No;
                    for (int i = ack_No; i < ack_No + Client.WINDOWSIZE; i++) {
                        if (Client.ACKHISTORY.get(i) != null) {
                            Client.CURRENTWINDOWPOINTER = i;
                        } else
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}