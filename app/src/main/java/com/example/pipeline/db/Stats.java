package com.example.pipeline.db;

import java.io.Serializable;

//Model class for table Stats
//implementing Serializable in order to be able to store a Stat in a sequence of bytes to a file
public class Stats implements Serializable {
    private	String category;
    private	String colour;
    private	int count;

    //Constructors for a productivity Stat
    public Stats(String category, String colour, int count) {
        this.category = category;
        this.colour = colour;
        this.count = count;
    }

    //getter methods for private attributes in Stats model
    public String getCategory() {
        return category;
    }

    public String getColour() {
        return "#" + colour.substring(3); //returns colour in HEX #RRGGBB, instead of #AARRGGBB (# + RRGGBB)
    }

    public int getCount() {
        return count;
    }

}
