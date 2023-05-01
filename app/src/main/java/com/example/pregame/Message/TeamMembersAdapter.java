package com.example.pregame.Message;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.HomePage.PlayerHomeActivity;
import com.example.pregame.Model.User;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TeamMembersAdapter extends RecyclerView.Adapter<TeamMembersAdapter.ExampleViewHolder> {
    private final List<User> teamMembers;
    private final Context context;
    private String currentUserName;
    private final FragmentManager fragmentManager;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseUser currentUser;

    public TeamMembersAdapter(List<User> teamMembers, Context context, FragmentManager fragmentManager) {
        this.teamMembers = teamMembers;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTv;
        ImageView profilePic;
        MaterialCardView cardView;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            currentUser = firebaseAuth.getCurrentUser();
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference();
            firebaseFirestore = FirebaseFirestore.getInstance();
            fullNameTv = itemView.findViewById(R.id.player_full_name);
            profilePic = itemView.findViewById(R.id.profile_pic);
            cardView = itemView.findViewById(R.id.player_card_view);
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
        User teamMember = teamMembers.get(position);
        String fullName = teamMember.getFirstName() + " " + teamMember.getSurname();

        holder.fullNameTv.setText(fullName);

        storageReference.child("users").child(teamMember.UserId).child("profile_pic")
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

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSenderReceiverDetails(fullName);
            }
        });
    }

    private void getSenderReceiverDetails(String teamMember) {
        String currentUserType;
        if (PlayerHomeActivity.userType.equals("Player")) {
            currentUserType = "player";
        } else {
            currentUserType = "coach";
        }

        firebaseFirestore.collection(currentUserType).document(currentUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        currentUserName = user.getFirstName() + " " + user.getSurname();

                        ChatFragment chatFragment = new ChatFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("Receiver", teamMember);
                        bundle.putString("Sender", currentUserName);
                        chatFragment.setArguments(bundle);
                        fragmentManager.beginTransaction().replace(R.id.container, chatFragment).commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Chat", e.toString());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return teamMembers.size();
    }

}
