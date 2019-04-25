package aau.itcom.rabbithabit;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.objects.Habit;
import aau.itcom.rabbithabit.objects.HabitPublished;

import static java.security.AccessController.getContext;

public class AddHabitActivity extends AppCompatActivity {

    private static final String TAG = "AddHabitActivity";
    ArrayList<Habit> habits;
    ArrayList<HabitPublished> habitsToDisplay;
    ListView listView;
    private static CustomAdapter adapter;
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

        loadTrendingHabits();
        displayTrendingLayout = findViewById(R.id.trendingHabitsLayout);

        habits = new ArrayList<>();
        habits.add(new Habit("Smoke less", 30, "Smoke less than usual"));
        habits.add(new Habit("Walk for at least 15 min", 30, "Walkie walkie"));
        habits.add(new Habit("Shut the fuck up", 30, "STFU"));
        habits.add(new Habit("Shut the fuck up", 30, "STFU"));
        habits.add(new Habit("Shut the fuck up", 30, "STFU"));
        habits.add(new Habit("Shut the fuck up", 30, "STFU"));
        habits.add(new Habit("Shut the fuck up", 30, "STFU"));
        habits.add(new Habit("Shut the fuck up", 30, "STFU"));

        adapter = new CustomAdapter(habits, getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Habit habit = habits.get(position);

                Snackbar.make(view, habit.getDetails(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
    }

    private void loadTrendingHabits() {
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
        habitsToDisplay = new ArrayList<>(db.getArrayListOfHabitsPublished());

        ArrayList<TextView> textViews = new ArrayList<>(createTextFieldsForHabits());
        for(int i = 0;i<textViews.size();i++){
            displayTrendingLayout.addView(textViews.get(i));
        }
    }

    public ArrayList<TextView> createTextFieldsForHabits() {
        Log.d(TAG, "Inside createTextFieldsForHabits()");
        ArrayList<TextView> arrayOfTextViews = new ArrayList<>();

        for (int i = 0; i < habitsToDisplay.size(); i++) {
            Log.d(TAG, "Inside loop for textfields - createTextFieldsForHabits()");
            arrayOfTextViews.add(habitsToDisplay.get(i).display(getApplicationContext(), 18, params, habitsToDisplay.get(i)));
        }
        return arrayOfTextViews;
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

            Log.d(THREAD_TRENDING_HABIT_TAG, "Story is: " + db.getStory().getTextContent());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayHabits();
                }
            });
        }
    }
}
