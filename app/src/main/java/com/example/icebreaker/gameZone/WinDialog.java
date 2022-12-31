package com.example.icebreaker.gameZone;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.icebreaker.R;

public class WinDialog extends Dialog {
    private  String message;
    private final theGame theGame;
    public WinDialog(@NonNull Context context,String message) {
        super(context);
        this.message = message;
        this.theGame = ((theGame) context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_dialog);

        final TextView messageTV = findViewById(R.id.messageTV);
        final Button startBtn = findViewById(R.id.startNewMatch);
        messageTV.setText(message);

        startBtn.setOnClickListener(v -> {
            dismiss();
            getContext().startActivity(new Intent(getContext(),theGame.class));
            theGame.finish();
        });

    }
}
