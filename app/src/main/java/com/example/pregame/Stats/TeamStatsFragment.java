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
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.pregame.CoachHomeActivity;
import com.example.pregame.Model.MatchStats;
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

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;

public class TeamStatsFragment extends Fragment {
    public static final String TAG = "TeamStats";
    private View view;
    private TextView teamNameTv, winsLegendTv, loseLegendTv, drawsLegendTv, notPlayedLegendTv, averagePointsScoredTv, averagePointsAgainstTv;
    private LinearLayout generalStatsLL, trainingStatsLL;
    private ScrollView matchStatsSV;
    private Button generalStatsBut, trainingStatsBut, matchStatsBut;
    private PieChart matchResultsPieChart;
    private BarChart totalMatchStatsBarChart;
    private FirebaseFirestore firebaseFirestore;
    private int totalPlayed, totalWins, totalPointsScored, totalPointsAgainst, totalMatchesWithStats, totalAssist, totalOffReb, totalDefReb, totalBlocks, totalFouls, totalSteals, totalTurnovers;
    private double averagePointsScore, averagePointsAgainst;

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
        matchStatsSV = view.findViewById(R.id.match_stats_sv);
        matchStatsBut = view.findViewById(R.id.match_stats_button);
        matchResultsPieChart = view.findViewById(R.id.match_results_pie_chart);
        winsLegendTv = view.findViewById(R.id.wins_legend_tv);
        loseLegendTv = view.findViewById(R.id.loses_legend_tv);
        drawsLegendTv = view.findViewById(R.id.draws_legend_tv);
        notPlayedLegendTv = view.findViewById(R.id.not_played_legend_tv);
        averagePointsScoredTv = view.findViewById(R.id.average_points_scored_tv);
        averagePointsAgainstTv = view.findViewById(R.id.average_points_against_tv);
        totalMatchStatsBarChart = view.findViewById(R.id.total_match_stats_bar_chart);

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

    public void setMatchStats(Team team) {
        firebaseFirestore.collection("team").whereEqualTo("teamName", team.getTeamName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                String teamDoc = queryDocumentSnapshot.getId();

                                setMatchResultsPieChart(teamDoc, "Won", "Wins", "#00FF00", winsLegendTv);
                                setMatchResultsPieChart(teamDoc, "Lose", "Loses", "#FF0000", loseLegendTv);
                                setMatchResultsPieChart(teamDoc, "Draw", "Draws", "#FFFF00", drawsLegendTv);
                                setMatchResultsPieChart(teamDoc, "Haven't Played", "Haven't Played", "#0000FF", notPlayedLegendTv);
                                matchResultsPieChart.startAnimation();

                                calculateMatchPoints(teamDoc);
                                setTotalMatchStatBarChart(teamDoc);
                            }
                        }
                    }
                });
    }

    public void setMatchResultsPieChart(String teamDoc, String statusType, String status, String colour, TextView textView) {
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

    public void calculateMatchPoints(String teamDoc) {
        firebaseFirestore.collection("team").document(teamDoc).collection("matchStats").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                               MatchStats matchStats = document.toObject(MatchStats.class);
                               totalPointsScored += matchStats.getMyTeamScore();
                               totalPointsAgainst += matchStats.getOpponentScore();
                               totalMatchesWithStats += 1;
                            }

                            if (totalMatchesWithStats != 0) {
                                averagePointsScore = totalPointsScored/totalMatchesWithStats;
                                averagePointsAgainst = totalPointsAgainst/totalMatchesWithStats;
                                averagePointsScoredTv.setText(String.valueOf(averagePointsScore));
                                averagePointsAgainstTv.setText(String.valueOf(averagePointsAgainst));
                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void setTotalMatchStatBarChart(String teamDoc) {
        firebaseFirestore.collection("team").document(teamDoc).collection("matchStats").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MatchStats matchStats = document.toObject(MatchStats.class);

                                totalPointsScored += matchStats.getMyTeamScore();
                                totalPointsAgainst += matchStats.getOpponentScore();
                                totalAssist += matchStats.getAssists();
                                totalOffReb += matchStats.getOffensiveRebounds();
                                totalDefReb += matchStats.getDefensiveRebounds();
                                totalBlocks += matchStats.getBlocks();
                                totalFouls += matchStats.getFouls();
                                totalSteals += matchStats.getSteals();
                                totalTurnovers += matchStats.getTurnovers();
                            }
//                            totalMatchStatsBarChart.addBar(new BarModel("P", totalPointsScored, Color.parseColor("#ff00ff")));
//                            totalMatchStatsBarChart.addBar(new BarModel("P/A", totalPointsAgainst, Color.parseColor("#800080")));
                            totalMatchStatsBarChart.addBar(new BarModel("Asst", totalAssist, Color.parseColor("#ff0000")));
                            totalMatchStatsBarChart.addBar(new BarModel("O/R", totalOffReb, Color.parseColor("#ffa500")));
                            totalMatchStatsBarChart.addBar(new BarModel("D/R", totalDefReb, Color.parseColor("#ffff00")));
                            totalMatchStatsBarChart.addBar(new BarModel("Blk", totalBlocks, Color.parseColor("#00ff00")));
                            totalMatchStatsBarChart.addBar(new BarModel("F", totalFouls, Color.parseColor("#008000")));
                            totalMatchStatsBarChart.addBar(new BarModel("Stl", totalSteals, Color.parseColor("#00ffff")));
                            totalMatchStatsBarChart.addBar(new BarModel("To", totalTurnovers, Color.parseColor("#0000ff")));

                            totalMatchStatsBarChart.startAnimation();

                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
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
                matchStatsSV.setVisibility(View.INVISIBLE);
            }
        });

        trainingStatsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalStatsLL.setVisibility(View.INVISIBLE);
                trainingStatsLL.setVisibility(View.VISIBLE);
                matchStatsSV.setVisibility(View.INVISIBLE);
            }
        });

        matchStatsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalStatsLL.setVisibility(View.INVISIBLE);
                trainingStatsLL.setVisibility(View.INVISIBLE);
                matchStatsSV.setVisibility(View.VISIBLE);
            }
        });
    }
}