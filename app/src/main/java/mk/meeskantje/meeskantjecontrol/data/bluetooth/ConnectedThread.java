package mk.meeskantje.meeskantjecontrol.data.bluetooth;

import android.app.Dialog;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import mk.meeskantje.meeskantjecontrol.data.UDP.UDPClient;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private PacketQueue queue;
    private Dialog dialog;
    private UDPClient dataprovider;

    public ConnectedThread(BluetoothSocket socket, PacketQueue queue, Dialog dialog, UDPClient dataprovider) {
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
        this.dataprovider = dataprovider;
    }

    public void run() {
        System.out.println("CONNECTED");
        System.out.println("QUEUE SIZE: " + queue.getPackets().size());
        if (dialog != null) {
            dialog.cancel();
        }

        dataprovider.setConnectedThread(this);
//        while (true) {
//            System.out.println("SENDING");
//            if (queue.getQueueLength() > 0) {
//                DatagramPacket packet = queue.getNextPacket();
//                if (packet != null) {
//                    write(packet.getData());
//                }
//            }
//        }
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
}
