package com.example.yunwen.textface.ui;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.util.AttributeSet;
import android.widget.Toast;

import com.example.yunwen.textface.R;
import com.example.yunwen.textface.camera.CameraInterface;
import com.example.yunwen.textface.person.Changeactivity;
import com.example.yunwen.textface.util.Util;

public class FaceView extends android.support.v7.widget.AppCompatImageView {

	private static final String TAG = "孟晨";
	private Context mContext;
	/**画笔*/
	private Paint mLinePaint;
	/**脸部特征*/
	public static Face[] mFaces;
	private Matrix mMatrix = new Matrix();
	private RectF mRect = new RectF();
	private Drawable mFaceIndicator = null;



	public FaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		/**初始化画笔*/
		initPaint();
		mContext = context;
		mFaceIndicator = getResources().getDrawable(R.drawable.ic_face_find_2);
	}

	public static boolean isthePeople(){
		if(mFaces==null){
			return false;
		}
		return mFaces.length > 0 && mFaces!=null;
	}

	/**初始化画笔*/
	private void initPaint(){
		mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		int color = Color.rgb(98, 212, 68);
		mLinePaint.setColor(color);
		mLinePaint.setStyle(Style.STROKE);
		mLinePaint.setStrokeWidth(5f);
		mLinePaint.setAlpha(180);
	}


	/**将人脸信息传入后绘制*/
	public void setFaces(Face[] faces){
		this.mFaces = faces;
		invalidate();
	}

	/**清空人脸信息并绘制*/
	public void clearFaces(){
		mFaces = null;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(mFaces == null || mFaces.length < 1){
			return;
		}
		Toast.makeText(mContext, "有人", Toast.LENGTH_SHORT).show();
		boolean isMirror = false;
        /**CameraId为-1*/
		int Id = CameraInterface.getInstance().getCameraId();
		if(Id == CameraInfo.CAMERA_FACING_BACK){
			isMirror = false;
		}else if(Id == CameraInfo.CAMERA_FACING_FRONT){
			isMirror = true;
		}
		Util.prepareMatrix(mMatrix, isMirror, 90, getWidth(), getHeight());
		canvas.save();
		mMatrix.postRotate(0);
		canvas.rotate(-0);
		for(int i = 0; i< mFaces.length; i++){
			mRect.set(mFaces[i].rect);
			mMatrix.mapRect(mRect);
            mFaceIndicator.setBounds(Math.round(mRect.left), Math.round(mRect.top),
                    Math.round(mRect.right), Math.round(mRect.bottom));
            mFaceIndicator.draw(canvas);
		}
		canvas.restore();
		super.onDraw(canvas);
	}


}
