package mk.meeskantje.meeskantjecontrol.data.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import mk.meeskantje.meeskantjecontrol.data.bluetooth.PacketQueue;

public class UDPSocket {
    private UDPSender sender;
    private UDPReciever reciever;
    private DatagramSocket socket;

    public UDPSocket(int port, PacketQueue queue) {
        try {
            this.socket = new DatagramSocket(port);
            this.sender = new UDPSender(queue, socket);
            this.reciever = new UDPReciever(queue, socket);
            this.sender.start();
            this.reciever.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public UDPReciever getReciever() {
        return reciever;
    }

    public void send(DatagramPacket out) {
        try {
            this.sender.send(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        this.socket.close();
    }


}
