package com.example.pregame.Stats;

import android.graphics.Typeface;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pregame.Model.Attendance;
import com.example.pregame.Model.IndividualStats;
import com.example.pregame.Model.MatchStats;
import com.example.pregame.Model.MatchTraining;
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

import java.util.ArrayList;

public class SelectPlayersFragment extends Fragment {
    public static final String TAG = "SelectPlayersFragment";
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private MatchStats matchStats;
    private String teamDoc;
    private FragmentTransaction transaction;
    private LinearLayout playerTable;
    private ArrayList<DocumentReference> selectedPlayers;

    public SelectPlayersFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_players, container, false);

        hideNavigation(View.GONE);
        setup();
        setNumberOfPlayers();
        getPlayerDetails();
        setTableRow("Player Name", "Availability", true, false, null);

        return view;
    }

    private void hideNavigation(int visibility) {
        MaterialToolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(visibility);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(visibility);
    }

    private void getPlayerDetails() {
        firebaseFirestore.collection("team").document(teamDoc).collection("training_match").whereEqualTo("id", matchStats.getId()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                MatchTraining matchTraining = documentSnapshot.toObject(MatchTraining.class);

                                for (Attendance attendance : matchTraining.getAttendance()) {
                                    if (attendance.getType().equals("player")) {
                                        DocumentReference user = attendance.getUser();
                                        getPlayerFullName(user, attendance.getResponse());
                                    }
                                }
                            }
                        } else {
                            Log.e(TAG, "No match with that id");
                        }
                    }
                });
    }

    private void getPlayerFullName(DocumentReference user, String response) {
        firebaseFirestore.collection("player").document(user.getId()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User player = documentSnapshot.toObject(User.class);
                        String fullName = player.getFirstName() + " " + player.getSurname();
                        setTableRow(fullName, response, false, true, user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to get player's full name.", e);
                    }
                });
    }

    private void setTableRow(String columnOne, String columnTwo, boolean bold, boolean checkbox, DocumentReference user) {
        LinearLayout rowLayout = new LinearLayout(getContext());

        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rowLayout.setLayoutParams(layoutParams);
        rowLayout.setPadding(1, 1, 1, 2);
        rowLayout.setBackgroundColor(getResources().getColor(R.color.black));

        LinearLayout firstColumn = createColumn(columnOne, bold);
        LinearLayout secondColumn = createColumn(columnTwo, bold);
        LinearLayout thirdColumn = createLastColumn(checkbox, user);

        rowLayout.addView(firstColumn);
        rowLayout.addView(secondColumn);
        rowLayout.addView(thirdColumn);
        playerTable.addView(rowLayout);
    }

    private LinearLayout createColumn(String text, boolean bold) {
        LinearLayout column = new LinearLayout(getContext());
        LinearLayout.LayoutParams columnLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2.0f);
        columnLayoutParams.setMargins(3, 0, 0, 0);
        column.setLayoutParams(columnLayoutParams);

        TextView textView = new TextView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 48);
        textView.setBackgroundColor(getResources().getColor(R.color.white));
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(10, 0, 10, 0);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(text);

        if (bold)
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

        textView.setLayoutParams(params);
        column.addView(textView);

        return column;
    }

    private LinearLayout createLastColumn(boolean checkbox, DocumentReference user) {
        LinearLayout column = new LinearLayout(getContext());
        LinearLayout.LayoutParams columnLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.1f);
        columnLayoutParams.setMargins(3, 0, 0, 0);
        column.setBackgroundColor(getResources().getColor(R.color.white));
        column.setLayoutParams(columnLayoutParams);

        CheckBox checkBox = new CheckBox(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 48);
        checkBox.setPadding(0,1,0,1);
        checkBox.setLayoutParams(params);

        if(checkbox) {
            column.addView(checkBox);
            selectPlayerCheckbox(checkBox, user);
        } else {
            checkBox.setVisibility(View.INVISIBLE);
            column.addView(checkBox);
        }

        return column;
    }

    private void selectPlayerCheckbox(CheckBox checkBox, DocumentReference user) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    selectedPlayers.add(user);
                } else {
                    selectedPlayers.remove(user);
                }
                setNumberOfPlayers();
            }
        });
    }

    private void setNumberOfPlayers() {
        TextView numberOfPlayersTv = view.findViewById(R.id.number_of_players_selected);
        String numOfPlayers = "Number of players selected: " + selectedPlayers.size();
        numberOfPlayersTv.setText(numOfPlayers);
    }

    private void startMatch() {
        if (selectedPlayers.size() >= 0) {
            ArrayList<IndividualStats> players = new ArrayList<>();
            for (DocumentReference reference : selectedPlayers) {
                IndividualStats individualStats = new IndividualStats(reference, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                players.add(individualStats);
            }

            updateFireStore(players);
        } else {
            Toast.makeText(getContext(), "You must select at least 3 players.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFireStore(ArrayList<IndividualStats> players) {
        matchStats.setPlayers(players);

        firebaseFirestore.collection("team").document(teamDoc).collection("match_stats").document(matchStats.StatsId).set(matchStats)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e(TAG, "Updated match stats player array");
                        CreateMatchStatsFragment createMatchStatsFragment = new CreateMatchStatsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("teamDoc", teamDoc);
                        bundle.putString("matchStatId", matchStats.StatsId);
                        bundle.putSerializable("matchStats", matchStats);
                        createMatchStatsFragment.setArguments(bundle);
                        transaction.replace(R.id.container, createMatchStatsFragment).commit();
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
        teamDoc = bundle.getString("teamDoc");
        transaction = getFragmentManager().beginTransaction();
        selectedPlayers = new ArrayList<>();

        playerTable = view.findViewById(R.id.player_table);
        TextView opponentNameTv = view.findViewById(R.id.opponent_name_tv);
        opponentNameTv.setText(matchStats.getOpponent());

        Button startMatch = view.findViewById(R.id.start_match_button);
        startMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMatch();
            }
        });

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideNavigation(View.VISIBLE);
                transaction.replace(R.id.container, new GameStatsFragment()).commit();
            }
        });
    }
}