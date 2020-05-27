package com.example.pipeline;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pipeline.db.Events;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SelectedEventFragment extends Fragment{

    //used to output event data in text and list forms (list being used to show all tasks for event)
    TextView EventTextView;
    TextView EventDate;
    TextView EventCategory;
    TextView EventDescription;
    ListView EventTasksView;

    private ArrayList<String> tasks = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    //creating the fragment within host activity AddSelectEditActivityHost
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //instantiating the fragment's UI, using a defined layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        // Inflating layout fragment_selected_event in the fragment
        View view = inflater.inflate(R.layout.fragment_selected_event, parent, false);
        return view;
    }

    //handles all the necessary actions following fragment view being inflated
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //assigning UI components defined in the layout
        EventTasksView = view.findViewById(R.id.selectedEventTasks);
        EventTextView = view.findViewById(R.id.selectedEvent);
        EventDate = view.findViewById(R.id.textDate);
        EventDescription = view.findViewById(R.id.textDescription);
        EventCategory= view.findViewById(R.id.textCategory);

        //Bundle - used for passing data to a fragment. Intents are ONLY used between Activities, not between Activity and Fragment
        Bundle bundle = this.getArguments(); //retrieving arguments sent from activity AddSelectEditActivityHost

        if(bundle != null){
            //a bundle was found - data is passed
            //getting the stream of data (hence Serializable) associated with key "data" and casting it to Model Events
            Events eventModel = (Events) bundle.getSerializable("data");

            //using getter methods in Events to retrieve the Serializable data, and view them in the respective UI Components
            EventTextView.setText(eventModel.getTitle());
            EventDate.setText("Deadline Date: " + eventModel.getDate());
            EventDescription.setText("Event Description: " + eventModel.getDescription());
            EventCategory.setText(eventModel.getCategory());

            JSONObject json = null;

            //converting string of tasks to a JSON object of multiple tasks, to show the tasks in a list view
            try {
                json = new JSONObject(eventModel.getTasks()); //create a JSON object from the tasks String in the model
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //returns value mappped by key "tasks"
            JSONArray jArray = json.optJSONArray("tasks");
            if (jArray == null){ //no tasks found in the tasks String
                tasks.add("No tasks found!");
            }
            else {
                // tasks are mapped sequentially by an integer, starting from 0 as the first task
                for (int i = 0; i < jArray.length(); i++) {
                    tasks.add(jArray.optString(i));  //obtaining task mapped to key (i), and adding it to local ArrayList tasks
                }
            }

            //the ArrayAdapter converts an ArrayList of tasks into View items loaded into the ListView
            arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, tasks );

            //loading the adapter in the ListView
            EventTasksView.setAdapter(arrayAdapter);
        }
    }

    /*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        tasks.remove(position);

        arrayAdapter.notifyDataSetChanged();
    }
    */

}
