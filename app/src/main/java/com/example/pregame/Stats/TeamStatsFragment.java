package com.example.pregame.Stats;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pregame.HomePage.CoachHomeActivity;
import com.example.pregame.Model.Attendance;
import com.example.pregame.Model.MatchTraining;
import com.example.pregame.Model.Team;
import com.example.pregame.HomePage.PlayerHomeActivity;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;

public class TeamStatsFragment extends Fragment {
    public static final String TAG = "TeamStats";
    private View view;
    private TextView teamNameTv;
    private FragmentTransaction transaction;
    private FirebaseFirestore firebaseFirestore;
    private int numOfTrainings, numOfMatches, trainingsRemaining, trainingsCompleted, trainingsCancelled, matchWon, matchLost, matchDraw, matchCancelled, matchRemaining, matchesPlayed;
    private double totalYesCount, totalNoCount, totalHasNotRespondedCount, totalResponsesCount;
    private String teamDoc;
    private Team team;

    public TeamStatsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_team_stats, container, false);

        setup();

        return view;
    }

    private void getTeamDetails() {
        if (PlayerHomeActivity.userType.equals("Player")) {
            team = PlayerHomeActivity.currentTeam;
        } else {
            team = CoachHomeActivity.currentTeam;
        }
        teamNameTv.setText(team.getTeamName());

        firebaseFirestore.collection("team").whereEqualTo("teamName", team.getTeamName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                teamDoc = queryDocumentSnapshot.getId();
                                setGeneralTeamStats(team);
                            }
                        }
                    }
                });
        }

    private void setGeneralTeamStats(Team team) {
        TextView numOfPlayersTv = view.findViewById(R.id.num_of_players_tv);
        TextView numOfCoachesTv = view.findViewById(R.id.num_of_coaches_tv);
        TextView numOfTrainingsTv = view.findViewById(R.id.num_of_trainings_tv);
        TextView numOfMatchesTv = view.findViewById(R.id.num_of_matches_tv);

        int numOfPlayers = team.getPlayers().size();
        int numOfCoaches = team.getCoaches().size();
        numOfPlayersTv.setText(String.valueOf(numOfPlayers));
        numOfCoachesTv.setText(String.valueOf(numOfCoaches));

        getMatchTrainingCount(numOfTrainingsTv, numOfMatchesTv);
        getAttendanceData();
    }

    private void getMatchTrainingCount(TextView numOfTrainingsTv, TextView numOfMatchesTv) {
        firebaseFirestore.collection("team").document(teamDoc).collection("training_match").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MatchTraining matchTraining = document.toObject(MatchTraining.class);
                                if (matchTraining.getType().equals("Match")) {
                                    numOfMatches += 1;
                                    numOfMatchesTv.setText(String.valueOf(numOfMatches));
                                    getMatchStatus(matchTraining);
                                } else {
                                    numOfTrainings += 1;
                                    numOfTrainingsTv.setText(String.valueOf(numOfTrainings));
                                    getTrainingStatus(matchTraining);
                                }
                            }
                            setPieCharts();
                        } else {
                            Log.e(TAG, "Count Failed: " + task.getException());
                        }
                    }
                });
    }

    private void getTrainingStatus(MatchTraining matchTraining) {
        if (matchTraining.getStatus().equals("Hasn't Happened")) {
            trainingsRemaining += 1;
        } else if (matchTraining.getStatus().equals("Completed")) {
            trainingsCompleted += 1;
        } else {
            trainingsCancelled += 1;
        }
    }

    private void getMatchStatus(MatchTraining matchTraining) {
        switch (matchTraining.getStatus()) {
            case "Haven't Played":
                matchRemaining += 1;
                break;
            case "Won":
                matchWon += 1;
                matchesPlayed += 1;
                break;
            case "Lost":
                matchLost += 1;
                matchesPlayed += 1;
                break;
            case "Draw":
                matchDraw += 1;
                matchesPlayed += 1;
                break;
            default:
                matchCancelled += 1;
                break;
        }
    }

    private void setPieCharts() {
        PieChart trainingPieChart, matchPieChart;
        TextView tRemLegendTv, tComLegendTv, tCanLegendTv, mWonLegendTv, mLostLegendTv, mDrawLegendTv, mCanLegendTv, mRemLegendTv;
        trainingPieChart = view.findViewById(R.id.training_results_pie_chart);
        tRemLegendTv = view.findViewById(R.id.training_remaining_legend_tv);
        tComLegendTv = view.findViewById(R.id.training_completed_legend_tv);
        tCanLegendTv = view.findViewById(R.id.trainings_cancelled_legend_tv);
        matchPieChart = view.findViewById(R.id.match_results_pie_chart);
        mWonLegendTv = view.findViewById(R.id.match_won_legend_tv);
        mLostLegendTv = view.findViewById(R.id.match_lost_legend_tv);
        mDrawLegendTv = view.findViewById(R.id.match_draw_legend_tv);
        mCanLegendTv = view.findViewById(R.id.match_cancelled_legend_tv);
        mRemLegendTv = view.findViewById(R.id.match_remaining_legend_tv);
        double winPercentage;

        setPieChatSlice(trainingPieChart, "Remaining", trainingsRemaining, "#0000FF", tRemLegendTv);
        setPieChatSlice(trainingPieChart,"Completed", trainingsCompleted, "#00FF00", tComLegendTv);
        setPieChatSlice(trainingPieChart,"Cancelled", trainingsCancelled, "#FF0000", tCanLegendTv);
        trainingPieChart.startAnimation();

        setPieChatSlice(matchPieChart,"Remaining", matchRemaining, "#0000FF", mRemLegendTv);
        setPieChatSlice(matchPieChart,"Won", matchWon, "#00FF00", mWonLegendTv);
        setPieChatSlice(matchPieChart,"Draw", matchDraw, "#FFFF00", mDrawLegendTv);
        setPieChatSlice(matchPieChart,"Lost", matchLost, "#FF000000", mLostLegendTv);
        setPieChatSlice(matchPieChart,"Cancelled", matchCancelled, "#FF0000", mCanLegendTv);

        if (matchesPlayed == 0) {
            winPercentage = 0;
        } else {
            winPercentage = (matchWon*100) / matchesPlayed;
        }

        matchPieChart.startAnimation();
        matchPieChart.setUseInnerValue(true);
        matchPieChart.setInnerValueSize(70);
        matchPieChart.setInnerValueString(winPercentage + "%");
    }

    private void setPieChatSlice(PieChart pieChat, String status, int count, String colour, TextView textView) {
        pieChat.addPieSlice(
                new PieModel(status, count, Color.parseColor(colour))
        );

        String legendText = count + " " + status;
        textView.setText(legendText);
    }

    private void getAttendanceData() {
        firebaseFirestore.collection("team").document(teamDoc).collection("training_match").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MatchTraining matchTraining = document.toObject(MatchTraining.class);
                                int yesCount = 0, noCount = 0, hasNotRespondedCount = 0;
                                int total = matchTraining.getAttendance().size();

                                for (Attendance attendance : matchTraining.getAttendance()) {
                                    if (attendance.getResponse().equals("Yes"))
                                        yesCount += 1;
                                    else if (attendance.getResponse().equals("No"))
                                        noCount += 1;
                                    else
                                        hasNotRespondedCount += 1;
                                }
                                totalYesCount += yesCount;
                                totalNoCount += noCount;
                                totalHasNotRespondedCount += hasNotRespondedCount;
                                totalResponsesCount += total;
                            }
                            calculateResponsePercentages();
                        } else {
                            Log.e(TAG, "Count Failed: " + task.getException());
                        }
                    }
                });
    }

    private void calculateResponsePercentages() {
        TextView yesPercentTv = view.findViewById(R.id.yes_percent_tv);
        TextView noPercentTv = view.findViewById(R.id.no_percent_tv);
        TextView noResponseTv = view.findViewById(R.id.has_not_responded_percent_tv);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        double yesPercentage = Double.parseDouble(decimalFormat.format((totalYesCount/totalResponsesCount) * 100));
        double noPercentage = Double.parseDouble(decimalFormat.format((totalNoCount/totalResponsesCount) * 100));
        double hasNotRespondedPercentage = Double.parseDouble(decimalFormat.format((totalHasNotRespondedCount/totalResponsesCount) * 100));

        yesPercentTv.setText(String.valueOf(yesPercentage));
        noPercentTv.setText(String.valueOf(noPercentage));
        noResponseTv.setText(String.valueOf(hasNotRespondedPercentage));
    }

    private void setup() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        teamNameTv = view.findViewById(R.id.team_name);
        transaction = getFragmentManager().beginTransaction();

        getTeamDetails();

        Button generalStatsBut = view.findViewById(R.id.general_stats_button);
        generalStatsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, new TeamStatsFragment()).commit();
            }
        });

        Button matchStats = view.findViewById(R.id.match_stats_button);
        matchStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MatchStatsFragment matchStatsFragment = new MatchStatsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("teamDoc", teamDoc);
                bundle.putSerializable("myTeam", team);
                matchStatsFragment.setArguments(bundle);
                transaction.replace(R.id.container, matchStatsFragment).commit();
            }
        });
    }

}