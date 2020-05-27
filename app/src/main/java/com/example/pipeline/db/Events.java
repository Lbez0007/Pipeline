package com.example.pipeline.db;

import java.io.Serializable;

//Model class for table Events
//implementing Serializable in order to be able to store an Event in a sequence of bytes to a file
public class Events implements Serializable {
    private	int	id;
    private	String title;
    private	String description;
    private	String date;
    private	String category;
    private	String colour;
    private	String tasks;

    //Constructors for an Event
    public Events(String title, String description, String date, String category, String colour, String tasks) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.category = category;
        this.colour = colour;
        this.tasks = tasks;
    }

    public Events(int id, String title, String description, String date, String category, String colour, String tasks ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.category = category;
        this.colour = colour;
        this.tasks = tasks;
    }

    //getters and setters for our private modifier attributes
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getColour() {
        return colour;
    }

    public String getTasks() {
        return tasks;
    }
}
