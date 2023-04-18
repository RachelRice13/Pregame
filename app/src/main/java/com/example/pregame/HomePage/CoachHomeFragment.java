package com.example.pregame.HomePage;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pregame.R;

public class CoachHomeFragment extends HomeFragmentTemplate {

    public CoachHomeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coach_home, container, false);
        getTeamDoc(view, CoachHomeActivity.currentTeam.getTeamName());
        return view;
    }
}