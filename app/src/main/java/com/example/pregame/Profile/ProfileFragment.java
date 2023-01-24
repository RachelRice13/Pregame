package com.example.pregame.Profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pregame.Model.Coach;
import com.example.pregame.Model.Player;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {
    private View view;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private TextView fullNameTv, emailTv, phoneTv, dobTv, profileSettingsTv;
    private ImageView profilePicIv;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        currentUser = firebaseAuth.getCurrentUser();
        profileSettingsTv = view.findViewById(R.id.profile_settings_tv);
        profilePicIv = view.findViewById(R.id.profile_pic);
        getUserDetails();

        profileSettingsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new EditProfileFragment()).commit();
            }
        });

        return view;
    }

    public void getUserDetails() {
        fullNameTv = view.findViewById(R.id.profile_user_full_name_tv);
        emailTv = view.findViewById(R.id.profile_email_tv);
        phoneTv = view.findViewById(R.id.profile_phone_tv);
        dobTv = view.findViewById(R.id.profile_dob_tv);

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

                            fullNameTv.setText(player.getFirstName() + " " + player.getSurname());
                            emailTv.setText(player.getEmail());
                            phoneTv.setText(player.getPhoneNumber());
                            dobTv.setText(player.getDob());
                        } else {
                            // Check to see if user exist in the Coach collection
                            firebaseFirestore.collection("coach").document(currentUser.getUid()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                Coach coach = documentSnapshot.toObject(Coach.class);

                                                fullNameTv.setText(coach.getFirstName() + " " + coach.getSurname());
                                                emailTv.setText(coach.getEmail());
                                                phoneTv.setText(coach.getPhoneNumber());
                                                dobTv.setText(coach.getDob());
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}