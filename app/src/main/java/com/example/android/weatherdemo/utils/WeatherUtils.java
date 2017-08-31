package com.example.android.weatherdemo.utils;

import android.content.Context;

import com.example.android.weatherdemo.R;
import com.example.android.weatherdemo.data.WeatherPreferences;

/**
 * Created by tomaki on 23/08/2017.
 */

public final class WeatherUtils {

    private static final String TAG = WeatherUtils.class.getSimpleName();

    /**
     * Converts from Celsius to Fahrenheit.
     */
    private static double celsiusToFahrenheit(double temperatureInCelsius) {
        double temperatureInFahrenheit = (temperatureInCelsius * 1.8) + 32;
        return temperatureInFahrenheit;
    }

    /**
     * Converts from Fahrenheit to Celsius.
     */
    private static double FahrenheitToCelsius(double temperatureInFahrenheit) {
        double temperatureInCelsius = (temperatureInFahrenheit - 32) / 1.8;
        return temperatureInCelsius;
    }

    /**
     * Converts from meter / sec to miles / hour.
     */
    private static double speedMetricToImperial(double speed) {
        return speed / 0.44704;
    }

    /**
     * Makes temperature conversation between Fahrenheit and Celsius if needed by user preference.
     */
    public static String formatTemperature(Context context, double temperature) {
        int temperatureFormatResourceId = R.string.format_temperature;

        if (!WeatherPreferences.isMetric(context)) {
            temperature = celsiusToFahrenheit(temperature);
            temperatureFormatResourceId = R.string.format_temperature;
        }

        return String.format(context.getString(temperatureFormatResourceId), temperature);
    }

    public static String formatTemperatureWithUnit(Context context, double temperature) {
        int temperatureFormatResourceId = R.string.format_temperature_celsius;

        if (!WeatherPreferences.isMetric(context)) {
            temperature = celsiusToFahrenheit(temperature);
            temperatureFormatResourceId = R.string.format_temperature_fahrenheit;
        }

        return String.format(context.getString(temperatureFormatResourceId), temperature);
    }

    /**
     * Formats the temperatures to be displayed as "MAX°C / MIN°C" or "MAX°F / MIN°F"
     */
    public static String formatHighLows(Context context, double max, double min) {
        long roundedHigh = Math.round(max);
        long roundedLow = Math.round(min);

        String formattedMax = formatTemperature(context, roundedHigh);
        String formattedMin = formatTemperature(context, roundedLow);

        return formattedMax + " / " + formattedMin;
    }

    /**
     * Makes conversation between meter / sec and miles / hour for displaying wind speed.
     */
    public static String formatWindSpeed(Context context, double speed) {
        int windspeedFormatResourceId = R.string.format_wind_speed_metric;

        if (!WeatherPreferences.isMetric(context)) {
            speed = speedMetricToImperial(speed);
            windspeedFormatResourceId = R.string.format_wind_speed_imperial;
        }

        return String.format(context.getString(windspeedFormatResourceId), speed);
    }

    /**
     * Returns the icon resource id, represents the weather, beased on the ID sent by the API
     */
    public static int getIconByWeatherId(Context c, String id){
        String iconName = Constants.WEATHER_ICON_PREFIX.concat(id);
        int resId = c.getResources().getIdentifier(iconName, "drawable", c.getPackageName());
        if(resId != 0){
            return resId;
        }
        return getDefaultWeatherIcon();
    }

    public static int getDefaultWeatherIcon(){
        return R.drawable.ic_weather_not_found;
    }
}
