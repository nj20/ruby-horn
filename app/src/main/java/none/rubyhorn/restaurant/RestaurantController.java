package none.rubyhorn.restaurant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by nikolasvamvou on 8/18/17.
 */

public class RestaurantController {

    ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
    JSONObject response;


    public RestaurantController(JSONObject object){
        this.response = object;
    }

    public void storeRestaurants() throws JSONException {

        String name;
        String description;
        String imageUrl;
        String distanceString;
        double distance;
        Bitmap restaurantImage;

        JSONArray array = null;
        try {
            array = this.response.getJSONArray("body");

            for(int i = 0; i < array.length(); i++){
                JSONObject jsonobject = array.getJSONObject(i);

                //creating the fields for the restaurant object
                name = jsonobject.getString("name");
                description = jsonobject.getString("description");
                distanceString = jsonobject.getString("distance");
                distance = roundDistance(distanceString);
                imageUrl = jsonobject.getString("photo");
                restaurantImage = downloadImage(imageUrl);


                Restaurant restaurant = new Restaurant(name, description, distance, restaurantImage);
                this.restaurants.add(restaurant);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public Bitmap downloadImage(String url) throws MalformedURLException {
        try {
            URL imageUrl = new URL(url);
            Bitmap image = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            return image;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public void sortRestaurantsByDistance(){

        Collections.sort(this.restaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant r1, Restaurant r2) {
                if(r1.distance > r2.distance){
                    return 1;
                }
                if(r1.distance < r2.distance){
                    return -1;
                }
                return 0;
            }
        });

    }

    public double roundDistance(String distance){

        double dis = Double.parseDouble(distance);
        return Math.floor(dis);

    }

    public ArrayList<Restaurant> getRestaurants(){
        return this.restaurants;
    }

}
