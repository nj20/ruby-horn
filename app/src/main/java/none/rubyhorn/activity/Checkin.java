package none.rubyhorn.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import none.rubyhorn.R;
import none.rubyhorn.activity.template.ActivityWithLocationPermission;
import none.rubyhorn.models.Restaurant;
import none.rubyhorn.service.RestaurantService;
import none.rubyhorn.views.RestaurantListView;

/**
 * The first activity that happens.
 * Allows the user to check in to a restaurant
 */
public class Checkin extends ActivityWithLocationPermission
{
    private RestaurantListView restaurantListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin);
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
                addRestaurantList(restaurants);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                showErrorDialog("We could not connect to our servers. There might be a problem with your internet or our servers");
            }
        });
    }

    private void addRestaurantList(Restaurant[] restaurants)
    {
        restaurantListView = new RestaurantListView(this, restaurants, new Response.Listener<Restaurant>()
        {
            @Override
            public void onResponse(Restaurant restaurant)
            {
                showMenuActivity(restaurant);
            }
        });
    }

    private void showMenuActivity(Restaurant restaurant)
    {
        Intent intent = new Intent(this, MenuActivity.class);
        MenuActivity.setRestaurant(restaurant);
        startActivity(intent);
    }

    private void showErrorDialog(String error)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Checkin.this);
        final View view = getLayoutInflater().inflate(R.layout.error_message, null);
        TextView header = (TextView) view.findViewById(R.id.header);
        header.setText(error);
        dialogBuilder.setView(view);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                finish();
            }

        });
        dialogBuilder.show();
    }
}

