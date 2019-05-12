package aau.itcom.rabbithabit;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroTutorialScreen extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_intro_tutorial_screen);

        addSlide(AppIntroFragment.newInstance("First screen", "First application introduction screen", R.drawable.first_screen, R.drawable.another_gradient));
        addSlide(AppIntroFragment.newInstance("Second screen", "Second application introduction screen", R.drawable.second_screen, R.drawable.another_gradient));
        addSlide(AppIntroFragment.newInstance("Third screen", "Third application introduction screen", R.drawable.third_screen, R.drawable.another_gradient));


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
