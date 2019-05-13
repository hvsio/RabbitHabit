package aau.itcom.rabbithabit;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroTutorialScreen extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_intro_tutorial_screen);

        int color = Color.parseColor("#95C2E7");

        addSlide(AppIntroFragment.newInstance("Reach everything with only one button!", "Click on a plus button in the corner of your phone to add your picture, story and habits!", R.drawable.fab_button, color));
        addSlide(AppIntroFragment.newInstance("Enter your story and mood of the day!", "Something cool happened to you today? Write it down adn select how you feel about so you will be able to keep this memory forever!", R.drawable.story_picture, color));
        addSlide(AppIntroFragment.newInstance("Implement new habits to improve your life!", "Choose top habits from the ranking or create the new ones, which will benefit you the most!", R.drawable.habit_picture, color));
        addSlide(AppIntroFragment.newInstance("Easily follow your progress! ", "Click and hold for a few seconds a habit, which you have completed and be proud of yourself!", R.drawable.habits_completed, color));

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent i = new Intent(getApplicationContext(), WelcomePage.class);
        startActivity(i);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent i = new Intent(getApplicationContext(), WelcomePage.class);
        startActivity(i);
    }
}
