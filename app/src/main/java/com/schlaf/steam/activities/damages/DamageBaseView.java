package com.schlaf.steam.activities.damages;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

/**
 * abstract parent for all custom view providing damage visual status
 * @author S0085289
 *
 */
public abstract class DamageBaseView extends View implements DamageChangeObserver {

	protected static final int COLUMN_BORDER_COLOR = Color.RED;
	protected static final int COLUMN_INNER_COLOR = Color.LTGRAY;
	protected static final int BOX_BORDER_COLOR = Color.BLACK;
	protected static final int BOX_INNER_COLOR_OK = Color.WHITE;
	protected static final int BOX_INNER_COLOR_DAMAGED = Color.GRAY;
	protected static final int BOX_INNER_COLOR_DAMAGED_PENDING = Color.RED;
	protected static final int BOX_INNER_COLOR_REPAIRED_PENDING = Color.GREEN;
	
	
	public DamageBaseView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DamageBaseView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}


	
}
