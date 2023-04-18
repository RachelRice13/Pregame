package com.example.pregame.Upload;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pregame.Model.Folder;
import com.example.pregame.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

@SuppressLint("LongLogTag")
public class ViewFolderDetailsFragment extends Fragment {
    public static final String TAG = "ViewFolderDetailsFragment";
    private View view;
    private Folder folder;
    private ArrayList<String> mediaPaths;
    private ViewFolderDetailsAdapter viewFolderDetailsAdapter;

    public ViewFolderDetailsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_folder_details, container, false);

        Bundle bundle = getArguments();
        folder = (Folder) bundle.getSerializable("folder");

        mediaPaths = new ArrayList<>();

        getDetails();
        setToolbarIcon();
        buttonSetup();

        return  view;
    }

    private void getDetails() {
        TextView folderTitleTv, folderDateTv, folderUserTv, numOfPhotosTv, numOfVideosTv;
        folderTitleTv = view.findViewById(R.id.folder_title_tv);
        folderDateTv = view.findViewById(R.id.folder_date_tv);
        folderUserTv = view.findViewById(R.id.folder_user_upload_tv);
        numOfPhotosTv = view.findViewById(R.id.folder_num_of_photos_tv);
        numOfVideosTv = view.findViewById(R.id.folder_num_of_videos_tv);

        folderTitleTv.setText(folder.getTitle());
        folderDateTv.setText(folder.getDate());
        folderUserTv.setText(" | By " + folder.getUser());
        numOfPhotosTv.setText(folder.getPhotos().size() + " Photos");
        numOfVideosTv.setText(folder.getVideos().size() + " Videos");

        mediaPaths.addAll(folder.getPhotos());
        mediaPaths.addAll(folder.getVideos());

        buildRecyclerView();
    }

    private void buildRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.pictures_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        viewFolderDetailsAdapter = new ViewFolderDetailsAdapter(mediaPaths, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(viewFolderDetailsAdapter);
    }


    private void setToolbarIcon() {
        ImageView toolbarIcon = getActivity().findViewById(R.id.toolbar_end_icon);
        toolbarIcon.setVisibility(View.GONE);
    }

    private void buttonSetup() {
        FloatingActionButton goBack = view.findViewById(R.id.go_back);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new UploadPictureFragment()).commit();
            }
        });
    }
}