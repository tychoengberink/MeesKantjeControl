package mk.meeskantje.meeskantjecontrol.data.UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPClient extends Thread {
      private boolean bKeepRunning = true;
    private String lastMessage = "";
    private  DatagramSocket socket = null;

    public void run() {
        System.out.println("Listener started");
        String message;
        byte[] lmessage = new byte[256];
        DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);

        try {
            DatagramSocket socket = new DatagramSocket(33333);

            while(bKeepRunning) {
                socket.receive(packet);
                message = new String(lmessage, 0, packet.getLength());
                lastMessage = message;
                System.out.println(message);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (socket != null) {
            socket.close();
        }
    }

    public void kill() {
        bKeepRunning = false;
    }

    public String getLastMessage() {
        return lastMessage;
    }


}
