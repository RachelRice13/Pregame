package com.example.pregame.Stats;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pregame.HomePage.CoachHomeActivity;
import com.example.pregame.Model.MatchStats;
import com.example.pregame.Model.Team;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CreateMatchStatsFragment extends Fragment {
    private static final String TAG = "MatchStats";
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private final int[] myTeamScore = {0}, opponentScore = {0}, offReb = {0}, defReb = {0}, assists = {0}, blocks = {0}, turnovers = {0}, steals = {0}, fouls = {0};
    private FloatingActionButton myTeamPlusOneFB, myTeamPlusTwoFB, myTeamPlusThreeFB, opponentPlusOneFB, opponentPlusTwoFB, opponentPlusThreeFB, offRebFB, defRebFB, asstFB, blockFB, turnoverFB, stealFB, foulFB;
    private TextView myTeamScoreTv, opponentScoreTv, matchMessageTv;
    private Button endMatchButton;
    private Team currentTeam;
    private String opponent, date;

    public CreateMatchStatsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_match_stats, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        currentTeam = CoachHomeActivity.currentTeam;
        opponent = getArguments().getString("Opponent");
        date = getArguments().getString("Date");

        getViews();
        setButtons();
        endMatch();

        return view;
    }

    public void getViews() {
        TextView myTeamTv = view.findViewById(R.id.my_team_name_tv);
        myTeamTv.setText(currentTeam.getTeamName());
        TextView opponentTv = view.findViewById(R.id.opponent_name_tv);
        opponentTv.setText(opponent);

        myTeamScoreTv = view.findViewById(R.id.my_team_score_tv);
        opponentScoreTv = view.findViewById(R.id.opponent_score_tv);
        myTeamScoreTv.setText(String.valueOf(myTeamScore[0]));
        opponentScoreTv.setText(String.valueOf(opponentScore[0]));
        matchMessageTv = view.findViewById(R.id.match_message_tv);
        matchMessageTv.setText("Match Started");

        myTeamPlusOneFB = view.findViewById(R.id.my_team_plus_one_fb);
        myTeamPlusTwoFB = view.findViewById(R.id.my_team_plus_two_fb);
        myTeamPlusThreeFB = view.findViewById(R.id.my_team_plus_three_fb);
        opponentPlusOneFB = view.findViewById(R.id.opponent_plus_one_fb);
        opponentPlusTwoFB = view.findViewById(R.id.opponent_plus_two_fb);
        opponentPlusThreeFB = view.findViewById(R.id.opponent_plus_three_fb);

        offRebFB = view.findViewById(R.id.offensive_rebound_fb);
        defRebFB = view.findViewById(R.id.defensive_rebound_fb);
        asstFB = view.findViewById(R.id.assist_fb);
        blockFB = view.findViewById(R.id.block_fb);
        turnoverFB = view.findViewById(R.id.turnover_fb);
        stealFB = view.findViewById(R.id.steal_fb);
        foulFB = view.findViewById(R.id.foul_fb);

        endMatchButton = view.findViewById(R.id.end_game_button);
    }

    public void setButtons() {
        setButton(myTeamPlusOneFB, myTeamScore, "+1 Point for " + currentTeam.getTeamName(), 1);
        setButton(opponentPlusOneFB, opponentScore, "+1 Point for " + opponent, 1);
        setButton(myTeamPlusTwoFB, myTeamScore, "+2 Point for " + currentTeam.getTeamName(), 2);
        setButton(opponentPlusTwoFB, opponentScore, "+2 Point for " + opponent, 2);
        setButton(myTeamPlusThreeFB, myTeamScore, "+3 Point for " + currentTeam.getTeamName(), 3);
        setButton(opponentPlusThreeFB, opponentScore, "+3 Point for " + opponent, 3);

        setButton(offRebFB, offReb, "+1 Offensive Rebound", 1);
        setButton(defRebFB, defReb, "+1 Defensive Rebound", 1);
        setButton(asstFB, assists, "+1 Assist", 1);
        setButton(blockFB, blocks, "+1 Block", 1);
        setButton(turnoverFB, turnovers, "+1 Turnover", 1);
        setButton(stealFB, steals, "+1 Steal", 1);
        setButton(foulFB, fouls, "+1 Foul", 1);
    }

    public void setButton(FloatingActionButton button, int[] globalNum, String message, int addBy) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalNum[0] += addBy;

                if (message.contains(currentTeam.getTeamName())) {
                    myTeamScoreTv.setText(String.valueOf(myTeamScore[0]));
                    matchMessageTv.setText(message);
                } else if (message.contains(opponent)) {
                    opponentScoreTv.setText(String.valueOf(globalNum[0]));
                    matchMessageTv.setText(message);
                } else {
                    matchMessageTv.setText(message);
                }
            }
        });
    }

    public void endMatch() {
        endMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MatchStats matchStats = new MatchStats(currentTeam.getTeamName(), opponent, date, myTeamScore[0], opponentScore[0], offReb[0], defReb[0], assists[0], blocks[0], turnovers[0], steals[0], fouls[0]);

                firebaseFirestore.collection("team").whereEqualTo("teamName", currentTeam.getTeamName()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        String teamDoc = queryDocumentSnapshot.getId();

                                        firebaseFirestore.collection("team").document(teamDoc).collection("matchStats")
                                                .add(matchStats)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference reference) {
                                                        Log.d(TAG, "Successfully added match stats to DB");
                                                        Toast.makeText(getContext(), "Added Match Stats to DB", Toast.LENGTH_SHORT).show();
                                                        getFragmentManager().beginTransaction().replace(R.id.container, new GameStatsFragment()).commit();
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
                            }
                        });
            }
        });
    }

}