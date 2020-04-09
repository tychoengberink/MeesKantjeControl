package mk.meeskantje.meeskantjecontrol.data.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.UUID;

import mk.meeskantje.meeskantjecontrol.data.UDP.UDPSocket;

public class ConnectionService {
    private PacketQueue queue;
    private static final UUID DEFAULT_SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private boolean paused;
    private final BluetoothAdapter mBlueToothAd;
    private ConnectThread mConnectThread;
    private AcceptThread mAcceptThread;
    private UDPSocket dataHandler;

    ProgressDialog dialog;
    Context context;

    public ConnectionService(PacketQueue queue, Context context, UDPSocket dataHandler) {
        this.context = context;
        this.mBlueToothAd = BluetoothAdapter.getDefaultAdapter();
        this.queue = queue;
        this.dataHandler = dataHandler;
        start();
    }

    /**
     * Starts the acceptThread.
     */
    public synchronized void start() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread(mBlueToothAd, DEFAULT_SPP_UUID, queue, dialog, dataHandler);
            mAcceptThread.start();
        }
    }

    /**
     * Defines and starts a new ConnectThread.
     * @param device the Bluetooth device.
     */
    public void startClient(BluetoothDevice device) {
        dialog = ProgressDialog.show(context, "Connecting bluetooth", "Loading...", true);

        mConnectThread = new ConnectThread(device, mBlueToothAd, DEFAULT_SPP_UUID, queue, dialog, dataHandler);
        mConnectThread.start();
    }

    public ConnectThread getmConnectThread() {
        return mConnectThread;
    }

    public AcceptThread getmAcceptThread() {
        return mAcceptThread;
    }
}

