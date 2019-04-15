package aau.itcom.rabbithabit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.view.View;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class MainPageActivity extends AppCompatActivity {

    FloatingActionButton fabHabit, fabStory, fabAdd, fabPhoto;
    CoordinatorLayout transitionsContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        transitionsContainer = findViewById(R.id.mainPageLayout);
        fabHabit = transitionsContainer.findViewById(R.id.fabHabit);
        fabStory = transitionsContainer.findViewById(R.id.fabStory);
        fabPhoto = transitionsContainer.findViewById(R.id.fabPhoto);

        fabAdd = transitionsContainer.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                fabHabit.setVisibility(View.VISIBLE);
                fabStory.setVisibility(View.VISIBLE);
                fabPhoto.setVisibility(View.VISIBLE);

            }
        });

        transitionsContainer.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                fabHabit.setVisibility(View.INVISIBLE);
                fabStory.setVisibility(View.INVISIBLE);
                fabPhoto.setVisibility(View.INVISIBLE);
            }
        });
    }

    static Intent createNewIntent(Context context) {
        return new Intent(context, MainPageActivity.class);
    }

    public void logOutFromFirebase(View view){
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        startActivity(LoginActivity.createNewIntent(getApplicationContext()));
    }

    public void abc (View view) {
        Intent intent = new Intent(getApplicationContext(), HabitDetailsActivity.class);
        startActivity(intent);
    }

}
