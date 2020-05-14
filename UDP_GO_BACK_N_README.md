# UDP- Go Back N Protocol

[https://github.com/ravirao1208/UDP\_Go\_Back\_N-Protocol.git](https://github.com/ravirao1208/UDP_Go_Back_N-Protocol.git)

### Prerequisites :

* Java 8 installed
* VS Code IDE.

There are  two Subfolders assocaited with UDP - Go back N

1. UDP\_GO\_BACK\_N\_CLIENT
2. UDP\_GO\_BACK\_N\_SERVER

Always run server first and then run the client

#### UDP\_GO\_BACK\_N\_SERVER :

The job of this module is to accept packet and store it in the file and also send ACK back for the packet received.
Following are the step to be followed to run the UDP\_GO\_BACK\_N\_SERVER

1. Click on the Run and Debug icon on the IDE.
2. Select <span class="colour" style="color: rgb(152, 195, 121);">ServerApp<UDP\_GO\_BACK\_N\_SERVER> </span><span class="colour" style="color: rgb(152, 195, 121);"> from the launch config file.</span>
3. As the you select and run new terminal will pop
    * Enter server port number (eg: 9093)
    * Enter file name to where the packets to be stored (eg : serverCopyFile)

#### UDP\_GO\_BACK\_N\_CLIENT :

Job of this module is to transer the file packets to UDP server and also makes sures that with in the time limit if ACK is not received for the packet sent current window packets are retransmitted.
Following are the step to be followed to run the UDPclient

1. Click on the Run and Debug icon on the IDE
2. Select <span class="colour" style="color: rgb(152, 195, 121);">ClientApp<UDP\_GO\_BACK\_N\_CLIENT></span><span class="colour" style="color: rgb(152, 195, 121);">  from the launch config file.</span>
3. As the you select and run new terminal will pop there
    * Enter the server HostName  as localhost if the server is running with in the server if not please pass valid hostname (eg : localhost)
    * Enter Server port number (eg: 9093)
    * Enter the window size (eg: 5)
    * Entet the Max Segment size ( < 2001) (eg: 2000)

##### \*\*Note : observer the terminal for packet transmission and ACK \*\*

### Output:

At the end of file transmission you can find  file created in the UDP\_GO\_BACK\_N\_SERVER/resources/serverfiles/\<filename that you gave> with data in it.