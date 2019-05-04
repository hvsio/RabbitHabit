package aau.itcom.rabbithabit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.objects.HabitPersonal;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainPageFragment extends Fragment {

    Database db;
    private static final String TAG = "MainPageFragment";
    private LinearLayout.LayoutParams params;
    private LinearLayout habitsLayout;
    private TextView storyTextView;
    private ImageView photoView;
    private CircleImageView profilePic;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_to_mainpage, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = Database.getInstance();

        View v = getView();
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        habitsLayout = v.findViewById(R.id.habitLinearLayout);
        storyTextView = v.findViewById(R.id.textViewForStoryContent);
        photoView = v.findViewById(R.id.photoOfTheDay);
        profilePic = v.findViewById(R.id.profile_image);
        photoView.setVisibility(View.GONE);
        loadDetails();
    }

    private void loadDetails(){
        Log.d(TAG, "I am inside loadDetails(), and I am starting THREADS!");
        ExecutorService service = Executors.newCachedThreadPool();
        service.submit(new LoadHabitsTask());
        service.submit(new LoadStoryTask());
        service.submit(new LoadPhotoTask());
        service.submit(new LoadProfilePictureTask());
        service.shutdown();

        Log.d(TAG, "I am inside loadDetails(), and I am loading info");
        db.loadSetOfHabitsOnDate(Calendar.getInstance().getTime(), FirebaseAuth.getInstance().getCurrentUser());
        db.loadPhotoOnDate(Calendar.getInstance().getTime(), FirebaseAuth.getInstance().getCurrentUser(), getContext());
        db.loadStoryOnDate(Calendar.getInstance().getTime(), FirebaseAuth.getInstance().getCurrentUser());
        db.loadProfilePicture(FirebaseAuth.getInstance().getCurrentUser(), getContext());

    }

    private void displayHabits(){

        try {
            for (int i = 0; i < db.getArrayListOfHabitsPersonal().size(); i++) {
                Log.d(TAG, "Inside loop for textfields - createTextFieldsForHabits()");
                TextView textView = new TextView(getContext());
                textView.setText(db.getArrayListOfHabitsPersonal().get(i).getName());
                textView.setTextSize(18);
                habitsLayout.addView(textView);

                final HabitPersonal habitPersonal = db.getArrayListOfHabitsPersonal().get(i);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = HabitDetailsActivity.createNewIntent(getContext());
                        intent.putExtra("habitPersonal", habitPersonal);
                        getContext().startActivity(intent);
                    }
                });
                textView.setLayoutParams(params);
                textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.my_button_white));

            }
        } catch (NoSuchElementException ex){
            Log.w(TAG, "Error loading Habits. No habits to display!\n" + ex);
            // make textfield NO HABITS TO DISPLAY!
        }
    }

    private void displayStory(){
        String text = "You have no story to display.";

        try{
            db.getStory();
           // storyTextView.setText(db.getStory().getTextContent());
            storyTextView.setText(R.string.story_content);
        } catch (NoSuchElementException | NullPointerException ex) {
            Log.w(TAG, "Error loading Story. No story to display!\n" + ex);
            storyTextView.setText(text);
        }

    }

    private void displayPhoto(){
        try{
            photoView.setImageURI(db.getPhoto());
            photoView.setVisibility(View.VISIBLE);
        } catch(NoSuchElementException ex) {
            Log.w(TAG, "Error loading Photo. No photo to display!\n" + ex);
        }
    }

    private void displayProfilePicture() {
        profilePic.setImageURI(db.getPhoto());
    }

    private class LoadHabitsTask implements Runnable{

        private static final String THREAD_HABIT_TAG = "LoadHabitsTask";

        @Override
        public void run() {
            try {
                synchronized (Database.LOCK_FOR_HABITS) {
                    Database.LOCK_FOR_HABITS.wait();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
            try {
                synchronized (Database.LOCK_FOR_PHOTO) {
                    Database.LOCK_FOR_PHOTO.wait();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
            try {
                synchronized (Database.LOCK_FOR_STORY) {
                    Database.LOCK_FOR_STORY.wait();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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

    private class LoadProfilePictureTask implements Runnable{

        private static final String THREAD_PHOTO_TAG = "LoadProfilePictureTask";

        @Override
        public void run() {
            try {
                synchronized (Database.LOCK_FOR_PROFILE_PIC) {
                    Database.LOCK_FOR_PROFILE_PIC.wait();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isAdded()){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayProfilePicture();
                    }
                });
            } else {
                Log.w(THREAD_PHOTO_TAG, "Fragment is no longer attached to its activity");
            }
        }
    }

}
