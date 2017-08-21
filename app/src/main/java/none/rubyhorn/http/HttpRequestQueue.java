package none.rubyhorn.http;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class HttpRequestQueue
{
    private static HttpRequestQueue instance;

    private RequestQueue requestQueue;

    public static HttpRequestQueue Instance(Context context)
    {
        if(instance == null)
        {
            instance = new HttpRequestQueue(context);
        }
        return instance;
    }

    private HttpRequestQueue(Context context)
    {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void add(Request request)
    {
        request.setShouldCache(false);
        requestQueue.add(request);
    }
}
