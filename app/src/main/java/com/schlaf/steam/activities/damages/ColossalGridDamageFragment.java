package com.schlaf.steam.activities.damages;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.battle.BattleListFragment.BattleListInterface;
import com.schlaf.steam.adapters.DamageRowAdapter;
import com.schlaf.steam.data.ColossalDamageGrid;
import com.schlaf.steam.data.DamageGrid;
import com.schlaf.steam.data.WarjackDamageGrid;

public class ColossalGridDamageFragment extends DialogFragment implements DamageChangeObserver, ColumnChangeObserver,OnItemClickListener {

	private BattleListInterface listener;
	ColossalDamageGrid grid;
	WarjackDamageGridView leftDamageGridView;
	WarjackDamageGridView rightDamageGridView;
	DamageLineView forceFieldView;
	WarjackDamageGrid leftGrid;
	WarjackDamageGrid rightGrid;
	ModelDamageLine forceFieldGrid;
	
	
	ListView damagesListView;
	DamageRowAdapter adapter;
	private int lastDamageValueSelected = 0;

	
	
	int selectedSide = NONE;
	static final int NONE = 0;
	static final int LEFT = 1;
	static final int RIGHT = 2;
	
	// column selected in view
	int currentColumn = 0;
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d("ColossalGridDamageFragment", "onCreateDialog");
		
		grid = (ColossalDamageGrid) listener.getCurrentDamageGrid();
		leftGrid = grid.getLeftGrid();
		rightGrid = grid.getRightGrid();
		forceFieldGrid = grid.getForceFieldGrid();
		
		
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.apply_damages_to) + grid.getModel().getName());
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		// create ContextThemeWrapper from the original Activity Context with the custom theme
		Context context = new ContextThemeWrapper(getActivity(), R.style.WhacTheme);
		// clone the inflater using the ContextThemeWrapper
		LayoutInflater localInflater = inflater.cloneInContext(context);
		// inflate using the cloned inflater, not the passed in default	
		View view = localInflater.inflate(R.layout.damage_dialog_ff_colossal_fragment, null);
		

		leftDamageGridView = (WarjackDamageGridView) view.findViewById(R.id.damageGridViewLeft);		
		leftDamageGridView.setGrid((WarjackDamageGrid) leftGrid);
		leftDamageGridView.setEdit(true);
		leftDamageGridView.setCurrentColumn(0);
		leftDamageGridView.registerColumnObserver(this);
		leftGrid.registerObserver(leftDamageGridView);
		leftGrid.registerObserver(this);
		
		rightDamageGridView = (WarjackDamageGridView) view.findViewById(R.id.damageGridViewRight);		
		rightDamageGridView.setGrid((WarjackDamageGrid) rightGrid);
		rightDamageGridView.setEdit(true);
		rightDamageGridView.setCurrentColumn(-1);
		rightDamageGridView.registerColumnObserver(this);
		rightGrid.registerObserver(rightDamageGridView);
		rightGrid.registerObserver(this);

		forceFieldView = (DamageLineView) view.findViewById(R.id.damageLineForceField);
		if (forceFieldGrid != null) {
			forceFieldView.setForceField(true);
			forceFieldView.setDamageLine(forceFieldGrid);
			forceFieldView.setEdit(true);
			forceFieldGrid.registerObserver(forceFieldView);
			forceFieldGrid.registerObserver(this);
		} else {
			forceFieldView.setVisibility(View.GONE);
		}

		selectedSide = LEFT;
		
		SelectableDamageValue[] damagesNumber = new SelectableDamageValue[grid.getDamageStatus().getRemainingPoints()+1];
		for (int i = 0; i < grid.getDamageStatus().getRemainingPoints() + 1; i++) {
			damagesNumber[i] = new SelectableDamageValue(i);
		}
		damagesListView = (ListView) view.findViewById(R.id.listViewDamages);
		damagesListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		adapter = new DamageRowAdapter(getActivity(), damagesNumber);
		damagesListView.setAdapter(adapter);
		
		damagesListView.setOnItemClickListener(this);

