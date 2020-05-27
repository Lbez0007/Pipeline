package com.example.pipeline.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase; //class which exposes methods to manage a SQLite database.
import android.database.sqlite.SQLiteOpenHelper; //helper class to manage database creation and version management.
import android.content.ContentValues;
import android.database.Cursor;
import com.example.pipeline.db.EventsParameters.EventEntry; //EventEntry contains parameters for our Events table
import java.util.ArrayList;

//extending SQLiteOpenHelper to be able to access and manage our SQLite database instance
public class EventsReaderDbHelper extends SQLiteOpenHelper {
    //Scope - to access database and provide CRUD methods for Pipeline application

    private	static final int DATABASE_VERSION =	5;
    private	static final String	DATABASE_NAME = "pipeline"; //name for database used for app

    //constructor for a Db helper, to provide access to CRUD methods to other classes
    public EventsReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creation of table upon first execution on device
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Injecting SQL query using SQLiteOpenHelper
        String	CREATE_EVENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + EventEntry.TABLE_NAME
                + "(" + EventEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + EventEntry.COLUMN_TITLE + " TEXT,"
                + EventEntry.COLUMN_DESCRIPTION + " TEXT,"
                + EventEntry.COLUMN_DATE + " TEXT,"
                + EventEntry.COLUMN_CATEGORY + " TEXT,"
                + EventEntry.COLUMN_COLOUR + " TEXT,"
                + EventEntry.COLUMN_TASKS + " TEXT" + ")";
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    //Handles changes in db schema
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //removes old table (drops it) and replaces with newer table
        db.execSQL("DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME);
        onCreate(db);
    }

    //Creates Event records in table specified in EventsParameters
    public void createEvents(Events events){
        // Creating a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        // Filling the map of values
        values.put(EventEntry.COLUMN_TITLE, events.getTitle());
        values.put(EventEntry.COLUMN_DESCRIPTION, events.getDescription());
        values.put(EventEntry.COLUMN_DATE, events.getDate());
        values.put(EventEntry.COLUMN_CATEGORY, events.getCategory());
        values.put(EventEntry.COLUMN_COLOUR, events.getColour());
        values.put(EventEntry.COLUMN_TASKS, events.getTasks());

        //To deploy new records to db, we need to obtain Writable Database
        SQLiteDatabase db = this.getWritableDatabase();
        //using SQLiteDatabase insert method to insert values in table "Events"
        db.insert(EventEntry.TABLE_NAME, null, values);
    }

    //Method which handles changes to particular records
    public void updateEvents(Events events){ //receiving as params events of type Events to obtain values
        // Creating a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        // Filling the map of values
        values.put(EventEntry.COLUMN_TITLE, events.getTitle());
        values.put(EventEntry.COLUMN_DESCRIPTION, events.getDescription());
        values.put(EventEntry.COLUMN_DATE, events.getDate());
        values.put(EventEntry.COLUMN_CATEGORY, events.getCategory());
        values.put(EventEntry.COLUMN_COLOUR, events.getColour());
        values.put(EventEntry.COLUMN_TASKS, events.getTasks());

        SQLiteDatabase db = this.getWritableDatabase();

        //update method in SQLiteDatabase class
        //refer to https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase#update
        //updating event whose id is equal to id of parameter events
        db.update(EventEntry.TABLE_NAME, values, EventEntry._ID	+ "	= ?", new String[] {String.valueOf(events.getId())});
    }

    //Method to handle deletion of record
    public void deleteEvent(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        //delete method in SQLiteDatabase class
        //refer to https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase#delete
        db.delete(EventEntry.TABLE_NAME, EventEntry._ID	+ "	= ?", new String[] { String.valueOf(id)});
    }

    //Method to get all records in Events table and stores them in a returned ArrayList of type Events
    public ArrayList<Events> listEvents(){
        String sql = "SELECT * FROM " + EventEntry.TABLE_NAME ;
        //No changes need to be masde to db, thus we use readable database (as per SQLiteDatabase class)
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Events> listEvents = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);

        //traversing all records returned with our sql query
        if(cursor.moveToFirst()){
            do{
                int id = Integer.parseInt(cursor.getString(0));
                String title = cursor.getString(1);
                String description = cursor.getString(2);
                String date = cursor.getString(3);
                String category = cursor.getString(4);
                String colour = cursor.getString(5);
                String tasks = cursor.getString(6);
                listEvents.add(new Events(id, title, description, date, category, colour, tasks));
            }
            while (cursor.moveToNext());
        }
        cursor.close(); //upon finishing traversing all records

        return listEvents;
    }

    //Method to get all records in Events table and stores them in a returned ArrayList of type Stats
    public ArrayList<Stats> listStats(){
        String sql = "SELECT category, colour, COUNT(*) FROM " + EventEntry.TABLE_NAME + " GROUP BY category, colour" ;
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Stats> listStats = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);

        //traversing all records returned with our sql query
        if(cursor.moveToFirst()){
            do{
                String category = cursor.getString(0);
                String colour = cursor.getString(1);
                int count = Integer.parseInt(cursor.getString(2));
                listStats.add(new Stats(category, colour, count));
            }
            while (cursor.moveToNext());
        }
        cursor.close(); //upon finishing traversing all records

        return listStats;
    }
}
