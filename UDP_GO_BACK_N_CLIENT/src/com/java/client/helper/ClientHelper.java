package com.java.client.helper;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import com.java.client.service.Client;

public class ClientHelper {
    // the method is used to convert the data to packet
    public static byte[] finalPacketFrames(byte[] data, int MSSSize, int sequenceNumber) {
        byte[] finalPacket = new byte[data.length + 8];
        byte[] checkSumByte = checksum(data); // calculate check sum
        byte[] sequenceNumberBytes = sequenceNumberBytes(sequenceNumber); // cpnvert sequence no to Byte of array
        byte[] dataTypeIndicator = getDataTypeIndicatorByte(21845); // To Indicate type of data
        System.arraycopy(sequenceNumberBytes, 0, finalPacket, 0, 4); // 0-3 seq-no
        System.arraycopy(checkSumByte, 0, finalPacket, 4, 2); // 4-6 check-sum
        System.arraycopy(dataTypeIndicator, 0, finalPacket, 6, 2); // 6-8 datatype
        System.arraycopy(data, 0, finalPacket, 8, data.length); // 8- actual data
        /*
         * System.out.println(finalPacket);
         * System.out.println("finalPacketSize"+finalPacket.length);
         */
        return finalPacket;
    }

    // this method is used to convert the entire data into equal sized MSS
    public static byte[][] chunkArray(byte[] array, int chunkSize) {
        int numOfChunks = (int) Math.ceil((double) array.length / chunkSize);
        byte[][] output = new byte[numOfChunks][];
        for (int i = 0; i < numOfChunks; ++i) {
            int start = i * chunkSize;// start pos
            int length = Math.min(array.length - start, chunkSize);// end pos
            byte[] temp = new byte[length];
            System.arraycopy(array, start, temp, 0, length); // src, startpos, dest , destpos, lenght
            output[i] = temp;
        }
        return output;
    }

    // used to calculate checksum
    public static byte[] checksum(byte data[]) {
        int sum = 0;
        int nob;
        BigInteger totalSum;
        byte[] checksumByte = new byte[2];
        int[] c_data = new int[2000];
        for (int i = 0; i < data.length; i++) {

            // Complementing the entered data
            // Here we find the number of bits required to represent
            // the data, like say 8 requires 1000, i.e 4 bits
            nob = (int) (Math.floor(Math.log(data[i]) / Math.log(2))) + 1;

            // Here we do a XOR of the data with the number 2^n -1,
            // where n is the nob calculated in previous step
            c_data[i] = ((1 << nob) - 1) ^ data[i];

            // Adding the complemented data and storing in sum
            sum += c_data[i];
        }
        totalSum = BigInteger.valueOf(sum);
        checksumByte = totalSum.toByteArray();
        return checksumByte;
    }

    // convert sequence number into byte sequence
    public static byte[] sequenceNumberBytes(int number) {
        return ByteBuffer.allocate(4).putInt(number).array();
    }

    // Type of data being transmitted
    public static byte[] getDataTypeIndicatorByte(int number) {
        BigInteger dataInt = BigInteger.valueOf(number);
        return dataInt.toByteArray();
    }

    public static void goBackNProtocol(int sendingWindowPointer, int windowSize, byte[][] dataArray, DatagramSocket ds,
            int MSS, InetAddress serverAdress, int serverPort) throws IOException, InterruptedException {
        int endOfWindow;
        if (sendingWindowPointer + windowSize >= dataArray.length - 1) {
            endOfWindow = dataArray.length - 1;
        } else {
            endOfWindow = sendingWindowPointer + windowSize;
        }

        for (int i = sendingWindowPointer; i <= endOfWindow; i++) {
            byte[] udpPacket = finalPacketFrames(dataArray[i], MSS, i);
            DatagramPacket dp = new DatagramPacket(udpPacket, udpPacket.length, serverAdress, serverPort);
            TimeUnit.MILLISECONDS.sleep(50);
            ds.send(dp);
            // noting down the time that is allowed to wait for ack before re-sending
            // formate time <-> Seq-no
            Client.PACKETSENDTIME.put(System.currentTimeMillis() + 10000, i);
        }
    }

}

// https://gist.github.com/lesleh/7724554
// this repository has been used to split the data into chunks of data array