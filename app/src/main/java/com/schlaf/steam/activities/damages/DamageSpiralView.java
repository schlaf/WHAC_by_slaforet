package com.schlaf.steam.activities.damages;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.schlaf.steam.data.DamageGrid;
import com.schlaf.steam.data.SingleModel;
import com.schlaf.steam.data.WarbeastDamageSpiral;
import com.schlaf.steam.data.WarbeastDamageSpiral.AspectEnum;
import com.schlaf.steam.data.WarbeastDamageSpiral.DamageBranch;
import com.schlaf.steam.data.WarbeastDamageSpiral.DamageCircle;

public class DamageSpiralView extends DamageBaseView implements ColumnChangeNotifier  {
	
	private static final String TAG = "DamageSpiralView";
	
	/**
	 * pourcentage du rayon dédié é l'affichage
	 */
	private static final float POURCENTAGE_RAYON = 0.75f;
	/**
	 * ouverture de la spirale (90é)
	 */
	private static final float OUVERTURE_ANGULAIRE = (float) (Math.PI * 3 / 4);
	
	private static final double POSITIVE_ANGULAR_OFFSET = Math.PI / 11;
	private static final double NEGATIVE_ANGULAR_OFFSET = Math.PI / 10;
	
	private boolean edit = true;

	WarbeastDamageSpiral spiral;
	List<Coords> coords = new ArrayList<Coords>(50);
	
	Coords[][] staticCoordsOuter = new Coords[6][12]; // 6 branches external
	Coords[][] staticCoordsInner = new Coords[3][2]; // only 3 branches internal with 2 boxes each
	CoordsColumn[] staticCoordsBranches = new CoordsColumn[6];
	
	
	/** column to apply damages */
	private int currentColumn = 1; // column are numbered from 1 to 6 (not array-style!)
	List<CoordsColumn> coordsColumnIndicator = new ArrayList<CoordsColumn>(6);
	
	transient List<ColumnChangeObserver> columnObservers = new ArrayList<ColumnChangeObserver>();
	
	Paint paint = new Paint(); 
	int maxCircle = 0;
	
	BlurMaskFilter filter = new BlurMaskFilter(5, Blur.SOLID); // filter for aspect name
	
	int radialMultiplicator = 4;
	int radialMultiplicatorSelected = 4;

	Path pLine = new Path(); // path to draw line of the branch
	PathEffect pe = new CornerPathEffect(20); // path to make the previous path curvy
	
	PathEffect dash = new PathDashPathEffect(makePathDash(), 12, 5,
            PathDashPathEffect.Style.ROTATE); 
	PathEffect finalPath = new ComposePathEffect(pe, dash);

	
	RadialGradient gradient;
	
	/**
	 * association d'un cercle de dommage é son centre, permet de trouver oé l'on clique
	 * @author S0085289
	 *
	 */
	private class Coords {
		private DamageCircle circle;
		private int x;
		private int y;
		private int radius;
		
		public Coords(DamageCircle circle, int x, int y, int radius) {
			// Log.w(TAG, "Coords allocation, please avoid!");
			this.circle = circle;
			this.x = x;
			this.y = y;
			this.radius = radius;
		}
		
		public int distanceCarreeFrom(int xx, int yy) {
			return ( (xx-x)*(xx-x) + (yy-y)*(yy-y) );
		}
		
