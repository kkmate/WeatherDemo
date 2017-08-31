package com.example.android.weatherdemo.utils;

/**
 * Created by tomaki on 23/08/2017.
 */

import android.content.ContentValues;
import android.content.Context;

import com.example.android.weatherdemo.data.WeatherContract;
import com.example.android.weatherdemo.data.WeatherPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Utilities to handle OpenWeatherMap JSON data.
 */
public final class OpenWeatherMapJsonUtils {

    private static final String TAG = OpenWeatherMapJsonUtils.class.getSimpleName();

    /**
     * Parses JSON response into ContentValues.
     */
    public static ContentValues[] getWeatherContentValuesFromJson(Context context, String forecastJsonStr)
            throws JSONException {

        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        if (forecastJson.has(Constants.OWM_KEY_RESPONSE_CODE)) {
            int errorCode = forecastJson.getInt(Constants.OWM_KEY_RESPONSE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray jsonWeatherArray = forecastJson.getJSONArray(Constants.OWM_KEY_LIST);

        JSONObject cityJson = forecastJson.getJSONObject(Constants.OWM_KEY_CITY);

        JSONObject cityCoord = cityJson.getJSONObject(Constants.OWM_KEY_COORD);
        double cityLatitude = cityCoord.getDouble(Constants.OWM_KEY_COORD_LAT);
        double cityLongitude = cityCoord.getDouble(Constants.OWM_KEY_COORD_LON);

        WeatherPreferences.setPreferredWeatherLocation(context, cityLatitude, cityLongitude);
        ContentValues[] weatherContentValues = new ContentValues[jsonWeatherArray.length()];
        long normalizedUtcStartDay = WeatherDateUtils.getNormalizedUtcDateForToday();

        for (int i = 0; i < jsonWeatherArray.length(); i++) {
            long dateTimeMillis;
            int humidity, weatherId;
            double pressure, windSpeed, windDirection;
            String descShort, descLong, iconStr;
            double max, min;

            /* JSON object representing a day */
            JSONObject dayForecast = jsonWeatherArray.getJSONObject(i);
            /* Get interesting information */
            dateTimeMillis = normalizedUtcStartDay + Constants.DAY_IN_MILLIS * i;
            descShort = dayForecast.getJSONArray(Constants.OWM_KEY_WEATHER).getJSONObject(0)
                    .getString(Constants.OWM_KEY_DESCRIPTION_SHORT);
            descLong = dayForecast.getJSONArray(Constants.OWM_KEY_WEATHER).getJSONObject(0)
                    .getString(Constants.OWM_KEY_DESCRIPTION_LONG);
            iconStr = dayForecast.getJSONArray(Constants.OWM_KEY_WEATHER).getJSONObject(0)
                    .getString(Constants.OWM_KEY_ICON);
            pressure = dayForecast.getDouble(Constants.OWM_KEY_PRESSURE);
            humidity = dayForecast.getInt(Constants.OWM_KEY_HUMIDITY);
            windSpeed = dayForecast.getDouble(Constants.OWM_KEY_WINDSPEED);
            windDirection = dayForecast.getDouble(Constants.OWM_KEY_WIND_DIRECTION);

            /* Gets description. */
            JSONObject weatherObject =
                    dayForecast.getJSONArray(Constants.OWM_KEY_WEATHER).getJSONObject(0);
            weatherId = weatherObject.getInt(Constants.OWM_KEY_ID);

            JSONObject temperatureObject = dayForecast.getJSONObject(Constants.OWM_KEY_TEMPERATURE);
            max = temperatureObject.getDouble(Constants.OWM_KEY_TEMP_MAX);
            min = temperatureObject.getDouble(Constants.OWM_KEY_TEMP_MIN);

            /* Prepare contentValues */
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherContract.WeatherEntry.COL_DATE, dateTimeMillis);
            weatherValues.put(WeatherContract.WeatherEntry.COL_HUMIDITY, humidity);
            weatherValues.put(WeatherContract.WeatherEntry.COL_PRESSURE, pressure);
            weatherValues.put(WeatherContract.WeatherEntry.COL_WIND_SPEED, windSpeed);
            weatherValues.put(WeatherContract.WeatherEntry.COL_DEGREES, windDirection);
            weatherValues.put(WeatherContract.WeatherEntry.COL_TEMP_MAX, max);
            weatherValues.put(WeatherContract.WeatherEntry.COL_TEMP_MIN, min);
            weatherValues.put(WeatherContract.WeatherEntry.COL_ID, weatherId);
            weatherValues.put(WeatherContract.WeatherEntry.COL_DESC_SHORT, descShort);
            weatherValues.put(WeatherContract.WeatherEntry.COL_DESC_LONG, capitalizeFirstLetter(descLong));
            weatherValues.put(WeatherContract.WeatherEntry.COL_ICON, iconStr);

            weatherContentValues[i] = weatherValues;
        }

        return weatherContentValues;
    }

    /**
     * Helper function to capitalize first character of text.
     * @param s String to be capitalized.
     * @return Capitalizes String.
     */
    private static String capitalizeFirstLetter(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
