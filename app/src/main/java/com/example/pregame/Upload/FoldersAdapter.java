package com.example.pregame.Upload;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.Model.Folder;
import com.example.pregame.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.ExampleViewHolder> {
    public static final String TAG = "FoldersAdapter";
    private View view;
    private List<Folder> folders;
    private Context context;
    private FragmentManager fragmentManager;

    public FoldersAdapter (List<Folder> folders, Context context, FragmentManager fragmentManager) {
        this.folders = folders;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder  {
        TextView folderTitleTv, folderDateTv, folderUserTv, numOfPhotosTv, numOfVideosTv;
        MaterialCardView cardView;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            folderTitleTv = itemView.findViewById(R.id.folder_title_tv);
            folderDateTv = itemView.findViewById(R.id.folder_date_tv);
            folderUserTv = itemView.findViewById(R.id.folder_user_upload_tv);
            numOfPhotosTv = itemView.findViewById(R.id.folder_num_of_photos_tv);
            numOfVideosTv = itemView.findViewById(R.id.folder_num_of_videos_tv);
            cardView = itemView.findViewById(R.id.folder_cv);
        }
    }

    @NonNull
    @Override
    public FoldersAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.row_layout_media_folders, parent, false);
        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoldersAdapter.ExampleViewHolder holder, int position) {
        Folder folder = folders.get(position);
        holder.folderTitleTv.setText(folder.getTitle());
        holder.folderDateTv.setText(folder.getDate());
        holder.folderUserTv.setText("| By " + folder.getUser());
        holder.numOfPhotosTv.setText("(" + folder.getPhotos().size() + ")");
        holder.numOfVideosTv.setText("(" + folder.getVideos().size() + ")");

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewFolderDetailsFragment viewFolderDetailsFragment = new ViewFolderDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("folder", folder);
                viewFolderDetailsFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, viewFolderDetailsFragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }
}