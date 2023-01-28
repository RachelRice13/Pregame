package com.example.pregame.TrainingMatch;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pregame.CoachHomeActivity;
import com.example.pregame.Model.Match;
import com.example.pregame.Model.Team;
import com.example.pregame.PlayerHomeActivity;
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

public class TrainingMatchFragment extends Fragment {
    public static final String TAG = "TrainingMatch";
    private View view;
    private ArrayList<Match> matches;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MatchAdapter matchAdapter;
    private TextView teamNameTv;
    private Team team;
    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration listenerRegistration;

    public TrainingMatchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_training_match, container, false);

        teamNameTv = view.findViewById(R.id.team_name);
        firebaseFirestore = FirebaseFirestore.getInstance();

        getTeamDetails();
        buildMatchRecyclerView();

        Button addMatch = view.findViewById(R.id.add_match_button);
        addMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMatch.newInstance().show(getParentFragmentManager(), AddMatch.TAG);
            }
        });

        return view;
    }

    public void getTeamDetails() {
        if (PlayerHomeActivity.userType.equals("Player")) {
            team = PlayerHomeActivity.currentTeam;
        } else {
            team = CoachHomeActivity.currentTeam;
        }

        String teamName = team.getTeamName();
        teamNameTv.setText(teamName);
    }

    public void populateMatches() {
        firebaseFirestore.collection("team").whereEqualTo("teamName", team.getTeamName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                String teamDoc = queryDocumentSnapshot.getId();

                                Query query = firebaseFirestore.collection("team").document(teamDoc).collection("match").orderBy("date", Query.Direction.ASCENDING);

                                listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        for (DocumentChange change : value.getDocumentChanges()) {
                                            if (change.getType() == DocumentChange.Type.ADDED) {
                                                Match match = change.getDocument().toObject(Match.class).withId(change.getDocument().getId());
                                                matches.add(match);
                                                matchAdapter.notifyDataSetChanged();
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

    public void buildMatchRecyclerView() {
        matches = new ArrayList<>();
        recyclerView = view.findViewById(R.id.match_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        matchAdapter = new MatchAdapter(matches, getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(matchAdapter);
        populateMatches();
    }
}