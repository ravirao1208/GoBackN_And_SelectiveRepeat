package com.java.standard.tcp.server.service;

import java.io.*;
import java.io.IOException;
import java.net.SocketException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
    private ServerSocket sersocket = null;
    private Socket socket = null;
    private String path = null;
    private FileInputStream fis = null;
    private BufferedInputStream bis = null;
    private OutputStream os = null;

    public FileServer(int port) throws Exception {
        try {
            this.path = System.getProperty("java.class.path") + "\\..\\resources";
            System.out.println("Serving directory '" + path + "' on port " + port + "...");
            sersocket = new ServerSocket(port);
            socket = sersocket.accept();
            System.out.println("Accepted connection : " + socket);
        } catch (SocketException e) {
            System.out.println(e);
        }
    }

    public void service() throws IOException {
        try {

            InputStream istream = socket.getInputStream();
            BufferedReader fileRead = new BufferedReader(new InputStreamReader(istream));
            String fname = fileRead.readLine();

            File myFile = new File(path + "\\" + fname);
            byte[] mybytearray = new byte[(int) myFile.length()];

            if (!myFile.exists()) {
                fileRead.close();
                throw new FileNotFoundException("Requested File not found!");
            }

            fis = new FileInputStream(myFile);
            bis = new BufferedInputStream(fis);
            bis.read(mybytearray, 0, mybytearray.length);
            os = socket.getOutputStream();

            System.out.println("Sending " + fname + "...");
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            fileRead.close();
            System.out.println("Done.");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            if (bis != null)
                bis.close();
            if (os != null)
                os.close();
            if (socket != null)
                socket.close();
            if (sersocket != null)
                sersocket.close();
        }
    }
}