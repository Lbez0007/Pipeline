package com.example.pipeline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

public class AddSelectEditActivityHost extends AppCompatActivity {

    //method responsible for creating the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_select_host); //using layout activity_add_select_host for the activity's UI

        Intent intent = getIntent(); //getting intent from activity calling this activity

        if(intent.getSerializableExtra("data")  != null){ //if Serializable extra associated with key "data" is found

            SelectedEventFragment fragment = new SelectedEventFragment(); //creating an instance of SelectedEventFragment
            //Bundle - used for passing data to a fragment. Intents are ONLY used between Activities, not between Activity and Fragment
            Bundle bundle = new Bundle();
            //the serializable extra received is passed as a bundle Serializable extra to SelectedEventFragment
            bundle.putSerializable("data", intent.getSerializableExtra("data"));
            fragment.setArguments(bundle);

            //initiating hosting of fragment within activity
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frameFragment, fragment).commit(); //replacing the Frame Layout in the activity with SelectedEventFragment
        }

        else if(intent.getSerializableExtra("editData") != null) { //if Serializable extra associated with key "editData" is found

            EditEventFragment fragment = new EditEventFragment(); //creating an instance of EditEventFragment
            //Bundle - used for passing data to a fragment. Intents are ONLY used between Activities, not between Activity and Fragment
            Bundle bundle = new Bundle();
            //the serializable extra received is passed as a bundle Serializable extra to EditEventFragment
            bundle.putSerializable("editData", intent.getSerializableExtra("editData"));
            fragment.setArguments(bundle);

            //initiating hosting of fragment within activity
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frameFragment, fragment).commit(); //replacing the Frame Layout in the activity with EditEventFragment
        }

        else {
            Fragment fragment = new AddEventFragment(); //creating an instance of AddEventFragment

            //initiating hosting of fragment within activity
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frameFragment, fragment).commit(); //replacing the Frame Layout in the activity with AddEventFragment
        }


    }

    //handles configuration changes - ex.switching from portrait to landscape view
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
