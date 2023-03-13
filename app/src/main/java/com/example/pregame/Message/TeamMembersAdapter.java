package com.example.pregame.Message;

import android.content.Context;
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

import com.example.pregame.Model.Coach;
import com.example.pregame.Model.Player;
import com.example.pregame.PlayerHomeActivity;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TeamMembersAdapter extends RecyclerView.Adapter<TeamMembersAdapter.ExampleViewHolder> {
    public static final String TeamMember = "TeamMember";
    private List<String> teamMembers;
    private Context context;
    private String currentUserType, currentUserName;
    private FragmentManager fragmentManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;

    public TeamMembersAdapter(List<String> teamMembers, Context context, FragmentManager fragmentManager) {
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

            firebaseAuth = FirebaseAuth.getInstance();
            currentUser = firebaseAuth.getCurrentUser();
            firebaseFirestore = FirebaseFirestore.getInstance();
            fullNameTv = itemView.findViewById(R.id.player_full_name);
            profilePic = itemView.findViewById(R.id.profile_pic);
            cardView = itemView.findViewById(R.id.player_card_view);
        }
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.player_row_layout, parent, false);

        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        String teamMember = teamMembers.get(position);

        holder.fullNameTv.setText(teamMember);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSenderReceiverDetails(teamMember);
            }
        });
    }

    private void getSenderReceiverDetails(String teamMember) {
        if (PlayerHomeActivity.userType.equals("Player")) {
            currentUserType = "player";
        } else {
            currentUserType = "coach";
        }

        firebaseFirestore.collection(currentUserType).document(currentUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (currentUserType.equals("player")) {
                            Player player = documentSnapshot.toObject(Player.class);
                            currentUserName = player.getFirstName() + " " + player.getSurname();
                        } else {
                            Coach coach = documentSnapshot.toObject(Coach.class);
                            currentUserName = coach.getFirstName() + " " + coach.getSurname();
                        }

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
