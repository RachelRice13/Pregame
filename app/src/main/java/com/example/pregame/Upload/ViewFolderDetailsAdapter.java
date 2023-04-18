package com.example.pregame.Upload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ViewFolderDetailsAdapter extends RecyclerView.Adapter<ViewFolderDetailsAdapter.ExampleViewHolder> {
    public static final String TAG = "FoldersAdapter";
    private View view;
    private List<String> mediaPaths;
    private Context context;
    private StorageReference storageReference;

    public ViewFolderDetailsAdapter(List<String> mediaPaths, Context context) {
        this.mediaPaths = mediaPaths;
        this.context = context;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder  {
        MaterialCardView cardView;
        ImageView folderMediaIv;
        VideoView videoView;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference();
            cardView = itemView.findViewById(R.id.folder_media_cv);
            folderMediaIv = itemView.findViewById(R.id.picture_iv);
            videoView = itemView.findViewById(R.id.video_vv);
        }
    }

    @NonNull
    @Override
    public ViewFolderDetailsAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.row_layout_folder_media, parent, false);
        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewFolderDetailsAdapter.ExampleViewHolder holder, int position) {
        String mediaPath = mediaPaths.get(position);
        String[] splits = mediaPath.split("\\.");

        if (splits[1].equals("mp4")) {
            holder.folderMediaIv.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.VISIBLE);

            holder.videoView.setVideoPath(mediaPath);
            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(holder.videoView);
            mediaController.setMediaPlayer(holder.videoView);
            holder.videoView.setMediaController(mediaController);
            holder.videoView.start();
        } else {
            holder.videoView.setVisibility(View.GONE);
            holder.folderMediaIv.setVisibility(View.VISIBLE);

            StorageReference imageRef = storageReference.child(mediaPath);
            imageRef.getBytes(1024*1024)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            holder.folderMediaIv.setImageBitmap(bitmap);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            holder.folderMediaIv.setImageResource(R.drawable.ic_image);
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return mediaPaths.size();
    }
}