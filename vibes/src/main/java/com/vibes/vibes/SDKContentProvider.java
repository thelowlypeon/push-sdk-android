package com.vibes.vibes;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Handles initialization of the Vibes SDK without requiring the creation of a custom Application
 * subclass.
 *
 * @see <a href="https://firebase.googleblog.com/2016/12/how-does-firebase-initialize-on-android.html">How does Firebase initialize on Android?</a>
 */
public class SDKContentProvider extends ContentProvider {
    private static final String TAG = "SDKContentProvider";

    /**
     * Initializes the SDK.
     * @return true if initialization was successful; false otherwise
     */
    @Override
    public boolean onCreate() {
        Vibes.initialize(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
