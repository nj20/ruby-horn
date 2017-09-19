package none.rubyhorn.models;

import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Order
{
    public Map<String, Integer> items;

    public Order()
    {
        items = new HashMap<>();
    }

    public Order(String string)
    {
        String[] data = string.split(";");

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
    }

    public double getOrderTotal(RestaurantMenu menu)
    {
        double totalPrice = 0;
        Iterator it = items.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<String, Integer> pair = (Map.Entry)it.next();
            totalPrice += menu.getItem(pair.getKey()).price * pair.getValue();
        }
        return totalPrice;
    }

    public int getTotalNumberOfItems()
    {
        int totalNumber = 0;
        Iterator it = items.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<String, Integer> pair = (Map.Entry)it.next();
            totalNumber += pair.getValue();
        }
        return totalNumber;
    }

    public String toString()
    {
        return items.toString();
    }
}
