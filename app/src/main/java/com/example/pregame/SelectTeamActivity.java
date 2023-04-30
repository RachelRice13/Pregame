package com.example.pregame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pregame.Common.CommonActivity;
import com.example.pregame.HomePage.CoachHomeActivity;
import com.example.pregame.HomePage.PlayerHomeActivity;
import com.example.pregame.Model.Team;
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

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class SelectTeamActivity extends CommonActivity {
    private static final String TAG = "SelectTeam";
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;
    private ArrayList<Team> teams;
    private TeamListAdapter adapter;
    private Button createTeamButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_team);

        setup();

        hideActionBar();
    }

    private void getUserDetails() {
        firebaseFirestore.collection("player").document(currentUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            getUserData("player");
                            PlayerHomeActivity.userType = "Player";
                            createTeamButton.setVisibility(View.INVISIBLE);
                        } else {
                            firebaseFirestore.collection("coach").document(currentUser.getUid()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                getUserData("coach");
                                                CoachHomeActivity.userType = "Coach";
                                            } else {
                                                Toast.makeText(SelectTeamActivity.this, "Error Logging In", Toast.LENGTH_SHORT).show();
                                                Log.e(TAG, "User doesn't exist in either the Player or Coach collections");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void getUserData(String userType) {
        firebaseFirestore.collection(userType).document(currentUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> data = documentSnapshot.getData();
                            ArrayList<DocumentReference> teams = (ArrayList<DocumentReference>) data.get("teams");
                            displayTeams(teams);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "User doesn't exist.\n" + e.getMessage());
                    }
                });
    }

    private void displayTeams(ArrayList<DocumentReference> teamsReference) {
        for (int i = 0; i < teamsReference.size(); i++) {
            DocumentReference reference = teamsReference.get(i);

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

    private void buildRecyclerView() {
        teams = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.team_list_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new TeamListAdapter(teams, this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void createTeam() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View createTeamV = layoutInflater.inflate(R.layout.dialogue_create_new_team, null);
        AlertDialog.Builder createTeamAD = new AlertDialog.Builder(this);
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
                                addToFireStore("team", documentReference.getId(), "coach", currentUser.getUid(), "teams");
//                                Toast.makeText(SelectTeamActivity.this, "Successfully created " + teamName, Toast.LENGTH_SHORT).show();
                                alert.cancel();

                                teams.add(newTeam);
                                adapter.notifyDataSetChanged();
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

    private void joinTeam() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View joinTeamV = layoutInflater.inflate(R.layout.dialogue_join_new_team, null);
        AlertDialog.Builder joinTeamAD = new AlertDialog.Builder(this);

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
                                        isThisTheTeam(document.toObject(Team.class), document.getId(), alert);
                                    }
                                } else {
                                    Log.e(TAG, "Error getting documents: ", task.getException());
                                    Toast.makeText(SelectTeamActivity.this, "That team code doesn't exist", Toast.LENGTH_SHORT).show();
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

    private void isThisTheTeam(Team team, String id, AlertDialog alert) {
        AlertDialog.Builder isTeamAD = new AlertDialog.Builder(SelectTeamActivity.this);

        isTeamAD
                .setCancelable(false)
                .setTitle("Are you sure?")
                .setMessage("Is '" + team.getTeamName() + "' the team you want to join?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean containsUser;

                        // Check and see if user already exists
                        if (PlayerHomeActivity.userType.equals("Player")) {
                            containsUser = containsUser(team.getPlayers());
                            if (containsUser) {
                                Toast.makeText(SelectTeamActivity.this, currentUser.getEmail() + " is already a part of '" + team.getTeamName() + "'", Toast.LENGTH_LONG).show();
                            } else {
                                addToFireStore("team", id, "player", currentUser.getUid(), "teams");
                                teams.clear();
                                getUserData("player");

                                addToFireStore("player", currentUser.getUid(), "team", id, "players");
                                alert.cancel();
                            }

                        } else {
                            containsUser = containsUser(team.getCoaches());

                            if (containsUser) {
                                Toast.makeText(SelectTeamActivity.this, currentUser.getEmail() + " is already a part of '" + team.getTeamName() + "'", Toast.LENGTH_LONG).show();
                            } else {
                                addToFireStore("team", id, "coach", currentUser.getUid(), "teams");
                                teams.clear();
                                getUserData("coach");

                                addToFireStore("coach", currentUser.getUid(), "team", id, "coaches");
                                Toast.makeText(SelectTeamActivity.this, "Successfully added " + currentUser.getEmail() + " to " + team.getTeamName(), Toast.LENGTH_SHORT).show();
                                alert.cancel();
                            }
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = isTeamAD.create();
        alertDialog.show();
    }

    private void addToFireStore(String refType, String refId, String collectionType, String collectionId, String arrayName) {
        DocumentReference reference = firebaseFirestore.collection(refType).document(refId);

        firebaseFirestore.collection(collectionType).document(collectionId).update(arrayName, FieldValue.arrayUnion(reference))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Added data to db");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.toString());
                    }
                });
    }

    private boolean containsUser(ArrayList<DocumentReference> references) {
        for (int i = 0; i < references.size(); i++) {
            DocumentReference reference = references.get(i);
            if (reference.getId().equals(currentUser.getUid())) {
                Log.e(TAG, "Team contains coach " + currentUser.getEmail());
                return true;
            }
        }
        return false;
    }

    private void setup() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        buildRecyclerView();
        getUserDetails();

        Button joinTeamButton = findViewById(R.id.join_team_button);
        joinTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinTeam();
            }
        });

        createTeamButton = findViewById(R.id.create_team_button);
        createTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTeam();
            }
        });
    }
}