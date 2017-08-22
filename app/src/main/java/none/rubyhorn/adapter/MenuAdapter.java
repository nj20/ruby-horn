package none.rubyhorn.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import none.rubyhorn.R;
import none.rubyhorn.http.HttpRequestQueue;
import none.rubyhorn.models.MenuItem;
import none.rubyhorn.models.MenuSection;
import none.rubyhorn.models.RestaurantMenu;

public class MenuAdapter
{
    private static MenuAdapter instance;

    public static MenuAdapter Instance(AppCompatActivity context)
    {
        if(instance == null)
        {
            instance = new MenuAdapter(context);
        }
        return instance;
    }

    private AppCompatActivity context;

    private MenuAdapter(AppCompatActivity context)
    {
        this.context = context;
    }

    public void clearMenu()
    {
        LinearLayout layout = (LinearLayout)context.findViewById(R.id.menuLayout);
        layout.removeAllViews();;
    }

    public void updateMenuHeader(final String restaurantName, final String restaurantDescription, String restaurantImage)
    {
        ImageRequest request = new ImageRequest(restaurantImage, new Response.Listener<Bitmap>()
        {
            @Override
            public void onResponse(Bitmap response)
            {
                LinearLayout layout = (LinearLayout)context.findViewById(R.id.menuLayout);
                View menuHeader = View.inflate(context, R.layout.menu_header, null);

                TextView title = menuHeader.findViewById(R.id.name);
                title.setText(restaurantName);

                TextView description = menuHeader.findViewById(R.id.description);
                description.setText(restaurantDescription);

                ImageView image = menuHeader.findViewById(R.id.image);
                image.setImageBitmap(response);

                layout.addView(menuHeader, 0);
            }
        }, 10000, 10000, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {

            }
        });
        HttpRequestQueue.Instance(context).add(request);
    }

    public void updateMenu(RestaurantMenu menu)
    {
        LinearLayout layout = (LinearLayout)context.findViewById(R.id.menuLayout);

        for(int sectionCount = 0; sectionCount < menu.sections.length; sectionCount++)
        {
            MenuSection section = menu.sections[sectionCount];
            View sectionView = View.inflate(context, R.layout.menu_section, null);
            LinearLayout sectionViewLayout = sectionView.findViewById(R.id.sectionLayout);

            View sectionHeaderView = View.inflate(context, R.layout.menu_section_header, null);
            TextView sectinoHeaderText = sectionHeaderView.findViewById(R.id.sectionHeader);
            sectinoHeaderText.setText(section.name);

            sectionViewLayout.addView(sectionHeaderView);

            for(int itemCount = 0; itemCount < section.items.length; itemCount++)
            {
                View menuItem = View.inflate(context, R.layout.menu_item, null);
                MenuItem item = section.items[itemCount];

                TextView itemName = menuItem.findViewById(R.id.name);
                itemName.setText(item.name);

                TextView description = menuItem.findViewById(R.id.description);
                description.setText(item.description);

                TextView price = menuItem.findViewById(R.id.price);
                price.setText(item.price + "£");
                menuItem.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View view)
                    {
                        onMenuItemClick((ConstraintLayout)view);
                    }
                });
                sectionViewLayout.addView(menuItem);
            }
            layout.addView(sectionView);
        }
    }

    private void onMenuItemClick(ConstraintLayout view)
    {
        final TextView counter = view.findViewById(R.id.quantity);
        counter.setVisibility(View.VISIBLE);
        incrementCounter(counter);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.text_pop);
        animation.reset();
        counter.clearAnimation();;
        counter.startAnimation(animation);

        final View delete = view.findViewById(R.id.deleteButton);
        delete.setVisibility(View.VISIBLE);
        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               counter.setVisibility(View.INVISIBLE);
                resetCounter(counter);
                delete.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void incrementCounter(TextView counter)
    {
        int numberOfItems = Integer.parseInt(counter.getText().toString().substring(1)) + 1;
        counter.setText("x" + numberOfItems);
    }

    private void resetCounter(TextView counter)
    {
        counter.setText("x0");
    }
}
