package com.example.pregame.InjuryReport;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pregame.R;

@SuppressLint("ClickableViewAccessibility")
public class InjuryReportFragment extends Fragment {
    private View view;
    private RelativeLayout mainLayout;
    private TextView selectedAreaTv;
    private String selectedArea;
    private boolean front = true;

    public InjuryReportFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_injury_report, container, false);

        setup();

        return view;
    }

    private void createItem(int xCord, int yCord) {
        selectedAreaTv = new TextView(getContext());
        selectedAreaTv.setBackgroundResource(R.drawable.offensive_player);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(20, 20);
        params.setMargins(xCord, yCord, 0, 0);
        selectedAreaTv.setLayoutParams(params);
        selectedAreaTv.setTag("TAG");
        mainLayout.addView(selectedAreaTv);
    }

    private void getBodyPart(int xCord, int yCord) {
        selectBodyPartBack(xCord, 450, 650, yCord, 360, 540, "Head", "Head");
        selectBodyPartBack(xCord, 470, 610, yCord, 541, 610, "Neck", "Neck");
        selectBodyPartBack(xCord, 430, 670, yCord, 611, 900, "Chest", "Upper Back");
        selectBodyPartBack(xCord, 430, 670, yCord, 901, 1100, "Stomach", "Lower Back");

        selectBodyPartBack(xCord, 370, 540, yCord, 1101, 1295, "Right Quad", "Left Hamstring");
        selectBodyPartBack(xCord, 410, 520, yCord, 1300, 1450, "Right Knee", "Left Knee");
        selectBodyPartBack(xCord, 390, 515, yCord, 1451, 1670, "Right Shin", "Left Calf");
        selectBodyPartBack(xCord, 345, 490, yCord, 1671, 1820, "Right Foot", "Left Foot");

        selectBodyPartBack(xCord, 335, 429, yCord, 610, 715, "Right Shoulder", "Left Shoulder");
        selectBodyPartBack(xCord, 315, 429, yCord, 716, 825, "Right Bicep", "Left Tricep");
        selectBodyPartBack(xCord, 255, 395, yCord, 826, 1005, "Right Forearm", "Left Forearm");
        selectBodyPartBack(xCord, 180, 330, yCord, 1006, 1121, "Right Hand", "Left Hand");

        selectBodyPartBack(xCord, 550, 720, yCord, 1101, 1295, "Left Quad", "Right Hamstring");
        selectBodyPartBack(xCord, 575, 685, yCord, 1300, 1445, "Left Knee", "Right Knee");
        selectBodyPartBack(xCord, 580, 705, yCord, 1450, 1670, "Left Shin", "Right Calf");
        selectBodyPartBack(xCord, 610, 755, yCord, 1675, 1820, "Left Foot", "Right Foot");

        selectBodyPartBack(xCord, 671, 755, yCord, 610, 715, "Left Shoulder", "Right Shoulder");
        selectBodyPartBack(xCord, 671, 780, yCord, 716, 825, "Left Bicep", "Right Tricep");
        selectBodyPartBack(xCord, 700, 840, yCord, 826, 1005, "Left Forearm", "Right Forearm");
        selectBodyPartBack(xCord, 765, 905, yCord, 1006, 1121, "Left Hand", "Right Hand");
    }

    private void selectBodyPartBack(int xCord, int xCordGreater, int xCordLess, int yCord, int yCordGreater, int yCordLess, String frontBodyPart, String backBodyPart) {
        if (xCord >= xCordGreater && xCord <= xCordLess && yCord >= yCordGreater && yCord <= yCordLess) {
            if (front)
                selectedArea = frontBodyPart;
            else
                selectedArea = backBodyPart;
        }
    }

    private void createInjuryReport() {
        if (!selectedArea.equals("")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            alertDialogBuilder
                    .setCancelable(false)
                    .setTitle("Create Injury Report")
                    .setMessage("Do you want to create an injury report for your " + selectedArea + "?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            CreateInjuryReportFragment createInjuryReportFragment = new CreateInjuryReportFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("injuredArea", selectedArea);
                            createInjuryReportFragment.setArguments(bundle);
                            getFragmentManager().beginTransaction().replace(R.id.container, createInjuryReportFragment).commit();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mainLayout.removeView(selectedAreaTv);
                            selectedArea = "";
                            dialogInterface.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
    }

    private void setup() {
        TextView humanBodyTv = view.findViewById(R.id.human_body);
        TextView rightTv = view.findViewById(R.id.right_tv);
        TextView leftTv = view.findViewById(R.id.left_tv);

        setFrontBackButton(R.id.front_body_button, R.id.back_body_button, humanBodyTv, R.drawable.human_body_front, rightTv, R.string.right, leftTv, R.string.left, true);
        setFrontBackButton(R.id.back_body_button, R.id.front_body_button, humanBodyTv, R.drawable.human_body_back, rightTv, R.string.left, leftTv, R.string.right, false);

        mainLayout = view.findViewById(R.id.main_layout);
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    int xCord = (int) motionEvent.getX();
                    int yCord = (int) motionEvent.getY();

                    if (selectedAreaTv != null)
                        mainLayout.removeView(selectedAreaTv);

                    createItem(xCord, yCord);
                    selectedArea = "";
                    getBodyPart(xCord, yCord);
                    createInjuryReport();
                }
                return true;
            }
        });
    }

    private void setFrontBackButton(int id, int secondId, TextView humanBodyTv, int humanBody, TextView rightTv, int rightText, TextView leftTv, int leftText, boolean value) {
        Button button = view.findViewById(id);
        Button secondButton = view.findViewById(secondId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                humanBodyTv.setBackgroundResource(humanBody);
                rightTv.setText(rightText);
                leftTv.setText(leftText);
                front = value;
                button.setBackgroundColor(getResources().getColor(R.color.light_grey));
                secondButton.setBackgroundColor(getResources().getColor(R.color.grey));
            }
        });
    }
}