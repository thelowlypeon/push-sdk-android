package com.vibes.vibes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The default implementation of BitmapExtractor
 * <p>
 * Pulls a stream from a URL and converts to a Bitmap
 */
public class HTTPBitmapExtractor implements BitmapExtractor {

    /**
     * Opens a URL connection and converts the stream into a bitmap
     *
     * @param imageUrl: String
     * @return Bitmap?
     */
    @Override
    @Nullable
    public Bitmap getBitmapFrom(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            return BitmapFactory.decodeStream(connection.getInputStream());
        } catch (IOException e) {
            Vibes.getCurrentLogger().log(e);
            return null;
        }
    }
}
