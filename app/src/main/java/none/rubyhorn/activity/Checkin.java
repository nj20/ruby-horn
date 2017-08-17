package none.rubyhorn.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import none.rubyhorn.R;
import none.rubyhorn.httpService.HttpRequestManager;
import none.rubyhorn.locationService.LocationBackend;
import none.rubyhorn.locationService.LocationLis;

public class Checkin extends AppCompatActivity
{

    LocationManager locationManager;
    LocationListener locationListener;

    //check for permission first time and adds location service permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //check if we have permission
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        getLocationPermissions();
        HttpRequestManager requestManager = HttpRequestManager.Instance(this);
        String url = getString(R.string.host) + "/api/restaurant/location/all";

        LocationBackend locationRequest = new LocationBackend(getApplicationContext(), url);
        locationRequest.getRestaurantsByLocation();
    }

    public void getLocationPermissions()
    {
        //how to access the location of the user
        //ask for location service
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //location listener, listens for changes in location
        locationListener = new LocationLis(getApplicationContext());

        //if devide is running SDK < 23 just use it without request
        if (Build.VERSION.SDK_INT < 23)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else
        {
            //we need to request user permission to location, check if we have location
            //check if we have permission, if the location has alerady been granted not first time
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                //ask for permission goes to top method
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else
            {
                //We have permission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }
}
