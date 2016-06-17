package nxr.tpadnexus.lib.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import nxr.tpadnexus.lib.TPadNexusActivity;

public class BlackWhiteView extends View {

	private String TAG = new String("DepthMapView");
	private int height, width;

	private TPadNexusActivity tpadActivity;
	private final Context mainContext;

	private Paint dataPaint;
	private float scaleFactor;
	private Matrix scaleMat;

	private boolean openCvLoaded = false;

	private static volatile Bitmap dataBitmap = null;

	private static float py, px;
	private static int pixelData;

	public BlackWhiteView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mainContext = context;

		tpadActivity = (TPadNexusActivity) mainContext;

		Bitmap defaultBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
		setDataBitmap(defaultBitmap);

		dataPaint = new Paint();
		dataPaint.setColor(Color.DKGRAY);
		dataPaint.setAntiAlias(true);

		scaleMat = new Matrix();
		scaleFactor = 1;
		scaleMat.postScale(1 / scaleFactor, 1 / scaleFactor);

		Log.i(TAG, "Trying to load OpenCV library");
		if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_7, mainContext, mOpenCVCallBack)) {
			Log.e(TAG, "Cannot connect to OpenCV Manager");
		}
	}

	private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(getContext()) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.e("TEST", "Connected to OpenCV Manager");
				openCvLoaded = true;
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	public void setDataBitmap(Bitmap bmp) {
		dataBitmap = null;
		dataBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
		resetScaleFactor();
		invalidate();
	}

	private void resetScaleFactor() {
		scaleMat = null;
		scaleMat = new Matrix();
		scaleFactor = dataBitmap.getWidth() / (float) width;
		scaleMat.postScale(1 / scaleFactor, 1 / scaleFactor);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.MAGENTA);
		canvas.drawBitmap(dataBitmap, scaleMat, dataPaint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		resetScaleFactor();
		invalidate();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (openCvLoaded == false) {
			return false;
		}

		switch (event.getActionMasked()) {

		case MotionEvent.ACTION_DOWN:
			px = event.getX() * scaleFactor;
			py = event.getY() * scaleFactor;
			pixelData = dataBitmap.getPixel((int)px, (int)py);
			if (pixelData == -16777216) {
				tpadActivity.sendTPad(0.0f);
			} else {
				tpadActivity.sendTPad(0.8f);
			}
			break;

		case MotionEvent.ACTION_MOVE:
			px = event.getX() * scaleFactor;
			py = event.getY() * scaleFactor;
			pixelData = dataBitmap.getPixel((int)px, (int)py);
			if (pixelData == -16777216) {
				tpadActivity.sendTPad(0.0f);
			} else {
				tpadActivity.sendTPad(0.8f);
			}
			break;

		case MotionEvent.ACTION_UP:
			tpadActivity.sendTPad(0f);
			break;

		case MotionEvent.ACTION_CANCEL:
			break;
		}

		return true;
	}
}
