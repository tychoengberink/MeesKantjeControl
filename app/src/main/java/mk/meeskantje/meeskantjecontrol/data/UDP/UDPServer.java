package mk.meeskantje.meeskantjecontrol.data.UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class UDPServer extends Thread {
    DatagramSocket socket;
    boolean running;
    private byte[] buf = null;
    private ArrayList<byte[]> queue;


    public void setRunning(boolean running){
        this.running = running;
    }

    @Override
    public void run() {

        queue = new ArrayList<>();
        running = true;

        try {
            socket = new DatagramSocket(33333);


            while(running){
//                byte[] buf ="Hello World!".getBytes();
                setBuf();
                if (this.buf != null) {
                    DatagramPacket packet = null;
                    InetAddress addres = InetAddress.getByName("192.168.1.107");
                    int port = 33333;
                    packet = new DatagramPacket(this.buf, this.buf.length, addres, port);

                    socket.send(packet);
                    this.buf = null;

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

    public void setBuf() {
        if (this.queue.size() > 0) {
            this.buf = this.queue.get(0);
            this.queue.remove(0);
        }
    }

    public void addQueue(byte[] message) {
        this.queue.add(message);
    }
}
