package com.example.pregame.Stats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.Model.MatchStats;
import com.example.pregame.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MatchStatsAdapter extends RecyclerView.Adapter<MatchStatsAdapter.ExampleViewHolder> {
    public static final String TAG = "MatchStatsAdapter";
    private List<MatchStats> matchStats;
    private Context context;
    private FirebaseFirestore firebaseFirestore;

    public MatchStatsAdapter (List<MatchStats> matchStats, Context context) {
        this.matchStats = matchStats;
        this.context = context;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder  {
        TextView title, date, score;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.match_stats_title);
            date = itemView.findViewById(R.id.match_stats_date);
            score = itemView.findViewById(R.id.match_stats_score);
        }
    }

    @NonNull
    @Override
    public MatchStatsAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.match_stats_row_layout, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();

        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchStatsAdapter.ExampleViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MatchStats matchStat = matchStats.get(position);
        String matchTitle = matchStat.getMyTeam() + " vs " + matchStat.getOpponent();
        holder.title.setText(matchTitle);
        holder.date.setText(matchStat.getDate());
        String matchScore = "Score: " + matchStat.getMyTeamScore() + ":" + matchStat.getOpponentScore();
        holder.score.setText(matchScore);
    }

    @Override
    public int getItemCount() {
        return matchStats.size();
    }
}
