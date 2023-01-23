package com.example.pregame.Team;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pregame.Model.Coach;
import com.example.pregame.Model.Team;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.UUID;

public class TeamFragment extends Fragment {
    public static final String TAG = "CreateTeam";
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    public TeamFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_team, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        getTeamsInfo();

        FloatingActionButton createTeam = view.findViewById(R.id.create_team_button);
        createTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTeam();
            }
        });

        FloatingActionButton joinTeam = view.findViewById(R.id.join_team_button);
        joinTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }

    public void getTeamsInfo() {
        firebaseFirestore.collection("coach").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Coach currentCoach = documentSnapshot.toObject(Coach.class);

                        // Search through the Coach's array of teams
                        for (int i = 0; i < currentCoach.getTeams().size(); i++) {
                            DocumentReference reference = currentCoach.getTeams().get(i);

                            // Get each teams info
                            reference.get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Team team = documentSnapshot.toObject(Team.class);

                                            // Add team details to RV
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

    public void createTeam() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View createTeamV = layoutInflater.inflate(R.layout.create_new_team, null);
        AlertDialog.Builder createTeamAD = new AlertDialog.Builder(getContext());
        EditText teamNameEt = createTeamV.findViewById(R.id.team_name_et);
        Button createTeam = createTeamV.findViewById(R.id.create_new_team_button);
        Button cancelTeam = createTeamV.findViewById(R.id.cancel_new_team_button);

        createTeamAD.setCancelable(false).setView(createTeamV);
        AlertDialog alert = createTeamAD.create();
        alert.show();

        createTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teamName = teamNameEt.getText().toString();
                String teamCode = UUID.randomUUID().toString().replace("-","").substring(0,8);
                ArrayList<DocumentReference> coaches = new ArrayList<>();

                DocumentReference currentCoach = firebaseFirestore.collection("coach").document(currentUser.getUid());
                coaches.add(currentCoach);
                Team newTeam = new Team(teamName, teamCode, new ArrayList<>(), coaches);

                firebaseFirestore.collection("team")
                        .add(newTeam)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "Successfully added new team to database");
                                addTeamToCoach(documentReference.getId());
                                Toast.makeText(getContext(), "Successfully created " + teamName, Toast.LENGTH_SHORT).show();
                                alert.cancel();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "ERROR: " + e.getMessage());
                            }
                        });
            }
        });

        cancelTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
    }

    public void addTeamToCoach(String id) {
        DocumentReference team = firebaseFirestore.collection("team").document(id);
        firebaseFirestore.collection("coach").document(currentUser.getUid()).update("teams", FieldValue.arrayUnion(team))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Added team to coach array");
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