package com.schlaf.steam.activities.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.schlaf.steam.R;
 
 
public class SearchHelper {
    public static final String COLUMN_NAME = "name";
 
    private static final String TAG = "SearchHelper";
    //private DatabaseHelper mDbHelper;
    // private SQLiteDatabase mDb;
 
    private static final String DATABASE_NAME = "Data2";
    private static final String FTS_VIRTUAL_TABLE = "Info";
    private static final int DATABASE_VERSION = 16;
    
    private final Context context;
 
	public static final String INTENT_SPELL ="schlaf.intent.action.SPELL";
	public static final String INTENT_CARD ="schlaf.intent.action.CARD";
	public static final String INTENT_RULE ="schlaf.intent.action.RULE";
	public static final String INTENT_CAPACITY ="schlaf.intent.action.CAPACITY";

	private static final String SPELL_ICON = "android.resource://com.schlaf.steam/drawable/action_spell";
	private static final String CARD_ICON = "android.resource://com.schlaf.steam/drawable/ic_menu_cc";
	private static final String CAPACITY_ICON = "android.resource://com.schlaf.steam/drawable/ic_menu_attachment";
    
    
    
    
    //The columns we'll include in the dictionary table
    public static final String KEY_ID = "whac_id";
    public static final String KEY_WORD = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String KEY_DEFINITION = SearchManager.SUGGEST_COLUMN_TEXT_2;
    public static final String KEY_INTENT = SearchManager.SUGGEST_COLUMN_INTENT_ACTION;
    public static final String KEY_ICON = SearchManager.SUGGEST_COLUMN_ICON_1;

    private final DictionaryOpenHelper mDatabaseOpenHelper;
    private static final HashMap<String,String> mColumnMap = buildColumnMap();

    /**
     * Constructor
     * @param context The Context within which to work, used to create the DB
     */
    public SearchHelper(Context context) {
    	this.context = context;
        mDatabaseOpenHelper = new DictionaryOpenHelper(context);
    }

