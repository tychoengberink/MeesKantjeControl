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

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

import mk.meeskantje.meeskantjecontrol.data.bluetooth.ConnectionService;
import mk.meeskantje.meeskantjecontrol.data.bluetooth.DeviceListAdapter;

import mk.meeskantje.meeskantjecontrol.data.DataProvider;
import mk.meeskantje.meeskantjecontrol.data.response.ArrayListResponse;
import mk.meeskantje.meeskantjecontrol.data.response.CoordinateResponse;
import mk.meeskantje.meeskantjecontrol.data.response.CountryResponse;
import mk.meeskantje.meeskantjecontrol.data.response.DroneResponse;
import mk.meeskantje.meeskantjecontrol.data.response.SensorLogResponse;
import mk.meeskantje.meeskantjecontrol.data.response.SensorResponse;
import mk.meeskantje.meeskantjecontrol.model.Coordinate;
import mk.meeskantje.meeskantjecontrol.model.Country;
import mk.meeskantje.meeskantjecontrol.model.Drone;
import mk.meeskantje.meeskantjecontrol.model.Sensor;
import mk.meeskantje.meeskantjecontrol.model.SensorLog;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView deviceList;
    Button onOffButton;
    Button discoverable;
    Button startButton;
    TextView sendingData;
    TextView receivingData;

    private DataProvider dataProvider;
    private List<Coordinate> listCoordinate;
    private List<Country> listCountry;
    private List<Drone> listDrone;
    private List<Sensor> listSensor;
    private List<SensorLog> listSensorLog;

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

        dataProvider = new DataProvider(this);

        listCoordinate = new ArrayList<>();
        listCountry = new ArrayList<>();
        listDrone = new ArrayList<>();
        listSensor = new ArrayList<>();
        listSensorLog = new ArrayList<>();

        onOffButton = findViewById(R.id.onOffButton);
        discoverable = findViewById(R.id.discover);
        startButton = findViewById(R.id.start_connection);

        sendingData = findViewById(R.id.sending_info);
        receivingData = findViewById(R.id.receiving_info);

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


        HashMap<String, String> parametersCountry = new HashMap<String, String>();
        parametersCountry.put("id", "1");
        dataProvider.request(DataProvider.GET_COUNTRY, parametersCountry, new CountryResponse() {
            @Override
            public void response(Country data) {
                System.out.println("GET_COUNTRY " + data);
            }

            @Override
            public void error(VolleyError error) {
                System.out.println(error);
            }
        });

        dataProvider.request(DataProvider.GET_COUNTRIES, null, new ArrayListResponse() {
            @Override
            public void response(ArrayList<?> data) {
                System.out.println("GET_COUNTRIES " + data);
            }

            @Override
            public void error(VolleyError error) {
                System.out.println(error);
            }
        });

        HashMap<String, String> parametersCoordinates = new HashMap<String, String>();
        parametersCoordinates.put("id", "1");
        dataProvider.request(DataProvider.GET_COORDINATE, parametersCoordinates, new CoordinateResponse() {
            @Override
            public void response(Coordinate data) {
                System.out.println("GET_COORDINATE " + data);
            }

            @Override
            public void error(VolleyError error) {
                System.out.println(error);
            }
        });

        dataProvider.request(DataProvider.GET_COORDINATES, null, new ArrayListResponse() {
            @Override
            public void response(ArrayList<?> data) {
                System.out.println("GET_COORDINATES " + data);
            }

            @Override
            public void error(VolleyError error) {
                System.out.println(error);
            }
        });

        HashMap<String, String> parametersDrones = new HashMap<String, String>();
        parametersDrones.put("id", "1");
        dataProvider.request(DataProvider.GET_DRONE, parametersDrones, new DroneResponse() {
            @Override
            public void response(Drone data) {
                System.out.println("GET_DRONE " + data);
            }

            @Override
            public void error(VolleyError error) {
                System.out.println(error);
            }
        });

        dataProvider.request(DataProvider.GET_DRONES, null, new ArrayListResponse() {
            @Override
            public void response(ArrayList<?> data) {
                System.out.println("GET_DRONES " + data);
            }

            @Override
            public void error(VolleyError error) {
                System.out.println(error);
            }
        });

        HashMap<String, String> parametersSensor = new HashMap<String, String>();
        parametersSensor.put("id", "1");
        dataProvider.request(DataProvider.GET_SENSOR, parametersSensor, new SensorResponse() {
            @Override
            public void response(Sensor data) {
                System.out.println("GET_SENSOR " + data);
            }

            @Override
            public void error(VolleyError error) {
                System.out.println(error);
            }
        });

        dataProvider.request(DataProvider.GET_SENSORS, null, new ArrayListResponse() {
            @Override
            public void response(ArrayList<?> data) {
                System.out.println("GET_SENSORS " + data);
            }

            @Override
            public void error(VolleyError error) {
                System.out.println(error);
            }
        });

        HashMap<String, String> parameterssensorLogs = new HashMap<String, String>();
        parameterssensorLogs.put("id", "1");
        dataProvider.request(DataProvider.GET_SENSORLOG, parameterssensorLogs, new SensorLogResponse() {
            @Override
            public void response(SensorLog data) {
                System.out.println("GET_SENSORLOG " + data);
            }

            @Override
            public void error(VolleyError error) {
                System.out.println(error);
            }
        });

        dataProvider.request(DataProvider.GET_SENSORLOGS, null, new ArrayListResponse() {
            @Override
            public void response(ArrayList<?> data) {
                System.out.println("GET_SENSORLOGS " + data);
            }

            @Override
            public void error(VolleyError error) {
                System.out.println(error);
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

    public void btnEnableDisable_Discoverable(View view) {
        Log.d(TAG, "Device discoverable for 300 seconds.");

        Intent discoverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverIntent);

        IntentFilter intentFilter = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(broadcastReceiverSec, intentFilter);
    }

    public void btnDiscover(View view) {
        Log.d(TAG, "Looking for devices.");

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "Canceling discovery");

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
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
            connectionService = new ConnectionService(this);
        }
    }

    public void startBluetoothConnection(View view) {
        connectionService.startClient(mainDevice);
    }

    public void setReceivedData(String data) {
        receivingData.setText(data);
    }

    public void setSendingData(String data) {
        sendingData.setText(data);
    }
}
