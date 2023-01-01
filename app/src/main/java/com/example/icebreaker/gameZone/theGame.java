package com.example.icebreaker.gameZone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.icebreaker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class theGame extends AppCompatActivity {

    private ConstraintLayout player1Layout, player2Layout;
    private ImageView image1,image2,image3,image4,image5,image6,image7,image8,image9;
    private TextView player1TV, player2TV;

    private final List<int[]> combinationsList = new ArrayList<>();
    private final List<String> doneBoxes = new ArrayList<>();

    private String playerUniqueId = "0";
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;

    private boolean opponentFound = false;

    private String opponentUniqueId = "0";

    private String status = "matching";
    private String  myName;

    private String playerTurn = "";
    private String connectionId = "";

    ValueEventListener turnsEventListener, wonEventListener;

    private final String [] boxesSelectedBy = {"","","","","","","","",""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_game);
        initFields();
        setGame();
        temp();
//        waitForOpponent();
        turnsListener();
        wonListener();
        boardListener();
    }

    private void temp() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Waiting For Opponent");
        progressDialog.show();

        firebaseDatabase.getReference().child("connections").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!opponentFound){

                    if(snapshot.hasChildren()){

                        for(DataSnapshot connections : snapshot.getChildren()){
                            String conId  =connections.getKey();
                            int getPlayersCount = (int)connections.getChildrenCount();
                            if(status.equals("waiting")){

                                if(getPlayersCount==2){
                                    playerTurn = playerUniqueId;
                                    applyPlayerTurn(playerTurn);

                                    boolean playerFound = false;
                                    for(DataSnapshot players : connections.getChildren()){
                                        String getPlayerUniqueId = players.getKey();
                                        if(getPlayerUniqueId.equals(playerUniqueId)){
                                            playerFound = true;
                                        }
                                        else if(playerFound){
                                            String getOpponentPlayerName = players.child("player_name").getValue(String.class);
                                            opponentUniqueId = players.getKey();
                                            player2TV.setText(getOpponentPlayerName);
                                            connectionId = conId;
                                            opponentFound = true;

                                            firebaseDatabase.getReference().child("turns").child(connectionId).addValueEventListener(turnsEventListener);
                                            firebaseDatabase.getReference().child("won").child(connectionId).addValueEventListener(wonEventListener);

                                            if(progressDialog.isShowing()){
                                                progressDialog.dismiss();
                                            }
                                            firebaseDatabase.getReference().child("connections").removeEventListener(this);

                                        }
                                    }
                                }
                            }
                            else{
                                if(getPlayersCount == 1){
                                    connections.child(playerUniqueId).child("player_name").getRef().setValue(myName);
                                    for(DataSnapshot players : connections.getChildren()){
                                        String getOpponentName = players.child("player_name").getValue(String.class);
                                        opponentUniqueId = players.getKey();
                                        playerTurn = opponentUniqueId;
                                        applyPlayerTurn(playerTurn);
                                        player2TV.setText(getOpponentName);
                                        connectionId = conId;
                                        opponentFound = true;


                                        firebaseDatabase.getReference().child("turns").child(connectionId).addValueEventListener(turnsEventListener);
                                        firebaseDatabase.getReference().child("won").child(connectionId).addValueEventListener(wonEventListener);

                                        if(progressDialog.isShowing()){
                                            progressDialog.dismiss();
                                        }
                                        firebaseDatabase.getReference().child("connections").removeEventListener(this);
                                        break;
                                    }
                                }

                            }
                        }

                        if(!opponentFound && !status.equals("waiting")){
                            String connectionUniqueId = String.valueOf((System.currentTimeMillis()));
                            snapshot.child(connectionUniqueId).child(playerUniqueId).child("player_name").getRef().setValue(myName);
                            status = "waiting";
                        }
                    }

                    else{
                        String connectionUniqueId = String.valueOf((System.currentTimeMillis()));
                        snapshot.child(connectionUniqueId).child(playerUniqueId).child("player_name").getRef().setValue(myName);
                        status = "waiting";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initFields() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        playerUniqueId = firebaseAuth.getUid();
        player1Layout = findViewById(R.id.player1Layout);
        player2Layout = findViewById(R.id.player2Layout);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        image6 = findViewById(R.id.image6);
        image7 = findViewById(R.id.image7);
        image8 = findViewById(R.id.image8);
        image9 = findViewById(R.id.image9);
        myName = getIntent().getStringExtra("Name");
        player1TV = findViewById(R.id.player1TV);
        player2TV = findViewById(R.id.player2TV);

        combinationsList.add(new int[]{0,1,2});
        combinationsList.add(new int[]{3,4,5});
        combinationsList.add(new int[]{6,7,8});
        combinationsList.add(new int[]{0,3,6});
        combinationsList.add(new int[]{1,4,7});
        combinationsList.add(new int[]{2,5,8});
        combinationsList.add(new int[]{2,4,6});
        combinationsList.add(new int[]{0,4,8});
    }

    private void waitForOpponent() {

        AlertDialog.Builder builder = new AlertDialog.Builder(theGame.this);
        final View PopUp = getLayoutInflater().inflate(R.layout.waitingpopout, null);
        builder.setView(PopUp);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        firebaseDatabase.getReference().child("connections").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!opponentFound){

                    if(snapshot.hasChildren()){

                        for(DataSnapshot connections : snapshot.getChildren()){
                            String conId  =connections.getKey();
                            int getPlayersCount = (int)connections.getChildrenCount();
                            if(status.equals("waiting")){
                                boolean playerFound = false;

                                if(getPlayersCount==2){
                                    playerTurn = playerUniqueId;
                                    applyPlayerTurn(playerTurn);

                                    for(DataSnapshot players : connections.getChildren()){
                                        String getPlayerUniqueId = players.getKey();
                                        if(getPlayerUniqueId.equals(playerUniqueId)){
                                            playerFound = true;
                                        }
                                        else if(playerFound){
                                            String getOpponentPlayerName = players.child("player_name").getValue(String.class);
                                            opponentUniqueId = players.getKey();
                                            player2TV.setText(getOpponentPlayerName);
                                            connectionId = conId;
                                            opponentFound = true;

                                            firebaseDatabase.getReference().child("turns").child(connectionId).addValueEventListener(turnsEventListener);
                                            firebaseDatabase.getReference().child("won").child(connectionId).addValueEventListener(wonEventListener);

                                            dialog.dismiss();

                                            firebaseDatabase.getReference().child("connections").removeEventListener(this);

                                        }
                                    }
                                }
                            }
                            else{
                                if(getPlayersCount == 1){
                                    connections.child(playerUniqueId).child("player_name").getRef().setValue(myName);
                                    for(DataSnapshot players : connections.getChildren()){
                                        String getOpponentName = players.child("player_name").getValue(String.class);
                                        opponentUniqueId = players.getKey();
                                        playerTurn = opponentUniqueId;
                                        applyPlayerTurn(playerTurn);
                                        player2TV.setText(getOpponentName);
                                        connectionId = conId;
                                        opponentFound = true;


                                        firebaseDatabase.getReference().child("turns").child(connectionId).addValueEventListener(turnsEventListener);
                                        firebaseDatabase.getReference().child("won").child(connectionId).addValueEventListener(wonEventListener);

                                        dialog.dismiss();

                                        firebaseDatabase.getReference().child("connections").removeEventListener(this);
                                        break;
                                    }
                                }

                            }
                        }

                        if(!opponentFound && !status.equals("waiting")){
                            String connectionUniqueId = String.valueOf((System.currentTimeMillis()));
                            snapshot.child(connectionUniqueId).child(playerUniqueId).child("player_name").getRef().setValue(myName);
                            status = "waiting";
                        }
                    }

                    else{
                        String connectionUniqueId = String.valueOf((System.currentTimeMillis()));
                        snapshot.child(connectionUniqueId).child(playerUniqueId).child("player_name").getRef().setValue(myName);
                        status = "waiting";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setGame() {
        player1TV.setText(myName);
    }

    private void turnsListener() {
        turnsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.getChildrenCount() == 2){
                        final int getBoxPosition = Integer.parseInt(dataSnapshot.child("box_position").getValue(String.class));
                        final String getPlayerId = dataSnapshot.child("player_id").getValue(String.class);
                        if(!doneBoxes.contains(String.valueOf(getBoxPosition))){
                            doneBoxes.add(String.valueOf(getBoxPosition));
                            if(getBoxPosition == 1){
                                selectBox(image1,getBoxPosition,getPlayerId);

                            }
                            else if(getBoxPosition == 2){
                                selectBox(image2,getBoxPosition,getPlayerId);

                            }
                            else if(getBoxPosition ==3){
                                selectBox(image3,getBoxPosition,getPlayerId);

                            }
                            else if(getBoxPosition == 4){
                                selectBox(image4,getBoxPosition,getPlayerId);

                            }
                            else if(getBoxPosition == 5){
                                selectBox(image5,getBoxPosition,getPlayerId);

                            }
                            else if(getBoxPosition == 6){
                                selectBox(image6,getBoxPosition,getPlayerId);

                            }
                            else if(getBoxPosition == 7){
                                selectBox(image7,getBoxPosition,getPlayerId);

                            }
                            else if(getBoxPosition == 8){
                                selectBox(image8,getBoxPosition,getPlayerId);

                            }
                            else if(getBoxPosition == 9){
                                selectBox(image9,getBoxPosition,getPlayerId);

                            }


                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

    }

    private void wonListener() {
        wonEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("player_id")) {
                    String getWinPlayerId = snapshot.child("player_id").getValue(String.class);

                    AlertDialog.Builder builder = new AlertDialog.Builder(theGame.this);
                    final View PopUp = getLayoutInflater().inflate(R.layout.activity_win_dialog, null);
                    builder.setView(PopUp);
                    builder.setCancelable(false);
                    AlertDialog dialog = builder.create();

                    final TextView messageTV = PopUp.findViewById(R.id.messageTV);
                    if (getWinPlayerId.equals(playerUniqueId)) {
                        messageTV.setText("You won the game");}
                    else {messageTV.setText("Opponent won the game");}

                    final Button startBtn = PopUp.findViewById(R.id.startNewMatch);
                    startBtn.setOnClickListener(v -> {
                        dialog.dismiss();
                        Intent intent = new Intent(theGame.this, theGame.class);
                        intent.putExtra("Name", myName);
                        startActivity(intent);
                        theGame.this.finish();
                    });
                    dialog.show();


                    firebaseDatabase.getReference().child("turns").child(connectionId).removeEventListener(turnsEventListener);
                    firebaseDatabase.getReference().child("won").child(connectionId).removeEventListener(wonEventListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void boardListener(){
        image1.setOnClickListener(v -> {
            if(!doneBoxes.contains("1") && playerTurn.equals(playerUniqueId)){
                ((ImageView)v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("box_position").setValue("1");
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("player_id").setValue(playerUniqueId);
                playerTurn = opponentUniqueId;
            }
        });
        image2.setOnClickListener(v -> {
            if(!doneBoxes.contains("2") && playerTurn.equals(playerUniqueId)){
                ((ImageView)v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("box_position").setValue("2");
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("player_id").setValue(playerUniqueId);
                playerTurn = opponentUniqueId;
            }
        });
        image3.setOnClickListener(v -> {
            if(!doneBoxes.contains("3") && playerTurn.equals(playerUniqueId)){
                ((ImageView)v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("box_position").setValue("3");
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("player_id").setValue(playerUniqueId);
                playerTurn = opponentUniqueId;
            }
        });
        image4.setOnClickListener(v -> {
            if(!doneBoxes.contains("4") && playerTurn.equals(playerUniqueId)){
                ((ImageView)v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("box_position").setValue("4");
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("player_id").setValue(playerUniqueId);
                playerTurn = opponentUniqueId;
            }
        });
        image5.setOnClickListener(v -> {
            if(!doneBoxes.contains("5") && playerTurn.equals(playerUniqueId)){
                ((ImageView)v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("box_position").setValue("5");
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("player_id").setValue(playerUniqueId);
                playerTurn = opponentUniqueId;
            }
        });
        image6.setOnClickListener(v -> {
            if(!doneBoxes.contains("6") && playerTurn.equals(playerUniqueId)){
                ((ImageView)v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("box_position").setValue("6");
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("player_id").setValue(playerUniqueId);
                playerTurn = opponentUniqueId;
            }
        });
        image7.setOnClickListener(v -> {
            if(!doneBoxes.contains("7") && playerTurn.equals(playerUniqueId)){
                ((ImageView)v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("box_position").setValue("7");
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("player_id").setValue(playerUniqueId);
                playerTurn = opponentUniqueId;
            }
        });
        image8.setOnClickListener(v -> {
            if(!doneBoxes.contains("8") && playerTurn.equals(playerUniqueId)){
                ((ImageView)v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("box_position").setValue("8");
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("player_id").setValue(playerUniqueId);
                playerTurn = opponentUniqueId;
            }
        });
        image9.setOnClickListener(v -> {
            if(!doneBoxes.contains("9") && playerTurn.equals(playerUniqueId)){
                ((ImageView)v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("box_position").setValue("9");
                firebaseDatabase.getReference().child("turns").child(connectionId).child(String.valueOf(doneBoxes.size()+1)).child("player_id").setValue(playerUniqueId);
                playerTurn = opponentUniqueId;
            }
        });
    }

    private void applyPlayerTurn(String playerUniqueId2) {
        if(playerUniqueId2.equals(playerUniqueId)) {
            player1Layout.setBackgroundResource(R.drawable.round_back_dark_blue_stroke);
            player2Layout.setBackgroundResource(R.drawable.round_back_dark_blue_20);
        }
        else{
            player1Layout.setBackgroundResource(R.drawable.round_back_dark_blue_20);
            player2Layout.setBackgroundResource(R.drawable.round_back_dark_blue_stroke);
        }
    }
    private void selectBox(ImageView imageView, int selectedBoxPosition,String selectedByPlayer){
        boxesSelectedBy[selectedBoxPosition-1] = selectedByPlayer;
        if(selectedByPlayer.equals(playerUniqueId)){
            imageView.setImageResource(R.drawable.x);
            playerTurn = opponentUniqueId;
        }
        else{
            imageView.setImageResource(R.drawable.o);
            playerTurn = playerUniqueId;
        }
        applyPlayerTurn(playerTurn);
        if(checkPlayerWin(selectedByPlayer)){
            firebaseDatabase.getReference().child("won").child(connectionId).child("player_id").setValue(selectedByPlayer);

        }
        if(doneBoxes.size()==9){
            AlertDialog.Builder builder = new AlertDialog.Builder(theGame.this);
            final View PopUp = getLayoutInflater().inflate(R.layout.activity_win_dialog, null);
            builder.setView(PopUp);
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();

            final TextView messageTV = findViewById(R.id.messageTV);
                messageTV.setText("It's a draw!");

                    final Button startBtn = findViewById(R.id.startNewMatch);
                    startBtn.setOnClickListener(v -> {
                        dialog.dismiss();
                        Intent intent = new Intent(theGame.this, theGame.class);
                        intent.putExtra("Name", myName);
                        startActivity(intent);
                        theGame.this.finish();
                    });
            dialog.show();
        }
    }
    private boolean checkPlayerWin(String playerId){
        boolean isPlayerWon = false;
        for ( int i=0; i< combinationsList.size(); i++){
            final int[] combination = combinationsList.get(i);
            if(boxesSelectedBy[combination[0]].equals(playerId) &&
                    boxesSelectedBy[combination[1]].equals(playerId) &&
                    boxesSelectedBy[combination[2]].equals(playerId)){
                isPlayerWon = true;
            }
        }
        return isPlayerWon;
    }
}