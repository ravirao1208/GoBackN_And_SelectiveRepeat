package com.java.client.thread;

import java.util.List;
import java.util.stream.Collectors;

import com.java.client.service.Client;

public class CheckPacketTime extends Thread {
    @Override
    public void run() {
        while (true) {
            if (!Client.PACKETSENDTIME.isEmpty()) {
                List<Long> timestampList = Client.PACKETSENDTIME.keySet().stream()
                        .filter(x -> x < System.currentTimeMillis()
                                && Client.ACKHISTORY.get(Client.PACKETSENDTIME.get(x)) == null)
                        .sorted().collect(Collectors.toList());
                if (!timestampList.isEmpty()) {
                    System.out.println("<<<<<<<<<<<<<<<<<<<--------------Timeout---------->>>>>>>>>>>>>>>>>>>>>");
                    System.out.println("Before resetting window pointer :" + Client.CURRENTWINDOWPOINTER);
                    Client.CURRENTWINDOWPOINTER = Client.PACKETSENDTIME.get(timestampList.get(0));
                    System.out.println("After resetting window pointer : " + Client.CURRENTWINDOWPOINTER);
                    for (Long time : timestampList) {
                        Client.ACKHISTORY.remove(Client.PACKETSENDTIME.get(time));
                        Client.PACKETSENDTIME.remove(time);
                    }
                    Client.PACKETSENDTIME.clear();
                    timestampList.clear();
                }
            }
        }
    }
}
