package com.example.yunwen.textface.camera.preview;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera.CameraInfo;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.example.yunwen.textface.camera.CameraInterface;


public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "孟晨";
	CameraInterface mCameraInterface;
	Context mContext;
	SurfaceHolder mSurfaceHolder;

	public CameraSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		/**surface中获取holder*/
		mSurfaceHolder = getHolder();
		mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		CameraInterface.getInstance().doOpenCamera(null, CameraInfo.CAMERA_FACING_BACK);
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		CameraInterface.getInstance().doStartPreview(mSurfaceHolder, 1.333f);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		CameraInterface.getInstance().doStopCamera();
	}

	public SurfaceHolder getSurfaceHolder(){
		return mSurfaceHolder;
	}
	
}
