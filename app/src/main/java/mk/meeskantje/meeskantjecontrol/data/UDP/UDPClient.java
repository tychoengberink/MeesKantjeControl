package mk.meeskantje.meeskantjecontrol.data.UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import mk.meeskantje.meeskantjecontrol.data.bluetooth.ConnectedThread;
import mk.meeskantje.meeskantjecontrol.data.bluetooth.PacketQueue;

public class UDPClient extends Thread {
    private boolean bKeepRunning = true;
    private String lastMessage = "";
    private  DatagramSocket socket = null;
    private PacketQueue queue;
    private ConnectedThread connectedThread;

    public UDPClient(PacketQueue queue) {
        this.queue = queue;
        this.connectedThread = null;
    }

    public void run() {
        System.out.println("Listener started");
        String message;
        byte[] lMessage = new byte[255];
        DatagramPacket packet = new DatagramPacket(lMessage, lMessage.length);

        try {
            DatagramSocket socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.setBroadcast(true);
            socket.bind(new InetSocketAddress(33333));


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
