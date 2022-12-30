package com.example.icebreaker.gameZone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.icebreaker.R;

public class PlayerName extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_name);

        final EditText playerNameEt = findViewById(R.id.playerNameEt);
        final Button startGameBtn = findViewById(R.id.startGameBtn);

        startGameBtn.setOnClickListener(v -> {
            final  String getPlayerName = playerNameEt.getText().toString();
            if (getPlayerName.isEmpty()){
                Toast.makeText(PlayerName.this,"Please enter player name",Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(PlayerName.this, theGame.class);
                intent.putExtra("playerName",getPlayerName);
                startActivity(intent);
                finish();
            }
        });
    }
}