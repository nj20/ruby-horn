package none.rubyhorn.models;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;


public class Order
{
    public Map<String, Integer> items;

    public float totalPrice;

    public int totalQuantity;

    public Order()
    {
        items = new HashMap<>();
    }

    public Order(String json)
    {
        String[] data = json.split(";");

        String order = data[0];
        order = order.substring(1, order.length()-1);
        String[] keyValuePairs = order.split(",");
        items = new HashMap<>();
        for(String pair : keyValuePairs)
        {
            String[] entry = pair.split("=");
            if(entry.length == 1)
            {
                return;
            }
            items.put(entry[0].trim(), Integer.parseInt(entry[1].trim()));
        }

        String[] metaData = data[1].split(",");
        totalPrice = Float.parseFloat(metaData[0]);
        totalQuantity = Integer.parseInt(metaData[1]);

    }

    public String toString()
    {
        return items.toString() + ";" + totalPrice + "," + totalQuantity;
    }
}
