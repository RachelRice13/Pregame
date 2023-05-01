package com.example.pregame.TrainingMatch;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pregame.Model.Attendance;
import com.example.pregame.Model.MatchTraining;
import com.example.pregame.R;

import java.util.ArrayList;

public class ViewAttendanceFragment extends Fragment {
    private View view;
    public static final String TAG = "ViewAttendanceFragment";
    private MatchTraining matchTraining;
    private AttendanceAdapter attendanceAdapter;

    public ViewAttendanceFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_attendance, container, false);
        setup();
        buildRecyclerView();
        return view;
    }

    private void buildRecyclerView() {
        ArrayList<Attendance> attendance = matchTraining.getAttendance();
        RecyclerView recyclerView = view.findViewById(R.id.response_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        attendanceAdapter = new AttendanceAdapter(attendance, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(attendanceAdapter);
        filter("Yes");
    }

    private void filter(String type) {
        ArrayList<Attendance> filteredList = new ArrayList<>();

        for (Attendance attendance : matchTraining.getAttendance()) {
            if (attendance.getResponse().equals(type)) {
                filteredList.add(attendance);
            }
        }
        attendanceAdapter.filteredList(filteredList);
    }

    private void setup() {
        Bundle bundle = getArguments();
        matchTraining = (MatchTraining) bundle.getSerializable("matchTraining");

        buttonSetup(R.id.yes_response_button, "Yes");
        buttonSetup(R.id.no_response_button, "No");
        buttonSetup(R.id.pending_response_button, "Hasn't Responded");
    }

    private void buttonSetup(int buttonId, String type) {
        Button button = view.findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter(type);
            }
        });
    }
}