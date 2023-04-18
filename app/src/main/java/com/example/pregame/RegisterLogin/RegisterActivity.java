package com.example.pregame.RegisterLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pregame.Common.Validation;
import com.example.pregame.LandingPage;
import com.example.pregame.Model.Coach;
import com.example.pregame.Model.Player;
import com.example.pregame.R;
import com.example.pregame.SelectTeamActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private RadioGroup radioGroup;
    private String userType, dob;
    private EditText firstNameET, surnameET, phoneET, emailET, passwordET;
    private TextInputLayout firstNameLO, surnameLO, phoneLO, emailLO, passwordLO;
    private TextView dobTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        radioGroup = findViewById(R.id.radio_group);
        firstNameLO = findViewById(R.id.reg_first_name);
        surnameLO = findViewById(R.id.reg_surname);
        emailLO = findViewById(R.id.reg_email);
        phoneLO = findViewById(R.id.reg_phone_number);
        passwordLO = findViewById(R.id.reg_password);

        setDatePicker();

        Button register = findViewById(R.id.register_user_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        Button goToLP = findViewById(R.id.landing_page_button);
        goToLP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent landingPageIntent = new Intent(RegisterActivity.this, LandingPage.class);
                startActivity(landingPageIntent);
            }
        });

        // Hides the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void register() {
        firstNameET = findViewById(R.id.reg_first_name_et);
        surnameET = findViewById(R.id.reg_surname_et);
        phoneET = findViewById(R.id.reg_phone_number_et);
        emailET = findViewById(R.id.reg_email_et);
        passwordET = findViewById(R.id.reg_password_et);

        String firstName = firstNameET.getText().toString();
        String surname = surnameET.getText().toString();
        String phone = phoneET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        boolean validFirstName = Validation.validateBlank(firstName, firstNameLO);
        boolean validSurname = Validation.validateBlank(surname, surnameLO);
        boolean validPhone = Validation.validatePhone(phone, phoneLO);
        boolean validEmail = Validation.validateBlank(email, emailLO);
        boolean validPassword = Validation.validatePassword(password, passwordLO);

        getSelectedRB();

        if (validFirstName && validSurname && validPhone && validEmail && validPassword) {
            if (userType.equals("Player")) {
                addPlayerToDB(firstName, surname, phone, email, password);
            } else {
                addCoachToDB(firstName, surname, phone, email, password);
            }
        }

    }

    public void getSelectedRB() {
        int selectedButton = radioGroup.getCheckedRadioButtonId();

         if (selectedButton == R.id.radio_coach){
            userType = "Coach";
        } else if (selectedButton == R.id.radio_player){
            userType = "Player";
        }
    }

    public void setDatePicker() {
        dobTV = findViewById(R.id.reg_dob_tv);
        dobTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int DAY = calendar.get(Calendar.DATE);
                int MONTH = calendar.get(Calendar.MONTH);
                int YEAR = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        month += 1;
                        dobTV.setText(dayOfMonth + "/" + month + "/" + year);
                        dob = dayOfMonth + "/" + month + "/" + year;
                    }
                }, YEAR, MONTH, DAY);
                datePickerDialog.show();
            }
        });
    }

    public void addPlayerToDB(String firstName, String surname, String phone, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Add to Firestore
                            Player player = new Player(firstName, surname, dob, phone, email, password, new ArrayList<DocumentReference>());
                            String playerId = FirebaseAuth.getInstance().getUid();;
                            addPlayerToFireStore(player, playerId);
                        } else {
                            Log.e(TAG, task.getException().getMessage());
                            if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                                emailLO.setError("Email Taken");
                            }

                        }
                    }
                });
    }

    public void addPlayerToFireStore(Player player, String playerId) {
        firebaseFirestore.collection("player").document(playerId)
                .set(player)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "createUserWithEmail: successful");
                        Intent selectTeamIntent = new Intent(RegisterActivity.this, SelectTeamActivity.class);
                        startActivity(selectTeamIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Failed to Register Player", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.toString());
                    }
                });
    }


    public void addCoachToDB(String firstName, String surname, String phone, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Add to Firestore
                            Coach coach = new Coach(firstName, surname, dob, phone, email, password, new ArrayList<DocumentReference>());
                            String coachId = FirebaseAuth.getInstance().getUid();;
                            addCoachToFireStore(coach, coachId);
                        } else {
                            Log.e(TAG, task.getException().getMessage());
                            if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                                emailLO.setError("Email Taken");
                            }

                        }
                    }
                });
    }

    public void addCoachToFireStore(Coach coach, String coachId) {
        firebaseFirestore.collection("coach").document(coachId)
                .set(coach)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "createUserWithEmail: successful");
                        Intent selectTeamIntent = new Intent(RegisterActivity.this, SelectTeamActivity.class);
                        startActivity(selectTeamIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Failed to Register Coach", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.toString());
                    }
                });
    }
}