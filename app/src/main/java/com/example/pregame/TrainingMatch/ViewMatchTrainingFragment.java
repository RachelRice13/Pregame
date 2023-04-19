package com.example.pregame.TrainingMatch;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pregame.HomePage.CoachHomeActivity;
import com.example.pregame.HomePage.CoachHomeFragment;
import com.example.pregame.HomePage.PlayerHomeActivity;
import com.example.pregame.HomePage.PlayerHomeFragment;
import com.example.pregame.Model.Attendance;
import com.example.pregame.Model.MatchTraining;
import com.example.pregame.Model.Team;
import com.example.pregame.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewMatchTrainingFragment extends Fragment {
    private View view;
    public static final String TAG = "ViewMTFragment";
    private MatchTraining matchTraining;
    private TextView teamNameTv, eventType, titleTv, dateTimeTv, locationTv, homeScoreTv, awayScoreTv, yesResponseTv, noResponseTv, pendingResponseTv, areYouGoingTv;
    private int numOfYes, numOfNo, numOfPending;
    private FragmentTransaction transaction;
    private LinearLayout matchScoreLL;
    private String type, teamDoc;

    public ViewMatchTrainingFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_view_match_training, container, false);

        Bundle bundle = getArguments();
        matchTraining = (MatchTraining) bundle.getSerializable("matchTraining");
        type = bundle.getString("type");
        teamDoc = bundle.getString("teamDoc");
        transaction = getFragmentManager().beginTransaction();

        setup();
        getDetails();
       return view;
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void getDetails() {
        eventType.setText(matchTraining.getType());
        titleTv.setText(matchTraining.getTitle());
        locationTv.setText(matchTraining.getLocation());
        homeScoreTv.setText(String.valueOf(matchTraining.getTeamScore()));
        awayScoreTv.setText(String.valueOf(matchTraining.getOpponentScore()));
        areYouGoingTv.setText("Are you going to the " + matchTraining.getType().toLowerCase());
        getAttendanceScore();

        Date date = new Date();
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(matchTraining.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dayOfTheWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);
        String month = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
        String dayDate = new SimpleDateFormat("d", Locale.ENGLISH).format(date);
        dateTimeTv.setText(dayOfTheWeek + ", " + month + ", " + dayDate + " at " + matchTraining.getStartTime());

        Team team;
        if (PlayerHomeActivity.userType.equals("Player")) {
            team = PlayerHomeActivity.currentTeam;
        } else {
            team = CoachHomeActivity.currentTeam;
        }
        teamNameTv.setText(team.getTeamName());

        if (matchTraining.getType().equals("Training")) {
            matchScoreLL.setVisibility(View.GONE);
        }
    }

    private void getAttendanceScore() {
        for (Attendance attendance : matchTraining.getAttendance()) {
            if (attendance.getResponse().equals("Yes")) {
                numOfYes += 1;
            } else if (attendance.getResponse().equals("No")) {
                numOfNo += 1;
            } else {
                numOfPending += 1;
            }
        }
        yesResponseTv.setText(String.valueOf(numOfYes));
        noResponseTv.setText(String.valueOf(numOfNo));
        pendingResponseTv.setText(String.valueOf(numOfPending));
    }

    private void setup() {
        teamNameTv = view.findViewById(R.id.team_name_tv);
        eventType = view.findViewById(R.id.event_type_tv);
        titleTv = view.findViewById(R.id.event_title_tv);
        dateTimeTv = view.findViewById(R.id.event_date_time_tv);
        locationTv = view.findViewById(R.id.event_location_tv);
        homeScoreTv = view.findViewById(R.id.home_score_tv);
        awayScoreTv = view.findViewById(R.id.away_score_tv);
        yesResponseTv = view.findViewById(R.id.number_of_yes_tv);
        noResponseTv = view.findViewById(R.id.number_of_nos_tv);
        pendingResponseTv = view.findViewById(R.id.number_of_pending_tv);
        areYouGoingTv = view.findViewById(R.id.are_you_going_tv);
        matchScoreLL = view.findViewById(R.id.match_score_ll);

        FloatingActionButton goBack = view.findViewById(R.id.go_back_button);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("CoachHome")) {
                    transaction.replace(R.id.container, new CoachHomeFragment()).commit();
                } else if (type.equals("PlayerHome")) {
                    transaction.replace(R.id.container, new PlayerHomeFragment()).commit();
                } else {
                    transaction.replace(R.id.container, new TrainingMatchFragment()).commit();
                }
            }
        });

        RelativeLayout yesResponseRl = view.findViewById(R.id.yes_rl);
        yesResponseRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Click No", Toast.LENGTH_SHORT).show();
            }
        });

        RelativeLayout noResponseRl = view.findViewById(R.id.no_rl);
        noResponseRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Click No", Toast.LENGTH_SHORT).show();
            }
        });
    }
}