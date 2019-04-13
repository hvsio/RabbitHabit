package aau.itcom.rabbithabit.objects;

import android.widget.TextView;

import java.util.Date;
import java.util.Map;

public class HabitPersonal extends Habit {
    private Date startDate;
    private Date endDate;
    Map<Date, Boolean> complexion;

    public HabitPersonal(String name, int duration, Date startDate, Date endDate) {
        super(name, duration);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void display(TextView textView) {
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isCompletedOn(Date date){
        return true;
    }

    public void setCompletedOn(Date date){

    }

    public void displayStatistics(){

    }
}
