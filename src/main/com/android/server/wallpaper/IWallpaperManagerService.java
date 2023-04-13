package com.android.server.wallpaper;

import android.app.IWallpaperManager;
import android.os.IBinder;
/* loaded from: classes2.dex */
public interface IWallpaperManagerService extends IWallpaperManager, IBinder {
    void onBootPhase(int i);

    void onUnlockUser(int i);
}
