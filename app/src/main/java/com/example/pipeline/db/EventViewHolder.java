package com.example.pipeline.db;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pipeline.R;

public class EventViewHolder extends RecyclerView.ViewHolder {

    public TextView title, description, date;
    public ImageView editEvent;
    public ImageView deleteEvent;
    public ImageView accessEvent;
    public LinearLayout layout;

    public EventViewHolder(View itemView) {
        super(itemView);
        title = (TextView)itemView.findViewById(R.id.event_name);
        description = (TextView)itemView.findViewById(R.id.event_description);
        date = (TextView)itemView.findViewById(R.id.event_date);
        editEvent = (ImageView)itemView.findViewById(R.id.edit_event);
        deleteEvent = (ImageView)itemView.findViewById(R.id.delete_event);
        accessEvent = (ImageView)itemView.findViewById(R.id.access_event);
        layout = itemView.findViewById(R.id.cardLayout);

    }
}