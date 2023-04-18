package com.example.pregame.TrainingMatch;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.pregame.Common.Validation;
import com.example.pregame.HomePage.CoachHomeActivity;
import com.example.pregame.Model.Attendance;
import com.example.pregame.Model.MatchTraining;
import com.example.pregame.Model.Team;
import com.example.pregame.Model.User;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class AddMatchFragment extends Fragment {
    public static final String TAG = "AddMatchFragment";
    private View view;
    private FragmentTransaction transaction;
    private FirebaseFirestore firebaseFirestore;
    private EditText titleEt, locationEt;
    private TextInputLayout titleLo, locationLo;
    private TextView dateTv, startTimeTv, meetTimeTv;
    private String date = "", meetTime = "", startTime = "";
    private final Team currentTeam = CoachHomeActivity.currentTeam;
    private ArrayList<Attendance> attendances = new ArrayList<>();

    public AddMatchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_match, container, false);

        transaction = getFragmentManager().beginTransaction();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setup();
        setDatePicker();
        setMeetTimePicker();
        setStartTimePicker();
        addToList(currentTeam.getCoaches(), "coach");
        addToList(currentTeam.getPlayers(), "player");

        return view;
    }

    private void addMatch() {
        String title = titleEt.getText().toString();
        String location = locationEt.getText().toString();

        boolean validTitle = Validation.validateBlank(title, titleLo);
        boolean validLocation = Validation.validateBlank(location, locationLo);
        boolean validDate = Validation.validateString(date);
        boolean validStartTime = Validation.validateString(startTime);
        boolean validMeetTime = Validation.validateString(meetTime);

        if (validTitle && validDate && validStartTime && validMeetTime && validLocation) {
            MatchTraining match = new MatchTraining(title, date, startTime, meetTime, location, "Haven't Played", "match", 0, 0, attendances);
            addToFireStore(match);
        }
    }

    private void addToFireStore(MatchTraining match) {
        firebaseFirestore.collection("team").whereEqualTo("teamName", currentTeam.getTeamName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                String teamDoc = queryDocumentSnapshot.getId();

                                firebaseFirestore.collection("team").document(teamDoc).collection("training_match")
                                        .add(match)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "Successfully added match to DB");
                                                transaction.replace(R.id.container, new TrainingMatchFragment()).commit();
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

    private void addToList(ArrayList<DocumentReference> docRefArrayList, String teamMemberType) {
        for (int i = 0; i < docRefArrayList.size(); i++) {
            DocumentReference reference = docRefArrayList.get(i);
            reference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            String username = user.getFirstName() + " " + user.getSurname();
                            Attendance attendance = new Attendance(username, "Hasn't Responded", "", teamMemberType);
                            attendances.add(attendance);
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

    private void setDatePicker() {
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
                        date = dayOfMonth + "/" + month + "/" + year;
                        dateTv.setText(date);
                    }
                }, YEAR, MONTH, DAY);
                datePickerDialog.show();
            }
        });
    }

    private void setMeetTimePicker() {
        meetTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        meetTime = hourOfDay + ":" + minute;
                        meetTimeTv.setText(meetTime);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });
    }

    private void setStartTimePicker() {
        startTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        startTime = hourOfDay + ":" + minute;
                        startTimeTv.setText(startTime);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });
    }

    private void setup() {
        ImageView toolbarIcon = getActivity().findViewById(R.id.toolbar_end_icon);
        toolbarIcon.setVisibility(View.INVISIBLE);

        titleEt = view.findViewById(R.id.match_title_et);
        titleLo = view.findViewById(R.id.match_title);
        locationEt = view.findViewById(R.id.location_et);
        locationLo = view.findViewById(R.id.location);
        dateTv = view.findViewById(R.id.match_date);
        startTimeTv = view.findViewById(R.id.start_time);
        meetTimeTv = view.findViewById(R.id.meet_time);

        Button createMatch = view.findViewById(R.id.create_match_button);
        createMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMatch();
            }
        });

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, new TrainingMatchFragment()).commit();
            }
        });
    }
}