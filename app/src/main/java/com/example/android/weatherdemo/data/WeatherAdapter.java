package com.example.android.weatherdemo.data;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.weatherdemo.R;
import com.example.android.weatherdemo.utils.WeatherDateUtils;
import com.example.android.weatherdemo.utils.WeatherUtils;

/**
 * Created by tomaki on 23/08/2017.
 */

/**
 * Works as a bridge between the weather data and the RecycleView.
 */
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherAdapterViewHolder>{
    /* Populates the list differently if it is today data */
    private static final int VIEW_LIST_ITEM_TODAY = 0;
    private static final int VIEW_LIST_ITEM_OTHER_DAYS = 1;
    /* Data set from Cursor*/
    private Cursor mCursor;
    /* Context to accessing eg. utilities.*/
    private Context mContext;
    /* Flag, which helps deciding it is today or not */
    private boolean mIsToday;
    /* Listener for adapter item clicks. */
    private final WeatherAdapterOnClickHandler mOnClickListener;

    /**
     * Constructor, creates a WeatherAdapter.
     * @param c Context to access utilities.
     * @param onClickHandler On click listener, called when user has selected a list item.
     */
    public WeatherAdapter(Context c, WeatherAdapterOnClickHandler onClickHandler){
        mContext = c;
        mOnClickListener = onClickHandler;
        mIsToday = c.getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * Called when a ViewHolder is created.
     * @param parent ViewGroup which is the container View of this ViewHolder.
     * @param viewType To distinguish today or other days views.
     * @return WeatherAdapterViewHolder which contains the View for the list item.
     */
    @Override
    public WeatherAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int idToInflate;
        switch (viewType) {
            case VIEW_LIST_ITEM_TODAY: {
                idToInflate = R.layout.weather_list_item_today;
                break;
            }
            case VIEW_LIST_ITEM_OTHER_DAYS: {
                idToInflate = R.layout.weather_list_item;
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid view type: " + viewType);
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(idToInflate, parent, false);
        v.setFocusable(true);
        
        return new WeatherAdapterViewHolder(v);
    }

    /**
     * Called when ViewHolder is about to be displayed in the adapter.
     * @param holder ViewHolder which is going to be displayed.
     * @param position Position of the item in tha adapter's data.
     */
    @Override
    public void onBindViewHolder(WeatherAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String iconIdStr = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherEntry.COL_ICON));
        int iconResourceId = WeatherUtils.getIconByWeatherId(mContext, iconIdStr);
        long dateInMillis =
                mCursor.getLong(mCursor.getColumnIndex(WeatherContract.WeatherEntry.COL_DATE));
        String dateString = WeatherDateUtils.getReadableDate(mContext, dateInMillis);
        if (position == 0) {
            dateString = WeatherDateUtils.getReadableDateWithLocation(mContext,dateInMillis);
        }
        double tempMax =
                mCursor.getDouble(mCursor.getColumnIndex(WeatherContract.WeatherEntry.COL_TEMP_MAX));
        String tempMaxStr = WeatherUtils.formatTemperature(mContext, tempMax);
        double tempMin =
                mCursor.getDouble(mCursor.getColumnIndex(WeatherContract.WeatherEntry.COL_TEMP_MIN));
        String tempMinStr = WeatherUtils.formatTemperature(mContext, tempMin);
        String description =
                mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherEntry.COL_DESC_SHORT));
        holder.iconImageView.setImageResource(iconResourceId);
        holder.dateTextView.setText(dateString);
        holder.descriptionTextView.setText(description);
        holder.tempMaxTextView.setText(tempMaxStr);
        holder.tempMinTextView.setText(tempMinStr);
    }

    /**
     * Gets the number of items in the data set.
     * @return Number of weather data in the Cursor.
     */
    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    /**
     * Gets the type code of the ViewType of the current item.
     * @param position Position in the RecyclerView.
     * @return Either today or other days.
     */
    @Override
    public int getItemViewType(int position) {
        if (mIsToday && position == 0) {
            return VIEW_LIST_ITEM_TODAY;
        } else {
            return VIEW_LIST_ITEM_OTHER_DAYS;
        }
    }

    /**
     * Helper class, used to cache the views for the weather data.
     */
    public class WeatherAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        final ImageView iconImageView;
        final TextView dateTextView;
        final TextView descriptionTextView;
        final TextView tempMaxTextView;
        final TextView tempMinTextView;

        WeatherAdapterViewHolder(View v){
            super(v);
            iconImageView = (ImageView) v.findViewById(R.id.weather_icon);
            dateTextView = (TextView) v.findViewById(R.id.date);
            descriptionTextView = (TextView) v.findViewById(R.id.weather_description);
            tempMaxTextView = (TextView) v.findViewById(R.id.high_temperature);
            tempMinTextView = (TextView) v.findViewById(R.id.low_temperature);

            v.setOnClickListener(this);
        }

        /**
         * Callback, which called when a ViewHolder was clicked.
         * @param view The clicked View.
         */
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis =
                    mCursor.getLong(mCursor.getColumnIndex(WeatherContract.WeatherEntry.COL_DATE));
            mOnClickListener.onClick(dateInMillis);
        }

    }

    /**
     * Swaps the Cursor and notifies the RecyclerView to update.
     * @param newCursor New Cursor to be the new data source.
     */
    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * Interface which receives the onClick events.
     */
    public interface WeatherAdapterOnClickHandler{
        void onClick(long date);
    }
}
