package com.example.pipeline.db;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.pipeline.AddSelectEditActivityHost;
import com.example.pipeline.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//
//implementing Filterable for the search button to be able to filter the data according to input
public class EventsAdapter extends RecyclerView.Adapter<EventViewHolder> implements Filterable {

    private Context context;
    public ArrayList<Events> listEvents;
    private ArrayList<Events> _ArrayList;
    private EventsReaderDbHelper _Database;

    public EventsAdapter(Context context, ArrayList<Events> listEvents) {
        this.context = context;
        this.listEvents = listEvents;
        this._ArrayList = listEvents;
        _Database = new EventsReaderDbHelper(context);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_layout, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, final int position) {
        //binding data to our EventViewHolder in order to direct data back to our recycler view
        final Events events = listEvents.get(position);

        holder.title.setText(events.getTitle());
        holder.description.setText(events.getDescription());
        String dbDate = events.getDate();
        holder.date.setText(events.getDate());

        int myColor = Color.parseColor(events.getColour());
        holder.layout.setBackgroundColor(myColor);


        //logic for deadlines which expired (to highlight them to user)
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(dbDate);
            Date today = Calendar.getInstance().getTime();

            if (today.after(date)) {
                holder.title.setTextColor(Color.RED);
                holder.description.setTextColor(Color.RED);
                holder.date.setTextColor(Color.RED);
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        holder.deleteEvent.setOnClickListener(new View.OnClickListener() {
            //handles instance where delete button is clicked on card
            @Override
            public void onClick(View view) {
                listEvents.remove(listEvents.get(position));
                //delete row from database
                _Database.deleteEvent(events.getId());
                notifyItemRemoved(position);
            }
        });

        holder.accessEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(context.getApplicationContext(), AddSelectEditActivityHost.class);
                intent1.putExtra("data", listEvents.get(position));

                context.startActivity(intent1);
            }
        });

        holder.editEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(context.getApplicationContext(), AddSelectEditActivityHost.class);
                intent1.putExtra("editData", listEvents.get(position));

                context.startActivity(intent1);
            }
        });

    }

    public interface SelectedEvent{
        void selectedEvent(Events event);

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    listEvents = _ArrayList;
                }
                else {
                    ArrayList<Events> filteredList = new ArrayList<>();

                    for (Events events : _ArrayList) {

                        if (events.getTitle().toLowerCase().contains(charString)) {
                            filteredList.add(events);
                        }
                    }
                    listEvents= filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = listEvents;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listEvents = (ArrayList<Events>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return listEvents.size();
    }

}