package com.vibes.vibes.util;

import android.util.Patterns;

/**
 * Utility class for most common functionality used in the Vibes SDK
 */
public class VibesUtil {
    /**
     * Check if a given string is empty or null
     * @param str The String to check for empty or null
     * @return boolean
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Helper function to check if a given string matches a url
     * @param str The URL String to check pattern match
     * @return boolean
     */
    public static boolean isUrlMatch(String str) {
        return Patterns.WEB_URL.matcher(str).matches();
    }
}
