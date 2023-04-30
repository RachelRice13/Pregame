package com.example.pregame.Profile;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.example.pregame.Common.Validation;
import com.example.pregame.HomePage.PlayerHomeActivity;
import com.example.pregame.Model.User;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditProfileFragment extends Fragment {
    public static final String TAG = "EditProfile";
    private View view;
    private User user;
    private String userType;
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private TextView fullNameTv, phoneTv, passwordTv, emailTv;
    private ImageView profilePicIv;
    private Uri profileUri;

    public EditProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        setup();
        getUserDetails();

        return view;
    }

    private void getUserDetails() {
        TextView dobTv = view.findViewById(R.id.edit_profile_dob_tv);

        getProfilePicture();

        fullNameTv.setText(user.getFirstName() + " " + user.getSurname());
        emailTv.setText(user.getEmail());
        phoneTv.setText(user.getPhoneNumber());
        passwordTv.setText(user.getPassword());
        dobTv.setText(user.getDob());
    }

    private void getProfilePicture() {
        storageReference.child("users/" + currentUser.getUid() + "/profile_pic")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri.toString()).fit().centerCrop().into(profilePicIv);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        profilePicIv.setImageResource(R.drawable.ic_profile);
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

    private void uploadPicture() {
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

    private void editFullName() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View editFullNameV = layoutInflater.inflate(R.layout.dialogue_edit_full_name, null);
        AlertDialog.Builder editFullNameAD = new AlertDialog.Builder(getContext());

        EditText editFirstNameEt = editFullNameV.findViewById(R.id.first_name_et);
        TextInputLayout editFirstNameLO = editFullNameV.findViewById(R.id.first_name);
        EditText editSurnameEt = editFullNameV.findViewById(R.id.surname_et);
        TextInputLayout editSurnameLO = editFullNameV.findViewById(R.id.surname);
        Button editFullNameButton = editFullNameV.findViewById(R.id.edit_full_name_button);
        Button goBack = editFullNameV.findViewById(R.id.cancel_full_name_button);

        editFirstNameEt.setText(user.getFirstName());
        editSurnameEt.setText(user.getSurname());

        editFullNameAD.setCancelable(false).setView(editFullNameV);
        AlertDialog alert = editFullNameAD.create();
        alert.show();

        editFullNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = editFirstNameEt.getText().toString();
                String surname = editSurnameEt.getText().toString();

                boolean validFirst = Validation.validateBlank(firstName, editFirstNameLO);
                boolean validSurname = Validation.validateBlank(surname, editSurnameLO);

                if (validFirst && validSurname) {
                    updateFirestore( "firstName", firstName);
                    updateFirestore( "surname", surname);
                    alert.cancel();
                    Snackbar.make(view, "Updated Full Name", Snackbar.LENGTH_SHORT).show();
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

    private void editPhoneNumber() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View editPhoneV = layoutInflater.inflate(R.layout.dialogue_edit_phone_number, null);
        AlertDialog.Builder editPhoneAD = new AlertDialog.Builder(getContext());

        EditText editPhoneEt = editPhoneV.findViewById(R.id.phone_number_et);
        TextInputLayout editPhoneLO = editPhoneV.findViewById(R.id.phone_number);
        Button editPhoneButton = editPhoneV.findViewById(R.id.edit_phone_number_button);
        Button goBack = editPhoneV.findViewById(R.id.cancel_edit_phone_number_button);

        editPhoneAD.setCancelable(false).setView(editPhoneV);
        AlertDialog alert = editPhoneAD.create();
        alert.show();

        editPhoneEt.setText(user.getPhoneNumber());

        editPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPhoneNumber = editPhoneEt.getText().toString();

                boolean validPhone = Validation.validatePhone(newPhoneNumber, editPhoneLO);

                if (validPhone) {
                    updateFirestore( "phoneNumber", newPhoneNumber);
                    alert.cancel();
                    getUserDetails();
                    Snackbar.make(view, "Updated Phone Number", Snackbar.LENGTH_SHORT).show();
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

    private void editPassword() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View editPhoneV = layoutInflater.inflate(R.layout.dialogue_edit_phone_number, null);
        AlertDialog.Builder editPhoneAD = new AlertDialog.Builder(getContext());

        EditText editPasswordEt = editPhoneV.findViewById(R.id.phone_number_et);
        TextInputLayout editPasswordLO = editPhoneV.findViewById(R.id.phone_number);
        Button editPasswordButton = editPhoneV.findViewById(R.id.edit_phone_number_button);
        Button goBack = editPhoneV.findViewById(R.id.cancel_edit_phone_number_button);
        TextView editPasswordTitleTv = editPhoneV.findViewById(R.id.edit_detail_details);

        editPasswordEt.setText(user.getPassword());
        editPasswordTitleTv.setText("Edit Password");
        editPasswordLO.setHint("Password");
        editPasswordLO.setStartIconDrawable(R.drawable.ic_password);

        editPhoneAD.setCancelable(false).setView(editPhoneV);
        AlertDialog alert = editPhoneAD.create();
        alert.show();

        editPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = editPasswordEt.getText().toString();
                boolean validPassword = Validation.validatePassword(password, editPasswordLO);

                if (validPassword) {
                    currentUser.updatePassword(password)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        updateFirestore("password", password);
                                        alert.cancel();
                                        getUserDetails();
                                        Snackbar.make(view, "Successfully updated password", Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        Log.e(TAG, "Failed to updated password");
                                    }
                                }
                            });
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

    private void editEmail() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View editPhoneV = layoutInflater.inflate(R.layout.dialogue_edit_phone_number, null);
        AlertDialog.Builder editPhoneAD = new AlertDialog.Builder(getContext());

        EditText editEmailEt = editPhoneV.findViewById(R.id.phone_number_et);
        TextInputLayout editEmailLO = editPhoneV.findViewById(R.id.phone_number);
        Button editEmailButton = editPhoneV.findViewById(R.id.edit_phone_number_button);
        Button goBack = editPhoneV.findViewById(R.id.cancel_edit_phone_number_button);
        TextView editEmailTitleTv = editPhoneV.findViewById(R.id.edit_detail_details);

        editEmailEt.setText(user.getEmail());
        editEmailTitleTv.setText("Edit Email");
        editEmailLO.setHint("Email");
        editEmailLO.setStartIconDrawable(R.drawable.ic_email);

        editPhoneAD.setCancelable(false).setView(editPhoneV);
        AlertDialog alert = editPhoneAD.create();
        alert.show();

        editEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmailEt.getText().toString();

                boolean validEmail = Validation.validateBlank(email, editEmailLO);

                if (validEmail) {
                    currentUser.updateEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        updateFirestore("email", email);
                                        alert.cancel();
                                        getUserDetails();
                                        Snackbar.make(view, "Successfully updated email", Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        Log.e(TAG, "Failed to updated password");
                                    }
                                }
                            });
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

    private void updateFirestore(String dataType, String data) {
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

    private void setup() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        currentUser = firebaseAuth.getCurrentUser();
        profilePicIv = view.findViewById(R.id.edit_profile_pic);

        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("user");
        userType = bundle.getString("userType");

        FloatingActionButton goBack = view.findViewById(R.id.edit_profile_go_back);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new ProfileFragment()).commit();
            }
        });

        fullNameTv = view.findViewById(R.id.edit_profile_full_name_tv);
        fullNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editFullName();
            }
        });

        phoneTv = view.findViewById(R.id.edit_profile_phone_tv);
        phoneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPhoneNumber();
            }
        });

        passwordTv = view.findViewById(R.id.edit_profile_password_tv);
        passwordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPassword();
            }
        });


        emailTv = view.findViewById(R.id.edit_profile_email_tv);
        emailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEmail();
            }
        });

        profilePicIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseProfilePicture();
            }
        });
    }
}