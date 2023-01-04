package com.example.icebreaker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icebreaker.users.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // Button views
    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the button views
        initViews();

        // Set up the login button
        setupLoginButton();

        // Set up the register button
        setupRegisterButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
//             If the user is already logged in, start the home activity
            startActivity(new Intent(this, Home.class));
        }
    }

    // Method to initialize the button views
    private void initViews() {
        loginButton = findViewById(R.id.Login);
        registerButton = findViewById(R.id.register);
    }

    // Method to set up the login button
    private void setupLoginButton() {
        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        });
    }

    // Method to set up the register button
    private void setupRegisterButton() {
        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });
    }
}
