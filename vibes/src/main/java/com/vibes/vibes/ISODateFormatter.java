package com.vibes.vibes;

import androidx.annotation.NonNull;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper class for parsing and formatting dates in ISO format.
 */
class ISODateFormatter {
    public static final String TAG = "ISODateFormatter";
    // PUSHSDK-337
    // In java 7 XXX is allowed in the pattern for ISO 8601 which seems to work on the latest
    // android phone. Nevertheless, when using yyyy-MM-dd'T'HH:mm:ss.SSSZ in an older phone, it
    // thows an exception "Date - unknown pattern character 'X'"
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ") {
        @Override
        public StringBuffer format(@NonNull Date date, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
            StringBuffer rfcFormat = super.format(date, toAppendTo, pos);
            return rfcFormat.insert(rfcFormat.length() - 2, ":");
        }

        @Override
        public Date parse(@NonNull String text, @NonNull ParsePosition pos) {
            if (text.length() > 3) {
                text = text.substring(0, text.length() - 3) + text.substring(text.length() - 2);
            }
            return super.parse(text, pos);
        }
    };
    private static SimpleDateFormat secondsDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    /**
     * Formats a Date in ISO format
     * @param date the date to format
     * @return the formatted string
     */
    static String toISOString(Date date) {
        return ISODateFormatter.simpleDateFormat.format(date);
    }

    /**
     * Formats a Date in ISO format
     * @param date the date to format
     * @return the formatted string
     */
    static String toSecondsString(Date date) {
        return ISODateFormatter.secondsDateFormat.format(date);
    }
    /**
     * Parses an ISO date string to a Date object.
     * @param iso The string to parse
     * @return the generated Date
     */
    static Date fromISOString(String iso) {
        try {
            return ISODateFormatter.simpleDateFormat.parse(iso);
        } catch (Exception e) {
            Vibes.getCurrentLogger().log(e);
        }
        return null;
    }
}
