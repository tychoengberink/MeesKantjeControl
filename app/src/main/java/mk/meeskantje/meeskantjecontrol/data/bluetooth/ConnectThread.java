package mk.meeskantje.meeskantjecontrol.data.bluetooth;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

import mk.meeskantje.meeskantjecontrol.data.UDP.UDPClient;

public class ConnectThread extends Thread {
    private static final String TAG = "BluetoothService";
    private BluetoothSocket connectSocket;
    private BluetoothDevice device;
    private ConnectedThread manageMyConnectedSocket;
    private BluetoothAdapter mBlueToothAd;
    private PacketQueue queue;
    private Dialog dialog;
    private UDPClient dataprovider;

    public ConnectThread(BluetoothDevice cDevice, BluetoothAdapter mBlueToothAd, UUID default_ssp, PacketQueue queue, Dialog dialog, UDPClient dataprovider) {
        device = cDevice;
        BluetoothSocket tmp = null;

        try {
            tmp = device.createRfcommSocketToServiceRecord(default_ssp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mBlueToothAd = mBlueToothAd;
        connectSocket = tmp;
        this.queue = queue;
        this.dialog = dialog;
        this.dataprovider = dataprovider;
    }

    public void run() {
        mBlueToothAd.cancelDiscovery();
        System.out.println(mBlueToothAd.isDiscovering());
        try {
            connectSocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connectSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        manageMyConnectedSocket = new ConnectedThread(connectSocket, queue, dialog, dataprovider);
        manageMyConnectedSocket.start();

    }

    public void cancel() {
        try {
            this.connectSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConnectedThread getManageMyConnectedSocket() {
        return manageMyConnectedSocket;
    }
}