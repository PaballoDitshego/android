package za.co.moxomo.contentproviders;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.io.IOException;
import java.util.HashMap;

import za.co.moxomo.databasehelpers.SearchSuggestionDatabaseHelper;


public class SearchSuggestionProvider extends ContentProvider {

    private static final String PROVIDER_NAME = "za.co.moxomo.contentproviders.SEARCH";
    private static final String URL = "content://" + PROVIDER_NAME + "/suggestions";
    private static final String SUGGESTIONS_TABLE_NAME = "search_strings";
    private static final Uri CONTENT_URI = Uri.parse(URL);
    private static final int SUGGESTIONS = 10;
    private static final int SUGGESTIONS_ID = 20;
    private static final HashMap<String, String> SUGGESTIONS_PROJECTION_MAP = new HashMap<String, String>();

    static {

        SUGGESTIONS_PROJECTION_MAP.put("_id", "_id");
        SUGGESTIONS_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_1, "value AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        SUGGESTIONS_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA, "value AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA);
    }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {

        SearchSuggestionDatabaseHelper dbHelper = new SearchSuggestionDatabaseHelper(getContext());

        try {
            dbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            dbHelper.openDataBase();

        } catch (SQLException sqle) {

            throw sqle;

        }

        db = dbHelper.getReadableDatabase();

        return (db == null) ? false : true;

    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String query = uri.getLastPathSegment();
        qb.setTables(SUGGESTIONS_TABLE_NAME);
        qb.appendWhere("value LIKE '" + query + "%'");
        qb.setProjectionMap(SUGGESTIONS_PROJECTION_MAP);

        return qb.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
