package com.example.pregame.TrainingMatch;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.pregame.HomePage.CoachHomeActivity;
import com.example.pregame.Model.MatchTraining;
import com.example.pregame.Model.Team;
import com.example.pregame.HomePage.PlayerHomeActivity;
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
    private ArrayList<MatchTraining> matchTrainings;
    private MatchTrainingAdapter matchTrainingAdapter;
    private RecyclerView recyclerView;
    private TextView teamNameTv;
    private Team team;
    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration listenerRegistration;
    private String teamDoc;

    public TrainingMatchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_training_match, container, false);

        teamNameTv = view.findViewById(R.id.team_name);
        firebaseFirestore = FirebaseFirestore.getInstance();

        setToolbarIcon();
        getTeamDetails();

        return view;
    }

    private void setToolbarIcon() {
        ImageView toolbarIcon = getActivity().findViewById(R.id.toolbar_end_icon);
        toolbarIcon.setVisibility(View.VISIBLE);
        toolbarIcon.setImageResource(R.drawable.ic_menu);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        toolbarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(getContext(), toolbarIcon);
                menu.getMenuInflater().inflate(R.menu.match_training_menu, menu.getMenu());

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.nav_add_match) {
                            transaction.replace(R.id.container, new AddMatchFragment()).commit();
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.nav_add_training) {
                            transaction.replace(R.id.container, new AddTrainingFragment()).commit();
                            return true;
                        }
                        return false;
                    }
                });
                menu.show();
            }
        });
    }

    private void getTeamDetails() {
        if (PlayerHomeActivity.userType.equals("Player")) {
            team = PlayerHomeActivity.currentTeam;
        } else {
            team = CoachHomeActivity.currentTeam;
        }
        String teamName = team.getTeamName();
        teamNameTv.setText(teamName);

        firebaseFirestore.collection("team").whereEqualTo("teamName", team.getTeamName()).get()
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
        matchTrainings = new ArrayList<>();
        recyclerView = view.findViewById(R.id.training_match_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        matchTrainingAdapter = new MatchTrainingAdapter(matchTrainings, getContext(), teamDoc);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MatchTrainingTouchHelper(matchTrainingAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(matchTrainingAdapter);
        populateList();
    }

    public void populateList() {
        firebaseFirestore.collection("team").whereEqualTo("teamName", team.getTeamName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                String teamDoc = queryDocumentSnapshot.getId();
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
                    }
                });
    }
}