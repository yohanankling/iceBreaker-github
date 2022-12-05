package com.example.icebreaker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icebreaker.users.Register;

public class MainActivity extends AppCompatActivity {

    private Button Login, register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent  intent = new Intent(MainActivity.this, PlayZone.class);
//        startActivity(intent);
        initViews();
        LoginButton();
        RegisterButton();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//        if(firebaseUser != null){
//
//        } else{
//            startActivity (new Intent(this, Login.class));
//            finish();}
//    }

    private void initViews() {
        Login = findViewById(R.id.Login);
        register = findViewById(R.id.register);
    }

    private void LoginButton() {
        Login.setOnClickListener(view -> {
            Intent  intent = new Intent(MainActivity.this, com.example.icebreaker.users.Login.class);
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