package aau.itcom.rabbithabit.objects;

import android.widget.TextView;

import java.io.Serializable;

public class Habit implements Serializable {
    private String name;
    private int duration;
    private String details;

    public Habit(String name, int duration, String details) {
        this.name = name;
        this.duration = duration;
        this.details = details;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO : DO IT!
        return super.equals(obj);
    }

    void display(TextView textView){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDetails() { return details; }

    public void setDetails(String details) { this.details = details; }
}
