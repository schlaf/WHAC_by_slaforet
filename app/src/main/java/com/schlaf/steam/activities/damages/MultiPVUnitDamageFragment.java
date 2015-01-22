package com.schlaf.steam.activities.damages;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.battle.BattleListFragment.BattleListInterface;
import com.schlaf.steam.activities.battle.BattleSingleton;
import com.schlaf.steam.activities.battle.MultiPVUnit;
import com.schlaf.steam.adapters.DamageRowAdapter;
import com.schlaf.steam.data.DamageBox;
import com.schlaf.steam.data.DamageGrid;
import com.schlaf.steam.data.MultiPVUnitGrid;

public class MultiPVUnitDamageFragment extends DialogFragment implements DamageChangeObserver, OnItemClickListener, ColumnChangeObserver {

	private BattleListInterface listener;
	MultiPVUnitGrid grid;
	DamageUnitView unitDamageView;
	MultiPVUnit unit;
	
	ListView damagesListView;
	DamageRowAdapter adapter;
	private int lastDamageValueSelected = 0;

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d("SingleLineDamageFragment", "onCreateDialog");
		grid = (MultiPVUnitGrid) listener.getCurrentDamageGrid();
		
		
		int modelNumber = BattleSingleton.getInstance().getModelNumber();
		
		
		unit= (MultiPVUnit) listener.getCurrentModel();
		
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.apply_damages_to) + listener.getCurrentModel().getLabel());
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		View view = inflater.inflate(R.layout.damage_dialog_unit_fragment, null);

		
		int remainingPoints = grid.getDamageLines().get(modelNumber).getDamagePendingStatus().getRemainingPoints();
		
		SelectableDamageValue[] damagesNumber = new SelectableDamageValue[remainingPoints+1];
		for (int i = 0; i < remainingPoints + 1; i++) {
			damagesNumber[i] = new SelectableDamageValue(i);
		}
		damagesListView = (ListView) view.findViewById(R.id.listViewDamages);
		damagesListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		adapter = new DamageRowAdapter(getActivity(), damagesNumber);
		damagesListView.setAdapter(adapter);
		
		damagesListView.setOnItemClickListener(this);

