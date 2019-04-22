package aau.itcom.rabbithabit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.objects.HabitPersonal;

public class MainPageFragment extends Fragment {

    Database db;
    private static final String TAG = "MainPageFragment";
    private LinearLayout.LayoutParams params;
    private LinearLayout habitsLayout;
    private TextView storyTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_to_main_page, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = Database.getInstance();

        View v = getView();
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        habitsLayout = v.findViewById(R.id.LinearLayout);
        storyTextView = v.findViewById(R.id.myStoryContentTextView);
        loadDetails();
    }

    private void loadDetails(){
        Log.d(TAG, "I am inside loadDetails(), and I am starting THREADS!");
        /*ExecutorService service = Executors.newCachedThreadPool();
        service.submit(new LoadHabitsTask());
        service.submit(new LoadStoryTask());
        //service.submit(new LoadPhotoTask());
        service.shutdown();

        Log.d(TAG, "I am inside loadDetails(), and I am loading info");
        db.loadSetOfHabitsOnDate(Calendar.getInstance().getTime(), FirebaseAuth.getInstance().getCurrentUser());
        //db.loadPhotoOnDate(Calendar.getInstance().getTime(), FirebaseAuth.getInstance().getCurrentUser());
        db.loadStoryOnDate(Calendar.getInstance().getTime(), FirebaseAuth.getInstance().getCurrentUser());
*/

    }

    private void displayHabits(){

        for (int i =0; i<db.getArrayListOfHabits().size(); i++){
            Log.d(TAG, "Inside loop for textfields - createTextFieldsForHabits()");
            TextView textView = new TextView(getContext());
            textView.setText(db.getArrayListOfHabits().get(i).getName());
            textView.setTextSize(18);

            final HabitPersonal habitPersonal = db.getArrayListOfHabits().get(i);
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
            habitsLayout.addView(textView);
        }
    }

    private void displayStory(){
        storyTextView.setText(db.getStory().getTextContent());
    }

    private void displayPhoto(){

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

}
