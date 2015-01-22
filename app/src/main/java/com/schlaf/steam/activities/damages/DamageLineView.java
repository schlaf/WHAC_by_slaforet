package com.schlaf.steam.activities.damages;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.schlaf.steam.R;
import com.schlaf.steam.data.DamageBox;
import com.schlaf.steam.data.DamageGrid;

public class DamageLineView extends DamageBaseView  {

	private boolean edit;

	ModelDamageLine damageLine;
	
	List<Coords> coords = new ArrayList<Coords>();;
	private Coords[] coordsArray = new Coords[30];


	Rect clipbounds = new Rect();
	
	private Drawable textureFondForceField;
	
	private final Drawable textureCaseWhite;
	private final Drawable textureCaseGrey;
	private final Drawable textureCaseRed;
	private final Drawable textureCaseGreen;

	
	EmbossMaskFilter embossMaskFilter = new EmbossMaskFilter(new float[] { 2, 2, 2 }, 0.3f, 3.0f, 2.0f);
	Bitmap bitmap_Source = BitmapFactory.decodeResource(getResources(), R.drawable.texture_damages);

	
	Paint paint;

	private boolean forceField = false;
	
	/**
	 * association d'une case de dommage é son centre, permet de trouver oé l'on
	 * clique
	 * 
	 * @author S0085289
	 * 
	 */
	private class Coords {
		private DamageBox box;
		private int x;
		private int y;

		public Coords(DamageBox box, int x, int y) {
			this.box = box;
			this.x = x;
			this.y = y;
		}

		public int distanceCarreeFrom(int xx, int yy) {
			return ((xx - x) * (xx - x) + (yy - y) * (yy - y));
		}
		
		public Coords populate(DamageBox box, int x, int y) {
			this.box = box;
			this.x = x;
			this.y = y;
			return this;
		}
		

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
			break;
		case MotionEvent.ACTION_UP:

			if (edit) {
				int distance = 10000000;
				Coords pointProche = null;

				for (Coords coord : coords) {
					int dist = coord.distanceCarreeFrom(evX, evY);
					if (dist < distance) {
						pointProche = coord;
						distance = dist;
					}
				}

				pointProche.box.flipFlop();
				invalidate();
			} else {
//				System.out.println("shake!");
//				Animation shake = AnimationUtils.loadAnimation(getContext(),
//						R.anim.shake);
//				this.startAnimation(shake);
			}

