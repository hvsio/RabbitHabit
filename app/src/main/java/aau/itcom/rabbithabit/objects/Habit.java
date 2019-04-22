package aau.itcom.rabbithabit.objects;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;

public class Habit implements Serializable{
    private String name;
    private long duration;
    private String details;

    public Habit(String name, long duration, String details) {
        this.name = name;
        this.duration = duration;
        this.details = details;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO : DO IT!
        return super.equals(obj);
    }

    public TextView display(final Context context, int textSize, LinearLayout.LayoutParams params, Habit listener ){
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDetails() { return details; }

    public void setDetails(String details) { this.details = details; }
}
