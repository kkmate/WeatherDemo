package com.example.android.weatherdemo.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.weatherdemo.utils.Constants;
import com.example.android.weatherdemo.utils.WeatherDateUtils;

/**
 * Created by tomaki on 26/08/2017.
 */

/**
 * ContentProvider, which allows to insert or query data.
 * Only the necessary methods have been implemented.
 */
public class WeatherContentProvider extends ContentProvider {

    /* To mach URIs */
    private static final int CODE_WEATHER = 111;
    private static final int CODE_WEATHER_WITH_DATE = 112;
    /* URI matcher to check requests. */
    private static final UriMatcher mUriMatcher = buildUriMatcher();
    /* Database helper */
    private WeatherDatabaseHelper mDatabaseHelper;

    /**
     * Creates UriMatcher which checks requested URIs.
     * @return UriMatcher which matches the URIs.
     */
    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Constants.WEATHER_CONTRACT_AUTHORITY;
        matcher.addURI(authority, Constants.WEATHER_CONTRACT_PATH_WEATHER, CODE_WEATHER);
        matcher.addURI(authority, Constants.WEATHER_CONTRACT_PATH_WEATHER + "/#", CODE_WEATHER_WITH_DATE);
        return matcher;
    }

    /**
     * Lifecycle callback, called during startup, initializes the DatabaseHelper.
     * @return True, if provider was loaded, false otherwise.
     */
    @Override
    public boolean onCreate() {
        mDatabaseHelper = new WeatherDatabaseHelper(getContext());
        return true;
    }

    /**
     * Handles query requests.
     * @param uri URI to the query.
     * @param strings List of columns to be included in the cursor. Null means all columns are
     *                included.
     * @param s Selection criteria, filtering rows. Null means all rows are included.
     * @param strings1 Selection arguments.
     * @param s1 Order of the rows in the cursor.
     * @return Cursor contains the result of the query.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor cursor;
        switch (mUriMatcher.match(uri)) {
            case CODE_WEATHER_WITH_DATE: {
                String normalizedUtcDateString = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{normalizedUtcDateString};

                cursor = mDatabaseHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        strings,
                        WeatherContract.WeatherEntry.COL_DATE + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        s1);

                break;
            }
            case CODE_WEATHER: {
                cursor = mDatabaseHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        strings,
                        s,
                        strings1,
                        null,
                        null,
                        s1);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Must be overridden, not used.
     * @param uri Query.
     * @return Type.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Inserts a set of rows.
     * @param uri URI of the request.
     * @param values Column name - value pairs to be added to the database.
     * @return Number of the values were inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        switch (mUriMatcher.match(uri)) {
            case CODE_WEATHER:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long weatherDate =
                                value.getAsLong(WeatherContract.WeatherEntry.COL_DATE);
                        if (!WeatherDateUtils.isDateNormalized(weatherDate)) {
                            throw new IllegalArgumentException("Date must be normalized to insert");
                        }

                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    /**
     * Must be overridden. Not used.
     * @param uri Request URI.
     * @param contentValues Values to be added.
     * @return URI for the newly inserted items.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    /**
     * Deletes one or more rows.
     * @param uri Request URi.
     * @param s Selection.
     * @param strings Selection arguments.
     * @return Number of rows affected.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int numRowsDeleted;
        if (null == s) s = "1";

        switch (mUriMatcher.match(uri)) {
            case CODE_WEATHER:
                numRowsDeleted = mDatabaseHelper.getWritableDatabase().delete(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        s,
                        strings);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;
    }

    /**
     * Must be overridden. Not used.
     * @param uri Request URI.
     * @param contentValues Values to be added.
     * @param s Selection.
     * @param strings Selection arguments.
     * @return Number of rows affected.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
