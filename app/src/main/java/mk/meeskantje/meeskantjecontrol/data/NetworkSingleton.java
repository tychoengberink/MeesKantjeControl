package mk.meeskantje.meeskantjecontrol.data;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkSingleton {
    private static NetworkSingleton instance;
    private static Context ctx;
    private RequestQueue requestQueue;

    private NetworkSingleton(Context ctx){
        this.ctx = ctx;
    }

    public static synchronized NetworkSingleton getInstance(Context ctx){
        if(instance == null)instance = new NetworkSingleton(ctx);
        return instance;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }
}
