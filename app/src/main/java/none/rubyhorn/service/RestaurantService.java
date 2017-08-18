package none.rubyhorn.service;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import none.rubyhorn.R;
import none.rubyhorn.http.HttpRequestManager;
import none.rubyhorn.models.Restaurant;

public class RestaurantService
{
    //region Singleton
    private static RestaurantService instance;

    public static RestaurantService Instance(Context context)
    {
        if(instance == null)
        {
            instance = new RestaurantService(context.getApplicationContext());
        }
        return instance;
    }
    //endregion

    private Context context;

    private HttpRequestManager requestManager;

    private RestaurantService(Context context)
    {
        this.context = context;
        requestManager = HttpRequestManager.Instance(context);
    }

    public void getRestaurantsByLocation(double lat, double lon, double acc, double range, final Response.Listener<Restaurant[]> listener, final Response.ErrorListener errorListener)
    {
        String url = context.getString(R.string.host) + "/api/restaurant/location/all";

        HashMap<String, String> locationHeader = new HashMap<>();

        locationHeader.put("latitude", Double.toString(lat));
        locationHeader.put("longitude", Double.toString(lon));
        locationHeader.put("accuracy", Double.toString(acc));
        locationHeader.put("range", Double.toString(range));

        this.requestManager.makeRequest(0, url, locationHeader, null, new HttpRequestManager.Listener()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                   Restaurant.parse(context, response.getJSONArray("body"), new Response.Listener<Restaurant[]>() {
                       @Override
                       public void onResponse(Restaurant[] restaurants)
                       {
                            listener.onResponse(restaurants);
                       }
                   }, new Response.ErrorListener()
                   {
                       @Override
                       public void onErrorResponse(VolleyError error)
                       {
                            errorListener.onErrorResponse(error);
                       }
                   });
                } catch (Exception e)
                {
                    errorListener.onErrorResponse(new VolleyError(e.toString()));
                }
            }
            @Override
            public void onError(JSONObject error)
            {
                errorListener.onErrorResponse(new VolleyError(error.toString()));
            }
        });
    }
}
