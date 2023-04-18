package com.example.pregame.Team;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pregame.HomePage.CoachHomeActivity;
import com.example.pregame.Model.Player;
import com.example.pregame.Model.Team;
import com.example.pregame.HomePage.PlayerHomeActivity;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class TeamFragment extends Fragment {
    public static final String TAG = "TeamInfo";
    private View view;
    private ArrayList<Player> players;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PlayerListAdapter adapter;
    private TextView teamNameTv;
    private Team team;

    public TeamFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_team, container, false);

        teamNameTv = view.findViewById(R.id.team_name);

        buildRecyclerView();
        getTeamDetails();

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

        ArrayList<DocumentReference> playersDR = team.getPlayers();
        for (int i = 0; i < playersDR.size(); i++) {
            DocumentReference reference = playersDR.get(i);
            reference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Player player = documentSnapshot.toObject(Player.class);
                            players.add(player);
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    });
        }

    }

    public void buildRecyclerView() {
        players = new ArrayList<>();
        recyclerView = view.findViewById(R.id.team_list_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        adapter = new PlayerListAdapter(players, getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}