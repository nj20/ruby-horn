package none.rubyhorn.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import none.rubyhorn.adapter.MenuAdapter;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import none.rubyhorn.R;
import none.rubyhorn.models.Order;
import none.rubyhorn.models.RestaurantMenu;
import none.rubyhorn.service.MenuService;

public class MenuActivity extends AppCompatActivity
{
    private Order order;
    private RestaurantMenu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        Intent intent = getIntent();

        String restaurantId = intent.getExtras().getString("restaurantId");
        final String restaurantName = intent.getExtras().getString("name");
        final String restaurantDescription = intent.getExtras().getString("description");
        final String restaurantImageUrl = intent.getExtras().getString("imageUrl");
        MenuService.Instance(this).getMenuById(restaurantId, new Response.Listener<RestaurantMenu>()
        {
            @Override
            public void onResponse(RestaurantMenu response)
            {
                menu = response;
                order = loadPreviousOrder(menu.restaurantId);
                clearMenu();
                updateMenuHeader(restaurantName, restaurantDescription, restaurantImageUrl);
                updateMenu(response, order);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {

            }
        });
    }

    private void clearMenu()
    {
        MenuAdapter.Instance().clearMenu(this);
    }

    private void updateMenuHeader(String restaurantName, String restaurantDescription, String restaurantImage)
    {
        MenuAdapter.Instance().updateMenuHeader(this, restaurantName, restaurantDescription, restaurantImage);
    }

    private void updateMenu(RestaurantMenu menu, final Order order)
    {
        MenuAdapter.Instance().updateMenu(this, menu, order,
        new Response.Listener<String>()
        {
            @Override
            public void onResponse(String item)
            {
                Integer quantity = order.items.get(item);
                if(quantity == null)
                {
                    order.items.put(item, 1);
                }
                else
                {
                    order.items.put(item, order.items.get(item) + 1);
                }
            }
        },
        new Response.Listener<String>()
        {
            @Override
            public void onResponse(String item)
            {
                order.items.remove(item);
            }
        });
    }

    private Order loadPreviousOrder(String restaurantId)
    {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.orderFile), MODE_PRIVATE);
        String jsonOrder = sharedPref.getString(restaurantId, null);
        if(jsonOrder == null)
        {
            return new Order();
        }
        else
        {
            return new Order(jsonOrder);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.orderFile), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(menu.restaurantId, order.items.toString());
        editor.commit();
    }
}
