package none.rubyhorn.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import none.rubyhorn.adapter.MenuAdapter;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import none.rubyhorn.R;
import none.rubyhorn.models.RestaurantMenu;
import none.rubyhorn.service.MenuService;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Intent intent = getIntent();

        String restaurantId = intent.getExtras().getString("id");
        final String restaurantName = intent.getExtras().getString("name");
        final String restaurantDescription = intent.getExtras().getString("description");
        final String restaurantImageUrl = intent.getExtras().getString("imageUrl");
        MenuService.Instance(this).getMenuById(restaurantId, new Response.Listener<RestaurantMenu>()
        {
            @Override
            public void onResponse(RestaurantMenu response)
            {
                clearMenu();
                updateMenuHeader(restaurantName, restaurantDescription, restaurantImageUrl);
                updateMenu(response);
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

    private void updateMenu(RestaurantMenu menu)
    {
        MenuAdapter.Instance().updateMenu(this, menu);
    }
}
