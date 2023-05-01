package com.example.pregame.TrainingMatch;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.Model.Attendance;
import com.example.pregame.Model.User;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ExampleViewHolder> {
    private List<Attendance> attendances;
    private Context context;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;


    public AttendanceAdapter(List<Attendance> attendances, Context context) {
        this.attendances = attendances;
        this.context = context;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder  {
        TextView fullNameTv, dateTv;
        ImageView profilePicIv;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference();
            firebaseFirestore = FirebaseFirestore.getInstance();
            fullNameTv = itemView.findViewById(R.id.fullName_tv);
            dateTv = itemView.findViewById(R.id.date_tv);
            profilePicIv = itemView.findViewById(R.id.profile_pic);
        }
    }

    @NonNull
    @Override
    public AttendanceAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_layout_response, parent, false);
        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.ExampleViewHolder holder, int position) {
        Attendance attendance = attendances.get(position);
        String userId = attendance.getUser().getId();
        String responseDate;

        if (attendance.getDate().equals(""))
            responseDate = "";
        else
            responseDate = "Responded: " + attendance.getDate();

        holder.dateTv.setText(responseDate);

        firebaseFirestore.collection(attendance.getType()).document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            String fullName = user.getFirstName() + " " + user.getSurname();
                            holder.fullNameTv.setText(fullName);
                        } else {
                            String fullName = "";
                            holder.fullNameTv.setText(fullName);
                        }
                    }
                });

        storageReference.child("users").child(userId).child("profile_pic")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri.toString()).fit().centerCrop().into(holder.profilePicIv);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.profilePicIv.setImageResource(R.drawable.ic_profile);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return attendances.size();
    }

    public void filteredList(ArrayList<Attendance> filteredList) {
        attendances = filteredList;
        notifyDataSetChanged();
    }
}
