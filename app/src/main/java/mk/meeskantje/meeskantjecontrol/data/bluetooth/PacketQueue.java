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
        this.packets.remove(0);
        return tmp;
    }

    public DatagramPacket getNextDownPacket() {
        DatagramPacket tmp = this.downPackets.get(0);
        this.downPackets.remove(0);
        return tmp;
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
