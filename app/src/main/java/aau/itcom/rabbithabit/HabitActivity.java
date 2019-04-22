package aau.itcom.rabbithabit;

import android.content.Context;
import android.content.Intent;
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

public class HabitActivity extends AppCompatActivity {
    TextView nameTextView;
    TextView durationTextView;
    TextView detailsTextView;
    RadioGroup publishmentRadio;
    Database db;

    private static final String TAG = "AddingHabitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addownhabit);

        nameTextView = findViewById(R.id.nameOfHabit);
        durationTextView = findViewById(R.id.durationOfHabit);
        detailsTextView = findViewById(R.id.detailsEditText);
        publishmentRadio = findViewById(R.id.radioGroupPublishment);

        db = Database.getInstance();
    }

    public void collectInformation(View view) {
        if (checkInformation()) {
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
            Log.d("habitact", "to jest kurwa dziwne");
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
        db.addHabitPersonal(new HabitPersonal(nameTextView.getText().toString(),Integer.parseInt(durationTextView.getText().toString()), detailsTextView.getText().toString(), Calendar.getInstance().getTime()), FirebaseAuth.getInstance().getCurrentUser());
        db.addHabitPublished(new HabitPublished(nameTextView.getText().toString(),Integer.parseInt(durationTextView.getText().toString()), detailsTextView.getText().toString(), "true", FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), 0));
    }

    private void saveHabitAsPersonal() {
        db.addHabitPersonal(new HabitPersonal(nameTextView.getText().toString(),Integer.parseInt(durationTextView.getText().toString()), detailsTextView.getText().toString(), Calendar.getInstance().getTime()), FirebaseAuth.getInstance().getCurrentUser());
    }


    private void displayToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    static Intent createNewIntent(Context context) {
        return new Intent(context, HabitActivity.class);
    }
}
