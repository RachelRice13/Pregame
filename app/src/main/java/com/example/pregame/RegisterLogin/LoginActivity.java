package com.example.pregame.RegisterLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.pregame.Common.Validation;
import com.example.pregame.LandingPage;
import com.example.pregame.R;
import com.example.pregame.SelectTeamActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private EditText emailET, passwordET;
    private TextInputLayout emailLO, passwordLO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        emailLO = findViewById(R.id.login_email);
        passwordLO = findViewById(R.id.login_password);


        Button login = findViewById(R.id.login_user_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        Button goToLP = findViewById(R.id.landing_page_button);
        goToLP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent landingPageIntent = new Intent(LoginActivity.this, LandingPage.class);
                startActivity(landingPageIntent);
            }
        });

        // Hides the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void login() {
        emailET = findViewById(R.id.login_email_et);
        passwordET = findViewById(R.id.login_password_et);

        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        boolean validEmail = Validation.validateBlank(email, emailLO);
        boolean validPassword = Validation.validateBlank(password, passwordLO);

        if (validEmail && validPassword) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "loginUserWithEmail: success");
                                Intent selectTeamIntent = new Intent(LoginActivity.this, SelectTeamActivity.class);
                                startActivity(selectTeamIntent);
                            } else {
                                Log.e(TAG, "loginUserWithEmail: failure", task.getException());
                                if (task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                    emailLO.setError("Email doesn't exist");
                                }

                                if (task.getException().getMessage().equals("The password is invalid or the user does not have a password.")) {
                                    passwordLO.setError("Password doesn't match");
                                }
                            }
                        }
                    });
        }
    }
}