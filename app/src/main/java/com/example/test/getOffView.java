package com.example.test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.log4j.chainsaw.Main;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class getOffView extends AppCompatActivity {
    TextView textView,textView1;
    private ListView listView;
    private DatabaseReference databaseReference;
    public String format_Current_Time = "hh:mm:ss";
    public String format_yyyyMMdd = "yyyyMMdd";
    Date currentTime = java.util.Calendar.getInstance().getTime();
    int i=0;
    ArrayList<String> arrayList = new ArrayList<>();
    ProgressBar progressBar;
    String in,out,clo,late;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //String getname = "ChoiSeEun";
        // getname = "LeeNaYoung";
        String getname = getIntent().getStringExtra("getName");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_off_view);

        String img="";

        if(getname.equals("ChoiSeEun"))
            img="👩‍🔬 ";
        else if(getname.equals("LeeNaYoung"))
            img="👷 ";

        textView = findViewById(R.id.textName);
        textView.setText(img+getname+"님");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getOffView.this, android.R.layout.simple_list_item_1
                ,arrayList);

        listView = findViewById(R.id.ListView);
        listView.setAdapter(adapter);

        SimpleDateFormat formatT = new SimpleDateFormat(format_Current_Time, Locale.getDefault());
        String currentT = formatT.format(currentTime);

        SimpleDateFormat formatH = new SimpleDateFormat(format_yyyyMMdd,Locale.getDefault());
        String currentH = formatH.format(currentTime);

        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");
        databaseReference.child("Employee").child(getname).child(currentH)
                .child("PersonalInfo").child("퇴근 시간").setValue("퇴근 시간: "+currentT);

        progressBar = findViewById(R.id.progressBar5);
        progressBar.setVisibility(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef1 = database.getReference("Calendar").child("Employee").child(getname)
                .child(currentH).child("PersonalInfo");

        myRef1.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
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
                Intent intent = new Intent(getOffView.this, MainActivity.class);
                startActivity(intent);
            }
        },10000);

    }


}