package com.example.pregame.Team;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pregame.HomePage.PlayerHomeActivity;
import com.example.pregame.Model.IndividualStats;
import com.example.pregame.Model.Injury;
import com.example.pregame.Model.MatchStats;
import com.example.pregame.Model.Team;
import com.example.pregame.Model.User;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ViewPlayerDetailsFragment extends Fragment {
    public static final String TAG = "PlayerDetailsFragment";
    private View view;
    private User user;
    private FirebaseFirestore firebaseFirestore;
    private LinearLayout listOfTeams;
    private StorageReference storageReference;
    private ArrayList<Injury> injuries;
    private LinearLayout injuryTable, secondColumn;
    private final int[] gamesPlayed = {0}, pointsScored = {0}, takenFT = {0}, takenTwo = {0}, takenThree = {0}, madeFT = {0}, madeTwo = {0}, madeThree = {0}, offReb = {0}, defReb = {0}, totalReb ={0}, assists = {0}, blocks = {0}, turnovers = {0}, steals = {0}, fouls = {0};
    private ArrayList<String> statsList, columnNames;

    public ViewPlayerDetailsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_player_details, container, false);
        setup();
        setDetails();
        return view;
    }

    private void setDetails() {
        TextView fullNameTv = view.findViewById(R.id.profile_user_full_name_tv);
        TextView emailTv = view.findViewById(R.id.profile_email_tv);
        TextView phoneTv = view.findViewById(R.id.profile_phone_tv);
        TextView dobTv = view.findViewById(R.id.profile_dob_tv);
        listOfTeams = view.findViewById(R.id.list_of_teams);
        ImageView profilePic = view.findViewById(R.id.profile_pic);

        String fullName = user.getFirstName() + " " + user.getSurname();
        fullNameTv.setText(fullName);
        emailTv.setText(user.getEmail());
        phoneTv.setText(user.getPhoneNumber());
        dobTv.setText(user.getDob());

        storageReference.child("users").child(user.UserId).child("profile_pic")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri.toString()).fit().centerCrop().into(profilePic);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        profilePic.setImageResource(R.drawable.ic_profile);
                    }
                });

        for (DocumentReference team : user.getTeams()) {
            getTeamName(team);
        }

        setTableRow("Body Part", "Date", "Length of Injury", true, false, null);
        getInjuryDetails();
        setSecondColumn(columnNames);
        getTeamDoc();
    }

    private void getTeamName(DocumentReference team) {
        firebaseFirestore.collection("team").document(team.getId()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Team team = task.getResult().toObject(Team.class);
                            createTeamTextView(team.getTeamName());
                        }
                    }
                });
    }

    private void createTeamTextView(String teamName) {
        TextView textView = new TextView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setTextSize(18f);
        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(params);
        textView.setText(teamName);
        listOfTeams.addView(textView);
    }

    private void getInjuryDetails() {
        firebaseFirestore.collection("player").document(user.UserId).collection("injuryReport").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Injury injury = document.toObject(Injury.class);
                                injuries.add(injury);

                                for (Injury i : injuries) {
                                    setTableRow(i.getBodyPart(), i.getDateOfInjury(), i.getLengthOfInjury(), false, true, i);
                                }
                            }
                        }
                    }
                });
    }

    private void setTableRow(String columnOne, String columnTwo, String columnThree, boolean bold, boolean image, Injury injury) {
        LinearLayout rowLayout = new LinearLayout(getContext());

        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rowLayout.setLayoutParams(layoutParams);
        rowLayout.setPadding(1, 1, 1, 2);
        rowLayout.setBackgroundColor(getResources().getColor(R.color.black));

        LinearLayout firstColumn = createColumn(columnOne, 2f, bold);
        LinearLayout secondColumn = createColumn(columnTwo, 1.5f, bold);
        LinearLayout thirdColumn = createColumn(columnThree, 2.0f, bold);
        LinearLayout fourthColumn = createLastColumn(image, injury);

        rowLayout.addView(firstColumn);
        rowLayout.addView(secondColumn);
        rowLayout.addView(thirdColumn);
        rowLayout.addView(fourthColumn);
        injuryTable.addView(rowLayout);
    }

    private LinearLayout createColumn(String text, float weight, boolean bold) {
        LinearLayout column = new LinearLayout(getContext());
        LinearLayout.LayoutParams columnLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight);
        columnLayoutParams.setMargins(3, 0, 0, 0);
        column.setLayoutParams(columnLayoutParams);

        TextView textView = new TextView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 48);
        textView.setBackgroundColor(getResources().getColor(R.color.white));
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(10, 0, 10, 0);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(text);

        if (bold)
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

        textView.setLayoutParams(params);

        column.addView(textView);

        return column;
    }

    private LinearLayout createLastColumn(boolean image, Injury injury) {
        LinearLayout column = new LinearLayout(getContext());
        LinearLayout.LayoutParams columnLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.6f);
        columnLayoutParams.setMargins(4, 0, 5, 0);
        column.setLayoutParams(columnLayoutParams);

        ImageView imageView = new ImageView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 48);
        imageView.setBackgroundColor(getResources().getColor(R.color.white));
        imageView.setLayoutParams(params);

        if(image) {
            imageView.setImageResource(R.drawable.ic_info);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewInjuryDetails(injury);
                }
            });
        } else {
            imageView.setImageResource(R.drawable.ic_white_circle);
        }

        column.addView(imageView);

        return column;
    }

    private void viewInjuryDetails(Injury injury) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View injuryView = layoutInflater.inflate(R.layout.dialogue_view_injury_report_details, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        FloatingActionButton goBack = injuryView.findViewById(R.id.go_back);
        TextView bodyPartTv = injuryView.findViewById(R.id.body_part_tv);
        TextView descriptionTv = injuryView.findViewById(R.id.description_tv);
        TextView levelOfPainTv = injuryView.findViewById(R.id.level_of_pain_tv);
        TextView seenPhysioTv = injuryView.findViewById(R.id.seen_physio_tv);
        TextView dateTv = injuryView.findViewById(R.id.date_of_injury_tv);
        TextView lengthOfInjuryTv = injuryView.findViewById(R.id.length_of_injury_tv);
        TextView otherDetailsTv = injuryView.findViewById(R.id.other_details_tv);

        bodyPartTv.setText(injury.getBodyPart());
        descriptionTv.setText(injury.getDescriptionOfInjury());
        levelOfPainTv.setText(injury.getLevelOfPain() + "/10.0");
        seenPhysioTv.setText(injury.isSeenPhysio());
        dateTv.setText(injury.getDateOfInjury());
        lengthOfInjuryTv.setText(injury.getLengthOfInjury());
        otherDetailsTv.setText(injury.getOtherDetails());

        alertDialogBuilder.setCancelable(false).setView(injuryView);
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
    }

    private void getTeamDoc() {
        Team team = PlayerHomeActivity.currentTeam;
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("team").whereEqualTo("teamName", team.getTeamName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                String teamDoc = queryDocumentSnapshot.getId();
                                getMatchStats(teamDoc);
                            }
                        }
                    }
                });
    }

    private void getMatchStats(String teamDoc) {
        firebaseFirestore.collection("team").document(teamDoc).collection("match_stats").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MatchStats matchStats = document.toObject(MatchStats.class);

                                for (IndividualStats individualStats : matchStats.getPlayers()) {
                                    if (individualStats.getPlayer().getId().equals(user.UserId)) {
                                        calculateIndividualMatchStats(individualStats);
                                    }
                                }
                            }
                            addStatsToList();
                            setSecondColumn(statsList);
                            clear();
                            statsList.removeAll(statsList);

                        }
                    }
                });
    }

    private void addStatsToList() {
        DecimalFormat df = new DecimalFormat("#.#");
        totalReb[0] = defReb[0] + offReb[0];
        double fieldGoalPerc = calculatePercent(takenTwo[0], madeTwo[0]);
        double threePerc = calculatePercent(takenThree[0], madeThree[0]);
        double freeThrowPerc = calculatePercent(takenFT[0], madeFT[0]);

        statsList.add(String.valueOf(gamesPlayed[0]));
        statsList.add(String.valueOf(pointsScored[0]));
        statsList.add(String.valueOf(madeTwo[0]));
        statsList.add(String.valueOf(takenTwo[0]));
        statsList.add(df.format(fieldGoalPerc));
        statsList.add(String.valueOf(madeThree[0]));
        statsList.add(String.valueOf(takenThree[0]));
        statsList.add(df.format(threePerc));
        statsList.add(String.valueOf(madeFT[0]));
        statsList.add(String.valueOf(takenFT[0]));
        statsList.add(df.format(freeThrowPerc));
        statsList.add(String.valueOf(offReb[0]));
        statsList.add(String.valueOf(defReb[0]));
        statsList.add(String.valueOf(totalReb[0]));
        statsList.add(String.valueOf(assists[0]));
        statsList.add(String.valueOf(turnovers[0]));
        statsList.add(String.valueOf(steals[0]));
        statsList.add(String.valueOf(blocks[0]));
        statsList.add(String.valueOf(fouls[0]));
    }

    private double calculatePercent(int taken, int made) {
        if (taken == 0)
            return 0;
        else
            return (double) made*100 / (double) taken;
    }

    private void calculateIndividualMatchStats(IndividualStats stats) {
        gamesPlayed[0] += 1;
        pointsScored[0] += stats.getPointsScored();
        madeTwo[0] += stats.getShotsMadeTwo();
        takenTwo[0] += stats.getShotsTakenTwo();
        madeThree[0] += stats.getShotsMadeThree();
        takenThree[0] += stats.getShotsTakenThree();
        madeFT[0] += stats.getShotsMadeFt();
        takenFT[0] += stats.getShotsTakenFt();
        offReb[0] += stats.getOffensiveRebounds();
        defReb[0] += stats.getDefensiveRebounds();
        assists[0] += stats.getAssists();
        turnovers[0] += stats.getTurnovers();
        steals[0] += stats.getSteals();
        blocks[0] += stats.getBlocks();
        fouls[0] += stats.getFouls();
    }

    private void clear() {
        madeTwo[0] = 0; takenTwo[0] = 0; madeThree[0] = 0; takenThree[0] = 0; madeFT[0] = 0; takenFT[0] = 0;
        gamesPlayed[0] = 0; pointsScored[0] = 0;assists[0] = 0; turnovers[0] = 0; steals[0] = 0; blocks[0] = 0; fouls[0] = 0; offReb[0] = 0; defReb[0] = 0; totalReb[0] = 0;
    }

    private void setSecondColumn(ArrayList<String> columnNames) {
        LinearLayout rowLayout = new LinearLayout(getContext());
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  60);
        rowLayout.setLayoutParams(layoutParams);

        createTextView(rowLayout, columnNames.get(0));
        createTextView(rowLayout, columnNames.get(1));
        createTextView(rowLayout, columnNames.get(2));
        createTextView(rowLayout, columnNames.get(3));
        createTextView(rowLayout, columnNames.get(4));
        createTextView(rowLayout, columnNames.get(5));
        createTextView(rowLayout, columnNames.get(6));
        createTextView(rowLayout, columnNames.get(7));
        createTextView(rowLayout, columnNames.get(8));
        createTextView(rowLayout, columnNames.get(9));
        createTextView(rowLayout, columnNames.get(10));
        createTextView(rowLayout, columnNames.get(11));
        createTextView(rowLayout, columnNames.get(12));
        createTextView(rowLayout, columnNames.get(13));
        createTextView(rowLayout, columnNames.get(14));
        createTextView(rowLayout, columnNames.get(15));
        createTextView(rowLayout, columnNames.get(16));
        createTextView(rowLayout, columnNames.get(17));
        createTextView(rowLayout, columnNames.get(18));

        secondColumn.addView(rowLayout);

    }

    private void createTextView(LinearLayout rowLayout, String text) {
        TextView textView = new TextView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(120, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(text);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        rowLayout.addView(textView);
    }

    private void setup() {
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("user");
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        injuries = new ArrayList<>();
        columnNames = new ArrayList<>();
        statsList = new ArrayList<>();
        injuryTable = view.findViewById(R.id.injury_table);
        secondColumn = view.findViewById(R.id.stats_column);

        addNamesToList();

        FloatingActionButton goBack = view.findViewById(R.id.go_back);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new TeamFragment()).commit();
            }
        });
    }

    private void addNamesToList() {
        columnNames.add("GP");
        columnNames.add("PTS");
        columnNames.add("FGM");
        columnNames.add("FGA");
        columnNames.add("FG%");
        columnNames.add("3PM");
        columnNames.add("3PA");
        columnNames.add("3P%");
        columnNames.add("FTM");
        columnNames.add("FTA");
        columnNames.add("FT%");
        columnNames.add("OREB");
        columnNames.add("DREB");
        columnNames.add("REB");
        columnNames.add("AST");
        columnNames.add("TOV");
        columnNames.add("STL");
        columnNames.add("BLK");
        columnNames.add("FL");
    }
}