package com.example.icebreaker.gameZone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.icebreaker.R;

public class PlayerName extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_name);

        final EditText playerNameEt = findViewById(R.id.playerNameEt);
        final AppCompatButton startGameBtn = findViewById(R.id.startGameBtn);

        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
    }
}