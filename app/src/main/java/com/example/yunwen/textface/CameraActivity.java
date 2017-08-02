package com.example.yunwen.textface;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import com.example.yunwen.textface.camera.CameraInterface;
import com.example.yunwen.textface.camera.preview.CameraSurfaceView;
import com.example.yunwen.textface.mode.GoogleFaceDetect;
import com.example.yunwen.textface.person.Changeactivity;
import com.example.yunwen.textface.ui.FaceView;
import com.example.yunwen.textface.util.DisplayUtil;
import com.example.yunwen.textface.util.EventUtil;

import java.util.Timer;
import java.util.TimerTask;


public class CameraActivity extends Activity {
    /***/
    CameraSurfaceView surfaceView = null;
    ImageButton shutterBtn;
    ImageButton switchBtn;
    FaceView faceView;
    float previewRate = -1f;
    private MainHandler mMainHandler = null;
    GoogleFaceDetect googleFaceDetect = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initUI();
        initViewParams();
        mMainHandler = new MainHandler();
        googleFaceDetect = new GoogleFaceDetect(getApplicationContext(), mMainHandler);

        shutterBtn.setOnClickListener(new BtnListeners());
        switchBtn.setOnClickListener(new BtnListeners());
        mMainHandler.sendEmptyMessageDelayed(EventUtil.CAMERA_HAS_STARTED_PREVIEW, 1500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }

    Timer timer=new Timer();
    private void initUI(){
        surfaceView = (CameraSurfaceView)findViewById(R.id.camera_surfaceview);
        shutterBtn = (ImageButton)findViewById(R.id.btn_shutter);
        switchBtn = (ImageButton)findViewById(R.id.btn_switch);
        faceView = (FaceView)findViewById(R.id.face_view);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(faceView.isthePeople()){
                    startActivity(new Intent(CameraActivity.this,TwoActivity.class));
                    timer.cancel();
                    finish();
                }else{
                    return;
                }
            }
        },200,200);
    }

    private void initViewParams(){
        LayoutParams params = surfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        previewRate = DisplayUtil.getScreenRate(this);
        surfaceView.setLayoutParams(params);
    }




    private class BtnListeners implements OnClickListener{
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btn_shutter:
                    takePicture();
                    break;
                case R.id.btn_switch:
                    switchCamera();
                    break;
                default:break;
            }
        }
    }


    private  class MainHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case EventUtil.UPDATE_FACE_RECT:
                    Face[] faces = (Face[]) msg.obj;
                    faceView.setFaces(faces);
                    break;
                case EventUtil.CAMERA_HAS_STARTED_PREVIEW:
                    startGoogleFaceDetect();
                    break;
            }
            super.handleMessage(msg);
        }

    }

    private void takePicture(){
        CameraInterface.getInstance().doTakePicture();
        mMainHandler.sendEmptyMessageDelayed(EventUtil.CAMERA_HAS_STARTED_PREVIEW, 1500);
    }


    private void switchCamera(){
        stopGoogleFaceDetect();
        int newId = (CameraInterface.getInstance().getCameraId() + 1)%2;
        CameraInterface.getInstance().doStopCamera();
        CameraInterface.getInstance().doOpenCamera(null, newId);
        CameraInterface.getInstance().doStartPreview(surfaceView.getSurfaceHolder(), previewRate);
        mMainHandler.sendEmptyMessageDelayed(EventUtil.CAMERA_HAS_STARTED_PREVIEW, 1500);
//		startGoogleFaceDetect();

    }
    private void startGoogleFaceDetect(){
        Camera.Parameters params = CameraInterface.getInstance().getCameraParams();
        if(params.getMaxNumDetectedFaces() > 0){
            if(faceView != null){
                faceView.clearFaces();
                faceView.setVisibility(View.VISIBLE);
            }
            CameraInterface.getInstance().getCameraDevice().setFaceDetectionListener(googleFaceDetect);
            CameraInterface.getInstance().getCameraDevice().startFaceDetection();
        }
    }
    private void stopGoogleFaceDetect(){
        Camera.Parameters params = CameraInterface.getInstance().getCameraParams();
        if(params.getMaxNumDetectedFaces() > 0){
            CameraInterface.getInstance().getCameraDevice().setFaceDetectionListener(null);
            CameraInterface.getInstance().getCameraDevice().stopFaceDetection();
            faceView.clearFaces();
        }
    }

}
