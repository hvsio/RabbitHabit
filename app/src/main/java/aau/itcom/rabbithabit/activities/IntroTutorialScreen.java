package aau.itcom.rabbithabit.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import aau.itcom.rabbithabit.R;

public class IntroTutorialScreen extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int color = Color.parseColor("#95C2E7");

        addSlide(AppIntroFragment.newInstance("Reach everything with only one button!", "Click on a plus button in the corner of your phone to add your picture, story and habits!", R.drawable.fab_button, color));
        addSlide(AppIntroFragment.newInstance("Enter your story and mood of the day!", "Something cool happened to you today? Write it down adn select how you feel about so you will be able to keep this memory forever!", R.drawable.story_picture, color));
        addSlide(AppIntroFragment.newInstance("Implement new habits to improve your life!", "Choose top habits from the ranking or create the new ones, which will benefit you the most!", R.drawable.habit_picture, color));
        addSlide(AppIntroFragment.newInstance("Easily follow your progress! ", "Click and hold for a few seconds a habit, which you have completed and be proud of yourself!", R.drawable.habits_completed, color));

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(WelcomePage.createNewIntent(getApplicationContext()));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(WelcomePage.createNewIntent(getApplicationContext()));
    }

    public static Intent createNewIntent(Context context) {
        return new Intent(context, IntroTutorialScreen.class);
    }
}
