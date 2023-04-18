package com.example.pregame.Stats;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pregame.HomePage.CoachHomeActivity;
import com.example.pregame.Model.MatchStats;
import com.example.pregame.Model.Team;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class GameStatsFragment extends Fragment {
    private View view;
    private String date;
    private ArrayList<MatchStats> matchStats;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MatchStatsAdapter matchStatsAdapter;
    private Team currentTeam;
    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration listenerRegistration;

    public GameStatsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_game_stats, container, false);

        currentTeam = CoachHomeActivity.currentTeam;
        firebaseFirestore = FirebaseFirestore.getInstance();
        buildRecyclerView();

        Button startNewMatch = view.findViewById(R.id.start_new_match_button);
        startNewMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewMatch();
            }
        });

        return view;
    }

    public void populateMatchStats() {
        firebaseFirestore.collection("team").whereEqualTo("teamName", currentTeam.getTeamName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                String teamDoc = queryDocumentSnapshot.getId();
                                Query query = firebaseFirestore.collection("team").document(teamDoc).collection("matchStats").orderBy("date", Query.Direction.ASCENDING);

                                listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        for (DocumentChange change : value.getDocumentChanges()) {
                                            if (change.getType() == DocumentChange.Type.ADDED) {
                                                MatchStats matchStat = change.getDocument().toObject(MatchStats.class).withId(change.getDocument().getId());
                                                matchStats.add(matchStat);
                                                matchStatsAdapter.notifyDataSetChanged();
                                            }
                                        }
                                        listenerRegistration.remove();
                                    }
                                });
                            }
                        }
                    }
                });
    }

    public void buildRecyclerView() {
        matchStats = new ArrayList<>();
        recyclerView = view.findViewById(R.id.match_stats_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        matchStatsAdapter = new MatchStatsAdapter(matchStats, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(matchStatsAdapter);
        populateMatchStats();
    }

    public void startNewMatch() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.new_match_stats, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        EditText opponentNameEt = view.findViewById(R.id.opponent_et);
        TextInputLayout opponentLo = view.findViewById(R.id.opponent);
        TextView dateTv = view.findViewById(R.id.date_tv);
        FloatingActionButton goBack = view.findViewById(R.id.go_back);
        Button startMatch = view.findViewById(R.id.create_new_team_button);

        alertDialog.setCancelable(false).setView(view);
        AlertDialog alert = alertDialog.create();
        alert.show();

        setDatePicker(dateTv);

        startMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String opponent = opponentNameEt.getText().toString();

                boolean validOpponent = validateBlank(opponent, opponentLo);
                boolean validDate = validateDate();

                if (validOpponent && validDate) {
                    CreateMatchStatsFragment createMatchStatsFragment = new CreateMatchStatsFragment();
                    Bundle data = new Bundle();
                    data.putString("Opponent", opponent);
                    data.putString("Date", date);
                    createMatchStatsFragment.setArguments(data);

                    getFragmentManager().beginTransaction().replace(R.id.container, createMatchStatsFragment).commit();
                    alert.cancel();
                }
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
    }

    private void setDatePicker(TextView dateTv) {
        dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int DAY = calendar.get(Calendar.DATE);
                int MONTH = calendar.get(Calendar.MONTH);
                int YEAR = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        month += 1;
                        dateTv.setText(dayOfMonth + "/" + month + "/" + year);
                        date = dayOfMonth + "/" + month + "/" + year;
                    }
                }, YEAR, MONTH, DAY);
                datePickerDialog.show();
            }
        });
    }

    public boolean validateBlank(String text, TextInputLayout layout) {
        if (text.isEmpty()) {
            layout.setError("This is Required");
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

    public boolean validateDate() {
        if (date == null) {
            Log.e("DATE", "Date is required");
            return false;
        } else {
            return true;
        }
    }
}