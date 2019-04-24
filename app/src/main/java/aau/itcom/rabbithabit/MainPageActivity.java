package aau.itcom.rabbithabit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.transition.TransitionManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

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
        transitionsContainer = findViewById(R.id.mainPageLayout);
        fabAdd = findViewById(R.id.fabAdd);

        transitionsContainer = findViewById(R.id.mainLayout);
        fabHabit = transitionsContainer.findViewById(R.id.fabHabit);
        fabStory = transitionsContainer.findViewById(R.id.fabStory);
        fabPhoto = transitionsContainer.findViewById(R.id.fabPhoto);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                if (!isButtonAddClicked()){
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
            }
        });

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_calendar) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CalendarFragment()).commit();
        } else if (id == R.id.nav_main) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MainPageFragment()).commit();
        } else if (id == R.id.nav_profile) {
            //getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MainPageFragment()).commit();
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_log_out) {
            logOutFromFirebase();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    static Intent createNewIntent(Context context) {
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

    public void addPhoto(View view){
        startActivity(AddPhotoActivity.createNewIntent(getApplicationContext()));
    }
}