package com.example.pregame.Upload;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pregame.Common.Validation;
import com.example.pregame.HomePage.CoachHomeActivity;
import com.example.pregame.Model.Coach;
import com.example.pregame.Model.Media;
import com.example.pregame.Model.Player;
import com.example.pregame.Model.Team;
import com.example.pregame.HomePage.PlayerHomeActivity;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UploadDocsFragment extends Fragment {
    private static final String TAG = "UploadDocsFragment";
    private View view;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private Team team;
    private String documentName, currentUserId, teamDoc, username;
    private ArrayList<Media> documents;
    private DocumentsAdapter documentsAdapter;
    private ListenerRegistration listenerRegistration;

    public UploadDocsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_upload_docs, container, false);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        setTeamType();
        setToolbarIcon();

        return view;
    }

    private void setTeamType() {
        if (PlayerHomeActivity.userType.equals("Player")) {
            team = PlayerHomeActivity.currentTeam;
            getDetails("player");
        } else {
            team = CoachHomeActivity.currentTeam;
            getDetails("coach");
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

    private void getDetails(String userType) {
        firebaseFirestore.collection(userType).document(currentUserId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (userType.equals("player")) {
                            Player player = documentSnapshot.toObject(Player.class);
                            username = player.getFirstName() + " " + player.getSurname();
                        } else {
                            Coach coach = documentSnapshot.toObject(Coach.class);
                            username = coach.getFirstName() + " " + coach.getSurname();
                        }
                    }
                });
    }

    private void setToolbarIcon() {
        ImageView toolbarIcon = getActivity().findViewById(R.id.toolbar_end_icon);
        toolbarIcon.setVisibility(View.VISIBLE);
        toolbarIcon.setImageResource(R.drawable.ic_add);

        toolbarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();
            }
        });
    }

    private void buildRecyclerView() {
        documents = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.documents_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        documentsAdapter = new DocumentsAdapter(documents, getContext(), teamDoc);

        if (CoachHomeActivity.userType.equals("Coach")) {
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new DocumentTouchHelper(documentsAdapter));
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(documentsAdapter);
        populateList();
    }

    private void populateList() {
        Query query = firebaseFirestore.collection("team").document(teamDoc).collection("documents").orderBy("date", Query.Direction.DESCENDING);
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Media document = change.getDocument().toObject(Media.class).withId(change.getDocument().getId());
                        documents.add(document);
                        documentsAdapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();
            }
        });
    }

    private void chooseFile() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.add_document_name, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        EditText documentNameET = view.findViewById(R.id.document_name_et);
        TextInputLayout documentNameLo = view.findViewById(R.id.document_name);
        FloatingActionButton goBack = view.findViewById(R.id.go_back);
        Button chooseButton = view.findViewById(R.id.choose_button);

        alertDialog.setCancelable(false).setView(view);
        AlertDialog alert = alertDialog.create();
        alert.show();

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentName = documentNameET.getText().toString();
                boolean validDocumentName = Validation.validateBlank(documentName, documentNameLo);

                if (validDocumentName) {
                    Intent intent = new Intent();
                    intent.setType("application/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Files...."), 1);
                    alert.cancel();
                }
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uploadFiles(data.getData());
        }
    }

    private void uploadFiles(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading File....");
        progressDialog.show();

        StorageReference fileRef = storageReference.child("teams").child(team.getTeamName()).child("team_files").child(documentName);
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        fileRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar.make(view, "File Uploaded", Snackbar.LENGTH_LONG).show();
                        Media media = new Media(username, fileRef.getPath(), date, documentName);
                        addToFireStore(media);
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to Upload File", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Progress: " + (int) progressPercent + "%");
                    }
                });
    }

    private void addToFireStore(Media media) {
        firebaseFirestore.collection("team").document(teamDoc).collection("documents")
                .add(media)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference reference) {
                        Log.d(TAG, "Added file to firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.toString());
                    }
                });
    }
}