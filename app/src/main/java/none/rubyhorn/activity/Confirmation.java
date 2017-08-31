package none.rubyhorn.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Response;

import none.rubyhorn.R;
import none.rubyhorn.models.MenuItem;
import none.rubyhorn.models.Order;
import none.rubyhorn.models.Restaurant;
import none.rubyhorn.models.RestaurantMenu;
import none.rubyhorn.views.ConfirmationView;

public class Confirmation extends AppCompatActivity
{
    public static Restaurant restaurant;
    public static RestaurantMenu menu;
    public static Order order;

    private ConfirmationView view;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation);
        setConfirmationPage();
    }

    private void setConfirmationPage()
    {
        view = new ConfirmationView(this, menu, order,
        new Response.Listener<MenuItem>()
        {
            @Override
            public void onResponse(MenuItem item)
            {
                Integer quantity = order.items.get(item.id);
                if (quantity == null)
                {
                    order.items.put(item.id, 1);
                }
                else
                {
                    order.items.put(item.id, order.items.get(item.id) + 1);
                }
                order.totalPrice += item.price;
                order.totalQuantity++;
                view.updateOrderTotal();
            }
        },
        new Response.Listener<MenuItem>()
        {
            @Override
            public void onResponse(MenuItem item)
            {
                int quantity = order.items.get(item.id);
                order.items.remove(item.id);
                order.totalPrice -= quantity * item.price;
                order.totalQuantity -= quantity;
                view.updateOrderTotal();
            }
        },
        new Response.Listener()
        {
            @Override
            public void onResponse(Object response)
            {

            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.orderFile), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(menu.restaurantId, order.toString());
        editor.commit();
    }
}
