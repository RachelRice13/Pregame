package com.example.pregame.HomePage;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.Model.MatchTraining;
import com.example.pregame.R;
import com.example.pregame.TrainingMatch.MatchTrainingAdapter;
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
    private ArrayList<MatchTraining> matchTrainings;
    private MatchTrainingAdapter matchTrainingAdapter;

    public void getTeamDoc(View view, String teamName, String type) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("team").whereEqualTo("teamName", teamName).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                String teamDoc = queryDocumentSnapshot.getId();
                                buildRecyclerView(view, teamDoc, type);
                            }
                        }
                    }
                });
    }

    public void buildRecyclerView(View view, String teamDoc, String type) {
        matchTrainings = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.training_match_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        matchTrainingAdapter = new MatchTrainingAdapter(matchTrainings, getContext(), teamDoc, getFragmentManager(), type);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(matchTrainingAdapter);
        populateList(teamDoc);
    }

    public void populateList(String teamDoc) {
        Query query = firebaseFirestore.collection("team").document(teamDoc).collection("training_match").orderBy("date", Query.Direction.ASCENDING);
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        MatchTraining matchTraining = change.getDocument().toObject(MatchTraining.class).withId(change.getDocument().getId());
                        matchTrainings.add(matchTraining);
                        matchTrainingAdapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();
            }
        });
    }
}