package com.example.icebreaker.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icebreaker.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class chatActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    LinearLayoutManager linearLayoutManager;
    private FirebaseAuth firebaseAuth;
    FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder > chatAdapter;
    RecyclerView mrecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatfraggment);
        initFields();
    }

    private void initFields() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mrecyclerView = findViewById(R.id.recyclerview);
        Query query = firebaseFirestore.collection("Users");
        FirestoreRecyclerOptions<firebasemodel> allusername = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();
        chatAdapter = new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusername) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull firebasemodel firebasemodel) {

                noteViewHolder.username.setText(firebasemodel.Email);
                noteViewHolder.status.setText(firebasemodel.getStatus());
                if (firebasemodel.getStatus().equals("online")){
                    noteViewHolder.status.setTextColor(Color.GREEN);
                }
                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(chatActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatviewlayout, parent, false);
                return new NoteViewHolder(view);
            }
        };
        mrecyclerView.setHasFixedSize(true);
        // TODO     50+-   linearLayoutManager = new LinearLayoutManager(getContext()); , this, chatfraggment
        linearLayoutManager = new LinearLayoutManager(mrecyclerView.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mrecyclerView.setLayoutManager(linearLayoutManager);
        mrecyclerView.setAdapter(chatAdapter);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{

        private TextView username;
        private TextView status;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.UserName);
            status = itemView.findViewById(R.id.Status);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatAdapter.startListening();
        status("online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (chatAdapter != null){
            chatAdapter.stopListening();
            status("offline");
        }
    }

    private void status(String status) {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status", status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }
}