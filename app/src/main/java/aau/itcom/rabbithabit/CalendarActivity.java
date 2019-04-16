package aau.itcom.rabbithabit;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import aau.itcom.rabbithabit.objects.Day;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendar;
    Day currentDay;
    LinearLayout layout;
    TextView storyTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        storyTextView = findViewById(R.id.textViewStory);
        layout = findViewById(R.id.linearLayoutDay);
        calendar = findViewById(R.id.calendarViewDaily);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String collectDate = dayOfMonth + "." + month + "." + year;
                Date date = null;

                try {
                    date = new SimpleDateFormat("dd.MM.yyyy").parse(collectDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                changeCurrentDay(date);
            }
        });
    }

    private void changeCurrentDay(Date date) {
        currentDay = new Day(date);
        displayHabits();
        displayStory();
        displayPhoto();
    }

    private void displayStory() {
        storyTextView = currentDay.displayStory(storyTextView,getApplicationContext());
    }

    private void displayPhoto() {

    }

    private void displayHabits(){
        ArrayList<TextView> textViews = currentDay.displayHabits(getApplicationContext());
        for (int i = 0; i < textViews.size(); i++){
            layout.addView(textViews.get(i));
        }
    }
}
