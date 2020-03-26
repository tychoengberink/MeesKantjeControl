package mk.meeskantje.meeskantjecontrol.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

/*
All repsonses
*/
import mk.meeskantje.meeskantjecontrol.data.response.ArrayListResponse;
import mk.meeskantje.meeskantjecontrol.data.response.CoordinateResponse;
import mk.meeskantje.meeskantjecontrol.data.response.CountryResponse;
import mk.meeskantje.meeskantjecontrol.data.response.DroneResponse;
import mk.meeskantje.meeskantjecontrol.data.response.JsonArrayResponse;
import mk.meeskantje.meeskantjecontrol.data.response.JsonObjectResponse;
import mk.meeskantje.meeskantjecontrol.data.response.ProviderResponse;
import mk.meeskantje.meeskantjecontrol.data.response.SensorLogResponse;
import mk.meeskantje.meeskantjecontrol.data.response.SensorResponse;
import mk.meeskantje.meeskantjecontrol.model.Coordinate;
import mk.meeskantje.meeskantjecontrol.model.Country;
import mk.meeskantje.meeskantjecontrol.model.Drone;
import mk.meeskantje.meeskantjecontrol.model.Sensor;
import mk.meeskantje.meeskantjecontrol.model.SensorLog;
/*
All model
import mk.meeskantje.meeskantjecontrol.model.Group;
*/

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


//TODO Fix Object or Array Nightmare
public class DataProvider {

    private Context ctx;

    //Api URL here
    //TODO add right api
    private final static String API = "https://project-networking.mennospijker.nl/public/";

    //Actions
    //TODO add right actions
    public static final String GET_COUNTRY = "GET_COUNTRY";
    public static final String GET_COORDINATE = "GET_COORDINATE";
    public static final String GET_DRONE = "GET_DRONE";
    public static final String GET_SENSOR = "GET_SENSOR";
    public static final String GET_SENSORLOG = "GET_SENSORLOG";


    public static final String GET_COUNTRIES = "GET_COUNTRIES";
    public static final String GET_COORDINATES = "GET_COORDINATES";
    public static final String GET_DRONES = "GET_DRONES";
    public static final String GET_SENSORS = "GET_SENSORS";
    public static final String GET_SENSORLOGS = "GET_SENSORLOGS";

    public DataProvider(Context ctx) {
        this.ctx = ctx;
        NukeSSLCerts.nuke();
    }

    /**
     * This method makes a request to the webserver.
     *
     * @param action           action to take.
     * @param parameters       (Optional) Parameters to be send with the request.
     * @param providerResponse The response of the request, should be a subclass of the ProviderResponse interface depending on the action.
     */
    public void request(final String action, final HashMap<String, String> parameters, final ProviderResponse providerResponse) {
        JsonObjectRequest jsonObjectRequest = null;
        String URL = "";

        switch (action) {
            case GET_COUNTRY:
                URL = API + "api/countries?id=" + parameters.get("id");
                break;
            case GET_COUNTRIES:
                URL = API + "api/countries";
                break;
            case GET_COORDINATE:
                URL = API + "api/coordinates?id=" + parameters.get("id");
                break;
            case GET_COORDINATES:
                URL = API + "api/coordinates";
                break;
            case GET_DRONE:
                URL = API + "api/drones?id=" + parameters.get("id");
                break;
            case GET_DRONES:
                URL = API + "api/drones";
                break;
            case GET_SENSOR:
                URL = API + "api/sensors?id=" + parameters.get("id");
                break;
            case GET_SENSORS:
                URL = API + "api/sensors";
                break;
            case GET_SENSORLOG:
                URL = API + "api/sensor-logs?id=" + parameters.get("id");
                break;
            case GET_SENSORLOGS:
                URL = API + "api/sensor-logs";
                break;
        }
        JSONObject jsonObject = null;
        if (parameters != null) {
            jsonObject = new JSONObject(parameters);
        }

        objectRequest(action, URL, jsonObject, providerResponse);
    }

