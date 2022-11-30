package com.example.icebreaker.contactUs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.icebreaker.Home;
import com.example.icebreaker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;


public class Contact extends AppCompatActivity {
    private ImageButton backBtn, yohWhatsapp, yohLinkedin, yohGithub, yohCv,
            tzachWhatsapp, tzachLinkedin, tzachGithub, tzachCv,
            nadavWhatsapp, nadavLinkedin, nadavGithub, nadavCv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initFields();
        SetBtns();
    }

    private void initFields() {
        backBtn = findViewById(R.id.back);
        yohWhatsapp = findViewById(R.id.yohWhatsapp);
        yohLinkedin = findViewById(R.id.yohLinkedin);
        yohGithub = findViewById(R.id.yohGithub);
        yohCv = findViewById(R.id.yohCv);
        tzachWhatsapp = findViewById(R.id.tzachWhatsapp);
        tzachLinkedin = findViewById(R.id.tzachLinkedin);
        tzachGithub = findViewById(R.id.tzachGithub);
        tzachCv = findViewById(R.id.tzachCv);
        nadavWhatsapp = findViewById(R.id.nadavWhatsapp);
        nadavLinkedin = findViewById(R.id.nadavLinkedin);
        nadavGithub = findViewById(R.id.nadavGithub);
        nadavCv = findViewById(R.id.nadavCv);
    }

    private void SetBtns() {
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Contact.this, Home.class);
            startActivity(intent);
        });
        yohWhatsapp.setOnClickListener(view -> {
            String Url = "https://wa.me/972586669988?text=from iceBreaker";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(Url));
            startActivity(intent);
        });
        tzachWhatsapp.setOnClickListener(view -> {
            String Url = "https://wa.me/972546797394?text=from iceBreaker";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(Url));
            startActivity(intent);
        });
        nadavWhatsapp.setOnClickListener(view -> {
            String Url = "https://wa.me/972548841313?text=from iceBreaker";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(Url));
            startActivity(intent);
        });
        yohGithub.setOnClickListener(view -> {
            String Url = "https://github.com/yohanankling";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(Url));
            startActivity(intent);
        });
        tzachGithub.setOnClickListener(view -> {
            String Url = "https://github.com/tzachaker";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(Url));
            startActivity(intent);
        });
        nadavGithub.setOnClickListener(view -> {
            String Url = "https://github.com/yohanankling";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(Url));
            startActivity(intent);
        });
        yohLinkedin.setOnClickListener(view -> {
            String Url = "https://linkedin.com/in/yohanan-kling";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(Url));
            startActivity(intent);
        });

        tzachLinkedin.setOnClickListener(view -> {
            String Url = "https://www.linkedin.com/in/tzachaker";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(Url));
            startActivity(intent);
        });
        nadavLinkedin.setOnClickListener(view -> {
            String Url = "https://github.com/yohanankling";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(Url));
            startActivity(intent);
        });
        yohCv.setOnClickListener(view -> {
            String Url = "https://drive.google.com/file/d/1el9dCExmLQ_axgGPGSJBs0PbledH1HWi/view";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(Url));
            startActivity(intent);
        });
        tzachCv.setOnClickListener(view -> {
//            Intent intent = new Intent(Contact.this, OurPdfs.class);
//            String tzachPdf = "tzachcv.pdf";
//            intent.putExtra("pdf", tzachPdf);
//            startActivity(intent);
        });
        nadavCv.setOnClickListener(view -> {
//            Intent intent = new Intent(Contact.this, OurPdfs.class);
//            String nadavPdf = "nadavcv.pdf";
//            intent.putExtra("pdf", nadavPdf);
//            startActivity(intent);
        });
    }

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