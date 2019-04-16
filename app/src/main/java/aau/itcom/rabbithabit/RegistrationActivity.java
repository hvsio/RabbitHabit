package aau.itcom.rabbithabit;

import android.content.Intent;
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
import com.google.firebase.auth.UserProfileChangeRequest;

import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.objects.WelcomePage;

public class RegistrationActivity extends AppCompatActivity {

    Button registrationButton;
    EditText email;
    EditText password;
    EditText passwordConf;
    EditText name;
    String nameText;
    private FirebaseAuth mAuth;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        db = Database.getInstance();
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
         nameText = name.getText().toString();
        if (emailText.equals("") || passwordText.equals("") || nameText.equals("")) {
            Toast.makeText(getApplicationContext(), "Please fill all the fields!", Toast.LENGTH_SHORT).show();
        }
        else if (!passwordConfText.equals(passwordText)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();

        }
        else {
            createAccount(emailText, passwordText, nameText);
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            startActivity(MainPageActivity.createNewIntent(getApplicationContext()));
            Intent i = new Intent(RegistrationActivity.this, WelcomePage.class);

            i.putExtra("Name", nameText);
        }
    }

    private void createAccount(String email, String password, final String nameOfUser) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nameOfUser).build();
                            user.updateProfile(profileUpdates);

                            db.createNewUser(mAuth.getCurrentUser());
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            // TODO FIX THIS, INFO IS NOT ENOUGH
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

}
