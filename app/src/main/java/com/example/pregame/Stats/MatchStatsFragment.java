package com.example.pregame.Stats;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pregame.Model.MatchStats;
import com.example.pregame.Model.Team;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;

public class MatchStatsFragment extends Fragment {
    public static final String TAG = "MatchStatsFragment";
    private View view;
    private FragmentTransaction transaction;
    private int totalMatchesPlayed, totalShotsMade, totalShotsMissed, totalShotsTaken, turnovers, fouls, steals, offensiveReb, defensiveReb, assist, blocks, points, opponentPoints, fTTaken, twoTaken, threeTaken, twoMade, threeMade;
    private String teamDoc, teamType, shotType;
    private FirebaseFirestore firebaseFirestore;

    public MatchStatsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_match_stats, container, false);

        setup();
        getMatchStatDetails();

        return view;
    }

    private void getMatchStatDetails() {
        firebaseFirestore.collection("team").document(teamDoc).collection("match_stats").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            totalMatchesPlayed = 0; totalShotsMade = 0; totalShotsMissed = 0; totalShotsTaken = 0; turnovers = 0; fouls = 0; steals = 0; offensiveReb = 0;defensiveReb = 0; assist = 0; blocks = 0; points = 0; opponentPoints = 0; fTTaken = 0; twoTaken = 0; threeTaken = 0; twoMade = 0; threeMade = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MatchStats matchStats = document.toObject(MatchStats.class);
                                calculateShotData(matchStats);
                                calculateTotalStats(matchStats);
                            }
                            setPieCharts();
                            setTotalMatchStatBarChart();
                            setPointsData();
                            calculateEfficiency();
                            calculateRebounds();
                            calculateAssist();
                        } else {
                            Log.e(TAG, "Count Failed: " + task.getException());
                        }
                    }
                });
    }

    private void calculateTotalStats(MatchStats matchStats) {
        totalMatchesPlayed += 1;
        turnovers += matchStats.getTurnovers();
        fouls += matchStats.getFouls();
        steals += matchStats.getSteals();
        offensiveReb += matchStats.getOffensiveRebounds();
        defensiveReb += matchStats.getDefensiveRebounds();
        assist += matchStats.getAssists();
        blocks += matchStats.getBlocks();
        points += matchStats.getMyTeamScore();
        opponentPoints += matchStats.getOpponentScore();
        fTTaken += matchStats.getShotsTakenFt();
        twoTaken += matchStats.getShotsTakenTwo();
        threeTaken += matchStats.getShotsTakenThree();
        twoMade += matchStats.getShotsMadeTwo();
        threeMade += matchStats.getShotsMadeThree();
    }

    private void calculateShotData(MatchStats matchStats) {
        if (teamType.equals("My Team")) {
            if (shotType.equals("Free Throw")) {
                setShot(matchStats.getShotsMadeFt(), matchStats.getShotsMissedFt(), matchStats.getShotsTakenFt());
            } else if (shotType.equals("2pt")) {
                setShot(matchStats.getShotsMadeTwo(), matchStats.getShotsMissedTwo(), matchStats.getShotsTakenTwo());
            } else {
                setShot(matchStats.getShotsMadeThree(), matchStats.getShotsMissedThree(), matchStats.getShotsTakenThree());
            }
        } else {
            if (shotType.equals("Free Throw")) {
                setShot(matchStats.getOpponentShotsMadeFt(), matchStats.getOpponentShotsMissedFt(), matchStats.getOpponentShotsTakenFt());
            } else if (shotType.equals("2pt")) {
                setShot(matchStats.getOpponentShotsMadeTwo(), matchStats.getOpponentShotsMissedTwo(), matchStats.getOpponentShotsTakenTwo());
            } else {
                setShot(matchStats.getOpponentShotsMadeThree(), matchStats.getOpponentShotsMissedThree(), matchStats.getOpponentShotsTakenThree());
            }
        }
    }

    private void setShot(int made, int missed, int taken) {
        totalShotsMade += made;
        totalShotsMissed += missed;
        totalShotsTaken += taken;
    }

    private void setPieCharts() {
        TextView shotsMadeLegendTv, shotsMissedLegendTv, shotsTakenLegendTv;
        PieChart shotPercentagePieChart = view.findViewById(R.id.shots_breakdown_pie_chart);
        shotPercentagePieChart.clearChart();

        shotsMadeLegendTv = view.findViewById(R.id.legend_shot_made_tv);
        shotsMissedLegendTv = view.findViewById(R.id.legend_shot_missed_tv);
        shotsTakenLegendTv = view.findViewById(R.id.legend_shot_taken_tv);

        setPieChatSlice(shotPercentagePieChart, "Made", totalShotsMade, "#00FF00", shotsMadeLegendTv);
        setPieChatSlice(shotPercentagePieChart,"Missed", totalShotsMissed, "#FF0000", shotsMissedLegendTv);
        String shotsTaken = totalShotsTaken + " " + "Taken";
        shotsTakenLegendTv.setText(shotsTaken);
        shotPercentagePieChart.startAnimation();

        double shotPercentage = totalShotsMade*100 / totalShotsTaken;
        shotPercentagePieChart.startAnimation();
        shotPercentagePieChart.setUseInnerValue(true);
        shotPercentagePieChart.setInnerValueSize(60);
        shotPercentagePieChart.setInnerValueString(shotPercentage + "%");
    }

    private void setPieChatSlice(PieChart pieChat, String status, int count, String colour, TextView textView) {
        pieChat.addPieSlice(
                new PieModel(status, count, Color.parseColor(colour))
        );

        String legendText = count + " " + status;
        textView.setText(legendText);
    }

    private void setTotalMatchStatBarChart() {
        BarChart totalMatchStats = view.findViewById(R.id.total_match_stats_bar_chart);
        totalMatchStats.clearChart();

        totalMatchStats.addBar(new BarModel("Asst", assist, Color.parseColor("#ff0000")));
        totalMatchStats.addBar(new BarModel("O/R", offensiveReb, Color.parseColor("#ffa500")));
        totalMatchStats.addBar(new BarModel("D/R", defensiveReb, Color.parseColor("#ffff00")));
        totalMatchStats.addBar(new BarModel("Blk", blocks, Color.parseColor("#00ff00")));
        totalMatchStats.addBar(new BarModel("F", fouls, Color.parseColor("#008000")));
        totalMatchStats.addBar(new BarModel("Stl", steals, Color.parseColor("#00ffff")));
        totalMatchStats.addBar(new BarModel("To", turnovers, Color.parseColor("#0000ff")));
    }

    private void setPointsData() {
        TextView totalPointsTv, averagePointsTv, totalPointsAllowedTv, averagePointsAllowedTv;
        totalPointsTv = view.findViewById(R.id.total_points_scored);
        averagePointsTv = view.findViewById(R.id.average_points_per_game);
        totalPointsAllowedTv = view.findViewById(R.id.total_points_allowed);
        averagePointsAllowedTv = view.findViewById(R.id.average_points_allowed_per_game);

        double pointsScored = (double) points / (double) totalMatchesPlayed;
        double pointsAllowed = (double) opponentPoints / (double) totalMatchesPlayed;
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        totalPointsTv.setText(decimalFormat.format(points));
        averagePointsTv.setText(String.valueOf(pointsScored));
        totalPointsAllowedTv.setText(decimalFormat.format(opponentPoints));
        averagePointsAllowedTv.setText(String.valueOf(pointsAllowed));
    }

    private void calculateEfficiency() {
        TextView totalPossessionTv = view.findViewById(R.id.total_possessions_tv);
        TextView pointsPerPossessionTv = view.findViewById(R.id.points_per_possession_tv);
        TextView offensiveEfficiencyTv = view.findViewById(R.id.offensive_efficiency);
        TextView pointsAllowedTv = view.findViewById(R.id.points_allowed_per_possession_tv);
        TextView defensiveEfficiencyTv = view.findViewById(R.id.defensive_efficiency);
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        int totalFieldGoals = twoTaken + threeTaken;
        double totalPossessions = totalFieldGoals - offensiveReb + turnovers + (0.4*fTTaken);
        double pointsPerPossession = points / totalPossessions;
        double pointsAllowedPerPossession = opponentPoints / totalPossessions;
        double offensiveEfficiency = pointsPerPossession * 100;
        double defensiveEfficiency = pointsAllowedPerPossession * 100;

        totalPossessionTv.setText(decimalFormat.format(totalPossessions));
        pointsPerPossessionTv.setText(decimalFormat.format(pointsPerPossession));
        offensiveEfficiencyTv.setText(decimalFormat.format(offensiveEfficiency));
        pointsAllowedTv.setText(decimalFormat.format(pointsAllowedPerPossession));
        defensiveEfficiencyTv.setText(decimalFormat.format(defensiveEfficiency));
    }

    private void calculateRebounds() {
        TextView totalReboundsTv = view.findViewById(R.id.total_rebounds_tv);
        TextView avgDefReboundTv = view.findViewById(R.id.average_defensive_rebounds_tv);
        TextView percentDefRebTv = view.findViewById(R.id.percentage_of_def_rebounds);
        TextView avgOffReboundTv = view.findViewById(R.id.average_offensive_rebounds_tv);
        TextView percentOffRebTv = view.findViewById(R.id.percentage_of_off_rebounds);
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        int totalRebounds = defensiveReb + offensiveReb;
        double percentDefReb = (double) defensiveReb / (double) totalRebounds;
        double percentOffReb = (double) offensiveReb / (double) totalRebounds;
        double averageDefReb = (double) defensiveReb / (double) totalMatchesPlayed;
        double averageOffReb = (double) offensiveReb / (double) totalMatchesPlayed;

        totalReboundsTv.setText(String.valueOf(totalRebounds));
        avgDefReboundTv.setText(decimalFormat.format(averageDefReb));
        percentDefRebTv.setText(decimalFormat.format(percentDefReb));
        avgOffReboundTv.setText(decimalFormat.format(averageOffReb));
        percentOffRebTv.setText(decimalFormat.format(percentOffReb));

    }

    private void calculateAssist() {
        TextView averageAssistTv = view.findViewById(R.id.average_assist_tv);
        TextView averageAssistedFieldGoalsTv = view.findViewById(R.id.assisted_field_goals_tv);
        TextView averageTurnoverTv = view.findViewById(R.id.average_turnover);
        TextView assistToTurnoverTv = view.findViewById(R.id.assist_to_turnover);
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        double assistToTurnover = (double) assist / (double) turnovers;
        double averageAssist = (double) assist / (double) totalMatchesPlayed;
        double averageTurnover = (double) turnovers / (double) totalMatchesPlayed;
        int totalFieldGoalsMade = twoMade + threeMade;
        double assistedFieldGoal = (double) assist / (double) totalFieldGoalsMade;

        averageAssistTv.setText(decimalFormat.format(averageAssist));
        averageAssistedFieldGoalsTv.setText(decimalFormat.format(assistedFieldGoal));
        averageTurnoverTv.setText(decimalFormat.format(averageTurnover));
        assistToTurnoverTv.setText(decimalFormat.format(assistToTurnover));
    }

    private void setup() {
        transaction = getFragmentManager().beginTransaction();
        teamType = "My Team"; shotType = "Free Throw";
        Bundle bundle = getArguments();
        teamDoc = bundle.getString("teamDoc");
        Team currentTeam = (Team) bundle.getSerializable("myTeam");
        firebaseFirestore = FirebaseFirestore.getInstance();

        TextView teamNameTv = view.findViewById(R.id.team_name);
        teamNameTv.setText(currentTeam.getTeamName());

        setupButton(R.id.general_stats_button, new TeamStatsFragment());
        setupButton(R.id.match_stats_button, new MatchStatsFragment());

        displayLegend(R.id.display_legend_iv, R.layout.dialogue_bar_chart_legend);
        displayLegend(R.id.points_breakdown_legend_iv, R.layout.dialogue_points_breakdown_legend);
        displayLegend(R.id.possession_breakdown_legend_iv, R.layout.dialogue_possession_breakdown_legend);
        displayLegend(R.id.assist_breakdown_legend_iv, R.layout.dialogue_assist_breakdown_legend);
        displayLegend(R.id.rebounds_breakdown_legend_iv, R.layout.dialogue_rebounds_breakdown_legend);

        switchTeams();

        TextView myShotsTitleTv = view.findViewById(R.id.shot_percentage_tv);
        chooseMyTeamShotType(myShotsTitleTv, R.id.free_throw_button, "Free Throw");
        chooseMyTeamShotType( myShotsTitleTv, R.id.two_point_button, "2pt");
        chooseMyTeamShotType(myShotsTitleTv, R.id.three_point_button, "3pt");
    }

    private void displayLegend(int id, int legendId) {
        ImageView legendIv = view.findViewById(id);
        legendIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View legendView = layoutInflater.inflate(legendId, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                ImageView closeLegend = legendView.findViewById(R.id.close_legend_iv);

                alertDialogBuilder.setCancelable(false).setView(legendView);
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();

                closeLegend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.cancel();
                    }
                });
            }
        });
    }

    private void switchTeams() {
        SwitchCompat switchTeams = view.findViewById(R.id.switch_teams_button);

        switchTeams.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    teamType = "My Team";
                } else {
                    teamType = "Opponent";
                }

                switchTeams.setText(teamType);
                getMatchStatDetails();
            }
        });
    }

    private void chooseMyTeamShotType(TextView textView, int id, String type) {
        String title = type + " Shot Breakdown";
        Button freeThrowBut = view.findViewById(id);
        freeThrowBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shotType = type;
                getMatchStatDetails();
                textView.setText(title);
            }
        });
    }

    private void setupButton(int id, Fragment fragment) {
        Button button = view.findViewById(id);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, fragment).commit();
            }
        });
    }
}