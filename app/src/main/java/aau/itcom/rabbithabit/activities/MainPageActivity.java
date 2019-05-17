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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.transition.TransitionManager;
import android.util.Log;
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

import java.io.File;

import aau.itcom.rabbithabit.R;
import aau.itcom.rabbithabit.fragments.CalendarFragment;
import aau.itcom.rabbithabit.fragments.MainPageFragment;
import aau.itcom.rabbithabit.fragments.ProfileFragment;
import aau.itcom.rabbithabit.fragments.SettingsFragment;
import aau.itcom.rabbithabit.system.PhoneState;


public class MainPageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String KEY_FRAGMET = "key_fragment";
    FloatingActionButton fabHabit, fabStory, fabAdd, fabPhoto;
    CoordinatorLayout transitionsContainer;
    View viewBlurred;
    boolean isButtonAddClicked;
    NavigationView navigationView;


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

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_main);

        if (savedInstanceState != null) {
            String currentFragment = savedInstanceState.getString(KEY_FRAGMET);

            if (currentFragment.equals("MainPageFragment"))
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MainPageFragment()).commit();
            if (currentFragment.equals("CalendarFragment"))
                //getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CalendarFragment()).commit();
                getSupportFragmentManager().getFragment(savedInstanceState, CalendarFragment.class.getName());
            if (currentFragment.equals("ProfileFragment"))
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProfileFragment()).commit();
            if (currentFragment.equals("SettingsFragment"))
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SettingsFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MainPageFragment()).commit();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);

        if (currentFragment instanceof MainPageFragment)
            outState.putString(KEY_FRAGMET, "MainPageFragment");
        if (currentFragment instanceof CalendarFragment) {
            outState.putString(KEY_FRAGMET, "CalendarFragment");
            getSupportFragmentManager().putFragment(outState, CalendarFragment.class.getName(), getSupportFragmentManager().findFragmentByTag(CalendarFragment.class.getName()));
        }
        if (currentFragment instanceof ProfileFragment)
            outState.putString(KEY_FRAGMET, "ProfileFragment");
        if (currentFragment instanceof SettingsFragment)
            outState.putString(KEY_FRAGMET, "SettingsFragment");
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
        } else if (isButtonAddClicked) {
            fabAdd.performClick();
        } else if (getSupportFragmentManager().findFragmentById(R.id.frameLayout) instanceof MainPageFragment) {
            if (!doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else{
                PhoneState.deleteCache(getApplicationContext());
                this.finishAffinity();
            }
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {

                int index = getSupportFragmentManager().getBackStackEntryCount() - 2;
                Log.i("* * * * * * * * * * *", "INDEX IS: " + index);
                FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(index);
                Log.i("* * * * * * * * * * *", "FRAGMENT IS: " + backStackEntry.getName());


                Fragment fragment = getSupportFragmentManager().findFragmentByTag(backStackEntry.getName());
                Log.i("* * * * * * * * * * *", "FRAGMENT ASSIGNED IS: " + fragment);
                if (fragment instanceof MainPageFragment) {
                    navigationView.setCheckedItem(R.id.nav_main);
                }
                if (fragment instanceof CalendarFragment) {
                    Log.i("* * * * * * * * * * *", "INSIDE CALENDAR");
                    navigationView.setCheckedItem(R.id.nav_calendar);
                }
                if (fragment instanceof ProfileFragment) {
                    navigationView.setCheckedItem(R.id.nav_profile);
                }
                if (fragment instanceof SettingsFragment) {
                    navigationView.setCheckedItem(R.id.nav_settings);
                }

                getSupportFragmentManager().popBackStack();
            } else {
                navigationView.setCheckedItem(R.id.nav_main);
                getSupportFragmentManager().popBackStack();
            }
        } else
            super.onBackPressed();
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_calendar) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CalendarFragment(), CalendarFragment.class.getName()).addToBackStack(CalendarFragment.class.getName()).commit();
        } else if (id == R.id.nav_main) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MainPageFragment(), MainPageFragment.class.getName()).addToBackStack(MainPageFragment.class.getName()).commit();
        } else if (id == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProfileFragment(), ProfileFragment.class.getName()).addToBackStack(ProfileFragment.class.getName()).commit();
        } else if (id == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SettingsFragment(), SettingsFragment.class.getName()).addToBackStack(SettingsFragment.class.getName()).commit();
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
        startActivity(AddHabitActivity.createNewIntent(getApplicationContext()));
    }

    public void addStory(View view) {
        startActivity(AddStoryActivity.createNewIntent(getApplicationContext()));
    }

    public void addPhoto(View view) {
        startActivity(AddPhotoActivity.createNewIntent(getApplicationContext()));
    }

}