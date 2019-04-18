package aau.itcom.rabbithabit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.objects.HabitPersonal;
import aau.itcom.rabbithabit.objects.Photo;
import aau.itcom.rabbithabit.objects.Story;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendar;
    LinearLayout layout;
    TextView storyTextView;
    LinearLayout.LayoutParams params;
    ArrayList<HabitPersonal> habitArray;
    Story story;
    Photo photo;
    Database db;

    public static final Object LOCK_FOR_HABITS = new Object();
    public static final Object LOCK_FOR_STORY = new Object();
    public static final Object LOCK_FOR_PHOTO = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        habitArray = new ArrayList<>();

        db = Database.getInstance();
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        storyTextView = findViewById(R.id.textViewStory);
        layout = findViewById(R.id.linearLayoutDay);
        calendar = findViewById(R.id.calendarViewDaily);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String collectDate = dayOfMonth + "." + (month+1) + "." + year;
                changeCurrentDay(collectDate);
            }
        });
    }

    private void changeCurrentDay(String dateInString) {
        Date date = null;

        try {
            date = new SimpleDateFormat("dd.MM.yyyy").parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("CalendarActivity", "I am inside changeCurrentDay(), and I am starting THREADS!");
        ExecutorService service = Executors.newCachedThreadPool();
        service.submit(new LoadHabitsTask());
        service.submit(new LoadStoryTask());
        //service.submit(new LoadPhotoTask());
        service.shutdown();

        Log.d("CalendarActivity", "I am inside changeCurrentDay(), and I am loading info");
        db.loadSetOfHabitsOnDate(date, FirebaseAuth.getInstance().getCurrentUser());
        //db.loadPhotoOnDate(date, FirebaseAuth.getInstance().getCurrentUser());
        db.loadStoryOnDate(date, FirebaseAuth.getInstance().getCurrentUser());

    }

    private void displayStory() {
        story = new Story(db.getStory().getDate(),db.getStory().getTextContent(), db.getStory().getMood());
        storyTextView.setText(story.getTextContent());
    }


    private void displayPhoto() {
        //photo = new Photo(db.getPhoto().getDate(),db.getPhoto().getPhotoURLinDB());
    }

    private void displayHabits(){
        Log.d("CalendarActivity", "Inside displayHabits()");
        habitArray.addAll(db.getArrayListOfHabits());

        Log.d("CalendarActivity", "Inside displayHabits() and habit array size is: " + habitArray.size());
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.addAll(createTextFieldsForHabits(getApplicationContext()));
        for(int i = 0;i<textViews.size();i++){
            layout.addView(textViews.get(i));
        }
    }

    public ArrayList<TextView> createTextFieldsForHabits(final Context context){
        Log.d("Day", "Inside displayHabits()");
        ArrayList<TextView> arrayOfTextViews = new ArrayList<>();
        for (int i =0; i<habitArray.size(); i++){
            Log.d("Day", "Inside loop for textfields - displayHabits()");
            TextView textView = new TextView(context);
            textView.setText(habitArray.get(i).getName());
            textView.setTextSize(18);
            final HabitPersonal habitPersonal = habitArray.get(i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = HabitDetailsActivity.createNewIntent(context);
                    intent.putExtra("habitPersonal",habitPersonal);
                    context.startActivity(intent);
                }
            });
            textView.setLayoutParams(params);
            textView.setBackground(ContextCompat.getDrawable(context, R.drawable.my_button_white));
            arrayOfTextViews.add(textView);
        }
        return arrayOfTextViews;
    }

    private class LoadHabitsTask implements Runnable{

        private static final String THREAD_HABIT_TAG = "LoadHabitsTask";

        @Override
        public void run() {
            Log.d(THREAD_HABIT_TAG, "is running");
            try {
                synchronized (LOCK_FOR_HABITS) {
                    Log.d(THREAD_HABIT_TAG, "I am waiting");
                    LOCK_FOR_HABITS.wait();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d(THREAD_HABIT_TAG, "First element of array is: " + db.getArrayListOfHabits().get(0).toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayHabits();
                }
            });

        }
    }

    private class LoadPhotoTask implements Runnable{

        private static final String THREAD_PHOTO_TAG = "LoadPhotoTask";

        @Override
        public void run() {
            Log.d(THREAD_PHOTO_TAG, "is running");
            try {
                synchronized (LOCK_FOR_PHOTO) {
                    Log.d("THREAD !", "I am waiting");
                    LOCK_FOR_PHOTO.wait();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d(THREAD_PHOTO_TAG, "First element of array is: " + db.getArrayListOfHabits().get(0).toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayPhoto();
                }
            });

        }
    }

    private class LoadStoryTask implements Runnable{

        private static final String THREAD_STORY_TAG = "LoadStoryTask";

        @Override
        public void run() {
            Log.d(THREAD_STORY_TAG, "is running");
            try {
                synchronized (LOCK_FOR_STORY) {
                    Log.d(THREAD_STORY_TAG, "I am waiting");
                    LOCK_FOR_STORY.wait();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d(THREAD_STORY_TAG, "Story is: " + db.getStory().getTextContent());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayStory();
                }
            });

        }
    }

}
