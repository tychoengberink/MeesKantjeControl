package mk.meeskantje.meeskantjecontrol.data.UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer extends Thread {
    DatagramSocket socket;
    boolean running;


    public void setRunning(boolean running){
        this.running = running;
    }

    @Override
    public void run() {

        running = true;

        try {
            socket = new DatagramSocket(33333);


            while(running){
                byte[] buf ="Hello World!".getBytes();
                DatagramPacket packet = null;

                InetAddress addres = InetAddress.getByName("192.168.1.79");
                int port = 33333;
                packet = new DatagramPacket(buf, buf.length, addres, port);

                socket.send(packet);
                continue;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void kill() {
        running = false;
    }
}
