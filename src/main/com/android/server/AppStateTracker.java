package com.android.server;
/* loaded from: classes5.dex */
public interface AppStateTracker {
    public static final String TAG = "AppStateTracker";

    /* loaded from: classes5.dex */
    public interface BackgroundRestrictedAppListener {
        void updateBackgroundRestrictedForUidPackage(int i, String str, boolean z);
    }

    void addBackgroundRestrictedAppListener(BackgroundRestrictedAppListener backgroundRestrictedAppListener);

    boolean isAppBackgroundRestricted(int i, String str);
}
