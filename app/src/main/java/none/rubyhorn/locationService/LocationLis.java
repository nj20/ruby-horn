package none.rubyhorn.locationService;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationLis implements android.location.LocationListener
{
    private Context applicationContext;

    public LocationLis(Context context)
    {
        applicationContext = context.getApplicationContext();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.d("debug", "COMES");
        //last int how many you want
        try
        {
            Geocoder geocoder = new Geocoder(applicationContext, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);



            //using real device works

            if(addressList != null && addressList.size() > 0)
            {
                Log.i("PlaceInfo", addressList.get(0).toString());
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        Log.i("Location", location.toString());
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
}
