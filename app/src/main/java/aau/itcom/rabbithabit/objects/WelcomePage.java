package aau.itcom.rabbithabit.objects;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import aau.itcom.rabbithabit.MainPageActivity;
import aau.itcom.rabbithabit.R;

public class WelcomePage extends AppCompatActivity {

    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        name = findViewById(R.id.textViewWelcomeName);
        name.setText(getIntent().getStringExtra("Name"));


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
            }
        }, 4*1000);
    }
}
