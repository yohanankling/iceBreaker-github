//package com.example.icebreaker.chats;
//
//import android.content.Context;
//import android.os.Message;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.icebreaker.R;
//
//import java.util.ArrayList;
//
//public class chatListAdapter extends RecyclerView.Adapter {
//
//    Context context;
//    ArrayList<Messages> messagesArrayList;
//    int ITEM_SEND = 1;
//    int ITEM_RECIVED = 2;
//
//    public chatListAdapter(Context context, ArrayList<Messages> messagesArrayList) {
//        this.context = context;
//        this.messagesArrayList = messagesArrayList;
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }
//
//    class SenderViewHolder extends RecyclerView.ViewHolder {
//        TextView textViewmessage;
//        TextView timeofmessage;
//        public SenderViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textViewmessage = itemView.findViewById(R.id)
//        }
//    }
//}
