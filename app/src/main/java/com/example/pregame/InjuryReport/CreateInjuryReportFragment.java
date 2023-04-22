package com.example.pregame.InjuryReport;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pregame.Common.Validation;
import com.example.pregame.HomePage.PlayerHomeFragment;
import com.example.pregame.Model.Injury;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

@SuppressLint("LongLogTag")
public class CreateInjuryReportFragment extends Fragment {
    private static final String TAG = "CreateInjuryReportFragment";
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;

    private TextView dateTv;
    private EditText injuryLengthNumEt;
    private Spinner lengthOfInjurySpinner;
    private CheckBox unknownCheckbox;
    private float levelOfPain = 0;
    private String date = "", bodyPart, lengthOfInjury, seenPhysio;
    private FragmentTransaction transaction;

    public CreateInjuryReportFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_create_injury_report, container, false);

        setup();
        setInjurySpinner(lengthOfInjurySpinner);
        unknownCheckbox(unknownCheckbox, injuryLengthNumEt, lengthOfInjurySpinner);
        setDatePicker();
        getSlider();

       return view;
    }

    private void submitInjuryReport() {
        EditText injuryDescriptionEt = view.findViewById(R.id.injury_description_et);
        TextInputLayout injuryDescriptionLo = view.findViewById(R.id.injury_description);
        EditText otherDetailsEt = view.findViewById(R.id.other_details_et);

        String injuryDescription = injuryDescriptionEt.getText().toString();
        String otherDetails = otherDetailsEt.getText().toString();

        getSelectedRB();

        boolean validDescription = Validation.validateBlank(injuryDescription, injuryDescriptionLo);
        boolean validDate = Validation.validateString(date);
        boolean validSeenPhysio = Validation.validateSeenPhysio(seenPhysio);
        boolean validLengthOfInjury;

        if (unknownCheckbox.isChecked()) {
            lengthOfInjury = "Unknown";
            validLengthOfInjury = true;
        } else {
            if (!injuryLengthNumEt.getText().toString().isEmpty()) {
                int lengthNum = Integer.parseInt(injuryLengthNumEt.getText().toString());
                lengthOfInjury = lengthNum + " " + lengthOfInjurySpinner.getSelectedItem().toString();
                validLengthOfInjury = Validation.validateLengthOfInjury(lengthOfInjury);
            } else {
                Toast.makeText(getContext(), "Need to enter a number for length of injury", Toast.LENGTH_SHORT).show();
                validLengthOfInjury = false;
            }
        }

        if (validDescription && validDate && validSeenPhysio && validLengthOfInjury) {
            Injury injury = new Injury(bodyPart, injuryDescription, lengthOfInjury, otherDetails, date, levelOfPain, seenPhysio);
            addToFireStore(injury);
        }
    }

    private void addToFireStore(Injury injury) {
        firebaseFirestore.collection("player").document(currentUser.getUid()).collection("injuryReport")
                .add(injury)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference reference) {
                        Snackbar.make(view, "Successfully created injury report.", Snackbar.LENGTH_SHORT).show();
                        transaction.replace(R.id.container, new PlayerHomeFragment()).commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.toString());
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

    private void setInjurySpinner(Spinner injurySpinner) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.injury_length_options));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        injurySpinner.setAdapter(spinnerArrayAdapter);
    }

    private void getSelectedRB() {
        RadioGroup seenPhysioGroup = view.findViewById(R.id.radio_group);
        int selectedButton = seenPhysioGroup.getCheckedRadioButtonId();

        if (selectedButton == -1) {
            seenPhysio = "Nothing Selected";
        } else if (selectedButton == R.id.radio_yes){
            seenPhysio = "Yes";
        } else if (selectedButton == R.id.radio_no){
            seenPhysio = "No";
        }
    }

    private void getSlider() {
        Slider levelOfPainSlider = view.findViewById(R.id.level_of_pain_slider);
        levelOfPainSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                levelOfPain = value;
            }
        });
    }

    private void unknownCheckbox(CheckBox checkBox, EditText editText, Spinner spinner) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    editText.setEnabled(false);
                    spinner.setEnabled(false);
                } else {
                    editText.setEnabled(true);
                    spinner.setEnabled(true);
                }
            }
        });
    }

    private void setup() {
        transaction = getFragmentManager().beginTransaction();
        Bundle bundle = getArguments();
        bodyPart = bundle.getString("injuredArea");
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        dateTv = view.findViewById(R.id.date_tv);
        injuryLengthNumEt = view.findViewById(R.id.injury_length_number);
        unknownCheckbox = view.findViewById(R.id.unknown_checkbox);
        lengthOfInjurySpinner = view.findViewById(R.id.injury_length_spinner);

        FloatingActionButton goBackBut = view.findViewById(R.id.go_back);
        goBackBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, new InjuryReportFragment()).commit();
            }
        });

        Button submitBut = view.findViewById(R.id.submit_injury_button);
        submitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitInjuryReport();
            }
        });
    }
}