package com.company;

import java.io.Serializable;

public class FirstNFLine implements Serializable {
    public int cat_id;
    public String cat_name;
    public String color;
    public int breed_id;
    public String breed;
    public int place_id;
    public String place_name;
    public String type;
    public int food_id;
    public String food_name;
    public int price;

    public FirstNFLine(int cat_id, String cat_name, String color, int breed_id, String breed, int place_id, String place_name, String type, int food_id, String food_name, int price)
    {
        this.cat_id = cat_id;
        this.cat_name = cat_name;
        this.color = color;
        this.breed = breed;
        this.place_id = place_id;
        this.place_name = place_name;
        this.type = type;
        this.food_id = food_id;
        this.food_name = food_name;
        this.price = price;
        this.breed_id = breed_id;
    }

    public String toString(){
        return cat_name + " " + place_name + " " + food_name;
    }
}
