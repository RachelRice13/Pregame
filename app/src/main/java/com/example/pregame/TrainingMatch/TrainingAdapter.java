package com.example.pregame.TrainingMatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.Model.Training;
import com.example.pregame.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.ExampleViewHolder> {
    public static final String TAG = "TrainingAdapter";
    public static final String Training = "Training";
    private List<Training> trainings;
    private Context context;
    private FirebaseFirestore firebaseFirestore;

    public TrainingAdapter (List<Training> trainings, Context context) {
        this.trainings = trainings;
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
            Training training = trainings.get(position);

            Intent intent = new Intent(view.getContext(), TrainingMatchFragment.class);
            intent.putExtra(Training, (Serializable) training);
            view.getContext().startActivity(intent);
        }
    }

    @NonNull
    @Override
    public TrainingAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.match_row_layout, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();

        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingAdapter.ExampleViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Training training = trainings.get(position);
        holder.title.setText(training.getTrainingTitle());
        holder.startTime.setText("Start Time: " + training.getStartTime());

        Date date = new Date();
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(training.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.month.setText(new SimpleDateFormat("MMM", Locale.ENGLISH).format(date));
        holder.date.setText(new SimpleDateFormat("d", Locale.ENGLISH).format(date));
        holder.dayOfTheWeek.setText(new SimpleDateFormat("EEE", Locale.ENGLISH).format(date));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Training training = trainings.get(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trainings.size();
    }

    public Context getContext() {
        return context;
    }
}
