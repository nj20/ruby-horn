package none.rubyhorn.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NamanJain on 22/08/2017.
 */

public class Order
{
    public Map<String, Integer> items;

    public Order()
    {
        items = new HashMap<>();
    }

    public Order(String order)
    {
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
    }
}
