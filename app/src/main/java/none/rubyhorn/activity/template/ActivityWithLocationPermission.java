package none.rubyhorn.activity.template;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Extend this class to get activity with location permission and tracking
 * Call getLocationPermissions() to get permissions
 *  Override onLocationChange to get notified
 */

public abstract class ActivityWithLocationPermission extends AppCompatActivity
{

    protected LocationManager locationManager;
    private boolean permissionResultRecieved = false;
    //check for permission first time and adds location service permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionResultRecieved = true;
    }

    /**
     * Requests user for location permission
     */
    protected void requestLocationUpdate()
    {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

    }

    /**
     * Override to get notified about location
     * @param location
     */
    public abstract void onLocationChange(Location location);

    public void updateLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            if(permissionResultRecieved)
            {
                onLocationChange(null);
            }
            return;
        }

        if(isNetworkEnabled())
        {
            onLocationChange(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        }
        else if(isGPSEnabled())
        {
            onLocationChange(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        }
    }

    public boolean isLocationServiceEnabled()
    {
        return (isGPSEnabled() || isNetworkEnabled());
    }

    private boolean isGPSEnabled()
    {
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean isNetworkEnabled()
    {
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}
