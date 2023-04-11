package com.vibes.vibes;

import androidx.annotation.Nullable;

public class StubSoundExtractor implements SoundExtractor {

    @Override
    public int getSoundFrom(@Nullable String resourceName) {
        if (resourceName != null && resourceName.equals("ding")) {
            return 1;
        }
        return 0;
    }
}
