package com.example.pregame.Stats;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pregame.CoachHomeActivity;
import com.example.pregame.Model.Team;
import com.example.pregame.PlayerHomeActivity;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class TeamStatsFragment extends Fragment {
    public static final String TAG = "TeamStats";
    private View view;
    private TextView teamNameTv;
    private LinearLayout generalStatsLL, trainingStatsLL, matchStatsLL;
    private Button generalStatsBut, trainingStatsBut, matchStatsBut;
    private FirebaseFirestore firebaseFirestore;

    public TeamStatsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_team_stats, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        teamNameTv = view.findViewById(R.id.team_name);
        generalStatsLL = view.findViewById(R.id.general_stats_ll);
        generalStatsBut = view.findViewById(R.id.general_stats_button);
        trainingStatsLL = view.findViewById(R.id.training_stats_ll);
        trainingStatsBut = view.findViewById(R.id.training_stats_button);
        matchStatsLL = view.findViewById(R.id.match_stats_ll);
        matchStatsBut = view.findViewById(R.id.match_stats_button);

        getTeamDetails();
        choose();

        return view;
    }

    public void getTeamDetails() {
        Team team;

        if (PlayerHomeActivity.userType.equals("Player")) {
            team = PlayerHomeActivity.currentTeam;
        } else {
            team = CoachHomeActivity.currentTeam;
        }

        teamNameTv.setText(team.getTeamName());

        setGeneralTeamStats(team);
    }

    public void setGeneralTeamStats(Team team) {
        TextView numOfPlayersTv = view.findViewById(R.id.num_of_players_tv);
        TextView numOfCoachesTv = view.findViewById(R.id.num_of_coaches_tv);
        TextView numOfTrainingsTv = view.findViewById(R.id.num_of_trainings_tv);
        TextView numOfMatchesTv = view.findViewById(R.id.num_of_matches_tv);

        // Get the number of players and coaches
        int numOfPlayers = team.getPlayers().size();
        int numOfCoaches = team.getCoaches().size();
        numOfPlayersTv.setText(String.valueOf(numOfPlayers));
        numOfCoachesTv.setText(String.valueOf(numOfCoaches));

        // Get the number of trainings and matches
        firebaseFirestore.collection("team").whereEqualTo("teamName", team.getTeamName()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        String teamDoc = queryDocumentSnapshot.getId();

                                        // Get the number of trainings
                                        getCount(teamDoc, "training", numOfTrainingsTv);
                                        // Get the number of matches
                                        getCount(teamDoc, "match", numOfMatchesTv);
                                    }
                                }
                            }
                        });
    }

    public void getCount(String teamDoc, String type, TextView textView) {
        firebaseFirestore.collection("team").document(teamDoc).collection(type).count()
                .get(AggregateSource.SERVER)
                .addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            AggregateQuerySnapshot snapshot = task.getResult();
                            textView.setText(String.valueOf(snapshot.getCount()));
                        } else {
                            Log.e(TAG, "Count Failed: " + task.getException());
                        }
                    }
                });
    }

    public void choose() {
        generalStatsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalStatsLL.setVisibility(View.VISIBLE);
                trainingStatsLL.setVisibility(View.INVISIBLE);
                matchStatsLL.setVisibility(View.INVISIBLE);
            }
        });

        trainingStatsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalStatsLL.setVisibility(View.INVISIBLE);
                trainingStatsLL.setVisibility(View.VISIBLE);
                matchStatsLL.setVisibility(View.INVISIBLE);
            }
        });

        matchStatsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalStatsLL.setVisibility(View.INVISIBLE);
                trainingStatsLL.setVisibility(View.INVISIBLE);
                matchStatsLL.setVisibility(View.VISIBLE);
            }
        });
    }
}