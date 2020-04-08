package mk.meeskantje.meeskantjecontrol.data.UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import mk.meeskantje.meeskantjecontrol.data.bluetooth.ConnectedThread;
import mk.meeskantje.meeskantjecontrol.data.bluetooth.PacketQueue;

public class UDPReciever extends Thread {
    private boolean bKeepRunning = true;
    private String lastMessage = "";
    private DatagramSocket socket = null;
    private PacketQueue queue;
    private ConnectedThread connectedThread;

    public UDPReciever(PacketQueue queue, DatagramSocket socket) {
        this.queue = queue;
        this.connectedThread = null;
        this.socket = socket;
    }

    public void run() {
        System.out.println("Reciever started");
        String message;
        byte[] lMessage = new byte[255];
        DatagramPacket packet = new DatagramPacket(lMessage, lMessage.length);

        try {
            while(bKeepRunning) {
                socket.receive(packet);
                if (packet != null) {
                    if (connectedThread != null) {
                        message = new String(lMessage, 0, packet.getLength());
                        lastMessage = message;
                        connectedThread.write(message.getBytes());
                    }
                }


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

    public void setConnectedThread(ConnectedThread connectedThread) {
        this.connectedThread = connectedThread;
    }
}
