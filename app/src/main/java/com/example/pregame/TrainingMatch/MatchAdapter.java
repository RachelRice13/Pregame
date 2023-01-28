package com.example.pregame.TrainingMatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.Model.Match;
import com.example.pregame.PlayerHomeActivity;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ExampleViewHolder> {
    public static final String Match = "Match";
    private List<Match> matches;
    private Context context;
    private FirebaseFirestore firebaseFirestore;

    public MatchAdapter (List<Match> matches, Context context) {
        this.matches = matches;
        this.context = context;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, startTime, month, date, dayOfTheWeek;
        MaterialCardView cardView;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_name);
            startTime = itemView.findViewById(R.id.start_time);
            month = itemView.findViewById(R.id.month_tv);
            date = itemView.findViewById(R.id.date_tv);
            dayOfTheWeek = itemView.findViewById(R.id.day_of_the_week_tv);
            cardView = itemView.findViewById(R.id.match_card_view);
        }

        @Override
        public void onClick(View view) {
            int position = this.getLayoutPosition();
            Match match = matches.get(position);

            Intent intent = new Intent(view.getContext(), TrainingMatchFragment.class);
            intent.putExtra(Match, (Serializable) match);
            view.getContext().startActivity(intent);
        }
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.match_row_layout, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();

        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Match match = matches.get(position);
        holder.title.setText(match.getMatchTitle());
        holder.startTime.setText("Start Time: " + match.getStartTime());

        // Get the date
        Date date = new Date();
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(match.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.month.setText(new SimpleDateFormat("MMM", Locale.ENGLISH).format(date));
        holder.date.setText(new SimpleDateFormat("d", Locale.ENGLISH).format(date));
        holder.dayOfTheWeek.setText(new SimpleDateFormat("EEE", Locale.ENGLISH).format(date));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Match match = matches.get(position);
                viewMatchDetails(match);
            }
        });
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public Context getContext() {
        return context;
    }

    public void viewMatchDetails(Match match) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View viewMatchDetailsV = layoutInflater.inflate(R.layout.view_match_details, null);
        AlertDialog.Builder viewMatchDetailsAD = new AlertDialog.Builder(getContext());

        TextView matchTitleTv = viewMatchDetailsV.findViewById(R.id.match_title_tv);
        TextView homeOrAwayTv = viewMatchDetailsV.findViewById(R.id.home_or_away);
        TextView statusTv = viewMatchDetailsV.findViewById(R.id.status_tv);
        TextView scoreTv = viewMatchDetailsV.findViewById(R.id.score_tv);
        TextView dateTv = viewMatchDetailsV.findViewById(R.id.date_tv);
        TextView opponentTv = viewMatchDetailsV.findViewById(R.id.opponent_name_tv);
        TextView startTimeTv = viewMatchDetailsV.findViewById(R.id.start_time_tv);
        TextView meetTimeTv = viewMatchDetailsV.findViewById(R.id.meet_time_tv);
        TextView locationTv = viewMatchDetailsV.findViewById(R.id.location_tv);
        Button goBack = viewMatchDetailsV.findViewById(R.id.go_back_button);

        matchTitleTv.setText(match.getMatchTitle());

        if (match.isHomeOrAway()) {
            homeOrAwayTv.setText("Home");
        } else {
            homeOrAwayTv.setText("Away");
        }
        statusTv.setText("Status: " + match.getStatus());
        String score = match.getTeamScore() + " - " + match.getOpponentScore();
        scoreTv.setText("Score: " + score);
        dateTv.setText("Date: " + match.getDate());
        opponentTv.setText("Opponent: " + match.getOpponentName());
        startTimeTv.setText("Start Time: " + match.getStartTime());
        meetTimeTv.setText("Meet Time: " + match.getMeetTime());
        locationTv.setText("Location: " + match.getLocation());

        viewMatchDetailsAD.setCancelable(false).setView(viewMatchDetailsV);
        AlertDialog alert = viewMatchDetailsAD.create();
        alert.show();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
    }

    public void deleteMatch(int position) {
        Match match = matches.get(position);

        firebaseFirestore.collection("team").whereEqualTo("teamName", PlayerHomeActivity.currentTeam.getTeamName()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        String teamDoc = queryDocumentSnapshot.getId();
                                        firebaseFirestore.collection("team").document(teamDoc).collection("match").document(match.MatchId).delete();
                                        matches.remove(position);
                                        notifyItemRemoved(position);
                                    }
                                }
                            }
                        });
    }
}