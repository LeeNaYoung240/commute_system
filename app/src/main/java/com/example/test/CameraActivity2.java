package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.Locale;

public class CameraActivity2 extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG="MainActivity";
    private Mat mGray;
    private Mat mRgba;
    int cnt = 0;
    private CameraBridgeViewBase mOpenCvCameraView;
    private long prevFrameTime; private int frameCounter;
    public face_Recognition face_Recognition;
    TextView fpsTextView;
    private double fps = 0;

    private BaseLoaderCallback mLoaderCallback=new BaseLoaderCallback(this) {
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
    public CameraActivity2(){
        Log.i(TAG,"Instantiated new "+this.getClass());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int MY_PERMISSIONS_REQUEST_CAMERA=0;

        if(ContextCompat.checkSelfPermission(CameraActivity2.this,android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(CameraActivity2.this,new String[]{android.Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_CAMERA);
        }
        setContentView(R.layout.activity_camera2);

        fpsTextView = findViewById(R.id.fpsTextView);

        //speak("출근 기록을 위해 사용자 인식을 시작하겠습니다.");

        ActivityCompat.requestPermissions(CameraActivity2.this,new String[]
                {android.Manifest.permission.CAMERA},1);

        mOpenCvCameraView=(CameraBridgeViewBase) findViewById(R.id.frame_Surface);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraPermissionGranted();
        mOpenCvCameraView.setCameraIndex(0);

        mOpenCvCameraView.enableView();

        try{
            int inputSize=64;
            face_Recognition  = new face_Recognition(getAssets(),
                    CameraActivity2.this,
                    "real_real_model.tflite", inputSize);

        }
        catch (IOException e){
            e.printStackTrace();
            Log.d("CameraActivity2","Model is not loaded");
        }

    }
    @Override
    protected  void onResume(){
        super.onResume();
        if(OpenCVLoader.initDebug()){
            Log.d(TAG,"Opencv initialization is done");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        }
        else{
            Log.d(TAG,"Opencv is not loaded. try again");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,this,mLoaderCallback);
        }

        boolean isIn = getIntent().getBooleanExtra("isIn_Out",false);

        Toast.makeText(CameraActivity2.this, "얼굴 인식 중 . . . .", Toast.LENGTH_SHORT).show();

        Handler hander = new Handler();
        hander.postDelayed(new Runnable() {
            @Override
            public void run() {
                int compare = face_Recognition.getName();

                if(compare==10)
                {
                    if(cnt == 0){
                        if(isIn){
                            Intent intent = new Intent(CameraActivity2.this,ShowInformation.class);
                            intent.putExtra("getName","ChoiSeEun");

                            startActivity(intent);
                        }
                        else{
                            Intent intent = new Intent(CameraActivity2.this,getOffView.class);
                            intent.putExtra("getName","ChoiSeEun");

                            startActivity(intent);
                        }
                    } cnt=1;
                }
                else if(compare ==20){
                    if(cnt ==0){
                        if(isIn)
                        {
                            Intent intent = new Intent(CameraActivity2.this,ShowInformation.class);
                            intent.putExtra("getName","LeeNaYoung");

                            startActivity(intent);
                        }
                        else
                        {
                            Intent intent = new Intent(CameraActivity2.this,getOffView.class);
                            intent.putExtra("getName","LeeNaYoung");

                            startActivity(intent);
                        }
                    } cnt=1;
                }
                else
                {
                    Toast.makeText(CameraActivity2.this, "잘못된 사용자 감지.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CameraActivity2.this,MainActivity.class);
                    intent.putExtra("getName","LeeNaYoung");

                    startActivity(intent);
                }
            }
        },7000);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mOpenCvCameraView!=null){
            mOpenCvCameraView.disableView();
        }
    }
    public void onDestroy(){

        super.onDestroy();
        if(mOpenCvCameraView!=null)
        {
            mOpenCvCameraView.disableView();
        }
    }
    public void onCameraViewStarted(int width, int height)
    {
        mRgba=new Mat(height,width, CvType.CV_8UC4);
        mGray=new Mat(height,width,CvType.CV_8UC1);

    }
    public void onCameraViewStopped(){
        mRgba.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){

        mRgba=inputFrame.rgba();
        mGray=inputFrame.gray();

        long currentTime = System.currentTimeMillis();

        mRgba=face_Recognition.recognizeImage(mRgba);

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


        //Core.flip(mRgba, mRgba, 1);
        return mRgba;
    }

}