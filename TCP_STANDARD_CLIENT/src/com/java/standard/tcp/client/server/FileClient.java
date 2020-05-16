package com.java.standard.tcp.client.server;

import java.io.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class FileClient {
    private Socket socket = null;
    private OutputStream ostream = null;
    private FileOutputStream fos = null;
    private BufferedOutputStream bos = null;

    public FileClient(String ServerIPAddress, int port) throws Exception {
        socket = new Socket(ServerIPAddress, port);
    }

    public void service() throws IOException {

        Scanner sc = new Scanner(System.in);
        ostream = socket.getOutputStream();
        try {
            System.out.println("\n---------------------------------------------------------");
            System.out.println("      TCP Client to download the files from server");
            System.out.println("---------------------------------------------------------");
            System.out.println("Enter 1 : to download the file \nEnter 2 : to Exit \n");
            if (sc.nextInt() == 1) {

                System.out.println("Enter the filename with extension");
                String fileName = sc.next();

                PrintWriter pwrite = new PrintWriter(ostream, true);
                pwrite.println(fileName);

                int bytesRead;
                int current = 0;

                byte[] mybytearray = new byte[123456789];
                InputStream is = socket.getInputStream();

                bytesRead = is.read(mybytearray, 0, mybytearray.length);
                current = bytesRead;
                if (current != -1) {
                    do {
                        bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
                        if (bytesRead >= 0)
                            current += bytesRead;
                    } while (bytesRead > -1);

                    fos = new FileOutputStream(fileName);
                    bos = new BufferedOutputStream(fos);

                    bos.write(mybytearray, 0, current);
                    bos.flush();
                    System.out.println("File " + fileName + " downloaded successfully");
                } else
                    throw new ArrayIndexOutOfBoundsException("File Not Found!");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        } finally {
            if (fos != null)
                fos.close();
            if (bos != null)
                bos.close();
            if (socket != null)
                socket.close();
        }
    }
}
