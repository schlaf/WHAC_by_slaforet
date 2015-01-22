package com.schlaf.steam.activities.battleresult;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.battle.BattleResult;
import com.schlaf.steam.adapters.BattleResultRowAdapter;
import com.schlaf.steam.storage.StorageManager;

public class BattleResultsListFragment extends ListFragment {
	
	public static final String ID = "BattleResultsListFragment";

	public interface ChooseBattleResultListener {
		public void viewResultDetail(BattleResult result);
	}

	private boolean sortByDate = false;
	private boolean sortByPlayer = false;
	private boolean sortByWin = false;
	
	private List<BattleResult> lists;
	BattleResultRowAdapter adapter;
	
	ChooseBattleResultListener listener; // parent activity

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    lists = StorageManager.getBattleResults(getActivity());
	    
	    adapter = new BattleResultRowAdapter(getActivity(), lists);

	    setListAdapter(adapter);
	    
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		listener.viewResultDetail(lists.get(position));
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		Log.d("ExistingArmiesFragment", "onAttach");
		super.onAttach(activity);
		if (activity instanceof ChooseBattleResultListener) {
			listener = (ChooseBattleResultListener) activity;
		} else {
			throw new UnsupportedOperationException(activity.toString()
					+ " must implemenet ChooseBattleResultListener");
		}
	}
	
	public void notifyResultDeletion(BattleResult result) {
		adapter.remove(result);
		adapter.notifyDataSetChanged();
	}

	public void sortByDate() {
		if (sortByDate) {
			BattleResult.dateComparator.reverseOrder();
		}
		sortByDate = true;
		sortByPlayer = false;
		sortByWin = false;

		adapter.sort(BattleResult.dateComparator);
		adapter.notifyDataSetChanged();
	}

	public void sortByPlayer() {
		if (sortByPlayer) {
			BattleResult.player2Comparator.reverseOrder();
		}
		sortByDate = false;
		sortByPlayer = true;
		sortByWin = false;

		adapter.sort(BattleResult.player2Comparator);
		adapter.notifyDataSetChanged();
	}

	public void sortByWin() {
		
		if (sortByWin) {
			BattleResult.victoryComparator.reverseOrder();
		}
		sortByDate = false;
		sortByPlayer = false;
		sortByWin = true;
		
		adapter.sort(BattleResult.victoryComparator);
		adapter.notifyDataSetChanged();
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if (lists == null || lists.isEmpty()) {
			setEmptyText(getResources().getString(R.string.no_results));	
		}
	}

	
}
