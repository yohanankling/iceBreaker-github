package com.example.icebreaker.gameZone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icebreaker.Home;
import com.example.icebreaker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

public class PlayZone extends AppCompatActivity {
    private TextView txt;
    private ImageButton XTopRight, XTopCenter, XTopLeft,
            XMiddleRight, XMiddleCenter, XMiddleLeft,
            XDownRight, XDownCenter, XDownLeft,
            OTopRight, OTopCenter, OTopLeft,
            OMiddleRight, OMiddleCenter, OMiddleLeft,
            ODownRight, ODownCenter, ODownLeft;
    private Button QuitBtn;
    private String Email;
    DatabaseReference LoginFirebase = FirebaseDatabase.getInstance().getReferenceFromUrl("" +
            "https://icebreaker-7db2a-default-rtdb.firebaseio.com/");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_zone);
        initFields();
        XButton();
        OButton();
        QuitButton();
    }
    private void initFields() {
        Email = getIntent().getStringExtra("Email");
        txt = findViewById(R.id.textView);
        XTopRight = findViewById(R.id.XTopRight);
        XTopCenter = findViewById(R.id.XTopCenter);
        XTopLeft = findViewById(R.id.XTopLeft);
        XMiddleRight = findViewById(R.id.XMiddleRight);
        XMiddleCenter = findViewById(R.id.XMiddleCenter);
        XMiddleLeft = findViewById(R.id.XMiddleLeft);
        XDownRight = findViewById(R.id.XDownRight);
        XDownCenter = findViewById(R.id.XDownCenter);
        XDownLeft = findViewById(R.id.XDownLeft);
        OTopRight = findViewById(R.id.OTopRight);
        OTopCenter = findViewById(R.id.OTopCenter);
        OTopLeft = findViewById(R.id.OTopLeft);
        OMiddleRight = findViewById(R.id.OMiddleRight);
        OMiddleCenter = findViewById(R.id.OMiddleCenter);
        OMiddleLeft = findViewById(R.id.OMiddleLeft);
        ODownRight = findViewById(R.id.ODownRight);
        ODownCenter = findViewById(R.id.ODownCenter);
        ODownLeft = findViewById(R.id.ODownLeft);
        QuitBtn = findViewById(R.id.QuitBtn);
    }

    private void XButton() {
        XTopRight.setOnClickListener(view -> {
            XTopRight.setVisibility(View.VISIBLE);
            txt.setText("aaaaaaa");
        });
        XTopCenter.setOnClickListener(view -> XTopCenter.setVisibility(View.VISIBLE));
        XTopLeft.setOnClickListener(view -> XTopLeft.setVisibility(View.VISIBLE));
        XMiddleRight.setOnClickListener(view -> XMiddleRight.setVisibility(View.VISIBLE));
        XMiddleCenter.setOnClickListener(view -> XMiddleCenter.setVisibility(View.VISIBLE));
        XMiddleLeft.setOnClickListener(view -> XMiddleLeft.setVisibility(View.VISIBLE));
        XDownRight.setOnClickListener(view -> XDownRight.setVisibility(View.VISIBLE));
        XDownCenter.setOnClickListener(view -> XDownCenter.setVisibility(View.VISIBLE));
        XDownLeft.setOnClickListener(view -> XDownLeft.setVisibility(View.VISIBLE));
    }

    private void OButton() {
        OTopRight.setOnClickListener(view -> OTopRight.setVisibility(View.VISIBLE));
        OTopCenter.setOnClickListener(view -> OTopCenter.setVisibility(View.VISIBLE));
        OTopLeft.setOnClickListener(view -> OTopLeft.setVisibility(View.VISIBLE));
        OMiddleRight.setOnClickListener(view -> OMiddleRight.setVisibility(View.VISIBLE));
        OMiddleCenter.setOnClickListener(view -> OMiddleCenter.setVisibility(View.VISIBLE));
        OMiddleLeft.setOnClickListener(view -> OMiddleLeft.setVisibility(View.VISIBLE));
        ODownRight.setOnClickListener(view -> ODownRight.setVisibility(View.VISIBLE));
        ODownCenter.setOnClickListener(view -> ODownCenter.setVisibility(View.VISIBLE));
        ODownLeft.setOnClickListener(view -> ODownLeft.setVisibility(View.VISIBLE));
    }

    private void QuitButton() {
        QuitBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("are you sure?");
            builder.setMessage("shaming and stuff");
            builder.setPositiveButton("yes",
                    (dialog, which) -> {
                        Toast.makeText(PlayZone.this, "bye-bye!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PlayZone.this, Home.class);
                        startActivity(intent);
                    });
            builder.setNegativeButton("no, keep going",
                    (dialog, which) ->
                            Toast.makeText(PlayZone.this, "good to have you back!", Toast.LENGTH_SHORT).show());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }


//    private void clearFields() {
//        // TODO: clear fields choose
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        setStatus("offline");
    }

    private void setStatus(String state) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String UserId = firebaseAuth.getCurrentUser().getUid();
        String SaveCurrentDate, SaveCurrentTime;
        Calendar date = Calendar.getInstance();
        SimpleDateFormat CurrentDate = new SimpleDateFormat("MMM dd yyyy");
        SaveCurrentDate = CurrentDate.format(date.getTime());

        Calendar time = Calendar.getInstance();
        SimpleDateFormat CurrentTime = new SimpleDateFormat("hh:mm a");
        SaveCurrentTime = CurrentTime.format(time.getTime());
        String LastSeen = SaveCurrentDate + "" + SaveCurrentTime;

        if (state.equals("offline")) {
            firebaseAuth.signOut();
            databaseReference.child("online").child(UserId).removeValue();
            databaseReference.child("offline").child(UserId).setValue(LastSeen);
        }
    }


}

