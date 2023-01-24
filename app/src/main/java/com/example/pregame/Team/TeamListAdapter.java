package com.example.pregame.Team;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.Model.Team;
import com.example.pregame.R;

import java.util.List;

public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.ExampleViewHolder> {
    private List<Team> teams;
    private Context context;

    public TeamListAdapter(List<Team> teams, Context context) {
        this.teams = teams;
        this.context = context;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView teamNameTv;
        TextView teamCodeTv;
        TextView numOfPeopleTv;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            teamNameTv = itemView.findViewById(R.id.tl_team_name);
            teamCodeTv = itemView.findViewById(R.id.tl_team_code);
            numOfPeopleTv = itemView.findViewById(R.id.tl_num_of_people);
        }
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.team_list_row_layout, parent, false);

        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        Team team = teams.get(position);
        int numOfPeople = team.getPlayers().size() + team.getCoaches().size();
        holder.teamNameTv.setText(team.getTeamName());
        holder.teamCodeTv.setText(team.getTeamCode());
        holder.numOfPeopleTv.setText(Integer.toString(numOfPeople));
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

}