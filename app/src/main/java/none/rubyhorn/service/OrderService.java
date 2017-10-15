package none.rubyhorn.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import none.rubyhorn.R;
import none.rubyhorn.http.HttpRequestManager;
import none.rubyhorn.models.Order;

/**
 * Created by NamanJain on 26/09/2017.
 */

public class OrderService
{
    private static OrderService instance;

    public static OrderService Instance(Context context)
    {
        if(instance == null)
        {
            instance = new OrderService(context.getApplicationContext());
        }
        return instance;
    }
    //endregion

    private Context context;

    private HttpRequestManager requestManager;

    private OrderService(Context context)
    {
        this.context = context;
        requestManager = HttpRequestManager.Instance(context);
    }

    public void sendOrder(String restaurantId, String tableNumber, String chefNotes, Order order, final Response.Listener<JSONObject> onResponse, final Response.ErrorListener onError)
    {
        String url = context.getString(R.string.host) + "/api/order";
        JSONObject body = new JSONObject();
        try
        {
            body.put("items", order.toJsonArray());
            body.put("restaurantId", restaurantId);
            body.put("chefNotes", chefNotes);
            body.put("time", System.currentTimeMillis()/1000L);
            body.put("tableId", tableNumber);
        }
        catch (JSONException e)
        {
            onError.onErrorResponse(new VolleyError(e.toString()));
        }
        Log.d("error", body.toString());
        requestManager.makeRequest(Request.Method.POST, url, null, body, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                onResponse.onResponse(response);
            }
        },
        new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                onError.onErrorResponse(error);
            }
        });
    }
}
