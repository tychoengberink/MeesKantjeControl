package mk.meeskantje.meeskantjecontrol.data.UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import mk.meeskantje.meeskantjecontrol.data.bluetooth.ConnectionService;

public class UDPClient extends Thread {
    private boolean bKeepRunning = true;
    private String lastMessage = "";
    private  DatagramSocket socket = null;
    private ConnectionService service;

    public UDPClient(ConnectionService service) {
        this.service = service;
    }

    public void run() {
        System.out.println("Listener started");
        String message;
        byte[] lMessage = new byte[256];
        DatagramPacket packet = new DatagramPacket(lMessage, lMessage.length);

        try {
            DatagramSocket socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.setBroadcast(true);
            socket.bind(new InetSocketAddress(33333));


            while(bKeepRunning) {
                socket.receive(packet);
                System.out.println(service);
                if (packet != null) {
                    this.service.addQueue(packet);
                }

                message = new String(lMessage, 0, packet.getLength());
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
