package com.vibes.vibes;

import android.support.annotation.Nullable;
import android.support.annotation.RawRes;

/**
 * Interface defining how to retrieve a sound given a resource name
 */
public interface SoundExtractor {

    /**
     * Override this method to return the index of a custom sound
     * <p>
     * return -1 if no resource is found
     */
    @RawRes
    int getSoundFrom(@Nullable String resourceName);
}
