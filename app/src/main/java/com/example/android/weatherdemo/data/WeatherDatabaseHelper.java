package com.example.android.weatherdemo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.weatherdemo.utils.Constants;

/**
 * Created by tomaki on 26/08/2017.
 */

/**
 * Helps managing the local database.
 */
public class WeatherDatabaseHelper extends SQLiteOpenHelper {

    /**
     * Constructor.
     * @param c Context.
     */
    public WeatherDatabaseHelper(Context c){
        super(c, Constants.WEATHER_DATABASE_NAME, null, Constants.WEATHER_DATABASE_VERSION);
    }

    /**
     * Callback, called when the database is created.
     * @param sqLiteDatabase Database
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + " (" +
                        WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WeatherContract.WeatherEntry.COL_DATE + " INTEGER NOT NULL, " +
                        WeatherContract.WeatherEntry.COL_ID + " INTEGER NOT NULL, " +
                        WeatherContract.WeatherEntry.COL_TEMP_MIN   + " REAL NOT NULL, " +
                        WeatherContract.WeatherEntry.COL_TEMP_MAX   + " REAL NOT NULL, " +
                        WeatherContract.WeatherEntry.COL_HUMIDITY   + " REAL NOT NULL, " +
                        WeatherContract.WeatherEntry.COL_PRESSURE   + " REAL NOT NULL, " +
                        WeatherContract.WeatherEntry.COL_WIND_SPEED + " REAL NOT NULL, " +
                        WeatherContract.WeatherEntry.COL_DEGREES    + " REAL NOT NULL, " +
                        WeatherContract.WeatherEntry.COL_DESC_SHORT + " TEXT NOT NULL, " +
                        WeatherContract.WeatherEntry.COL_DESC_LONG    + " TEXT, " +
                        WeatherContract.WeatherEntry.COL_ICON    + " TEXT, " +
                        " UNIQUE (" + WeatherContract.WeatherEntry.COL_DATE + ") ON CONFLICT REPLACE" +
                        ");"
        );
    }

    /**
     * Recreates the database.
     * @param sqLiteDatabase Database, which has to be upgraded.
     * @param i Old database version.
     * @param i1 New database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
