package aau.itcom.rabbithabit.objects;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import aau.itcom.rabbithabit.HabitDetailsActivity;
import aau.itcom.rabbithabit.R;

public class HabitPersonal extends Habit implements Serializable {
    private Date startDate;
    private String[] arrayOfDates;
    private Map<String, Boolean> complexion;



    public HabitPersonal(String name, long duration, String details, Date startDate) {
        super(name, duration, details);
        this.startDate = startDate;
        initializeComplexionAndArrayOfDates(startDate, duration);
    }

    private void initializeComplexionAndArrayOfDates(Date startDate, long duration) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        arrayOfDates = new String[(int) duration];
        complexion = new HashMap<>();

        for (int i = 0; i < duration; i++){
            arrayOfDates[i] = dateFormat.format(calendar.getTime());
            complexion.put(arrayOfDates[i], false);
            calendar.add(Calendar.DATE, 1);
        }
    }

    @Override
    public TextView display(final Context context, int textSize, LinearLayout.LayoutParams params, final Habit listener) {
        Log.d("display() in hPersonal", " am inside");
        TextView textView = new TextView(context);
        textView.setText(listener.getName());
        textView.setTextSize(textSize);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = HabitDetailsActivity.createNewIntent(context);
                intent.putExtra("habit", listener);
                context.startActivity(intent);
            }
        });
        textView.setLayoutParams(params);
        textView.setBackground(ContextCompat.getDrawable(context, R.drawable.my_button_white));
        return textView;
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
        //return (String[]) complexion.keySet().toArray();
        return arrayOfDates;
    }

    public Map<String,Boolean> getComplexionMap(){
        return complexion;
    }

    public void displayStatistics(){

    }

    @Override
    public String toString() {
        return "HabitPersonal{" +
                "startDate=" + startDate +
                ", arrayOfDates=" + Arrays.toString(arrayOfDates) +
                ", complexion=" + complexion +
                '}';
    }
}
