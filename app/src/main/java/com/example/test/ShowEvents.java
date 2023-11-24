package com.example.test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Person;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ShowEvents extends AppCompatActivity {



    Date currentTime = java.util.Calendar.getInstance().getTime();
    public String format_yyyyMMdd = "yyyyMMdd";
    public String format_yyyyMMdd_forTitle = "yyyy-MM-dd";
    ArrayList<String> AdminarrayList = new ArrayList<>();
    ArrayList<String> arrayList = new ArrayList<>();
    ProgressBar progressBar1,progressBar2;
    private ListView eventListView,adminListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events);

        String getname = getIntent().getStringExtra("getName");
        Toast.makeText(ShowEvents.this,"getname: "+getname,Toast.LENGTH_SHORT);


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShowEvents.this, android.R.layout.simple_list_item_1
                ,arrayList);
        final ArrayAdapter<String> Adminadapter = new ArrayAdapter<String>(ShowEvents.this, android.R.layout.simple_list_item_1
                ,AdminarrayList);

        eventListView = findViewById(R.id.PersonalListView);
        eventListView.setAdapter(adapter);

        adminListView = findViewById(R.id.AdminListView);
        adminListView.setAdapter(Adminadapter);

        SimpleDateFormat formatT = new SimpleDateFormat(format_yyyyMMdd_forTitle, Locale.getDefault());
        String currentT = formatT.format(currentTime);

        SimpleDateFormat formatH = new SimpleDateFormat(format_yyyyMMdd,Locale.getDefault());
        String currentH = formatH.format(currentTime);

        //currentDate.setText("Today's Sh");

        progressBar1 = findViewById(R.id.AdminprogressBar);
        progressBar1.setVisibility(View.VISIBLE);

        progressBar2 = findViewById(R.id.PersonalProgressBar);
        progressBar2.setVisibility(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference AdminList = database.getReference("Calendar").child("Admin")
                .child(currentH);
        AdminList.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar1.setVisibility(View.GONE);
                String v = snapshot.getValue(String.class);
                AdminarrayList.add(v); Adminadapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Adminadapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference myRef1 = database.getReference("Calendar").child("Employee").child(getname)
                .child(currentH).child("PersonalEvents");

        myRef1.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar2.setVisibility(View.GONE);
                String v = snapshot.getValue(String.class);
                arrayList.add(v); adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    protected void onResume(){
        super.onResume();


        Handler hander = new Handler();
        hander.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ShowEvents.this, MainActivity.class);
                startActivity(intent);
            }
        },7000);

    }
}