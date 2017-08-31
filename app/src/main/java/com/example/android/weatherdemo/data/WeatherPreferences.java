package com.example.android.weatherdemo.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.android.weatherdemo.R;
import com.example.android.weatherdemo.utils.Constants;
import com.example.android.weatherdemo.utils.WeatherDateUtils;

/**
 * Created by tomaki on 22/08/2017.
 */

/**
 * Helper functions.
 */
public class WeatherPreferences {
    /**
     * Returns true if the user has selected metric temperature display.
     */
    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyUnit = context.getString(R.string.pref_units_key);
        String defaultMetric = context.getString(R.string.pref_units_metric);
        String preferred = prefs.getString(keyUnit,defaultMetric);
        return preferred.equalsIgnoreCase(defaultMetric);
    }

    /**
     * Returns the location set in SharedPreferences.
     * @param context Context to access DefaultSharedPreferences.
     * @return Preferred location.
     */
    public static String getPreferredWeatherLocation(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyLocation = context.getString(R.string.pref_location_key);
        String defaultLocation = context.getString(R.string.pref_location_default);
        return prefs.getString(keyLocation, defaultLocation);
    }

    /**
     * Sets the preferred location in SharedPreferences.
     * @param context Context.
     * @param lat Latitude.
     * @param lon Longitude.
     */
    public static void setPreferredWeatherLocation(Context context, double lat, double lon) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putLong(Constants.PREF_COORD_LAT, Double.doubleToRawLongBits(lat));
        editor.putLong(Constants.PREF_COORD_LON, Double.doubleToRawLongBits(lon));
        editor.apply();
    }

    /**
     * Sets last update time.
     * @param context Context.
     */
    public static void setLastUpdateTime(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(Constants.PREF_LAST_UPDATED, WeatherDateUtils.getNormalizedUtcDateForToday());
        editor.apply();
    }

    /**
     * Decides whether update needed or not.
     * @param context Context.
     * @return True if update has to be performed, false otherwise.
     */
    public static boolean isUpdateNeeded(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Long lastUpdate = prefs.getLong(Constants.PREF_LAST_UPDATED, -1);
        Long today = WeatherDateUtils.getNormalizedUtcDateForToday();
        Integer res = lastUpdate.compareTo(today);
        return res == 0 ? false : true;
    }
}
