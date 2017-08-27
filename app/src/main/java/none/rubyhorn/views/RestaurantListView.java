package none.rubyhorn.views;

import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.android.volley.Response;

import none.rubyhorn.R;
import none.rubyhorn.models.Restaurant;

public class RestaurantListView
{
    private LinearLayout restaurantList;

    public RestaurantListView(AppCompatActivity context, Restaurant[] restaurants, Response.Listener<Restaurant> onClick)
    {
        restaurantList = (LinearLayout)context.findViewById(R.id.restaurantList);
        restaurantList.removeAllViews();
        for(int count = 0; count < restaurants.length; count++)
        {
            CheckinCardView checkinCardView = new CheckinCardView(context, restaurants[count], onClick);
            restaurantList.addView(checkinCardView.getView());
        }
    }

    public LinearLayout getView()
    {
        return restaurantList;
    }
}
