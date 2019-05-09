package aau.itcom.rabbithabit;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.tomer.fadingtextview.FadingTextView;

import java.util.concurrent.TimeUnit;

import aau.itcom.rabbithabit.MainPageActivity;
import aau.itcom.rabbithabit.R;

public class WelcomePage extends AppCompatActivity {

    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);


       // name = findViewById(R.id.textViewWelcomeName);
        FadingTextView fadingTextView = findViewById(R.id.textViewWelcomeName);
        fadingTextView.setTexts(new String[]{"Welcome!", "It's good to see you again " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "!"});
        fadingTextView.setTimeout(4, TimeUnit.SECONDS);
        //  name.setText(getIntent().getStringExtra("Name"));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
            }
        }, 4*1000);
    }

    static Intent createNewIntent(Context context) {
        return new Intent(context, WelcomePage.class);
    }

}
