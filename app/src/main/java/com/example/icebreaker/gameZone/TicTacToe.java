package com.example.icebreaker.gameZone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.icebreaker.R;
import com.example.icebreaker.users.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TicTacToe extends AppCompatActivity {
//    private LinearLayout player1, player2;
//    private ImageView img1, img2, img3, img4, img5, img6, img7, img8, img9;
//    private TextView player1Tv, player2Tv;
//    private final List<int[]> combinationlist = new ArrayList<>();
//    private final int[][] winningPositions = {};
//    private String[] data;
//    private int senderUid = 0, senderName = 1, recieverUid = 2, recieverName = 3;
//    public User sender = new User("", "", "", "", "", "", true, false, true);
//    public User reciver = new User("", "", "", "", "", "", true, false, true);
//    private DatabaseReference databaseReference;
//    private String status = "waiting", playerTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);
//        initFields();
//        initUsers();
//        game();

    }

//    private void initFields() {
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//        player1 = findViewById(R.id.player1);
//        player2 = findViewById(R.id.player2);
//        img1 = findViewById(R.id.imageView1);
//        img2 = findViewById(R.id.imageView2);
//        img3 = findViewById(R.id.imageView3);
//        img4 = findViewById(R.id.imageView4);
//        img5 = findViewById(R.id.imageView5);
//        img6 = findViewById(R.id.imageView6);
//        img7 = findViewById(R.id.imageView7);
//        img8 = findViewById(R.id.imageView8);
//        img9 = findViewById(R.id.imageView9);
//        player1Tv = findViewById(R.id.player1Tv);
//        player2Tv = findViewById(R.id.player2Tv);
//        combinationlist.add(new int[]{0, 1, 2});
//        combinationlist.add(new int[]{3, 4, 5});
//        combinationlist.add(new int[]{6, 7 ,8});
//        combinationlist.add(new int[]{0, 3, 6});
//        combinationlist.add(new int[]{1, 4, 7});
//        combinationlist.add(new int[]{2, 5, 8});
//        combinationlist.add(new int[]{0, 4, 8});
//        combinationlist.add(new int[]{2, 4, 6});
//    }
//    public void initUsers(){
//        data = getIntent().getStringArrayExtra("data");
//        sender.setId(data[senderUid]);
//        sender.setName(data[senderName]);
//        reciver.setId(data[recieverUid]);
//        reciver.setName(data[recieverName]);
//    }
//
//    public void game(){
//        databaseReference.child("game").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                snapshot.child(sender.getId()).child("player name").getRef().setValue(sender.getName());
//            }
//            //
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//        playerTurn = sender.getId();
//    }
}