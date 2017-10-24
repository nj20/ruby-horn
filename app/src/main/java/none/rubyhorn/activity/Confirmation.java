package none.rubyhorn.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Debug;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import none.rubyhorn.R;
import none.rubyhorn.models.MenuItem;
import none.rubyhorn.models.Order;
import none.rubyhorn.models.Restaurant;
import none.rubyhorn.models.RestaurantMenu;
import none.rubyhorn.service.OrderService;
import none.rubyhorn.views.ConfirmationView;
import none.rubyhorn.views.WaitView;

public class Confirmation extends AppCompatActivity
{
    public static Restaurant restaurant;
    public static String tableNumber;
    public static RestaurantMenu menu;
    public static Order order;

    private ConfirmationView view;
    private boolean paid;

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
                view.updateOrderTotal();
            }
        },
        new Response.Listener()
        {
            @Override
            public void onResponse(Object chefNotes)
            {
                if(!paid)
                {
                    paid = true;
                    OrderService.Instance(getApplicationContext()).sendOrder(restaurant.id, tableNumber, chefNotes.toString(), order,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response)
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
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            showErrorDialog("We could not connect to our servers. There might be a problem with your internet or our servers");
                            paid = false;
                        }
                    });
                }
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

    private void showErrorDialog(String error)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Confirmation.this);
        final View view = getLayoutInflater().inflate(R.layout.error_message, null);
        TextView header = (TextView) view.findViewById(R.id.header);
        header.setText(error);
        dialogBuilder.setView(view);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
            }

        });
        dialogBuilder.show();
    }
}
