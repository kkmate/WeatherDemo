package com.example.android.weatherdemo.utils;

import android.net.Uri;

import com.example.android.weatherdemo.data.WeatherContract;

/**
 * Created by tomaki on 26/08/2017.
 */

public final class Constants {

    /* OWM API */
    public static final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    public static final String RET_FORMAT = "json";
    public static final String RET_UNITS = "metric";
    public static final int RET_NUM_DAYS = 10;
    public static final String PARAM_QUERY = "q";
    public static final String PARAM_FORMAT = "mode";
    public static final String PARAM_UNITS = "units";
    public static final String PARAM_DAYS = "cnt";
    public static final String PARAM_APPID = "appid";

    /* OWM JSON keys*/
    public static final String OWM_KEY_CITY = "city";
    public static final String OWM_KEY_COORD = "coord";
    public static final String OWM_KEY_COORD_LAT = "lat";
    public static final String OWM_KEY_COORD_LON = "lon";
    public static final String OWM_KEY_LIST = "list";
    public static final String OWM_KEY_PRESSURE = "pressure";
    public static final String OWM_KEY_HUMIDITY = "humidity";
    public static final String OWM_KEY_WINDSPEED = "speed";
    public static final String OWM_KEY_WIND_DIRECTION = "deg";
    public static final String OWM_KEY_TEMPERATURE = "temp";
    public static final String OWM_KEY_TEMP_MAX = "max";
    public static final String OWM_KEY_TEMP_MIN = "min";
    public static final String OWM_KEY_WEATHER = "weather";
    public static final String OWM_KEY_DESCRIPTION_SHORT = "main";
    public static final String OWM_KEY_DESCRIPTION_LONG = "description";
    public static final String OWM_KEY_ID = "id";
    public static final String OWM_KEY_RESPONSE_CODE = "cod";
    public static final String OWM_KEY_ICON = "icon";

    /* For date conversations */
    public static final long SECOND_IN_MILLIS = 1000;
    public static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;

    /* Database name */
    public static final String WEATHER_DATABASE_NAME = "weather.db";
    /* Database version */
    public static final int WEATHER_DATABASE_VERSION = 7;
    /* Name for the Content Provider */
    public static final String WEATHER_CONTRACT_AUTHORITY = "com.example.android.weatherdemo";
    /* Base of all URIs which will be used */
    public static final Uri WEATHER_CONTRACT_BASE_CONTENT_URI = Uri.parse("content://" + WEATHER_CONTRACT_AUTHORITY);
    /* App specific specific path segment */
    public static final String WEATHER_CONTRACT_PATH_WEATHER = "weather";

    /* Preferences */
    public static final String PREF_COORD_LAT = "coord_lat";
    public static final String PREF_COORD_LON = "coord_lon";
    public static final String PREF_LAST_UPDATED = "pref_last_updated";

    /* Projection for weather list data */
    public static final String[] MAIN_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COL_DATE,
            WeatherContract.WeatherEntry.COL_TEMP_MAX,
            WeatherContract.WeatherEntry.COL_TEMP_MIN,
            WeatherContract.WeatherEntry.COL_ID,
            WeatherContract.WeatherEntry.COL_DESC_SHORT,
            WeatherContract.WeatherEntry.COL_ICON
    };

    /* Projection for daily data */
    public static final String[] WEATHER_DETAIL_PROJECTION = {
            WeatherContract.WeatherEntry.COL_DATE,
            WeatherContract.WeatherEntry.COL_TEMP_MAX,
            WeatherContract.WeatherEntry.COL_TEMP_MIN,
            WeatherContract.WeatherEntry.COL_HUMIDITY,
            WeatherContract.WeatherEntry.COL_PRESSURE,
            WeatherContract.WeatherEntry.COL_WIND_SPEED,
            WeatherContract.WeatherEntry.COL_DEGREES,
            WeatherContract.WeatherEntry.COL_ID,
            WeatherContract.WeatherEntry.COL_DESC_LONG,
            WeatherContract.WeatherEntry.COL_ICON
    };

    public static final String WEATHER_ICON_PREFIX = "ic_weather_";
    public static final String SEPARATOR_COMMA = ", ";

    /* Permission settings codes */
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    /* Result codes */
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    /* ResultReceiver */
    private static final String PACKAGE_NAME =
            "com.example.android.weatherdemo";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    /* Loader IDs */
    public static final int DETAIL_LOADER_ID = 10;
    public static final int WEATHER_LOADER_ID = 0;
}
