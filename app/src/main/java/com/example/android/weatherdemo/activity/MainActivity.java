package com.example.android.weatherdemo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.weatherdemo.BuildConfig;
import com.example.android.weatherdemo.R;
import com.example.android.weatherdemo.data.WeatherAdapter;
import com.example.android.weatherdemo.data.WeatherContract;
import com.example.android.weatherdemo.data.WeatherPreferences;
import com.example.android.weatherdemo.location.LocationPresenter;
import com.example.android.weatherdemo.location.MainContract;
import com.example.android.weatherdemo.settings.SettingsActivity;
import com.example.android.weatherdemo.utils.Constants;
import com.example.android.weatherdemo.utils.FetchWeatherDataTask;
import com.google.android.gms.common.api.ResolvableApiException;

import static com.example.android.weatherdemo.utils.Constants.REQUEST_CHECK_SETTINGS;
import static com.example.android.weatherdemo.utils.Constants.REQUEST_PERMISSIONS_REQUEST_CODE;
import static com.example.android.weatherdemo.utils.Constants.WEATHER_LOADER_ID;


public class MainActivity extends AppCompatActivity implements
        WeatherAdapter.WeatherAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener,
        FetchWeatherDataTask.WeatherDataTaskListener,
        MainContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();

    /* Location Presenter instance*/
    private MainContract.LocationPresenter mLocationPresenter;
    /* RecyclerView to display weather data as a list */
    private RecyclerView mRecyclerView;
    /* Initial position of teh RecyclerView */
    private int mPosition = RecyclerView.NO_POSITION;
    /* Adapter, which helps populating the recyclerView */
    private WeatherAdapter mWeatherAdapter;
    /* Progress bar to indicate loading */
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Reference to the RecyclerView, to the TextView for displaying the weather data
         * and to the ProgressBar. */
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_weather_data);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* The WeatherAdapter manages the connection between the weather data and the
         * correcponding Views. */
        mWeatherAdapter = new WeatherAdapter(this, this);

        /* Setting the RecyclerView up with LayoutManager and WeatherAdapter. */
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mWeatherAdapter);

        mLocationPresenter = new LocationPresenter();
        mLocationPresenter.init(this.getApplicationContext(), this);

        showLoading();

        /* Connects the Activity into the loader lifecycle. */
        getSupportLoaderManager().initLoader(WEATHER_LOADER_ID, null, this);
        /* Fetch new weather data if it is not up-to-date */
        if (WeatherPreferences.isUpdateNeeded(getApplicationContext())){
            new FetchWeatherDataTask(this, this).execute();
        }

        /* Registering reference change listener */
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Shows dialog the get permission from the user.
     * The result is coming in onActivityResult().
     * @param rae ResolvableApiException
     */
    @Override
    public void runtimePermissionRequest(ResolvableApiException rae){
        try {
            rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the message in a Snackbar.
     * @param message Message to be shown.
     */
    @Override
    public void showMessage(String message){
        showSnackbar(message);
    }

    /**
     * Shows progress bar.
     */
    @Override
    public void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * Hides progress bar.
     */
    @Override
    public void hideLoading(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    /**
     * Callback for the result from requestion permissions.
     * @param requestCode Code of requested permission.
     * @param permissions Requested permission.
     * @param grantResults Result from the user. Either granted or denied.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "UI dismissed.");
                mLocationPresenter.permissionDenied();
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted, starting location updates");
                mLocationPresenter.permissionGranted();
            } else {
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    /**
     * Checks if all permissions are granted.
     * @return True, if permissions are granted, false otherwise.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests permission for accessing location.
     * Results will be delivered to onRequestPermissionsResult().
     */
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Log.i(TAG, "Showing permission rationale");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Sets weather data view visible and hide the error.
     */
    private void showWeatherDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Show message in a snackbar with action.
     * @param mainTextStringId String ID, text retrieved from resources.
     * @param actionStringId Action ID.
     * @param listener OnClickListener.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(R.id.main_container),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private void showSnackbar(final String text) {
        Snackbar.make(findViewById(R.id.main_container), text, Snackbar.LENGTH_LONG).show();
    }

    /**
     * OnDestroy lifecycle callback.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        /* Unregistering reference change listener */
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Handles click events happen on the displayed wheather data.
     */
    @Override
    public void onClick(long date) {
        Intent intent = new Intent(this, DailyActivty.class);
        Uri uriForDateClicked = WeatherContract.WeatherEntry.buildWeatherUriWithDate(date);
        intent.setData(uriForDateClicked);
        startActivity(intent);
    }

    /**
     * Inflates the specified menu to the app bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Handles menu item clicks.
     *  - Reloads weather data.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.menu_action_location:
                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                    mLocationPresenter.permissionGranted();
                }
                return true;
            case R.id.menu_action_refresh:
                showLoading();
                new FetchWeatherDataTask(this, this).execute();
                return true;
            case R.id.menu_action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when a shared preference is changed, added, or removed.
     * @param sharedPreferences SharedPreferences which was changed.
     * @param s Key of the changed preference.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equalsIgnoreCase(getString(R.string.pref_location_key))){
            new FetchWeatherDataTask(this, this).execute();
        }else if (s.equalsIgnoreCase(getString(R.string.pref_units_key)) ||
                s.equalsIgnoreCase(getString(R.string.pref_location_key))){
            getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
        }
    }

    /**
     * Callback, result of runtime permission request.
     * @param requestCode Code of the request.
     * @param resultCode Code of the result.
     * @param data Other data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        mLocationPresenter.permissionGranted();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mLocationPresenter.permissionDenied();
                        break;
                }
                break;
        }
    }

    /**
     * Callback, called when FetchWeatherDataTask completed.
     */
    @Override
    public void onDataLoaded() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        getSupportLoaderManager().initLoader(WEATHER_LOADER_ID, null, MainActivity.this);
        WeatherPreferences.setLastUpdateTime(getApplicationContext());
        showMessage("Weather data updated");
    }

    /**
     * Creates a Loader, which is used to fetch weather data from the database.
     * @param id Loader ID.
     * @param args Arguments Bundle.
     * @return New Loader instance.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case WEATHER_LOADER_ID:
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
                String sortOrder = WeatherContract.WeatherEntry.COL_DATE + " ASC";
                String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                return new CursorLoader(this,
                        forecastQueryUri,
                        Constants.MAIN_FORECAST_PROJECTION,
                        selection,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    /**
     * Called when loader has completed loading data.
     * @param loader Loader which finished.
     * @param data Cursor retrieved by the loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mWeatherAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION){
            mPosition = 0;
        }
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0){
            showWeatherDataView();
        }else{
            showSnackbar("Something went wrong, try again!");
        }
    }

    /**
     * Called when a loader is being reset.
     * @param loader Loader which is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mWeatherAdapter.swapCursor(null);
    }
}
