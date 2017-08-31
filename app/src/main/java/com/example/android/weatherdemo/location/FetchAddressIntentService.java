package com.example.android.weatherdemo.location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.weatherdemo.R;
import com.example.android.weatherdemo.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by tomaki on 28/08/2017.
 */

/**
 * Tries to fetch an address for the given location using a Geocoder.
 * Delivers the result to the ResultReceiver.
 */
public class FetchAddressIntentService extends IntentService{
    private static final String TAG = FetchAddressIntentService.class.getSimpleName();;

    /* Receiver where the result will be delivered to. */
    private ResultReceiver mReceiver;

    /**
     * Constructor
     */
    public FetchAddressIntentService() {
        super(TAG);
    }

    /**
     * Inverse geocoding. Tries to get an address to the corresponding location information which
     * has been sent via Intent Extra.
     * @param intent Intent which contains the location information.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        if (mReceiver == null) {
            Log.d(TAG, "No receiver.");
            return;
        }

        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

        if (location == null) {
            errorMessage = getString(R.string.no_location_data_provided);
            Log.e(TAG, errorMessage);
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {
            /* Network or I/O problems.*/
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            /* Invalid lat or lon values.*/
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
        }

        /* If no address was found. */
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        /* Return the city name (getLocality) of the first result. */
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();
            addressFragments.add(address.getLocality());
            Log.i(TAG, getString(R.string.address_found));
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"), addressFragments));
        }
    }

    /**
     * Sends a resultCode and message to the receiver.
     */
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

}
