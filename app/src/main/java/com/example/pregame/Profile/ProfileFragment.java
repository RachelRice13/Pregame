package com.example.pregame.Profile;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pregame.HomePage.CoachHomeActivity;
import com.example.pregame.InjuryReport.ViewInjuryFragment;
import com.example.pregame.Model.User;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    private View view;
    private User user;
    private String userType;
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private TextView fullNameTv, emailTv, phoneTv, dobTv, passwordTv;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_profile, container, false);

        setup();
        getUserDetails();

        return view;
    }

    private void getUserDetails() {
        fullNameTv = view.findViewById(R.id.profile_user_full_name_tv);
        emailTv = view.findViewById(R.id.profile_email_tv);
        phoneTv = view.findViewById(R.id.profile_phone_tv);
        dobTv = view.findViewById(R.id.profile_dob_tv);
        passwordTv = view.findViewById(R.id.profile_password_tv);

        getProfilePicture();

        firebaseFirestore.collection("player").document(currentUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            User player = documentSnapshot.toObject(User.class);
                            setDetails(player);
                            user = player;
                            userType = "player";
                        } else {
                            firebaseFirestore.collection("coach").document(currentUser.getUid()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                User coach = documentSnapshot.toObject(User.class);
                                                setDetails(coach);
                                                user = coach;
                                                userType = "coach";
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void setDetails(User user) {
        fullNameTv.setText(user.getFirstName() + " " + user.getSurname());
        emailTv.setText(user.getEmail());
        phoneTv.setText(user.getPhoneNumber());
        dobTv.setText(user.getDob());
        passwordTv.setText(user.getPassword());
    }

    private void getProfilePicture() {
        ImageView profilePicIv = view.findViewById(R.id.profile_pic);

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

    private void setup() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        currentUser = firebaseAuth.getCurrentUser();
        TextView profileSettingsTv = view.findViewById(R.id.profile_settings_tv);
        TextView injuryReportTv = view.findViewById(R.id.profile_injury_tv);
        TextView purchaseHistoryTv = view.findViewById(R.id.profile_purchase_history_tv);

        if (CoachHomeActivity.userType.equals("Coach")) {
            injuryReportTv.setVisibility(View.GONE);
            purchaseHistoryTv.setVisibility(View.GONE);
        }

        Button myTeamsButton = view.findViewById(R.id.view_my_teams);
        myTeamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, new SelectTeamFragment()).commit();
            }
        });

        profileSettingsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                bundle.getString("userType", userType);
                editProfileFragment.setArguments(bundle);
                transaction.replace(R.id.container, editProfileFragment).commit();
            }
        });


        injuryReportTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new ViewInjuryFragment()).commit();
            }
        });

        purchaseHistoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, new PurchaseHistoryFragment()).commit();
            }
        });
    }

}