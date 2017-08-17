package none.rubyhorn.locationService;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

import none.rubyhorn.R;
import none.rubyhorn.httpService.HttpRequestManager;

/**
 * Created by nikolasvamvou on 8/17/17.
 */

public class LocationBackend{

    Context context;
    HttpRequestManager requestManager;
    String url;

    public LocationBackend(Context context, String url){
        this.context = context;
        this.requestManager = HttpRequestManager.Instance(context); //get application context
        this.url = url;
    }

    public void getRestaurantsByLocation(){
        String url = context.getString(R.string.host) + "/api/restaurant/location/all";

        this.requestManager.makeRequest(0, url, new HashMap<String, String>(), null, new HttpRequestManager.Listener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("debug", response.toString());
            }

            @Override
            public void onError(JSONObject error) {
                Log.d("error", error.toString());
            }
        });
    }






}
