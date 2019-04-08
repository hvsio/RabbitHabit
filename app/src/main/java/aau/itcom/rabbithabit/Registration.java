package aau.itcom.rabbithabit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Registration extends AppCompatActivity {

    Button registrationButton;
    EditText email;
    EditText password;
    EditText passwordConf;
    EditText name;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        passwordConf = findViewById(R.id.passwordConf);
        name = findViewById(R.id.name);
        registrationButton = findViewById(R.id.registerWithEmailButton3);

    }

    public void registerUser(View view) {
        String emailText = email.getText().toString().replaceAll(" ","");
        String passwordText = password.getText().toString().replaceAll(" ","");
        String passwordConfText = passwordConf.getText().toString().replaceAll(" ","");
        String nameText = name.getText().toString().replaceAll(" ","");
        if (emailText.equals("") || passwordText.equals("") || nameText.equals("")) {
            Toast.makeText(getApplicationContext(), "Please fill all the fields!", Toast.LENGTH_SHORT).show();
        }
        else if (!passwordConfText.equals(passwordText)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "User registered!", Toast.LENGTH_SHORT).show();
        }
    }

}
