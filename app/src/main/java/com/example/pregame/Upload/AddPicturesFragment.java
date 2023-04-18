package com.example.pregame.Upload;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pregame.HomePage.CoachHomeActivity;
import com.example.pregame.Model.Coach;
import com.example.pregame.Model.Folder;
import com.example.pregame.Model.Player;
import com.example.pregame.Model.Team;
import com.example.pregame.HomePage.PlayerHomeActivity;
import com.example.pregame.R;
import com.example.pregame.RegisterLogin.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AddPicturesFragment extends Fragment {
    private static final String TAG = "UploadDocsFragment";
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private List<Uri> imagesUris, videosUris;
    private ArrayList<String> imagesPaths, videosPaths;
    private Team team;
    private FragmentTransaction transaction;
    private TextView numberOfPhotosTv, numberOfVideosTv;
    private ImageView addPhotoIv, addVideoIv;
    private String mediaType, folderTitle, teamDoc, currentUserId, username, date;

    public AddPicturesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_pictures, container, false);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        transaction = getFragmentManager().beginTransaction();
        numberOfPhotosTv = view.findViewById(R.id.number_of_pictures_tv);
        numberOfVideosTv = view.findViewById(R.id.number_of_videos_tv);
        addPhotoIv = view.findViewById(R.id.add_photo_iv);
        addVideoIv = view.findViewById(R.id.add_video_iv);
        imagesUris = new ArrayList<>();
        videosUris = new ArrayList<>();
        imagesPaths = new ArrayList<>();
        videosPaths = new ArrayList<>();
        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        hideToolbarIcon();
        setTeamType();
        getNumberTvValue();
        buttonSetup();

        return view;
    }

    private void hideToolbarIcon() {
        ImageView toolbarIcon = getActivity().findViewById(R.id.toolbar_end_icon);
        toolbarIcon.setVisibility(View.INVISIBLE);
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

    private void getNumberTvValue() {
        numberOfPhotosTv.setText("Number of Photos Selected is: " + imagesUris.size());
        numberOfVideosTv.setText("Number of Videos Selected is: " + videosUris.size());
    }

    private void buttonSetup() {
        addPhotoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        addVideoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoChooser();
            }
        });

        FloatingActionButton goBack = view.findViewById(R.id.go_back_button);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, new UploadPictureFragment()).commit();
            }
        });

        Button createFolderBut = view.findViewById(R.id.create_folder_button);
        createFolderBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFolder();
            }
        });
    }

    private void imageChooser() {
        mediaType = "photo";
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    private void videoChooser() {
        mediaType = "video";
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri mediaUri = data.getData();

            if (mediaType.equals("photo")) {
                imagesUris.add(mediaUri);
            } else {
                videosUris.add(mediaUri);
            }

            getNumberTvValue();
        }
    }

    private void createFolder() {
        EditText folderTitleEt = view.findViewById(R.id.folder_title_et);
        TextInputLayout folderTitleLO = view.findViewById(R.id.folder_title);
        folderTitle = folderTitleEt.getText().toString();

        boolean validFolderTitle = RegisterActivity.validateBlank(folderTitle, folderTitleLO);
        boolean validNumberOfPhotosVideos = validateNumberOfPhotosVideos();

        if (validFolderTitle && validNumberOfPhotosVideos) {
            for (Uri uri : imagesUris) {
                uploadMedia(uri, imagesPaths);
            }

            for (Uri uri : videosUris) {
                uploadMedia(uri, videosPaths);
            }

            Folder folder = new Folder(username, date, folderTitle, imagesPaths, videosPaths);
            addToFireStore(folder);
        }
    }

    private boolean validateNumberOfPhotosVideos() {
        if (imagesUris.size() == 0 && videosUris.size() == 0) {
            Toast.makeText(getContext(), "You must select some photos or videos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getVideosType(Uri uri) {
        ContentResolver r = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(uri));
    }

    private void uploadMedia(Uri uri, ArrayList<String> paths) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading Image/Videos....");
        progressDialog.show();
        String randomId = UUID.randomUUID().toString();

        StorageReference teamPicRef = storageReference.child("teams").child(team.getTeamName()).child("team_pictures").child(folderTitle).child(randomId + "." + getVideosType(uri));
        paths.add(teamPicRef.getPath());

        teamPicRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Upload Successful");
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to upload\n" + e.getMessage());
                        progressDialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Progress: " + (int) progress + "%");
                    }
                });
    }

    private void addToFireStore(Folder folder) {
        firebaseFirestore.collection("team").document(teamDoc).collection("team_pictures").document(folderTitle)
                .set(folder)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Added folder to firestore");
                        transaction.replace(R.id.container, new UploadPictureFragment()).commit();
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