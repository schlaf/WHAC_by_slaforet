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
import com.schlaf.steam.data.DamageGrid;
import com.schlaf.steam.data.WarjackDamageGrid;

public class WarjackGridDamageFragment extends DialogFragment implements DamageChangeObserver, ColumnChangeObserver,OnItemClickListener {

	private static final String TAG = "WarjackGridDamageFragment";
	
	private BattleListInterface listener;
	WarjackDamageGridView damageGridView;
	DamageGrid grid;
	// DamageButtonView damageButton;
	
	
	ListView damagesListView;
	DamageRowAdapter adapter;
	private int lastDamageValueSelected = 0;

	// column selected in view
	int currentColumn = 0;
	
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d("WarjackGridDamageFragment", "onCreateDialog");
		
		grid = listener.getCurrentDamageGrid();
		
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.apply_damages_to) + grid.getModel().getName());
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		// create ContextThemeWrapper from the original Activity Context with the custom theme
		Context context = new ContextThemeWrapper(getActivity(), R.style.WhacTheme);
		// clone the inflater using the ContextThemeWrapper
		LayoutInflater localInflater = inflater.cloneInContext(context);
		// inflate using the cloned inflater, not the passed in default	
		View view = localInflater.inflate(R.layout.damage_dialog_grid_fragment, null);
		

		damageGridView = (WarjackDamageGridView) view.findViewById(R.id.damageGridView1);		
		damageGridView.setGrid((WarjackDamageGrid) grid);
		damageGridView.setEdit(true);
		damageGridView.setCurrentColumn(0);
		damageGridView.registerColumnObserver(this);
		grid.registerObserver(this);
		
		
		SelectableDamageValue[] damagesNumber = new SelectableDamageValue[grid.getDamageStatus().getRemainingPoints()+1];
		for (int i = 0; i < grid.getDamageStatus().getRemainingPoints() + 1; i++) {
			damagesNumber[i] = new SelectableDamageValue(i);
		}
		damagesListView = (ListView) view.findViewById(R.id.listViewDamages);
		damagesListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		adapter = new DamageRowAdapter(getActivity(), damagesNumber);
		damagesListView.setAdapter(adapter);
		
		damagesListView.setOnItemClickListener(this);

		view.findViewById(R.id.buttonHeal).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				grid.heal1point(damageGridView.getCurrentColumn());;
			}
		});
		
		view.findViewById(R.id.buttonCancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				grid.resetFakeDamages();
				WarjackGridDamageFragment.this.dismiss();
			}
		});
		
		view.findViewById(R.id.buttonCommit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				grid.commitFakeDamages();
				WarjackGridDamageFragment.this.dismiss();
			}
		});
		
		view.findViewById(R.id.buttonApply).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				damageNumberPicker.setValue(0);
//				damageNumberPicker.setWrapSelectorWheel(false);
				grid.commitFakeDamages();
			}
		});
		
		builder.setView(view);

		// Create the AlertDialog object and return it
		return builder.create(); 
	}
	

	@Override
	public void onAttach(Activity activity) {
		Log.d("WarjackGridDamageFragment", "onAttach");
		super.onAttach(activity);
		if (activity instanceof BattleListInterface) {
			listener = (BattleListInterface) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement BattleListInterface");
		}
		
		
	}

    public void addDamage(int i) {
    	grid.applyFakeDamages( damageGridView.getCurrentColumn() , i);
    	// damageNumberPicker.setMaxValue(grid.getDamageStatus().getRemainingPoints());
    }
    
    public void removeDamage(int i) {
    	grid.applyFakeDamages(damageGridView.getCurrentColumn() , -i);
    	// damageNumberPicker.setMaxValue(grid.getDamageStatus().getRemainingPoints());
    }


	@Override
	public void onChangeDamageStatus(DamageGrid grid) {
		Log.d(TAG, "onChangeDamageStatus");
	}
	
	@Override
	public void onChangeColumn(ColumnChangeNotifier gridView) {
		updateDamageList();
	}
	
	
	private void updateDamageList() {
		
		lastDamageValueSelected = 0;
		
		int remainingPoints = ((WarjackDamageGrid) grid).getDamagePendingStatus().getRemainingPoints();
		SelectableDamageValue[] damagesNumber = new SelectableDamageValue[remainingPoints+1];
		for (int i = 0; i < remainingPoints + 1; i++) {
			damagesNumber[i] = new SelectableDamageValue(i);
		}
		adapter = new DamageRowAdapter(getActivity(), damagesNumber);
		damagesListView.setAdapter(adapter);
	}
	
	@Override
	public void onApplyCommitOrCancel(DamageGrid grid) {
		updateDamageList();
	}


	@Override
	public void onDestroyView() {
		Log.d(TAG, "onDestroyView - remove listeners");
		super.onDestroyView();
		grid.removeObserver(this);
		grid.removeObserver(damageGridView);
		damageGridView.deleteColumnObserver(this);
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
			grid.applyFakeDamages( damageGridView.getCurrentColumn() , offsetDamage);
		}
		
		for (int i = 0; i < adapter.getCount(); i++) {
			if (i != position) {
				adapter.getItem(i).setChecked(false);
			}
		}
		
		adapter.notifyDataSetChanged();
		
	}

}
