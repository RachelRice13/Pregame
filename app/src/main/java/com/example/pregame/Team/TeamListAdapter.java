package com.example.pregame.Team;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.HomePage.CoachHomeActivity;
import com.example.pregame.Model.Team;
import com.example.pregame.HomePage.PlayerHomeActivity;
import com.example.pregame.R;
import com.google.android.material.card.MaterialCardView;

import java.io.Serializable;
import java.util.List;

public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.ExampleViewHolder> {
    public static final String TEAM = "Team";
    private List<Team> teams;
    private Context context;

    public TeamListAdapter(List<Team> teams, Context context) {
        this.teams = teams;
        this.context = context;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView teamNameTv;
        TextView teamCodeTv;
        TextView numOfPeopleTv;
        MaterialCardView cardView;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            teamNameTv = itemView.findViewById(R.id.tl_team_name);
            teamCodeTv = itemView.findViewById(R.id.tl_team_code);
            numOfPeopleTv = itemView.findViewById(R.id.tl_num_of_people);
            cardView = itemView.findViewById(R.id.team_card_view);
        }

        @Override
        public void onClick(View view) {
            int position = this.getLayoutPosition();
            Team team = teams.get(position);

            Intent intent = new Intent(view.getContext(), TeamFragment.class);
            intent.putExtra(TEAM, (Serializable) team);
            view.getContext().startActivity(intent);
        }
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.team_list_row_layout, parent, false);

        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Team team = teams.get(position);
        int numOfPeople = team.getPlayers().size() + team.getCoaches().size();
        holder.teamNameTv.setText(team.getTeamName());
        holder.teamCodeTv.setText(team.getTeamCode());
        holder.numOfPeopleTv.setText(Integer.toString(numOfPeople));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Team team = teams.get(position);
                PlayerHomeActivity.currentTeam = team;
                CoachHomeActivity.currentTeam = team;

                if (PlayerHomeActivity.userType.equals("Player")) {
                    Intent playerIntent = new Intent(view.getContext(), PlayerHomeActivity.class);
                    view.getContext().startActivity(playerIntent);
                } else {
                    Intent coachIntent = new Intent(view.getContext(), CoachHomeActivity.class);
                    view.getContext().startActivity(coachIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

}