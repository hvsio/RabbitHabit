package aau.itcom.rabbithabit.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.service.autofill.CharSequenceTransformation;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

import aau.itcom.rabbithabit.R;
import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.objects.HabitPersonal;
import aau.itcom.rabbithabit.objects.HabitPublished;
import aau.itcom.rabbithabit.system.PhoneState;

import static aau.itcom.rabbithabit.adapters.CustomAdapterSearchingHabits.PASS_HABIT_DETAILS;
import static aau.itcom.rabbithabit.adapters.CustomAdapterSearchingHabits.PASS_HABIT_DURATION;
import static aau.itcom.rabbithabit.adapters.CustomAdapterSearchingHabits.PASS_HABIT_NAME;


public class HabitActivity extends AppCompatActivity {
    TextView nameTextView;
    TextView detailsTextView;
    RadioGroup publishmentRadio;
    TextView questionBar;
    TextView durationTextView;
    Database db;
    Intent intentFromSearching;

    private static final String TAG = "AddingHabitActivity";
    private static final String KEY_PUBLISHMENT = "key_publishment";

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


        if (intentFromSearching.getExtras() != null) {
            Bundle retrievedBundle = getIntent().getExtras();
            String habitName = retrievedBundle.getString(PASS_HABIT_NAME);
            String habitDetails = retrievedBundle.getString(PASS_HABIT_DETAILS);
            String habitDuration = retrievedBundle.getString(PASS_HABIT_DURATION);
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

        PhoneState.hideKeyboard(this);
        if (savedInstanceState != null){
            if (savedInstanceState.getBoolean(KEY_PUBLISHMENT))
                publishmentRadio.check(R.id.yesButton);
            else
                publishmentRadio.check(R.id.noButton);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (publishmentRadio.getCheckedRadioButtonId() != R.id.yesButton)
            outState.putBoolean(KEY_PUBLISHMENT, true);
        else if (publishmentRadio.getCheckedRadioButtonId() != R.id.noButton)
            outState.putBoolean(KEY_PUBLISHMENT, false);
    }

    public void collectInformation(View view) {
        if (intentFromSearching.getExtras() != null) {
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
        int durationInDays = Integer.parseInt(durationTextView.getText().toString());

        if (durationInDays > 100) {
            displayToast("Max duration is 100 days!");
            return false;
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
        db.addHabitPersonal(new HabitPersonal(nameTextView.getText().toString(), Integer.parseInt(durationTextView.getText().toString()), detailsTextView.getText().toString(), Calendar.getInstance().getTime(), null), FirebaseAuth.getInstance().getCurrentUser());
        db.addHabitPublished(new HabitPublished(nameTextView.getText().toString(), Integer.parseInt(durationTextView.getText().toString()), detailsTextView.getText().toString(), "true", FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), 0));
    }

    private void saveHabitAsPersonal() {
        db.addHabitPersonal(new HabitPersonal(nameTextView.getText().toString(), Integer.parseInt(durationTextView.getText().toString()), detailsTextView.getText().toString(), Calendar.getInstance().getTime(), null), FirebaseAuth.getInstance().getCurrentUser());
    }


    private void displayToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }


    public static Intent createNewIntent(Context context) {
        return new Intent(context, HabitActivity.class);
    }
}
