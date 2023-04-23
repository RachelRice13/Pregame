package com.example.pregame.TrainingMatch;

import android.annotation.SuppressLint;
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
import com.example.pregame.Model.Attendance;
import com.example.pregame.Model.MatchTraining;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class UpdateMatchFragment extends Fragment {
    private static final String TAG = "UpdateMatchFragment";
    private View view;
    private FragmentTransaction transaction;
    private FirebaseFirestore firebaseFirestore;
    private MatchTraining matchTraining;
    private EditText titleEt, locationEt, homeScoreEt, opponentScoreEt;
    private TextInputLayout titleLo, locationLo;
    private TextView dateTv, startTimeTv, wonTv, lostTv, drawTv, cancelledTv;
    private String date = "", startTime = "", status, teamDoc;

    public UpdateMatchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_update_match, container, false);

        setup();
        setDatePicker();
        setStartTimePicker();
        getDetails();

        return view;
    }

    private void getDetails() {
        status = matchTraining.getStatus();
        date = matchTraining.getDate();
        startTime = matchTraining.getStartTime();
        titleEt.setText(matchTraining.getTitle());
        startTimeTv.setText(startTime);
        dateTv.setText(date);
        locationEt.setText(matchTraining.getLocation());
        homeScoreEt.setText(String.valueOf(matchTraining.getTeamScore()));
        opponentScoreEt.setText(String.valueOf(matchTraining.getOpponentScore()));

        getBackgroundColour("Won", wonTv, R.color.green, R.color.black);
        getBackgroundColour("Lost", lostTv, R.color.dark_blue, R.color.white);
        getBackgroundColour("Draw", drawTv, R.color.yellow, R.color.black);
        getBackgroundColour("Cancelled", cancelledTv, R.color.red, R.color.white);
    }

    private void getBackgroundColour(String status, TextView textView, int backgroundColour, int textColour) {
        if (matchTraining.getStatus().equals(status)) {
            textView.setBackgroundResource(backgroundColour);
            textView.setTextColor(getResources().getColor(textColour));
        }
    }

    private void setBackground(TextView textView, int backgroundColour, int textColour) {
        textView.setBackgroundResource(backgroundColour);
        textView.setTextColor(getResources().getColor(textColour));
    }

    private void updateMatch() {
        String title = titleEt.getText().toString();
        String location = locationEt.getText().toString();
        String homeScoreS = homeScoreEt.getText().toString();
        String opponentScoreS = opponentScoreEt.getText().toString();

        boolean validTitle = Validation.validateBlank(title, titleLo);
        boolean validDate = Validation.validateString(date);
        boolean validTime = Validation.validateString(startTime);
        boolean validLocation = Validation.validateBlank(location, locationLo);
        boolean validHomeScore = Validation.validateString(homeScoreS);
        boolean validOpponentScore = Validation.validateString(opponentScoreS);
        boolean validStatus = Validation.validateString(status);

        if (validTitle && validDate && validTime && validLocation && validHomeScore && validOpponentScore && validStatus) {
            int homeScore = Integer.parseInt(homeScoreS);
            int opponentScore = Integer.parseInt(opponentScoreS);
            String type = matchTraining.getType();
            ArrayList<Attendance> attendance = matchTraining.getAttendance();
            String id = matchTraining.getId();
            String opponent = matchTraining.getOpponent();

            MatchTraining newMatchTraining = new MatchTraining(title, date, startTime, location, status, type, id, opponent, homeScore, opponentScore, attendance);
            updateFireStore(newMatchTraining);
        }
    }

    private void updateFireStore(MatchTraining newMatchTraining) {
        firebaseFirestore.collection("team").document(teamDoc).collection("training_match").document(matchTraining.MatchTrainingId).set(newMatchTraining)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Snackbar.make(view, "Updated Match " + newMatchTraining.getTitle(), Snackbar.LENGTH_LONG).show();
                        transaction.replace(R.id.container, new TrainingMatchFragment()).commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update match.", e);
                    }
                });
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
        transaction = getFragmentManager().beginTransaction();
        firebaseFirestore = FirebaseFirestore.getInstance();
        Bundle bundle = getArguments();
        matchTraining = (MatchTraining) bundle.getSerializable("matchTraining");
        teamDoc = bundle.getString("teamDoc");

        titleEt = view.findViewById(R.id.match_title_et);
        titleLo = view.findViewById(R.id.match_title);
        locationEt = view.findViewById(R.id.location_et);
        locationLo = view.findViewById(R.id.location);
        dateTv = view.findViewById(R.id.match_date);
        startTimeTv = view.findViewById(R.id.start_time);
        homeScoreEt = view.findViewById(R.id.home_score_et);
        opponentScoreEt = view.findViewById(R.id.opponent_score_et);
        wonTv = view.findViewById(R.id.status_won_tv);
        lostTv = view.findViewById(R.id.status_lost_tv);
        drawTv = view.findViewById(R.id.status_draw_tv);
        cancelledTv = view.findViewById(R.id.status_cancelled_tv);

        Button updateMatch = view.findViewById(R.id.update_match_button);
        updateMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMatch();
            }
        });

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, new TrainingMatchFragment()).commit();
            }
        });

        setStatusButton (wonTv, R.color.green, R.color.black, "Won", lostTv, drawTv, cancelledTv);
        setStatusButton (lostTv, R.color.dark_blue, R.color.white, "Lost", wonTv, drawTv, cancelledTv);
        setStatusButton (drawTv, R.color.yellow, R.color.black, "Draw", wonTv, lostTv, cancelledTv);
        setStatusButton (cancelledTv, R.color.red, R.color.white, "Cancelled", wonTv, lostTv, drawTv);

    }

    private void setStatusButton (TextView selectedTv, int selectedBc, int selectTc, String statusString, TextView first, TextView second, TextView third) {
        selectedTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBackground(selectedTv, selectedBc, selectTc);
                status = statusString;
                setBackground(first, R.drawable.grey_border, R.color.black);
                setBackground(second, R.drawable.grey_border, R.color.black);
                setBackground(third, R.drawable.grey_border, R.color.black);
            }
        });
    }
}