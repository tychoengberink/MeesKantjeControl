package mk.meeskantje.meeskantjecontrol.data.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPSender extends Thread {
    private DatagramSocket socket;

    public UDPSender(DatagramSocket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {

    }

    public void send(DatagramPacket packet) throws IOException {
        socket.send(packet);
    }
}
