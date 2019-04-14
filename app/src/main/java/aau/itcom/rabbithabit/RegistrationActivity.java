package aau.itcom.rabbithabit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

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
        String passwordText = password.getText().toString();
        String passwordConfText = passwordConf.getText().toString();
        String nameText = name.getText().toString();
        if (emailText.equals("") || passwordText.equals("") || nameText.equals("")) {
            Toast.makeText(getApplicationContext(), "Please fill all the fields!", Toast.LENGTH_SHORT).show();
        }
        else if (!passwordConfText.equals(passwordText)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();

        }
        else {
            createAccount(emailText, passwordText);
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        startActivity(MainPageActivity.createNewIntent(getApplicationContext()));
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

}
