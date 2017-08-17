package none.rubyhorn.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

import none.rubyhorn.R;
import none.rubyhorn.httpService.HttpRequestManager;

public class Checkin extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        HttpRequestManager requestManager = HttpRequestManager.Instance(this);

        requestManager.makeRequest(0, "http://golden-bird-qe-756441617.us-west-2.elb.amazonaws.com:80/api/restaurant/allForCustomer", new HashMap<String, String>(), null, new HttpRequestManager.Listener()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d("debug", response.toString());
            }

            @Override
            public void onError(JSONObject error)
            {
                Log.d("error", error.toString());
            }
        });
    }
}
