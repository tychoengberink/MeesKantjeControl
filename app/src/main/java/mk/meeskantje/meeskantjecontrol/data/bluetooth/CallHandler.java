package mk.meeskantje.meeskantjecontrol.data.bluetooth;

import android.content.Context;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import mk.meeskantje.meeskantjecontrol.data.DataProvider;
import mk.meeskantje.meeskantjecontrol.data.response.ArrayListResponse;

public class CallHandler extends Thread {

    private JSONObject jsonObject;
    private DataProvider dataProvider;
    private OutputStream outStream;
    private InputStream inStream;
    private ArrayList<?> oldDrones, oldCountries, oldSensors, oldCoords, oldSensorLogs;

    public CallHandler(JSONObject data, Context context, OutputStream outStream, InputStream inStream) {
        this.jsonObject = data;
        this.dataProvider = new DataProvider(context);
        this.outStream = outStream;
        this.inStream = inStream;

        this.oldCoords = null;
        this.oldCountries = null;
        this.oldDrones = null;
        this.oldSensorLogs = null;
        this.oldSensors = null;
    }

    @Override
    public void run() {

        while (true) {

            droneAPICall();
            coordAPICall();
            sensorAPICall();
            sensorLogsAPICall();
            countryAPICall();

            try {
                Thread.sleep(3 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void droneAPICall() {
        dataProvider.request(DataProvider.GET_DRONES, null, new ArrayListResponse() {
            @Override
            public void response(ArrayList<?> data) {
                System.out.println("GET_DRONES " + data);
                checkForChanges(oldDrones, data);
                oldDrones = data;
            }

            @Override
            public void error(VolleyError error) {
                System.out.println(error);
            }
        });
    }

    public void coordAPICall() {
        dataProvider.request(DataProvider.GET_COORDINATES, null, new ArrayListResponse() {
            @Override
            public void response(ArrayList<?> data) {
                System.out.println("GET_COORDINATES " + data);
                checkForChanges(oldCoords, data);
                oldCoords = data;
            }

            @Override
            public void error(VolleyError error) {
                System.out.println(error);
            }
        });
    }

    public void sensorAPICall() {
        dataProvider.request(DataProvider.GET_SENSORS, null, new ArrayListResponse() {
            @Override
            public void response(ArrayList<?> data) {
                System.out.println("GET_SENSORS " + data);
                checkForChanges(oldSensors, data);
                oldSensors = data;
            }

            @Override
            public void error(VolleyError error) {
                System.out.println(error);
            }
        });
    }

    public void countryAPICall() {
        dataProvider.request(DataProvider.GET_COUNTRIES, null, new ArrayListResponse() {
            @Override
            public void response(ArrayList<?> data) {
                System.out.println("GET_COUNTRIES " + data);
                checkForChanges(oldCountries, data);
                oldCountries = data;
            }

            @Override
            public void error(VolleyError error) {
                System.out.println(error);
            }
        });
    }

    public void sensorLogsAPICall() {
        dataProvider.request(DataProvider.GET_SENSORLOGS, null, new ArrayListResponse() {
            @Override
            public void response(ArrayList<?> data) {
                System.out.println("GET_SENSORLOGS " + data);
                checkForChanges(oldSensorLogs, data);
                oldSensorLogs = data;
            }

            @Override
            public void error(VolleyError error) {
                System.out.println(error);
            }
        });
    }

    public void checkForChanges(ArrayList<?> oldList, ArrayList<?> newList) {
        ArrayList<String> newData = new ArrayList<>();

        if (oldList == null || !oldList.equals(newData)) {
            for (int i = 0; i < newList.size(); i++) {
                if (!oldList.contains(newList.get(i))) {
                    newData.add(newList.get(i).toString());
                }
            }
        }

        writeNewData(newData);
    }

    public void writeNewData(ArrayList<String> newData) {
        for (int i = 0; i < newData.size(); i++) {
            try {
                writeToArduino(newData.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeToArduino(String s) throws IOException {
        this.outStream.write(s.getBytes(StandardCharsets.UTF_8));
    }
}
