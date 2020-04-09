package mk.meeskantje.meeskantjecontrol.data.bluetooth;

import java.net.DatagramPacket;
import java.util.ArrayList;

public class PacketQueue {
    private ArrayList<DatagramPacket> packets;
    private ArrayList<DatagramPacket> downPackets;

    public PacketQueue() {
        this.packets = new ArrayList<>();
        this.downPackets = new ArrayList<>();
    }

    public void addQueue(DatagramPacket packet) {
        this.packets.add(packet);
    }

    public void addDownQueue(DatagramPacket packet) {
        this.downPackets.add(packet);
    }

    public DatagramPacket getNextPacket() {
        return packets.remove(0);

    }

    public DatagramPacket getNextDownPacket() {
        return downPackets.remove(0);
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
