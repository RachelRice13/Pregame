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

import com.example.pregame.Model.IndividualStats;
import com.example.pregame.Model.MatchStats;
import com.example.pregame.Model.StatLeaders;
import com.example.pregame.Model.Team;
import com.example.pregame.Model.User;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class IndividualStatsFragment extends Fragment {
    public static final String TAG = "IndividualStatsFragment";
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private String teamDoc;
    private LinearLayout firstColumn, secondColumn;
    private ArrayList<String> columnNames, statsList;
    private ArrayList<StatLeaders> pointsLeader, freeThrowLeader, twoPointLeader, threePointLeader, assistLeader, rebounderLeader, stealsLeader, blocksLeader, foulsLeader, turnoversLeader;
    private final int[] gamesPlayed = {0}, pointsScored = {0}, takenFT = {0}, takenTwo = {0}, takenThree = {0}, madeFT = {0}, madeTwo = {0}, madeThree = {0}, offReb = {0}, defReb = {0}, totalReb ={0}, assists = {0}, blocks = {0}, turnovers = {0}, steals = {0}, fouls = {0};

    public IndividualStatsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_individual_stats, container, false);

        setup();
        setNameColumn("Player");
        setSecondColumn(columnNames);
        getPlayerDetails();

        return view;
    }

    private void getPlayerDetails() {
        firebaseFirestore.collection("team").document(teamDoc).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Team team = task.getResult().toObject(Team.class);

                            for (int i = 0; i < team.getPlayers().size(); i++) {
                                DocumentReference reference = team.getPlayers().get(i);
                                getPlayerFullName(reference);
                            }
                        }
                    }
                });
    }

    private void getPlayerFullName(DocumentReference user) {
        firebaseFirestore.collection("player").document(user.getId()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User player = documentSnapshot.toObject(User.class);
                        String fullName = player.getFirstName() + " " + player.getSurname();
                        setNameColumn(fullName);
                        getMatchStats(user);
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

    private void getMatchStats(DocumentReference reference) {
        firebaseFirestore.collection("team").document(teamDoc).collection("match_stats").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MatchStats matchStats = document.toObject(MatchStats.class);

                                for (IndividualStats individualStats : matchStats.getPlayers()) {
                                    if (individualStats.getPlayer().equals(reference)) {
                                        calculateIndividualMatchStats(individualStats);
                                    }
                                }
                            }
                            addStatsToList(reference);
                            setSecondColumn(statsList);
                            setLeaders();
                            clear();
                            statsList.removeAll(statsList);

                        }
                    }
                });
    }

    private void addStatsToList(DocumentReference reference) {
        DecimalFormat df = new DecimalFormat("#.#");
        totalReb[0] = defReb[0] + offReb[0];
        double fieldGoalPerc = calculatePercent(takenTwo[0], madeTwo[0]);
        double threePerc = calculatePercent(takenThree[0], madeThree[0]);
        double freeThrowPerc = calculatePercent(takenFT[0], madeFT[0]);

        statsList.add(String.valueOf(gamesPlayed[0]));
        statsList.add(String.valueOf(pointsScored[0]));
        statsList.add(String.valueOf(madeTwo[0]));
        statsList.add(String.valueOf(takenTwo[0]));
        statsList.add(df.format(fieldGoalPerc));
        statsList.add(String.valueOf(madeThree[0]));
        statsList.add(String.valueOf(takenThree[0]));
        statsList.add(df.format(threePerc));
        statsList.add(String.valueOf(madeFT[0]));
        statsList.add(String.valueOf(takenFT[0]));
        statsList.add(df.format(freeThrowPerc));
        statsList.add(String.valueOf(offReb[0]));
        statsList.add(String.valueOf(defReb[0]));
        statsList.add(String.valueOf(totalReb[0]));
        statsList.add(String.valueOf(assists[0]));
        statsList.add(String.valueOf(turnovers[0]));
        statsList.add(String.valueOf(steals[0]));
        statsList.add(String.valueOf(blocks[0]));
        statsList.add(String.valueOf(fouls[0]));

        addToLeadersList(reference, pointsScored[0], pointsLeader);
        addToLeadersList(reference, madeFT[0], freeThrowLeader);
        addToLeadersList(reference, madeTwo[0], twoPointLeader);
        addToLeadersList(reference, madeThree[0], threePointLeader);
        addToLeadersList(reference, assists[0], assistLeader);
        addToLeadersList(reference, totalReb[0], rebounderLeader);
        addToLeadersList(reference, steals[0], stealsLeader);
        addToLeadersList(reference, blocks[0], blocksLeader);
        addToLeadersList(reference, fouls[0], foulsLeader);
        addToLeadersList(reference, turnovers[0], turnoversLeader);
    }

    private double calculatePercent(int taken, int made) {
        if (taken == 0)
            return 0;
        else
            return (double) made*100 / (double) taken;
    }

    private void calculateIndividualMatchStats(IndividualStats stats) {
        gamesPlayed[0] += 1;
        pointsScored[0] += stats.getPointsScored();
        madeTwo[0] += stats.getShotsMadeTwo();
        takenTwo[0] += stats.getShotsTakenTwo();
        madeThree[0] += stats.getShotsMadeThree();
        takenThree[0] += stats.getShotsTakenThree();
        madeFT[0] += stats.getShotsMadeFt();
        takenFT[0] += stats.getShotsTakenFt();
        offReb[0] += stats.getOffensiveRebounds();
        defReb[0] += stats.getDefensiveRebounds();
        assists[0] += stats.getAssists();
        turnovers[0] += stats.getTurnovers();
        steals[0] += stats.getSteals();
        blocks[0] += stats.getBlocks();
        fouls[0] += stats.getFouls();
    }

    private void clear() {
        madeTwo[0] = 0; takenTwo[0] = 0; madeThree[0] = 0; takenThree[0] = 0; madeFT[0] = 0; takenFT[0] = 0;
        gamesPlayed[0] = 0; pointsScored[0] = 0;assists[0] = 0; turnovers[0] = 0; steals[0] = 0; blocks[0] = 0; fouls[0] = 0; offReb[0] = 0; defReb[0] = 0; totalReb[0] = 0;
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
        createTextView(rowLayout, columnNames.get(18));

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

    private void addToLeadersList(DocumentReference reference, int number, ArrayList<StatLeaders> statLeadersList) {
        boolean contains = containsPlayer(reference, statLeadersList);

        if (!contains) {
            StatLeaders newStatsLeaderName = new StatLeaders(reference, number);
            statLeadersList.add(newStatsLeaderName);
        }

    }

    private boolean containsPlayer(DocumentReference reference, ArrayList<StatLeaders> statLeadersList) {
        for (StatLeaders statLeaders : statLeadersList) {
            if (statLeaders.getReference().equals(reference)) {
                return true;
            }
        }
        return false;
    }

    private void setLeaders() {
        sortLeaders(pointsLeader);
        getPlayersFullName(pointsLeader, R.id.leader_points_name_one, R.id.leader_points_number_one, R.id.leader_points_name_two, R.id.leader_points_number_two, R.id.leader_points_name_three, R.id.leader_points_number_three);

        sortLeaders(freeThrowLeader);
        getPlayersFullName(freeThrowLeader, R.id.leader_free_throws_name_one, R.id.leader_free_throws_number_one, R.id.leader_free_throws_name_two, R.id.leader_free_throws_number_two, R.id.leader_free_throws_name_three, R.id.leader_free_throws_number_three);

        sortLeaders(twoPointLeader);
        getPlayersFullName(twoPointLeader, R.id.leader_two_points_name_one, R.id.leader_two_points_number_one, R.id.leader_two_points_name_two, R.id.leader_two_points_number_two, R.id.leader_two_points_name_three, R.id.leader_two_points_number_three);

        sortLeaders(threePointLeader);
        getPlayersFullName(threePointLeader, R.id.leader_three_points_name_one, R.id.leader_three_points_number_one, R.id.leader_three_points_name_two, R.id.leader_three_points_number_two, R.id.leader_three_points_name_three, R.id.leader_three_points_number_three);

        sortLeaders(assistLeader);
        getPlayersFullName(assistLeader, R.id.leader_assist_name_one, R.id.leader_assist_number_one, R.id.leader_assist_name_two, R.id.leader_assist_number_two, R.id.leader_assist_name_three, R.id.leader_assist_number_three);

        sortLeaders(rebounderLeader);
        getPlayersFullName(rebounderLeader, R.id.leader_rebounds_name_one, R.id.leader_rebounds_number_one, R.id.leader_rebounds_name_two, R.id.leader_rebounds_number_two, R.id.leader_rebounds_name_three, R.id.leader_rebounds_number_three);

        sortLeaders(stealsLeader);
        getPlayersFullName(stealsLeader, R.id.leader_steals_name_one, R.id.leader_steals_number_one, R.id.leader_steals_name_two, R.id.leader_steals_number_two, R.id.leader_steals_name_three, R.id.leader_steals_number_three);

        sortLeaders(blocksLeader);
        getPlayersFullName(blocksLeader, R.id.leader_blocks_name_one, R.id.leader_blocks_number_one, R.id.leader_blocks_name_two, R.id.leader_blocks_number_two, R.id.leader_blocks_name_three, R.id.leader_blocks_number_three);

        sortLeaders(foulsLeader);
        getPlayersFullName(foulsLeader, R.id.leader_fouls_name_one, R.id.leader_fouls_number_one, R.id.leader_fouls_name_two, R.id.leader_fouls_number_two, R.id.leader_fouls_name_three, R.id.leader_fouls_number_three);

        sortLeaders(turnoversLeader);
        getPlayersFullName(turnoversLeader, R.id.leader_turnovers_name_one, R.id.leader_turnovers_number_one, R.id.leader_turnovers_name_two, R.id.leader_turnovers_number_two, R.id.leader_turnovers_name_three, R.id.leader_turnovers_number_three);
    }

    private void sortLeaders(ArrayList<StatLeaders> statLeadersList) {
        Collections.sort(statLeadersList, new Comparator<StatLeaders>() {
            public int compare(StatLeaders p1, StatLeaders p2) {
                return Integer.compare(p2.getNumber(), p1.getNumber());
            }
        });
    }

    private void getPlayersFullName(ArrayList<StatLeaders> statsList, int oneName, int oneNum, int twoName, int twoNum, int threeName, int threeNum) {
        for (StatLeaders statLeaders : statsList) {
            firebaseFirestore.collection("player").document(statLeaders.getReference().getId()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User player = documentSnapshot.toObject(User.class);
                            String fullName = player.getFirstName() + " " + player.getSurname();

                            if (statsList.indexOf(statLeaders) == 0)
                                setTextView(statsList.get(0), fullName, oneName, oneNum);
                            else if (statsList.indexOf(statLeaders) == 1)
                                setTextView(statsList.get(1), fullName, twoName, twoNum);
                            else if (statsList.indexOf(statLeaders) == 2)
                                setTextView(statsList.get(2), fullName, threeName, threeNum);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Failed to get player's full name.", e);
                        }
                    });
        }
    }

    private void setTextView(StatLeaders statLeaders, String fullName,  int nameId, int numId) {
        TextView oneNameTv = view.findViewById(nameId);
        TextView oneNumTv = view.findViewById(numId);
        oneNameTv.setText(fullName);
        oneNumTv.setText(String.valueOf(statLeaders.getNumber()));
    }

    private void setup() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Bundle bundle = getArguments();
        teamDoc = bundle.getString("teamDoc");
        Team team = (Team) bundle.getSerializable("myTeam");

        columnNames = new ArrayList<>();
        statsList = new ArrayList<>();
        pointsLeader = new ArrayList<>();
        freeThrowLeader = new ArrayList<>();
        twoPointLeader = new ArrayList<>();
        threePointLeader = new ArrayList<>();
        assistLeader = new ArrayList<>();
        rebounderLeader = new ArrayList<>();
        stealsLeader = new ArrayList<>();
        blocksLeader = new ArrayList<>();
        foulsLeader = new ArrayList<>();
        turnoversLeader = new ArrayList<>();

        TextView teamNameTv = view.findViewById(R.id.team_name);
        teamNameTv.setText(team.getTeamName());
        firstColumn = view.findViewById(R.id.name_column);
        secondColumn = view.findViewById(R.id.stats_column);

        addNamesToList();

        Button generalStats = view.findViewById(R.id.general_stats_button);
        generalStats.setOnClickListener(new View.OnClickListener() {
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

    private void addNamesToList() {
        columnNames.add("GP");
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