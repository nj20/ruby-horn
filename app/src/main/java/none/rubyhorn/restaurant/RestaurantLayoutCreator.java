package none.rubyhorn.restaurant;

import org.json.JSONObject;

/**
 * Created by nikolasvamvou on 8/18/17.
 */
//This class processes the backend json response and creates an object
public class RestaurantLayoutCreator {

    String name;
    String description;
    JSONObject response;

    public RestaurantLayoutCreator(JSONObject object){
        this.response = object;
    }

    public void setNameFromResponse(){

    }

    public void setDescriptionFromResponse(){

    }

    public void setImageFromResponse(){


    }



}
