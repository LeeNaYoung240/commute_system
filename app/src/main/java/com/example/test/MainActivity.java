package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.log4j.chainsaw.Main;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;

    static {
        if(OpenCVLoader.initDebug()){
            Log.d("MainActivity: ","Opencv is loaded");
        }
        else{
            Log.d("MainActivity: ","Opencv failed to load");
        }
    }

    private Button camera_button;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        camera_button=findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(), "출근 기록을 위해 사용자 인식을 시작하겠습니다.", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "정면을 바라봐주세요", Toast.LENGTH_SHORT).show();
                speak("출근 기록을 위해 사용자 인식을 시작하겠습니다. 정면을 바라봐주세요");

                Handler hander = new Handler();
                hander.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this,CameraActivity2.class);
                        intent.putExtra("isIn_Out",true);
                        finish();
                        startActivity(intent);
                    }
                },6000);

                //finish();
            }

        });
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

    public void onButton1Clicked2(View v)
    {
        CurrentTime t = new CurrentTime();
        if(t.hour>=18 )
        {

            Toast.makeText(this, "퇴근 가능한 시간입니다.", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "퇴근 기록을 위해 사용자 인식을 시작하겠습니다.", Toast.LENGTH_LONG).show();
            speak("퇴근 기록을 위해 사용자 인식을 시작하겠습니다.");
            Handler hander = new Handler();
            hander.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this,CameraActivity2.class);
                    intent.putExtra("isIn_Out",false);
                    startActivity(intent);
                }
            },6000);
        }
         // Toast는 간단한 메세지를 잠깐 보여주는 역할을 수행한다.
        else{
            Toast.makeText(this, "퇴근 불가능한 시간입니다.", Toast.LENGTH_LONG).show(); // Toast는 간단한 메세지를 잠깐 보여주는 역할을 수행한다.
            speak("퇴근 불가능한 시간입니다.");
        }

        //startActivity(new Intent(MainActivity.this,CameraHelmet.class)); finish();
    }
    public void testClick(View v){
        //Intent intent = new Intent(MainActivity.this, ShowEvents.class);
        Intent intent = new Intent(MainActivity.this, getOffView.class);
        startActivity(intent);
    }


}



