package com.schlaf.steam.activities.damages;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class RotaryKnobView extends View {

	private static final int START_ANGLE = 240;
	private static final int END_ANGLE = -60;
	private static final int ARC_WIDTH = 8;
	

	public interface OnRotationListener {
		public void onRotate(int thick);
	}
	
	private int maxValue = 16; // max number of offsets
	private int currentValue = 3; // current value, from 0 to maxValue -1;
	private boolean rotating = false; 

	Drawable scrubberNormal;
	Drawable scrubberRotating;

	Paint paint;
	Rect clipbounds = new Rect();

	

	private int[] BLUE_RGB = new int[] { 73, 206, 255 };
	private int[] RED_RGB = new int[] { 255, 73, 73 };

	public static final int RED = 1;
	public static final int BLUE = 2;

	float arcWidthPixel; // basic arc width converting in exact pixel (not dp)

	int screenSize;
	int padding;

	int w;
	int h;

	int wOrigin;
	int hOrigin;

	int wCenter;
	int hCenter;
	
	int radius;
	int externalRadius;
	int internalRadius;

	Resources r = getResources();

	private float mAngleDown;
	private float mAngleUp;
	private OnRotationListener m_listener;

	public RotaryKnobView(Context context) {
		super(context);
	}

	public RotaryKnobView(Context context, AttributeSet attributes) {
		super(context, attributes);
		setFocusable(true);

		scrubberNormal = context.getResources().getDrawable(
				com.schlaf.steam.R.drawable.scrubber_control_normal_holo);
		scrubberRotating = context.getResources().getDrawable(
				com.schlaf.steam.R.drawable.scrubber_control_pressed_holo);

		paint = new Paint();
		
	}


	@Override
	public void onDraw(Canvas canvas) {

		arcWidthPixel = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				ARC_WIDTH, r.getDisplayMetrics());

		w = getWidth();
		h = getHeight();
		screenSize = Math.min(w, h);

		wCenter = w / 2;
		hCenter = h / 2;

		padding = screenSize / 20;
		
		radius = ( w - padding ) / 2;
		internalRadius = (int) ( ( w - padding ) / 2 - arcWidthPixel / 2);
		externalRadius = (int) ( ( w - padding ) / 2 + arcWidthPixel / 2);

		drawMarks(canvas);
		drawArc(canvas);
		drawKnob(canvas);
		drawValue(canvas);

	}

	private void drawKnob(Canvas canvas) {

		int h = 32; // no need to calculate w, this is a circle!

		double angle = getAngle(currentValue, maxValue);
		
		int x = (int) (Math.cos(angle) * radius  + wCenter);
		int y = (int) (-Math.sin(angle) * radius + hCenter);

		if (rotating) {
			scrubberRotating.setBounds(x - h / 2, y - h / 2, x + h / 2, y + h
					/ 2);
			scrubberRotating.draw(canvas);
		} else {
			scrubberNormal
					.setBounds(x - h / 2, y - h / 2, x + h / 2, y + h / 2);
			scrubberNormal.draw(canvas);
		}

	}

	private void drawMarks(Canvas canvas) {

		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		paint.setShader(null);

		RectF oval = new RectF(wCenter - externalRadius, hCenter - externalRadius , wCenter + externalRadius
				, hCenter + externalRadius);
		for (int i = 0; i <= maxValue; i++) {
			canvas.drawArc(oval, - (START_ANGLE - i * (START_ANGLE - END_ANGLE) / maxValue - 0.75f), 1.5f, true, paint);
		}

		paint.setColor(Color.BLACK);
		paint.setShader(null);
		canvas.drawCircle(w / 2, h / 2, internalRadius , paint);
	}

	private void drawArc(Canvas canvas) {

		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setColor(Color.argb(255, 73, 206, 255));


		RectF oval = new RectF(wCenter - radius, hCenter - radius, wCenter + radius, hCenter + radius);
		
		canvas.drawArc(oval, - START_ANGLE,  currentValue * (START_ANGLE - END_ANGLE) / maxValue, false, paint);
		
		paint.setColor(Color.BLACK);
		paint.setShader(null);
		
		paint.setStyle(Paint.Style.FILL);
	}

	private void drawValue(Canvas canvas) {

		paint.setShader(null);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize((float) padding * 3f);
		String result = String.valueOf(currentValue);

		if (rotating) {
			paint.setColor(Color.WHITE);
			canvas.drawText(result, w / 2, h / 2 + padding * 1.2f, paint);

			// paint.setColor(Color.DKGRAY);
			BlurMaskFilter filter = new BlurMaskFilter(5, Blur.OUTER);
			paint.setMaskFilter(filter);
			canvas.drawText(result, w / 2, h / 2 + padding * 1.2f, paint);

		} else {
			paint.setColor(Color.DKGRAY);
			canvas.drawText(result, w / 2, h / 2 + padding * 1.2f, paint);
		}

		paint.setMaskFilter(null);
	}

	public void updateTicker() {
		invalidate();
	}

	
	/**
	 * lorsqu'on touche la vue.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		final int action = event.getAction();
		// (1)
		final int evX = (int) event.getX();
		final int evY = (int) event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			rotating = true;
			break;
		case MotionEvent.ACTION_UP:
			rotating = false;
			break;
		case MotionEvent.ACTION_MOVE: 
			float x = event.getX() / ((float) getWidth());
			float y = event.getY() / ((float) getHeight()); // high y = low in screen!
			float rotDegrees = calculateDegrees(x , 1 - y);// 1- to correct our
																// custom axis
																// direction
			
			
			
			Log.d("rotaryKnob", "x=" + x + " - y = "+ y + " - angle = " + rotDegrees);
			rotating = true;
			
			// -180 < rotDegrees < 180 --> faire un offset
			if ( -90 > rotDegrees && rotDegrees < END_ANGLE) {
				currentValue = maxValue;
			} else if ( rotDegrees < END_ANGLE) {
				rotDegrees += 360;
			}
			
			if (rotDegrees > START_ANGLE) {
				currentValue = 0;
			}
			if (rotDegrees < END_ANGLE ) {
				currentValue = maxValue;
			}
			if (rotDegrees < START_ANGLE && rotDegrees > END_ANGLE) {
				currentValue = (int) (START_ANGLE - rotDegrees) * maxValue / (START_ANGLE - END_ANGLE) ; 
				Log.d("rotaryKnob", "currentValue = " + currentValue);
			}
			  
			invalidate();
			break;
		} // end switch
		return true; // consume event

	}
	


	private float calculateDegrees(float x, float y) {
		
		float relativeX = x - 0.5f;
		float relativeY = y - 0.5f;
		
		double result = 0;
		
		result = Math.toDegrees(Math.atan2(relativeY, relativeX));
		
		// result = result / (2 * Math.PI) *360; 
		
		return (float)result;
		
	}

	private float getAngle(int value, int nbTickers) {
		
		float offsetAngle = (START_ANGLE - END_ANGLE) * value / nbTickers;
		
		float resultDegree = START_ANGLE - offsetAngle;
		
		float resultRadian = (float) ( Math.PI * 2 * resultDegree / 360 );
		
		return resultRadian;
		
	}
	
	public void setRotorPercentage(int percentage) {
		int posDegree = percentage * 3 - 150;
		if (posDegree < 0)
			posDegree = 360 + posDegree;
		setRotorPosAngle(posDegree);
	}

	private void setRotorPosAngle(float pos) {

	}
}

