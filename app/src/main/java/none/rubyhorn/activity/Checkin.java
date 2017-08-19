package none.rubyhorn.activity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import none.rubyhorn.R;
import none.rubyhorn.activity.template.ActivityWithLocationPermission;
import none.rubyhorn.adapter.CheckinAdapter;
import none.rubyhorn.models.Restaurant;
import none.rubyhorn.service.RestaurantService;

/**
 * The first activity that happens.
 * Allows the user to check in to a restaurant
 */
public class Checkin extends ActivityWithLocationPermission
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //Requests for location every time activity is resumed
        getLocationPermissions();
    }

    @Override
    public void onLocationChange(Location location)
    {
        //Once we get the location, we do not need to request further updates
        if(location == null)
        {
            return;
        }
        else
        {
            locationManager.removeUpdates(locationListener);
        }

        RestaurantService restaurantService = RestaurantService.Instance(this);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        float accuracy = location.getAccuracy();
        float range = 1000000000;

        restaurantService.getRestaurantsByLocation(latitude, longitude, accuracy, range, new Response.Listener<Restaurant[]>()
        {
            @Override
            public void onResponse(Restaurant[] restaurants)
            {
                updateRestaurants(restaurants);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("ERROR", error.toString());
            }
        });
    }

    private void updateRestaurants(Restaurant[] restaurants)
    {
        CheckinAdapter checkinAdapter = CheckinAdapter.Instance();
        checkinAdapter.updateRestaurantList(this, restaurants);
    }
}

