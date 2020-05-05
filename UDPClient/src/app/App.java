package app;

import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        String serverHostName;
        int serverPortNumber;
        String fileName;
        int windowSize;
        int maximumSegmentSize;
        if (args.length == 5) {
            serverHostName = args[0];
            serverPortNumber = Integer.parseInt(args[1]);
            fileName = args[2];
            windowSize = Integer.parseInt(args[3]);
            maximumSegmentSize = Integer.parseInt(args[4]);
            Client client = Client.getInstane();
            client.sendpacketToServer(serverHostName, serverPortNumber, fileName, windowSize, maximumSegmentSize);
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