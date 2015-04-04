package com.schlaf.steam.activities.damages;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.schlaf.steam.R;
import com.schlaf.steam.data.DamageBox;
import com.schlaf.steam.data.DamageGrid;
import com.schlaf.steam.data.MultiPVUnitGrid;

public class DamageUnitView extends DamageBaseView implements ColumnChangeNotifier {

	private static boolean D = false;
	
	MultiPVUnitGrid grid;
	
	List<Coords> coords = new ArrayList<Coords>();;
	
	private final Drawable textureCaseWhite;
	private final Drawable textureCaseGrey;
	private final Drawable textureCaseRed;
	private final Drawable textureCaseGreen;

	
//	EmbossMaskFilter embossMaskFilter = new EmbossMaskFilter(new float[] { 2, 2, 2 }, 0.3f, 3.0f, 2.0f);
//	Bitmap bitmap_Source = BitmapFactory.decodeResource(getResources(), R.drawable.texture_damages);

	
	private final static int BIG_LINE_H = 128;
	
	
	Paint paint;

	private int maxHitPoints; // max hitpoints for any model in unit

	private int modelCount; // number of models in unit
	
	private int lineCount; // 1 model = 1 line, except when > 10HP, count as 2
	
	private int selectedModelNumber = 3;
	
	private int halfgridDimension = 0;
	
	private ArrayList<Rect> textZones = new ArrayList<Rect>();
	
	Rect clipbounds = new Rect();
	Rect bounds = new Rect(); // for text size
	
	transient List<ColumnChangeObserver> columnObservers = new ArrayList<ColumnChangeObserver>();
	
