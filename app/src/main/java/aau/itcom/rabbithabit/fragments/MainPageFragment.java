package aau.itcom.rabbithabit.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hsalf.smilerating.SmileRating;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aau.itcom.rabbithabit.R;
import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.objects.HabitPersonal;
import aau.itcom.rabbithabit.system.PhoneState;
import aau.itcom.rabbithabit.objects.Story;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainPageFragment extends Fragment {

    Database db;
    private static final String TAG = "MainPageFragment";
    private SmileRating ratingBar;
    private LinearLayout habitsLayout;
    private TextView storyTextView;
    private ImageView photoView;
    private CircleImageView profilePic;

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
        ConstraintLayout constraintLayoutMainPageFragment = v.findViewById(R.id.layout);
        ratingBar = constraintLayoutMainPageFragment.findViewById(R.id.ratingBar2);
        habitsLayout = v.findViewById(R.id.habitsLayoutMainPage);
        storyTextView = v.findViewById(R.id.textViewForStoryContent);
        photoView = v.findViewById(R.id.photoOfTheDay);
        profilePic = v.findViewById(R.id.profile_image);

        if (savedInstanceState != null){
            habitsLayout.removeAllViews();
        } else {
            loadDetails();
        }
/*

        Button button = v.findViewById(R.id.buttonTesting);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), IntroTutorialScreen.class);
                startActivity(i);
            }
        });
*/
    }

    private void loadDetails() {
        Log.d(TAG, "I am inside loadDetails(), and I am starting THREADS!");
        SharedPreferences pref = getContext().getSharedPreferences(SettingsFragment.SETTINGS, Context.MODE_PRIVATE);

        ExecutorService service = Executors.newCachedThreadPool();
        service.submit(new LoadHabitsTask());
        service.submit(new LoadStoryTask());
        service.submit(new LoadProfilePictureTask());
        service.shutdown();

        Log.d(TAG, "I am inside loadDetails(), and I am loading info");
        db.loadSetOfHabitsOnDate(Calendar.getInstance().getTime(), FirebaseAuth.getInstance().getCurrentUser());
        db.loadStoryOnDate(Calendar.getInstance().getTime(), FirebaseAuth.getInstance().getCurrentUser());
        db.loadProfilePicture(FirebaseAuth.getInstance().getCurrentUser(), getContext());

        if (PhoneState.getConnectionType(getActivity()).equals("WIFI") || !pref.getBoolean(SettingsFragment.DOWNLOAD_PHOTO, true)) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(new LoadPhotoTask());
            executorService.shutdown();
            db.loadPhotoOnDate(Calendar.getInstance().getTime(), FirebaseAuth.getInstance().getCurrentUser(), getContext());
        } else
            Toast.makeText(getActivity(), "Connect to WIFI or change setting to download photos", Toast.LENGTH_LONG).show();

    }

    private void displayHabits() {
        Log.i(TAG, "INSIDE DISPLAYHABITS()");

        habitsLayout.removeAllViews();
        final ArrayList<TextView> textViews = new ArrayList<>(createTextFieldsForHabits());

        for (int i = 0; i < textViews.size(); i++) {
            habitsLayout.addView(textViews.get(i));
        }
    }

    public ArrayList<TextView> createTextFieldsForHabits() {
        Log.d(TAG, "Inside createTextFieldsForHabits()");
        ArrayList<TextView> arrayOfTextViews = new ArrayList<>();

        ArrayList<HabitPersonal> habits = new ArrayList<>(db.getArrayListOfHabitsPersonal());

        for (int i = 0; i < habits.size(); i++) {
            arrayOfTextViews.add(habits.get(i).display(getActivity(), 18, habitsLayout.getLayoutParams(), habits.get(i), Calendar.getInstance().getTime()));
        }

        return arrayOfTextViews;
    }


    private void displayStory() {
        String text = "You have no story to display.";
        Story story = null;

        try {
            story = db.getStory();
        } catch (NoSuchElementException ex) {
            Log.w(TAG, "Error loading Story. No story to display!\n" + ex);
            storyTextView.setText(text);
        }

        try {
            storyTextView.setText(story.getTextContent());
        } catch (NullPointerException ex) {
            Log.w(TAG, "Error loading Story Content. No story to display!\n" + ex);
            storyTextView.setText(text);
        }

        try {
            ratingBar.setSelectedSmile(((int) story.getMood()));
        } catch (NullPointerException ex) {
            Log.w(TAG, "Error loading Story Mood. No mood to display!\n" + ex);
        }
    }

    private void displayPhoto() {
        try {
            Uri photo = db.getPhoto();

            if (photo == null) {
                photoView.setImageResource(R.drawable.no_picture);
                photoView.setRotation(0);
            } else {
                photoView.setImageURI(photo);
                photoView.setRotation(90);
            }
        } catch (NoSuchElementException ex) {
            Log.w(TAG, "Error loading Photo. No photo to display!\n" + ex);
        }
    }

    private void displayProfilePicture() {
        profilePic.setImageURI(db.getProfilePhoto());

    }

    private class LoadHabitsTask implements Runnable {

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
            try {
                synchronized (Database.LOCK_FOR_PHOTO) {
                    Database.LOCK_FOR_PHOTO.wait();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
            try {
                synchronized (Database.LOCK_FOR_STORY) {
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

    private class LoadProfilePictureTask implements Runnable {

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

            if (isAdded()) {
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
