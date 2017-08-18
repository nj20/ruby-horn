package none.rubyhorn.restaurant;

import android.graphics.Bitmap;

/**
 * Created by nikolasvamvou on 8/18/17.
 */

public class Restaurant {


    public String name;
    String description;
    double distance;
    Bitmap restaurantImage;


    public Restaurant(String name, String description, double distance, Bitmap image){
        this.name = name;
        this.description = description;
        this.distance = distance;
        this.restaurantImage = image;
    }

    public Bitmap getRestaurantImage() {
        return restaurantImage;
    }

    public void setRestaurantImage(Bitmap restaurantImage) {
        this.restaurantImage = restaurantImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }






}
