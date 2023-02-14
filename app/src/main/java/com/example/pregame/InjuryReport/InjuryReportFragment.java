package com.example.pregame.InjuryReport;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pregame.Model.Injury;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InjuryReportFragment extends Fragment {
    private static final String TAG = "InjuryReport";
    private View view;
    private TextView dateTv;
    private RadioGroup seenPhysioGroup;
    private SeekBar levelOfPainSB;
    private String lengthOfInjury;
    private int levelOfPain;
    private boolean seenPhysio;
    private Date date;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;

    public InjuryReportFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_injury_report, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        ImageButton humanBodyButton = view.findViewById(R.id.human_body);
        humanBodyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               setInjuryDetails("Body");
           }
        });

        return view;
    }

    public void setInjuryDetails(String bodyPart) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.injury_details, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        EditText injuryDescEt = view.findViewById(R.id.injury_description_et);
        dateTv = view.findViewById(R.id.date_tv);
        levelOfPainSB = view.findViewById(R.id.pain_level_seekbar);
        seenPhysioGroup = view.findViewById(R.id.radio_group);
        EditText lengthOfInjuryEt = view.findViewById(R.id.injury_length_number);
        Spinner lengthOfInjuryS = view.findViewById(R.id.injury_length_string);
        CheckBox unknownCB = view.findViewById(R.id.unknown_checkbox);
        EditText otherDetailsEt = view.findViewById(R.id.other_details_et);
        FloatingActionButton cancel = view.findViewById(R.id.go_back);
        Button submit = view.findViewById(R.id.submit_injury_button);

        alertDialog.setCancelable(false).setView(view);
        AlertDialog alert = alertDialog.create();
        alert.show();

        setDatePicker();
        getSeekBar();
        setInjurySpinner(lengthOfInjuryS);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String injuryDesc = injuryDescEt.getText().toString();
                String otherDetails = otherDetailsEt.getText().toString();
                int lengthOfInjuryInt = Integer.parseInt(lengthOfInjuryEt.getText().toString());
                String spinnerOption = lengthOfInjuryS.getSelectedItem().toString();
                lengthOfInjury = lengthOfInjuryInt + " " + spinnerOption;

                getSelectedRB();

                Injury newInjury = new Injury(bodyPart, injuryDesc, lengthOfInjury, otherDetails, date, levelOfPain, seenPhysio);

                // Add to Firestore
                firebaseFirestore.collection("player").document(currentUser.getUid()).collection("injuryReport")
                        .add(newInjury)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference reference) {
                                Log.d(TAG, "Added injury to firestore");
                                alert.cancel();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, e.toString());
                                alert.cancel();
                            }
                        });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
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
                        dateTv.setText(dayOfMonth + "/" + month + "/" + year);
                        String dateString = dayOfMonth + "/" + month + "/" + year;
                        try {
                            date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, YEAR, MONTH, DAY);
                datePickerDialog.show();
            }
        });
    }

    private void getSelectedRB() {
        int selectedButton = seenPhysioGroup.getCheckedRadioButtonId();

        if (selectedButton == -1) {
            Toast.makeText(getContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
        } else if (selectedButton == R.id.radio_yes){
            seenPhysio = true;
        } else if (selectedButton == R.id.radio_no){
            seenPhysio = false;
        }
    }

    private void getSeekBar() {
        levelOfPainSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                Log.d("SeekBar", "Progress Value: " + progress);
                levelOfPain = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("SeekBar", "Seekbar touch started");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("SeekBar", "Seekbar touch stopped");
            }
        });
    }

    private void setInjurySpinner(Spinner injurySpinner) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.injury_length_options));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        injurySpinner.setAdapter(spinnerArrayAdapter);
    }

    private void unknownCheckbox(CheckBox checkBox, EditText editText, Spinner spinner, int number, String option) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    lengthOfInjury = "Unknown";
                    editText.setEnabled(false);
                    spinner.setEnabled(false);
                } else {
                    editText.setEnabled(true);
                    spinner.setEnabled(true);

                    lengthOfInjury = number + " " + option;
                }
            }
        });
    }


}