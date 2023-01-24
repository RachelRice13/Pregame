package com.example.pregame.Profile;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pregame.Model.Coach;
import com.example.pregame.Model.Player;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfileFragment extends Fragment {
    public static String userType = "";
    public static Player globalPlayer = new Player();
    public static Coach globalCoach = new Coach();
    public static final String TAG = "Profile";
    private View view;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private TextView fullNameTv, emailTv, phoneTv, dobTv, passwordTv;
    private ImageView profilePicIv;
    private Uri profileUri;

    public EditProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        currentUser = firebaseAuth.getCurrentUser();
        profilePicIv = view.findViewById(R.id.edit_profile_pic);

        getUserDetails();

        FloatingActionButton goBack = view.findViewById(R.id.edit_profile_go_back);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new ProfileFragment()).commit();
            }
        });

        profilePicIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseProfilePicture();
            }
        });

        TextView editFullNameButton = view.findViewById(R.id.edit_profile_full_name_tv);
        editFullNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editFullName();
            }
        });

        TextView editPhoneNumberButton = view.findViewById(R.id.edit_profile_phone_tv);
        editPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPhoneNumber();
            }
        });

        return view;
    }

    public void getUserDetails() {
        fullNameTv = view.findViewById(R.id.edit_profile_full_name_tv);
        emailTv = view.findViewById(R.id.edit_profile_email_tv);
        phoneTv = view.findViewById(R.id.edit_profile_phone_tv);
        dobTv = view.findViewById(R.id.edit_profile_dob_tv);
        passwordTv = view.findViewById(R.id.edit_profile_password_tv);

        // Setting Profile Picture
        storageReference.child("users/" + currentUser.getUid() + "/profile_pic")
                .getBytes(1024*1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        profilePicIv.setImageBitmap(bitmap);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        profilePicIv.setImageResource(R.drawable.ic_profile);
                    }
                });


        // Check to see if user exist in the Player collection
        firebaseFirestore.collection("player").document(currentUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Player player = documentSnapshot.toObject(Player.class);
                            userType = "Player";
                            globalPlayer = player;

                            fullNameTv.setText(player.getFirstName() + " " + player.getSurname());
                            emailTv.setText(player.getEmail());
                            phoneTv.setText(player.getPhoneNumber());
                            dobTv.setText(player.getDob());
                            passwordTv.setText(player.getPassword());
                        } else {
                            // Check to see if user exist in the Coach collection
                            firebaseFirestore.collection("coach").document(currentUser.getUid()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                Coach coach = documentSnapshot.toObject(Coach.class);
                                                userType = "Coach";
                                                globalCoach = coach;

                                                fullNameTv.setText(coach.getFirstName() + " " + coach.getSurname());
                                                emailTv.setText(coach.getEmail());
                                                phoneTv.setText(coach.getPhoneNumber());
                                                dobTv.setText(coach.getDob());
                                                passwordTv.setText(coach.getPassword());
                                            }
                                        }
                                    });
                        }
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
            profilePicIv.setImageURI(profileUri);
            uploadPicture();
        }
    }

    public void uploadPicture() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading Image....");
        progressDialog.show();

        StorageReference profilePictureRef = storageReference.child("users/" + currentUser.getUid() + "/profile_pic");

        profilePictureRef.putFile(profileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar.make(view, "Image Uploaded", Snackbar.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to Upload Picture", Toast.LENGTH_SHORT).show();
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

    public void editFullName() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View editFullNameV = layoutInflater.inflate(R.layout.edit_full_name, null);
        AlertDialog.Builder editFullNameAD = new AlertDialog.Builder(getContext());

        EditText editFirstNameEt = editFullNameV.findViewById(R.id.first_name_et);
        TextInputLayout editFirstNameLO = editFullNameV.findViewById(R.id.first_name);
        EditText editSurnameEt = editFullNameV.findViewById(R.id.surname_et);
        TextInputLayout editSurnameLO = editFullNameV.findViewById(R.id.surname);
        Button editFullNameButton = editFullNameV.findViewById(R.id.edit_full_name_button);
        Button goBack = editFullNameV.findViewById(R.id.cancel_full_name_button);

        if (userType.equals("Player")) {
            editFirstNameEt.setText(globalPlayer.getFirstName());
            editSurnameEt.setText(globalPlayer.getSurname());
        } else {
            editFirstNameEt.setText(globalCoach.getFirstName());
            editSurnameEt.setText(globalCoach.getSurname());
        }

        editFullNameAD.setCancelable(false).setView(editFullNameV);
        AlertDialog alert = editFullNameAD.create();
        alert.show();

        editFullNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = editFirstNameEt.getText().toString();
                String surname = editSurnameEt.getText().toString();

                if (firstName.isEmpty() || surname.isEmpty()) {
                    editFirstNameLO.setError("This is required");
                    editSurnameLO.setError("This is required");
                } else {
                    if (userType.equals("Player")) {
                        updateFirestore("player", "firstName", firstName);
                        updateFirestore("player", "surname", surname);
                    } else {
                        updateFirestore("coach", "firstName", firstName);
                        updateFirestore("coach", "surname", surname);
                    }
                    Toast.makeText(getContext(), "Updated Full Name", Toast.LENGTH_SHORT).show();
                    alert.cancel();
                    getUserDetails();
                }
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
    }

    public void editPhoneNumber() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View editPhoneV = layoutInflater.inflate(R.layout.edit_phone_number, null);
        AlertDialog.Builder editPhoneAD = new AlertDialog.Builder(getContext());

        EditText editPhoneEt = editPhoneV.findViewById(R.id.phone_number_et);
        TextInputLayout editPhoneLO = editPhoneV.findViewById(R.id.phone_number);
        Button editPhoneButton = editPhoneV.findViewById(R.id.edit_phone_number_button);
        Button goBack = editPhoneV.findViewById(R.id.cancel_edit_phone_number_button);

        editPhoneAD.setCancelable(false).setView(editPhoneV);
        AlertDialog alert = editPhoneAD.create();
        alert.show();

        if (userType.equals("Player")) {
            editPhoneEt.setText(globalPlayer.getPhoneNumber());
        } else {
            editPhoneEt.setText(globalCoach.getPhoneNumber());
        }

        editPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPhoneNumber = editPhoneEt.getText().toString();

                if (newPhoneNumber.isEmpty()) {
                    editPhoneLO.setError("This is required");
                } else {
                    if (userType.equals("Player")) {
                        updateFirestore("player", "phoneNumber", newPhoneNumber);
                    } else {
                        updateFirestore("coach", "phoneNumber", newPhoneNumber);
                    }
                    Toast.makeText(getContext(), "Updated Phone Number", Toast.LENGTH_SHORT).show();
                    alert.cancel();
                    getUserDetails();
                }
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
    }

    public void updateFirestore(String userType, String dataType, String data) {
        firebaseFirestore.collection(userType).document(currentUser.getUid())
                .update(dataType, data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Successfully updated phone number for " + currentUser.getEmail());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update phone number for " + currentUser.getEmail() + "\n" + e.getMessage());
                    }
                });
    }
}