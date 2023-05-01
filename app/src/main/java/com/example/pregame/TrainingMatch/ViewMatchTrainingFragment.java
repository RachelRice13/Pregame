package com.example.pregame.TrainingMatch;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pregame.HomePage.CoachHomeActivity;
import com.example.pregame.HomePage.CoachHomeFragment;
import com.example.pregame.HomePage.PlayerHomeActivity;
import com.example.pregame.HomePage.PlayerHomeFragment;
import com.example.pregame.Model.Attendance;
import com.example.pregame.Model.MatchTraining;
import com.example.pregame.Model.Team;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ViewMatchTrainingFragment extends Fragment {
    private View view;
    public static final String TAG = "ViewMTFragment";
    private MatchTraining matchTraining;
    private TextView teamNameTv, eventType, titleTv, dateTimeTv, locationTv, homeScoreTv, awayScoreTv, yesResponseTv, noResponseTv, pendingResponseTv, areYouGoingTv;
    private ImageView yesIv, noIv;
    private int position;
    private FragmentTransaction transaction;
    private LinearLayout matchScoreLL;
    private FirebaseFirestore firebaseFirestore;
    private String teamDoc, currentUserId, currentType;
    private DocumentReference myRef;

    public ViewMatchTrainingFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_view_match_training, container, false);

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
        areYouGoingTv.setText("Are you going to the " + matchTraining.getType().toLowerCase() + "?");
        getAttendanceScore(matchTraining.getAttendance());
        setRatingColour();

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

    private void getAttendanceScore(ArrayList<Attendance> attendances) {
        int numOfYes = 0, numOfNo = 0, numOfPending = 0;

        for (Attendance attendance : attendances) {
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

    private void setRatingColour() {
        for (Attendance attendance : matchTraining.getAttendance()) {
            if (attendance.getUser().getId().equals(currentUserId)) {
                if (attendance.getResponse().equals("Yes"))
                    yesIv.setColorFilter(getResources().getColor(R.color.green));
                else if (attendance.getResponse().equals("No"))
                    noIv.setColorFilter(getResources().getColor(R.color.red));
                else
                    noIv.setColorFilter(getResources().getColor(R.color.black));
            }
        }
    }

    private void response(String response) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        String dateTime = formatter.format(date);

        firebaseFirestore.collection("team").document(teamDoc).collection("training_match").whereEqualTo("title", matchTraining.getTitle()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                MatchTraining matchTraining = documentSnapshot.toObject(MatchTraining.class);
                                ArrayList<Attendance> attendances = matchTraining.getAttendance();
                                String docId = documentSnapshot.getId();
                                boolean found = false;

                                for (int i = 0; i < matchTraining.getAttendance().size(); i++) {
                                    Attendance attendance = matchTraining.getAttendance().get(i);
                                    if (attendance.getUser().getId().equals(currentUserId)) {
                                        found = true;
                                        position = i;
                                        myRef = attendance.getUser();
                                        currentType = attendance.getType();
                                    }
                                }
                                Attendance attendance = new Attendance(response, dateTime, currentType, myRef);

                                if (found)
                                    updateResponse(docId, attendances, position, attendance);
                            }
                        }
                    }
                });
    }

    private void updateResponse(String docId, ArrayList<Attendance> attendances, int position, Attendance myAttendance) {
        attendances.set(position, myAttendance);

        firebaseFirestore.collection("team").document(teamDoc).collection("training_match").document(docId).update("attendance", attendances)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Updated attendance response");
                        if (myAttendance.getResponse().equals("Yes"))
                            setResponseIvColours(yesIv, R.color.green, noIv);
                        else
                            setResponseIvColours(noIv, R.color.red, yesIv);

                        getAttendanceScore(attendances);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update attendance response", e);
                    }
                });

    }

    private void setResponseIvColours(ImageView first, int colourId, ImageView second) {
        first.setColorFilter(getResources().getColor(colourId));
        second.setColorFilter(getResources().getColor(R.color.black));
    }

    private void setup() {
        ImageView toolbarIcon = getActivity().findViewById(R.id.toolbar_end_icon);
        toolbarIcon.setVisibility(View.INVISIBLE);Bundle bundle = getArguments();
        matchTraining = (MatchTraining) bundle.getSerializable("matchTraining");
        String type = bundle.getString("type");
        teamDoc = bundle.getString("teamDoc");
        transaction = getFragmentManager().beginTransaction();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

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
        yesIv = view.findViewById(R.id.yes_button);
        noIv = view.findViewById(R.id.no_button);

        LinearLayout goToResponse = view.findViewById(R.id.event_responses_ll);
        goToResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewAttendanceFragment viewAttendanceFragment = new ViewAttendanceFragment();
                Bundle goBundle = new Bundle();
                goBundle.putSerializable("matchTraining", matchTraining);
                viewAttendanceFragment.setArguments(goBundle);
                transaction.replace(R.id.container, viewAttendanceFragment).commit();
            }
        });

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

        setResponseButton(R.id.yes_rl, "Yes");
        setResponseButton(R.id.no_rl, "No");
    }

    private void setResponseButton(int id, String response) {
        RelativeLayout responseRl = view.findViewById(id);
        responseRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                response(response);
            }
        });
    }
}