package com.example.pregame.Profile;

import android.content.DialogInterface;
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

import com.example.pregame.HomePage.PlayerHomeActivity;
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

import java.util.ArrayList;

public class SelectTeamFragment extends Fragment {
    public static final String TAG = "SelectTeamFragment";
    private View view;
    private String userType;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;
    private ArrayList<Team> teams;
    private TeamListAdapter adapter;

    public SelectTeamFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_team, container, false);

        setup();
        getUsersTeams();

        return view;
    }

    private void getUsersTeams() {
        if (PlayerHomeActivity.userType.equals("Player")) {
            userType = "player";
        } else {
            userType = "coach";
        }
        buildRecyclerView();
    }

    private void buildRecyclerView() {
        teams = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.my_teams_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new TeamListAdapter(teams, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        populateList();
    }

    private void populateList() {
        firebaseFirestore.collection(userType).document(currentUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User currentCoach = documentSnapshot.toObject(User.class);

                        for (int i = 0; i < currentCoach.getTeams().size(); i++) {
                            DocumentReference reference = currentCoach.getTeams().get(i);
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

    private void joinTeamLayoutInflater() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View joinTeamV = layoutInflater.inflate(R.layout.dialogue_join_new_team, null);
        AlertDialog.Builder joinTeamAD = new AlertDialog.Builder(getContext());

        EditText teamCode1 = joinTeamV.findViewById(R.id.team_code_1);
        EditText teamCode2 = joinTeamV.findViewById(R.id.team_code_2);
        EditText teamCode3 = joinTeamV.findViewById(R.id.team_code_3);
        EditText teamCode4 = joinTeamV.findViewById(R.id.team_code_4);
        EditText teamCode5 = joinTeamV.findViewById(R.id.team_code_5);
        EditText teamCode6 = joinTeamV.findViewById(R.id.team_code_6);
        EditText teamCode7 = joinTeamV.findViewById(R.id.team_code_7);
        EditText teamCode8 = joinTeamV.findViewById(R.id.team_code_8);

        Button joinTeamButton = joinTeamV.findViewById(R.id.join_new_team_button);
        FloatingActionButton goBack = joinTeamV.findViewById(R.id.join_team_go_back);

        joinTeamAD.setCancelable(false).setView(joinTeamV);
        AlertDialog alert = joinTeamAD.create();
        alert.show();

        joinTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teamCode = teamCode1.getText().toString() + teamCode2.getText().toString() + teamCode3.getText().toString() + teamCode4.getText().toString() + teamCode5.getText().toString() + teamCode6.getText().toString() + teamCode7.getText().toString() + teamCode8.getText().toString();

                joinTeam(teamCode, alert);
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
    }

    private void joinTeam(String teamCode, AlertDialog alert) {
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
                            Toast.makeText(getContext(), "That team code doesn't exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void isThisTheTeam(Team team, String id, AlertDialog alert) {
        AlertDialog.Builder isTeamAD = new AlertDialog.Builder(getContext());

        isTeamAD
                .setCancelable(false)
                .setTitle("Are you sure?")
                .setMessage("Is '" + team.getTeamName() + "' the team you want to join?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean containsUser;

                        if (PlayerHomeActivity.userType.equals("Player")) {
                            containsUser = containsUser(team.getPlayers());
                            if (containsUser) {
                                Toast.makeText(getContext(), currentUser.getEmail() + " is already a part of '" + team.getTeamName() + "'", Toast.LENGTH_LONG).show();
                            } else {
                                addToFireStore("team", id, "player", currentUser.getUid(), "teams");
                                teams.clear();
                                buildRecyclerView();
                                addToFireStore("player", currentUser.getUid(), "team", id, "players");
                                alert.cancel();
                            }
                        } else {
                            containsUser = containsUser(team.getCoaches());

                            if (containsUser) {
                                Toast.makeText(getContext(), currentUser.getEmail() + " is already a part of '" + team.getTeamName() + "'", Toast.LENGTH_LONG).show();
                            } else {
                                addToFireStore("team", id, "coach", currentUser.getUid(), "teams");
                                teams.clear();
                                buildRecyclerView();
                                addToFireStore("coach", currentUser.getUid(), "team", id, "coaches");
                                Toast.makeText(getContext(), "Successfully added " + currentUser.getEmail() + " to " + team.getTeamName(), Toast.LENGTH_SHORT).show();
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

    private boolean containsUser(ArrayList<DocumentReference> references) {
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

    private void setup() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        Button button = view.findViewById(R.id.join_team_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinTeamLayoutInflater();
            }
        });

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
            }
        });

    }
}