	private int damageLineHeight = 0;
	
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
		
 
	}	
	
	public DamageUnitView(Context context, AttributeSet attributes) {
		super(context, attributes);
		init(context);
		
		textureCaseWhite = context.getResources().getDrawable(R.drawable.fond_case_grid_white);
		textureCaseGrey = context.getResources().getDrawable(R.drawable.fond_case_grid_grey);
		textureCaseGreen = context.getResources().getDrawable(R.drawable.fond_case_grid_green);
		textureCaseRed = context.getResources().getDrawable(R.drawable.fond_case_grid_red);

		
		
		
	}

	public DamageUnitView(Context context) {
		this(context, null);
	}
	
	private void init(Context context) {
		grid = new MultiPVUnitGrid();
		setFocusable(true);
		if (isInEditMode()) {
			ArrayList<ModelDamageLine> damageLines = new ArrayList<ModelDamageLine>();
			ModelDamageLine damageLine1 = new ModelDamageLine(22, 1);
//			ModelDamageLine damageLine2 = new ModelDamageLine(8, 2);
//			ModelDamageLine damageLine3 = new ModelDamageLine(8, 2);
			damageLines.add(damageLine1);
//			damageLines.add(damageLine2);
//			damageLines.add(damageLine3);
			grid.setDamageLines(damageLines);
			setGrid(grid);
			recalculateProportions();
			selectedModelNumber = 1;
			damageLineHeight = 72;
		} else {
			selectedModelNumber = 0;
			damageLineHeight = (int) getResources().getDimension(R.dimen.unit_damages_line_height);
		}
		
		
		
		
		
		paint = new Paint();
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

			int distance = 10000000;
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
				invalidate();
			} else {
				// change selected model only if not clicking a box...
				int modelNumber = 0;
//				Log.d("DamageUnitView", "testing point " + evX + " - " + evY);
				for (Rect textZone : textZones) {
					if (textZone.contains(evX, evY)) {
//						Log.d("DamageUnitView", "found in zone number " + modelNumber);
						selectedModelNumber = modelNumber;
						notifyColumnChange();
						// grid.notifyBoxChange();
						invalidate();
					}
					modelNumber ++;
				}
			}
			
			break;
		}
		return true; // consume event

	}
	
	
	@Override
	public void onDraw(Canvas canvas) {

		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		
		paint.setStyle(Style.FILL);

		int padding = 3;

		int usableWidth = w - (padding * 2);
		int usableHeight = h - (padding * 2);
		
		paint.setAntiAlias(true);

		// make the entire canvas with background
		clipbounds.set(0, 0, w, h);
		
//		paint.setMaskFilter(embossMaskFilter);
//		canvas.drawBitmap(bitmap_Source, null, clipbounds, paint);
		paint.setMaskFilter(null);

		
//		textureFond.setBounds(clipbounds);
//		textureFond.draw(canvas);
//		if (! isInEditMode()) {
//			
//		} else {
//			paint.setColor(Color.WHITE);
//			canvas.drawRect(clipbounds, paint);
//		}

		coords.clear();
		
		int modelNum = 0;
		
		// grid dimension depending on w, h, model count and max hit points...
		paint.setTextAlign(Align.LEFT);
		
		// textZones.clear();
		
		int yOffset = padding;
		for (ModelDamageLine damageLine : grid.getMultiPvDamageLines()) {
			
			boolean currentlyEdited = false;
			
			if (modelNum == selectedModelNumber) {
				currentlyEdited = true;
			}
			int heightDrawn = drawModelLine(modelNum, damageLine, currentlyEdited, yOffset, w, usableWidth, usableHeight, canvas); 
			
			yOffset += heightDrawn;
			
			modelNum ++;
		}


	}

	/**
	 * 
	 * @param modelNum
	 * @param damageLine
	 * @param currentlyEdited
	 * @param yOffset
	 * @param usableHeight 
	 * @param usableWidth 
	 * @param canvas 
	 * @return new yOffset
	 */
	private int drawModelLine(int modelNum, ModelDamageLine damageLine,
			boolean currentlyEdited, int yOffset, int fullWidth,  int usableWidth, int usableHeight, Canvas canvas) {
		
		
		int modelHitPoints = damageLine.getTotalHits();
		int padding = 3;
		
		int modelZoneHeight = 0;
		if (grid.getMultiPvDamageLines().size() == 1) {
			modelZoneHeight = usableHeight;
		} else if (grid.getMultiPvDamageLines().size() == 2) { // divide 1/3 2/3
			if (currentlyEdited) {
				modelZoneHeight = usableHeight * 2 / 3;
			} else {
				modelZoneHeight = usableHeight / 3;	
			}
		} else if (grid.getMultiPvDamageLines().size() == 3) { // divide 1/2 1/4 1/4
			if (currentlyEdited) {
				modelZoneHeight = usableHeight / 2;
			} else {
				modelZoneHeight = usableHeight / 4;	
			}
		} else { // divide 1/3 for edited, 2/3 for all others
			if (currentlyEdited) {
				modelZoneHeight = usableHeight / 3;
			} else {
				modelZoneHeight = usableHeight * 2 / 3 / ( modelCount - 1);
			}
		}
		
		int yAxis = (int) ( modelZoneHeight /2 ) ;
		
		yAxis = yOffset + yAxis; 
		

		clipbounds.set(3, yOffset+3, fullWidth-3, yOffset  + modelZoneHeight);
		if (currentlyEdited) {
			paint.setStrokeWidth(2);
			paint.setStyle(Style.STROKE);
			paint.setColor(Color.RED);
			canvas.drawRect(clipbounds, paint);
			
			paint.setStyle(Style.FILL);
			paint.setColor(getResources().getColor(R.color.primary_text_default_material_light));
		} else {
			paint.setColor(getResources().getColor(R.color.secondary_text_default_material_light));
		}
		
		int yAxisText = yAxis; // - modelZoneHeight / 4;
		paint.setTextSize(modelZoneHeight / 4);
		
		String modelTitle = "";
		if (isInEditMode()) {
			modelTitle = "model xx #" + modelNum;
		} else {
			modelTitle = damageLine.getModel().getName();
		}
		
		
	    // ask the paint for the bounding rect if it were to draw this
	    // text
	    paint.getTextBounds(modelTitle, 0, modelTitle.length(), bounds);
	 
	    // get the height that would have been produced
	    int wText = bounds.right - bounds.left;
	 
	    float targetSize = (float) ( usableWidth - 20) *.9f;
	    
	    if (wText > targetSize) {
	    	// reduce to fit
		    // figure out what textSize setting would create that height
		    // of text
		    float reduce  = ((targetSize/wText));
		    // and set it into the paint
		    paint.setTextSize(modelZoneHeight / 4 * reduce);
	    }
		
	    canvas.drawText(modelTitle, 20 , yAxisText - modelZoneHeight / 5 , paint);
		
		if (textZones.get(modelNum) == null) {
			textZones.add(new Rect());
		}
		textZones.get(modelNum).set(0, yOffset, fullWidth, yOffset + modelZoneHeight);
		// textZones.add(textZone);
		
		///////////////////////////////////////
		// start drawing boxes
		///////////////////////////////////////
		
		boolean moreThan10 = false;
		boolean moreThan20 = false;

		int modelZoneBoxesHeight = modelZoneHeight / 2;
		
		if (modelHitPoints <= 10 ) {
			yAxis = yAxis + (int) ( modelZoneBoxesHeight /2 ) ;
		} else if (modelHitPoints > 10 && modelHitPoints <=20) {
			moreThan10 = true;
			yAxis = yAxis + (int) ( modelZoneBoxesHeight /4 ) ;
		} else if (modelHitPoints > 20) {
			moreThan20 = true;
			yAxis = yAxis + (int) ( modelZoneBoxesHeight /6 ) ;
		}
		
		if (modelHitPoints > 10) {
			modelHitPoints = 10;
		}

		
		int nbLines = 1 + (moreThan10?1:0) + (moreThan20?1:0);
		int halfgridDimension = ((fullWidth / modelHitPoints) - 3) / 2;
		halfgridDimension = Math.min( (modelZoneBoxesHeight/nbLines -3 ) /4 , halfgridDimension); // ensure no box will be to high for the container...

		
		int colNum = 0;
		int columnOffset = usableWidth - ( modelHitPoints * ( halfgridDimension * 2 + 3));

		// int columnOffset = usableWidth - ( hitPointUpToMax * ( halfgridDimension * 2 + 3));
		// offset boxes to the right if less boxes that expected hitpoints
		int usableWidthRemaining = usableWidth - columnOffset;

		
		for (DamageBox box : damageLine.getBoxes()) {

			if (colNum > 9) {
				colNum = 0;

				if (moreThan20) {
					yAxis += (int) (modelZoneBoxesHeight/3);
				} else {
					yAxis += (int) (modelZoneBoxesHeight/2);
				}
			}

			int xAxis = (int)  columnOffset + (usableWidthRemaining / modelHitPoints * colNum) + (usableWidthRemaining / modelHitPoints / 2)
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
			
			coords.add(new Coords(box, xAxis, yAxis));
			// coords.add(coordsArray[realDamageBoxIndex].populate(box, xAxis, yAxis));
			
			colNum ++;
		}

		
//		
//		if (twoLines) {
//			for (DamageBox box : damageLine.getBoxes()) {
//
//				if (colNum >=10) {
//					colNum = 0;
//					yAxisBoxes += modelZoneHeight / 4;
//				}
//				
//				int xAxis = (int)  columnOffset + (usableWidthRemaining / hitPoints * colNum) + (usableWidthRemaining / hitPoints / 2)
//						+ 3;
//				
//				
//				Drawable target;
//				if (box.isCurrentlyChangePending()) {
//					if (box.isDamaged() && box.isDamagedPending()) {
//						paint.setColor(BOX_INNER_COLOR_DAMAGED); // damaged : gray inside
//						target = textureCaseGrey;
//					} else if (box.isDamaged() && ! box.isDamagedPending()){
//						paint.setColor(BOX_INNER_COLOR_REPAIRED_PENDING);
//						target = textureCaseGreen;
//					} else if (! box.isDamaged() && box.isDamagedPending()) {
//						paint.setColor(BOX_INNER_COLOR_DAMAGED_PENDING);
//						target = textureCaseRed;
//					} else {
//						paint.setColor(BOX_INNER_COLOR_OK); // no damaged : white inside
//						target = textureCaseWhite;
//					}
//				} else {
//					if (box.isDamaged()) {
//						paint.setColor(BOX_INNER_COLOR_DAMAGED); // damaged : gray inside
//						target = textureCaseGrey;
//					} else {
//						paint.setColor(BOX_INNER_COLOR_OK); // no damaged : white inside
//						target = textureCaseWhite;
//					}
//				}
//				
//				target.setBounds(xAxis - halfgridDimension, yAxisBoxes
//						- halfgridDimension, xAxis + halfgridDimension,
//						yAxisBoxes + halfgridDimension);
//				
//				target.draw(canvas);
//
//				if (currentlyEdited) { // boxes out of the edit zones won't react
//					coords.add(new Coords(box, xAxis, yAxisBoxes));
//				}
//				colNum ++;
//			}
//		} else {
//			for (DamageBox box : damageLine.getBoxes()) {
//
//				int xAxis = (int)  columnOffset + (usableWidthRemaining / hitPointUpToMax * colNum) + (usableWidthRemaining / hitPoints / 2)
//						+ 3;
//				
//				
//				Drawable target;
//				if (box.isCurrentlyChangePending()) {
//					if (box.isDamaged() && box.isDamagedPending()) {
//						paint.setColor(BOX_INNER_COLOR_DAMAGED); // damaged : gray inside
//						target = textureCaseGrey;
//					} else if (box.isDamaged() && ! box.isDamagedPending()){
//						paint.setColor(BOX_INNER_COLOR_REPAIRED_PENDING);
//						target = textureCaseGreen;
//					} else if (! box.isDamaged() && box.isDamagedPending()) {
//						paint.setColor(BOX_INNER_COLOR_DAMAGED_PENDING);
//						target = textureCaseRed;
//					} else {
//						paint.setColor(BOX_INNER_COLOR_OK); // no damaged : white inside
//						target = textureCaseWhite;
//					}
//				} else {
//					if (box.isDamaged()) {
//						paint.setColor(BOX_INNER_COLOR_DAMAGED); // damaged : gray inside
//						target = textureCaseGrey;
//					} else {
//						paint.setColor(BOX_INNER_COLOR_OK); // no damaged : white inside
//						target = textureCaseWhite;
//					}
//				}
//				
//				target.setBounds(xAxis - halfgridDimension, yAxisBoxes
//						- halfgridDimension, xAxis + halfgridDimension,
//						yAxisBoxes + halfgridDimension);
//				
//				target.draw(canvas);
//
//				if (currentlyEdited) { // boxes out of the edit zones won't react
//					coords.add(new Coords(box, xAxis, yAxisBoxes));
//				}
//				colNum ++;
//			}
//		}
		
		
		return modelZoneHeight;
		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		if (D) Log.d("UnitDamageView", "onMeasure");
		
		int desiredWidth = 480;
		int desiredHeight = 1000; //  * grid.getDamageLines().size();
		
		if (lineCount <= 3 ) {
			desiredHeight = Math.min( lineCount * BIG_LINE_H,  desiredHeight);	
		} else {
			desiredHeight = Math.min( lineCount * damageLineHeight,  desiredHeight);
		}
		
		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		// Measure Width
		//Log.d("DamageLineView", "widthMode = "); 
		if (widthMode == MeasureSpec.EXACTLY) {
			// Must be this size
//			Log.d("UnitDamageView", "w EXACTLY" + widthSize);
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			// Can't be bigger than...
			width = Math.min(desiredWidth, widthSize);
//			Log.d("UnitDamageView", "w AT_MOST" + width);
		} else {
//			Log.d("UnitDamageView", "w I WHICH");
			// Be whatever you want
			width = desiredWidth;
		}

		// desiredHeight = (int) (width  / desiredAspectRatio);
		
		// Measure Height
		//Log.d("DamageLineView", "heightMode = "); 
		if (heightMode == MeasureSpec.EXACTLY) {
			if (D) Log.d("UnitDamageView", "H EXACTLY" + heightSize);
			// Must be this size
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			// Can't be bigger than...
			if (D) Log.d("UnitDamageView", "H AT_MOST" + heightSize);
			height = Math.min(desiredHeight, heightSize);
		} else {
			if (D) Log.d("UnitDamageView", "I WHICH " + desiredHeight);
			// Be whatever you want
			height = desiredHeight; // (int) (width  / desiredAspectRatio);
		}

		// MUST CALL THIS
		setMeasuredDimension(width, height);

	}	
	
	@Override
	public void onChangeDamageStatus(DamageGrid grid) {
		invalidate();
	}

	public MultiPVUnitGrid getGrid() {
		return grid;
	}

	public void setGrid(MultiPVUnitGrid grid) {
		this.grid = grid;
		grid.registerObserver(this);
		
		textZones = new ArrayList<Rect>(grid.getDamageLines().size());
		for (int i = 0; i< grid.getDamageLines().size(); i++) {
			textZones.add(new Rect());
		}
		recalculateProportions();
	}
	
	private void recalculateProportions() {
		
		lineCount = grid.getMultiPvDamageLines().size();
		
		for (ModelDamageLine damageLine : grid.getMultiPvDamageLines()) {
			if (damageLine.getTotalHits() > maxHitPoints) {
				maxHitPoints = damageLine.getTotalHits();
			}
			if (damageLine.getTotalHits() > 10) {
				lineCount++;
			}
		}
		
		modelCount = grid.getMultiPvDamageLines().size();
	}

	public int getSelectedModelNumber() {
		return selectedModelNumber;
	}

	public void setSelectedModelNumber(int selectedModelNumber) {
		if (selectedModelNumber != this.selectedModelNumber) {
			this.selectedModelNumber = selectedModelNumber;
			invalidate();
		}
		
	}

	@Override
	public void onApplyCommitOrCancel(DamageGrid grid) {
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
	public void deleteColumnObserver(ColumnChangeObserver observer) {
		if (columnObservers != null) {
			columnObservers.remove(observer);
		}
	}
}
