package none.rubyhorn.adapter;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.TouchDelegate;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.json.JSONException;
import org.json.JSONObject;

import none.rubyhorn.R;
import none.rubyhorn.http.HttpRequestQueue;
import none.rubyhorn.models.MenuItem;
import none.rubyhorn.models.MenuSection;
import none.rubyhorn.models.Order;
import none.rubyhorn.models.RestaurantMenu;

public class MenuAdapter
{
    private static MenuAdapter instance;

    public static MenuAdapter Instance()
    {
        if(instance == null)
        {
            instance = new MenuAdapter();
        }
        return instance;
    }

    private MenuAdapter()
    {
    }

    public void clearMenu(final AppCompatActivity context)
    {
        LinearLayout layout = (LinearLayout)context.findViewById(R.id.menuLayout);
        layout.removeAllViews();
    }

    public void updateMenuHeader(final AppCompatActivity context, final String restaurantName, final String restaurantDescription, String restaurantImage)
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

    public void updateMenu(final AppCompatActivity context, RestaurantMenu menu, Order order, Response.Listener<String> onAdd, Response.Listener<String> onDelete)
    {
        LinearLayout layout = (LinearLayout)context.findViewById(R.id.menuLayout);

        for(int sectionCount = 0; sectionCount < menu.sections.length; sectionCount++)
        {
            MenuSection section = menu.sections[sectionCount];
            View sectionView = View.inflate(context, R.layout.menu_section, null);
            updateSection(context, sectionView, section, order, onAdd, onDelete);
            layout.addView(sectionView);
        }
    }

    private void updateSection(final AppCompatActivity context, View sectionView, MenuSection section, Order order, Response.Listener<String> onAdd, Response.Listener<String> onDelete)
    {
        LinearLayout sectionViewLayout = sectionView.findViewById(R.id.sectionLayout);

        //Setting section header
        View sectionHeaderView = View.inflate(context, R.layout.menu_section_header, null);
        TextView sectinoHeaderText = sectionHeaderView.findViewById(R.id.sectionHeader);
        sectinoHeaderText.setText(section.name);
        sectionViewLayout.addView(sectionHeaderView);

        for(int itemCount = 0; itemCount < section.items.length; itemCount++)
        {
            View menuItemView = View.inflate(context, R.layout.menu_item, null);
            MenuItem item = section.items[itemCount];
            updateMenuItem(context, menuItemView, item, order.items.get(item.id), onAdd, onDelete);
            sectionViewLayout.addView(menuItemView);
        }
    }

    private void updateMenuItem(final AppCompatActivity context, View menuItemView, MenuItem item, Integer quantity, final Response.Listener<String> onAdd, final Response.Listener<String> onDelete)
    {
        menuItemView.setTag(item.id);

        TextView itemName = menuItemView.findViewById(R.id.name);
        itemName.setText(item.name);

        TextView description = menuItemView.findViewById(R.id.description);
        description.setText(item.description);

        TextView price = menuItemView.findViewById(R.id.price);
        price.setText(item.price + "£");

        if(quantity != null)
        {
            final TextView counter = menuItemView.findViewById(R.id.quantity);
            counter.setVisibility(View.VISIBLE);
            counter.setText("x" + quantity);

            final View delete = menuItemView.findViewById(R.id.deleteButton);
            delete.setVisibility(View.VISIBLE);
        }

        menuItemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onAdd.onResponse(view.getTag().toString());
                onMenuItemClick(context, (ConstraintLayout)view, onDelete);
            }
        });
    }

    private void onMenuItemClick(final AppCompatActivity context, ConstraintLayout view, final Response.Listener<String> onDelete)
    {
        final TextView counter = view.findViewById(R.id.quantity);
        final View delete = view.findViewById(R.id.deleteButton);

        Animation textAnimation = AnimationUtils.loadAnimation(context, R.anim.view_pop);
        textAnimation.reset();
        Animation imageAnimation = AnimationUtils.loadAnimation(context, R.anim.view_appear);
        imageAnimation.reset();
        if(counter.getVisibility() == View.INVISIBLE)
        {
            textAnimation = AnimationUtils.loadAnimation(context, R.anim.view_appear);
            delete.clearAnimation();
            delete.startAnimation(imageAnimation);
        }
        counter.clearAnimation();;
        counter.startAnimation(textAnimation);

        counter.setVisibility(View.VISIBLE);
        incrementCounter(counter);
        delete.setVisibility(View.VISIBLE);

        if(!delete.hasOnClickListeners())
        {
            final String id = view.getTag().toString();
            delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onDelete.onResponse(id);
                    counter.setVisibility(View.INVISIBLE);
                    resetCounter(counter);
                    delete.setVisibility(View.INVISIBLE);
                }
            });
        }
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
