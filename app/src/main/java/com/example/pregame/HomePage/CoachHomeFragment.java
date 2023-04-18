package com.example.pregame.HomePage;

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
import android.widget.LinearLayout;

import com.example.pregame.Model.Match;
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

public class CoachHomeFragment extends Fragment {
    private View view;
    private LinearLayout matchLL, trainingLL;
    private Button matchBut, trainingBut;
    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration listenerRegistration;
    private ArrayList<Match> matches;
    private ArrayList<Training> trainings;
    private RecyclerView recyclerView, trainingRecyclerView;
    private RecyclerView.LayoutManager layoutManager, trainingLayoutManager;
    private MatchAdapter matchAdapter;
    private TrainingAdapter trainingAdapter;

    public CoachHomeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_coach_home, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        matchBut = view.findViewById(R.id.view_matches_button);
        trainingBut = view.findViewById(R.id.view_trainings_button);
        matchLL = view.findViewById(R.id.match_ll);
        trainingLL = view.findViewById(R.id.training_ll);

        buildTrainingRecyclerView();
        choose();

        return view;
    }

    public void populateMatches() {
        firebaseFirestore.collection("team").whereEqualTo("teamName", CoachHomeActivity.currentTeam.getTeamName()).get()
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

    public void populateTrainings() {
        firebaseFirestore.collection("team").whereEqualTo("teamName", CoachHomeActivity.currentTeam.getTeamName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                String teamDoc = queryDocumentSnapshot.getId();

                                Query query = firebaseFirestore.collection("team").document(teamDoc).collection("training").orderBy("date", Query.Direction.ASCENDING);

                                listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        for (DocumentChange change : value.getDocumentChanges()) {
                                            if (change.getType() == DocumentChange.Type.ADDED) {
                                                Training training = change.getDocument().toObject(Training.class).withId(change.getDocument().getId());
                                                trainings.add(training);
                                                trainingAdapter.notifyDataSetChanged();
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
        recyclerView = view.findViewById(R.id.matches_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        matchAdapter = new MatchAdapter(matches, getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(matchAdapter);
        populateMatches();
    }

    public void buildTrainingRecyclerView() {
        trainings = new ArrayList<>();
        trainingRecyclerView = view.findViewById(R.id.training_rv);
        trainingRecyclerView.setHasFixedSize(true);
        trainingLayoutManager = new LinearLayoutManager(view.getContext());
        trainingAdapter = new TrainingAdapter(trainings, getContext());
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TrainingTouchHelper(trainingAdapter));
//        itemTouchHelper.attachToRecyclerView(trainingRecyclerView);

        trainingRecyclerView.setLayoutManager(trainingLayoutManager);
        trainingRecyclerView.setAdapter(trainingAdapter);
        populateTrainings();
    }

    public void choose() {
        matchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matchLL.setVisibility(View.VISIBLE);
                trainingLL.setVisibility(View.INVISIBLE);
                buildMatchRecyclerView();
            }
        });

        trainingBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matchLL.setVisibility(View.INVISIBLE);
                trainingLL.setVisibility(View.VISIBLE);
            }
        });
    }
}