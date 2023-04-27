package com.example.pregame.Stats;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pregame.HomePage.CoachHomeFragment;
import com.example.pregame.Model.IndividualStats;
import com.example.pregame.Model.MatchStats;
import com.example.pregame.Model.User;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ViewMatchStatsFragment extends Fragment {
    private static final String TAG = "ViewMatchStatsFragment";
    private View view;
    private MatchStats matchStats;
    private String matchId, teamDoc;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<String> columnNames, statsList;
    private LinearLayout firstColumn, secondColumn;

    public ViewMatchStatsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_match_stats, container, false);
        setup();
        setIndividualStats();
        return view;
    }

    private void setIndividualStats() {
        setNameColumn("Player");
        setSecondColumn(columnNames);

        for (IndividualStats individualStats : matchStats.getPlayers()) {
            getPlayerFullName(individualStats.getPlayer());
            addStatsToList(individualStats);
            setSecondColumn(statsList);
            statsList.removeAll(statsList);
        }

    }

    private void getPlayerFullName(DocumentReference user) {
        firebaseFirestore.collection("player").document(user.getId()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User player = documentSnapshot.toObject(User.class);
                        String fullName = player.getFirstName() + " " + player.getSurname();
                        setNameColumn(fullName);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to get player's full name.", e);
                    }
                });
    }

    private void setNameColumn(String fullName) {
        LinearLayout rowLayout = new LinearLayout(getContext());
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(480,  60);
        rowLayout.setLayoutParams(layoutParams);

        TextView textView = new TextView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(fullName);
        textView.setLayoutParams(params);

        rowLayout.addView(textView);
        firstColumn.addView(rowLayout);
    }

    private void addStatsToList(IndividualStats individualStats) {
        DecimalFormat df = new DecimalFormat("#.#");
        int totalReb = individualStats.getDefensiveRebounds() + individualStats.getOffensiveRebounds();
        double fieldGoalPerc = calculatePercent(individualStats.getShotsTakenTwo(), individualStats.getShotsMadeTwo());
        double threePerc = calculatePercent(individualStats.getShotsTakenThree(), individualStats.getShotsMadeThree());
        double freeThrowPerc = calculatePercent(individualStats.getShotsTakenFt(), individualStats.getShotsMadeFt());

        statsList.add(String.valueOf(individualStats.getPointsScored()));
        statsList.add(String.valueOf(individualStats.getShotsMadeTwo()));
        statsList.add(String.valueOf(individualStats.getShotsTakenTwo()));
        statsList.add(df.format(fieldGoalPerc));
        statsList.add(String.valueOf(individualStats.getShotsMadeThree()));
        statsList.add(String.valueOf(individualStats.getShotsTakenThree()));
        statsList.add(df.format(threePerc));
        statsList.add(String.valueOf(individualStats.getShotsMadeFt()));
        statsList.add(String.valueOf(individualStats.getShotsTakenFt()));
        statsList.add(df.format(freeThrowPerc));
        statsList.add(String.valueOf(individualStats.getOffensiveRebounds()));
        statsList.add(String.valueOf(individualStats.getDefensiveRebounds()));
        statsList.add(String.valueOf(totalReb));
        statsList.add(String.valueOf(individualStats.getAssists()));
        statsList.add(String.valueOf(individualStats.getTurnovers()));
        statsList.add(String.valueOf(individualStats.getSteals()));
        statsList.add(String.valueOf(individualStats.getBlocks()));
        statsList.add(String.valueOf(individualStats.getFouls()));
    }

    private double calculatePercent(int taken, int made) {
        if (taken == 0)
            return 0;
        else
            return (double) made*100 / (double) taken;
    }

    private void setSecondColumn(ArrayList<String> columnNames) {
        LinearLayout rowLayout = new LinearLayout(getContext());
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  60);
        rowLayout.setLayoutParams(layoutParams);

        createTextView(rowLayout, columnNames.get(0));
        createTextView(rowLayout, columnNames.get(1));
        createTextView(rowLayout, columnNames.get(2));
        createTextView(rowLayout, columnNames.get(3));
        createTextView(rowLayout, columnNames.get(4));
        createTextView(rowLayout, columnNames.get(5));
        createTextView(rowLayout, columnNames.get(6));
        createTextView(rowLayout, columnNames.get(7));
        createTextView(rowLayout, columnNames.get(8));
        createTextView(rowLayout, columnNames.get(9));
        createTextView(rowLayout, columnNames.get(10));
        createTextView(rowLayout, columnNames.get(11));
        createTextView(rowLayout, columnNames.get(12));
        createTextView(rowLayout, columnNames.get(13));
        createTextView(rowLayout, columnNames.get(14));
        createTextView(rowLayout, columnNames.get(15));
        createTextView(rowLayout, columnNames.get(16));
        createTextView(rowLayout, columnNames.get(17));

        secondColumn.addView(rowLayout);

    }

    private void createTextView(LinearLayout rowLayout, String text) {
        TextView textView = new TextView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(120, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(text);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        rowLayout.addView(textView);
    }

    private void goHome() {
        hideNavigation();
        getMatch();
        updateFireStore();
    }

    private void hideNavigation() {
        MaterialToolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    private void updateFireStore() {
        firebaseFirestore.collection("team").document(teamDoc).collection("match_stats").document(matchId).set(matchStats)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, new GameStatsFragment()).commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update match stats player array");
                    }
                });
    }

    private void getMatch() {
        firebaseFirestore.collection("team").document(teamDoc).collection("training_match").whereEqualTo("id", matchStats.getId()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                String matchId = queryDocumentSnapshot.getId();

                                updateMatchScore(matchId, "teamScore", matchStats.getMyTeamScore());
                                updateMatchScore(matchId, "opponentScore", matchStats.getOpponentScore());

                                if (matchStats.getMyTeamScore() > matchStats.getOpponentScore())
                                    updateStatus(matchId, "Won");
                                else if (matchStats.getMyTeamScore() < matchStats.getOpponentScore())
                                    updateStatus(matchId, "Lost");
                                else if (matchStats.getMyTeamScore() == matchStats.getOpponentScore())
                                    updateStatus(matchId, "Draw");
                                else
                                    updateStatus(matchId, "Haven't Played");

                            }
                        }
                    }
                });
    }

    private void updateMatchScore(String id, String dataType, int number) {
        firebaseFirestore.collection("team").document(teamDoc).collection("training_match").document(id).update(dataType, number)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Updated " + dataType);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update match stats player array");
                    }
                });
    }

    private void updateStatus(String id, String status) {
        firebaseFirestore.collection("team").document(teamDoc).collection("training_match").document(id).update("status", status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Updated match status");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update match stats player array");
                    }
                });
    }

    private void setup() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        Bundle bundle = getArguments();
        matchStats = (MatchStats) bundle.getSerializable("matchStats");
        matchId = bundle.getString("matchStatId");
        teamDoc = bundle.getString("teamDoc");
        columnNames = new ArrayList<>();
        statsList = new ArrayList<>();
        addNamesToList();
        firstColumn = view.findViewById(R.id.name_column);
        secondColumn = view.findViewById(R.id.stats_column);

        TextView myTeamTv = view.findViewById(R.id.my_team_name_tv);
        myTeamTv.setText(matchStats.getMyTeam());
        TextView opponentTv = view.findViewById(R.id.opponent_name_tv);
        opponentTv.setText(matchStats.getOpponent());

        setTextView(R.id.myTeam_made_ft_tv, matchStats.getShotsMadeFt());
        setTextView(R.id.my_team_missed_ft_tv, matchStats.getShotsMissedFt());
        setTextView(R.id.myTeam_taken_ft_tv, matchStats.getShotsTakenFt());
        setTextView(R.id.myTeam_made_two_tv, matchStats.getShotsMadeTwo());
        setTextView(R.id.my_team_missed_two_tv, matchStats.getShotsMissedTwo());
        setTextView(R.id.myTeam_taken_two_tv, matchStats.getShotsTakenTwo());
        setTextView(R.id.myTeam_made_three_tv, matchStats.getShotsMadeThree());
        setTextView(R.id.my_team_missed_three_tv, matchStats.getShotsMissedThree());
        setTextView(R.id.myTeam_taken_three_tv, matchStats.getShotsTakenThree());

        setTextView(R.id.opponent_made_ft_tv, matchStats.getOpponentShotsMadeFt());
        setTextView(R.id.opponent_missed_ft_tv, matchStats.getOpponentShotsMissedFt());
        setTextView(R.id.opponent_taken_ft_tv, matchStats.getOpponentShotsTakenFt());
        setTextView(R.id.opponent_made_two_tv, matchStats.getOpponentShotsMadeTwo());
        setTextView(R.id.opponent_missed_two_tv, matchStats.getOpponentShotsMissedTwo());
        setTextView(R.id.opponent_taken_two_tv, matchStats.getOpponentShotsTakenTwo());
        setTextView(R.id.opponent_made_three_tv, matchStats.getOpponentShotsMadeThree());
        setTextView(R.id.opponent_missed_three_tv, matchStats.getOpponentShotsMissedThree());
        setTextView(R.id.opponent_taken_three_tv, matchStats.getOpponentShotsTakenThree());

        setTextView(R.id.offensive_rebounds_num_tv, matchStats.getOffensiveRebounds());
        setTextView(R.id.defensive_rebounds_num_tv, matchStats.getDefensiveRebounds());
        setTextView(R.id.blocks_number_tv, matchStats.getBlocks());
        setTextView(R.id.steals_number_tv, matchStats.getSteals());
        setTextView(R.id.assists_num_tv, matchStats.getAssists());
        setTextView(R.id.turnover_num_tv, matchStats.getTurnovers());
        setTextView(R.id.fouls_number_tv, matchStats.getFouls());

        setTextView(R.id.my_team_score_tv, matchStats.getMyTeamScore());
        setTextView(R.id.opponent_score_tv, matchStats.getOpponentScore());

        Button goToHomePageBut = view.findViewById(R.id.go_home);
        goToHomePageBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goHome();
            }
        });
    }

    private void setTextView(int id, int number) {
        TextView textView = view.findViewById(id);
        textView.setText(String.valueOf(number));
    }

    private void addNamesToList() {
        columnNames.add("PTS");
        columnNames.add("FGM");
        columnNames.add("FGA");
        columnNames.add("FG%");
        columnNames.add("3PM");
        columnNames.add("3PA");
        columnNames.add("3P%");
        columnNames.add("FTM");
        columnNames.add("FTA");
        columnNames.add("FT%");
        columnNames.add("OREB");
        columnNames.add("DREB");
        columnNames.add("REB");
        columnNames.add("AST");
        columnNames.add("TOV");
        columnNames.add("STL");
        columnNames.add("BLK");
        columnNames.add("FL");
    }
}