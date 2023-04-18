package com.example.pregame.HomePage;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.Model.Match;
import com.example.pregame.Model.Team;
import com.example.pregame.Model.Training;
import com.example.pregame.R;
import com.example.pregame.TrainingMatch.MatchAdapter;
import com.example.pregame.TrainingMatch.TrainingAdapter;
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

public class HomeFragmentTemplate extends Fragment {
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration listenerRegistration;
    private ArrayList<Match> matches;
    private ArrayList<Training> trainings;
    private MatchAdapter matchAdapter;
    private TrainingAdapter trainingAdapter;

    public void choose(View mainView) {
        LinearLayout matchLL = mainView.findViewById(R.id.match_ll);
        LinearLayout trainingLL = mainView.findViewById(R.id.training_ll);

        Button matchBut = mainView.findViewById(R.id.view_matches_button);
        matchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matchLL.setVisibility(View.VISIBLE);
                trainingLL.setVisibility(View.INVISIBLE);
                buildRecyclerView(mainView, R.id.matches_rv, "match");
            }
        });

        Button trainingBut = mainView.findViewById(R.id.view_trainings_button);
        trainingBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matchLL.setVisibility(View.INVISIBLE);
                trainingLL.setVisibility(View.VISIBLE);
            }
        });
    }

    public void buildRecyclerView(View view, int recyclerviewId, String type) {
        RecyclerView recyclerView = view.findViewById(recyclerviewId);
        recyclerView.setHasFixedSize(true);

        if (type.equals("training")) {
            trainings = new ArrayList<>();
            trainingAdapter = new TrainingAdapter(trainings, getContext());
            recyclerView.setAdapter(trainingAdapter);
        } else {
            matches = new ArrayList<>();
            matchAdapter = new MatchAdapter(matches, getContext());
            recyclerView.setAdapter(matchAdapter);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        populateList(type);
    }

    public void populateList(String type) {
        Team team;
        if (PlayerHomeActivity.userType.equals("Player")) {
            team = PlayerHomeActivity.currentTeam;
        } else {
            team = CoachHomeActivity.currentTeam;
        }

        firebaseFirestore.collection("team").whereEqualTo("teamName", team.getTeamName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                String teamDoc = queryDocumentSnapshot.getId();
                                Query query = firebaseFirestore.collection("team").document(teamDoc).collection(type).orderBy("date", Query.Direction.ASCENDING);
                                listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        for (DocumentChange change : value.getDocumentChanges()) {
                                            if (change.getType() == DocumentChange.Type.ADDED) {
                                                if (type.equals("training")) {
                                                    Training training = change.getDocument().toObject(Training.class).withId(change.getDocument().getId());
                                                    trainings.add(training);
                                                    trainingAdapter.notifyDataSetChanged();
                                                } else {
                                                    Match match = change.getDocument().toObject(Match.class).withId(change.getDocument().getId());
                                                    matches.add(match);
                                                    matchAdapter.notifyDataSetChanged();
                                                }
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
}