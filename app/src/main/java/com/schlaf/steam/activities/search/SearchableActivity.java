package com.schlaf.steam.activities.search;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.schlaf.steam.R;

public class SearchableActivity extends ActionBarActivity {

	private static final String TAG = "SearchableActivity";
	
	private ListView myList;
	
	public static final String INTENT_SPELL ="schlaf.intent.action.SPELL";
	public static final String INTENT_CARD ="schlaf.intent.action.CARD";
	public static final String INTENT_RULE ="schlaf.intent.action.RULE";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.search, null);

	    
	  //relate the listView from java to the one created in xml
        myList = (ListView) view.findViewById(R.id.searchResultList);
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{});
	    myList.setAdapter(adapter);
     
	    
	    setContentView(view);
	    
	    handleIntent(getIntent());

	}

	@Override
	protected void onNewIntent(Intent intent) {
	    setIntent(intent);
	    handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      doMySearch(query);
	    } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
	        // Handle a suggestions click (because the suggestions all use ACTION_VIEW)
	        Uri data = intent.getData();
	        showResult(data);
	    } else if (INTENT_SPELL.equals(intent.getAction())) {
	    	Uri data = intent.getData();
	    	Toast.makeText(this, "this is a spell " + data.toString(), Toast.LENGTH_SHORT).show();
	    }
	}

	private void showResult(Uri data) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "show result " + data.toString(), Toast.LENGTH_SHORT).show();
	}

	private void doMySearch(String query) {
		
		CursorLoader loader = new CursorLoader(this, SearchSuggestionProvider.CONTENT_URI, null, null,new String[] {query}, null);

		Cursor cursor  = loader.loadInBackground();
		
//		Cursor cursor = managedQuery(SearchSuggestionProvider.CONTENT_URI, null, null,
//                new String[] {query}, null);

		
//		// TODO Auto-generated method stub
//		Toast.makeText(this, "searched : " + query, Toast.LENGTH_SHORT).show();
//		
//		SearchHelper mDbHelper = new SearchHelper(this);
//		
//        mDbHelper.open();
// 
//        //Clear all names
//        mDbHelper.deleteAllNames();
// 
//        String[] nameList = new String[] { "Abomination", "Advance Deployment","Arc Node","Commander","Construct","Eyeless Sight","Critical Corrosion"};
//        
//        // Create the list of names which will be displayed on search
//        for (String name : nameList) {
//            mDbHelper.createList(name);
//        }	        
//        
//		
//		Cursor cursor = mDbHelper.searchByInputText((query != null ? query+"*" : "@@@@"));
		
		if (cursor != null) {

			String[] from = new String[] {SearchHelper.KEY_WORD};

			Log.d(TAG, "found " + cursor.getCount() + " entries");
			String value = cursor.getString(0);
			Log.d(TAG, "first value = " + value);
			
			// Create a simple cursor adapter to keep the search data
			SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, new int[]{android.R.id.text1});
			
			myList.setAdapter(cursorAdapter);

		}
	}		
	
}
