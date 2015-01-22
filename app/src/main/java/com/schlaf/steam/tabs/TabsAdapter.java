package com.schlaf.steam.tabs;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewGroup;

/**
 * This is a helper class that implements the management of tabs and all details
 * of connecting a ViewPager with associated TabHost. It relies on a trick.
 * Normally a tab host has a simple API for supplying a View or Intent that each
 * tab will show. This is not sufficient for switching between pages. So instead
 * we make the content part of the tab host 0dp high (it is not shown) and the
 * TabsAdapter supplies its own dummy view to show as the tab content. It
 * listens to changes in tabs, and takes care of switch to the correct paged in
 * the ViewPager whenever the selected tab changes.
 */
public class TabsAdapter extends FragmentPagerAdapter implements
		ViewPager.OnPageChangeListener {
	private final ViewPager mViewPager;
	private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

	public static final class TabInfo {
		public final String tabId; // unique Id of tab, either player1, player2,
									// chrono, or Id of card
		private final String tabTitle;
		private Fragment fragment;
		private final Bundle args;

		TabInfo(String _tabId, String _tabTitle, Fragment _fragment,
				Bundle _args) {
			tabId = _tabId;
			tabTitle = _tabTitle;
			fragment = _fragment;
			args = _args;
		}
	}

	public TabsAdapter(ActionBarActivity activity, ViewPager pager) {
		super(activity.getSupportFragmentManager());
		mViewPager = pager;
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
	}

	public void addTabAtPosition(String tabId, String tabTitle,
			Fragment fragment, Bundle args, int position) {
		TabInfo info = new TabInfo(tabId, tabTitle, fragment, args);
		mTabs.add(position, info);
		notifyDataSetChanged();
	}

	public void addTab(String tabId, String tabTitle, Fragment fragment,
			Bundle args) {
		TabInfo info = new TabInfo(tabId, tabTitle, fragment, args);
		mTabs.add(info);
		// mActionBar.addTab(tab);
		notifyDataSetChanged();
	}

	public void removeTab(int index) {
		mTabs.remove(index);
		notifyDataSetChanged();
	}
	
	/**
	 * return the index of tab whose id is given, or -1 if not found.
	 * 
	 * @param id
	 * @return
	 */
	public int getTabIndexForId(String id) {
		for (TabInfo tab : mTabs) {
			if (tab.tabId.equals(id)) {
				return mTabs.indexOf(tab);
			}
		}
		return -1;
	}

	@Override
	public int getCount() {
		return mTabs.size();
	}

	@Override
	public long getItemId(int position) {
		TabInfo info = mTabs.get(position);
		String uniqueId = info.tabId;
		return uniqueId.hashCode();
	}

	@Override
	public Fragment getItem(int position) {
		TabInfo info = mTabs.get(position);
		return info.fragment;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		// mActionBar.setSelectedNavigationItem(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	public void selectTab(int position) {
		// mActionBar.setSelectedNavigationItem(position);
		mViewPager.setCurrentItem(position, true);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTabs.get(position).tabTitle;
	}

	@Override
	public int getItemPosition(Object object) {
		for (TabInfo info : mTabs) {
			if (info.fragment == object) {
				return mTabs.indexOf(info);
			}
		}
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Object obj = super.instantiateItem(container, position); // the "old" fragment
		Fragment fragment = mTabs.get(position).fragment;
		
		if (obj != fragment) {
			// desynchro!
			mTabs.get(position).fragment = (Fragment) obj; // restore old fragment in current adapter
		}
		
		return obj;
	}
}