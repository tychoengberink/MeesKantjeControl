package mk.meeskantje.meeskantjecontrol.data.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class ConnectionService {
    private static final String TAG = "BluetoothService";
    private static final String appName = "MYAPP";

    private static final UUID MY_UUID_INSECURE =  UUID.fromString("8ce255c0-200a-11e0-ac64-08002000c9a66");

    private final BluetoothAdapter mBlueToothAd;
    private BluetoothDevice device;
    private UUID deviceUUID;
    private ConnectThread mConnectThread;
    private AcceptThread mAcceptThread;
    ProgressDialog dialog;
    Context context;

    private ConnectedThread mConnectedThread;

    public ConnectionService(Context context) {
        this.context = context;
        this.mBlueToothAd = BluetoothAdapter.getDefaultAdapter();
        start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() throws IOException {
            this.mmServerSocket = null;

            mBlueToothAd.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);


        }

        public void run() {
            BluetoothSocket socket = null;

            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket connectSocket;

        public ConnectThread (BluetoothDevice cDevice, UUID uuid) {
            device = cDevice;
            deviceUUID = uuid;
        }

        public void run() {
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.connectSocket = tmp;

            mBlueToothAd.cancelDiscovery();

            try {
                this.connectSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();

                try {
                    this.connectSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            connected(this.connectSocket, device);


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

    public void startClient(BluetoothDevice device, UUID uuid) {
        dialog = ProgressDialog.show(context, "Connecting bluetooth", "Loading...", true);

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread (BluetoothSocket socket) {
            this.mmSocket = socket;

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                dialog.dismiss();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];

            int bytes;

            while (true) {

                try {
                    bytes = mmInStream.read(buffer);
                    String incoming = new String(buffer, 0, bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write (byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
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

    private void connected(BluetoothSocket connectSocket, BluetoothDevice device) {
        mConnectedThread = new ConnectedThread(connectSocket);
        mConnectedThread.start();


    }

    public void write (byte[] out) {
        ConnectedThread thread;

        mConnectedThread.write(out);
    }
}
