package com.example.pregame.Message;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pregame.Model.Message;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private View view;
    private String receiver, sender, message;
    private EditText messageEt;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView messageRecyclerview;
    private List<Message> messageList;
    private MessageAdapter adapter;

    public ChatFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_chat, container, false);

        TextView receiversNameTv = view.findViewById(R.id.receiver_name_tv);
        ImageView goBackBut = view.findViewById(R.id.back_button);
       messageEt = view.findViewById(R.id.type_message_et);
        FloatingActionButton sendMessageBut = view.findViewById(R.id.send_message_button);
       firebaseFirestore = FirebaseFirestore.getInstance();

        Bundle bundle = getArguments();
        receiver = bundle.getString("Receiver");
        sender = bundle.getString("Sender");

        receiversNameTv.setText(receiver);

        goBackBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new MessageFragment()).commit();
            }
        });

        sendMessageBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = messageEt.getText().toString();
                if (!message.equals("")) {
                    sendMessage(message);
                    messageEt.setText("");
                } else {
                    Toast.makeText(getContext(), "Enter a message to send", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getMessages();
        buildRecyclerView();

        return view;
    }

    private void sendMessage(String message) {
        Message messageDetails = new Message(sender, message);

        firebaseFirestore.collection("chat").document(sender).collection(receiver).add(messageDetails)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference reference) {
                        firebaseFirestore.collection("chat").document(receiver).collection(sender).add(messageDetails);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Chat", e.toString());
                    }
                });
    }

    public void getMessages() {
        firebaseFirestore.collection("chat").document(sender).collection(receiver).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Message message = queryDocumentSnapshot.toObject(Message.class);
                                messageList.add(message);
                                adapter.notifyDataSetChanged();
                                messageRecyclerview.scrollToPosition(messageList.size()-1);
                            }
                        }
                    }
                });
    }

    private void buildRecyclerView() {
        messageList = new ArrayList<>();
        messageRecyclerview = view.findViewById(R.id.messages_recyclerview);
        messageRecyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        adapter = new MessageAdapter(messageList, sender);
        messageRecyclerview.setLayoutManager(layoutManager);
        messageRecyclerview.setAdapter(adapter);
    }

}