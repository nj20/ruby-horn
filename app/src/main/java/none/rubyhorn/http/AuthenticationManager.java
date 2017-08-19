package none.rubyhorn.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import none.rubyhorn.R;

/**
 * This class is used for generating session key in order to use the backend
 */
public class AuthenticationManager
{
    private static AuthenticationManager instance;

    public static AuthenticationManager Instance(Context context)
    {
        if(instance == null)
        {
            instance = new AuthenticationManager(context);
        }
        return instance;
    }

    private SharedPreferences sharedPref;

    private Context context;

    private SecureRandom random;

    private AuthenticationManager(Context context)
    {
        sharedPref = context.getSharedPreferences(context.getString(R.string.credentialsFile), context.MODE_PRIVATE);
        this.context = context.getApplicationContext();
        random = new SecureRandom();
    }

    /**
     * Sets the session key of the user in preference file. Use this method when the current session key has expired
     * @param listener callback
     */
    public void setSessionKey(final AuthenticationManager.Listener listener)
    {
        final String loginURL = context.getString(R.string.host) + "/api/user/login/";

        try
        {
            getUserDetails(new Listener()
            {
                @Override
                public void onResponse(Map<String, String> userCredentials)
                {
                    //After getting user credentials, attempts to login
                    RequestQueue requestQueue = HttpRequestQueue.Instance(context);
                    JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, loginURL, new JSONObject(userCredentials),
                    new Response.Listener<JSONObject>()
                    {
                        //If login is successful, we can save it in shared preference file.
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            try
                            {
                                String sessionkey = response.getJSONObject("body").getString("sessionKey");
                                Map<String, String> result = new HashMap<>();
                                result.put("sessionkey", sessionkey);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(context.getString(R.string.savedSessionkey), sessionkey);
                                editor.commit();
                                listener.onResponse(result);
                            } catch (JSONException e)
                            {
                                Map<String, String> error = new HashMap<>();
                                error.put("statusCode", "500");
                                error.put("error", "Invalid structure returned");
                                listener.onError(error);
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            NetworkResponse networkResponse = error.networkResponse;
                            //If responds with user not found, this means that the user has been deleted
                            //We must remove the currently saved user so that we can generate a new user
                            if(networkResponse.statusCode == 404)
                            {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.remove(context.getString(R.string.savedUserid));
                                editor.remove(context.getString(R.string.savedUserpassword));
                                editor.commit();
                                setSessionKey(listener);
                            }
                            else
                            {
                                String jsonError = new String(networkResponse.data);
                                Map<String, String> err = new HashMap<>();
                                err.put("error", jsonError);
                                err.put("statusCode", "" + networkResponse.statusCode);
                                listener.onError(err);
                            }
                        }
                    });
                    requestQueue.add(postRequest);
                }
                //If getUserDetails return with error
                @Override
                public void onError(Map<String, String> error)
                {
                    listener.onError(error);
                }
            });
        }
        catch (JSONException e)
        {
            Map<String, String> error = new HashMap<>();
            error.put("statusCode", "500");
            error.put("error", "Invalid structure returned");
            listener.onError(error);
        }

    }

    /**
     * Gets the user details (userid and password). If the user does note exist, it will create one
     * @param listener callback
     * @throws JSONException
     */
    public void getUserDetails(final AuthenticationManager.Listener listener) throws JSONException
    {
        final Map<String, String> userDetails = new HashMap<>();

        //Loading details
        String userid = sharedPref.getString(context.getString(R.string.savedUserid), null);
        String password = sharedPref.getString(context.getString(R.string.savedUserpassword), null);

        //If does not exist
        if(userid == null || password == null)
        {
            //Generating new userid and password
            final String generatedUserId = new BigInteger(130, random).toString(32) + System.currentTimeMillis();
            final String generatedPassword = new BigInteger(130, random).toString(32);

            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(context.getString(R.string.savedUserid), generatedUserId);
            editor.putString(context.getString(R.string.savedUserpassword), generatedPassword);

            //Setting up the request options
            String registerURL = context.getString(R.string.host) + "/api/user/";

            HashMap<String, String> body = new HashMap<>();
            body.put("_id", generatedUserId);
            body.put("password", generatedPassword);
            body.put("type", "0");

            RequestQueue requestQueue = HttpRequestQueue.Instance(context);

            //Requesting to register a new user
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, registerURL, new JSONObject(body),
            //If successfully registers, we store the newly generated userid and password
            new Response.Listener<JSONObject>()
            {

                @Override
                public void onResponse(JSONObject response)
                {
                    userDetails.put("_id", generatedUserId);
                    userDetails.put("password", generatedPassword);
                    listener.onResponse(userDetails);
                    editor.apply();
                }
            },
            new Response.ErrorListener()
            {

                @Override
                public void onErrorResponse(VolleyError error)
                {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null)
                    {
                        String jsonError = new String(networkResponse.data);
                        Map<String, String> err = new HashMap<>();
                        err.put("error", jsonError);
                        err.put("statusCode", "" + networkResponse.statusCode);
                        listener.onError(err);
                    }

                }
            });
            requestQueue.add(postRequest);

        }
        else
        {
            userDetails.put("_id", userid);
            userDetails.put("password", password);
            listener.onResponse(userDetails);
        }

    }

    public interface Listener
    {
        void onResponse(Map<String, String> data);

        void onError(Map<String, String> error);
    }

}
