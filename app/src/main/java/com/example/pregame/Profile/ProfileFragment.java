package com.example.pregame.Profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pregame.HomePage.CoachHomeActivity;
import com.example.pregame.InjuryReport.ViewInjuryFragment;
import com.example.pregame.Model.Team;
import com.example.pregame.Model.User;
import com.example.pregame.R;
import com.example.pregame.Team.TeamListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    public static String userType = "";
    public static User globalPlayer = new User();
    public static User globalCoach = new User();
    public static final String TAG = "Profile";
    private View view;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private TextView fullNameTv;
    private TextView emailTv;
    private TextView phoneTv;
    private TextView dobTv;
    private ImageView profilePicIv;
    private ArrayList<Team> teams;
    private TeamListAdapter adapter;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        currentUser = firebaseAuth.getCurrentUser();
        TextView profileSettingsTv = view.findViewById(R.id.profile_settings_tv);
        TextView injuryReportTv = view.findViewById(R.id.profile_injury_tv);
        profilePicIv = view.findViewById(R.id.profile_pic);
        getUserDetails();

        Button myTeamsButton = view.findViewById(R.id.view_my_teams);
        myTeamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewMyTeams();
            }
        });

        profileSettingsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new EditProfileFragment()).commit();
            }
        });

        if (CoachHomeActivity.userType.equals("Coach")) {
            injuryReportTv.setVisibility(View.GONE);
        }

        injuryReportTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new ViewInjuryFragment()).commit();
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
                            User player = documentSnapshot.toObject(User.class);
                            userType = "Player";
                            globalPlayer = player;

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
                                                User coach = documentSnapshot.toObject(User.class);
                                                userType = "Coach";
                                                globalCoach = coach;

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

    public void viewMyTeams() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View viewMyTeamsV = layoutInflater.inflate(R.layout.view_teams, null);
        AlertDialog.Builder viewMyTeamsAD = new AlertDialog.Builder(getContext());

        Button joinTeamButton = viewMyTeamsV.findViewById(R.id.join_team_button);
        Button goBack = viewMyTeamsV.findViewById(R.id.cancel_button);

        teams = new ArrayList<>();
        RecyclerView recyclerView = viewMyTeamsV.findViewById(R.id.my_teams_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(viewMyTeamsV.getContext());
        adapter = new TeamListAdapter(teams, getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if (userType.equals("Player")) {
            getPlayerTeamsInfo();
        } else {
            getCoachTeamsInfo();
        }

        viewMyTeamsAD.setCancelable(false).setView(viewMyTeamsV);
        AlertDialog alert = viewMyTeamsAD.create();
        alert.show();

        joinTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinTeam();
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
    }

    public void getCoachTeamsInfo() {
        firebaseFirestore.collection("coach").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User currentCoach = documentSnapshot.toObject(User.class);

                        // Search through the Coach's array of teams
                        for (int i = 0; i < currentCoach.getTeams().size(); i++) {
                            DocumentReference reference = currentCoach.getTeams().get(i);
                            // Get each teams info
                            reference.get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Team team = documentSnapshot.toObject(Team.class);
                                            teams.add(team);
                                            adapter.notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                    });
                        }
                    }
                });
    }

    public void getPlayerTeamsInfo() {
        firebaseFirestore.collection("player").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User currentPlayer = documentSnapshot.toObject(User.class);

                        // Search through the Coach's array of teams
                        for (int i = 0; i < currentPlayer.getTeams().size(); i++) {
                            DocumentReference reference = currentPlayer.getTeams().get(i);
                            // Get each teams info
                            reference.get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Team team = documentSnapshot.toObject(Team.class);
                                            teams.add(team);
                                            adapter.notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                    });
                        }
                    }
                });
    }

    public void joinTeam() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View joinTeamV = layoutInflater.inflate(R.layout.join_new_team, null);
        AlertDialog.Builder joinTeamAD = new AlertDialog.Builder(getContext());

        EditText teamCode1 = joinTeamV.findViewById(R.id.team_code_1);
        EditText teamCode2 = joinTeamV.findViewById(R.id.team_code_2);
        EditText teamCode3 = joinTeamV.findViewById(R.id.team_code_3);
        EditText teamCode4 = joinTeamV.findViewById(R.id.team_code_4);
        EditText teamCode5 = joinTeamV.findViewById(R.id.team_code_5);
        EditText teamCode6 = joinTeamV.findViewById(R.id.team_code_6);
        EditText teamCode7 = joinTeamV.findViewById(R.id.team_code_7);
        EditText teamCode8 = joinTeamV.findViewById(R.id.team_code_8);

        Button joinTeam = joinTeamV.findViewById(R.id.join_new_team_button);
        FloatingActionButton goBack = joinTeamV.findViewById(R.id.join_team_go_back);

        joinTeamAD.setCancelable(false).setView(joinTeamV);
        AlertDialog alert = joinTeamAD.create();
        alert.show();

        joinTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teamCode = teamCode1.getText().toString() + teamCode2.getText().toString() + teamCode3.getText().toString() + teamCode4.getText().toString() + teamCode5.getText().toString() + teamCode6.getText().toString() + teamCode7.getText().toString() + teamCode8.getText().toString();

                // Search through collection
                firebaseFirestore.collection("team").whereEqualTo("teamCode", teamCode)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Team team = document.toObject(Team.class);
                                        boolean containsUser;

                                        // Display alert builder, asking "Is this the team you want to join?"

                                        // Check and see if user already exists
                                        if (userType.equals("Player")) {
                                            containsUser = containsUser(team.getPlayers());

                                            if (containsUser) {
                                                Toast.makeText(getContext(), currentUser.getEmail() + " is already a part of '" + team.getTeamName() + "'", Toast.LENGTH_LONG).show();
                                            } else {
                                                addTeamToUser(document.getId(), "player");
                                                teams.clear();
                                                getPlayerTeamsInfo();

                                                addUserToTeam(document.getId(), "player", "players");
                                                alert.cancel();
                                            }

                                        } else {
                                            containsUser = containsUser(team.getCoaches());

                                            if (containsUser) {
                                                Toast.makeText(getContext(), currentUser.getEmail() + " is already a part of '" + team.getTeamName() + "'", Toast.LENGTH_LONG).show();
                                            } else {
                                                addTeamToUser(document.getId(), "coach");
                                                teams.clear();
                                                getCoachTeamsInfo();

                                                addUserToTeam(document.getId(), "coach", "coaches");
                                                Toast.makeText(getContext(), "Successfully added " + currentUser.getEmail() + " to " + team.getTeamName(), Toast.LENGTH_SHORT).show();
                                                alert.cancel();
                                            }
                                        }
                                        Toast.makeText(getContext(), "Successfully added " + currentUser.getEmail() + " to " + team.getTeamName(), Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Log.e(TAG, "Error getting documents: ", task.getException());
                                    Toast.makeText(getContext(), "That team code doesn't exist", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
    }

    public boolean containsUser(ArrayList<DocumentReference> references) {
        for (int i = 0; i < references.size(); i++) {
            DocumentReference reference = references.get(i);
            if (reference.getId().equals(currentUser.getUid())) {
                Log.e(TAG, "Team contains coach " + currentUser.getEmail());
                return true;
            } else {
                Log.e(TAG, "Team doesn't contains coach " + currentUser.getEmail());
                return false;
            }
        }
        return false;
    }

    public void addTeamToUser(String id, String referenceType) {
        DocumentReference team = firebaseFirestore.collection("team").document(id);
        firebaseFirestore.collection(referenceType).document(currentUser.getUid()).update("teams", FieldValue.arrayUnion(team))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Added team to user array");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.toString());
                    }
                });
    }

    public void addUserToTeam(String teamId, String referenceType, String array) {
        DocumentReference coach = firebaseFirestore.collection(referenceType).document(currentUser.getUid());

        firebaseFirestore.collection("team").document(teamId).update(array, FieldValue.arrayUnion(coach))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Added user to team array");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.toString());
                    }
                });
    }

}