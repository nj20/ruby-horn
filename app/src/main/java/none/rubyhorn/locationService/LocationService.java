package none.rubyhorn.locationService;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import none.rubyhorn.R;
import none.rubyhorn.httpService.HttpRequestManager;
import none.rubyhorn.restaurant.RestaurantController;

/**
 * Created by nikolasvamvou on 8/17/17.
 */

public class LocationService {

    Context context;
    HttpRequestManager requestManager;
    String url;

    public LocationService(Context context){
        this.context = context;
        this.requestManager = HttpRequestManager.Instance(context); //get application context
        this.url = url;
    }

    public void getRestaurantsByLocation(double lat, double lon, double acc){


        String url = context.getString(R.string.host) + "/api/restaurant/location/all";

        HashMap<String, String> locationHeader = new HashMap<String, String>();

        String latitude = Double.toString(lat);
        String longitude = Double.toString(lon);
        String accuracy = Double.toString(acc);
        //range in which to search restaurants


        String range = Double.toString(100);

        locationHeader.put("latitude", latitude);
        locationHeader.put("longitude", longitude);
        locationHeader.put("accuracy", accuracy);
        locationHeader.put("range", range);
        
        this.requestManager.makeRequest(0, url, locationHeader, null, new HttpRequestManager.Listener() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                Log.d("debug", response.toString());
                Log.i("Made1", response.toString());

                //create the components after the response

                RestaurantController restaurantController = new RestaurantController(response);

                //creates an array list with the restaurants inside the restaurantController
                restaurantController.storeRestaurants();

                //show them based on distance
                restaurantController.sortRestaurantsByDistance();

                //response

            }

            @Override
            public void onError(JSONObject error) {
                Log.d("error", error.toString());
                Log.i("Shit", "shit");
            }
        });
    }






}
