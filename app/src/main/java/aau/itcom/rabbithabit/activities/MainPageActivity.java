package aau.itcom.rabbithabit.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.transition.TransitionManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import aau.itcom.rabbithabit.R;
import aau.itcom.rabbithabit.fragments.CalendarFragment;
import aau.itcom.rabbithabit.fragments.MainPageFragment;
import aau.itcom.rabbithabit.fragments.ProfileFragment;
import aau.itcom.rabbithabit.fragments.SettingsFragment;


public class MainPageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton fabHabit, fabStory, fabAdd, fabPhoto;
    CoordinatorLayout transitionsContainer;
    View viewBlurred;
    boolean isButtonAddClicked;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        isButtonAddClicked = false;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewBlurred = findViewById(R.id.viewBlurred);
        fabAdd = findViewById(R.id.fabAdd);

        transitionsContainer = findViewById(R.id.mainLayout);
        fabHabit = transitionsContainer.findViewById(R.id.fabHabit);
        fabStory = transitionsContainer.findViewById(R.id.fabStory);
        fabPhoto = transitionsContainer.findViewById(R.id.fabPhoto);


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                if (!isButtonAddClicked()) {
                    TransitionManager.beginDelayedTransition(transitionsContainer);
                    viewBlurred.setVisibility(View.VISIBLE);
                    fabHabit.setVisibility(View.VISIBLE);
                    fabStory.setVisibility(View.VISIBLE);
                    fabPhoto.setVisibility(View.VISIBLE);
                    setButtonAddClicked(true);
                } else {
                    TransitionManager.beginDelayedTransition(transitionsContainer);
                    viewBlurred.setVisibility(View.GONE);
                    fabHabit.setVisibility(View.INVISIBLE);
                    fabStory.setVisibility(View.INVISIBLE);
                    fabPhoto.setVisibility(View.INVISIBLE);
                    setButtonAddClicked(false);
                }

            }
        });

        fabPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoto(v);
                viewBlurred.performClick();
            }
        });

        fabStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStory(v);
                viewBlurred.performClick();
            }
        });

        fabHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHabit(v);
                viewBlurred.performClick();
            }
        });

        viewBlurred.setSoundEffectsEnabled(false);
        viewBlurred.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                viewBlurred.setVisibility(View.GONE);
                fabHabit.setVisibility(View.INVISIBLE);
                fabStory.setVisibility(View.INVISIBLE);
                fabPhoto.setVisibility(View.INVISIBLE);
                setButtonAddClicked(false);
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MainPageFragment()).commit();

    }

    public boolean isButtonAddClicked() {
        return isButtonAddClicked;
    }

    public void setButtonAddClicked(boolean buttonAddClicked) {
        isButtonAddClicked = buttonAddClicked;
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().findFragmentById(R.id.frameLayout) instanceof MainPageFragment) {
            //Checking for fragment count on backstack
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();

            } else if (!doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                this.finishAffinity();
            }
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_calendar) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CalendarFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_main) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MainPageFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProfileFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SettingsFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_log_out) {
            logOutFromFirebase();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static Intent createNewIntent(Context context) {
        return new Intent(context, MainPageActivity.class);
    }

    public void logOutFromFirebase() {
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        startActivity(LoginActivity.createNewIntent(getApplicationContext()));
    }

    public void addHabit(View view) {
        Intent intent = new Intent(getApplicationContext(), AddHabitActivity.class);
        startActivity(intent);
    }

    public void addStory(View view) {
        Intent intent = new Intent(getApplicationContext(), AddStoryActivity.class);
        startActivity(intent);
    }

    public void addPhoto(View view) {
        startActivity(AddPhotoActivity.createNewIntent(getApplicationContext()));
    }

}