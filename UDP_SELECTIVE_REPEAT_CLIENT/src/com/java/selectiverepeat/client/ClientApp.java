package com.java.selectiverepeat.client;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.java.selectiverepeat.client.helper.ClientHelper;

public class ClientApp {
	// keep a mapping between the packet expiry time and the packet number
	public static Map<Long, Integer> SRRPACKETSENDTIME = new HashMap<Long, Integer>();
	// a list to maintain the packets whose acknowledgments has been send
	public static Set<Integer> ACKNOWLEDGEDPACKETLIST = new HashSet<>();
	// a single datagram socket for communication.
	public static DatagramSocket ds;
	// the entire file data is stored inside this array
	public static byte[] byteArray;
	// the byteArray is split on basis of MSS
	public static byte[][] byteArray2;
	// the total Maximum Segment Size
	public static int MSS;
	// the host to which the packet needs to be send
	public static String serverHostName;
	// the port number to which the packet needs to be send
	public static int serverPortNumber;
	// a variable to hold the value for the current window limit
	public static int tempWindowPointer = 0;
	// this hold the value for the beginning of the window
	public static int CURRENTWINDOWPOINTER = 0;
	// this is used to hold the input which has been given by the user.
	public static int windowSize;
	// the boolean is used to check if the window is fully filled.
	public static Boolean windowFilled = Boolean.TRUE;

	public static int CURRENTACKNOWLEDGEDPACKETNUMBER = 0;

	/**
	 * The method is used to check the time at which the acknowledgment has been
	 * received in order resend the packet when the timeout occurs.
	 **/
	public static class checkPacketTime extends Thread {
		public void run() {
			try {
				ds = new DatagramSocket();
				while (true) {
					long currentTime = System.currentTimeMillis();
					if (SRRPACKETSENDTIME.size() > 0 && SRRPACKETSENDTIME.containsKey(System.currentTimeMillis())
							&& (!ACKNOWLEDGEDPACKETLIST.contains(SRRPACKETSENDTIME.get(System.currentTimeMillis())))) {
						int packetIDToBeResend = SRRPACKETSENDTIME.get(System.currentTimeMillis());
						SRRPACKETSENDTIME.put(currentTime + 400, packetIDToBeResend);
						byte[] udpPacket = ClientHelper.finalPacketFrames(byteArray2[packetIDToBeResend], MSS, packetIDToBeResend);
						DatagramPacket dp = new DatagramPacket(udpPacket, udpPacket.length, InetAddress.getLocalHost(),
								serverPortNumber);
						ds.send(dp);
						 System.out.println("Packet Loss Timeout: "+ packetIDToBeResend); 
					}
					if (ACKNOWLEDGEDPACKETLIST.size() == windowSize) {
						windowFilled = Boolean.TRUE;
						ACKNOWLEDGEDPACKETLIST.clear();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void SRRSendPacketToServer(String hostName, int serverPort, String fileName, int windowSize, int MSS)
			throws IOException, InterruptedException {
		Path path = Paths.get(System.getProperty("java.class.path") + "\\..\\resources\\clientfiles\\" + fileName + ".txt");
		byteArray = Files.readAllBytes(path);
		byteArray2 = ClientHelper.chunkArray(byteArray, MSS);
		InetAddress serverAdress = InetAddress.getByName(hostName);
		while (CURRENTWINDOWPOINTER < byteArray2.length - 1) {
			if (CURRENTACKNOWLEDGEDPACKETNUMBER <= tempWindowPointer /* && windowFilled.equals(Boolean.TRUE) */) {
				windowFilled = Boolean.FALSE;
				tempWindowPointer = windowSize + tempWindowPointer;
				ClientHelper.selectiveRepeatRequest(CURRENTWINDOWPOINTER, windowSize, byteArray2, ds, MSS, serverAdress, serverPort);
			}
		}
	}

	public static class SRRReceivePacket extends Thread {
		public void run() {
			try {
				ds = new DatagramSocket();
				while (true) {
					byte[] sequenceNumberBytes = new byte[4];
					DatagramPacket receivedDatagram = new DatagramPacket(sequenceNumberBytes,
							sequenceNumberBytes.length);
					ds.receive(receivedDatagram);
					CURRENTACKNOWLEDGEDPACKETNUMBER = Math.max(new BigInteger(sequenceNumberBytes).intValue(),
							CURRENTACKNOWLEDGEDPACKETNUMBER);
					ACKNOWLEDGEDPACKETLIST.add(CURRENTACKNOWLEDGEDPACKETNUMBER - 1);
					
					  System.out.println(ACKNOWLEDGEDPACKETLIST.size() + "The size of ack list.");
					  System.out.println(new BigInteger(sequenceNumberBytes).intValue() +
					  ": Received number"); System.out.println(CURRENTACKNOWLEDGEDPACKETNUMBER +
					  ": CurrentAckNumber");
					 
					if (CURRENTACKNOWLEDGEDPACKETNUMBER > CURRENTWINDOWPOINTER) {
						CURRENTWINDOWPOINTER = CURRENTACKNOWLEDGEDPACKETNUMBER;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) throws IOException, InterruptedException {
		long startTime = System.currentTimeMillis();
		String fileName;
		if (args.length == 5) {
			serverHostName = args[0];
			serverPortNumber = Integer.parseInt(args[1]);
			fileName = args[2];
			windowSize = Integer.parseInt(args[3]);
			MSS = Integer.parseInt(args[4]);
			new SRRReceivePacket().start();
			/* System.out.println("besides receiving thread"); */
			new checkPacketTime().start();
			SRRSendPacketToServer(serverHostName, serverPortNumber, fileName, windowSize, MSS);
		} else {
			System.out.println("Missing Command Line Arguments");
			System.out.println(
					"The arguments should be ServerHostName,ServerPort,Filename,Window Size,Maximum Segment Size.");
		}
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total time in seconds : " + TimeUnit.MILLISECONDS.toSeconds(totalTime));
	}

}
