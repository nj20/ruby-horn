package none.rubyhorn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuAdapter;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.Response;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import none.rubyhorn.R;
import none.rubyhorn.models.Order;
import none.rubyhorn.models.RestaurantMenu;
import none.rubyhorn.views.WaitView;

public class WaitActivity extends AppCompatActivity
{
    private static Order order;
    public static RestaurantMenu menu;
    public static String tableNumber;

    private WaitView view;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait);
        setView();
    }

    private void setView()
    {
        final AppCompatActivity instance = this;
        view = new WaitView(this, tableNumber, order, menu, new Response.Listener() {
            @Override
            public void onResponse(Object response)
            {
                Intent intent = new Intent(instance, MenuActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
    }

    public static void addOrder(Order newOrder)
    {
        if(order == null)
        {
            setOrder();
        }
        order.totalPrice += newOrder.totalPrice;
        order.totalQuantity += newOrder.totalQuantity;
        Iterator it = newOrder.items.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<String, Integer> pair = (Map.Entry)it.next();
            String itemId = pair.getKey();
            int quantity = pair.getValue();
            if(order.items.containsKey(itemId))
            {
                order.items.put(itemId, order.items.get(itemId) + quantity);
            }
            else
            {
                order.items.put(itemId, quantity);
            }
            it.remove();
        }
    }

    private static void setOrder()
    {
        order = new Order();
        order.items = new HashMap<>();
    }
}
