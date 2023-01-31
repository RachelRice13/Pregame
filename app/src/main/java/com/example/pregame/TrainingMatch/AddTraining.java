package com.example.pregame.TrainingMatch;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.pregame.CoachHomeActivity;
import com.example.pregame.Model.Training;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class AddTraining extends DialogFragment {
    public static final String TAG = "AddTraining";
    private TextView dateTv, startTimeTv, meetTimeTv;
    private EditText trainingTitleEt, locationEt, detailsEt;
    private Button addButton, cancelButton;
    private String trainingTitle, date, startTime, meetTime, location, details;
    private FirebaseFirestore firebaseFirestore;

    public static AddTraining newInstance() {
        return new AddTraining();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.add_training, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();

        trainingTitleEt = view.findViewById(R.id.training_title_et);
        dateTv = view.findViewById(R.id.training_date);
        startTimeTv = view.findViewById(R.id.start_time);
        meetTimeTv = view.findViewById(R.id.meet_time);
        locationEt = view.findViewById(R.id.location_et);
        detailsEt = view.findViewById(R.id.details_et);

        addButton = view.findViewById(R.id.create_match_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        setDatePicker();
        setStartTimePicker();
        setMeetTimePicker();

        // Add match to database
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trainingTitle = trainingTitleEt.getText().toString();
                location = locationEt.getText().toString();
                details = detailsEt.getText().toString();

                firebaseFirestore.collection("team").whereEqualTo("teamName", CoachHomeActivity.currentTeam.getTeamName()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        String teamDoc = queryDocumentSnapshot.getId();

                                        Training training = new Training(trainingTitle, date, startTime, meetTime, location, details);

                                        firebaseFirestore.collection("team").document(teamDoc).collection("training")
                                                .add(training)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Log.d(TAG, "Successfully added training to DB");
                                                        Toast.makeText(getContext(), "Created Training '" + training.getTrainingTitle() + "'", Toast.LENGTH_SHORT).show();
                                                        dismiss();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e(TAG, e.toString());
                                                    }
                                                });
                                    }
                                }
                            }
                        });
            }
        });

        // Cancel add match
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setDatePicker() {
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
                        dateTv.setText(dayOfMonth + "/" + month + "/" + year);
                        date = dayOfMonth + "/" + month + "/" + year;
                    }
                }, YEAR, MONTH, DAY);
                datePickerDialog.show();
            }
        });
    }

    public void setStartTimePicker() {
        startTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        startTimeTv.setText(String.format("%02d:%02d", hourOfDay, minute));
                        startTime = String.format("%02d:%02d", hourOfDay, minute);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });
    }

    public void setMeetTimePicker() {
        meetTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        meetTimeTv.setText(String.format("%02d:%02d", hourOfDay, minute));
                        meetTime = String.format("%02d:%02d", hourOfDay, minute);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });
    }
}
