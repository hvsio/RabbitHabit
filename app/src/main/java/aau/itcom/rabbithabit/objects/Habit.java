package aau.itcom.rabbithabit.objects;

import android.widget.TextView;

import java.io.Serializable;

public class Habit implements Serializable {
    private String name;
    private int duration;

    public Habit(String name, int duration) {
        this.name = name;
        this.duration = duration;
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
}
