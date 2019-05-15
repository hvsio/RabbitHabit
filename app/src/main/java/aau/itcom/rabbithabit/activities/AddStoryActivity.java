package aau.itcom.rabbithabit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.hsalf.smilerating.SmileRating;

import java.util.Calendar;

import aau.itcom.rabbithabit.R;
import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.objects.Story;

public class AddStoryActivity extends AppCompatActivity {

    private static final String KEY_STORY = "key_story";
    private static final String KEY_MOOD = "key_mood";
    String TAG = "AddStoryActivity";
    private EditText story;
    Database db;
    long getRating;
    SmileRating smileRating;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        db = Database.getInstance();
        smileRating = findViewById(R.id.ratingBar);
        story = findViewById(R.id.editTextStory);

        Button add = findViewById(R.id.buttonAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "*****************************************************************************************************mood is: " + getRating);
                db.addStory(new Story(Calendar.getInstance().getTime(), story.getText().toString(), getRating - 1), FirebaseAuth.getInstance().getCurrentUser());
                startActivity(MainPageActivity.createNewIntent(getApplicationContext()));

            }
        });

        smileRating.setOnRatingSelectedListener(new SmileRating.OnRatingSelectedListener() {
            @Override
            public void onRatingSelected(int dua, boolean reselected) {
                // level is from 1 to 5 (0 when none selected)
                // reselected is false when user selects different smiley that previously selected one
                // true when the same smiley is selected.
                // Except if it first time, then the value will be false.
                int level = smileRating.getRating();
                getRating = level;
            }
        });

        if (savedInstanceState != null){
            story.setText(savedInstanceState.getCharSequence(KEY_STORY));
            smileRating.setSelectedSmile(savedInstanceState.getInt(KEY_MOOD)-1);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_STORY, story.getText());
        outState.putInt(KEY_MOOD, smileRating.getRating());
    }

    static Intent createNewIntent(Context context) {
        return new Intent(context, AddStoryActivity.class);
    }
}
