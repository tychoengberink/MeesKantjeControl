package mk.meeskantje.meeskantjecontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.UUID;

import mk.meeskantje.meeskantjecontrol.data.UDP.UDPClient;
import mk.meeskantje.meeskantjecontrol.data.UDP.UDPServer;
import mk.meeskantje.meeskantjecontrol.data.bluetooth.ConnectionService;
import mk.meeskantje.meeskantjecontrol.data.bluetooth.DeviceListAdapter;
import mk.meeskantje.meeskantjecontrol.data.bluetooth.PacketQueue;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView deviceList;
    Button onOffButton;
    Button startButton;

    private UDPClient dataProvider;
    private UDPServer dataSender;
    private PacketQueue queue;

    private static final String TAG = "MainActivity";
    public ArrayList<BluetoothDevice> devices;
    public DeviceListAdapter deviceListAdapter;

    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-08002000c9a66");

    private BluetoothAdapter bluetoothAdapter;
    private ConnectionService connectionService;

    private BluetoothDevice mainDevice;


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onReceive: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "onReceive: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver broadcastReceiverSec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "onReceiveSec: Discoverability Enabled.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "onReceiveSec: Able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "onReceiveSec: Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "onReceiveSec: Connected.");
                        break;
                }
            }
        }
    };

    private BroadcastReceiver broadcastReceiverThird = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device);
                Log.d(TAG, "onReceiveThird: " + device.getName() + ": " + device.getAddress());
                deviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, devices);
                deviceList.setAdapter(deviceListAdapter);
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Log.d(TAG, "onReceiveThird: Connected.");
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "onReceiveThird: Discovery finished.");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                Log.d(TAG, "onReceiveThird: Requested disconnection.");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Log.d(TAG, "onReceiveThird: Disconnected.");
            }
        }
    };

    private BroadcastReceiver broadcastReceiverFour = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "onReceiveFour: Bonded.");
                    mainDevice = device;
                    connectionService.resumeSender();
                }

                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "onReceiveFour: Bonding.");
                }

                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "onReceiveFour: No Bond.");
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(broadcastReceiverSec);
        unregisterReceiver(broadcastReceiverThird);
        unregisterReceiver(broadcastReceiverFour);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = new PacketQueue();

        onOffButton = findViewById(R.id.onOffButton);
        startButton = findViewById(R.id.start_connection);

        devices = new ArrayList<>();
        deviceList = findViewById(R.id.device_list);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        deviceList.setOnItemClickListener(MainActivity.this);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadcastReceiverFour, filter);

        onOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothSwitch();
            }
        });

    }

    public void bluetoothSwitch() {
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableIntent);

                IntentFilter bluetoothIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(broadcastReceiver, bluetoothIntent);
            } else {
                bluetoothAdapter.disable();

                IntentFilter bluetoothIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(broadcastReceiver, bluetoothIntent);
            }
        } else {
            Log.d(TAG, "NO BLUETOOTH AVAILABLE");
        }
    }

    public void btnDiscover(View view) {
        Log.d(TAG, "Looking for devices.");

        this.devices.clear();

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "Canceling discovery");

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            discoverDevicesIntent.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            discoverDevicesIntent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
            discoverDevicesIntent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            registerReceiver(broadcastReceiverThird, discoverDevicesIntent);
        }

        if (!bluetoothAdapter.isDiscovering()) {
            checkPermissions();

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiverThird, discoverDevicesIntent);
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permission = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permission += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");

            if (permission != 0) {
                this.requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                }, 1001);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        bluetoothAdapter.cancelDiscovery();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "Trying to pair with " + devices.get(i).getName());
            devices.get(i).createBond();
            mainDevice = devices.get(i);
            connectionService = new ConnectionService(queue, this);
        }
    }

    public void startBluetoothConnection(View view) {
        System.out.println("starting Client");
        connectionService.startClient(mainDevice);
    }

    protected void onResume() {
        dataSender = new UDPServer(queue);
        dataSender.start();
        dataProvider = new UDPClient(queue);
        dataProvider.start();
        super.onResume();
    }

    protected void onPause() {
        dataSender.kill();
        dataProvider.kill();
        super.onPause();
    }



    @Override
    protected void onStop() {
        dataSender.kill();
        dataProvider.kill();

        super.onStop();
    }
}
