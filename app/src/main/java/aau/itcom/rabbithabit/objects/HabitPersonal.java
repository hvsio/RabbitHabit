package aau.itcom.rabbithabit.objects;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HabitPersonal extends Habit {
    private Date startDate;
    private String[] arrayOfDates;
    private Map<String, Boolean> complexion;

    public HabitPersonal(String name, int duration, Date startDate) {
        super(name, duration);
        this.startDate = startDate;
    }

    @Override
    public void display(TextView textView) {
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public boolean isCompletedOn(Date date){
        return true;
    }

    public void setCompletedOn(Date date){

    }

    public String[] getArrayOfDates(){
        return (String[]) complexion.keySet().toArray();
    }

    public Map<String,Boolean> getComplexionMap(){
        return complexion;
    }

    public void displayStatistics(){

    }
}
