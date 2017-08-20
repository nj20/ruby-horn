package none.rubyhorn.service;

import android.content.Context;
import android.util.Log;
import android.view.Menu;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import none.rubyhorn.R;
import none.rubyhorn.http.HttpRequestManager;
import none.rubyhorn.models.RestaurantMenu;

public class MenuService
{
    //region Singleton
    private static MenuService instance;

    public static MenuService Instance(Context context)
    {
        if(instance == null)
        {
            instance = new MenuService(context.getApplicationContext());
        }
        return instance;
    }
    //endregion

    private Context context;

    private HttpRequestManager requestManager;

    private MenuService(Context context)
    {
        this.context = context;
        requestManager = HttpRequestManager.Instance(context);
    }

    public void getMenuById(String id, final Response.Listener<RestaurantMenu> listener, final Response.ErrorListener errorListener)
    {
        String url = context.getString(R.string.host) + "/api/menu";
        Map<String, String> headers = new HashMap<>();
        headers.put("restaurantid", id);
        requestManager.makeRequest(Request.Method.GET, url, headers, null, new HttpRequestManager.Listener()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    listener.onResponse(RestaurantMenu.parse(response.getJSONObject("body")));
                } catch (JSONException e)
                {
                    errorListener.onErrorResponse(new VolleyError(response.toString()));
                }
            }

            @Override
            public void onError(JSONObject error)
            {
                errorListener.onErrorResponse(new VolleyError(error.toString()));
            }
        });
    }

}
