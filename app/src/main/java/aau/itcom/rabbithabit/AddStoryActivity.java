package aau.itcom.rabbithabit;

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
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

import java.util.Calendar;

import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.objects.Story;

public class AddStoryActivity extends AppCompatActivity {

    String TAG = "AddStoryActivity";
    private EditText story;
    private Button add;
    Database db;
    long getRating;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        db = Database.getInstance();
        final SmileRating smileRating = findViewById(R.id.ratingBar);
        story = findViewById(R.id.editTextStory);
        add = findViewById(R.id.buttonAdd);

        //final long getRating = smileRating.getRating();

//        smileRating.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
//            @Override
//            public void onSmileySelected(@BaseRating.Smiley int smiley, boolean reselected) {
//                // reselected is false when user selects different smiley that previously selected one
//                // true when the same smiley is selected.
//                // Except if it first time, then the value will be false.
//                switch (smiley) {
//                    case SmileRating.BAD:
//                        Log.i(TAG, "Bad");
//                        break;
//                    case SmileRating.GOOD:
//                        Log.i(TAG, "Good");
//                        break;
//                    case SmileRating.GREAT:
//                        Log.i(TAG, "Great");
//                        break;
//                    case SmileRating.OKAY:
//                        Log.i(TAG, "Okay");
//                        break;
//                    case SmileRating.TERRIBLE:
//                        Log.i(TAG, "Terrible");
//                        break;
//                }
//            }
//        });






        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainPageActivity.class);
                Log.i(TAG, "*****************************************************************************************************mood is: " + getRating);
                db.addStory(new Story(Calendar.getInstance().getTime(), story.getText().toString(), getRating-1 ), FirebaseAuth.getInstance().getCurrentUser());
                startActivity(i);

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
    }





     static Intent createNewIntent(Context context) {
        return new Intent(context, AddStoryActivity.class);
    }
}
