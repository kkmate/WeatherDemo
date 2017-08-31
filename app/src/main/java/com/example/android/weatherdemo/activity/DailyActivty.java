package com.example.android.weatherdemo.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.weatherdemo.R;
import com.example.android.weatherdemo.data.WeatherContract;
import com.example.android.weatherdemo.utils.Constants;
import com.example.android.weatherdemo.utils.WeatherDateUtils;
import com.example.android.weatherdemo.utils.WeatherUtils;

/**
 * Created by tomaki on 24/08/2017.
 */

/**
 * Activity for handling and displaying daily weather data, after the user clicked on an item
 * in the RecyclerView.
 */
public class DailyActivty extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = DailyActivty.class.getSimpleName();

    /* Stores the URI for the chosen day's details. */
    private Uri mUri;

    private String mDailyWeatherStr;
    /* Views on the layout to be populated */
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTemperatureView;
    private TextView mLowTemperatureView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;
    private ImageView mIconImageView;

    /**
     * Lifecycle callback, initializes, find the Views.
     * @param savedInstanceState Saved state Bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        Intent intent = getIntent();
        mUri = intent.getData();

        mDateView = (TextView) findViewById(R.id.date);
        mDescriptionView = (TextView) findViewById(R.id.weather_description);
        mHighTemperatureView = (TextView) findViewById(R.id.high_temperature);
        mLowTemperatureView = (TextView) findViewById(R.id.low_temperature);
        mHumidityView = (TextView) findViewById(R.id.humidity);
        mWindView = (TextView) findViewById(R.id.wind);
        mPressureView = (TextView) findViewById(R.id.pressure);
        mIconImageView = (ImageView) findViewById(R.id.weather_icon);

        /* Connects the Activity into the loader lifecycle. */
        getSupportLoaderManager().initLoader(Constants.DETAIL_LOADER_ID, null, this);
    }

    /**
     * Initializes and inflates the option menu.
     * @param menu Menu to display.
     * @return True, the menu to be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.daily_menu, menu);
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mDailyWeatherStr)
                .getIntent();
        menu.findItem(R.id.menu_action_share).setIntent(shareIntent);
        return true;
    }

    /**
     * Crates a CursorLoader to access the daily information about the selected day.
     * @param id Loader ID.
     * @param args Bundle arguments.
     * @return New loader to load the data.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case Constants.DETAIL_LOADER_ID:

                return new CursorLoader(this,
                        mUri,
                        Constants.WEATHER_DETAIL_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    /**
     * Callback, which called when the LoaderManager has completed loading.
     * Populate the Views with corresponding daily weather information.
     * @param loader CursorLoader.
     * @param data Corsor, which contains the data.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            return;
        }
        String iconIdStr = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COL_ICON));
        int iconResourceId = WeatherUtils.getIconByWeatherId(this, iconIdStr);
        String dateText = WeatherDateUtils.getReadableDateWithLocation(
                this,
                data.getLong(data.getColumnIndex(WeatherContract.WeatherEntry.COL_DATE)));
        String description =
                data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COL_DESC_LONG));
        float humidity =
                data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COL_HUMIDITY));
        String humidityString = getString(R.string.format_humidity, humidity);
        double tempMax =
                data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COL_TEMP_MAX));
        String tempMaxStr = WeatherUtils.formatTemperature(this, tempMax);
        double tempMin =
                data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COL_TEMP_MIN));
        String tempMinStr = WeatherUtils.formatTemperature(this, tempMin);
        double windSpeed =
                data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COL_WIND_SPEED));
        String winSpeedStr = WeatherUtils.formatWindSpeed(this, windSpeed);
        float pressure =
                data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COL_PRESSURE));
        String pressureString = getString(R.string.format_pressure, pressure);
        mDailyWeatherStr = String.format("%s - %s - %s/%s",
                dateText, description, tempMaxStr, tempMinStr);
        mDateView.setText(dateText);
        mDescriptionView.setText(description);
        mHighTemperatureView.setText(tempMaxStr);
        mLowTemperatureView.setText(tempMinStr);
        mHumidityView.setText(humidityString);
        mWindView.setText(winSpeedStr);
        mPressureView.setText(pressureString);
        mIconImageView.setImageResource(iconResourceId);
    }

    /**
     * Must be implemented. Not used.
     * @param loader Loader to reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
