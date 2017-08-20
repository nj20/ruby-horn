package none.rubyhorn.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import none.rubyhorn.http.HttpRequestQueue;

public class Restaurant implements Serializable
{
    public String name;
    public String description;
    public double distance;
    public Bitmap restaurantImage;
    public String id;
    public String url;

    public Restaurant(String id, String name, String description, double distance, Bitmap image, String url)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.distance = distance;
        this.restaurantImage = image;
        this.url = url;
    }

    public static void parse(Context context, final JSONObject json, final Response.Listener<Restaurant> listener, final Response.ErrorListener errorListener)
    {
        try
        {
            downloadImage(json.getString("photo"), new Response.Listener<Bitmap>()
            {
                @Override
                public void onResponse(Bitmap restaurantImage)
                {
                    try
                    {
                        String id = json.getString("_id");
                        String name = json.getString("name");
                        String description = json.getString("description");
                        String url = json.getString("photo");
                        double distance = Double.parseDouble(json.getString("distance"));
                        listener.onResponse(new Restaurant(id, name, description, distance, restaurantImage, url));
                    }catch(Exception e)
                    {
                        errorListener.onErrorResponse(new VolleyError("Exception while converting JSON to resturant"));
                    }
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    errorListener.onErrorResponse(error);
                }
            }, context);
        } catch (Exception e)
        {
            errorListener.onErrorResponse(new VolleyError(e.toString()));
        }
    }

    public static void parse(Context context, final JSONArray array, final Response.Listener<Restaurant[]> listener, final Response.ErrorListener errorListener) throws JSONException, IOException
    {
        final ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();

        for(int i = 0; i < array.length(); i++)
        {
            JSONObject json = array.getJSONObject(i);
            parse(context, json, new Response.Listener<Restaurant>()
            {
                @Override
                public void onResponse(Restaurant restaurant)
                {
                    restaurants.add(restaurant);
                    if(restaurants.size() == array.length())
                    {
                        Restaurant[] r = new Restaurant[restaurants.size()];
                        listener.onResponse(restaurants.toArray(r));
                    }
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    errorListener.onErrorResponse(error);
                }
            });
        }
    }

    private static void downloadImage(String url, Response.Listener<Bitmap> listener, Response.ErrorListener errorListener, Context context) throws IOException
    {
        ImageRequest request = new ImageRequest(url, listener, 10000, 10000, ImageView.ScaleType.CENTER_CROP, null, errorListener);
        HttpRequestQueue.Instance(context).add(request);
    }
}
