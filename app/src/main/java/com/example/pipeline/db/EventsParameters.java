package com.example.pipeline.db;
import android.provider.BaseColumns;

public class EventsParameters {
    //Scope - to specify SQLite table details of Events

    private EventsParameters () {
    }

    //By implementing the BaseColumns interface, inner class inherits a primary key field called _ID
    public static final class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "Events";  //Specifying table name for storing events

        //columns to bs stored in table "Events"
        static final String COLUMN_TITLE = "title";
        static final String COLUMN_DESCRIPTION = "description";
        static final String COLUMN_DATE = "date";
        static final String COLUMN_CATEGORY = "category";
        static final String COLUMN_COLOUR = "colour";
        static final String COLUMN_TASKS = "tasks";

    }
}
