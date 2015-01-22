package com.schlaf.steam.activities.damages;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.schlaf.steam.R;
import com.schlaf.steam.data.DamageBox;
import com.schlaf.steam.data.DamageColumn;
import com.schlaf.steam.data.DamageGrid;
import com.schlaf.steam.data.SingleModel;
import com.schlaf.steam.data.WarjackDamageGrid;
import com.schlaf.steam.data.WarmachineDamageSystemsEnum;

public class WarjackDamageGridView extends DamageBaseView implements ColumnChangeNotifier {

	private static final String TAG = "WarjackDamageGridView";
	private static final boolean D = false;

	private boolean edit = true;

	WarjackDamageGrid grid;
	List<Coords> coords = new ArrayList<Coords>(50);
	List<CoordsColumn> coordsColumnIndicator = new ArrayList<CoordsColumn>(6);
	
	// semi-static fields to avoid re-instanciations
	private Coords[][] coordsArray = new Coords[6][6];
	private CoordsColumn[] coordsColumnArray = new CoordsColumn[6];
	private LinearGradient[] shadersInner;
	private LinearGradient[] shadersBorder;
	RectF oval = new RectF();

	SquareViewMeasurer measurer = new SquareViewMeasurer(480);
	
	
	transient List<ColumnChangeObserver> columnObservers = new ArrayList<ColumnChangeObserver>();

	
	// private final Drawable textureFond;
	private final Drawable textureCaseWhite;
	private final Drawable textureCaseGrey;
	private final Drawable textureCaseRed;
	private final Drawable textureCaseGreen;

	Rect clipbounds = new Rect();;

	Paint paint;
	
	private int halfgridDimension;
	
	/** column to apply damages */
	private int currentColumn = -1;

	
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

				CoordsColumn indicateurColumnProche = null;
				for (CoordsColumn coord : coordsColumnIndicator) {
					int dist = coord.distanceCarreeFrom(evX, evY);
					if (dist < distance) {
						indicateurColumnProche = coord;
						distance = dist;
					}
				}
				if (distance < halfgridDimension * halfgridDimension * 2.2) {
					if (indicateurColumnProche.colNumber != currentColumn) {
						currentColumn = indicateurColumnProche.colNumber;
						notifyColumnChange();
						invalidate();
						return true; // if clicked on a column indicator, exit to prevent clicking on the first box in column!
					}
				}
				
				
				distance = 10000000;
				Coords pointProche = null;
				for (Coords coord : coords) {
					int dist = coord.distanceCarreeFrom(evX, evY);
					if (dist < distance) {
						pointProche = coord;
						distance = dist;
					}
				}
				if (distance < halfgridDimension * halfgridDimension * 3) {
					pointProche.box.flipFlop();	
				}
				
