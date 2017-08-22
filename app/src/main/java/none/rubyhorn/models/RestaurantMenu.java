package none.rubyhorn.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RestaurantMenu
{
    public String restaurantId;
    public MenuSection[] sections;

    public static RestaurantMenu parse(JSONObject json) throws JSONException
    {
        RestaurantMenu menu = new RestaurantMenu();
        menu.restaurantId = json.getString("_id");
        JSONArray sections = json.getJSONArray("sections");

        MenuSection[] parsedSections = new MenuSection[sections.length()];
        for(int sectionCount = 0; sectionCount < sections.length(); sectionCount++)
        {
            JSONObject section = sections.getJSONObject(sectionCount);
            JSONArray menuItems = section.getJSONArray("menuItems");
            String sectionName = section.getString("name");
            parsedSections[sectionCount] = new MenuSection();
            parsedSections[sectionCount].name = sectionName;

            parsedSections[sectionCount].items = new MenuItem[menuItems.length()];
            for(int itemCount = 0; itemCount < menuItems.length(); itemCount++)
            {
                JSONObject menuItem = menuItems.getJSONObject(itemCount);
                MenuItem item = new MenuItem();
                item.name = menuItem.getString("name");
                item.id = menuItem.getString("_id");
                item.price = menuItem.getDouble("price");
                item.description = menuItem.getString("description");
                parsedSections[sectionCount].items[itemCount] = item;
            }
        }
        menu.sections = parsedSections;
        return menu;
    }
}
