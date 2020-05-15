package com.java.selectiverepeat.client.helper;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import com.java.selectiverepeat.client.ClientApp;

public class ClientHelper {
	// the method is used to convert the data to packet
	public static byte[] finalPacketFrames(byte[] data, int MSSSize, int sequenceNumber) {
		byte[] finalPacket = new byte[data.length + 8];
		byte[] checkSumByte = checksum(data);
		byte[] sequenceNumberBytes = sequenceNumberBytes(sequenceNumber);
		byte[] dataTypeIndicator = getDataTypeIndicatorByte(21845);
		System.arraycopy(sequenceNumberBytes, 0, finalPacket, 0, 4);
		System.arraycopy(checkSumByte, 0, finalPacket, 4, 2);
		System.arraycopy(dataTypeIndicator, 0, finalPacket, 6, 2);
		System.arraycopy(data, 0, finalPacket, 8, data.length);
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
			int start = i * chunkSize;
			int length = Math.min(array.length - start, chunkSize);
			byte[] temp = new byte[length];
			System.arraycopy(array, start, temp, 0, length);
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

	// convert dataTypeIndicator to byte sequence
	public static byte[] getDataTypeIndicatorByte(int number) {
		BigInteger dataInt = BigInteger.valueOf(number);
		byte[] dataType = dataInt.toByteArray();
		return dataType;
	}

	

	public static void selectiveRepeatRequest(int sendingWindowPointer, int windowSize, byte[][] dataArray,
			DatagramSocket ds, int MSS, InetAddress serverAdress, int serverPort)
			throws IOException, InterruptedException {
		int endOfWindow;
		if (sendingWindowPointer + windowSize >= dataArray.length - 1) {
			endOfWindow = dataArray.length - 1;
		} else {
			endOfWindow = sendingWindowPointer + windowSize;
		}
		/* System.out.println("Packet Loss,Sequence Number: "+ sendingWindowPointer); */
		for (int i = sendingWindowPointer; i < endOfWindow; i++) {
			byte[] udpPacket = finalPacketFrames(dataArray[i], MSS, i);
			DatagramPacket dp = new DatagramPacket(udpPacket, udpPacket.length, serverAdress, serverPort);
			TimeUnit.MILLISECONDS.sleep(50);
			ds.send(dp);
			ClientApp.SRRPACKETSENDTIME.put(System.currentTimeMillis() + 400, i);
		}

	}

	// to print the information about the packets which were not received after the
	// timeout.
	public static void printInfoLostPackets(int currentAckPointer, int lastPacketSend) {
		for (int i = currentAckPointer; i < lastPacketSend; i++) {
			System.out.println("Timeout,Sequence Number : " + i);
		}

	}

}

//https://gist.github.com/lesleh/7724554
//this repository has been used to split the data into chunks of data array