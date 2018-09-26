package com.vibes.vibes;

import android.support.annotation.RawRes;

/**
 * The default implementation of SoundExtractor
 * <p>
 * Always returns an invalid resource.
 */
public class NoOpSoundExtractor implements SoundExtractor {

    /**
     * Returns an invalid resource.  Will use default sound in {@link NotificationFactory}
     *
     * @param resourceName
     * @return an invalid resource
     */
    @Override
    @RawRes
    public int getSoundFrom(String resourceName) {
        return 0;
    }

}
