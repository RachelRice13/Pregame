package com.example.pregame.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.Model.Message;
import com.example.pregame.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ExampleViewHolder> {
    List<Message> messageList;
    String userName;
    boolean status;
    int send;
    int receive;

    public MessageAdapter(List<Message> messageList, String userName) {
        this.messageList = messageList;
        this.userName = userName;
        status = false;
        send = 1;
        receive = 2;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            if (status) {
                textView = itemView.findViewById(R.id.send_tv);
            } else {
                textView = itemView.findViewById(R.id.received_tv);
            }
        }
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == send)  {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_send,parent,false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_received,parent,false);
        }
        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        holder.textView.setText(messageList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSender().equals(userName)) {
            status = true;
            return send;
        } else {
            status = false;
            return receive;
        }
    }

}
