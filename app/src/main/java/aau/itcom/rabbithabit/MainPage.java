package aau.itcom.rabbithabit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
    }

    static Intent createNewIntent(Context context) {
        return new Intent(context, MainPage.class);
    }

    public void logOutFromFrirebase(View view){
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        startActivity(LoginActivity.createNewIntent(getApplicationContext()));
    }
}
