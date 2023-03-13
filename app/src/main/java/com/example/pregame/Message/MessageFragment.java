package com.example.pregame.Message;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pregame.CoachHomeActivity;
import com.example.pregame.Model.Coach;
import com.example.pregame.Model.Player;
import com.example.pregame.Model.Team;
import com.example.pregame.PlayerHomeActivity;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {
    public static final String TAG = "MessageFragment";
    private View view;
    private List<String> teamMembersList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TeamMembersAdapter adapter;

    public MessageFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);

        buildRecyclerView();
        getTeamMembers();

        return view;
    }

    private void getTeamMembers() {
        Team team;
        if (PlayerHomeActivity.userType.equals("Player")) {
            team = PlayerHomeActivity.currentTeam;
        } else {
            team = CoachHomeActivity.currentTeam;
        }

        addToList(team.getPlayers(), "Player");
        addToList(team.getCoaches(), "Coach");

    }

    private void addToList(ArrayList<DocumentReference> docRefArrayList, String teamMemberType) {
        for (int i = 0; i < docRefArrayList.size(); i++) {
            DocumentReference reference = docRefArrayList.get(i);
            reference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (teamMemberType.equals("Player")) {
                                Player player = documentSnapshot.toObject(Player.class);
                                String playerName = player.getFirstName() + " " + player.getSurname();
                                teamMembersList.add(playerName);
                            } else {
                                Coach coach = documentSnapshot.toObject(Coach.class);
                                String coachName = coach.getFirstName() + " " + coach.getSurname();
                                teamMembersList.add(coachName);
                            }
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

    private void buildRecyclerView() {
        teamMembersList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.team_members_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        adapter = new TeamMembersAdapter(teamMembersList, getContext(), getFragmentManager());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}