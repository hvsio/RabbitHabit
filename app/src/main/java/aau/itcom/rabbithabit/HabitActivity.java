package aau.itcom.rabbithabit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HabitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addownhabit);
    }

    static Intent createNewIntent(Context context) {
        return new Intent(context, HabitActivity.class);
    }
}
