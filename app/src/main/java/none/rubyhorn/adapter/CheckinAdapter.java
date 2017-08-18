package none.rubyhorn.adapter;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import none.rubyhorn.R;

/**
 * Returns the Checkin view (list of restaurants);
 */
public class CheckinAdapter
{
    private static CheckinAdapter instance;

    public static CheckinAdapter Instance()
    {
        if(instance == null)
        {
            instance = new CheckinAdapter();
        }
        return instance;
    }

    private CheckinAdapter()
    {

    }

    public void setView(AppCompatActivity context)
    {
        LinearLayout layout = (LinearLayout)context.findViewById(R.id.restaurantList);
        for(int count = 0; count < 10; count++)
        {
            View restaurant = View.inflate(context, R.layout.checkin_card, null);
            restaurant.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.addView(restaurant);
        }
    }
}
