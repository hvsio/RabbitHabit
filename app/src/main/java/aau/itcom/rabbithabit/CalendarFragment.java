package aau.itcom.rabbithabit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class CalendarFragment extends Fragment {

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

    private static final String TAG = "CalendarFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();

        habitArray = new ArrayList<>();

        db = Database.getInstance();
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        storyTextView = v.findViewById(R.id.textViewStory);
        layout = v.findViewById(R.id.linearLayoutDay);
        calendar = v.findViewById(R.id.calendarViewDaily);
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

        Log.d(TAG, "I am inside changeCurrentDay(), and I am starting THREADS!");
        ExecutorService service = Executors.newCachedThreadPool();
        service.submit(new LoadHabitsTask());
        service.submit(new LoadStoryTask());
        //service.submit(new LoadPhotoTask());
        service.shutdown();

        Log.d(TAG, "I am inside changeCurrentDay(), and I am loading info");
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
        Log.d(TAG, "Inside displayHabits()");
        habitArray.addAll(db.getArrayListOfHabits());

        Log.d(TAG, "Inside displayHabits() and habit array size is: " + habitArray.size());
        ArrayList<TextView> textViews = new ArrayList<>(createTextFieldsForHabits());
        for(int i = 0;i<textViews.size();i++){
            layout.addView(textViews.get(i));
        }
    }

    public ArrayList<TextView> createTextFieldsForHabits(){
        Log.d(TAG, "Inside createTextFieldsForHabits()");
        ArrayList<TextView> arrayOfTextViews = new ArrayList<>();

        for (int i =0; i<habitArray.size(); i++){
            Log.d(TAG, "Inside loop for textfields - createTextFieldsForHabits()");
            TextView textView = new TextView(getContext());
            textView.setText(habitArray.get(i).getName());
            textView.setTextSize(18);

            final HabitPersonal habitPersonal = habitArray.get(i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = HabitDetailsActivity.createNewIntent(getContext());
                    intent.putExtra("habitPersonal",habitPersonal);
                    getContext().startActivity(intent);
                }
            });
            textView.setLayoutParams(params);
            textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.my_button_white));
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

            if(isAdded()){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayHabits();
                    }
                });
            } else {
                Log.w(THREAD_HABIT_TAG, "Fragment is no longer attached to its activity");
            }
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

            if (isAdded()){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayPhoto();
                    }
                });
            } else {
                Log.w(THREAD_PHOTO_TAG, "Fragment is no longer attached to its activity");
            }
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
            if (isAdded()){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayStory();
                    }
                });
            } else {
                Log.w(THREAD_STORY_TAG, "Fragment is no longer attached to its activity");
            }
        }
    }

}
