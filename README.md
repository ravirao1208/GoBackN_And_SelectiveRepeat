# UDP- Go Back N Protocol

### Prerequisites :

* Java 8 installed
* any IDE.

There are  two Subfolders assocaited with the Protocol

1. UDPClient
2. UDPServer

Always run server first and then run the client

#### UDP Server :

job of this module is to accept packet and store it in the file and also send ACK back for the packet received.

Following are the step to be followed to run the UDPclient

1. clone this project into local repository
2. Navigate into UDPServer folder ( eg :  cd  git/[UDP\_Go\_Back\_N-Protocol](https://github.com/ravirao1208/UDP_Go_Back_N-Protocol)/UDPServer.
3. complie the program with javac \<file name> [if you wold like to run via command prompt].
4. run the UDPServer>>src>>ServerApp Main method  by passing 2 arguments(eg: java App \<PortNo Filename>) PortNo : Server's port no -- Filename: saves packet data to the file
if you are using any Dev tools like VSCode or eclipse, please make sure you pass the arguments.

#### UDP Client :

Job of this module is to transer the file packets to UDP server and also makes sures that with in the time limit if ACK is not received for the packet sent current window packets are retransmitted.

Following are the step to be followed to run the UDPclient

1. clone this project into local repository
2. Navigate into UDPClient folder ( eg :  cd  git/[UDP\_Go\_Back\_N-Protocol](https://github.com/ravirao1208/UDP_Go_Back_N-Protocol)/UDPClient.
3. complie the program with javac \<file name> [if you wold like to run via command prompt]
4. run the UDPClient>>src>>App Main method  by passing three arguments(eg: java App \<ServerHostName ServerPort Filename>)  if you are using any Dev tools like VSCode or eclipse, please make sure you pass the arguments.

##### **Note : observer the terminal for packet transmission and ACK **
<br>
### Output:
    At the end of file transmission you can find the transmitted file in UDPServer/ServerFiles/\<fileName.txt> .
