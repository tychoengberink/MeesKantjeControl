package mk.meeskantje.meeskantjecontrol.data.bluetooth;

import java.net.DatagramPacket;
import java.util.ArrayList;

public class PacketQueue {
    private ArrayList<DatagramPacket> packets;

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

    public int getQueueLength() {
        return this.packets.size();
    }

    public ArrayList<DatagramPacket> getPackets() {
        return this.packets;
    }
}
