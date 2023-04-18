package com.example.pregame.HomePage;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pregame.R;

public class PlayerHomeFragment extends HomeFragmentTemplate {

    public PlayerHomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_home, container, false);

        buildRecyclerView(view, R.id.training_rv, "training");
        choose(view);

        return view;
    }
}