//		
//		damageNumberPicker = (NumberPicker) view.findViewById(R.id.numberPickerDamage);
//		damageNumberPicker.setMinValue(0); // - grid.getDamageStatus().getDamagedPoints());
//		damageNumberPicker.setMaxValue(10);
//		// damageNumberPicker.setMaxValue(grid.getDamageStatus().getRemainingPoints());
//		damageNumberPicker.setOnValueChangedListener(this);
//		damageNumberPicker.setWrapSelectorWheel(false);
//		damageNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
//		
		
		view.findViewById(R.id.buttonHeal).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				grid.heal1point(unitDamageView.getSelectedModelNumber());;
			}
		});
		
		
		// cancel
		view.findViewById(R.id.buttonCancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				grid.resetFakeDamages();
				MultiPVUnitDamageFragment.this.dismiss();
			}
		});
		
		// commit
		view.findViewById(R.id.buttonCommit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				grid.commitFakeDamages();
				MultiPVUnitDamageFragment.this.dismiss();
			}
		});
		
		// apply
		view.findViewById(R.id.buttonApply).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				grid.commitFakeDamages();
				
				
				// check field promotion?
				if (unit.isLeaderAndGrunts()) {
					if (grid.getDamageLines().get(0).getDamageStatus().getRemainingPoints() == 0) {
						// leader is dead! 
						askForPromotion();
					}
				}
				
			}

			
		});

		unitDamageView = (DamageUnitView) view.findViewById(R.id.damageGridView1);		
		unitDamageView.setGrid(grid);
		unitDamageView.setSelectedModelNumber(modelNumber);
		unitDamageView.registerColumnObserver(this);
		
		
		setDamagesListHeight();
		
		grid.registerObserver(this);
		
		builder.setView(view);
		// Create the AlertDialog object and return it
		return builder.create();
	}
	
	private void setDamagesListHeight() {
		// always show at least 5 damages, even if only 1 line for pseudo-units.
		if(adapter.getCount() < 5){
	        View item = adapter.getView(0, null, damagesListView);
	        item.measure(0, 0);         
	        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.component_standard_size), (int) (5.5 * item.getMeasuredHeight()));
	        damagesListView.setLayoutParams(params);
		}
		
		if(adapter.getCount() >= 5) { // show no more than 8 damages, and not less than 5
			
			int showNb = Math.min(adapter.getCount(), 5);
			
	        View item = adapter.getView(0, null, damagesListView);
	        item.measure(0, 0);         
	        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.component_standard_size), (int) ((showNb + 0.5) * item.getMeasuredHeight()));
	        damagesListView.setLayoutParams(params);
	        
		}
		damagesListView.getParent().requestLayout();

	}

	
	
	private void askForPromotion() {
		
		if (getPromotionCandidates().length == 0) {
			return; // nobody to promote!
		}
		
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(R.string.field_promotion);
		
		alert.setItems(getPromotionCandidates(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            	ModelDamageLine promoted = getPromotionCandidatesLines().get(which);
            	ModelDamageLine oldLeader = grid.getMultiPvDamageLines().get(0);
        
            	int i = 0;
            	for (DamageBox box : promoted.getBoxes()) {
            		oldLeader.getBoxes().get(i).setCurrentlyChangePending(box.isCurrentlyChangePending());
            		oldLeader.getBoxes().get(i).setDamaged(box.isDamaged());
            		oldLeader.getBoxes().get(i).setDamagedPending(box.isDamagedPending());
            		i++;
            		
            		// fill all promoted boxes
            		box.setDamaged(true);
            		box.setDamagedPending(false);
            		box.setCurrentlyChangePending(false);
            		
            	}
            	
            	updateDamageList();
            	unitDamageView.invalidate();
            }
		});
		alert.show();
		
	}

	private String[] getPromotionCandidates() {
		ArrayList<String> promotionCandidates = new ArrayList<String>();
		for ( ModelDamageLine line : grid.getMultiPvDamageLines()) {
			if (line.getDamageStatus().getRemainingPoints() > 0) {
				promotionCandidates.add(line.getModel().getName());	
			}
		}
		return promotionCandidates.toArray(new String[promotionCandidates.size()]);
	}
	
	private ArrayList<ModelDamageLine> getPromotionCandidatesLines() {
		ArrayList<ModelDamageLine> promotionCandidates = new ArrayList<ModelDamageLine>();
		for ( ModelDamageLine line : grid.getMultiPvDamageLines()) {
			if (line.getDamageStatus().getRemainingPoints() > 0) {
				promotionCandidates.add(line);	
			}
		}
		return promotionCandidates;
	}

	
	@Override
	public void onAttach(Activity activity) {
		Log.d("SingleLineDamageFragment", "onAttach");
		super.onAttach(activity);
		if (activity instanceof BattleListInterface) {
			listener = (BattleListInterface) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement BattleListInterface");
		}
		
		
	}


	
    public void addDamage(int i) {
    	int modelNumber = unitDamageView.getSelectedModelNumber();
    	grid.applyFakeDamages(modelNumber , i);
    }
    
    public void removeDamage(int i) {
    	int modelNumber =  unitDamageView.getSelectedModelNumber();
    	grid.applyFakeDamages(modelNumber , -i);
    }

    
	private void updateDamageList() {
		
		lastDamageValueSelected = 0;
		
		int modelNumber = unitDamageView.getSelectedModelNumber();
		
		int remainingPoints = grid.getDamageLines().get(modelNumber).getDamagePendingStatus().getRemainingPoints();
		SelectableDamageValue[] damagesNumber = new SelectableDamageValue[remainingPoints+1];
		for (int i = 0; i < remainingPoints + 1; i++) {
			damagesNumber[i] = new SelectableDamageValue(i);
		}
		adapter = new DamageRowAdapter(getActivity(), damagesNumber);
		damagesListView.setAdapter(adapter);
		
		setDamagesListHeight();
		
	}


	@Override
	public void onChangeDamageStatus(DamageGrid zegrid) {
//		if (selectedModelNumber != unitDamageView.getSelectedModelNumber()) {
//			selectedModelNumber = unitDamageView.getSelectedModelNumber();
//			// update status of number picker depending on damage grid status
//			int remainingPoints = grid.getMultiPvDamageLines().get( selectedModelNumber).getDamageStatus().getRemainingPoints();
//			damageNumberPicker.setMinValue(0);
//			damageNumberPicker.setValue(0);
//			damageNumberPicker.setMaxValue(remainingPoints); 
//			damageNumberPicker.setWrapSelectorWheel(false);
//		}
//		
	}

	@Override
	public void onApplyCommitOrCancel(DamageGrid grid) {
		updateDamageList();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		grid.removeObserver(this);
		grid.removeObserver(unitDamageView);
		// unitDamageView.deleteColumnObserver(this);
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
			grid.applyFakeDamages( unitDamageView.getSelectedModelNumber() , offsetDamage);
		}
		
		for (int i = 0; i < adapter.getCount(); i++) {
			if (i != position) {
				adapter.getItem(i).setChecked(false);
			}
		}
		
		adapter.notifyDataSetChanged();
		
	}

	@Override
	public void onChangeColumn(ColumnChangeNotifier gridView) {
		updateDamageList();
	}
}