//		view.findViewById(R.id.buttonHeal).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				grid.applyFakeDamages(damageGridView.getCurrentColumn(), -1);;
//			}
//		});
		
		view.findViewById(R.id.buttonHeal).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int column = -1;
		    	if (selectedSide == LEFT || selectedSide == NONE) {
		    		column = leftDamageGridView.getCurrentColumn();
		    	} else {
		    		column = rightDamageGridView.getCurrentColumn() + 6 ;
		    	}
				grid.heal1point(column);
			}
		});
		
		if (forceFieldGrid != null) {
			view.findViewById(R.id.buttonHealForceField).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					grid.heal1pointForceField();
				}
			});
		} else {
			view.findViewById(R.id.buttonHealForceField).setVisibility(View.GONE);	
		}
		
		
		
		view.findViewById(R.id.buttonCancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				grid.resetFakeDamages();
				ColossalGridDamageFragment.this.dismiss();
			}
		});
		
		view.findViewById(R.id.buttonCommit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				grid.commitFakeDamages();
				ColossalGridDamageFragment.this.dismiss();
			}
		});
		
		view.findViewById(R.id.buttonApply).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				grid.commitFakeDamages();
			}
		});
		
		builder.setView(view);

		// Create the AlertDialog object and return it
		return builder.create(); 
	}
	

	@Override
	public void onAttach(Activity activity) {
		Log.d("ColossalGridDamageFragment", "onAttach");
		super.onAttach(activity);
		if (activity instanceof BattleListInterface) {
			listener = (BattleListInterface) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement BattleListInterface");
		}
		
		
	}
	
    public void addDamage(int i) {
    	int column = -1;
    	if (selectedSide == LEFT || selectedSide == NONE) {
    		column = leftDamageGridView.getCurrentColumn();
    	} else {
    		column = rightDamageGridView.getCurrentColumn() + 6 ;
    	}
    	
    	grid.applyFakeDamages(column, i);
    	

    }
    
    public void removeDamage(int i) {
    	grid.applyFakeDamages(-1, -i);
    }


	@Override
	public void onChangeDamageStatus(DamageGrid notifyingGrid) {
		// updateDamagePicker();
		
		// propagate damage to colossal grid if message comes from one on L/R grid
//		if (notifyingGrid == leftGrid || notifyingGrid == rightGrid || notifyingGrid == forceFieldGrid) {
//			grid.notifyBoxChange();
//		} 
		
	}

	private void updateDamageList() {
		
		lastDamageValueSelected = 0;
		
		int remainingHP = 0;
		if ( selectedSide == LEFT) {
			remainingHP = leftGrid.getDamagePendingStatus().getRemainingPoints();
		} else {
			remainingHP = rightGrid.getDamagePendingStatus().getRemainingPoints();
		}
		
		if (forceFieldGrid != null) {
			remainingHP += forceFieldGrid.getDamagePendingStatus().getRemainingPoints();
		}
		
		SelectableDamageValue[] damagesNumber = new SelectableDamageValue[remainingHP+1];
		for (int i = 0; i < remainingHP + 1; i++) {
			damagesNumber[i] = new SelectableDamageValue(i);
		}
		adapter = new DamageRowAdapter(getActivity(), damagesNumber);
		damagesListView.setAdapter(adapter);
	}

	@Override
	public void onChangeColumn(ColumnChangeNotifier gridView) {
		Log.d("ColossalGridDamageFragment", "onChangeColumn");
		if (gridView == leftDamageGridView ) {
			rightDamageGridView.setCurrentColumn(-1);
			selectedSide = LEFT;
		} else if (gridView == rightDamageGridView ){
			leftDamageGridView.setCurrentColumn(-1);
			selectedSide = RIGHT;
		} else {
			selectedSide = NONE; // WTF?
		}
		updateDamageList();
	}
	

	@Override
	public void onApplyCommitOrCancel(DamageGrid grid) {
		updateDamageList();
	}

	/**
	 * on click on a damage value in listview
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final SelectableDamageValue item = (SelectableDamageValue) parent.getItemAtPosition(position);
		if (! item.isChecked()) {
			item.setChecked(true);
			int offsetDamage =  (position ) - lastDamageValueSelected;
			lastDamageValueSelected = position ;
			addDamage(offsetDamage);
			// grid.applyFakeDamages( damageGridView.getCurrentColumn() , offsetDamage);
		}
		
		for (int i = 0; i < adapter.getCount(); i++) {
			if (i != position) {
				adapter.getItem(i).setChecked(false);
			}
		}
		
		adapter.notifyDataSetChanged();
		
	}

	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		rightGrid.removeObserver(this);
		rightGrid.removeObserver(rightDamageGridView);
		rightDamageGridView.deleteColumnObserver(this);
		leftGrid.removeObserver(this);
		leftGrid.removeObserver(leftDamageGridView);
		leftDamageGridView.deleteColumnObserver(this);

		if (forceFieldGrid != null) {
			forceFieldGrid.removeObserver(this);
			forceFieldGrid.removeObserver(forceFieldView);
		}
	}

}
