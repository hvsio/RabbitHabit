package aau.itcom.rabbithabit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.objects.Habit;
import aau.itcom.rabbithabit.objects.HabitPersonal;
import aau.itcom.rabbithabit.objects.HabitPublished;

public class AddHabitActivity extends AppCompatActivity {

    private static final String TAG = "AddHabitActivity";
    static ArrayList<HabitPublished> habits;
    ListView listView;
    private static CustomAdapterSearchingHabits adapter;
    LinearLayout displayTrendingLayout;
    Database db;
    LinearLayout.LayoutParams params;

    public static final int OWN_HABIT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);
        listView = findViewById(R.id.listview);
        db = Database.getInstance();
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        habits = new ArrayList<>();
        habits.add(new HabitPublished("Smoke less", 30, "Smoke less than usual", "false", "Pre-defined", 1));
        habits.add(new HabitPublished("Walk for at least 15 min", 30, "Walkie walkie", "false", "Pre-defined", 1));
        habits.add(new HabitPublished("Be nicer", 30, "Some random details", "false", "Pre-defined", 1));
        habits.add(new HabitPublished("Drink more water", 30, "Some random details", "false", "Pre-defined", 1));
        habits.add(new HabitPublished("Do not procastinate", 30, "Some random details", "false", "Pre-defined", 1));
        habits.add(new HabitPublished("Meditate", 30, "Some random details", "false", "Pre-defined", 1));
        habits.add(new HabitPublished("Read for half an hour a day", 30, "Some random details", "false", "Pre-defined", 1));
        habits.add(new HabitPublished("Walk instead of taking a bus", 30, "Some random details", "false", "Pre-defined", 1));

        adapter = new CustomAdapterSearchingHabits(habits, getApplicationContext());

        listView.setAdapter(adapter);
    }

    public   void loadTrendingHabits(View view) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(new LoadTrendingHabitsTask());
        service.shutdown();
        db.loadSetOfTrendingHabits();
    }

    public void addOwnHabit(View view) {
        Intent intent = new Intent(getApplicationContext(), HabitActivity.class);
        startActivityForResult(intent, OWN_HABIT);
    }

    private void displayHabits() {
        habits.clear();
        habits.addAll(db.getArrayListOfHabitsPublished());
        Collections.reverse(habits);

        adapter = new CustomAdapterSearchingHabits(habits, getApplicationContext());

        listView.setAdapter(adapter);
    }

    static Intent createNewIntent(Context context) {
        return new Intent(context, AddHabitActivity.class);
    }

    private class LoadTrendingHabitsTask implements Runnable {

        private static final String THREAD_TRENDING_HABIT_TAG = "LoadTrendingHabitsTask";

        @Override
        public void run() {
            Log.d(THREAD_TRENDING_HABIT_TAG, "is running");
            try {
                synchronized (Database.LOCK_FOR_HABITS_PUBLISHED) {
                    Log.d(THREAD_TRENDING_HABIT_TAG, "I am waiting");
                    Database.LOCK_FOR_HABITS_PUBLISHED.wait();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Log.d(THREAD_TRENDING_HABIT_TAG, db.getArrayListOfHabitsPublished().toString());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayHabits();
                }
            });
        }
    }
}
