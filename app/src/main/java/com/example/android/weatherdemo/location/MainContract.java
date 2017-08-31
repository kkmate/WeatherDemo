package com.example.android.weatherdemo.location;

import android.content.Context;

import com.google.android.gms.common.api.ResolvableApiException;

/**
 * Created by tomaki on 28/08/2017.
 */

/**
 * Public Interface as a contract between the Presenter and the View.
 * Defines the way of communication between them.
 */
public interface MainContract {

    /**
     * Represents a View in MVP.
     */
    interface View{
        void runtimePermissionRequest(ResolvableApiException rae);
        void showMessage(String message);
        void showLoading();
        void hideLoading();
    }

    /**
     * Represents the Presenter.
     */
    interface LocationPresenter{
        void init(Context c, MainContract.View view);
        void permissionGranted();
        void permissionDenied();
    }
}
