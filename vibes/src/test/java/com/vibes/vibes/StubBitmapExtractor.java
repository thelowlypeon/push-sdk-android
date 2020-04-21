package com.vibes.vibes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class StubBitmapExtractor implements BitmapExtractor {

    @Override
    public Bitmap getBitmapFrom(String imageUrl) {
        if (imageUrl.equals("jpeg")) {
            return BitmapFactory.decodeByteArray(new byte[0], 0, 0);
        }
        return null;
    }
}
