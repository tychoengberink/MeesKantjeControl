package mk.meeskantje.meeskantjecontrol.data.bluetooth;

import java.net.DatagramPacket;
import java.util.ArrayList;

public class PacketQueue {
    private ArrayList<DatagramPacket> packets;
    private ArrayList<DatagramPacket> downPackets;

    public PacketQueue () {
        this.packets = new ArrayList<>();
    }

    public void addQueue(DatagramPacket packet) {
        this.packets.add(packet);
    }

    public DatagramPacket getNextPacket() {
        DatagramPacket tmp = this.packets.get(0);
        System.out.println("getNEXTPACKET");
        return tmp;
    }

    public void removeNextPacket() {
         this.packets.remove(0);
    }

    public DatagramPacket getNextDownPacket() {
        DatagramPacket tmp = this.downPackets.get(0);
        return tmp;
    }

    public void removeNextDownPacket() {
        this.downPackets.remove(0);
    }

    public int getQueueLength() {
        return this.packets.size();
    }

    public ArrayList<DatagramPacket> getPackets() {
        return this.packets;
    }

    public int getQueueDownLength() {
        return this.downPackets.size();
    }

    public ArrayList<DatagramPacket> getDownPackets() {
        return this.downPackets;
    }
}
