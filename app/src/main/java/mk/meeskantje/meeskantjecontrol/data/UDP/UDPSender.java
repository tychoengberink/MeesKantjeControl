package mk.meeskantje.meeskantjecontrol.data.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import mk.meeskantje.meeskantjecontrol.data.bluetooth.ConnectedThread;
import mk.meeskantje.meeskantjecontrol.data.bluetooth.PacketQueue;

public class UDPSender extends Thread {
    private DatagramSocket socket;
    private ConnectedThread connectedThread;
    private PacketQueue queue;

    public UDPSender(PacketQueue queue, DatagramSocket socket) {
        this.queue = queue;
        this.connectedThread = null;
        this.socket = socket;
    }


    @Override
    public void run() {

    }

    public void send(DatagramPacket packet) throws IOException {
        socket.send(packet);
    }
}
