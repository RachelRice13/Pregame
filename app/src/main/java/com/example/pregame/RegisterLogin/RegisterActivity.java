package com.example.pregame.RegisterLogin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pregame.Common.CommonActivity;
import com.example.pregame.Common.Validation;
import com.example.pregame.LandingPage;
import com.example.pregame.Model.User;
import com.example.pregame.R;
import com.example.pregame.SelectTeamActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;

public class RegisterActivity extends CommonActivity {
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private RadioGroup radioGroup;
    private String userType, dob, imagePath;
    private EditText firstNameET, surnameET, phoneET, emailET, passwordET;
    private TextInputLayout firstNameLO, surnameLO, phoneLO, emailLO, passwordLO;
    private TextView dobTV;
    private Uri profileUri;
    private ImageView profileIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setup();
        setDatePicker();
        goToPage(R.id.landing_page_button, RegisterActivity.this, LandingPage.class);
        hideActionBar();
    }

    private void register() {
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
        boolean validImage = Validation.validImage(profileUri, findViewById(R.id.scroll_view));

        getSelectedRB();

        if (validFirstName && validSurname && validPhone && validEmail && validPassword && validImage) {
            if (userType.equals("Player")) {
                addToDB(firstName, surname, phone, email, password, "player");
            } else {
                addToDB(firstName, surname, phone, email, password, "coach");
            }
        }

    }

    private void getSelectedRB() {
        int selectedButton = radioGroup.getCheckedRadioButtonId();

         if (selectedButton == R.id.radio_coach){
            userType = "Coach";
        } else if (selectedButton == R.id.radio_player){
            userType = "Player";
        }
    }

    private void setDatePicker() {
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
                        String date = dayOfMonth + "/" + month + "/" + year;
                        dobTV.setText(date);
                        dob = dayOfMonth + "/" + month + "/" + year;
                    }
                }, YEAR, MONTH, DAY);
                datePickerDialog.show();
            }
        });
    }

    private void addToDB(String firstName, String surname, String phone, String email, String password, String type) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String id = FirebaseAuth.getInstance().getUid();
                            uploadPicture(id);
                            User user = new User(firstName, surname, dob, phone, email, password, imagePath, new ArrayList<>());
                            addToFireStore(user, id, type);
                        } else {
                            Log.e(TAG, task.getException().getMessage());
                            if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                                emailLO.setError("Email Taken");
                            }
                        }
                    }
                });
    }

    private void addToFireStore(User user, String id, String type) {
        firebaseFirestore.collection(type).document(id)
                .set(user)
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
                        Toast.makeText(RegisterActivity.this, "Failed to Register User", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.toString());
                    }
                });
    }

    private void chooseProfilePicture() {
        Intent choosePictureIntent = new Intent();
        choosePictureIntent.setType("image/*");
        choosePictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(choosePictureIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profileUri = data.getData();
            profileIv.setImageURI(profileUri);
        }
    }

    public void uploadPicture(String id) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image....");
        progressDialog.show();

        StorageReference profilePictureRef = storageReference.child("users/" + id + "/profile_pic");
        imagePath = profilePictureRef.getPath();

        profilePictureRef.putFile(profileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar.make(findViewById(R.id.scroll_view), "Image Uploaded", Snackbar.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(R.id.scroll_view), "Failed to Upload Picture", Snackbar.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Progress: " + (int) progressPercent + "%");
                    }
                });
    }

    private void setup() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        firstNameET = findViewById(R.id.reg_first_name_et);
        surnameET = findViewById(R.id.reg_surname_et);
        phoneET = findViewById(R.id.reg_phone_number_et);
        emailET = findViewById(R.id.reg_email_et);
        passwordET = findViewById(R.id.reg_password_et);
        radioGroup = findViewById(R.id.radio_group);
        firstNameLO = findViewById(R.id.reg_first_name);
        surnameLO = findViewById(R.id.reg_surname);
        emailLO = findViewById(R.id.reg_email);
        phoneLO = findViewById(R.id.reg_phone_number);
        passwordLO = findViewById(R.id.reg_password);

        profileIv = findViewById(R.id.profile_pic);
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseProfilePicture();
            }
        });

        Button register = findViewById(R.id.register_user_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }
}