				invalidate();
			} 

			break;
		} // end switch
		return true; // consume event

	}

	public WarjackDamageGridView(Context context, AttributeSet attributes) {
		super(context, attributes);
		// getHolder().addCallback(this);
		// gridThread = new DamageViewThread(getHolder(), this);
		setFocusable(true);
		if (isInEditMode()) {
			grid = new WarjackDamageGrid(new SingleModel());
			grid.fromString("xxx.HHxxx.HCxx..CCxx..AMxxx.AMxxx.MM");
		} else {
			grid = new WarjackDamageGrid(new SingleModel());
			grid.fromString("xxxxxx");
		}
		
		// textureFond = context.getResources().getDrawable(R.drawable.fond_damage_grid);
		
		textureCaseWhite = context.getResources().getDrawable(R.drawable.fond_case_grid_white);
		textureCaseGrey = context.getResources().getDrawable(R.drawable.fond_case_grid_grey);
		textureCaseGreen = context.getResources().getDrawable(R.drawable.fond_case_grid_green);
		textureCaseRed = context.getResources().getDrawable(R.drawable.fond_case_grid_red);

		
		// intialize array of coords to prevent constant re-instanciation
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				coordsArray[i][j] = new Coords(null, 0, 0);
			}
			coordsColumnArray[i] = new CoordsColumn(i, 0, 0);
		}
		
		shadersInner = new LinearGradient[6];
		shadersBorder = new LinearGradient[6];

		paint = new Paint();
	}

	public WarjackDamageGridView(Context context) {
		this(context, null);
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {

		if (D) Log.d(TAG, "starting draw");
		
		int w = getWidth();
		int h = getHeight();

		
		paint.setStyle(Paint.Style.FILL);

		// make the entire canvas white
		clipbounds.set(0, 0, w, h);
		
		// textureFond.setBounds(clipbounds);
		// textureFond.draw(canvas);

		int padding = 7;

		int usableWidth = w - (padding * 2);
		halfgridDimension = ((usableWidth / 7) - 3) / 2;

		paint.setAntiAlias(true);

		coords.clear();
		coordsColumnIndicator.clear();
		
		if (edit) {
			if (currentColumn >= 0) {
				// draw chosen column in grey
				paint.setColor(COLUMN_INNER_COLOR);
				int xAxis = (int) (usableWidth / 6 * currentColumn) + usableWidth / 12
						+ padding;
				
				if (shadersInner[currentColumn] == null) {
					shadersInner[currentColumn] = new LinearGradient(xAxis - halfgridDimension - 1, padding/2 + 1, xAxis + halfgridDimension + 1,
							w - padding/2 - 1 , COLUMN_INNER_COLOR , Color.TRANSPARENT, TileMode.CLAMP);
				}
				
				paint.setShader(shadersInner[currentColumn]);
				
				canvas.drawRect(xAxis - halfgridDimension - 1, padding/2 + 1, xAxis + halfgridDimension + 1,
						w - padding/2 - 1, paint);
				
				paint.setColor(COLUMN_BORDER_COLOR);
				
				if (shadersBorder[currentColumn] == null) {
					shadersBorder[currentColumn] = new LinearGradient(xAxis - halfgridDimension - 3, padding/2 + 1, xAxis + halfgridDimension + 3,
							w - padding/2 - 1 , COLUMN_BORDER_COLOR , Color.TRANSPARENT, TileMode.CLAMP);
				}
				paint.setShader(shadersBorder[currentColumn]);
				paint.setStrokeWidth(3);
				paint.setStyle(Paint.Style.STROKE);
				canvas.drawRect(xAxis - halfgridDimension - 3, padding/2 + 1, xAxis + halfgridDimension + 3,
						w - padding/2 - 1, paint);

				// restor fill style
				paint.setShader(null);
				paint.setStyle(Paint.Style.FILL);
			}
		}
		
		
		for (DamageColumn column : grid.getColumns()) {
			int colNum = column.getId();
			int xAxis = (int) (usableWidth / 6 * colNum) + usableWidth / 12
					+ padding;

			int row = 0;
			
			// draw col number
			int cy = (int) usableWidth / 12  + padding;
			paint.setColor(Color.BLACK); // black border
			
			oval.set(xAxis - halfgridDimension, cy
					- halfgridDimension, xAxis + halfgridDimension, cy
					+ halfgridDimension);
			canvas.drawOval(oval, paint);
			
//			canvas.drawRect(xAxis - halfgridDimension, cy
//					- halfgridDimension, xAxis + halfgridDimension, cy
//					+ halfgridDimension, paint);
			if (colNum != currentColumn) {
				paint.setColor(Color.WHITE); // white inner	
			} else {
				paint.setColor(Color.GRAY);
			}
			oval.set(xAxis - halfgridDimension + 1, cy
					- halfgridDimension + 1, xAxis + halfgridDimension - 1,
					cy + halfgridDimension - 1);
			canvas.drawOval(oval, paint); // (xAxis, cy, halfgridDimension-2, paint);
//			canvas.drawRect(xAxis - halfgridDimension + 1, cy
//					- halfgridDimension + 1, xAxis + halfgridDimension - 1,
//					cy + halfgridDimension - 1, paint);
			
			if (colNum != currentColumn) {
				paint.setColor(Color.BLACK);	
			} else {
				paint.setColor(Color.WHITE);
			}
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize((float) halfgridDimension * 1.5f);
			canvas.drawText(String.valueOf(colNum+1), xAxis, cy + halfgridDimension * 0.55f, paint);
			
			coordsColumnIndicator.add(coordsColumnArray[colNum].populate(colNum, xAxis, cy));
			
			
			for (DamageBox box : column.getBoxes()) {

				if ( ! box.getSystem().equals(WarmachineDamageSystemsEnum.EMPTY)) {
					int yAxis = (int) (usableWidth / 7 * (row+1) ) + usableWidth / 12
							+ padding;

					
					
					Drawable target;
					if (box.isCurrentlyChangePending()) {
						if (box.isDamaged() && box.isDamagedPending()) {
//							paint.setColor(BOX_INNER_COLOR_DAMAGED); // damaged : gray inside
							target = textureCaseGrey;
						} else if (box.isDamaged() && ! box.isDamagedPending()){
//							paint.setColor(BOX_INNER_COLOR_REPAIRED_PENDING);
							target = textureCaseGreen;
						} else if (! box.isDamaged() && box.isDamagedPending()) {
//							paint.setColor(BOX_INNER_COLOR_DAMAGED_PENDING);
							target = textureCaseRed;
						} else {
//							paint.setColor(BOX_INNER_COLOR_OK); // no damaged : white inside
							target = textureCaseWhite;
						}
					} else {
						if (box.isDamaged()) {
//							paint.setColor(BOX_INNER_COLOR_DAMAGED); // damaged : gray inside
							target = textureCaseGrey;
						} else {
//							paint.setColor(BOX_INNER_COLOR_OK); // no damaged : white inside
							target = textureCaseWhite;
						}
					}
					
					target.setBounds(xAxis - halfgridDimension, yAxis
							- halfgridDimension, xAxis + halfgridDimension,
							yAxis + halfgridDimension);
					
					target.draw(canvas);
					
					
//					paint.setColor(BOX_BORDER_COLOR); // black border
//					canvas.drawRect(xAxis - halfgridDimension, yAxis
//							- halfgridDimension, xAxis + halfgridDimension, yAxis
//							+ halfgridDimension, paint); // (centerX, centerY, (int)
//															// 10 ,paint);
//					if (box.isCurrentlyChangePending()) {
//						if (box.isDamaged() && box.isDamagedPending()) {
//							paint.setColor(BOX_INNER_COLOR_DAMAGED); // damaged : gray inside
//						} else if (box.isDamaged() && ! box.isDamagedPending()){
//							paint.setColor(BOX_INNER_COLOR_REPAIRED_PENDING);
//						} else if (! box.isDamaged() && box.isDamagedPending()) {
//							paint.setColor(BOX_INNER_COLOR_DAMAGED_PENDING);
//						} else {
//							paint.setColor(BOX_INNER_COLOR_OK); // no damaged : white inside
//						}
//					} else {
//						if (box.isDamaged()) {
//							paint.setColor(BOX_INNER_COLOR_DAMAGED); // damaged : gray inside
//						} else {
//							paint.setColor(BOX_INNER_COLOR_OK); // no damaged : white inside
//						}
//					}
//					canvas.drawRect(xAxis - halfgridDimension + 1, yAxis
//							- halfgridDimension + 1, xAxis + halfgridDimension - 1,
//							yAxis + halfgridDimension - 1, paint);

					paint.setColor(BOX_BORDER_COLOR);
					paint.setTextAlign(Align.CENTER);
					paint.setTextSize((float) usableWidth / 12);
					canvas.drawText(box.getSystem().getCode(), xAxis, yAxis+usableWidth / 24, paint);
					
					// coords.add(new Coords(box, xAxis, yAxis));
					coords.add(coordsArray[colNum][row].populate(box, xAxis, yAxis));
					
				}

				row++;

			}

		}
		
		if (D) Log.d(TAG, "ending draw");
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
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
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		if (D) Log.d(TAG, "onMeasure");
		
		long t1 = System.currentTimeMillis();
		
		measurer.doMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(measurer.getChosenDimension(), measurer.getChosenDimension());
		
		long t2 = System.currentTimeMillis();
		if (D) Log.d(TAG, "square measurer " + (t2-t1));
		
		long t3 = System.currentTimeMillis();
		int mDrawableWidth = 480	; //320;
		int mDrawableHeight = 480	; // 320;
		int mMaxWidth = 480;
		int mMaxHeight = 480;

		int w;
		int h;

		// Desired aspect ratio of the view's contents (not including padding)
		float desiredAspect = 1; // 0.0f;

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

		long t4 = System.currentTimeMillis();
		if (D) Log.d(TAG, "standard measurer " + (t4-t3));

		
		// setMeasuredDimension(widthSize, heightSize);
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
    
    
	public WarjackDamageGrid getGrid() {
		return grid;
	}

	public void setGrid(WarjackDamageGrid grid) {
		this.grid = grid;
		grid.registerObserver(this);
	}

	@Override
	public void onChangeDamageStatus(DamageGrid grid) {
		if (D) Log.d(TAG, "onChangeDamageStatus");
		if (D) Log.d(TAG, "necessary invalidate...");
		invalidate();
	}

	public int getCurrentColumn() {
		return currentColumn;
	}

	public void setCurrentColumn(int currentColumn) {
		this.currentColumn = currentColumn;
		invalidate();
	}

	/**
	 * register an observer of this grid
	 * @param observer
	 */
	public void registerColumnObserver(ColumnChangeObserver observer) {
		if (columnObservers == null) {
			columnObservers = new ArrayList<ColumnChangeObserver>();
		}
		columnObservers.add(observer);
	}
	
	private void notifyColumnChange() {
		for (ColumnChangeObserver observer : columnObservers) {
			observer.onChangeColumn(this);
		}
	}

	@Override
	public void onApplyCommitOrCancel(DamageGrid grid) {
		invalidate();
	}

	@Override
	public void deleteColumnObserver(ColumnChangeObserver observer) {
		if (columnObservers != null) {
			columnObservers.remove(observer);
		}
	}
}
