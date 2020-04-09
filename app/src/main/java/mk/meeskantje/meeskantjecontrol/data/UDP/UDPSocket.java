package mk.meeskantje.meeskantjecontrol.data.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import mk.meeskantje.meeskantjecontrol.data.bluetooth.PacketQueue;

public class UDPSocket {
    private UDPSender sender;
    private UDPReciever reciever;
    private DatagramSocket socket;

    public UDPSocket(int port, PacketQueue queue) {
        try {
            this.socket = new DatagramSocket(port);
            this.socket.setSoTimeout(5000);
            this.sender = new UDPSender(socket);
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

    /**
     * Gives the message to the UDPSender where it will get send over UDP.
     * @param out message.
     */
    public void send(DatagramPacket out) {
        try {
            this.sender.send(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the address where the UDP sender sends to.
     * @return InetAddress address.
     * @throws UnknownHostException
     */
    public InetAddress getAdress() throws UnknownHostException {
        return InetAddress.getByName("192.168.178.20");
    }

    /**
     * Stops the receiver and closes the socket.
     */
    public void stop() {
        this.reciever.kill();
        this.socket.close();
    }


}
