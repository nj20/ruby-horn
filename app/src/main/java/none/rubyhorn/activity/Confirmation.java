package none.rubyhorn.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;

import none.rubyhorn.R;
import none.rubyhorn.models.MenuItem;
import none.rubyhorn.models.Order;
import none.rubyhorn.models.Restaurant;
import none.rubyhorn.models.RestaurantMenu;
import none.rubyhorn.views.ConfirmationView;
import none.rubyhorn.views.WaitView;

public class Confirmation extends AppCompatActivity
{
    public static Restaurant restaurant;
    public static String tableNumber;
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
        final Confirmation instance = this;
        view = new ConfirmationView(this, menu, order, tableNumber,
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
                int newQuantity = quantity - 1;
                if(newQuantity == 0)
                {
                    order.items.remove(item.id);
                    view.removeMenuItem(item);
                }
                else
                {
                    order.items.put(item.id, newQuantity);
                }
                order.totalPrice -=  item.price;
                order.totalQuantity -= 1;
                view.updateOrderTotal();
            }
        },
        new Response.Listener()
        {
            @Override
            public void onResponse(Object response)
            {
                WaitActivity.menu = menu;
                WaitActivity.addOrder(order);
                order = null;
                WaitActivity.tableNumber = tableNumber;
                Intent intent = new Intent(instance, WaitActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Context context = getApplicationContext();
                SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.orderFile), context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove(menu.restaurantId);
                editor.commit();
            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(order == null)
            return;
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.orderFile), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(menu.restaurantId, order.toString());
        editor.commit();
    }
}
