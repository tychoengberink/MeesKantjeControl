package mk.meeskantje.meeskantjecontrol;

import org.junit.Test;

import java.net.DatagramPacket;

import mk.meeskantje.meeskantjecontrol.data.bluetooth.PacketQueue;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class QueueTests {

    @Test
    public void addQueueTest() {
        PacketQueue queue = new PacketQueue();
        String test = "Hello World";
        DatagramPacket packet = new DatagramPacket(test.getBytes(), 0, 0);

        queue.addQueue(packet);
        queue.addDownQueue(packet);

        assertTrue(queue.getPackets().contains(packet));
        assertEquals(queue.getPackets().size(), 1);
        assertTrue(queue.getDownPackets().contains(packet));
        assertEquals(queue.getDownPackets().size(), 1);
    }

    @Test
    public void getFromQueueTest() {
        PacketQueue queue = new PacketQueue();
        String test = "Hello World";
        DatagramPacket packet = new DatagramPacket(test.getBytes(), 0, 0);

        queue.addQueue(packet);
        queue.addDownQueue(packet);

        assertEquals(queue.getNextPacket(), packet);
        assertEquals(queue.getPackets().size(), 0);

        assertEquals(queue.getNextDownPacket(), packet);
        assertEquals(queue.getDownPackets().size(), 0);
    }
}