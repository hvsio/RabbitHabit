package aau.itcom.rabbithabit.objects;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Date;

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

    public TextView display(final Context context, int textSize, ViewGroup.LayoutParams params, Habit listener, Date data ){
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

    public String getDetails() { return details; }

}
