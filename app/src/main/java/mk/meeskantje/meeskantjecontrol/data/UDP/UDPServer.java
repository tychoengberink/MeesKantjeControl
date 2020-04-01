package mk.meeskantje.meeskantjecontrol.data.UDP;

import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import mk.meeskantje.meeskantjecontrol.data.bluetooth.ConnectionService;

public class UDPServer extends Thread {
    DatagramSocket socket;
    boolean running;

//    private byte[] buf = null; TODO remove

    private ArrayList<DatagramPacket> queue;
    private ConnectionService service;

    public UDPServer(ConnectionService service) {
        this.service = service;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    @Override
    public void run() {

        queue = new ArrayList<>();
        running = true;
        byte[] lMessage = new byte[256];

        try {
            socket = new DatagramSocket(33334);

            while(running){
                if (this.queue.size() > 0) {
                    System.out.println("sending");
                    DatagramPacket packet = this.queue.get(0);

//                    InetAddress address = InetAddress.getByName("192.168.178.20"); TODO remove

                    String message = new String(lMessage, 0, packet.getLength());
                    this.service.write(message.getBytes());

//                    int port = 33334; TODO remove
//                    packet.setAddress(address);
//                    packet.setPort(port);

//                    socket.send(packet); TODO remove
//                    this.buf = null;
                    this.queue.remove(0);

                    //TODO add timer if needed
                }
                continue;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void kill() {
        running = false;
    }

    public void addQueue(DatagramPacket packet) {
        System.out.println("add queue");
        this.queue.add(packet);
    }
}
