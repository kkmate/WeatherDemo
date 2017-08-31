package com.example.android.weatherdemo.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.example.android.weatherdemo.data.WeatherContract;

import java.net.URL;

/**
 * Created by tomaki on 28/08/2017.
 */

/**
 * Fetches data from the OWM server asynchronously.
 */
public class FetchWeatherDataTask extends AsyncTask<Void, Void, Void> {

    Context mContext;
    /* Listener which implements WeatherDataTaskListener and to be notified when task completed. */
    WeatherDataTaskListener mListener;

    /**
     * Constructor.
     * @param c Context
     * @param listener Listener, which implements WeatherDataTaskListener interface.
     */
    public FetchWeatherDataTask(Context c, WeatherDataTaskListener listener){
        mContext = c;
        mListener = listener;
    }

    /**
     * Fetches the data in a background thread.
     * @param voids Parameters.
     * @return Result.
     */
    @Override
    protected Void doInBackground(Void... voids) {
        URL weatherRequestUrl = NetworkUtils.getUrl(mContext);

        try {
            String jsonWeatherResponse = NetworkUtils
                    .getHttpResponse(weatherRequestUrl);

            ContentValues[] weatherValues = OpenWeatherMapJsonUtils
                    .getWeatherContentValuesFromJson(mContext, jsonWeatherResponse);

            if (weatherValues != null && weatherValues.length != 0) {
                ContentResolver contentResolver = mContext.getContentResolver();
                contentResolver.delete(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        null,
                        null);

                contentResolver.bulkInsert(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        weatherValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Runs on the UI thread after doInBackground returned.
     * Notifies listener, that background work has been completed.
     * @param aVoid Parameters.
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        mListener.onDataLoaded();
    }

    /**
     * Interface to define communication.
     */
    public interface WeatherDataTaskListener{
        void onDataLoaded();
    }
}