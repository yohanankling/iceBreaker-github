package com.example.icebreaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class WelcomingActivity extends AppCompatActivity {

    // Constant for the delay time in milliseconds
    private static final int DELAY_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcoming);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set up a handler to start the next activity after the specified delay time
        new Handler().postDelayed(this::startNextActivity, DELAY_TIME);
    }

    // Method to start the next activity
    private void startNextActivity() {
        Intent intent = new Intent(WelcomingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
