package com.example.pregame.Stats;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.Model.MatchStats;
import com.example.pregame.R;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GameStatsAdapter extends RecyclerView.Adapter<GameStatsAdapter.ExampleViewHolder> {
    public static final String TAG = "GameStatsAdapter";
    private List<MatchStats> matchStats;
    private Context context;
    private FragmentManager fragmentManager;
    private String teamDoc;

    public GameStatsAdapter(ArrayList<MatchStats> matchStats, Context context, FragmentManager fragmentManager, String teamDoc) {
        this.matchStats = matchStats;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.teamDoc = teamDoc;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder  {
        TextView title, opponent, month, date, dayOfTheWeek;
        ImageView colourIv;
        MaterialCardView cardView;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_name);
            opponent = itemView.findViewById(R.id.start_time);
            month = itemView.findViewById(R.id.month_tv);
            date = itemView.findViewById(R.id.date_tv);
            dayOfTheWeek = itemView.findViewById(R.id.day_of_the_week_tv);
            colourIv = itemView.findViewById(R.id.training_match_colour);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }

    @NonNull
    @Override
    public GameStatsAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_layout_training_match, parent, false);
        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameStatsAdapter.ExampleViewHolder holder, int position) {
        MatchStats matchStat = matchStats.get(position);

        holder.title.setText(matchStat.getTitle());
        holder.opponent.setText("vs " + matchStat.getOpponent());
        holder.colourIv.setBackgroundColor(Color.parseColor("#005A9C"));

        Date date = new Date();
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(matchStat.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.month.setText(new SimpleDateFormat("MMM", Locale.ENGLISH).format(date));
        holder.date.setText(new SimpleDateFormat("d", Locale.ENGLISH).format(date));
        holder.dayOfTheWeek.setText(new SimpleDateFormat("EEE", Locale.ENGLISH).format(date));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectPlayersFragment selectPlayersFragment = new SelectPlayersFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("matchStats", matchStat);
                bundle.putString("teamDoc", teamDoc);
                selectPlayersFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, selectPlayersFragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return matchStats.size();
    }
}