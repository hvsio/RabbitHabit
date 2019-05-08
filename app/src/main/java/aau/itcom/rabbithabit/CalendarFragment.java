package aau.itcom.rabbithabit;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.objects.HabitPersonal;
import aau.itcom.rabbithabit.objects.Story;

public class CalendarFragment extends Fragment {

    CalendarView calendar;
    LinearLayout layout;
    LinearLayout layoutHabits;
    TextView storyTextView;
    LinearLayout.LayoutParams params;
    ArrayList<HabitPersonal> habitArray;
    ImageView imageView;
    ListView listView;
    CustomAdapterDayHabit adapter;
    Story story;
    Database db;
    private StorageReference mStorageRef;

    private static final String TAG = "CalendarFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mStorageRef = FirebaseStorage.getInstance().getReference();
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
        listView = v.findViewById(R.id.habits_from_the_day);
        layout = v.findViewById(R.id.linearLayoutOfDay);
        calendar = v.findViewById(R.id.calendarViewDaily);
        imageView = v.findViewById(R.id.imageView3);
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
        service.submit(new LoadPhotoTask());
        service.shutdown();

        Log.d(TAG, "I am inside changeCurrentDay(), and I am loading info");
        db.loadSetOfHabitsOnDate(date, FirebaseAuth.getInstance().getCurrentUser());
        db.loadPhotoOnDate(date, FirebaseAuth.getInstance().getCurrentUser(), getContext());
        db.loadStoryOnDate(date, FirebaseAuth.getInstance().getCurrentUser());

    }

    private void displayStory() {
        Log.d(TAG, "Inside displayStory()");
        story = new Story(db.getStory().getDate(),db.getStory().getTextContent(), db.getStory().getMood());
        storyTextView.setText(story.getTextContent());
    }


    private void displayPhoto() {
        if (db.getPhoto() == null) {
            imageView.setImageResource(R.drawable.no_picture);
            imageView.setRotation(0);
        }
        else {
            imageView.setImageURI(db.getPhoto());
            imageView.setRotation(90);
        }
    }

    private void displayHabits(){
        //ArrayList<TextView> textViews = new ArrayList<>(createTextFieldsForHabits());
        listView.setAdapter(null);
        Log.d(TAG, "Inside displayHabits()");
        habitArray.addAll(db.getArrayListOfHabitsPersonal());
        Log.d(TAG, "Inside displayHabits() and habit array size is: " + habitArray.size());
        adapter = new CustomAdapterDayHabit(getContext(), habitArray);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HabitPersonal habitPersonal = habitArray.get(position);
                Intent intent = HabitDetailsActivity.createNewIntent(getContext());
                intent.putExtra("habitPersonal", habitPersonal);
                getContext().startActivity(intent);
            }
        });


//        for(int i = 0;i<textViews.size();i++){
//            layoutHabits.addView(textViews.get(i));
//        }

    }

    public ArrayList<TextView> createTextFieldsForHabits(){
        Log.d(TAG, "Inside createTextFieldsForHabits()");
        ArrayList<TextView> arrayOfTextViews = new ArrayList<>();

        for (int i =0; i<habitArray.size(); i++){
            Log.d(TAG, "Inside loop for textfields - createTextFieldsForHabits()");
            arrayOfTextViews.add(habitArray.get(i).display(getContext(), 18, params, habitArray.get(i)));
        }
        return arrayOfTextViews;
    }



    private class LoadHabitsTask implements Runnable{

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

            Log.d(THREAD_HABIT_TAG, "First element of array is: " + db.getArrayListOfHabitsPersonal().get(0).toString());

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
                synchronized (Database.LOCK_FOR_PHOTO) {
                    Log.d("THREAD !", "I am waiting");
                    Database.LOCK_FOR_PHOTO.wait();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(THREAD_PHOTO_TAG, "After try-catch statement");

            Log.d(THREAD_PHOTO_TAG, "First element of array is: " + db.getArrayListOfHabitsPersonal().get(0).toString());

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
                synchronized (Database.LOCK_FOR_STORY) {
                    Log.d(THREAD_STORY_TAG, "I am waiting");
                    Database.LOCK_FOR_STORY.wait();
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
