package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ShowInformation extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    private DatabaseReference databaseReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    public String format_Current_Time = "hh:mm:ss";
    public String format_yyyyMMdd = "yyyyMMdd";
    Date currentTime = Calendar.getInstance().getTime();
    ProgressBar progressBar,progressBar1, progressBar2;
    String getLabelImg="";
    ImageView load;
    int i=0;
    String getjob="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_information);

        SimpleDateFormat formatT = new SimpleDateFormat(format_Current_Time, Locale.getDefault());
        String currentT = formatT.format(currentTime);

        SimpleDateFormat formatH = new SimpleDateFormat(format_yyyyMMdd,Locale.getDefault());
        String currentH = formatH.format(currentTime);

        String getname = getIntent().getStringExtra("getName");
        if(getname.equals("ChoiSeEun")) getLabelImg = "label2.jpg";
        else if(getname.equals("LeeNaYoung")) getLabelImg = "label1.jpg";
        //if(getname.equals("LeeNaYoung")) getLabelImg = "label1.png";
        //else if(getname.equals("ChoiSeEun")) getLabelImg = "label2.png";

        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");
        databaseReference.child("Employee").child(getname).child(currentH)
                .child("PersonalInfo").child("출근 시간").setValue("출근 시간: "+currentT);
        CurrentTime t = new CurrentTime();

        if(t.hour <= 10 && t.hour >= 7) {
            databaseReference.child("Employee").child(getname).child(currentH)
                    .child("PersonalInfo").child("지각 여부").setValue("지각 여부: "+false);
        }
        else databaseReference.child("Employee").child(getname).child(currentH)
                .child("PersonalInfo").child("지각 여부").setValue("지각 여부: "+true);


        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);

        progressBar1 = findViewById(R.id.progressBar);
        progressBar1.setVisibility(View.VISIBLE);

        progressBar2 = findViewById(R.id.progressBar4);
        progressBar2.setVisibility(View.VISIBLE);


        load = (ImageView) findViewById(R.id.imageView2);
       FirebaseStorage storage = FirebaseStorage.getInstance();
       StorageReference storageReference = storage.getReference();
       StorageReference pathReference = storageReference.child("jobInfo");

        if(pathReference == null){
           Toast.makeText(ShowInformation.this,"저장소에 사진이 없습니다.",Toast.LENGTH_SHORT).show();
       }
       else {
           StorageReference submitProfile = storageReference.child("jobInfo/"+getLabelImg);
           submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
               @Override
               public void onSuccess(Uri uri) {
                   progressBar.setVisibility(View.GONE);
                   Glide.with(ShowInformation.this).load(uri).into(load);

                   TextView textJobView = (TextView)findViewById(R.id.JobInfo); //TextView textJobViewENG = (TextView)findViewById(R.id.jobInfoENG);
                   TextView textNameView = (TextView)findViewById(R.id.nameInfo);

                   DatabaseReference myJob = database.getReference("Calendar").child("Employee").child(getname).child("EmployeeInfo");
                   myJob.addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           if(snapshot.getValue() !=null){
                               for(DataSnapshot Snapshot : snapshot.getChildren()){
                                   if(i==0) {
                                       progressBar1.setVisibility(View.GONE);
                                       String k = "" + Snapshot.getValue();
                                       textJobView.setText(" "+k);
                                        getjob = k;
                                   }
                                   else if(i==1)
                                   {
                                       progressBar2.setVisibility(View.GONE);
                                      String k = "" + Snapshot.getValue();
                                       textNameView.setText(" "+k);
                                   }
                                    i++;

                               }
                           }
                       }
                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });

                   speak("복장 규정 확인을 위해 객체 탐지를 진행합니다.");
                   Toast.makeText(ShowInformation.this,"복장 규정 확인을 위해 객체 탐지를 진행합니다.",Toast.LENGTH_SHORT).show();

                   Handler handler = new Handler();
                   handler.postDelayed(new Runnable() {
                       @Override
                       public void run() {

                           String getModels="", getlabelTxt="";
                           if(getjob.equals("중장비 관리자"))
                           {
                               getModels = "helmet.tflite";
                               getlabelTxt = "custom_label.txt";
                           }
                           else if(getjob.equals("화학 약품 관리자"))
                           {
                               getModels = "realLab.tflite";
                               getlabelTxt = "goggle_label.txt";
                           }

                           Intent intent = new Intent(ShowInformation.this,CameraHelmet.class);
                           intent.putExtra("getModels",getModels);
                           intent.putExtra("getlabelTxt",getlabelTxt);
                           intent.putExtra("getName",getname);
                           intent.putExtra("getJob",getjob);
                           startActivity(intent);

                       }
                   },10000);

               }

           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {

               }
           });
       }

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.KOREAN); // 언어 설정 (예: 영어)
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language is not supported.");
                    }
                } else {
                    Log.e("TTS", "Initialization failed.");
                }
            }
        });
    }
    public void speak(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }
}