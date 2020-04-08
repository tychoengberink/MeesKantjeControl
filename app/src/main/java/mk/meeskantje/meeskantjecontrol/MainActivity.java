package mk.meeskantje.meeskantjecontrol;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import mk.meeskantje.meeskantjecontrol.data.UDP.UDPSocket;
import mk.meeskantje.meeskantjecontrol.data.bluetooth.ConnectionService;
import mk.meeskantje.meeskantjecontrol.data.bluetooth.DeviceListAdapter;
import mk.meeskantje.meeskantjecontrol.data.bluetooth.PacketQueue;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView deviceList;
    Button onOffButton;
    Button startButton;

    private UDPSocket dataHandler;
    private PacketQueue queue;
    private boolean selected;

    private static final String TAG = "MainActivity";
    public ArrayList<BluetoothDevice> devices;
    public DeviceListAdapter deviceListAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private ConnectionService connectionService;
    private BluetoothDevice mainDevice;
    private final static int REQUEST_ENABLE_BT = 1;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device);
                Log.d(TAG, "onReceiveThird: " + device.getName() + ": " + device.getAddress());
                deviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, devices);
                deviceList.setAdapter(deviceListAdapter);
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.selected = false;

        queue = new PacketQueue();

        onOffButton = findViewById(R.id.onOffButton);
        startButton = findViewById(R.id.start_connection);

        devices = new ArrayList<>();
        deviceList = findViewById(R.id.device_list);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        deviceList.setOnItemClickListener(MainActivity.this);

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
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

            } else {
                if (connectionService != null) {
                    if (connectionService.getmConnectThread() != null) {
                        connectionService.getmConnectThread().cancel();
                    }
                    if (connectionService.getmAcceptThread() != null) {
                        connectionService.getmAcceptThread().cancel();
                    }
                }
                bluetoothAdapter.disable();
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
        }

        if (!bluetoothAdapter.isDiscovering()) {
            checkPermissions();

            bluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);
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
        this.selected = true;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "Trying to pair with " + devices.get(i).getName());
            devices.get(i).createBond();
            mainDevice = devices.get(i);
            connectionService = new ConnectionService(queue, this, dataHandler);
        }
    }

    public void startBluetoothConnection(View view) {
        if (this.selected) {
            System.out.println("starting Client");

            connectionService.startClient(mainDevice);

        }
    }

    protected void onResume() {
        dataHandler = new UDPSocket(33333, queue);
        super.onResume();
    }

    protected void onPause() {
        dataHandler.stop();
        super.onPause();
    }

    @Override
    protected void onStop() {
        dataHandler.stop();
        super.onStop();
    }
}
