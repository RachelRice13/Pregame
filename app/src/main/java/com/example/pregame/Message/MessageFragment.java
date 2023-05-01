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

import com.example.pregame.HomePage.CoachHomeActivity;
import com.example.pregame.Model.Team;
import com.example.pregame.HomePage.PlayerHomeActivity;
import com.example.pregame.Model.User;
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
    private List<User> teamMembersList;
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
                            User user = documentSnapshot.toObject(User.class).withId(documentSnapshot.getId());
                            teamMembersList.add(user);
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
        RecyclerView recyclerView = view.findViewById(R.id.team_members_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        adapter = new TeamMembersAdapter(teamMembersList, getContext(), getFragmentManager());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}