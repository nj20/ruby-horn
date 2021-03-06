package none.rubyhorn.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import none.rubyhorn.R;
import none.rubyhorn.activity.template.ActivityWithLocationPermission;
import none.rubyhorn.models.Restaurant;
import none.rubyhorn.service.RestaurantService;
import none.rubyhorn.util.InternetConnection;
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

        if(!InternetConnection.isConntected(this))
        {
            showErrorDialog("It seems that you are not connected to the internet. Please check your connection and try again");
        }
        else
        {
            //Gets location update. When this method is called, onLocationChange is triggered.
            requestLocationUpdate();
            if(!isLocationServiceEnabled())
            {
                showErrorDialog("We were not able to get your location in order to suggest you restaurants. Please check your location service and try again");
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updateLocation();
        MenuActivity.tableNumber = null;
    }

    @Override
    public void onLocationChange(Location location)
    {
        //Once we get the location, we do not need to request further updates
        if(location == null)
        {
            showErrorDialog("We were not able to get your location in order to suggest you restaurants. Please check your location service and try again");
            return;
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
        dialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }

        });
        dialogBuilder.show();
    }
}

