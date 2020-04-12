package mk.meeskantje.meeskantjecontrol.data.bluetooth;

import java.net.DatagramPacket;
import java.util.ArrayList;

public class PacketQueue {
    private ArrayList<DatagramPacket> packets;
    private ArrayList<DatagramPacket> downPackets;

    /**
     * The queue of packets that need to be send.
     */
    public PacketQueue() {
        this.packets = new ArrayList<>();
        this.downPackets = new ArrayList<>();
    }

    /**
     * Adds a packet to the upstream queue.
     * @param packet
     */
    public void addQueue(DatagramPacket packet) {
        this.packets.add(packet);
    }

    /**
     * Adds a packet to the downstream queue
     * @param packet
     */
    public void addDownQueue(DatagramPacket packet) {
        this.downPackets.add(packet);
    }

    /**
     * Returns the first packet in the queue and removes that packet from the queue.
     * @return DatagramPacket next packet in queue
     */
    public DatagramPacket getNextPacket() {
        return packets.remove(0);

    }

    /**
     * Returns the first packet in the queue and removes that packet from the queue.
     * @return DatagramPacket next packet in queue
     */
    public DatagramPacket getNextDownPacket() {
        return downPackets.remove(0);
    }

    public ArrayList<DatagramPacket> getPackets() {
        return this.packets;
    }

    public ArrayList<DatagramPacket> getDownPackets() {
        return this.downPackets;
    }
}
