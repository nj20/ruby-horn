package none.rubyhorn.views;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;

import none.rubyhorn.R;
import none.rubyhorn.models.Restaurant;

public class CheckinCardView
{
    private View checkinCard;

    public CheckinCardView(AppCompatActivity context, Restaurant restaurant, final Response.Listener<Restaurant> onClick)
    {
        checkinCard =View.inflate(context, R.layout.checkin_card, null);
        checkinCard.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView header = checkinCard.findViewById(R.id.header);
        TextView description = checkinCard.findViewById(R.id.description);
        ImageView image = checkinCard.findViewById(R.id.image);
        checkinCard.setTag(restaurant);
        header.setText(restaurant.name);
        image.setImageBitmap(restaurant.restaurantImage);
        description.setText(restaurant.description);

        checkinCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onClick.onResponse((Restaurant)view.getTag());
            }
        });
    }

    public View getView()
    {
        return checkinCard;
    }
}
