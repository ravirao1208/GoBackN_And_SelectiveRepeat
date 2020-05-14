package com.java.server.helper;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import com.java.server.entity.ServerPacket;

public class ServerHelper {

    public static ServerPacket decipherPacket(byte[] data) {
        byte[] sequenceBytes = new byte[4];
        byte[] checkSum = new byte[2];
        byte[] dataType = new byte[2];
        byte[] extractedData = new byte[data.length - 8];
        System.arraycopy(data, 0, sequenceBytes, 0, 4);
        System.arraycopy(data, 4, checkSum, 0, 2);
        System.arraycopy(data, 6, dataType, 0, 2);
        System.arraycopy(data, 8, extractedData, 0, data.length - 8);

        int checkSumDecimal = new BigInteger(checkSum).intValue();
        int sequenceNumberDecimal = new BigInteger(sequenceBytes).intValue();
        int dataTypeIndicatorDecimal = new BigInteger(dataType).intValue();
        String dataString = new String(extractedData, 0, extractedData.length);

        ServerPacket serverPacket = new ServerPacket();
        serverPacket.setCheckSum(checkSumDecimal);
        serverPacket.setSequenceNumber(sequenceNumberDecimal);
        serverPacket.setdataTypeIndicator(dataTypeIndicatorDecimal);
        serverPacket.setData(dataString);
        return serverPacket;

    }

    public static byte[] getAcknowledgmentNumber(int sequenceNumber) {
        int ackNumber = sequenceNumber + 1;
        byte[] ackNumberByte = ByteBuffer.allocate(4).putInt(ackNumber).array();
        return ackNumberByte;
    }

}
