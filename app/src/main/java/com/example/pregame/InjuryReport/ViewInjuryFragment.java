package com.example.pregame.InjuryReport;

import android.graphics.Typeface;
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

import com.example.pregame.Model.Injury;
import com.example.pregame.Profile.ProfileFragment;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewInjuryFragment extends Fragment {
    public static final String TAG = "ViewInjuryFragment";
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;
    private ArrayList<Injury> injuries;
    private LinearLayout injuryTable;

    public ViewInjuryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_injury, container, false);

        setup();
        setTableRow("Body Part", "Date", "Length of Injury", true, false, null);
        getInjuryDetails();

        return view;
    }

    private void getInjuryDetails() {
        firebaseFirestore.collection("player").document(currentUser.getUid()).collection("injuryReport").get()
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

    private void setup() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        injuries = new ArrayList<>();
        injuryTable = view.findViewById(R.id.injury_table);

        FloatingActionButton goBackBut = view.findViewById(R.id.go_back);
        goBackBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
            }
        });
    }

}