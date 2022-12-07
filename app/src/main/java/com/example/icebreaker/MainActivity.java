package com.example.icebreaker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icebreaker.users.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button Login, register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        LoginButton();
        RegisterButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            startActivity (new Intent(this, Home.class));

        } else{
            startActivity (new Intent(this, Login.class));
            finish();}
    }

    private void initViews() {
        Login = findViewById(R.id.Login);
        register = findViewById(R.id.register);
    }

    private void LoginButton() {
        Login.setOnClickListener(view -> {
            Intent  intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        });
    }

    private void RegisterButton() {
        register.setOnClickListener(view -> {
            Intent  intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });
    }
}