package com.example.pregame.Stats;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pregame.HomePage.CoachHomeActivity;
import com.example.pregame.Model.MatchStats;
import com.example.pregame.Model.Team;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GameStatsFragment extends Fragment {
    private View view;
    private ArrayList<MatchStats> matchStats;
    private Team currentTeam;
    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration listenerRegistration;
    private GameStatsAdapter gameStatsAdapter;
    private String teamDoc;

    public GameStatsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_game_stats, container, false);

        currentTeam = CoachHomeActivity.currentTeam;
        firebaseFirestore = FirebaseFirestore.getInstance();
        getTeamDoc();

        return view;
    }

    private void getTeamDoc() {
        firebaseFirestore.collection("team").whereEqualTo("teamName", currentTeam.getTeamName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                teamDoc = queryDocumentSnapshot.getId();
                                buildRecyclerView();
                            }
                        }
                    }
                });
    }

    private void buildRecyclerView() {
        matchStats = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.match_stats_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        gameStatsAdapter = new GameStatsAdapter(matchStats, getContext(), getFragmentManager(), teamDoc);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(gameStatsAdapter);
        populateMatchStats();
    }

    private void populateMatchStats() {
        Query query = firebaseFirestore.collection("team").document(teamDoc).collection("match_stats").orderBy("date", Query.Direction.ASCENDING);

        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        MatchStats matchStat = change.getDocument().toObject(MatchStats.class).withId(change.getDocument().getId());
                        matchStats.add(matchStat);
                        gameStatsAdapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();
            }
        });
    }
}