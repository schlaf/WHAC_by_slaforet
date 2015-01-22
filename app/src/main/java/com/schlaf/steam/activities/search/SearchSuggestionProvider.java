package com.schlaf.steam.activities.search;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class SearchSuggestionProvider extends ContentProvider {

	private static final String TAG = "SearchSuggestionProvider";
	private SearchHelper mDictionary;
	
	// UriMatcher stuff
    private static final int SEARCH_WORDS = 0;
    private static final int GET_WORD = 1;
    private static final int SEARCH_SUGGEST = 2;
    private static final int REFRESH_SHORTCUT = 3;
    public static String AUTHORITY = "com.schlaf.search.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/dictionary");
    
    

    
    

    // MIME types used for searching words or looking up a single definition
    public static final String WORDS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                                                  "/vnd.example.android.searchabledict";
    public static final String DEFINITION_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                                                       "/vnd.example.android.searchabledict";

    
    private static final UriMatcher sURIMatcher = buildUriMatcher();
    
    
    /**
     * Builds up a UriMatcher for search suggestion and shortcut refresh queries.
     */
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
        // to get definitions...
        matcher.addURI(AUTHORITY, "dictionary", SEARCH_WORDS);
        matcher.addURI(AUTHORITY, "dictionary/#", GET_WORD);
        // to get suggestions...
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

        /* The following are unused in this implementation, but if we include
         * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our suggestions table, we
         * could expect to receive refresh queries when a shortcutted suggestion is displayed in
         * Quick Search Box, in which case, the following Uris would be provided and we
         * would return a cursor with a single item representing the refreshed suggestion data.
         */
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
        return matcher;
    }

    
	
	@Override
	public boolean onCreate() {
		
		mDictionary = new SearchHelper(getContext());
		
        return false;
		
	}
	
	
	

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		// Use the UriMatcher to see what kind of query we have and format the db query accordingly
        switch (sURIMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                if (selectionArgs == null) {
                  throw new IllegalArgumentException(
                      "selectionArgs must be provided for the Uri: " + uri);
                }
                return getSuggestions(selectionArgs[0]);
            case SEARCH_WORDS:
                if (selectionArgs == null) {
                  throw new IllegalArgumentException(
                      "selectionArgs must be provided for the Uri: " + uri);
                }
                return search(selectionArgs[0]);
            case GET_WORD:
                return getWord(uri);
            case REFRESH_SHORTCUT:
                return refreshShortcut(uri);
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

		
		
//		
//		String query = uri.getLastPathSegment().toLowerCase();
//		Log.d(TAG, query);
//		
//		return mDbHelper.searchSuggestion(query);
		
//		MatrixCursor result = new MatrixCursor(new String[] {BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1});
//		if (query.startsWith("a")) {
//		result.addRow(new String[] {"1","Abomination"});
//		result.addRow(new String[] {"2","Ardent"});
//		}
//		if (query.startsWith("b")) {
//			result.addRow(new String[] {"1","Banana"});
//			result.addRow(new String[] {"2","Bonus"});
//			result.addRow(new String[] {"3","Benefit"});
//		}
//
//		result.addRow(new String[] {"4","Always"});
//		result.addRow(new String[] {"5","Show"});
//		result.addRow(new String[] {"6","This"});
//		
//		
//		return result;
	}
	
	
	private Cursor refreshShortcut(Uri uri) {
		/* This won't be called with the current implementation, but if we include
		 * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our suggestions table, we
		 * could expect to receive refresh queries when a shortcutted suggestion is displayed in
		 * Quick Search Box. In which case, this method will query the table for the specific
		 * word, using the given item Uri and provide all the columns originally provided with the
		 * suggestion query.
		 */
		String rowId = uri.getLastPathSegment();
		String[] columns = new String[] {
				BaseColumns._ID,
				SearchHelper.KEY_WORD,
				SearchHelper.KEY_DEFINITION,
				SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
				SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};

		return mDictionary.getWord(rowId, columns);
	}


    private Cursor search(String query) {
        query = query.toLowerCase();
        String[] columns = new String[] {
            BaseColumns._ID,
            SearchHelper.KEY_WORD,
            SearchHelper.KEY_DEFINITION,
            SearchManager.SUGGEST_COLUMN_INTENT_ACTION};

        return mDictionary.getWordMatches(query, columns);
      }

      private Cursor getWord(Uri uri) {
        String rowId = uri.getLastPathSegment();
        String[] columns = new String[] {
        		SearchHelper.KEY_WORD,
        		SearchHelper.KEY_DEFINITION};

        return mDictionary.getWord(rowId, columns);
      }

	
	private Cursor getSuggestions(String query) {
	      query = query.toLowerCase();
	      String[] columns = new String[] {
	          BaseColumns._ID,
	          SearchHelper.KEY_WORD,
	          SearchHelper.KEY_DEFINITION, 
	       /* SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
	                        (only if you want to refresh shortcuts) */
	          SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
	          SearchManager.SUGGEST_COLUMN_ICON_1,
	          SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};

	      return mDictionary.getWordMatches(query, columns);
	    }

	/**
     * This method is required in order to query the supported types.
     * It's also useful in our own query() method to determine the type of Uri received.
     */
    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case SEARCH_WORDS:
                return WORDS_MIME_TYPE;
            case GET_WORD:
                return DEFINITION_MIME_TYPE;
            case SEARCH_SUGGEST:
                return SearchManager.SUGGEST_MIME_TYPE;
            case REFRESH_SHORTCUT:
                return SearchManager.SHORTCUT_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }


	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
