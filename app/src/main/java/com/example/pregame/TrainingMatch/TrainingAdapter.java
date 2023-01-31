package com.example.pregame.TrainingMatch;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.CoachHomeActivity;
import com.example.pregame.Model.Training;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
                viewTrainingDetails(training);
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

    public void viewTrainingDetails(Training training) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View viewMTrainingDetailsV = layoutInflater.inflate(R.layout.view_training_details, null);
        AlertDialog.Builder viewTrainingDetailsAD = new AlertDialog.Builder(getContext());

        TextView trainingTitleTv = viewMTrainingDetailsV.findViewById(R.id.training_title_tv);
        TextView dateTv = viewMTrainingDetailsV.findViewById(R.id.date_tv);
        TextView startTimeTv = viewMTrainingDetailsV.findViewById(R.id.start_time_tv);
        TextView meetTimeTv = viewMTrainingDetailsV.findViewById(R.id.meet_time_tv);
        TextView locationTv = viewMTrainingDetailsV.findViewById(R.id.location_tv);
        TextView detailsTv = viewMTrainingDetailsV.findViewById(R.id.details_tv);
        Button goBack = viewMTrainingDetailsV.findViewById(R.id.go_back_button);

        trainingTitleTv.setText(training.getTrainingTitle());
        dateTv.setText("Date: " + training.getDate());
        startTimeTv.setText("Start Time: " + training.getStartTime());
        meetTimeTv.setText("Meet Time: " + training.getMeetTime());
        locationTv.setText("Location: " + training.getLocation());
        detailsTv.setText("Details: " + training.getDetails());

        viewTrainingDetailsAD.setCancelable(false).setView(viewMTrainingDetailsV);
        AlertDialog alert = viewTrainingDetailsAD.create();
        alert.show();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
    }

    public void deleteTraining(int position) {
        Training training = trainings.get(position);

        firebaseFirestore.collection("team").whereEqualTo("teamName", CoachHomeActivity.currentTeam.getTeamName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                String teamDoc = queryDocumentSnapshot.getId();
                                firebaseFirestore.collection("team").document(teamDoc).collection("training").document(training.TrainingId).delete();
                                trainings.remove(position);
                                notifyItemRemoved(position);
                            }
                        }
                    }
                });
    }

    public void editTrainingDetails(int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View editTrainingDetailsV = layoutInflater.inflate(R.layout.edit_training_details, null);
        AlertDialog.Builder editTrainingDetailsAD = new AlertDialog.Builder(getContext());

        Training training = trainings.get(position);
        TextView trainingTitleTv = editTrainingDetailsV.findViewById(R.id.training_title_tv);
        TextView dateTv = editTrainingDetailsV.findViewById(R.id.date_tv);
        TextView startTimeTv = editTrainingDetailsV.findViewById(R.id.start_time_tv);
        TextView meetTimeTv = editTrainingDetailsV.findViewById(R.id.meet_time_tv);
        TextView locationTv = editTrainingDetailsV.findViewById(R.id.location_tv);
        TextView detailsTv = editTrainingDetailsV.findViewById(R.id.details_tv);
        Button goBack = editTrainingDetailsV.findViewById(R.id.go_back_button);

        trainingTitleTv.setText(training.getTrainingTitle());
        dateTv.setText("Date: " + training.getDate());
        startTimeTv.setText("Start Time: " + training.getStartTime());
        meetTimeTv.setText("Meet Time: " + training.getMeetTime());
        locationTv.setText("Location: " + training.getLocation());
        detailsTv.setText("Details: " + training.getDetails());

        editTrainingDetailsAD.setCancelable(false).setView(editTrainingDetailsV);
        AlertDialog alert = editTrainingDetailsAD.create();
        alert.show();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
                notifyItemChanged(position);
            }
        });

        trainingTitleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editString(training, training.getTrainingTitle(), R.string.edit_training_title, R.string.training_title, R.drawable.ic_basketball, "trainingTitle");
                alert.cancel();
            }
        });

        locationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editString(training, training.getLocation(), R.string.edit_training_location, R.string.location, R.drawable.ic_location, "location");
                alert.cancel();
            }
        });

        detailsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editString(training, training.getDetails(), R.string.edit_training_details, R.string.details, R.drawable.ic_details, "details");
                alert.cancel();
            }
        });

        dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int DAY = calendar.get(Calendar.DATE);
                int MONTH = calendar.get(Calendar.MONTH);
                int YEAR = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        month += 1;
                        String date = dayOfMonth + "/" + month + "/" + year;
                        updateFirestoreString(training, "date", date);
                    }
                }, YEAR, MONTH, DAY);
                datePickerDialog.show();
            }
        });

        startTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        String startTime = String.format("%02d:%02d", hourOfDay, minute);
                        updateFirestoreString(training, "startTime", startTime);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        meetTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meetTimeTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar calendar = Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                String meetTime = String.format("%02d:%02d", hourOfDay, minute);
                                updateFirestoreString(training, "meetTime", meetTime);
                            }
                        }, hour, minute, false);
                        timePickerDialog.show();
                    }
                });
            }
        });
    }

    public void editString(Training training, String data, int title, int hint, int icon, String dataType) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View editStringV = layoutInflater.inflate(R.layout.edit_details, null);
        AlertDialog.Builder editStringAD = new AlertDialog.Builder(getContext());

        TextView titleTv = editStringV.findViewById(R.id.edit_detail_title);
        EditText editText = editStringV.findViewById(R.id.edit_text);
        TextInputLayout editTextLO = editStringV.findViewById(R.id.text_input_layout);
        Button editButton = editStringV.findViewById(R.id.edit_button);
        Button goBack = editStringV.findViewById(R.id.cancel_button);

        titleTv.setText(title);
        editText.setText(data);
        editTextLO.setHint(hint);
        editTextLO.setStartIconDrawable(icon);

        editStringAD.setCancelable(false).setView(editStringV);
        AlertDialog alert = editStringAD.create();
        alert.show();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();

                boolean validString = validateBlank(text, editTextLO);

                if (validString) {
                    updateFirestoreString(training, dataType, text);
                    notifyDataSetChanged();
                    alert.cancel();
                }
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDataSetChanged();
                alert.cancel();
            }
        });
    }

    public boolean validateBlank(String text, TextInputLayout layout) {
        if (text.isEmpty()) {
            layout.setError("This is Required");
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

    public void updateFirestoreString(Training training, String dataType, String data) {
        firebaseFirestore.collection("team").whereEqualTo("teamName", CoachHomeActivity.currentTeam.getTeamName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                String teamDoc = queryDocumentSnapshot.getId();

                                firebaseFirestore.collection("team").document(teamDoc).collection("training").document(training.TrainingId)
                                        .update(dataType, data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.e(TAG, "Successfully updated " + dataType + " for " + training.getTrainingTitle());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "Failed to update " + dataType + " for " + training.getTrainingTitle() + "\n" + e.getMessage());
                                            }
                                        });
                            }
                        }
                    }
                });
    }

}