    private void objectRequest(final String action, String URL, JSONObject parameters, final ProviderResponse providerResponse) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("data");
                    switch (action) {
                        case GET_COUNTRY:
                            JSONObject countryobject = array.getJSONObject(0);
                            CountryResponse countryResponse = (CountryResponse) providerResponse;
                            Country country = new Country(countryobject.getInt("id"), countryobject.getString("name"), countryobject.getString("code"), countryobject.getString("description"));
                            countryResponse.response(country);
                            break;
                        case GET_COUNTRIES:
                            ArrayListResponse countryArrayListResponse = (ArrayListResponse) providerResponse;
                            ArrayList<Country> countries = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                countries.add(new Country(array.getJSONObject(i).getInt("id"), array.getJSONObject(i).getString("name"), array.getJSONObject(i).getString("code"), array.getJSONObject(i).getString("description")));
                            }
                            countryArrayListResponse.response(countries);
                            break;
                        case GET_COORDINATE:
                            JSONObject coordinateobject = array.getJSONObject(0);
                            CoordinateResponse coordinateResponse = (CoordinateResponse) providerResponse;
                            Coordinate coordinate = new Coordinate(coordinateobject.getInt("id"), coordinateobject.getInt("drone_id"), (float) coordinateobject.getDouble("x"), (float) coordinateobject.getDouble("y"), (float) coordinateobject.getDouble("z"));
                            coordinateResponse.response(coordinate);
                            break;
                        case GET_COORDINATES:
                            ArrayListResponse coordinatesArrayListResponse = (ArrayListResponse) providerResponse;
                            ArrayList<Coordinate> coordinates = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                coordinates.add(new Coordinate(array.getJSONObject(i).getInt("id"), array.getJSONObject(i).getInt("drone_id"), (float) array.getJSONObject(i).getDouble("x"), (float) array.getJSONObject(i).getDouble("y"), (float) array.getJSONObject(i).getDouble("z")));
                            }
                            coordinatesArrayListResponse.response(coordinates);
                            break;
                        case GET_DRONE:
                            JSONObject droneobject = array.getJSONObject(0);
                            DroneResponse droneResponse = (DroneResponse) providerResponse;
                            Drone drone = new Drone(droneobject.getInt("id"), droneobject.getInt("country_id"));
                            droneResponse.response(drone);
                            break;
                        case GET_DRONES:
                            ArrayListResponse dronesArrayListResponse = (ArrayListResponse) providerResponse;
                            ArrayList<Drone> drones = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                drones.add(new Drone(array.getJSONObject(i).getInt("id"), array.getJSONObject(i).getInt("country_id")));
                            }
                            dronesArrayListResponse.response(drones);
                            break;
                        case GET_SENSOR:
                            JSONObject sensorobject = array.getJSONObject(0);
                            SensorResponse sensorResponse = (SensorResponse) providerResponse;
                            Sensor sensor = new Sensor(sensorobject.getInt("id"), sensorobject.getString("type"), sensorobject.getString("description"));
                            sensorResponse.response(sensor);
                            break;
                        case GET_SENSORS:
                            ArrayListResponse sensorsArrayListResponse = (ArrayListResponse) providerResponse;
                            ArrayList<Sensor> sensors = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                sensors.add(new Sensor(array.getJSONObject(i).getInt("id"), array.getJSONObject(i).getString("type"), array.getJSONObject(i).getString("description")));
                            }
                            sensorsArrayListResponse.response(sensors);
                            break;
                        case GET_SENSORLOG:
                            JSONObject sensorlogobject = array.getJSONObject(0);
                            SensorLogResponse sensorLogResponse = (SensorLogResponse) providerResponse;
                            SensorLog sensorLog = new SensorLog(sensorlogobject.getInt("id"), sensorlogobject.getInt("sensor_id"), sensorlogobject.getInt("coordinate_id"), sensorlogobject.getString("value"));
                            sensorLogResponse.response(sensorLog);
                            break;
                        case GET_SENSORLOGS:
                            ArrayListResponse sensorlogsArrayListResponse = (ArrayListResponse) providerResponse;
                            ArrayList<SensorLog> sensorlogs = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                sensorlogs.add(new SensorLog(array.getJSONObject(i).getInt("id"), array.getJSONObject(i).getInt("sensor_id"), array.getJSONObject(i).getInt("coordinate_id"), array.getJSONObject(i).getString("value")));
                            }
                            sensorlogsArrayListResponse.response(sensorlogs);
                            break;
                    }
                } catch (JSONException JSONe) {
                    JSONe.getStackTrace();
                    System.out.println(JSONe.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                providerResponse.error(error);
            }
        }) {
        };

        System.out.println();
        NetworkSingleton.getInstance(ctx).addToRequestQueue(jsonObjectRequest);
    }


    public void customObjectRequest(int method, String URL, final JSONObject parameters, final JsonObjectResponse jsonObjectResponse) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (method, API + URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            jsonObjectResponse.response(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        jsonObjectResponse.error(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //TODO change code
                params.put("Content-Type", "application/json");
                // params.put("secretcode", "24091999");
                return params;
            }

            @Override
            public byte[] getBody() {
                String requestBody = parameters.toString();
                try {
                    return requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        NetworkSingleton.getInstance(ctx).addToRequestQueue(jsonObjectRequest);
    }

    public void customArrayRequest(int method, String URL, final JSONObject parameters, final JsonArrayResponse jsonArrayResponse) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (method, API + URL, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        jsonArrayResponse.response(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        jsonArrayResponse.error(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //TODO change code
                // params.put("secretcode", "24091999");
                params.put("Content-Type", "application/json");
                return params;
            }

            @Override
            public byte[] getBody() {
                String requestBody = parameters.toString();
                try {
                    return requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        NetworkSingleton.getInstance(ctx).addToRequestQueue(jsonArrayRequest);
    }
}