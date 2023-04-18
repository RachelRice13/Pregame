package com.example.pregame.Upload;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.pregame.CoachHomeActivity;
import com.example.pregame.Model.Folder;
import com.example.pregame.Model.Team;
import com.example.pregame.PlayerHomeActivity;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UploadPictureFragment extends Fragment {
    private static final String TAG = "UploadPictureFragment";
    private View view;
    private List<Folder> folders;
    private FirebaseFirestore firebaseFirestore;
    private Team team;
    private FragmentTransaction transaction;
    private String currentUserId, teamDoc;
    private ListenerRegistration listenerRegistration;
    private FoldersAdapter foldersAdapter;

    public UploadPictureFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_upload_picture, container, false);

        transaction = getFragmentManager().beginTransaction();
        firebaseFirestore = FirebaseFirestore.getInstance();
        folders = new ArrayList<>();

        setToolbarIcon();
        setTeamType();

        return view;
    }

    private void setToolbarIcon() {
        ImageView toolbarIcon = getActivity().findViewById(R.id.toolbar_end_icon);
        toolbarIcon.setVisibility(View.VISIBLE);
        toolbarIcon.setImageResource(R.drawable.ic_add);

        toolbarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, new AddPicturesFragment()).commit();
            }
        });
    }

    private void setTeamType() {
        if (PlayerHomeActivity.userType.equals("Player")) {
            team = PlayerHomeActivity.currentTeam;
        } else {
            team = CoachHomeActivity.currentTeam;
        }

        firebaseFirestore.collection("team").whereEqualTo("teamName", team.getTeamName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                teamDoc = queryDocumentSnapshot.getId();
                                buildRecyclerView();
                            }
                        }
                    }
                });
    }

    private void buildRecyclerView() {
        folders = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.folders_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        foldersAdapter = new FoldersAdapter(folders, getContext(), getFragmentManager());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(foldersAdapter);
        populateList();
    }

    private void populateList() {
        Query query = firebaseFirestore.collection("team").document(teamDoc).collection("team_pictures").orderBy("date", Query.Direction.DESCENDING);
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Folder folder = change.getDocument().toObject(Folder.class).withId(change.getDocument().getId());
                        folders.add(folder);
                        foldersAdapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();
            }
        });
    }
}