			break;
		} // end switch
		return true;

	}

	public DamageLineView(Context context, AttributeSet attributes) {
		super(context, attributes);
		//getHolder().addCallback(this);
		// lineThread = new DamageViewThread(getHolder(), this);
		setFocusable(true);
		if (isInEditMode()) {
			damageLine = new ModelDamageLine(14,3);
		} else {
			damageLine = new ModelDamageLine(5,3);
		}
		
		textureFondForceField = context.getResources().getDrawable(R.drawable.forcefield_texture_colossal);
		
		textureCaseWhite = context.getResources().getDrawable(R.drawable.fond_case_grid_white);
		textureCaseGrey = context.getResources().getDrawable(R.drawable.fond_case_grid_grey);
		textureCaseGreen = context.getResources().getDrawable(R.drawable.fond_case_grid_green);
		textureCaseRed = context.getResources().getDrawable(R.drawable.fond_case_grid_red);

		for (int i = 0; i < 30; i++) {
			coordsArray[i] = new Coords(null, 0, 0);
		}
		
		paint = new Paint();
	}

	public DamageLineView(Context context) {
		this(context, null);
	}

	@Override
	public void onDraw(Canvas canvas) {

		int w = getWidth();
		int h = getHeight();

		
		paint.setStyle(Paint.Style.FILL);

		int padding = 3;

		int usableWidth = w - (padding * 2);
		
		paint.setAntiAlias(true);

		// make the entire canvas with background
		clipbounds.set(0, 0, w, h);
		if (forceField) {
			textureFondForceField.setBounds(clipbounds);
			textureFondForceField.draw(canvas);
		} else {
			if (isInEditMode()) {
				// canvas.drawBitmap(bitmap_Source, null, clipbounds, paint);
			} else {
//				paint.setMaskFilter(embossMaskFilter);
//				canvas.drawBitmap(bitmap_Source, null, clipbounds, paint);
//				paint.setMaskFilter(null);
			}
		}
		
		coords.clear();
		
		int yAxis = (int) ( h /2 ) ;
		

		int hitPoints = damageLine.getBoxes().size(); 
		// by default, ensure relatively constant layout even with low hit points
//		if ( damageLine.getBoxes().size() > 18) {
//			hitPoints = damageLine.getBoxes().size();	
//		}
		
		boolean moreThan10 = false;
		boolean moreThan20 = false;
		
		if (forceField) {
			// draw all boxes on 1 line
		} else {
			if (hitPoints > 10) {
				moreThan10 = true;
				yAxis = (int) ( h /4 ) ;
			}
			if (hitPoints > 20) {
				moreThan20 = true;
				yAxis = (int) ( h /6 ) ;
			}
			
			if (hitPoints > 10) {
				hitPoints = 10;
			}
		}
		
		
		
		
		int halfgridDimension = ((usableWidth / hitPoints) - 3) / 2;
		int nbLines = 1 + (moreThan10?1:0) + (moreThan20?1:0);
		
		halfgridDimension = Math.min( h/(3+nbLines), halfgridDimension); // ensure no box will be to high for the container...
		int colNum = 0;
		int realDamageBoxIndex = 0;
		int columnOffset = 0; // usableWidth - ( hitPoints * ( halfgridDimension * 2 + 3));
		int usableWidthRemaining = usableWidth - columnOffset;
		// int columnOffset = hitPoints - damageLine.getBoxes().size(); // offset boxes to the right if less boxes that expected hitpoints
		for (DamageBox box : damageLine.getBoxes()) {

			if (!forceField) {
				if (colNum > 9) {
					colNum = 0;
					
					if (moreThan20) {
						yAxis += (int) (h/3);
					} else {
						yAxis += (int) (h/2);
					}
				}
			}

			int xAxis = (int)  columnOffset + (usableWidthRemaining / hitPoints * colNum) + (usableWidthRemaining / hitPoints / 2)
					+ padding;

			
			Drawable target;
			if (box.isCurrentlyChangePending()) {
				if (box.isDamaged() && box.isDamagedPending()) {
					paint.setColor(BOX_INNER_COLOR_DAMAGED); // damaged : gray inside
					target = textureCaseGrey;
				} else if (box.isDamaged() && ! box.isDamagedPending()){
					paint.setColor(BOX_INNER_COLOR_REPAIRED_PENDING);
					target = textureCaseGreen;
				} else if (! box.isDamaged() && box.isDamagedPending()) {
					paint.setColor(BOX_INNER_COLOR_DAMAGED_PENDING);
					target = textureCaseRed;
				} else {
					paint.setColor(BOX_INNER_COLOR_OK); // no damaged : white inside
					target = textureCaseWhite;
				}
			} else {
				if (box.isDamaged()) {
					paint.setColor(BOX_INNER_COLOR_DAMAGED); // damaged : gray inside
					target = textureCaseGrey;
				} else {
					paint.setColor(BOX_INNER_COLOR_OK); // no damaged : white inside
					target = textureCaseWhite;
				}
			}
			
			target.setBounds(xAxis - halfgridDimension, yAxis
					- halfgridDimension, xAxis + halfgridDimension,
					yAxis + halfgridDimension);
			
			target.draw(canvas);
			
			
			coords.add(coordsArray[realDamageBoxIndex].populate(box, xAxis, yAxis));
			
			colNum ++;
			realDamageBoxIndex++;
		}

	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}
	
	public void setForceField(boolean forceField) {
		this.forceField = forceField;
	}

	public ModelDamageLine getDamageLine() {
		return damageLine;
	}

	public void setDamageLine(ModelDamageLine damageLine) {
		this.damageLine = damageLine;
		damageLine.registerObserver(this);
	}

