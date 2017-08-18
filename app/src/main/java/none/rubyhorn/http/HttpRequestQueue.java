package none.rubyhorn.http;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class HttpRequestQueue
{
    private static RequestQueue instance;

    public static RequestQueue Instance(Context context)
    {
        if(instance == null)
        {
            instance = Volley.newRequestQueue(context);
        }
        return instance;
    }
}
