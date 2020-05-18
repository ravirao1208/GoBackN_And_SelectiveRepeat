package com.java.standard.tcp.client.server;

import java.io.*;
import java.io.IOException;
import java.net.Socket;

public class FileClient {
    private Socket socket = null;
    private String fileName = null;
    private OutputStream ostream = null;
    private FileOutputStream fos = null;
    private BufferedOutputStream bos = null;

    public FileClient(String ServerIPAddress, int port, String fileName) throws Exception {
        socket = new Socket(ServerIPAddress, port);
        this.fileName = fileName;
    }

    public void service() throws IOException {

        ostream = socket.getOutputStream();
        try {
            long startTime = System.currentTimeMillis();
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

                String classPath = System.getProperty("java.class.path");
                String receivedFileName = classPath.substring(0, classPath.length() - 3) + "received\\received_file.txt";

                fos = new FileOutputStream(receivedFileName);
                bos = new BufferedOutputStream(fos);

                bos.write(mybytearray, 0, current);
                bos.flush();
                System.out.println("File " + fileName + " downloaded successfully");
                long elapsedTime = System.currentTimeMillis() - startTime;
                System.out.println("Time taken:" + elapsedTime + "ms");
            } else
                throw new ArrayIndexOutOfBoundsException("File Not Found!");
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
