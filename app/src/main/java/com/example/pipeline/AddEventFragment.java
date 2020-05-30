package com.example.pipeline;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pipeline.db.Events;
import com.example.pipeline.db.EventsReaderDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddEventFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

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
        //Instantiating the EventsReaderDbHelper
        _Database = new EventsReaderDbHelper(getActivity());
    }

    //instantiating the fragment's UI, using a defined layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        // inflating layout fragment_add_event in the fragment
        View view = inflater.inflate(R.layout.fragment_add_event, parent, false);
        return view;
    }

    //handles all the necessary actions following fragment view being inflated
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
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

        //creating calendar instance for date picker
        final Calendar _Calendar = Calendar.getInstance();
        //General event components assigned from layout
        final EditText titleField = (EditText) view.findViewById(R.id.insertName);
        final EditText descriptionField = (EditText) view.findViewById(R.id.insertDescription);
        final EditText dateField = (EditText) view.findViewById(R.id.insertDate);
        final Spinner eventField = (Spinner) view.findViewById(R.id.insertEventCategory);

        //date picker dialog handler - handles the selection of date from date picker
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            //method which handles date being chosen and selected
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                _Calendar.set(Calendar.YEAR, year);
                _Calendar.set(Calendar.MONTH, monthOfYear);
                _Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(_Calendar, dateField); //refer to updateLabel method below
            }

        };

        //event handler when clicking the date field
        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a new instance of a date picker dialog, with current day, month and year
                new DatePickerDialog(getActivity(), date, _Calendar
                        .get(Calendar.YEAR), _Calendar.get(Calendar.MONTH),
                        _Calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button buttonCanx = (Button) view.findViewById(R.id.noButton); //button handling creation of event
        //event handler for when pressing button CANCEL
        buttonCanx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish(); //return back to activity calling AddSelectEditActivityHost
            }
        });

        Button button = (Button) view.findViewById(R.id.okButton); //button handling creation of event
        //event handler for when pressing button OK to submit event
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
                    //creating an Event instance with the data submitted
                    Events newEvent = new Events(title, description, date, category, colour, taskList);
                    _Database.createEvents(newEvent); //saving event in database

                    getActivity().finish(); //return back to activity calling AddSelectEditActivityHost
                }
            }
        });
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

    //method which updates the text field for the date entry
    private void updateLabel(Calendar _Calendar, EditText dateField) {
        String myFormat = "dd/MM/yyyy"; //format we're using for all our dates
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        //setting the text of the date field to the date selected and saved to Calendar instance
        dateField.setText(sdf.format(_Calendar.getTime()));
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
