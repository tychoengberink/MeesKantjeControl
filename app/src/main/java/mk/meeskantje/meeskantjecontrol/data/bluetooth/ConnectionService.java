package mk.meeskantje.meeskantjecontrol.data.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.provider.Settings.NameValueTable.NAME;

public class ConnectionService {
    private static final String TAG = "BluetoothService";
    private static final UUID DEFAULT_SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private PacketQueue queue;
    private boolean paused;

    private final BluetoothAdapter mBlueToothAd;

    private ConnectThread mConnectThread;
    private AcceptThread mAcceptThread;
    private ConnectedThread manageMyConnectedSocket;
    ProgressDialog dialog;
    Context context;


    public ConnectionService(PacketQueue queue, Context context) {
        this.context = context;
        this.mBlueToothAd = BluetoothAdapter.getDefaultAdapter();
        this.queue = queue;
        start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() throws IOException {
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBlueToothAd.listenUsingRfcommWithServiceRecord(NAME, DEFAULT_SPP_UUID);

            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    pauseSender();
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.

                    if (manageMyConnectedSocket == null) {
                        manageMyConnectedSocket = new ConnectedThread(socket);
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
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket connectSocket;
        private BluetoothDevice device;


        public ConnectThread(BluetoothDevice cDevice) {
            device = cDevice;
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(DEFAULT_SPP_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            connectSocket = tmp;
        }

        public void run() {
            mBlueToothAd.cancelDiscovery();

            try {
                System.out.println("connecting");
                connectSocket.connect();
            } catch (IOException e) {
                System.out.println("error");
                e.printStackTrace();

                try {
                    connectSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            manageMyConnectedSocket = new ConnectedThread(connectSocket);
            manageMyConnectedSocket.start();

        }

        private void cancel() {
            try {
                this.connectSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void start() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mAcceptThread == null) {
            try {
                mAcceptThread = new AcceptThread();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mAcceptThread.start();
        }
    }

    public void startClient(BluetoothDevice device) {
        dialog = ProgressDialog.show(context, "Connecting bluetooth", "Loading...", true);

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
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
            dialog.cancel();
        }

        public void run() {
            while (true) {
                if (queue.getQueueLength() > 0 && !paused) {
                    System.out.println("Sending");
                    DatagramPacket packet = queue.getNextPacket();
                    if (packet != null) {
                        write(packet.getData());
                    }
                }
            }
        }

        public void write(byte[] bytes) {
            try {
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


    public void pauseSender () {
                System.out.println("pausing sender");
                this.paused = true;
            }

            public void resumeSender () {
                System.out.println("resuming sender");
                this.paused = false;
            }
        }
