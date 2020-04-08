package mk.meeskantje.meeskantjecontrol.data.bluetooth;

import android.app.Dialog;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;

import mk.meeskantje.meeskantjecontrol.data.UDP.UDPSocket;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private PacketQueue queue;
    private Dialog dialog;
    private UDPSocket dataHandler;

    public ConnectedThread(BluetoothSocket socket, PacketQueue queue, Dialog dialog, UDPSocket dataHandler) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        this.queue = queue;
        this.dialog = dialog;
        this.dataHandler = dataHandler;
    }

    public void run() {
        System.out.println("CONNECTED");
        System.out.println("QUEUE SIZE: " + queue.getPackets().size());
        if (dialog != null) {
            dialog.cancel();
        }

        dataHandler.getReciever().setConnectedThread(this);
        byte[] buffer = new byte[256];
        int bytes;

        // Keep looping to listen for received messages
        while (true) {
            try {
                bytes = mmInStream.read(buffer);            //read bytes from input buffer
                String readMessage = new String(buffer, 0, bytes);
                InetAddress addres = InetAddress.getByName("192.168.1.79");
                int port = 33333;
                DatagramPacket packet = null;
                byte[] buf = readMessage.getBytes();
                packet = new DatagramPacket(buf, buf.length, addres, port);
                dataHandler.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] bytes) {
        try {
            System.out.println(mmOutStream);
            mmOutStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream getMmInStream() {
        return mmInStream;
    }
}
