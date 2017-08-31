package com.example.android.weatherdemo.location;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.android.weatherdemo.R;
import com.example.android.weatherdemo.utils.Constants;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by tomaki on 28/08/2017.
 */

/**
 * Presenter which resposible for location updates and the delivery of this information
 * to the corresponding View.
 */
public class LocationPresenter implements MainContract.LocationPresenter,
        AddressResultReceiver.AddressResultReceiverListener{

    private static final String TAG = LocationPresenter.class.getSimpleName();
    /* View, which receives the updates from this presenter */
    MainContract.View mView;
    /* For utility methods */
    private Context mContext;
    /* Stores the location update */
    private Location mCurrentLocation;
    /* Stores request parameters for the Location Provider */
    private LocationRequest mLocationRequest;
    /* Used for checking settings and permissions */
    private LocationSettingsRequest mLocationSettingsRequest;
    /* Callback for location updates */
    private LocationCallback mLocationCallback;
    /* Provides access to Location Settings API */
    private SettingsClient mSettingsClient;
    /* Location Provider to receive location updates */
    private FusedLocationProviderClient mFusedLocationClient;
    /* To receive the delivered address information */
    private AddressResultReceiver mResultReceiver;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Initializes the presenter, sets it up in order to be able to get location updates.
     * @param c Context
     * @param view MainContract.View dislpays information as the presenter decided.
     */
    @Override
    public void init(Context c, MainContract.View view) {
        mView = view;
        mContext = c;
        mSettingsClient = LocationServices.getSettingsClient(c);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(c);
        mResultReceiver = new AddressResultReceiver(new Handler(), this);
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    /**
     * Callback, used when the user granted the requested permission.
     */
    @Override
    public void permissionGranted() {
        startLocationUpdates();
    }

    /**
     * Callback, used when the user denied the requested permission.
     */
    @Override
    public void permissionDenied() {
        mView.showMessage(mContext.getString(R.string.permission_denied_explanation));
    }

    /**
     * Callback for the location updates.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                stopLocationUpdates();

                if (!Geocoder.isPresent()) {
                    mView.showMessage(mContext.getString(R.string.no_geocoder));
                    return;
                }
                startIntentService();
            }
        };
    }

    /**
     * Creates and sets up a location request for getting the location of the user.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates and a location setting request, to check the necessary location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Tries to request location updates from the Location Provider. Runtime permission
     * must be granted.
     */
    private void startLocationUpdates(){
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");
                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Settings are not satisfied.  Ask for permission.");
                                ResolvableApiException rae = (ResolvableApiException) e;
                                mView.runtimePermissionRequest(rae);
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Log.e(TAG, "Missing permisison. Request it in runtime.");
                        }
                    }
                });
    }

    /**
     * Removes the listener from the Location Provider.
     */
    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private void startIntentService() {
        Intent intent = new Intent(mContext, FetchAddressIntentService.class);
        Intent i = new Intent();
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mCurrentLocation);
        mContext.startService(intent);
    }

    /**
     * Callback, which called by AddressResultReceiver,
     * when address was found to the requested location.
     * @param address Bundle contains the address information.
     *                City name at the moment.
     */
    @Override
    public void onReceiveSuccess(Bundle address) {
        String city = address.getString(Constants.RESULT_DATA_KEY);
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(mContext.getString(R.string.pref_location_key), city);
        editor.apply();
        mView.hideLoading();
        mView.showMessage("Location: " + city);
    }

    /**
     * Callback, which called by AddressResultReceiver in case of error during reverse geocoding.
     * @param errorMessage Description of error.
     */
    @Override
    public void onReceiveFailed(String errorMessage) {
        mView.showMessage(errorMessage);
    }
}
