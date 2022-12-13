package com.example.icebreaker.users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.icebreaker.Home;
import com.example.icebreaker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private Button LoginBtn, RegisterBtn;
    private EditText Email, Password;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initFields();
        LoginButton();
        RegisterButton();
    }

    private void initFields() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        LoginBtn = findViewById(R.id.LoginBtn);
        RegisterBtn = findViewById(R.id.RegisterBtn);
    }

    private void LoginButton() {
        LoginBtn.setOnClickListener(view -> {
            boolean validate = true;
            if (Email.getText().toString().isEmpty()) {
                Email.setHintTextColor(Color.RED);
                Email.setHint("enter an Email!");
                validate = false;
            }else {
                Email.setHintTextColor(Color.BLACK);
                Email.setHint(" example@gmail.com");
            }
            if (Password.getText().toString().isEmpty()){
                Password.setHintTextColor(Color.RED);
                Password.setHint("enter a password!");
                validate = false;
            }else {
                Password.setHintTextColor(Color.BLACK);
                Password.setHint(" Password");
            }
            if (validate) {
                ValidEmailOnDatabase();
            }
        });
    }

    private void ValidEmailOnDatabase() {
        firebaseAuth.signInWithEmailAndPassword(Email.getText().toString(),Password.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(Login.this, "login successfully!", Toast.LENGTH_SHORT).show();
                        setAndInitStatus();
                        Intent intent = new Intent(Login.this, Home.class);
                        startActivity(intent);
                        finish();
                    } else
                        Toast.makeText(Login.this, "oh! please try again..", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> Toast.makeText(Login.this, "Error: "+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show()).addOnCanceledListener(() -> Toast.makeText(Login.this, "login canceled..", Toast.LENGTH_SHORT).show());
    }

    private void setAndInitStatus() {
        String UserId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(UserId);
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", Email.getText().toString());
        userData.put("uid", UserId);
        userData.put("status", "online");
        userData.put("topic", "~null");
        documentReference.set(userData).addOnSuccessListener(unused -> {
        });
    }

    private void RegisterButton() {
        RegisterBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
            finish();
        });
    }
}