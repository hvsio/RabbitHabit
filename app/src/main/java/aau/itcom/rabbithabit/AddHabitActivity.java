package aau.itcom.rabbithabit;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.objects.Habit;

public class AddHabitActivity extends AppCompatActivity {

    ArrayList<Habit> habits;
    ListView listView;
    private static CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);
        listView = findViewById(R.id.listview);

        habits = new ArrayList<>();
        habits.add(new Habit("Smoke less", 30, "Smoke less than usual"));
        habits.add(new Habit("Walk for at least 15 min", 30, "Walkie walkie"));
        habits.add(new Habit("Shut the fuck up", 30, "STFU"));
        habits.add(new Habit("Shut the fuck up", 30, "STFU"));
        habits.add(new Habit("Shut the fuck up", 30, "STFU"));
        habits.add(new Habit("Shut the fuck up", 30, "STFU"));
        habits.add(new Habit("Shut the fuck up", 30, "STFU"));
        habits.add(new Habit("Shut the fuck up", 30, "STFU"));

        adapter= new CustomAdapter(habits, getApplicationContext());

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

    public void addOwnHabit(View view) {
        Intent intent = new Intent(getApplicationContext(), HabitActivity.class);
        startActivity(intent);
    }

}
