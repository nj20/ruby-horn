package none.rubyhorn.activity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import none.rubyhorn.R;
import none.rubyhorn.activity.template.ActivityWithLocationPermission;
import none.rubyhorn.adapter.CheckinAdapter;
import none.rubyhorn.models.Restaurant;
import none.rubyhorn.service.RestaurantService;

public class Checkin extends ActivityWithLocationPermission
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
    }

    @Override
    public void onLocationChange(Location location)
    {
        RestaurantService restaurantService = RestaurantService.Instance(this);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        float accuracy = location.getAccuracy();

        restaurantService.getRestaurantsByLocation(latitude, longitude, accuracy, 1000000000, new Response.Listener<Restaurant[]>()
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