    /**
     * Builds a map for all columns that may be requested, which will be given to the 
     * SQLiteQueryBuilder. This is a good way to define aliases for column names, but must include 
     * all columns, even if the value is the key. This allows the ContentProvider to request
     * columns w/o the need to know real column names and create the alias itself.
     */
    private static HashMap<String,String> buildColumnMap() {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put(KEY_ID, KEY_ID);
        map.put(KEY_WORD, KEY_WORD);
        map.put(KEY_DEFINITION, KEY_DEFINITION);
        map.put(KEY_INTENT, KEY_INTENT);
        map.put(KEY_ICON, KEY_ICON);
        map.put(BaseColumns._ID, "rowid AS " +
                BaseColumns._ID);
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_INTENT_DATA); 
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        return map;
    }

    /**
     * Returns a Cursor positioned at the word specified by rowId
     *
     * @param rowId id of word to retrieve
     * @param columns The columns to include, if null then all are included
     * @return Cursor positioned to matching word, or null if not found.
     */
    public Cursor getWord(String rowId, String[] columns) {
        String selection = "rowid = ?";
        String[] selectionArgs = new String[] {rowId};

        return query(selection, selectionArgs, columns);

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE rowid = <rowId>
         */
    }

    /**
     * Returns a Cursor over all words that match the given query
     *
     * @param query The string to search for
     * @param columns The columns to include, if null then all are included
     * @return Cursor over all words that match, or null if none found.
     */
    public Cursor getWordMatches(String query, String[] columns) {
        String selection = KEY_WORD + " MATCH ?";
        String[] selectionArgs = new String[] {query+"*"};

        return query(selection, selectionArgs, columns);

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE <KEY_WORD> MATCH 'query*'
         * which is an FTS3 search for the query text (plus a wildcard) inside the word column.
         *
         * - "rowid" is the unique id for all rows but we need this value for the "_id" column in
         *    order for the Adapters to work, so the columns need to make "_id" an alias for "rowid"
         * - "rowid" also needs to be used by the SUGGEST_COLUMN_INTENT_DATA alias in order
         *   for suggestions to carry the proper intent data.
         *   These aliases are defined in the DictionaryProvider when queries are made.
         * - This can be revised to also search the definition text with FTS3 by changing
         *   the selection clause to use FTS_VIRTUAL_TABLE instead of KEY_WORD (to search across
         *   the entire table, but sorting the relevance could be difficult.
         */
    }

    /**
     * Performs a database query.
     * @param selection The selection clause
     * @param selectionArgs Selection arguments for "?" components in the selection
     * @param columns The columns to return
     * @return A Cursor over all rows matching the query
     */
    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        /* The SQLiteBuilder provides a map for all possible columns requested to
         * actual columns in the database, creating a simple column alias mechanism
         * by which the ContentProvider does not need to know the real column names
         */
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);
        builder.setProjectionMap(mColumnMap);

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }


    /**
     * This creates/opens the database.
     */
    private static class DictionaryOpenHelper extends SQLiteOpenHelper {

        private final Context mHelperContext;
        private SQLiteDatabase mDatabase;

        /* Note that FTS3 does not support column constraints and thus, you cannot
         * declare a primary key. However, "rowid" is automatically used as a unique
         * identifier, so when making requests, we will use "_id" as an alias for "rowid"
         */
        private static final String FTS_TABLE_CREATE =
                    "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
                    " USING fts3 (" +
                    KEY_ID + ", " +
                    KEY_WORD + ", " +
                    KEY_DEFINITION + "," +
                    KEY_INTENT + "," +
                    KEY_ICON + ");";

        DictionaryOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mHelperContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mDatabase = db;
            mDatabase.execSQL(FTS_TABLE_CREATE);
            loadDictionary();
        }

        /**
         * Starts a thread to load the database table with words
         */
        private void loadDictionary() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        loadSpells();
                        loadCards();
                        loadCapacities();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

        private void loadSpells() throws IOException {
            Log.d(TAG, "Loading spells...");
            final Resources resources = mHelperContext.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.spells);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] strings = TextUtils.split(line, ";");
                    if (strings.length < 3) continue; // ignore line if not full
                    long id = addWord(strings[0].trim(), strings[1].trim(),strings[2].trim(), INTENT_SPELL, SPELL_ICON);
                    if (id < 0) {
                        Log.e(TAG, "unable to add word: " + strings[0].trim());
                    }
                }
            } finally {
                reader.close();
            }
            Log.d(TAG, "DONE loading words.");
        }
        
        private void loadCards() throws IOException {
            Log.d(TAG, "Loading cards...");
            final Resources resources = mHelperContext.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.models);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] strings = TextUtils.split(line, ";");
                    if (strings.length < 3) continue;
                    long id = addWord(strings[0].trim(), strings[1].trim(),strings[2].trim(), INTENT_CARD, CARD_ICON);
                    if (id < 0) {
                        Log.e(TAG, "unable to add model: " + strings[0].trim());
                    }
                }
            } finally {
                reader.close();
            }
            Log.d(TAG, "DONE loading words.");
        }
        
        private void loadCapacities() throws IOException {
            Log.d(TAG, "Loading capacities...");
            final Resources resources = mHelperContext.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.capacities);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] strings = TextUtils.split(line, ";");
                    if (strings.length < 3) continue;
                    long id = addWord(strings[0].trim(), strings[1].trim(),strings[2].trim(), INTENT_CAPACITY, CAPACITY_ICON);
                    if (id < 0) {
                        Log.e(TAG, "unable to add capacity: " + strings[0].trim());
                    }
                }
            } finally {
                reader.close();
            }
            Log.d(TAG, "DONE loading words.");
        }
        

        /**
         * Add a word to the dictionary.
         * @return rowId or -1 if failed
         */
        public long addWord(String id, String word, String definition, String intent, String icon) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_ID, id);
            initialValues.put(KEY_WORD, word);
            initialValues.put(KEY_DEFINITION, definition);
            initialValues.put(KEY_INTENT, intent);
            initialValues.put(KEY_ICON, icon);

            return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            onCreate(db);
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
//    private static class DatabaseHelper extends SQLiteOpenHelper {
// 
//        DatabaseHelper(Context context) {
//            super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        }
// 
// 
//        @Override
//        public void onCreate(SQLiteDatabase db) {
//            Log.w(TAG, DATABASE_CREATE);
//            db.execSQL(DATABASE_CREATE);
//            db.execSQL(DATABASE_CREATE_SUGGESTIONS);
//            
//            initSuggestionsBase(db);
//        }
// 
//        @Override
//        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
//                    + newVersion + ", which will destroy all old data");
//            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_SUGGESTIONS);
//            onCreate(db);
//        }
//        
//        public void initSuggestionsBase(SQLiteDatabase db) {
//    		
//        	// db.beginTransaction();
//    		String sql = "Insert or Replace into " + FTS_VIRTUAL_SUGGESTIONS +  "(" + SearchManager.SUGGEST_COLUMN_TEXT_1 + ") values(?)";
//            SQLiteStatement insert = db.compileStatement(sql);
//            String[] entries = new String[] {"Abomination","Ananas","Abricot","Bergeron","Beauce","Battlegroup","Coucou","Chaingun"};
//            for(int i=0;i<entries.length;i++){
//                insert.bindString(1, entries[i]);
//                insert.execute();
//            }
//            // db.setTransactionSuccessful();
//    	}
//    }
 
//    public SearchHelper open() throws SQLException {
//        mDbHelper = new DatabaseHelper(context);
//        mDb = mDbHelper.getWritableDatabase();
//        return this;
//    }
// 
//    public void close() {
//        if (mDbHelper != null) {
//            mDbHelper.close();
//        }
//    }
// 
// 
//    public long createList(String name) {
// 
//        ContentValues initialValues = new ContentValues();
// 
// 
//        initialValues.put(COLUMN_NAME, name);
// 
//        return mDb.insert(FTS_VIRTUAL_TABLE, null, initialValues);
// 
//    }
// 
//    public Cursor searchSuggestion(String inputText) throws SQLException {
//    	 
//    	String input = inputText + "*";
//    	
//        String query = "SELECT docid as _id," +
//        		SearchManager.SUGGEST_COLUMN_TEXT_1 +  " from " + FTS_VIRTUAL_SUGGESTIONS +
//                " where " + SearchManager.SUGGEST_COLUMN_TEXT_1 + " MATCH '" + input + "';";
// 
//        Cursor mCursor = mDb.rawQuery(query,null);
// 
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;    	
// 
//    }
// 
//    public Cursor searchByInputText(String inputText) throws SQLException {
// 
//        String query = "SELECT docid as _id," +
//                COLUMN_NAME +  " from " + FTS_VIRTUAL_TABLE +
//                " where " + COLUMN_NAME + " MATCH '" + inputText + "';";
// 
//        Cursor mCursor = mDb.rawQuery(query,null);
// 
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
// 
//    }
// 
// 
//    public boolean deleteAllNames() {
// 
//        int doneDelete = mDb.delete(FTS_VIRTUAL_TABLE, null , null);
//        return doneDelete > 0;
// 
//    }

	
}