package none.rubyhorn.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import none.rubyhorn.R;

/**
 * This class is used for making HTTP requests to the backend.
 * To access singleton instance, call the static method "Instance".
 */
public class HttpRequestManager
{

    //region SingleTon
    private static HttpRequestManager instance;

    public static HttpRequestManager Instance(Context context)
    {
        if(instance == null)
        {
            instance = new HttpRequestManager(context.getApplicationContext());
        }
        return instance;
    }
    //endregion

    private Context context;

    private SharedPreferences sharedPref;

    private HttpRequestManager(Context context)
    {
        this.context = context;
        sharedPref = context.getSharedPreferences(context.getString(R.string.credentialsFile), context.MODE_PRIVATE);
    }

    /**
     * Make a REST API call using this method
     * @param method
     * @param url
     * @param headers
     * @param body
     * @param listener
     */
    public void makeRequest(final int method, final String url, final Map<String, String> headers, final JSONObject body, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener)
    {
        //Making the request
        JsonObjectRequest getRequest = new JsonObjectRequest(method, url, body,
        new Response.Listener<JSONObject>()
        {
            //If successful, trigger callback
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d("innerResponse", response.toString());
                listener.onResponse(response);
            }
        },
        new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                NetworkResponse networkResponse = error.networkResponse;
                //If Unauthorized
                if(networkResponse.statusCode == 401)
                {
                    //Generate new session key
                    AuthenticationManager.Instance(context).setSessionKey(new Response.Listener<Map<String, String>>() {
                        //After generating new session key, we retry the request
                        @Override
                        public void onResponse(Map<String, String> data) {
                            makeRequest(method, url, headers, body, listener, errorListener);
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
                else
                {
                    String jsonError = new String(networkResponse.data);
                    Map<String, String> err = new HashMap<>();
                    err.put("error", jsonError);
                    err.put("statusCode", "" + networkResponse.statusCode);
                    errorListener.onErrorResponse(error);
                }
            }
        })
        {
            //Retrieving session key and putting in headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> h = new HashMap<>();
                if(headers != null)
                {
                    h = headers;
                }
                //if session key is not saved yet, it will be assigned as default.
                //If that is the case, the request will respond with unauthorized
                // and the error handler will automatically generate a new session key and store it
                String sessionkey = sharedPref.getString(context.getString(R.string.savedSessionkey), "default");
                h.put("sessionkey", sessionkey);
                Log.d("header", h.toString());
                return h;
            }
        };
        HttpRequestQueue.Instance(context).add(getRequest, false);
    }
}
