package com.vibes.vibes;

import android.content.Context;

/**
 * Utility class to find resources dynamically at runtime without knowing the resource id
 */
public class DynamicResourcesLoader {
    private static final String icon_name = "ic_stat_vibes_notif_icon";
    private static final String drawablePath = "drawable";
    private Context context;

    public DynamicResourcesLoader(Context context) {
        this.context = context;
    }

    /**
     * Returns the small icon id for push notifications. The small icon should be in /drawable/ic_stat_vibes_notif_icon
     * @return
     */
    public int getNotifIconResourceId() {
        return getNotifIconResourceId(icon_name, drawablePath, context.getPackageName());
    }

    /**
     * Obtains a resource id by the provided parameters
     * @param name
     * @param defType
     * @param defPackage
     * @return
     */
    public int getNotifIconResourceId(String name, String defType, String defPackage) {
        return context.getResources().getIdentifier(name, defType, defPackage);
    }
}
