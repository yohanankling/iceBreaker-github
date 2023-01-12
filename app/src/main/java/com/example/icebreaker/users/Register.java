package com.example.icebreaker.users;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icebreaker.MainActivity;
import com.example.icebreaker.R;
import com.example.icebreaker.users.model.RegisterModel;

public class Register extends AppCompatActivity {

    RegisterModel registerModel = new RegisterModel(this);

    private TextView GenderNotice;
    private Button Submit, Cancel;
    private EditText Name, Password, RePassword, Email;
    private ImageButton Male, Female;
    boolean MaleClick = false, FemaleClick = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initFields();
        GenderButton();
        submitButton();
        cancelButton();
    }

    private void initFields() {
        GenderNotice = findViewById(R.id.GenderNotice);
        Male = findViewById(R.id.Male);
        Female = findViewById(R.id.Female);
        Submit = findViewById(R.id.Submit);
        Cancel = findViewById(R.id.Cancel);
        Name = findViewById(R.id.Name);
        Password = findViewById(R.id.Password);
        RePassword = findViewById(R.id.RePassword);
        Email = findViewById(R.id.Email);
    }

    private void GenderButton() {
        Male.setOnClickListener(view -> {
            MaleClick = true;
            FemaleClick = false;
            Toast.makeText(Register.this, "hey bro", Toast.LENGTH_SHORT).show();
        });
        Female.setOnClickListener(view -> {
            FemaleClick = true;
            MaleClick = false;
            Toast.makeText(Register.this, "hey sis", Toast.LENGTH_SHORT).show();
        });
    }

    private void submitButton() {
        Submit.setOnClickListener(view -> {
            if (!validateRegistration()){
                Toast.makeText(Register.this, "registered unsuccessfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelButton() {
        Cancel.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("are you sure?");
            builder.setMessage("");
            builder.setPositiveButton("yes",
                    (dialog, which) -> {
                        Toast.makeText(Register.this, "registration canceled", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Register.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
            builder.setNegativeButton("no",
                    (dialog, which) ->
                            Toast.makeText(Register.this, "the canceled canceled", Toast.LENGTH_SHORT).show());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private boolean validateRegistration() {
        boolean validate = true;
        if (!MaleClick && !FemaleClick){
            GenderNotice.setVisibility(View.VISIBLE);
            validate = false;
        }else GenderNotice.setVisibility(View.GONE);

        if (Name.getText().toString().isEmpty()){
            Name.setHintTextColor(Color.RED);
            Name.setHint("enter a name!");
            validate = false;
        }else {
            Name.setHintTextColor(Color.BLACK);
            Name.setHint(" Full Name");
        }

        if (Password.getText().toString().isEmpty()){
            Password.setHintTextColor(Color.RED);
            Password.setHint("enter a password!");
            validate = false;
        }else {
            Password.setHintTextColor(Color.BLACK);
            Password.setHint(" Password");
        }

        if (RePassword.getText().toString().isEmpty()){
            RePassword.setHintTextColor(Color.RED);
            RePassword.setHint("re-enter a password!");
            validate = false;
        }else {
            RePassword.setHintTextColor(Color.BLACK);
            RePassword.setHint(" Re Enter Password");
        }
        if (!Password.getText().toString().equals(RePassword.getText().toString())
                && !RePassword.getText().toString().equals("")){
            Toast.makeText(Register.this, "entered two different passwords!", Toast.LENGTH_SHORT).show();
            validate = false;
        }

        if (Email.getText().toString().isEmpty()) {
            Email.setHintTextColor(Color.RED);
            Email.setHint("enter an Email!");
            validate = false;
        }else {
            Email.setHintTextColor(Color.BLACK);
            Email.setHint(" example@gmail.com");
        }
        if (validate){
            ValidEmailOnDatabase();
        }
        return validate;
    }

    private void ValidEmailOnDatabase() {
        registerModel.ValidEmailOnDatabase(Email.getText().toString(),Password.getText().toString(), Name.getText().toString(), MaleClick);
    }
}