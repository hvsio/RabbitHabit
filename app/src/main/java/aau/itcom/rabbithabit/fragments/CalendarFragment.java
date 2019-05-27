package aau.itcom.rabbithabit.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aau.itcom.rabbithabit.R;
import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.objects.HabitPersonal;
import aau.itcom.rabbithabit.system.PhoneState;
import aau.itcom.rabbithabit.objects.Story;

public class CalendarFragment extends Fragment {

    CalendarView calendar;
    LinearLayout habitsLayout;
    TextView storyTextView;
    LinearLayout.LayoutParams params;
    ArrayList<HabitPersonal> habitArray;
    ImageView imageView;
    SmileRating ratingBar;
    ProgressBar habitProgressBar;
    ProgressBar storyProgressBar;
    ProgressBar photoProgressBar;
    //Story story;
    Database db;
    Date date = null;

    private static final String TAG = "CalendarFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
        habitArray = new ArrayList<>();
        db = Database.getInstance();
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ratingBar = v.findViewById(R.id.ratingBar);
        storyTextView = v.findViewById(R.id.textViewForStory);
        habitsLayout = v.findViewById(R.id.calendarHabitsLinearLayout);
        photoProgressBar = v.findViewById(R.id.progressBarPhotoCalendar);
        storyProgressBar = v.findViewById(R.id.progressBarStoryCalendar);
        habitProgressBar = v.findViewById(R.id.progressBarHabitsCalendar);
        calendar = v.findViewById(R.id.calendarViewDaily);
        imageView = v.findViewById(R.id.imageView3);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String collectDate = dayOfMonth + "." + (month + 1) + "." + year;
                changeCurrentDay(collectDate);
            }
        });

        SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
        if (savedInstanceState == null){
            Log.i(TAG,"savedInstanceState == NULL!!! ");
            changeCurrentDay(formater.format(Calendar.getInstance().getTime()));
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Log.i(TAG, "savedInstanceState != null and date is: " + savedInstanceState.getString("date"));
            habitsLayout.removeAllViews();
            habitArray.clear();
            try {
                SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
                calendar.setDate(formater.parse(savedInstanceState.getString("date")).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            changeCurrentDay(savedInstanceState.getString("date"));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
        if (date != null)
            outState.putString("date", formater.format(date));
    }

    private void changeCurrentDay(String dateInString) {
        storyTextView.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        habitsLayout.setVisibility(View.GONE);

        habitProgressBar.setVisibility(View.VISIBLE);
        storyProgressBar.setVisibility(View.VISIBLE);
        photoProgressBar.setVisibility(View.VISIBLE);

        SharedPreferences pref = getContext().getSharedPreferences(SettingsFragment.SETTINGS, Context.MODE_PRIVATE);
        ratingBar.setSelectedSmile(BaseRating.NONE);
        storyTextView.setText("No text to display!");
        habitsLayout.removeAllViews();

        try {
            date = new SimpleDateFormat("dd.MM.yyyy").parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "I am inside changeCurrentDay(), and I am starting THREADS!");
        ExecutorService service = Executors.newCachedThreadPool();
        service.submit(new LoadHabitsTask());
        service.submit(new LoadStoryTask());
        service.submit(new LoadPhotoTask());
        service.shutdown();

        Log.d(TAG, "I am inside changeCurrentDay(), and I am loading info");
        db.loadSetOfHabitsOnDate(date, FirebaseAuth.getInstance().getCurrentUser());
        db.loadPhotoOnDate(date, FirebaseAuth.getInstance().getCurrentUser(), getContext());
        db.loadStoryOnDate(date, FirebaseAuth.getInstance().getCurrentUser());

        if (PhoneState.getConnectionType(getActivity()).equals("WIFI") || !pref.getBoolean(SettingsFragment.DOWNLOAD_PHOTO, true)) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(new LoadPhotoTask());
            executorService.shutdown();
            db.loadPhotoOnDate(date, FirebaseAuth.getInstance().getCurrentUser(), getContext());
        } else {
            Toast.makeText(getActivity(), "Connect to WIFI or change setting to download photos", Toast.LENGTH_LONG).show();
        }

    }

    private void displayStory() {
        Log.d(TAG, "Inside displayStory()");
        storyTextView.setText("No story to display!");

        Story story = null;
        try {
            story = db.getStory();
        } catch (NoSuchElementException ex) {
            Log.w(TAG, "Error loading Photo. No photo to display!\n" + ex);
        }

        if (story != null) {
            ratingBar.setSelectedSmile(((int) story.getMood()));
            if (story.getTextContent() != null)
                storyTextView.setText(story.getTextContent());
        }

        storyProgressBar.setVisibility(View.GONE);
        storyTextView.setVisibility(View.VISIBLE);

    }


    private void displayPhoto() {
        Uri photo = null;

        try{
            photo = db.getPhoto();
        } catch(NoSuchElementException e){
            e.printStackTrace();
        }

        if (photo == null) {
            imageView.setImageResource(R.drawable.no_picture);
            imageView.setRotation(0);
        } else {
            imageView.setImageURI(photo);
            imageView.setRotation(90);
        }

        photoProgressBar.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
    }

    private void displayHabits() {
        Log.i(TAG, "INSIDE DISPLAYHABITS()");

        habitsLayout.removeAllViews();
        final ArrayList<TextView> textViews = new ArrayList<>(createTextFieldsForHabits());

        for (int i = 0; i < textViews.size(); i++) {
            habitsLayout.addView(textViews.get(i));
        }

        habitProgressBar.setVisibility(View.GONE);
        habitsLayout.setVisibility(View.VISIBLE);
    }

    public ArrayList<TextView> createTextFieldsForHabits() {
        Log.d(TAG, "Inside createTextFieldsForHabits()");
        ArrayList<TextView> arrayOfTextViews = new ArrayList<>();

        ArrayList<HabitPersonal> habits = new ArrayList<>(db.getArrayListOfHabitsPersonal());

        for (int i = 0; i < habits.size(); i++) {
            arrayOfTextViews.add(habits.get(i).display(getActivity(), 18, habitsLayout.getLayoutParams(), habits.get(i), date));
            arrayOfTextViews.get(i).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
        }

        return arrayOfTextViews;
    }

    private class LoadHabitsTask implements Runnable {

        private static final String THREAD_HABIT_TAG = "LoadHabitsTask";

        @Override
        public void run() {
            Log.d(THREAD_HABIT_TAG, "is running");
            try {
                synchronized (Database.LOCK_FOR_HABITS) {
                    Log.d(THREAD_HABIT_TAG, "I am waiting");
                    Database.LOCK_FOR_HABITS.wait();
                    Log.d(TAG, "I was notified!");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(THREAD_HABIT_TAG, "After try-catch statement");

            if (isAdded()) {
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

    private class LoadPhotoTask implements Runnable {

        private static final String THREAD_PHOTO_TAG = "LoadPhotoTask";

        @Override
        public void run() {
            Log.d(THREAD_PHOTO_TAG, "is running");
            try {
                synchronized (Database.LOCK_FOR_PHOTO) {
                    Log.d(THREAD_PHOTO_TAG, "I am waiting");
                    Database.LOCK_FOR_PHOTO.wait();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(THREAD_PHOTO_TAG, "After try-catch statement");


            if (isAdded()) {
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

    private class LoadStoryTask implements Runnable {

        private static final String THREAD_STORY_TAG = "LoadStoryTask";

        @Override
        public void run() {
            Log.d(THREAD_STORY_TAG, "is running");
            try {
                synchronized (Database.LOCK_FOR_STORY) {
                    Log.d(THREAD_STORY_TAG, "I am waiting");
                    Database.LOCK_FOR_STORY.wait();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isAdded()) {
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
