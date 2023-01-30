package com.example.pregame.Stats;

import android.graphics.Color;
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

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class TeamStatsFragment extends Fragment {
    public static final String TAG = "TeamStats";
    private View view;
    private TextView teamNameTv, winsLegendTv, loseLegendTv, drawsLegendTv, notPlayedLegendTv;
    private LinearLayout generalStatsLL, trainingStatsLL, matchStatsLL;
    private Button generalStatsBut, trainingStatsBut, matchStatsBut;
    private PieChart matchResultsPieChart;
    private FirebaseFirestore firebaseFirestore;
    private int totalPlayed, totalWins;

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
        matchResultsPieChart = view.findViewById(R.id.match_results_pie_chart);
        winsLegendTv = view.findViewById(R.id.wins_legend_tv);
        loseLegendTv = view.findViewById(R.id.loses_legend_tv);
        drawsLegendTv = view.findViewById(R.id.draws_legend_tv);
        notPlayedLegendTv = view.findViewById(R.id.not_played_legend_tv);

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
        setMatchStats(team);
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

    public void setMatchStats(Team team) {
        firebaseFirestore.collection("team").whereEqualTo("teamName", team.getTeamName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                String teamDoc = queryDocumentSnapshot.getId();

                                // Get number of wins
                                getCountWhere(teamDoc, "Won", "Wins", "#00FF00", winsLegendTv);

                                // Get number of loses
                                getCountWhere(teamDoc, "Lose", "Loses", "#FF0000", loseLegendTv);

                                // Get number of draws
                                getCountWhere(teamDoc, "Draw", "Draws", "#FFFF00", drawsLegendTv);

                                // Get number of haven't played
                                getCountWhere(teamDoc, "Haven't Played", "Haven't Played", "#0000FF", notPlayedLegendTv);

                                matchResultsPieChart.startAnimation();
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

    public void getCountWhere(String teamDoc, String statusType, String status, String colour, TextView textView) {
        firebaseFirestore.collection("team").document(teamDoc).collection("match").whereEqualTo("status", statusType).count()
                .get(AggregateSource.SERVER)
                .addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            AggregateQuerySnapshot snapshot = task.getResult();
                            int count = (int) snapshot.getCount();
                            double winPercentage;

                            // Add pie slice to pie chart
                            matchResultsPieChart.addPieSlice(
                                    new PieModel(status, count, Color.parseColor(colour))
                            );

                            // Update Legend
                            textView.setText(count + " " + status);

                            // Calculate Win %
                            if (!status.equals("Haven't Played")) {
                                totalPlayed += count;
                            }

                            if (status.equals("Wins")) {
                                totalWins = count;
                            }

                            if (totalPlayed == 0) {
                                winPercentage = 0;
                            } else {
                                winPercentage = (totalWins*100)/totalPlayed;
                            }

                            matchResultsPieChart.setUseInnerValue(true);
                            matchResultsPieChart.setInnerValueSize(70);
                            matchResultsPieChart.setInnerValueString(winPercentage + "%");
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