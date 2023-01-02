package com.example.icebreaker.gameZone;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class inviter extends AppCompatActivity {

    private ConstraintLayout player1Layout, player2Layout;
    private ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9;
    private TextView player1TV, player2TV, score;

    private final List<int[]> combinationsList = new ArrayList<>();
    private final List<String> doneBoxes = new ArrayList<>();

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private boolean opponentFound = false;

    private String myName;
    private String myUid;
    private String opponentName;
    private String opponentUid;

    private int winning = 0;
    private int loses = 0;

    private String playerTurn = "";

    ValueEventListener turnsEventListener, wonEventListener;

    private final String[] boxesSelectedBy = {"", "", "", "", "", "", "", "", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_game);
        initFields();
        setGame();
    }

    private void initFields() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myUid = firebaseAuth.getUid();
        player1Layout = findViewById(R.id.player1Layout);
        player2Layout = findViewById(R.id.player2Layout);
        score = findViewById(R.id.score);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        image6 = findViewById(R.id.image6);
        image7 = findViewById(R.id.image7);
        image8 = findViewById(R.id.image8);
        image9 = findViewById(R.id.image9);
        myName = getIntent().getStringExtra("inviterName");
        opponentName = getIntent().getStringExtra("opponentName");
        opponentUid = getIntent().getStringExtra("opponentUid");
        player1TV = findViewById(R.id.player1TV);
        player2TV = findViewById(R.id.player2TV);

        combinationsList.add(new int[]{0, 1, 2});
        combinationsList.add(new int[]{3, 4, 5});
        combinationsList.add(new int[]{6, 7, 8});
        combinationsList.add(new int[]{0, 3, 6});
        combinationsList.add(new int[]{1, 4, 7});
        combinationsList.add(new int[]{2, 5, 8});
        combinationsList.add(new int[]{2, 4, 6});
        combinationsList.add(new int[]{0, 4, 8});
    }

    private void setGame() {
        player1TV.setText(myName);
        player2TV.setText(opponentName);
        waitForOpponent();
        turnsListener();
        wonListener();
        boardListener();
    }

    private void waitForOpponent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(inviter.this);
        final View PopUp = getLayoutInflater().inflate(R.layout.waitingpopout, null);
        builder.setView(PopUp);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        firebaseDatabase.getReference().child("connections").child(firebaseAuth.getUid()).child("inviterName").setValue(myName);
        firebaseDatabase.getReference().child("connections").child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!opponentFound) {
                    if (snapshot.hasChildren()) {
                        playerTurn = myUid;
                        applyPlayerTurn(playerTurn);
                        if ((int) snapshot.getChildrenCount() == 2) {
                            opponentFound = true;
                            firebaseDatabase.getReference().child("turns").child(myUid).addValueEventListener(turnsEventListener);
                            firebaseDatabase.getReference().child("won").child(myUid).addValueEventListener(wonEventListener);

                            dialog.dismiss();

                            firebaseDatabase.getReference().child("connections").child(firebaseAuth.getUid()).removeEventListener(this);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void turnsListener() {
        turnsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getChildrenCount() == 2) {
                        final int getBoxPosition = Integer.parseInt(dataSnapshot.child("box_position").getValue(String.class));
                        final String getPlayerId = dataSnapshot.child("player_id").getValue(String.class);
                        if (!doneBoxes.contains(String.valueOf(getBoxPosition))) {
                            doneBoxes.add(String.valueOf(getBoxPosition));
                            if (getBoxPosition == 1) {
                                selectBox(image1, getBoxPosition, getPlayerId);

                            } else if (getBoxPosition == 2) {
                                selectBox(image2, getBoxPosition, getPlayerId);

                            } else if (getBoxPosition == 3) {
                                selectBox(image3, getBoxPosition, getPlayerId);

                            } else if (getBoxPosition == 4) {
                                selectBox(image4, getBoxPosition, getPlayerId);

                            } else if (getBoxPosition == 5) {
                                selectBox(image5, getBoxPosition, getPlayerId);

                            } else if (getBoxPosition == 6) {
                                selectBox(image6, getBoxPosition, getPlayerId);

                            } else if (getBoxPosition == 7) {
                                selectBox(image7, getBoxPosition, getPlayerId);

                            } else if (getBoxPosition == 8) {
                                selectBox(image8, getBoxPosition, getPlayerId);

                            } else if (getBoxPosition == 9) {
                                selectBox(image9, getBoxPosition, getPlayerId);

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

                    AlertDialog.Builder builder = new AlertDialog.Builder(inviter.this);
                    final View PopUp = getLayoutInflater().inflate(R.layout.activity_win_dialog, null);
                    builder.setView(PopUp);
                    builder.setCancelable(false);
                    AlertDialog dialog = builder.create();

                    final TextView messageTV = PopUp.findViewById(R.id.messageTV);
                    if (getWinPlayerId.equals(myUid)) {
                        messageTV.setText("You won the game!");
                        winning++;
                        String newScore = winning + ":" + loses;
                        score.setText(newScore);
                    } else {
                        messageTV.setText(opponentName + " won the game!");
                        loses++;
                        String newScore = winning + ":" + loses;
                        score.setText(newScore);
                    }

                    final Button startBtn = PopUp.findViewById(R.id.startNewMatch);
                    startBtn.setOnClickListener(v -> {
                        dialog.dismiss();
                        resetGameData();
                    });
                    dialog.show();


                    firebaseDatabase.getReference().child("turns").child(myUid).removeEventListener(turnsEventListener);
                    firebaseDatabase.getReference().child("won").child(myUid).removeEventListener(wonEventListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void boardListener() {
        image1.setOnClickListener(v -> {
            if (!doneBoxes.contains("1") && playerTurn.equals(myUid)) {
                ((ImageView) v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("1");
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(myUid);
                playerTurn = opponentUid;
            }
        });
        image2.setOnClickListener(v -> {
            if (!doneBoxes.contains("2") && playerTurn.equals(myUid)) {
                ((ImageView) v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("2");
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(myUid);
                playerTurn = opponentUid;
            }
        });
        image3.setOnClickListener(v -> {
            if (!doneBoxes.contains("3") && playerTurn.equals(myUid)) {
                ((ImageView) v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("3");
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(myUid);
                playerTurn = opponentUid;
            }
        });
        image4.setOnClickListener(v -> {
            if (!doneBoxes.contains("4") && playerTurn.equals(myUid)) {
                ((ImageView) v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("4");
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(myUid);
                playerTurn = opponentUid;
            }
        });
        image5.setOnClickListener(v -> {
            if (!doneBoxes.contains("5") && playerTurn.equals(myUid)) {
                ((ImageView) v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("5");
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(myUid);
                playerTurn = opponentUid;
            }
        });
        image6.setOnClickListener(v -> {
            if (!doneBoxes.contains("6") && playerTurn.equals(myUid)) {
                ((ImageView) v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("6");
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(myUid);
                playerTurn = opponentUid;
            }
        });
        image7.setOnClickListener(v -> {
            if (!doneBoxes.contains("7") && playerTurn.equals(myUid)) {
                ((ImageView) v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("7");
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(myUid);
                playerTurn = opponentUid;
            }
        });
        image8.setOnClickListener(v -> {
            if (!doneBoxes.contains("8") && playerTurn.equals(myUid)) {
                ((ImageView) v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("8");
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(myUid);
                playerTurn = opponentUid;
            }
        });
        image9.setOnClickListener(v -> {
            if (!doneBoxes.contains("9") && playerTurn.equals(myUid)) {
                ((ImageView) v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("9");
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(myUid);
                playerTurn = opponentUid;
            }
        });
    }


    private void applyPlayerTurn(String playerUniqueId2) {
        if (playerUniqueId2.equals(myUid)) {
            player1Layout.setBackgroundResource(R.drawable.round_back_dark_blue_stroke);
            player2Layout.setBackgroundResource(R.drawable.round_back_dark_blue_20);
        } else {
            player1Layout.setBackgroundResource(R.drawable.round_back_dark_blue_20);
            player2Layout.setBackgroundResource(R.drawable.round_back_dark_blue_stroke);
        }
    }

    private void selectBox(ImageView imageView, int selectedBoxPosition, String selectedByPlayer) {
        boxesSelectedBy[selectedBoxPosition - 1] = selectedByPlayer;
        if (selectedByPlayer.equals(myUid)) {
            imageView.setImageResource(R.drawable.x);
            playerTurn = opponentUid;
        } else {
            imageView.setImageResource(R.drawable.o);
            playerTurn = myUid;
        }
        applyPlayerTurn(playerTurn);
        if (checkPlayerWin(selectedByPlayer)) {
            firebaseDatabase.getReference().child("won").child(myUid).child("player_id").setValue(selectedByPlayer);

        }
        if (doneBoxes.size() == 9) {
            AlertDialog.Builder builder = new AlertDialog.Builder(inviter.this);
            final View PopUp = getLayoutInflater().inflate(R.layout.activity_win_dialog, null);
            builder.setView(PopUp);
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();

            final TextView messageTV = findViewById(R.id.messageTV);
            messageTV.setText("It's a draw!");

            final Button startBtn = findViewById(R.id.startNewMatch);
            startBtn.setOnClickListener(v -> {
                dialog.dismiss();
                resetGameData();
            });
            dialog.show();
        }
    }

    private boolean checkPlayerWin(String playerId) {
        boolean isPlayerWon = false;
        for (int i = 0; i < combinationsList.size(); i++) {
            final int[] combination = combinationsList.get(i);
            if (boxesSelectedBy[combination[0]].equals(playerId) &&
                    boxesSelectedBy[combination[1]].equals(playerId) &&
                    boxesSelectedBy[combination[2]].equals(playerId)) {
                isPlayerWon = true;
            }
        }
        return isPlayerWon;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseDatabase.getReference().child("connections").child(firebaseAuth.getUid()).removeValue();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.isFinishing()) {
            firebaseDatabase.getReference().child("connections").child(firebaseAuth.getUid()).removeValue();
        }
    }

    private void resetGameData() {
        for (int i = 0; i < 9; i++) {
            boxesSelectedBy[i] = "";
        }
        firebaseDatabase.getReference().child("turns").child(myUid).removeValue();
        firebaseDatabase.getReference().child("won").child(myUid).removeValue();
        doneBoxes.clear();
        setGame();
    }

}