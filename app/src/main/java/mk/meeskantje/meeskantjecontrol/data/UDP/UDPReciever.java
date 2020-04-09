package mk.meeskantje.meeskantjecontrol.data.UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

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
        System.out.println("Receiver started");
        String message;
        byte[] lMessage = new byte[255];
        DatagramPacket packet = new DatagramPacket(lMessage, lMessage.length);

        try {
            while (bKeepRunning) {
                try {
                    socket.receive(packet);
                    queue.addQueue(packet);
                    System.out.println("RECIEVED UPPACKET SIZE = " + queue.getPackets().size());
                    if (connectedThread != null) {

                        if (queue.getPackets().size() > 0) {
                            DatagramPacket nextpacket = queue.getNextPacket();
                            message = new String(lMessage, 0, nextpacket.getLength());
                            lastMessage = message;
                            connectedThread.write(message.getBytes());
                            System.out.println("SENDED UPPACKET TO ARDUINO SIZE = " + queue.getPackets().size());
                        }
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Nothing recieved last 5 secs waiting :)");
                    if (queue.getPackets().size() > 0) {
                        DatagramPacket nextpacket = queue.getNextPacket();
                        message = new String(lMessage, 0, nextpacket.getLength());
                        lastMessage = message;
                        connectedThread.write(message.getBytes());
                        System.out.println("SENDED UPPACKET TO ARDUINO SIZE = " + queue.getPackets().size());
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
