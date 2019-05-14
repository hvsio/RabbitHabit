package aau.itcom.rabbithabit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.tomer.fadingtextview.FadingTextView;

import java.util.concurrent.TimeUnit;

import aau.itcom.rabbithabit.R;
import aau.itcom.rabbithabit.fragments.SettingsFragment;


public class WelcomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(SettingsFragment.SETTINGS, Context.MODE_PRIVATE);
        if (pref.getBoolean(SettingsFragment.WELCOME_SCREEN, true)) {
            FadingTextView fadingTextView = findViewById(R.id.textViewWelcomeName);
            fadingTextView.setTexts(new String[]{"Welcome!", "It's good to see you again " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "!"});
            fadingTextView.setTimeout(4, TimeUnit.SECONDS);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(MainPageActivity.createNewIntent(getApplicationContext()));
                }
            }, 4 * 1000);

        } else {
            startActivity(MainPageActivity.createNewIntent(getApplicationContext()));
        }
    }

    public static Intent createNewIntent(Context context) {
        return new Intent(context, WelcomePage.class);
    }

}
