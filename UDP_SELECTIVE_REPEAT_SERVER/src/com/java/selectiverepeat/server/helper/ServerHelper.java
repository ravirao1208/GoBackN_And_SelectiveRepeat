package com.java.selectiverepeat.server.helper;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Random;

import com.java.selectiverepeat.server.entity.ServerPacket;

public class ServerHelper {
    //this function is used to convert the byte array into the format :-
    //Sequence Number
    //Datatype indicator
    //CheckSum
    public static ServerPacket decipherPacket(byte[] data){
        byte[] sequenceBytes  = new byte[4];
        byte[] checkSum =new byte[2];
        byte[] dataType = new byte[2];
        byte[] extractedData = new byte[data.length-8];
        System.arraycopy(data,0,sequenceBytes,0,4);
        System.arraycopy(data,4,checkSum,0,2);
        System.arraycopy(data,6,dataType,0,2);
        System.arraycopy(data,8,extractedData,0,data.length-8);

        /*System.out.println("sequence Number : "+new BigInteger(sequenceBytes).intValue());
        System.out.println("checkSum : "+new BigInteger(checkSum).intValue());
        System.out.println("datatype : "+new BigInteger(dataType).intValue());*/


        int checkSumDecimal =new BigInteger(checkSum).intValue();
        int sequenceNumberDecimal =new BigInteger(sequenceBytes).intValue();
        int dataTypeIndicatorDecimal =new BigInteger(dataType).intValue();
        String dataString = new String(extractedData,0,extractedData.length);

        /*System.out.println("Inside the ServerHelper :" +dataString);*/
        ServerPacket serverPacket = new ServerPacket();
        serverPacket.setCheckSum(checkSumDecimal);
        serverPacket.setSequenceNumber(sequenceNumberDecimal);
        serverPacket.setdataTypeIndicator(dataTypeIndicatorDecimal);
        serverPacket.setData(dataString);
        return serverPacket;

    }
    //the function is used to convert the integer sequence number into bytes[] of acknowledgment number
    public static byte[] getAcknowledgmentNumber(int sequenceNumber) {
        int ackNumber =sequenceNumber+1;
        byte[] ackNumberByte = ByteBuffer.allocate(4).putInt(ackNumber).array();
        return ackNumberByte;
    }
    //the function is used to check if the current packet should be dropped.
    //Return true -->drop packet
    //Return false -->Dont drop packet
    public static Boolean dropPacket(double probability ){
        Random generator = new Random();
        double number = generator.nextDouble() ;
        //System.out.println("Number "+number);
        if(number<=probability){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
