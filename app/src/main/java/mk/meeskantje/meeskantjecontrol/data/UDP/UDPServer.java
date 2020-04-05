package mk.meeskantje.meeskantjecontrol.data.UDP;

import java.net.DatagramPacket;
import java.util.ArrayList;

import mk.meeskantje.meeskantjecontrol.data.bluetooth.ConnectionService;

public class UDPServer extends Thread {
    boolean running;

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

            while(running){
                if (this.queue.size() > 0) {
                    System.out.println("sending");
                    DatagramPacket packet = this.queue.get(0);

                    String message = new String(lMessage, 0, packet.getLength());
                    this.service.write(message.getBytes());
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
