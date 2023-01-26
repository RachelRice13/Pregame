package com.example.pregame.HomePage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pregame.CoachHomeActivity;
import com.example.pregame.R;
import com.google.firebase.auth.FirebaseAuth;

public class CoachHomeFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    private View view;

    public CoachHomeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_coach_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        TextView currentUser = view.findViewById(R.id.current_user);
        currentUser.setText(firebaseAuth.getCurrentUser().getEmail());

        TextView userType = view.findViewById(R.id.user_type);
        userType.setText(CoachHomeActivity.userType);

        TextView teamName = view.findViewById(R.id.team_name);
        teamName.setText(CoachHomeActivity.currentTeam.getTeamName());

        return view;
    }
}