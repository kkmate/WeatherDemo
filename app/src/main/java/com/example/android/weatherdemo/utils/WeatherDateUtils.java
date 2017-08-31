package com.example.android.weatherdemo.utils;

/**
 * Created by tomaki on 23/08/2017.
 */

import android.content.Context;
import android.text.format.DateUtils;

import com.example.android.weatherdemo.R;
import com.example.android.weatherdemo.data.WeatherPreferences;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Utilities to handle date conversions.
 */
public class WeatherDateUtils {

    private static final String TAG = WeatherDateUtils.class.getSimpleName();

    /* Base flags to date format */
    private static final int DATE_FORMAT_FLAGS =
            DateUtils.FORMAT_SHOW_DATE
            | DateUtils.FORMAT_NO_YEAR
            | DateUtils.FORMAT_SHOW_WEEKDAY;
    /**
     * Returns the number of days since the epoch in UTC time from the given date.
     */
    public static long getDayNumber(long date) {
        TimeZone timeZone = TimeZone.getDefault();
        long offset = timeZone.getOffset(date);
        return (date + offset) / Constants.DAY_IN_MILLIS;
    }

    /**
     * Decide whether the given date is normalized or not.
     * @param millisSinceEpoch Date in milliseconds.
     * @return True if normalized, false otherwise.
     */
    public static boolean isDateNormalized(long millisSinceEpoch) {
        boolean isDateNormalized = false;
        if (millisSinceEpoch % Constants.DAY_IN_MILLIS == 0) {
            isDateNormalized = true;
        }

        return isDateNormalized;
    }

    /**
     * Gets current date in UTC normalized format.
     * @return Current day in normalized format.
     */
    public static long getNormalizedUtcDateForToday() {
        long utcNowMillis = System.currentTimeMillis();
        TimeZone currentTimeZone = TimeZone.getDefault();
        long gmtOffsetMillis = currentTimeZone.getOffset(utcNowMillis);
        long timeSinceEpochLocalTimeMillis = utcNowMillis + gmtOffsetMillis;
        long daysSinceEpochLocal = TimeUnit.MILLISECONDS.toDays(timeSinceEpochLocalTimeMillis);
        long normalizedUtcMidnightMillis = TimeUnit.DAYS.toMillis(daysSinceEpochLocal);

        return normalizedUtcMidnightMillis;
    }

    /**
     * Normalizes the date to the beginning of the (UTC) day (midnight) in local time
     */
    public static long normalizeDate(long date) {
            long daysSinceEpoch = elapsedDaysSinceEpoch(date);
            long millisFromEpochToTodayAtMidnightUtc = daysSinceEpoch * Constants.DAY_IN_MILLIS;
            return millisFromEpochToTodayAtMidnightUtc;
    }

    /**
     * Gets the number of days since the epoch to the current date.
     * @param utcDate UTC time in milliseconds.
     * @return Number of days from the epoch to the date given.
     */
    private static long elapsedDaysSinceEpoch(long utcDate) {
        return TimeUnit.MILLISECONDS.toDays(utcDate);
    }

    /**
     * Converts UTC time to local.
     * @param utcDate UTC date in milliseconds.
     * @return Local date.
     */
    public static long getLocalDateFromUTC(long utcDate) {
        TimeZone timeZone = TimeZone.getDefault();
        long offset = timeZone.getOffset(utcDate);
        return utcDate - offset;
    }

    /**
     * Converts local time to UTC date.
     */
    public static long getUTCDateFromLocal(long localDate) {
        TimeZone timeZone = TimeZone.getDefault();
        long offset = timeZone.getOffset(localDate);
        return localDate + offset;
    }

    /**
     * Returns an easily readable format of the given date.
     */
    public static String getReadableDate(Context context, long dateInMillis) {

        long localDate = getLocalDateFromUTC(dateInMillis);
        long dayNumber = getDayNumber(localDate);
        long today = getDayNumber(System.currentTimeMillis());

        if (dayNumber < today + 7) {
            return getDayName(context, localDate);
        } else {
            return DateUtils.formatDateTime(context, localDate, DATE_FORMAT_FLAGS | DateUtils.FORMAT_ABBREV_ALL);
        }
    }

    /**
     * Returns an easily readable format of the given date with location appended.
     */
    public static String getReadableDateWithLocation(Context c, long dateInMillis){
        return getReadableDate(c, dateInMillis)
                .concat(Constants.SEPARATOR_COMMA)
                .concat(WeatherPreferences.getPreferredWeatherLocation(c));
    }

    /**
     * Returns the name of the given day. E.g "today", "Friday".
     */
    private static String getDayName(Context context, long dateInMillis) {
        long dayNumber = getDayNumber(dateInMillis);
        long today = getDayNumber(System.currentTimeMillis());
        long tomorrow = today + 1;
        if (dayNumber == today) {
            return context.getString(R.string.today);
        } else if (dayNumber == tomorrow) {
            return context.getString(R.string.tomorrow);
        } else {
            return DateUtils.formatDateTime(context, dateInMillis, DATE_FORMAT_FLAGS | DateUtils.FORMAT_ABBREV_MONTH);
        }
    }
}