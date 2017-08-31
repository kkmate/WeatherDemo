package com.example.android.weatherdemo.utils;

/**
 * Created by tomaki on 23/08/2017.
 */

import android.content.Context;
import android.net.Uri;

import com.example.android.weatherdemo.BuildConfig;
import com.example.android.weatherdemo.data.WeatherPreferences;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Utilities for network communications.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /**
     * Creates the URL to communicate with OWM server.
     * @param locationQuery Location to get the weather data.
     * @return Url to use to query the weather server.
     */
    public static URL buildUrl(String locationQuery) {
        Uri builtUri = Uri.parse(Constants.FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(Constants.PARAM_QUERY, locationQuery)
                .appendQueryParameter(Constants.PARAM_FORMAT, Constants.RET_FORMAT)
                .appendQueryParameter(Constants.PARAM_UNITS, Constants.RET_UNITS)
                .appendQueryParameter(Constants.PARAM_DAYS, Integer.toString(Constants.RET_NUM_DAYS))
                .appendQueryParameter(Constants.PARAM_APPID, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Returns with the result of the HTTP response.
     * @param url URL to fetch the HTTP response.
     * @return Content of the response.
     * @throws IOException Network or stream exceptions.
     */
    public static String getHttpResponse(URL url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = con.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("//^");
            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            con.disconnect();
        }
    }

    /**
     * Return the URL with location to query the server.
     * @param context Context
     * @return URL to communicate with the server.
     */
    public static URL getUrl(Context context) {
        String locationQuery = WeatherPreferences.getPreferredWeatherLocation(context);
        return buildUrl(locationQuery);
    }
}
