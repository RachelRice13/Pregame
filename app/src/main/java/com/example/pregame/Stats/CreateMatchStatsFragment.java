package com.example.pregame.Stats;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.pregame.Model.IndividualStats;
import com.example.pregame.Model.MatchStats;
import com.example.pregame.Model.User;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CreateMatchStatsFragment extends Fragment {
    private static final String TAG = "MatchStats";
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private MatchStats matchStats;
    private String matchId, teamDoc;
    private ArrayList<String> playerFullNames;
    private final int[] myTeamScore = {0}, opponentScore = {0}, myTeamTakenFT = {0}, myTeamTakenTwo = {0},myTeamTakenThree = {0}, opponentTakenFT = {0}, opponentTakenTwo = {0}, opponentTakenThree = {0}, myTeamMadeFT = {0}, myTeamMadeTwo = {0}, myTeamMadeThree = {0}, myTeamMissedFT = {0}, myTeamMissedTwo = {0}, myTeamMissedThree = {0}, opponentMadeFT = {0}, opponentMadeTwo = {0}, opponentMadeThree = {0}, opponentMissedFT = {0}, opponentMissedTwo = {0}, opponentMissedThree = {0}, offReb = {0}, defReb = {0}, assists = {0}, blocks = {0}, turnovers = {0}, steals = {0}, fouls = {0};
    private LinearLayout viewPlayerList;
    private TextView messagesTv;
    private FragmentTransaction transaction;

    public CreateMatchStatsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_match_stats, container, false);

        setUp();

        return view;
    }

    private void getPlayerFullName(DocumentReference user) {
        firebaseFirestore.collection("player").document(user.getId()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User player = documentSnapshot.toObject(User.class);
                        String fullName = player.getFirstName() + " " + player.getSurname();
                        playerFullNames.add(fullName);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to get player's full name.", e);
                    }
                });
    }

    private void endMatch() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Do you want end the match?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MatchStats updatedMatchStats = new MatchStats(matchStats.getMyTeam(), matchStats.getOpponent(), matchStats.getDate(), matchStats.getId(), matchStats.getTitle(), myTeamScore[0], opponentScore[0], opponentTakenTwo[0], opponentMissedTwo[0], opponentMadeTwo[0], opponentTakenThree[0], opponentMissedThree[0], opponentMadeThree[0], opponentTakenFT[0], opponentMissedFT[0], opponentMadeFT[0], matchStats.getPlayers(), myTeamTakenTwo[0], myTeamMissedTwo[0], myTeamMadeTwo[0], myTeamTakenThree[0], myTeamMissedThree[0], myTeamMadeThree[0], myTeamTakenFT[0], myTeamMissedFT[0], myTeamMadeFT[0], offReb[0], defReb[0], assists[0], blocks[0], turnovers[0], steals[0], fouls[0]);
                        ViewMatchStatsFragment viewMatchStatsFragment = new ViewMatchStatsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("matchStatId", matchId);
                        bundle.putString("teamDoc", teamDoc);
                        bundle.putSerializable("matchStats", updatedMatchStats);
                        viewMatchStatsFragment.setArguments(bundle);
                        transaction.replace(R.id.container, viewMatchStatsFragment).commit();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void assignPlayer(String statType) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View playerView = layoutInflater.inflate(R.layout.dialogue_display_players_match_stats, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        viewPlayerList = playerView.findViewById(R.id.player_list_ll);
        viewPlayerList.setOrientation(LinearLayout.VERTICAL);

        alertDialogBuilder.setCancelable(false).setView(playerView);
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        setPlayerRow(statType, alert);
    }

    private void setPlayerRow(String statType, AlertDialog alertDialog) {
        LinearLayout rowLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutParams.setMargins(5, 5, 5, 5);
        rowLayout.setLayoutParams(linearLayoutParams);

        RadioGroup radioGroup = new RadioGroup(getContext());
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        RadioButton[] radioButtons = new RadioButton[playerFullNames.size()];

        for (int i = 0; i < playerFullNames.size(); i++) {
            radioButtons[i] = new RadioButton(getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            radioButtons[i].setText(playerFullNames.get(i));
            radioButtons[i].setId(i);
            radioButtons[i].setLayoutParams(layoutParams);
            radioGroup.addView(radioButtons[i]);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int checkedRadioButton = radioGroup.getCheckedRadioButtonId();
                IndividualStats individualStats = matchStats.getPlayers().get(checkedRadioButton);
                getStat(individualStats, statType);
                alertDialog.cancel();
            }
        });

        rowLayout.addView(radioGroup);
        viewPlayerList.addView(rowLayout);
    }

    private void getStat(IndividualStats player, String statType) {
        int shotTakenFt = player.getShotsTakenFt(); int shotTakenTwo = player.getShotsTakenTwo(); int shotTakenThree = player.getShotsTakenThree(); int pointsScored = player.getPointsScored();

        if (statType.equals("Made FT")) {
            int number = player.getShotsMadeFt();
            number += 1; shotTakenFt += 1; pointsScored += 1;
            player.setShotsMadeFt(number); player.setShotsTakenFt(shotTakenFt); player.setPointsScored(pointsScored);
            Log.d(TAG, player.getPlayer().getId() + "\t\tMade FT: " + player.getShotsMadeFt());
        }
        if (statType.equals("Made 2pt")) {
            int number = player.getShotsMadeTwo();
            number += 1; shotTakenTwo += 1; pointsScored += 2;
            player.setShotsMadeTwo(number); player.setShotsTakenTwo(shotTakenTwo); player.setPointsScored(pointsScored);
            Log.d(TAG, player.getPlayer().getId() + "\t\tMade 2pt: " + player.getShotsMadeTwo());
        }
        if (statType.equals("Made 3pt")) {
            int number = player.getShotsMadeThree();
            number += 1; shotTakenThree += 1; pointsScored += 3;
            player.setShotsMadeThree(number); player.setShotsTakenThree(shotTakenThree); player.setPointsScored(pointsScored);
            Log.d(TAG, player.getPlayer().getId() + "\t\tMade 3pt: " + player.getShotsMadeThree());
        }
        if (statType.equals("Missed FT")) {
            int number = player.getShotsMissedFt();
            number += 1; shotTakenFt += 1;
            player.setShotsMissedFt(number); player.setShotsTakenFt(shotTakenFt);
            Log.d(TAG, player.getPlayer().getId() + "\t\tMissed FT: " + player.getShotsMissedFt());
        }
        if (statType.equals("Missed 2pt")) {
            int number = player.getShotsMissedTwo();
            number += 1; shotTakenTwo += 1;
            player.setShotsMissedTwo(number); player.setShotsTakenTwo(shotTakenTwo);
            Log.d(TAG, player.getPlayer().getId() + "\t\tMissed 2pt: " + player.getShotsMissedTwo());
        }
        if (statType.equals("Missed 3pt")) {
            int number = player.getShotsMissedThree();
            number += 1; shotTakenThree += 1;
            player.setShotsMissedThree(number); player.setShotsTakenThree(shotTakenThree);
            Log.d(TAG, player.getPlayer().getId() + "\t\tMissed 3pt: " + player.getShotsMissedThree());
        }
        if (statType.equals("Off Reb")) {
            int number = player.getOffensiveRebounds();
            number += 1; player.setOffensiveRebounds(number);
            Log.d(TAG, player.getPlayer().getId() + "\t\tOff Reb: " + player.getOffensiveRebounds());
        }
        if (statType.equals("Def Reb")) {
            int number = player.getDefensiveRebounds();
            number += 1; player.setDefensiveRebounds(number);
            Log.d(TAG, player.getPlayer().getId() + "\t\tDef Reb: " + player.getDefensiveRebounds());
        }
        if (statType.equals("Assist")) {
            int number = player.getAssists();
            number += 1; player.setAssists(number);
            Log.d(TAG, player.getPlayer().getId() + "\t\tAssist: " + player.getAssists());
        }
        if (statType.equals("Block")) {
            int number = player.getBlocks();
            number += 1; player.setBlocks(number);
            Log.d(TAG, player.getPlayer().getId() + "\t\tBlock: " + player.getBlocks());
        }
        if (statType.equals("Turnover")) {
            int number = player.getTurnovers();
            number += 1; player.setTurnovers(number);
            Log.d(TAG, player.getPlayer().getId() + "\t\tTurnover: " + player.getTurnovers());
        }
        if (statType.equals("Steal")) {
            int number = player.getSteals();
            number += 1; player.setSteals(number);
            Log.d(TAG, player.getPlayer().getId() + "\t\tSteal: " + player.getSteals());
        }
        if (statType.equals("Fouls")) {
            int number = player.getFouls();
            number += 1; player.setFouls(number);
            Log.d(TAG, player.getPlayer().getId() + "\t\tFouls: " + player.getFouls());
        }
    }

    private void setUp() {
        Bundle bundle = getArguments();
        matchStats = (MatchStats) bundle.getSerializable("matchStats");
        matchId = bundle.getString("matchStatId");
        teamDoc = bundle.getString("teamDoc");
        String myTeam = "My Team"; String opponent = "Opponent";
        transaction = getFragmentManager().beginTransaction();
        firebaseFirestore = FirebaseFirestore.getInstance();
        messagesTv = view.findViewById(R.id.messages_tv);
        messagesTv.setText("Match Started");
        playerFullNames = new ArrayList<>();
        playerFullNames.removeAll(playerFullNames);

        for (IndividualStats individualStats : matchStats.getPlayers()) {
            getPlayerFullName(individualStats.getPlayer());
        }

        TextView myTeamNameTv = view.findViewById(R.id.my_team_name_tv);
        myTeamNameTv.setText(matchStats.getMyTeam());
        TextView opponentNameTv = view.findViewById(R.id.opponent_name_tv);
        opponentNameTv.setText(matchStats.getOpponent());

        setScore(R.id.my_team_score_tv, myTeamScore);
        setScore(R.id.opponent_score_tv, opponentScore);

        Button endMatchButton = view.findViewById(R.id.end_match_button);
        endMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endMatch();
            }
        });

        scorersButtonSetup(R.id.my_team_plus_one_fb, myTeamMadeFT, myTeam, 1, "Made FT", myTeamTakenFT);
        scorersButtonSetup(R.id.my_team_plus_two_fb, myTeamMadeTwo, myTeam, 2, "Made 2pt", myTeamTakenTwo);
        scorersButtonSetup(R.id.my_team_plus_three_fb, myTeamMadeThree, myTeam, 3, "Made 3pt", myTeamTakenThree);
        missedShotButtonSetup(R.id.my_team_miss_one_fb, myTeamMissedFT, myTeam, "Missed FT", myTeamTakenFT);
        missedShotButtonSetup(R.id.my_team_miss_two_fb, myTeamMissedTwo, myTeam, "Missed 2pt", myTeamTakenTwo);
        missedShotButtonSetup(R.id.my_team_miss_three_fb, myTeamMissedThree, myTeam, "Missed 3pt", myTeamTakenThree);

        scorersButtonSetup(R.id.opponent_plus_one_fb, opponentMadeFT, opponent, 1, "Made FT", opponentTakenFT);
        scorersButtonSetup(R.id.opponent_plus_two_fb, opponentMadeTwo, opponent, 2, "Made 2pt", opponentTakenTwo);
        scorersButtonSetup(R.id.opponent_plus_three_fb, opponentMadeThree, opponent, 3, "Made 3pt", opponentTakenThree);
        missedShotButtonSetup(R.id.opponent_miss_one_fb, opponentMissedFT, opponent, "Missed FT", opponentTakenFT);
        missedShotButtonSetup(R.id.opponent_miss_two_fb, opponentMissedTwo, opponent, "Missed 2pt", opponentTakenTwo);
        missedShotButtonSetup(R.id.opponent_miss_three_fb, opponentMissedThree, opponent, "Missed 3pt", opponentTakenThree);

        buttonSetup(R.id.offensive_rebound_button, offReb, "Off Reb");
        buttonSetup(R.id.defensive_rebound_button, defReb, "Def Reb");
        buttonSetup(R.id.assist_button, assists, "Assist");
        buttonSetup(R.id.block_button, blocks, "Block");
        buttonSetup(R.id.turnover_button, turnovers, "Turnover");
        buttonSetup(R.id.steal_button, steals, "Steal");
        buttonSetup(R.id.foul_button, fouls, "Fouls");
    }

    private void buttonSetup(int buttonId, int[] number, String statType) {
        Button button = view.findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number[0] += 1;
                assignPlayer(statType);
                String message = matchStats.getMyTeam() + " " + statType;
                messagesTv.setText(message);
            }
        });
    }

    private void missedShotButtonSetup(int buttonId, int[] count, String type, String statType, int[] taken) {
        FloatingActionButton button = view.findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count[0] += 1; taken[0] += 1;
                String message;

                if (type.equals("My Team")) {
                    assignPlayer(statType);
                    message = matchStats.getMyTeam() + " " + statType;
                } else {
                    message = matchStats.getOpponent() + " " + statType;
                }
                messagesTv.setText(message);
            }
        });
    }

    private void scorersButtonSetup(int buttonId, int[] count, String type, int addBy, String statType, int[] taken) {
        FloatingActionButton button = view.findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count[0] += 1; taken[0] += 1;
                String message;

                if (type.equals("My Team")) {
                    myTeamScore[0] += addBy;
                    setScore(R.id.my_team_score_tv, myTeamScore);
                    assignPlayer(statType);
                    message = matchStats.getMyTeam() + " " + statType;
                } else {
                    opponentScore[0] += addBy;
                    setScore(R.id.opponent_score_tv, opponentScore);
                    message = matchStats.getOpponent() + " " + statType;
                }
                messagesTv.setText(message);
            }
        });
    }

    private void setScore(int id, int[] number) {
        TextView textView = view.findViewById(id);
        textView.setText(String.valueOf(number[0]));
    }
}