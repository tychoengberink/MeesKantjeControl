package mk.meeskantje.meeskantjecontrol.data.bluetooth;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import mk.meeskantje.meeskantjecontrol.data.UDP.UDPClient;

import static android.provider.Settings.NameValueTable.NAME;

public class AcceptThread extends Thread {
    private static final String TAG = "BluetoothService";
    private final BluetoothServerSocket mmServerSocket;
    private ConnectedThread manageMyConnectedSocket;
    private PacketQueue queue;
    private Dialog dialog;
    private UDPClient dataprovider;

    public AcceptThread(BluetoothAdapter mBlueToothAd, UUID default_ssp, PacketQueue queue, Dialog dialog, UDPClient dataprovider) {
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            tmp = mBlueToothAd.listenUsingRfcommWithServiceRecord(NAME, default_ssp);

        } catch (IOException e) {
            Log.e(TAG, "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
        this.queue = queue;
        this.dialog = dialog;
        this.dataprovider = dataprovider;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.

                if (manageMyConnectedSocket == null) {
                    manageMyConnectedSocket = new ConnectedThread(socket, queue, dialog, dataprovider);
                    manageMyConnectedSocket.start();
                }
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void cancel() {
        if (mmServerSocket != null) {
            try {
                this.mmServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
