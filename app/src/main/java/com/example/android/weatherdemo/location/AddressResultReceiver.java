package com.example.android.weatherdemo.location;

/**
 * Created by tomaki on 28/08/2017.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.example.android.weatherdemo.utils.Constants;

/**
 * Receiver for data, which was sent from FetchAddressIntentService.
 */
public class AddressResultReceiver extends ResultReceiver {
    AddressResultReceiverListener mListener;

    /**
     * Constructor.
     * @param handler Required by the super constructor.
     * @param listener Where the result will be delivered to.
     */
    AddressResultReceiver(Handler handler, AddressResultReceiverListener listener) {
        super(handler);
        mListener = listener;
    }

    /**
     *  Receives data sent from FetchAddressIntentService and notifies the listener.
     */
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        String s = resultData.getString(Constants.RESULT_DATA_KEY);
        if (mListener != null){
            if (resultCode == Constants.SUCCESS_RESULT) {
                mListener.onReceiveSuccess(resultData);
            }else{
                mListener.onReceiveFailed(s);
            }
        }
    }

    /**
     * Interface to communicate with the Listener object.
     */
    public interface AddressResultReceiverListener{
        void onReceiveSuccess(Bundle address);
        void onReceiveFailed(String errorMessage);
    }

}
