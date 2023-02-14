package com.example.pregame.InjuryReport;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.Model.Injury;
import com.example.pregame.Model.Match;
import com.example.pregame.R;
import com.example.pregame.TrainingMatch.MatchAdapter;
import com.example.pregame.TrainingMatch.TrainingMatchFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InjuryReportAdapter extends RecyclerView.Adapter<InjuryReportAdapter.ExampleViewHolder> {
    public static final String TAG = "InjuryAdapter";
    private List<Injury> injuries;
    private Context context;
    private FirebaseFirestore firebaseFirestore;

    public InjuryReportAdapter (List<Injury> injuries, Context context) {
        this.injuries = injuries;
        this.context = context;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView bodyPart, injuryDesc, levelOfPain, date, lengthOfInjury;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            bodyPart = itemView.findViewById(R.id.body_part_ir);
            injuryDesc = itemView.findViewById(R.id.description_ir);
            levelOfPain = itemView.findViewById(R.id.level_of_pain_ir);
            date = itemView.findViewById(R.id.date_ir);
            lengthOfInjury = itemView.findViewById(R.id.length_of_injury);
        }

    }

    @NonNull
    @Override
    public InjuryReportAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.injury_report_row_layout, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();

        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InjuryReportAdapter.ExampleViewHolder holder, int position) {
        Injury injury = injuries.get(position);
        holder.bodyPart.setText(injury.getBodyPart());
        holder.injuryDesc.setText(injury.getDescriptionOfInjury());
        holder.levelOfPain.setText("Level of Pain: " + injury.getLevelOfPain());
        Date injuryDate = injury.getDateOfInjury();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = dateFormat.format(injuryDate);
        holder.date.setText(dateString);
        holder.lengthOfInjury.setText(injury.getLengthOfInjury());
    }

    @Override
    public int getItemCount() {
        return injuries.size();
    }
}
