package com.example.icebreaker.topic;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icebreaker.R;
import com.example.icebreaker.chats.Chat;
import com.example.icebreaker.chats.FirebaseUser;

import java.util.ArrayList;

public class TopicAdapter extends RecyclerView.Adapter {

    private final Context context;
    private final ArrayList<FirebaseUser> usersArrayList;
    private final String MyName;


    public TopicAdapter(Context context, ArrayList<FirebaseUser> usersArrayList, Intent intent) {
        this.context = context;
        this.usersArrayList = usersArrayList;
        MyName = intent.getStringExtra("MyName");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.chatlistview, parent, false);
            return new userDetailes(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FirebaseUser firebaseUser = usersArrayList.get(position);
        userDetailes viewHolder = (userDetailes) holder;
            viewHolder.username.setText(" " + firebaseUser.getEmail() + " ");
            viewHolder.status.setText(firebaseUser.getStatus());
            viewHolder.send.setOnClickListener(v -> {
                Intent intent = new Intent(context, Chat.class);
                intent.putExtra("Uid", firebaseUser.getUid());
                intent.putExtra("Email", firebaseUser.getEmail());
                intent.putExtra("MyName", MyName);
                context.startActivity(intent);
            });
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    static class userDetailes extends RecyclerView.ViewHolder {

        private final TextView username;
        private final TextView status;
        private final ImageView send;

        public userDetailes(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.UserName);
            status = itemView.findViewById(R.id.Status);
            send = itemView.findViewById(R.id.send);

        }
    }
}