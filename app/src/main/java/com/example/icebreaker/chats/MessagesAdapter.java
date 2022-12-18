package com.example.icebreaker.chats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icebreaker.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class MessagesAdapter extends RecyclerView.Adapter {

    private final Context context;
    private final ArrayList<Message> messageArrayList;
    int SEND = 1;
    int RECIEVE = 2;

    public MessagesAdapter(Context context, ArrayList<Message> messageArrayList) {
        this.context = context;
        this.messageArrayList = messageArrayList;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        String Writer = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        if (Writer.equals(message.getSenderId())){
            return SEND;
        }
        else return RECIEVE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.send_message,parent,false);
            return new SenderViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.recieve_message,parent,false);
            return new recieverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        if (holder.getClass() == SenderViewHolder.class){
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.time.setText(message.getCurrentTime());
            viewHolder.message.setText(message.getMessage());
        }
        else{
            recieverViewHolder viewHolder = (recieverViewHolder) holder;
            viewHolder.time.setText(message.getCurrentTime());
            viewHolder.message.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    static class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView message;
        TextView time;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.sendermessage);
            time = itemView.findViewById(R.id.timeMessage);
        }
    }

    static class recieverViewHolder extends RecyclerView.ViewHolder {

        TextView message;
        TextView time;

        public recieverViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.recievermessage);
            time = itemView.findViewById(R.id.timeMessage);
        }
    }
}