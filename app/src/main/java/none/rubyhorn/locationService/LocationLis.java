package none.rubyhorn.locationService;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

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

        //sending the lan and lon to the user in order to display restaurant
        LocationService locationRequest = new LocationService(applicationContext.getApplicationContext());
        locationRequest.getRestaurantsByLocation(location.getLatitude(), location.getLongitude());



        /*
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
        */

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
