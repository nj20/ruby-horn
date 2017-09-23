package none.rubyhorn.activity.template;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Extend this class to get activity with location permission and tracking
 * Call getLocationPermissions() to get permissions
 *  Override onLocationChange to get notified
 */

public abstract class ActivityWithLocationPermission extends AppCompatActivity {

    protected LocationListener locationListener;
    protected LocationManager locationManager;
    private long minTimeUpdate = 0;
    private long minDistanceUpdate = 0;

    //check for permission first time and adds location service permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //check if we have permission
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeUpdate, minDistanceUpdate, locationListener);
                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                onLocationChange(loc);
            }
        }
    }

    /**
     * Requests user for location permission
     */
    protected void getLocationPermissions()
    {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                onLocationChange(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle)
            {
            }

            @Override
            public void onProviderEnabled(String s)
            {
            }

            @Override
            public void onProviderDisabled(String s)
            {
            }
        };

        if (Build.VERSION.SDK_INT < 23)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeUpdate, minDistanceUpdate, locationListener);
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            onLocationChange(loc);
        }
        else
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeUpdate, minDistanceUpdate, locationListener);
                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                onLocationChange(loc);
            }
        }
    }

    /**
     * Override to get notified about location
     * @param location
     */
    public abstract void onLocationChange(Location location);

    public boolean checkLocationPermission()
    {

        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}
