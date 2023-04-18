package com.example.pregame.Upload;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.Common.Validation;
import com.example.pregame.Model.Media;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DocumentsAdapter extends RecyclerView.Adapter<DocumentsAdapter.ExampleViewHolder>{
    public static final String TAG = "DocumentsAdapter";
    private View view;
    private List<Media> documents;
    private Context context;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private String teamDoc;

    public DocumentsAdapter (List<Media> documents, Context context, String teamDoc) {
        this.documents = documents;
        this.context = context;
        this.teamDoc = teamDoc;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder  {
        TextView documentTitleTv, documentDateTv, documentUserTv;
        MaterialCardView cardView;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference();
            firebaseFirestore = FirebaseFirestore.getInstance();
            documentTitleTv = itemView.findViewById(R.id.document_title_tv);
            documentDateTv = itemView.findViewById(R.id.document_date_tv);
            documentUserTv = itemView.findViewById(R.id.document_user_upload_tv);
            cardView = itemView.findViewById(R.id.document_cv);
        }
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.document_row_layout, parent, false);
        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        Media document = documents.get(position);
        StorageReference documentRef = storageReference.child(document.getMediaPath());
        File localFile = null;
        try {
            localFile = File.createTempFile("application", "*");
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.documentTitleTv.setText(document.getTitle());
        holder.documentDateTv.setText(document.getDate());
        holder.documentUserTv.setText("| By " + document.getUser());

        File finalLocalFile = localFile;
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documentRef.getFile(finalLocalFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.e(TAG, "Successfully downloaded " + document.getTitle());
                                Snackbar.make(view, "Successfully downloaded " + document.getTitle(), Snackbar.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Failed to download " + document.getTitle() + "\n" + e.getMessage());
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public Context getContext() {
        return context;
    }

    public void deleteDocument(int position) {
        Media document = documents.get(position);
        firebaseFirestore.collection("team").document(teamDoc).collection("documents").document(document.MediaId).delete();
        storageReference.child(document.getMediaPath()).delete();
        documents.remove(position);
        notifyItemRemoved(position);
        Snackbar.make(view, "Deleted document", Snackbar.LENGTH_LONG).show();
    }

    public void updateDocument(int position) {
        Media document = documents.get(position);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.add_document_name, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        EditText documentNameEt = view.findViewById(R.id.document_name_et);
        TextInputLayout documentNameLO = view.findViewById(R.id.document_name);
        Button updateNameButton = view.findViewById(R.id.choose_button);
        FloatingActionButton goBack = view.findViewById(R.id.go_back);

        alertDialog.setCancelable(false).setView(view);
        AlertDialog alert = alertDialog.create();
        alert.show();

        documentNameEt.setText(document.getTitle());
        updateNameButton.setText("Update");

        updateNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String documentName = documentNameEt.getText().toString();
                boolean validDocumentName = Validation.validateBlank(documentName, documentNameLO);

                if (validDocumentName) {
                    firebaseFirestore.collection("team").document(teamDoc).collection("documents").document(document.MediaId)
                            .update("title", documentName)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "Successfully updated document name");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Failed to update document name\n" + e.getMessage());
                                }
                            });
                    notifyDataSetChanged();
                    alert.cancel();
                }
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyItemChanged(position);
                alert.cancel();
            }
        });
    }
}