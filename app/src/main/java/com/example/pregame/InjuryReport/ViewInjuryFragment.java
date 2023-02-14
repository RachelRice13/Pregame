package com.example.pregame.InjuryReport;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pregame.Model.Injury;
import com.example.pregame.Profile.ProfileFragment;
import com.example.pregame.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewInjuryFragment extends Fragment {
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private ListenerRegistration listenerRegistration;
    private RecyclerView recyclerView;
    private InjuryReportAdapter injuryReportAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Injury> injuries;

    public ViewInjuryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_injury, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        buildRecyclerView();

        FloatingActionButton goBackBut = view.findViewById(R.id.go_back);
        goBackBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
            }
        });

        return view;
    }

    public void populateInjuries() {
        Query query = firebaseFirestore.collection("player").document(currentUser.getUid()).collection("injuryReport").orderBy("dateOfInjury", Query.Direction.ASCENDING);

        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Injury injury = change.getDocument().toObject(Injury.class).withId(change.getDocument().getId());
                        injuries.add(injury);
                        injuryReportAdapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();
            }
        });
    }

    public void buildRecyclerView() {
        injuries = new ArrayList<>();
        recyclerView = view.findViewById(R.id.injury_report_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        injuryReportAdapter = new InjuryReportAdapter(injuries, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(injuryReportAdapter);
        populateInjuries();
    }
}