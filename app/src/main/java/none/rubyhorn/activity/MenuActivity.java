package none.rubyhorn.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import none.rubyhorn.R;
import none.rubyhorn.models.MenuItem;
import none.rubyhorn.models.Order;
import none.rubyhorn.models.Restaurant;
import none.rubyhorn.models.RestaurantMenu;
import none.rubyhorn.service.MenuService;
import none.rubyhorn.views.MenuView;

public class MenuActivity extends AppCompatActivity
{
    private Order order;
    public static String tableNumber;
    private RestaurantMenu menu;
    private MenuView menuView;
    private static Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        MenuService.Instance(this).getMenuById(restaurant.id, new Response.Listener<RestaurantMenu>()
        {
            @Override
            public void onResponse(RestaurantMenu response)
            {
                menu = response;
                order = loadPreviousOrder(menu.restaurantId);
                setMenuView(restaurant, menu, order);
                if(tableNumber == null)
                    showTableNumberForm();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                showErrorDialog("We could not connect to our servers. There might be a problem with your internet or our servers");
            }
        });
    }

    private void showTableNumberForm()
    {
        final MenuActivity instance = this;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MenuActivity.this);
        final View view = getLayoutInflater().inflate(R.layout.menu_tablenumber, null);
        dialogBuilder.setView(view);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(R.string.checkIn, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                EditText tableNumberField = view.findViewById(R.id.tableNumberField);
                String tableNumber = tableNumberField.getText().toString();
                if(tableNumber.equals(""))
                {
                    instance.finish();
                }
                else
                {
                    instance.tableNumber = tableNumber;
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                finish();
            }
        });

        dialogBuilder.create();
        dialogBuilder.show();

    }

    private void setMenuView(final Restaurant restaurant, final RestaurantMenu menu, final Order order)
    {
        final AppCompatActivity instance = this;
        menuView = new MenuView(this, restaurant, menu, order,
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
                menuView.updateCheckoutButton(instance, order);
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
                }
                else
                {
                    order.items.put(item.id, newQuantity);
                }
                menuView.updateCheckoutButton(instance, order);
            }
        },
        new Response.Listener()
        {
            @Override
            public void onResponse(Object response)
            {
                Confirmation.restaurant = restaurant;
                Confirmation.order = order;
                Confirmation.menu = menu;
                Confirmation.tableNumber = tableNumber;
                Intent intent = new Intent(instance, Confirmation.class);
                instance.startActivity(intent);
            }
        });
    }

    private Order loadPreviousOrder(String restaurantId)
    {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.orderFile), MODE_PRIVATE);
        sharedPref.edit().clear();
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
    public void onPause()
    {
        super.onPause();
        if(menu != null)
        {
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.orderFile), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(menu.restaurantId, order.toString());
            editor.commit();
        }
    }

    public static void setRestaurant(Restaurant restaurant)
    {
        MenuActivity.restaurant = restaurant;
    }

    private void showErrorDialog(String error)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MenuActivity.this);
        final View view = getLayoutInflater().inflate(R.layout.error_message, null);
        TextView header = (TextView) view.findViewById(R.id.header);
        header.setText(error);
        dialogBuilder.setView(view);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }

        });
        dialogBuilder.show();
    }
}
