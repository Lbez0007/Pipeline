package com.example.pipeline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.pipeline.db.Events;
import com.example.pipeline.db.EventsReaderDbHelper;
import com.example.pipeline.db.Stats;

import java.util.ArrayList;
import java.util.List;

public class ProductivityStatsActivity extends AppCompatActivity {

    //creating an instance of the DB Helper to access the CRUD methods with SQLite DB instance 'pipeline'
    private EventsReaderDbHelper _Database = new EventsReaderDbHelper(this);
    private ArrayList<Stats> allCategoryStats = new ArrayList<>(); //creating an ArrayList of type Stats (defined model in db folder)

    //method responsible for creating the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productivity_stats); //using layout activity_productivity_stats for the activity's UI

        //assigning UI components defined in the layout
        final TextView noEventsFound = findViewById(R.id.productivityText);

        //creating a pie chart instance using external library AnyChart
        Pie pie = AnyChart.pie();

        //calling listStats method in db helper and setting allCategoryStats' value to listStats return
        allCategoryStats = _Database.listStats();

        //no stats returned from listStats method
        if (allCategoryStats.size() == 0){
            noEventsFound.setText("No Events added!");
            noEventsFound.bringToFront(); //showing the TextView component on front of layout
        }

        //creating an array of colours with the size set to amount of stats found
        String[] colours = new String [allCategoryStats.size()];
        //creating array list of type DataEntry to populate graph
        List<DataEntry> data = new ArrayList<>();

        //for loop to go through all stats returned
        for (int i = 0; i < allCategoryStats.size(); i++) {
            //obtaining data to populate graph
            data.add(new ValueDataEntry(allCategoryStats.get(i).getCategory(), allCategoryStats.get(i).getCount()));
            //obtaining colours for each stat (i.e. for each category) to modify the graph's palette
            colours[i] = allCategoryStats.get(i).getColour();
        }

        //making changes to graph
        pie.data(data);
        pie.palette(colours);

        //assigning UI components defined in the layout
        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        //displaying the pie chart created in the component anyChartView
        anyChartView.setChart(pie);
    }

    //handles configuration changes - ex.switching from portrait to landscape view
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}