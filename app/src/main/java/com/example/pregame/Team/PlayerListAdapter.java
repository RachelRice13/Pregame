package com.example.pregame.Team;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.Model.User;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ExampleViewHolder>{
    public static final String PLAYER = "Player";
    private final List<User> players;
    private final Context context;
    private StorageReference storageReference;
    private final FragmentManager fragmentManager;

    public PlayerListAdapter(List<User> players, Context context, FragmentManager fragmentManager) {
        this.players = players;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView fullNameTv;
        ImageView profilePic;
        MaterialCardView cardView;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference();
            fullNameTv = itemView.findViewById(R.id.player_full_name);
            profilePic = itemView.findViewById(R.id.profile_pic);
            cardView = itemView.findViewById(R.id.player_card_view);
        }

        @Override
        public void onClick(View view) {
            int position = this.getLayoutPosition();
            User player = players.get(position);

            Intent intent = new Intent(view.getContext(), TeamFragment.class);
            intent.putExtra(PLAYER, (Serializable) player);
            view.getContext().startActivity(intent);
        }
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_layout_player, parent, false);

        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        User player = players.get(position);
        holder.fullNameTv.setText(player.getFirstName() + " " + player.getSurname());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPlayerDetailsFragment viewPlayerDetailsFragment = new ViewPlayerDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", player);
                viewPlayerDetailsFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, viewPlayerDetailsFragment).commit();
            }
        });

        storageReference.child("users").child(player.UserId).child("profile_pic")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri.toString()).fit().centerCrop().into(holder.profilePic);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.profilePic.setImageResource(R.drawable.ic_profile);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }
}
