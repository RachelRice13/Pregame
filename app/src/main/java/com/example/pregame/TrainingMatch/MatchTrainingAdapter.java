package com.example.pregame.TrainingMatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.Model.MatchTraining;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MatchTrainingAdapter extends RecyclerView.Adapter<MatchTrainingAdapter.ExampleViewHolder>{
    private View view;
    public static final String TAG = "MatchTrainingAdapter";
    private List<MatchTraining> matchTrainings;
    private Context context;
    private FragmentManager fragmentManager;
    private FirebaseFirestore firebaseFirestore;
    private String teamDoc, type;

    public MatchTrainingAdapter(List<MatchTraining> matchTrainings, Context context, String teamDoc, FragmentManager fragmentManager, String type) {
        this.matchTrainings = matchTrainings;
        this.context = context;
        this.teamDoc = teamDoc;
        this.fragmentManager = fragmentManager;
        this.type = type;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder  {
        TextView title, startTime, month, date, dayOfTheWeek;
        ImageView colourIv;
        MaterialCardView cardView;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            firebaseFirestore = FirebaseFirestore.getInstance();
            title = itemView.findViewById(R.id.title_name);
            startTime = itemView.findViewById(R.id.start_time);
            month = itemView.findViewById(R.id.month_tv);
            date = itemView.findViewById(R.id.date_tv);
            dayOfTheWeek = itemView.findViewById(R.id.day_of_the_week_tv);
            colourIv = itemView.findViewById(R.id.training_match_colour);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }

    @NonNull
    @Override
    public MatchTrainingAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.row_layout_training_match, parent, false);
        return new ExampleViewHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull MatchTrainingAdapter.ExampleViewHolder holder, int position) {
        MatchTraining matchTraining = matchTrainings.get(position);

        holder.title.setText(matchTraining.getTitle());
        holder.startTime.setText("Start Time: " + matchTraining.getStartTime());
        Date date = new Date();
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(matchTraining.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.month.setText(new SimpleDateFormat("MMM", Locale.ENGLISH).format(date));
        holder.date.setText(new SimpleDateFormat("d", Locale.ENGLISH).format(date));
        holder.dayOfTheWeek.setText(new SimpleDateFormat("EEE", Locale.ENGLISH).format(date));

        if (matchTraining.getType().equals("Match")) {
            holder.colourIv.setBackgroundColor(Color.parseColor("#005A9C"));
        } else {
            holder.colourIv.setBackgroundColor(Color.parseColor("#FF5733"));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewMatchTrainingFragment viewMatchTrainingFragment = new ViewMatchTrainingFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("matchTraining", matchTraining);
                bundle.putString("teamDoc", teamDoc);
                bundle.putString("type", type);
                viewMatchTrainingFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, viewMatchTrainingFragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return matchTrainings.size();
    }

    public Context getContext() {
        return context;
    }

    public void deleteEvent(int position) {
        MatchTraining matchTraining = matchTrainings.get(position);
        firebaseFirestore.collection("team").document(teamDoc).collection("training_match").document(matchTraining.MatchTrainingId).delete();
        deleteMatchStats(matchTraining.getId());
        matchTrainings.remove(position);
        notifyItemRemoved(position);
        Snackbar.make(view, "Deleted event", Snackbar.LENGTH_LONG).show();
    }

    private void deleteMatchStats(String id) {
        firebaseFirestore.collection("team").document(teamDoc).collection("match_stats").whereEqualTo("id", id).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String id = documentSnapshot.getId();
                                firebaseFirestore.collection("team").document(teamDoc).collection("match_stats").document(id).delete();
                            }
                        } else {
                            Log.e(TAG, "No match with that id");
                        }
                    }
                });
    }

    public void updateEvent(int position) {
        MatchTraining matchTraining = matchTrainings.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("matchTraining", matchTraining);
        bundle.putString("teamDoc", teamDoc);

        if (matchTraining.getType().equals("Match")) {
            UpdateMatchFragment updateMatchFragment = new UpdateMatchFragment();
            updateMatchFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.container, updateMatchFragment).commit();
        } else {
            UpdateTrainingFragment updateTrainingFragment = new UpdateTrainingFragment();
            updateTrainingFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.container, updateTrainingFragment).commit();
        }

    }
}