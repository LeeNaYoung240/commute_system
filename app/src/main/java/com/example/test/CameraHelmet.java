package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CameraHelmet extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private final String TAG="MainActivity";
    private Mat mRgba;
    private Mat mGray;
    private long prevFrameTime; private int frameCounter;
    private CameraBridgeViewBase mOpenCvCameraView;
    private ObjectDetectorClass objectDetectorClass;
    private TextToSpeech textToSpeech;

    boolean checkWears=false;
    int cnt=0;
    String getName,getJob;
    TextView fpsTextView;

    private double fps = 0;

    public String format_yyyyMMdd = "yyyyMMdd";
    private DatabaseReference databaseReference;
    Date currentTime = Calendar.getInstance().getTime();

    private BaseLoaderCallback mLoaderCallback =new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface
                        .SUCCESS:{
                    Log.i(TAG,"OpenCv Is loaded");
                    mOpenCvCameraView.enableView();
                }
                default:
                {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public CameraHelmet(){
        Log.i(TAG,"Instantiated new "+this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String getModel = getIntent().getStringExtra("getModels");
        String getLabel = getIntent().getStringExtra("getlabelTxt");
        getName = getIntent().getStringExtra("getName");
        getJob = getIntent().getStringExtra("getJob");

        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int MY_PERMISSIONS_REQUEST_CAMERA=0;
        // if camera permission is not given it will ask for it on device
        if (ContextCompat.checkSelfPermission(CameraHelmet.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(CameraHelmet.this, new String[] {Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }

        setContentView(R.layout.activity_camera_helmet);

        fpsTextView = findViewById(R.id.fpsTextView);

        ActivityCompat.requestPermissions(CameraHelmet.this,new String[]
                {android.Manifest.permission.CAMERA},1);

        mOpenCvCameraView=(CameraBridgeViewBase) findViewById(R.id.frame_Surface);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraPermissionGranted();
        mOpenCvCameraView.setCameraIndex(0);
        mOpenCvCameraView.enableFpsMeter();
        mOpenCvCameraView.disableView();
        mOpenCvCameraView.enableView();

        try{
            // Copy and paste model.tflite and label in assets folder
            // Now replace model name, label name and input size
            // input size is 300 for this model
            // We trained model on input size =320

            objectDetectorClass=new ObjectDetectorClass(getAssets(),
                    getModel,
                    getLabel,
                    320);
            Log.d("MainActivity","Model is successfully loaded");


        }
        catch (IOException e){
            Log.d("MainActivity","Getting some error");
            e.printStackTrace();
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
    @Override
    protected void onResume() {
        super.onResume();
        if(OpenCVLoader.initDebug()){
            Log.d(TAG,"Opencv initialization is done");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        }
        else{
            Log.d(TAG,"Opencv is not loaded. try again");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,this,mLoaderCallback);
        }

        Handler hander = new Handler();
        hander.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat formatH = new SimpleDateFormat(format_yyyyMMdd, Locale.getDefault());
                String currentH = formatH.format(currentTime);
                if(getJob.equals("중장비 관리자"))
                {
                    checkWears = (objectDetectorClass.getHelmet()&objectDetectorClass.getVest());

                    databaseReference.child("Employee").child(getName)
                            .child(currentH).child("PersonalInfo").child("Helmet")
                            .setValue("Helmet: " + objectDetectorClass.getHelmet());

                    databaseReference.child("Employee").child(getName)
                            .child(currentH).child("PersonalInfo").child("Vest")
                            .setValue("Vest: " + objectDetectorClass.getVest());

                }
                else if(getJob.equals("화학 약품 관리자"))
                {
                    checkWears = (objectDetectorClass.getLabwears() & objectDetectorClass.getGoggle() & objectDetectorClass.getMask());

                    databaseReference.child("Employee").child(getName)
                            .child(currentH).child("PersonalInfo").child("LabWears")
                            .setValue("LabWears: " + objectDetectorClass.getLabwears());
                    databaseReference.child("Employee").child(getName)
                            .child(currentH).child("PersonalInfo").child("Mask")
                            .setValue("Mask: " + objectDetectorClass.getMask());
                    databaseReference.child("Employee").child(getName)
                            .child(currentH).child("PersonalInfo").child("Goggle")
                            .setValue("Goggles: " + objectDetectorClass.getGoggle());

                }

                if(checkWears)
                {

                    if (cnt == 0) {
                        Intent intent = new Intent(CameraHelmet.this, ShowEvents.class);
                        intent.putExtra("getName",getName);
                        startActivity(intent);

                        Toast.makeText(CameraHelmet.this, "일정표를 불러오는 중입니다. . . .", Toast.LENGTH_LONG).show();

                    } cnt = 1;
                }
                else
                {

                    Toast.makeText(CameraHelmet.this,"복장 규정을 준수해주세요.",Toast.LENGTH_LONG).show();
                    speak("복장 규정을 준수해주세요.");
                    Intent intent = new Intent(CameraHelmet.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        }, 40000);


    }



    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView !=null){
            mOpenCvCameraView.disableView();
        }
    }

    public void onDestroy(){
        super.onDestroy();
        if(mOpenCvCameraView !=null){
            mOpenCvCameraView.disableView();
        }

    }

    public void onCameraViewStarted(int width ,int height){
        mRgba=new Mat(height,width, CvType.CV_8UC4);
        mGray =new Mat(height,width,CvType.CV_8UC1);
    }

    public void onCameraViewStopped(){
        mRgba.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        mRgba=inputFrame.rgba();
        mGray=inputFrame.gray();

        long currentTime = System.currentTimeMillis();

        mRgba = objectDetectorClass.recognizeImage(mRgba);

        if (prevFrameTime == 0) {
            prevFrameTime = currentTime;
        }
        long elapsedTime = currentTime - prevFrameTime;
        frameCounter++;

        // 1초(1000 밀리초)가 경과하면 FPS를 다시 계산합니다.
        if (elapsedTime >= 1000) {
            fps = (double) frameCounter / (elapsedTime / 1000.0);
            frameCounter = 0;
            prevFrameTime = currentTime;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fpsTextView.setText("FPS: " + String.format("%.2f", fps));
                }
            });
        }

        // FPS를 화면에 출력하거나 로그로 기록할 수 있습니다.
        Log.d(TAG, "FPS: " + fps);




        return mRgba;
    }

}