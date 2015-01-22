package com.schlaf.steam.activities.damages;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.battle.BattleListFragment.BattleListInterface;
import com.schlaf.steam.adapters.DamageRowAdapter;
import com.schlaf.steam.data.DamageGrid;

public class SingleLineDamageFragment extends DialogFragment implements DamageChangeObserver, OnItemClickListener {

	private BattleListInterface listener;
	DamageGrid grid;
	DamageLineView damageLineView;
	ListView damagesListView;
	DamageRowAdapter adapter;
	private int lastDamageValueSelected = 0;
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d("SingleLineDamageFragment", "onCreateDialog");
		grid = listener.getCurrentDamageGrid();
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.apply_damages_to) + listener.getCurrentModel().getLabel());
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		View view = inflater.inflate(R.layout.damage_dialog_line_fragment, null);
		
		
		view.findViewById(R.id.buttonHeal).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				grid.applyFakeDamages(-1);;
				// WarbeastSpiralDamageFragment.this.dismiss();
			}
		});
		
		// cancel
		view.findViewById(R.id.buttonCancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				grid.resetFakeDamages();
				SingleLineDamageFragment.this.dismiss();
			}
		});
		
		// commit
		view.findViewById(R.id.buttonCommit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				grid.commitFakeDamages();
				SingleLineDamageFragment.this.dismiss();
			}
		});
		
		// apply
		view.findViewById(R.id.buttonApply).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				grid.commitFakeDamages();
			}
		});
		
		//damageButton.setAttachedGrid( grid);

		damageLineView = (DamageLineView) view.findViewById(R.id.damageLineView1);		
		damageLineView.setDamageLine((ModelDamageLine) grid);
		damageLineView.setEdit(true);
		
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

		restrictDamagesListHeight();
		
		builder.setView(view);
		// Create the AlertDialog object and return it
		return builder.create();
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

	private void updateDamageList() {
		
		lastDamageValueSelected = 0;
		
		int remainingPoints =  ((ModelDamageLine) grid).getDamagePendingStatus().getRemainingPoints();
		SelectableDamageValue[] damagesNumber = new SelectableDamageValue[remainingPoints+1];
		for (int i = 0; i < remainingPoints + 1; i++) {
			damagesNumber[i] = new SelectableDamageValue(i);
		}
		adapter = new DamageRowAdapter(getActivity(), damagesNumber);
		damagesListView.setAdapter(adapter);
		
		
		restrictDamagesListHeight();
		
	}



	private void restrictDamagesListHeight() {
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
		damagesListView.requestLayout();
		damagesListView.getParent().requestLayout();
	}
	

	@Override
	public void onChangeDamageStatus(DamageGrid grid) {
		// update status of number picker depending on damage grid status
	}



	@Override
	public void onApplyCommitOrCancel(DamageGrid grid) {
		updateDamageList();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		grid.removeObserver(this);
		grid.removeObserver(damageLineView);
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
			grid.applyFakeDamages(offsetDamage);
		}
		
		for (int i = 0; i < adapter.getCount(); i++) {
			if (i != position) {
				adapter.getItem(i).setChecked(false);
			}
		}
		
		adapter.notifyDataSetChanged();
		
	}
	
	
}
