package com.example.bm121.locationsharing.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bm121 on 7/11/2016.
 */
public class Point {

    public String uid;
    public String author;
    public String color;
    public double lat;
    public double lon;
    public Map<String, Boolean> stars = new HashMap<>();



    public Point() {
        // Default constructor required for calls to DataSnapshot.getValue(Point.class)
    }

    public Point(String uid, String author, String color, double lat, double lon) {
        this.uid = uid;
        this.author = author;
        this.color = color;
        this.lat = lat;
        this.lon = lon;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("color", color);
        result.put("lat", lat);
        result.put("lon", lon);

        return result;
    }


}
