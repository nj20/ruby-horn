package none.rubyhorn.adapter;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import none.rubyhorn.R;
import none.rubyhorn.models.Restaurant;

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

    public void updateRestaurantList(AppCompatActivity context, Restaurant[] restaurants)
    {
        LinearLayout layout = (LinearLayout)context.findViewById(R.id.restaurantList);
        layout.removeAllViews();
        for(int count = 0; count < restaurants.length; count++)
        {
            View restaurant = View.inflate(context, R.layout.checkin_card, null);
            TextView header = restaurant.findViewById(R.id.header);
            TextView description = restaurant.findViewById(R.id.description);
            ImageView image = restaurant.findViewById(R.id.image);
            restaurant.setTag(restaurants[count].id);
            header.setText(restaurants[count].name);
            image.setImageBitmap(restaurants[count].restaurantImage);
            description.setText(restaurants[count].description);

            restaurant.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            restaurant.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    view.getTag().toString();
                }
            });
            layout.addView(restaurant);
        }
    }
}

