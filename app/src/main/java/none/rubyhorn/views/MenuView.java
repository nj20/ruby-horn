package none.rubyhorn.views;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.Response;

import java.util.ArrayList;

import none.rubyhorn.R;
import none.rubyhorn.models.MenuItem;
import none.rubyhorn.models.MenuSection;
import none.rubyhorn.models.Order;
import none.rubyhorn.models.Restaurant;
import none.rubyhorn.models.RestaurantMenu;

public class MenuView
{
    TextView totalQuantity;
    TextView totalPrice;
    RestaurantMenu menu;
    private ArrayList<View> sectionViews;
    private MenuSection selectedSection;

    public MenuView(AppCompatActivity context, Restaurant restaurant, RestaurantMenu menu, Order order, Response.Listener<MenuItem> onAdd, Response.Listener<MenuItem> onDelete, Response.Listener onCheckout)
    {
        sectionViews = new ArrayList<>();
        selectedSection = null;
        this.menu = menu;
        clearMenu(context);
        setMenuHeader(context, restaurant);
        setMenu(context, menu, order, onAdd, onDelete);
        updateCheckoutButton(context, order, onCheckout);
        setMenuSlider(context, menu);
    }

    private void clearMenu(final AppCompatActivity context)
    {
        LinearLayout layout = (LinearLayout)context.findViewById(R.id.menuLayout);
        layout.removeAllViews();
    }

    private void setMenuHeader(final AppCompatActivity context, Restaurant restaurant)
    {
        LinearLayout layout = (LinearLayout)context.findViewById(R.id.menuLayout);
        View menuHeader = View.inflate(context, R.layout.menu_header, null);

        TextView title = menuHeader.findViewById(R.id.name);
        title.setText(restaurant.name);

        TextView description = menuHeader.findViewById(R.id.description);
        description.setText(restaurant.description);

        ImageView image = menuHeader.findViewById(R.id.image);
        image.setImageBitmap(restaurant.restaurantImage);

        layout.addView(menuHeader, 0);
    }

    private void setMenu(AppCompatActivity context, RestaurantMenu menu, Order order, Response.Listener<MenuItem> onAdd, Response.Listener<MenuItem> onDelete)
    {
        LinearLayout layout = (LinearLayout)context.findViewById(R.id.menuLayout);

        for(int sectionCount = 0; sectionCount < menu.sections.length; sectionCount++)
        {
            MenuSection section = menu.sections[sectionCount];
            View sectionView = View.inflate(context, R.layout.menu_section, null);
            setSection(context, sectionView, section, order, onAdd, onDelete);
            sectionViews.add(sectionView);
            layout.addView(sectionView);
        }
    }

    private void setSection(final AppCompatActivity context, View sectionView, MenuSection section, Order order, Response.Listener<MenuItem> onAdd, Response.Listener<MenuItem> onDelete)
    {
        LinearLayout sectionViewLayout = sectionView.findViewById(R.id.sectionLayout);

        //Setting section header
        View sectionHeaderView = View.inflate(context, R.layout.menu_section_header, null);
        TextView sectinoHeaderText = sectionHeaderView.findViewById(R.id.itemName);
        sectinoHeaderText.setText(section.name);
        sectionViewLayout.addView(sectionHeaderView);

        for(int itemCount = 0; itemCount < section.items.length; itemCount++)
        {
            MenuItem item = section.items[itemCount];
            View menuItemView = setMenuItem(context, item, order.items.get(item.id), onAdd, onDelete);
            sectionViewLayout.addView(menuItemView);
        }

        sectionView.setTag(R.string.sectionId, section.name);
    }

    private View setMenuItem(final AppCompatActivity context, MenuItem item, Integer quantity, final Response.Listener<MenuItem> onAdd, final Response.Listener<MenuItem> onDelete)
    {
        MenuItemView menuItemView = new MenuItemView(context, item, onAdd, onDelete, true);

        if(quantity != null)
            menuItemView.setQuantity(quantity, true);

        return menuItemView.getView();
    }

    public void updateCheckoutButton(AppCompatActivity context, Order order)
    {
        updateCheckoutButton(context, order, null);
    }

    private void updateCheckoutButton(AppCompatActivity context, Order order, final Response.Listener onCheckout)
    {
        ConstraintLayout checkout = (ConstraintLayout) context.findViewById(R.id.checkout);
        if(order.getTotalNumberOfItems() > 0)
        {
            checkout.setVisibility(View.VISIBLE);
        }
        else
        {
            checkout.setVisibility(View.INVISIBLE);
        }

        if(totalQuantity == null)
            totalQuantity = checkout.findViewById(R.id.quantity);

        if(totalPrice == null)
            totalPrice = checkout.findViewById(R.id.price);

        totalQuantity.setText(order.getTotalNumberOfItems() + "");
        totalPrice.setText(order.getOrderTotal(menu) + "£");

        if(onCheckout != null)
        {
            checkout.findViewById(R.id.checkoutButton).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onCheckout.onResponse(null);
                }
            });
        }
    }

    private void setMenuSlider(final AppCompatActivity context, RestaurantMenu menu)
    {
        MenuSliderView slider = new MenuSliderView(context, menu, new Response.Listener<MenuSection>()
        {
            @Override
            public void onResponse(MenuSection section)
            {
                selectedSection = section;
                setFilteredMenu(context);
            }
        }, new Response.Listener<MenuSection>()
        {
            @Override
            public void onResponse(MenuSection section)
            {
                if(selectedSection == section)
                {
                    selectedSection = null;
                }
                setFilteredMenu(context);
            }
        });
    }

    private void setFilteredMenu(AppCompatActivity context)
    {
        LinearLayout layout = (LinearLayout)context.findViewById(R.id.menuLayout);

        for(int count = 0; count < sectionViews.size(); count++)
        {
            layout.removeView(sectionViews.get(count));
        }

        if(selectedSection == null)
        {
            for(int count = 0; count < sectionViews.size(); count++)
            {
                layout.addView(sectionViews.get(count));
            }
        }
        else
        {
            for(int count = 0; count < sectionViews.size(); count++)
            {
                if(sectionViews.get(count).getTag(R.string.sectionId).equals(selectedSection.name))
                {
                    layout.addView(sectionViews.get(count));
                }

            }
        }
    }
}
