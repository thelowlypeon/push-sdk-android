package com.vibes.vibes;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

/**
 * Interface defining how to retrieve a bitmap given an imageUrl
 */
public interface BitmapExtractor {

    /**
     * Method called when a rich push notification is received. It will download the rich content
     * image.
     *
     * @param imageUrl: String
     * @return Bitmap?
     */
    @Nullable
    Bitmap getBitmapFrom(String imageUrl);
}

