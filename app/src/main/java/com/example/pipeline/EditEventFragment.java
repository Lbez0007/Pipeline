package com.example.pipeline;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pipeline.db.Events;
import com.example.pipeline.db.EventsReaderDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EditEventFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private EventsReaderDbHelper _Database;

    //Task List components
    private EditText enterTask;
    private Button addTask;
    private ListView taskList;
    private ArrayList<String> tasks = new ArrayList<>();
    private ArrayAdapter<String> arrayadapter;

    //creating the fragment within host activity AddSelectEditActivityHost
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _Database = new EventsReaderDbHelper(getActivity());
    }

    //instantiating the fragment's UI, using a defined layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        // Inflating layout fragment_add_event in the fragment (common for both adding and editing event)
        View view = inflater.inflate(R.layout.fragment_add_event, parent, false);
        return view;
    }

    //handles all the necessary actions following fragment view being inflated
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //Bundle - used for passing data to a fragment. Intents are ONLY used between Activities, not between Activity and Fragment
        Bundle bundle = this.getArguments(); //retrieving arguments sent from activity AddSelectEditActivityHost
        final Events eventModel;

        //Task List components
        if (bundle != null) {
            //a bundle was found - data is passed
            //getting the stream of data (hence Serializable) associated with key "editData" and casting it to Model Events
            eventModel = (Events) bundle.getSerializable("editData");

            //Task List components being initialised to UI components in layout
            enterTask = view.findViewById(R.id.insertTask);
            addTask = view.findViewById(R.id.addButton);
            taskList = view.findViewById(R.id.tasksList);

            //populating ArrayAdapter for ListView with tasks, using the hosting Activity AddSelectEditActivityHost as the context
            arrayadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, tasks);
            taskList.setAdapter(arrayadapter); //setting the ArrayAdapter as an adapter for the ListView

            //assigning event handlers to button for adding tasks, and to the list of tasks (when clicking a task within list)
            addTask.setOnClickListener(this);
            taskList.setOnItemClickListener(this);

            //General event components assigned from layout
            final EditText titleField = (EditText) view.findViewById(R.id.insertName);
            final EditText descriptionField = (EditText) view.findViewById(R.id.insertDescription);
            final EditText dateField = (EditText) view.findViewById(R.id.insertDate);
            final Spinner eventField = (Spinner) view.findViewById(R.id.insertEventCategory);

            //obtaining id of event edited
            final int id = eventModel.getId();
            //setting the EditText components' value to details of event being edited
            titleField.setText(eventModel.getTitle());
            descriptionField.setText(eventModel.getDescription());
            dateField.setText(eventModel.getDate());


            String eventText = eventModel.getCategory();
            //checking if Spinner eventField contains the category of the event, and setting the spinner on the position
            //where the category resides on the spinner
            for (int i = 0; i < eventField.getCount(); i++) {
                if (eventField.getItemAtPosition(i).toString().equalsIgnoreCase(eventText)) {
                    eventField.setSelection(i);
                }
            }

            JSONObject json = null;

            try {
                json = new JSONObject(eventModel.getTasks());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //returns value mappped by key "tasks"
            JSONArray jArray = json.optJSONArray("tasks");

            // tasks are mapped sequentially by an integer, starting from 0 as the first task
            for (int i = 0; i < jArray.length(); i++) {
                tasks.add(jArray.optString(i));  //obtaining task mapped to key (i), and adding it to local ArrayList tasks
            }

            Button buttonCanx = (Button) view.findViewById(R.id.noButton); //button handling creation of event
            //event handler for when pressing button CANCEL
            buttonCanx.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getActivity().finish(); //return back to activity calling AddSelectEditActivityHost
                }
            });

            Button button = (Button) view.findViewById(R.id.okButton); //button handling creation of event
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    //calling method JSONtoString to convert the list in a string (for saving in the db)
                    String taskList = JSONtoString(tasks);

                    //getting data from text fields
                    final String title = titleField.getText().toString();
                    final String date = dateField.getText().toString();
                    final String description = descriptionField.getText().toString();
                    final String category = eventField.getSelectedItem().toString();
                    String colour = "";

                    //choosing colour depending on category of evet
                    switch(category) {
                        case "Academic":
                            //obtaining Hex code of colour in values -> colors.xml as #AARRGGBB
                            colour = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.colorSecondary));
                            break;
                        case "Work/Business":
                            colour = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.colorSecondaryLight));
                            break;
                        case "Leisure":
                            colour = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.colorSecondaryDark));
                            break;
                        case "Conferences/Online Events":
                            colour = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.colorSecondaryAlt));
                            break;
                        default:
                            colour = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.colorSecondary));
                            break;
                    }

                    if (TextUtils.isEmpty(title)) { //if no title is inputted
                        //Toast.makeText(this, "Something went wrong. Check your input values", Toast.LENGTH_LONG).show();
                    } else {
                        //updating an Event instance with the data submitted
                        Events newEvent = new Events(id, title, description, date, category, colour, taskList);
                        _Database.updateEvents(newEvent); //saving event in database

                        getActivity().finish(); //return back to activity calling AddSelectEditActivityHost
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) { //event handler for Add Task button click
        switch (v.getId()) {
            case R.id.addButton:
                String taskInputted = enterTask.getText().toString(); //getting text inputted for particular task
                arrayadapter.add(taskInputted); //adding task to array adapter
                enterTask.setText(""); //resetting task input to be able to add more tasks
                //showing notification that task has been successfully added
                Toast.makeText(getActivity(), "Task Added", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    //handles clicking of tasks from ListView - to delete clicked tasks
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        tasks.remove(position); //removing selected task from ArrayList tasks
        arrayadapter.notifyDataSetChanged(); //update the ArrayAdapter
        Toast.makeText(getActivity(), "Delete", Toast.LENGTH_SHORT).show();
    }

    //converts ArrayList to JSON  body and parses the body in a String
    public String JSONtoString(ArrayList<String> tasks) {
        JSONObject jGroup = new JSONObject();

        try { //saving all tasks as an array paired with key "tasks" in a JSON body
            jGroup.put("tasks", new JSONArray(tasks));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //converting the JSON body to string
        String taskList = jGroup.toString();
        return taskList; //return will be used in the OnClick of the OK Button
    }
}
