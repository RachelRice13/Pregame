package com.example.pregame.Team;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.UUID;

public class TeamFragment extends Fragment {
    public static final String TAG_CREATE = "CreateTeam";
    public static final String TAG_TEAM = "TeamInfo";
    public static final String TAG_JOIN = "JoinTeam";
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private ArrayList<Team> teams;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TeamListAdapter adapter;

    public TeamFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_team, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        buildRecyclerView();

        FloatingActionButton createTeamButton = view.findViewById(R.id.create_team_button);
        createTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTeam();
            }
        });

        FloatingActionButton joinTeamButton = view.findViewById(R.id.join_team_button);
        joinTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinTeam();
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
                                            teams.add(team);
                                            adapter.notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG_TEAM, e.toString());
                                        }
                                    });
                        }
                    }
                });
    }

    public void buildRecyclerView() {
        teams = new ArrayList<>();
        recyclerView = view.findViewById(R.id.team_list_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        adapter = new TeamListAdapter(teams, getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        getTeamsInfo();
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
                                Log.d(TAG_CREATE, "Successfully added new team to database");
                                addTeamToCoach(documentReference.getId());
                                Toast.makeText(getContext(), "Successfully created " + teamName, Toast.LENGTH_SHORT).show();
                                alert.cancel();

                                teams.add(newTeam);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG_CREATE, "ERROR: " + e.getMessage());
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
                        Log.d(TAG_CREATE, "Added team to coach array");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG_CREATE, e.toString());
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

                                        // Display alert builder, asking "Is this the team you want to join?"

                                        // Check and see if coach already exists
                                        boolean containsCoach = containsCoach(team);

                                        if (containsCoach) {
                                            Toast.makeText(getContext(), currentUser.getEmail() + " is already a coach for '" + team.getTeamName() + "'", Toast.LENGTH_LONG).show();
                                        } else {
                                            // Add Team Reference to Coach Array of Teams
                                            addTeamToCoach(document.getId());
                                            teams.clear();
                                            getTeamsInfo();

                                            // Add Coach Reference to Team Array of Coaches
                                            addCoachToTeam(document.getId());

                                            Toast.makeText(getContext(), "Successfully added " + currentUser.getEmail() + " to " + team.getTeamName(), Toast.LENGTH_SHORT).show();
                                            alert.cancel();
                                        }

                                        Log.d(TAG_JOIN, document.getId() + " => " + team.getTeamName());
                                    }
                                } else {
                                    Log.e(TAG_JOIN, "Error getting documents: ", task.getException());
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

    public boolean containsCoach(Team team) {
        boolean found = false;
        for (int i = 0; i < team.getCoaches().size(); i++) {
            DocumentReference reference = team.getCoaches().get(i);
            if (reference.getId().equals(currentUser.getUid())) {
                Log.e(TAG_JOIN, "Team contains coach " + currentUser.getEmail());
                found = true;
                break;
            } else {
                Log.e(TAG_JOIN, "Team doesn't contains coach " + currentUser.getEmail());
                found =  false;
            }
        }
        return found;
    }

    public void addCoachToTeam(String teamId) {
        DocumentReference coach = firebaseFirestore.collection("coach").document(currentUser.getUid());

        firebaseFirestore.collection("team").document(teamId).update("coaches", FieldValue.arrayUnion(coach))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG_JOIN, "Added coach to team array");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG_JOIN, e.toString());
                    }
                });
    }

}