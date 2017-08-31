package com.example.android.weatherdemo.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.android.weatherdemo.utils.Constants;
import com.example.android.weatherdemo.utils.WeatherDateUtils;

/**
 * Created by tomaki on 26/08/2017.
 */

/**
 * Defines properties of the weather database.
 */
public class WeatherContract {
    /**
     * Inner class, defines the content.
     */
    public static final class WeatherEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Constants.WEATHER_CONTRACT_BASE_CONTENT_URI.buildUpon()
                .appendPath(Constants.WEATHER_CONTRACT_PATH_WEATHER).build();
        public static final String TABLE_NAME = "weather";
        public static final String COL_DATE = "weather_date";
        public static final String COL_ID = "weather_id";
        public static final String COL_TEMP_MIN = "weather_min";
        public static final String COL_TEMP_MAX = "weather_max";
        public static final String COL_HUMIDITY = "weather_humidity";
        public static final String COL_PRESSURE = "weather_pressure";
        public static final String COL_WIND_SPEED = "weather_wind";
        public static final String COL_DEGREES = "weather_degrees";
        public static final String COL_DESC_SHORT = "weather_description_short";
        public static final String COL_DESC_LONG = "weather_description_long";
        public static final String COL_ICON = "weather_icon";

        /**
         * Gets weather data query from today.
         * @return Query for today and onwards.
         */
        public static String getSqlSelectForTodayOnwards() {
            long normalizedUtcNow = WeatherDateUtils.normalizeDate(System.currentTimeMillis());
            return WeatherContract.WeatherEntry.COL_DATE + " >= " + normalizedUtcNow;
        }

        /**
         * Builds a URI, which is used to query by date.
         * @param date Normalized date in milliseconds.
         * @return Uri to query a day.
         */
        public static Uri buildWeatherUriWithDate(long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(date))
                    .build();
        }
    }
}
