package aau.itcom.rabbithabit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.objects.HabitPersonal;
import aau.itcom.rabbithabit.objects.HabitPublished;

import static aau.itcom.rabbithabit.CustomAdapterSearchingHabits.PASS_HABIT_DETAILS;
import static aau.itcom.rabbithabit.CustomAdapterSearchingHabits.PASS_HABIT_DURATION;
import static aau.itcom.rabbithabit.CustomAdapterSearchingHabits.PASS_HABIT_NAME;


public class HabitActivity extends AppCompatActivity {
    TextView nameTextView;
    TextView detailsTextView;
    RadioGroup publishmentRadio;
    TextView questionBar;
    TextView durationTextView;
    Database db;
    private String habitName;
    private String habitDetails;
    private String habitDuration;
    Intent intentFromSearching;

    private static final String TAG = "AddingHabitActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addownhabit);

        intentFromSearching = this.getIntent();

        nameTextView = findViewById(R.id.nameOfHabit);
        detailsTextView = findViewById(R.id.detailsEditText);
        publishmentRadio = findViewById(R.id.radioGroupPublishment);
        questionBar = findViewById(R.id.questionBar);
        durationTextView = findViewById(R.id.durationTextView);

        db = Database.getInstance();


        if(intentFromSearching.getExtras()!=null) {
            Bundle retrievedBundle = getIntent().getExtras();
            habitName = retrievedBundle.getString(PASS_HABIT_NAME);
            habitDetails = retrievedBundle.getString(PASS_HABIT_DETAILS);
            habitDuration = retrievedBundle.getString(PASS_HABIT_DURATION);
            nameTextView.setEnabled(false);
            nameTextView.setText(habitName);
            nameTextView.setTextColor(Color.BLACK);
            durationTextView.setEnabled(true);
            durationTextView.setText(habitDuration);
            durationTextView.setTextColor(Color.BLACK);
            detailsTextView.setEnabled(true);
            detailsTextView.setText(habitDetails);
            detailsTextView.setTextColor(Color.BLACK);
            publishmentRadio.setVisibility(View.GONE);
            questionBar.setVisibility(View.GONE);
        }
    }


    public void collectInformation(View view) {
        if(intentFromSearching.getExtras()!=null) {
            saveHabitAsPersonal();
        } else if (checkInformation()) {
            switch (publishmentRadio.getCheckedRadioButtonId()) {
                case R.id.yesButton:
                    blockEdition(true);
                    saveHabitAsPublished();
                    break;
                case R.id.noButton:
                    blockEdition(true);
                    saveHabitAsPersonal();
                    break;
            }
        } else {
            displayToast("Please enter all the information!");
        }

        startActivity(MainPageActivity.createNewIntent(getApplicationContext()));
    }

    private boolean checkInformation() {
        if (publishmentRadio.getCheckedRadioButtonId() == -1){
            Log.d(TAG, "to jest kurwa dziwne");
        }

        return !nameTextView.getText().equals("") && !durationTextView.getText().equals("")
                && !detailsTextView.getText().equals("") && publishmentRadio.getCheckedRadioButtonId() != -1;
    }

    private void blockEdition(boolean isToBeBlocked) {
        if (isToBeBlocked) {
            nameTextView.setEnabled(false);
            durationTextView.setEnabled(false);
            detailsTextView.setEnabled(false);
            publishmentRadio.setEnabled(false);
        } else {
            nameTextView.setEnabled(true);
           durationTextView.setEnabled(true);
            detailsTextView.setEnabled(true);
            publishmentRadio.setEnabled(true);
        }
    }

    private void saveHabitAsPublished() {
        db.addHabitPersonal(new HabitPersonal(nameTextView.getText().toString(),Integer.parseInt(durationTextView.getText().toString()), detailsTextView.getText().toString(), Calendar.getInstance().getTime(), null), FirebaseAuth.getInstance().getCurrentUser());
        db.addHabitPublished(new HabitPublished(nameTextView.getText().toString(),Integer.parseInt(durationTextView.getText().toString()), detailsTextView.getText().toString(), "true", FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), 0));
    }

    private void saveHabitAsPersonal() {
        db.addHabitPersonal(new HabitPersonal(nameTextView.getText().toString(),Integer.parseInt(durationTextView.getText().toString()), detailsTextView.getText().toString(), Calendar.getInstance().getTime(), null), FirebaseAuth.getInstance().getCurrentUser());
    }


    private void displayToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }



    public static Intent createNewIntent(Context context) {
        return new Intent(context, HabitActivity.class);
    }
}
