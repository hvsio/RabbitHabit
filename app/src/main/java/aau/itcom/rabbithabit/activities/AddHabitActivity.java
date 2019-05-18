package aau.itcom.rabbithabit.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aau.itcom.rabbithabit.adapters.CustomAdapterSearchingHabits;
import aau.itcom.rabbithabit.R;
import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.objects.HabitPublished;

public class AddHabitActivity extends AppCompatActivity {

    private static final String TAG = "AddHabitActivity";
    static ArrayList<HabitPublished> habits;
    ListView listView;
    private CustomAdapterSearchingHabits adapter;
    Database db;
    LinearLayout.LayoutParams params;
    Boolean trendingIsClicked = false;

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
        addToList();

        adapter = new CustomAdapterSearchingHabits(habits, getApplicationContext());

        listView.setAdapter(adapter);

        if (savedInstanceState != null) {
            trendingIsClicked = savedInstanceState.getBoolean("trendingClicked");
            if (trendingIsClicked) {
                ExecutorService service = Executors.newSingleThreadExecutor();
                service.submit(new LoadTrendingHabitsTask());
                service.shutdown();
                db.loadSetOfTrendingHabits();
            }
        }
    }

    public   void loadTrendingHabits(View view) {
        if (!trendingIsClicked) {
            trendingIsClicked = true;
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.submit(new LoadTrendingHabitsTask());
            service.shutdown();
            db.loadSetOfTrendingHabits();
        } else {
            trendingIsClicked = false;
            habits.clear();
            addToList();
            adapter = new CustomAdapterSearchingHabits(habits, getApplicationContext());
            listView.setAdapter(adapter);
        }
    }

    public void addOwnHabit(View view) {
        startActivityForResult(HabitActivity.createNewIntent(getApplicationContext()), OWN_HABIT);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("trendingClicked", trendingIsClicked);
        super.onSaveInstanceState(savedInstanceState);
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

    private void addToList () {
        habits.add(new HabitPublished("Smoke less", 30, "Smoke less than usual", "false", "Pre-defined", 1));
        habits.add(new HabitPublished("Walk for at least 15 min", 30, "Walkie walkie", "false", "Pre-defined", 1));
        habits.add(new HabitPublished("Be nicer", 30, "Some random details", "false", "Pre-defined", 1));
        habits.add(new HabitPublished("Drink more water", 30, "Some random details", "false", "Pre-defined", 1));
        habits.add(new HabitPublished("Do not procastinate", 30, "Some random details", "false", "Pre-defined", 1));
        habits.add(new HabitPublished("Meditate", 30, "Some random details", "false", "Pre-defined", 1));
        habits.add(new HabitPublished("Read for half an hour a day", 30, "Some random details", "false", "Pre-defined", 1));
        habits.add(new HabitPublished("Walk instead of taking a bus", 30, "Some random details", "false", "Pre-defined", 1));
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