		public Coords populate(DamageCircle circle, int x, int y, int radius) {
			this.circle = circle;
			this.x = x;
			this.y = y;
			this.radius = radius;
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
		 case MotionEvent.ACTION_DOWN :
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

				if (distance < pointProche.radius * pointProche.radius * 3.5) {
					pointProche.circle.flipFlop();
					spiral.notifyBoxChange();
				}


				distance = 10000000;

				CoordsColumn indicateurColumnProche = null;
				for (CoordsColumn coord : coordsColumnIndicator) {
					int dist = coord.distanceCarreeFrom(evX, evY);
					if (dist < distance) {
						indicateurColumnProche = coord;
						distance = dist;
					}
				}
				// to touch branch indicator, enlarge sensibility zone.
				if (distance < pointProche.radius * pointProche.radius * 4.5) {
					if (indicateurColumnProche.colNumber != currentColumn) {
						currentColumn = indicateurColumnProche.colNumber;
						notifyColumnChange();
					}

				}
			}
			break;
		} // end switch
		return true;

	}
	
	public DamageSpiralView(Context context, AttributeSet attributes) {
		super(context, attributes);
		//getHolder().addCallback(this);
		//spiralThread = new DamageViewThread(getHolder(), this);
		setFocusable(true);
		if (isInEditMode()) {
			SingleModel sm = new SingleModel();
			sm.setDEF(0);
			sm.setARM(0);
			sm.setName("example");
			spiral = new WarbeastDamageSpiral(sm);
			spiral.fromString("9-13-12");
			spiral.fromString("6-5-9");
		}
		
		// initialize static boxes
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 12; j++) {
				staticCoordsOuter[i][j] = new Coords(null, 0, 0, 0);
			}
			staticCoordsBranches[i] = new CoordsColumn(i, 0, 0);
		}
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				staticCoordsInner[i][j] = new Coords(null, 0, 0, 0);
			}
		}
		
		
		
		
	}

	public DamageSpiralView(Context context) {
		this (context, null);
	}

	@Override
	public void draw(Canvas canvas) {

		int h = getHeight();
		int l = getWidth();
		
		paint.setStyle(Paint.Style.FILL);

		// make the entire canvas white
		paint.setColor(Color.BLACK);
		canvas.drawPaint(paint);
		
		// centre : 435 / 572
		int centerX = l / 2;
		int centerY = h / 2;

		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		
		int rayon = Math.min(h, l) / 2;

		if (maxCircle == 0 ) {
			// first pass only
			for (AspectEnum aspect : spiral.getBranches().keySet()) {
				DamageBranch branch = spiral.getBranches().get(aspect);
				maxCircle = Math.max(maxCircle, branch.getCirclesLittle().size());
			}
			if (maxCircle < 8) {
				maxCircle = 8;
			} else {
				maxCircle += 1; // for legend box
			}
		}
		
		
		coords.clear();
		coordsColumnIndicator.clear();

		// draw aspect name
		for (AspectEnum aspect : spiral.getBranches().keySet()) {
			DamageBranch branch = spiral.getBranches().get(aspect);

			paint.setColor(Color.WHITE);
			paint.setTextSize((float) h/15);
			paint.setMaskFilter(filter);
			if (branch.getAspect() == AspectEnum.BODY) {
				paint.setTextAlign(Align.RIGHT);
				canvas.drawText( branch.getAspect().name(), l-10, h - 10, paint);
			} else if (branch.getAspect() == AspectEnum.MIND) {
				paint.setTextAlign(Align.RIGHT);
				canvas.drawText( branch.getAspect().name(), l - 10, h /10 + 10, paint);
			} else if (branch.getAspect() == AspectEnum.SPIRIT) {
				paint.setTextAlign(Align.LEFT);
				canvas.drawText( branch.getAspect().name(), 10,  h /10 + 10 , paint);
			}
			paint.setMaskFilter(null);
			
			drawBranchesBackground(rayon, centerX, centerY, branch, paint, canvas, maxCircle);
		}

		for (AspectEnum aspect : spiral.getBranches().keySet()) {
			double theta = aspect.getTheta();
			
			DamageBranch branch = spiral.getBranches().get(aspect);
			drawInnerBranch(theta, rayon, centerX, centerY, branch.getCirclesInner(), paint, canvas, maxCircle, aspect);
			drawOuterBranch(theta, rayon, centerX, centerY, branch.getCirclesLittle(), paint, canvas, false, maxCircle, aspect);
			drawOuterBranch(theta, rayon, centerX, centerY, branch.getCirclesBig(), paint, canvas, true, maxCircle, aspect);
		}
		
		
	}

	private void drawBranchesBackground(int rayon, int centerX,
			int centerY, DamageBranch branch, Paint paint, Canvas canvas,  int maxCircle) {

		AspectEnum aspect = branch.getAspect();
		double theta = aspect.getTheta();
		
		List<DamageCircle> circles = branch.getCirclesLittle();
		int circlesCount = circles.size() +1;
		branch.getCirclesBig();
		
		int base = aspect.getColor();
		int red = Color.red(base);
		int green = Color.green(base);
		int blue = Color.blue(base);
		
		if (aspect.getBranchId1() == currentColumn && edit) {
			
			// first draw 2nd branch to avoid overwrite color
			drawOuterBranchBackground(theta, rayon, centerX, centerY, paint,
					canvas, maxCircle, circlesCount, radialMultiplicator, red,
					green, blue, true, false);	
			
			drawOuterBranchBackground(theta, rayon, centerX, centerY, paint,
					canvas, maxCircle, circlesCount, radialMultiplicatorSelected, red,
					green, blue, false, true);
			
		} else if (aspect.getBranchId2() == currentColumn && edit) {
			drawOuterBranchBackground(theta, rayon, centerX, centerY, paint,
					canvas, maxCircle, circlesCount, radialMultiplicator, red,
					green, blue, false, false);
			
			drawOuterBranchBackground(theta, rayon, centerX, centerY, paint,
					canvas, maxCircle, circlesCount, radialMultiplicatorSelected, red,
					green, blue, true, true);
		} else {
			
			drawOuterBranchBackground(theta, rayon, centerX, centerY, paint,
					canvas, maxCircle, circlesCount, radialMultiplicator, red,
					green, blue, true, false);	
			
			drawOuterBranchBackground(theta, rayon, centerX, centerY, paint,
					canvas, maxCircle, circlesCount, radialMultiplicator, red,
					green, blue, false, false);
		}
			

		for (int i = 0; i < 2; i++) {

			int R = (int) ((i + 1) * (rayon * POURCENTAGE_RAYON / maxCircle)); 
			double angle = (i + 1) * (OUVERTURE_ANGULAIRE / maxCircle) + theta; 
			angle -= NEGATIVE_ANGULAR_OFFSET;
			double tailleCercle = 0.05 * rayon; // 8% du rayon

			int x = (int) (R * Math.cos(angle)) + centerX;
			int y = (int) (R * Math.sin(angle)) + centerY;

			int shadedColor1 = Color.argb(60 + 255 * i / circlesCount / 2, red,
					green, blue);
			int shadedColor2 = Color.argb(0, red, green, blue);

			gradient = new RadialGradient(x, y, (int) tailleCercle
					* radialMultiplicator, shadedColor1, shadedColor2,
					TileMode.CLAMP);
			paint.setShader(gradient);
			canvas.drawCircle(x, y, (int) tailleCercle * radialMultiplicator,
					paint);

		}
		paint.setShader(null);
	}

	private void drawOuterBranchBackground(double theta, int rayon,
			int centerX, int centerY, Paint paint, Canvas canvas,
			int maxCircle, int circlesCount, int radialMultiplicator, int red,
			int green, int blue, boolean outer, boolean isSelected) {
		for (int i = 0; i < maxCircle + 1 ; i ++) { //  +1 car cercle de légende
			
			int R = (int) ( 2.5 * rayon * POURCENTAGE_RAYON / maxCircle + (i+1) * ( rayon * POURCENTAGE_RAYON / (maxCircle+2))) ; // on prend 80% du rayon
			double angle = (i+3) * ( OUVERTURE_ANGULAIRE / (maxCircle)) + theta  ; // on tourne sur 90é + pi/10
			if (outer) {
				angle += POSITIVE_ANGULAR_OFFSET;
			} else {
				angle -= NEGATIVE_ANGULAR_OFFSET;
			}
			double tailleCercle = (1+ ((double) i/maxCircle/2)) * 0.06d * rayon; // de 6% é 8% du rayon
			
			int x = (int) (R * Math.cos(angle)) + centerX;
			int y = (int) (R * Math.sin(angle)) + centerY;
			

			int shadedColor1 = Color.argb( 125 + 255 * i / maxCircle / 2 , red, green, blue);
			int shadedColor2 = Color.argb( 0 , red, green, blue);

//			if (isSelected) {
//				int newred = Color.red(Color.YELLOW);
//				int newgreen = Color.green(Color.YELLOW);
//				int newblue = Color.blue(Color.YELLOW);
//				shadedColor1 = Color.argb( 255 , newred, newgreen, newblue);
//				shadedColor2 = Color.argb( 200 , newred, newgreen, newblue);
//			}
			int shadedColor3 = Color.argb( 0 , 255, 255, 255);
	
			// draw background
//			if (isSelected) {
//				gradient = new RadialGradient(x, y, (int) tailleCercle * radialMultiplicator, new int[] {shadedColor1 , shadedColor2,shadedColor3 }, null, TileMode.CLAMP);
//				paint.setShader(gradient);
//			} else {
				gradient = new RadialGradient(x, y, (int) tailleCercle * radialMultiplicator, shadedColor1 , shadedColor2, TileMode.CLAMP);
				paint.setShader(gradient);
//			}
			canvas.drawCircle(x, y, (int) tailleCercle * radialMultiplicator ,paint);
			
			paint.setShader(null);
		}
	}


	private void drawOuterBranch(double theta, int rayon, int centerX,
			int centerY, List<DamageCircle> circles, Paint paint, Canvas canvas, boolean outer, int maxCircle, AspectEnum aspect) {

		pLine.reset();
		
		for (int i = 0; i < maxCircle + 1 ; i ++) { //  +1 car cercle de légende
			int R = (int) ( 2.5 * rayon * POURCENTAGE_RAYON / maxCircle + (i+1) * ( rayon * POURCENTAGE_RAYON / (maxCircle+2))) ; // on prend 80% du rayon
			double angle = (i+3) * ( OUVERTURE_ANGULAIRE / (maxCircle)) + theta  ; // on tourne sur 90é + pi/10
			
			if (outer) {
				angle += POSITIVE_ANGULAR_OFFSET;
			} else {
				angle -= NEGATIVE_ANGULAR_OFFSET;
			}
			
			int x = (int) (R * Math.cos(angle)) + centerX;
			int y = (int) (R * Math.sin(angle)) + centerY;

			if (i==0) {
				pLine.moveTo(x,y);
			} else {
				pLine.lineTo(x, y);
			}

		}
		
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1.5f);
		
		paint.setPathEffect(pe); // make curvy
		
		if (aspect.getBranchId1() == currentColumn && ! outer && edit) {
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(2.5f);
			//paint.setPathEffect(finalPath);
			canvas.drawPath(pLine, paint);
		} else if (aspect.getBranchId2() == currentColumn && outer && edit) {
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(2.5f);
			//paint.setPathEffect(finalPath);
			canvas.drawPath(pLine, paint);	
		} else {
			canvas.drawPath(pLine, paint);	
		}
		
		
		
		paint.setStyle(Paint.Style.FILL);
		paint.setPathEffect(null);

		
		for (int i = 0; i < maxCircle + 1 ; i ++) { //  +1 car cercle de légende
			
			boolean emptyCircleForLegend = (i >= circles.size() && i < maxCircle);
			boolean lastCircleForLegend = (i == maxCircle );
			
			int R = (int) ( 2.5 * rayon * POURCENTAGE_RAYON / maxCircle + (i+1) * ( rayon * POURCENTAGE_RAYON / (maxCircle+2))) ; // on prend 80% du rayon
			double angle = (i+3) * ( OUVERTURE_ANGULAIRE / (maxCircle)) + theta  ; // on tourne sur 90é + pi/10
			if (outer) {
				angle += POSITIVE_ANGULAR_OFFSET;
			} else {
				angle -= NEGATIVE_ANGULAR_OFFSET;
			}
			double tailleCercle = (1+ ((double) i/maxCircle/2)) * 0.06d * rayon; // de 6% é 8% du rayon
			
			int x = (int) (R * Math.cos(angle)) + centerX;
			int y = (int) (R * Math.sin(angle)) + centerY;
			
			
			// cercle extérieur
			if ( ! lastCircleForLegend && !emptyCircleForLegend) {
				int branchNumber = 0;
				if (outer) {
					branchNumber = aspect.getBranchId2()-1;
				} else {
					branchNumber = aspect.getBranchId1()-1;
				}
				coords.add(staticCoordsOuter[branchNumber][i].populate(circles.get(i), x, y, (int) tailleCercle));
			} 
			if (lastCircleForLegend) {
				paint.setColor(Color.WHITE);	// case de légende
			} else if (emptyCircleForLegend) {
				//paint.setColor(Color.BLACK);
				tailleCercle = 0;
			} else {
				paint.setColor(Color.BLACK);	// case de légende
			}
			
			
			
			if (!emptyCircleForLegend) {
				canvas.drawCircle(x, y, (int) tailleCercle,paint);
			}

			
			if (lastCircleForLegend) {
				// cercle de légende, blanc sauf si selectionné
				if (aspect.getBranchId1() == currentColumn && !outer && edit) {
					paint.setColor(Color.RED);
				}
				else if (aspect.getBranchId2() == currentColumn && outer && edit) {
					paint.setColor(Color.RED);
				} else {
					paint.setColor(Color.DKGRAY);
				}
			} else {
				// case de dommage
				if (! emptyCircleForLegend) {
					DamageCircle circle = circles.get(i);
					if (circle.isCurrentlyChangePending()) {
						if (circle.isDamaged() && circle.isDamagedPending()) {
							paint.setColor(BOX_INNER_COLOR_DAMAGED); // damaged : gray inside
						} else if (circle.isDamaged() && ! circle.isDamagedPending()){
							paint.setColor(BOX_INNER_COLOR_REPAIRED_PENDING);
						} else if (! circle.isDamaged() && circle.isDamagedPending()) {
							paint.setColor(BOX_INNER_COLOR_DAMAGED_PENDING);
						} else {
							paint.setColor(BOX_INNER_COLOR_OK); // no damaged : white inside
						}
					} else {
						if (circle.isDamaged()) {
							paint.setColor(BOX_INNER_COLOR_DAMAGED); // damaged : gray inside
						} else {
							paint.setColor(BOX_INNER_COLOR_OK); // no damaged : white inside
						}
					}
				}
			}
			
			canvas.drawCircle(x, y, (int) tailleCercle - 1 ,paint);
			
			
			if (lastCircleForLegend) {
				// cercle de légende
				paint.setColor(Color.WHITE);
				paint.setTextAlign(Align.CENTER);
				paint.setTextSize((float) 0.08 * rayon);
				paint.setTypeface(Typeface.DEFAULT_BOLD);
				canvas.drawText(String.valueOf(outer?aspect.getBranchId2():aspect.getBranchId1()), x, (int) (y + 0.03 * rayon), paint);
			}
			
			if (lastCircleForLegend) {
				// save coord
				int colNum = 0;
				if (outer) {
					colNum = aspect.getBranchId2();
				} else {
					colNum = aspect.getBranchId1();
				}
				
				// CoordsColumn coord = new CoordsColumn(colNum, x, y);
				coordsColumnIndicator.add(staticCoordsBranches[colNum-1].populate(colNum, x, y));
			}
			
		}	
		
		
	}


	private void drawInnerBranch(double theta, int rayon, int centerX,
			int centerY, List<DamageCircle> circlesInner, Paint paint, Canvas canvas, int maxCircle, AspectEnum aspect) {
		for (int i = 0; i < circlesInner.size(); i ++) {
			
			int R = (int) ( (i+1) * ( rayon * POURCENTAGE_RAYON / maxCircle)) ; // on prend POURCENTAGE_RAYON% du rayon
			double angle = (i+1) * ( OUVERTURE_ANGULAIRE / maxCircle) + theta; // on tourne sur OUVERTURE_ANGULAIRE radians
			angle -= NEGATIVE_ANGULAR_OFFSET;
			double tailleCercle = 0.05 * rayon  ; // 8% du rayon 
			
			int x = (int) (R * Math.cos(angle)) + centerX;
			int y = (int) (R * Math.sin(angle)) + centerY;
			
			
			coords.add(staticCoordsInner[aspect.ordinal()][i].populate(circlesInner.get(i), x, y, (int) tailleCercle));
			// coords.add(new Coords(circlesInner.get(i), x, y, (int) tailleCercle));
			
			paint.setColor(Color.BLUE);
			canvas.drawCircle(x, y, (int) tailleCercle,paint);
			
			DamageCircle circle = circlesInner.get(i);
			if (circle.isCurrentlyChangePending()) {
				if (circle.isDamaged() && circle.isDamagedPending()) {
					paint.setColor(BOX_INNER_COLOR_DAMAGED); // damaged : gray inside
				} else if (circle.isDamaged() && ! circle.isDamagedPending()){
					paint.setColor(BOX_INNER_COLOR_REPAIRED_PENDING);
				} else if (! circle.isDamaged() && circle.isDamagedPending()) {
					paint.setColor(BOX_INNER_COLOR_DAMAGED_PENDING);
				} else {
					paint.setColor(BOX_INNER_COLOR_OK); // no damaged : white inside
				}
			} else {
				if (circle.isDamaged()) {
					paint.setColor(BOX_INNER_COLOR_DAMAGED); // damaged : gray inside
				} else {
					paint.setColor(BOX_INNER_COLOR_OK); // no damaged : white inside
				}
			}

			canvas.drawCircle(x, y, (int) tailleCercle - 1 ,paint);
			
		}		
	}


	public boolean isEdit() {
		return edit;
	}


	public void setEdit(boolean edit) {
		this.edit = edit;
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		Log.d(TAG, "onMeasure");
		
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

		setMeasuredDimension(widthSize, heightSize);
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


	@Override
	public void onChangeDamageStatus(DamageGrid grid) {
		// statusChanged = true;
		invalidate();
	}
	
	public void setSpiral(WarbeastDamageSpiral spiral) {
		this.spiral = spiral;
		spiral.registerObserver(this);
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
	public void registerColumnObserver(ColumnChangeObserver observer) {
		if (columnObservers == null) {
			columnObservers = new ArrayList<ColumnChangeObserver>();
		}
		if (! columnObservers.contains(observer)) {
			columnObservers.add(observer);	
		}
		
	}

	private void notifyColumnChange() {
		for (ColumnChangeObserver observer : columnObservers) {
			observer.onChangeColumn(this);
		}
		invalidate();
	}
	

	public int getCurrentColumn() {
		return currentColumn;
	}


	public void setCurrentColumn(int currentColumn) {
		this.currentColumn = currentColumn;
		invalidate();
	}


	static Path pathDash;
    private static Path makePathDash() {
    	if (pathDash == null) {
    		pathDash = new Path();
    		pathDash.moveTo(-6, 0);
    		pathDash.lineTo(0, -6);
    		pathDash.lineTo(-12, -6);
    		pathDash.lineTo(-18, 0);
    		pathDash.lineTo(-12, 6);
    		pathDash.lineTo(0, 6);
    	}
        return pathDash;
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