//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		// TODO Auto-generated method stub
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
//		// int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
//		this.setMeasuredDimension(parentWidth, parentWidth); // make sure the view is square, based upon width
//	}

	@Override
	public void onChangeDamageStatus(DamageGrid grid) {
		// statusChanged = true;
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int mDrawableWidth = 480	; //320;
		int mDrawableHeight = 480	; // 320;
		int mMaxWidth = 480;
		int mMaxHeight = 480;

		int nbBoxes = damageLine.getBoxes().size();
		
		// Desired aspect ratio of the view's contents (not including padding)
		float desiredAspect = 5; // 0.0f;
		
		if (nbBoxes <= 5) {
			desiredAspect = 3; 
		}
		if (nbBoxes > 10 && nbBoxes <=20) {
			desiredAspect = 4; 
		}
		if (nbBoxes > 20) {
			desiredAspect = 2.5f;
		}
		
		int w;
		int h;

		

		// We are allowed to change the view's width
		boolean resizeWidth = true; // false;

		// We are allowed to change the view's height
		boolean resizeHeight = true; // false;

//		final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
//		final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

		w = mDrawableWidth;
		h = mDrawableHeight;

		// We are supposed to adjust view bounds to match the aspect
		// ratio of our drawable. See if that is possible.
//		if (true) {
//			resizeWidth = widthSpecMode != MeasureSpec.EXACTLY;
//			resizeHeight = heightSpecMode != MeasureSpec.EXACTLY;
//
//			desiredAspect = (float) w / (float) h;
//		}

		int pleft = 0;
		int pright = 0;
		int ptop = 0;
		int pbottom = 0;

		int widthSize;
		int heightSize;

		if (resizeWidth || resizeHeight) {
			/*
			 * If we get here, it means we want to resize to match the drawables
			 * aspect ratio, and we have the freedom to change at least one
			 * dimension.
			 */

			// Get the max possible width given our constraints
			widthSize = resolveAdjustedSize(w + pleft + pright, mMaxWidth,
					widthMeasureSpec);

			// Get the max possible height given our constraints
			heightSize = resolveAdjustedSize(h + ptop + pbottom, mMaxHeight,
					heightMeasureSpec);

			if (desiredAspect != 0.0f) {
				// See what our actual aspect ratio is
				float actualAspect = (float) (widthSize - pleft - pright)
						/ (heightSize - ptop - pbottom);

				if (Math.abs(actualAspect - desiredAspect) > 0.0000001) {

					boolean done = false;

					// Try adjusting width to be proportional to height
					if (resizeWidth) {
						int newWidth = (int) (desiredAspect * (heightSize
								- ptop - pbottom))
								+ pleft + pright;
						if (newWidth <= widthSize) {
							widthSize = newWidth;
							done = true;
						}
					}

					// Try adjusting height to be proportional to width
					if (!done && resizeHeight) {
						int newHeight = (int) ((widthSize - pleft - pright) / desiredAspect)
								+ ptop + pbottom;
						if (newHeight <= heightSize) {
							heightSize = newHeight;
						}
					}
				}
			}
		} else {
			/*
			 * We are either don't want to preserve the drawables aspect ratio,
			 * or we are not allowed to change view dimensions. Just measure in
			 * the normal way.
			 */
			w += pleft + pright;
			h += ptop + pbottom;

			w = Math.max(w, getSuggestedMinimumWidth());
			h = Math.max(h, getSuggestedMinimumHeight());

			widthSize = myResolveSizeAndState(w, widthMeasureSpec, 0);
			heightSize = myResolveSizeAndState(h, heightMeasureSpec, 0);
		}

		setMeasuredDimension(widthSize, heightSize);
	}
	
	private int resolveAdjustedSize(int desiredSize, int maxSize,
			int measureSpec) {
		int result = desiredSize;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		switch (specMode) {
		case MeasureSpec.UNSPECIFIED:
			/*
			 * Parent says we can be as big as we want. Just don't be larger
			 * than max size imposed on ourselves.
			 */
			result = Math.min(desiredSize, maxSize);
			break;
		case MeasureSpec.AT_MOST:
			// Parent says we can be as big as we want, up to specSize.
			// Don't be larger than specSize, and don't be larger than
			// the max size imposed on ourselves.
			result = Math.min(Math.min(desiredSize, specSize), maxSize);
			break;
		case MeasureSpec.EXACTLY:
			// No choice. Do what we are told.
			result = specSize;
			break;
		}
		return result;
	}	
	
	   /**
     * Utility to reconcile a desired size and state, with constraints imposed
     * by a MeasureSpec.  Will take the desired size, unless a different size
     * is imposed by the constraints.  The returned value is a compound integer,
     * with the resolved size in the {@link #MEASURED_SIZE_MASK} bits and
     * optionally the bit {@link #MEASURED_STATE_TOO_SMALL} set if the resulting
     * size is smaller than the size the view wants to be.
     *
     * @param size How big the view wants to be
     * @param measureSpec Constraints imposed by the parent
     * @return Size information bit mask as defined by
     * {@link #MEASURED_SIZE_MASK} and {@link #MEASURED_STATE_TOO_SMALL}.
     */
    public static int myResolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize =  MeasureSpec.getSize(measureSpec);
        switch (specMode) {
        case MeasureSpec.UNSPECIFIED:
            result = size;
            break;
        case MeasureSpec.AT_MOST:
            if (specSize < size) {
                result = specSize | MEASURED_STATE_TOO_SMALL;
            } else {
                result = size;
            }
            break;
        case MeasureSpec.EXACTLY:
            result = specSize;
            break;
        }
        return result | (childMeasuredState&MEASURED_STATE_MASK);
    }
    
	
	protected void oldonMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d("DamageLineView", "onMeasure : w =" + widthMeasureSpec + " h = " + heightMeasureSpec);
		
		int nbBoxes = damageLine.getBoxes().size();
		
		int desiredHeight = 72;
		if (nbBoxes > 10 && nbBoxes <=20) {
			desiredHeight = 144; 
		}
		if (nbBoxes > 20) {
			desiredHeight = 216;
		}
		
		
		int desiredWidth = desiredHeight * nbBoxes / 2;
		if (nbBoxes > 10) {
			desiredWidth = desiredHeight * 10 / 2;
		}

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		// Measure Width
		Log.d("DamageLineView", "widthMode = "); 
		if (widthMode == MeasureSpec.EXACTLY) {
			// Must be this size
			Log.d("DamageLineView", "EXACTLY" + widthSize);
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			// Can't be bigger than...
			width = Math.min(desiredWidth, widthSize);
			Log.d("DamageLineView", "AT_MOST" + width);
		} else {
			Log.d("DamageLineView", "I WHICH");
			// Be whatever you want
			width = desiredWidth;
		}

		// Measure Height
		Log.d("DamageLineView", "heightMode = "); 
		if (heightMode == MeasureSpec.EXACTLY) {
			Log.d("DamageLineView", "EXACTLY" + heightSize);
			// Must be this size
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			// Can't be bigger than...
			height = Math.min(desiredHeight, heightSize);
			Log.d("DamageLineView", "AT_MOST" + height);
		} else {
			Log.d("DamageLineView", "I WHICH " + desiredHeight);
			// Be whatever you want
			
			height = width / nbBoxes ;
			
			if (nbBoxes > 10 && nbBoxes <=20) {
				height = (int) (width / nbBoxes  * 2) ; 
			}
			if (nbBoxes > 20) {
				height = (int) (width / nbBoxes  * 3) ;
			}
			
		}

		// MUST CALL THIS
		setMeasuredDimension(width, height);

		// TODO Auto-generated method stub
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
//		int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
//		this.setMeasuredDimension(parentWidth, parentHeight); 
	}
	
	@Override
	public void onApplyCommitOrCancel(DamageGrid grid) {
		invalidate();
	}
	
}
