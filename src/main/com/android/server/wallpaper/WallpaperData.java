package com.android.server.wallpaper;

import android.app.IWallpaperManagerCallback;
import android.app.WallpaperColors;
import android.content.ComponentName;
import android.graphics.Rect;
import android.os.RemoteCallbackList;
import android.util.SparseArray;
import com.android.server.wallpaper.WallpaperManagerService;
import java.io.File;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class WallpaperData {
    public boolean allowBackup;
    public RemoteCallbackList<IWallpaperManagerCallback> callbacks;
    public WallpaperManagerService.WallpaperConnection connection;
    public final File cropFile;
    public final Rect cropHint;
    public boolean fromForegroundApp;
    public boolean imageWallpaperPending;
    public long lastDiedTime;
    public boolean mIsColorExtractedFromDim;
    public boolean mSystemWasBoth;
    public SparseArray<Float> mUidToDimAmount;
    public float mWallpaperDimAmount;
    public int mWhich;
    public String name;
    public ComponentName nextWallpaperComponent;
    public WallpaperColors primaryColors;
    public IWallpaperManagerCallback setComplete;
    public int userId;
    public ComponentName wallpaperComponent;
    public final File wallpaperFile;
    public int wallpaperId;
    public WallpaperManagerService.WallpaperObserver wallpaperObserver;
    public boolean wallpaperUpdating;

    public WallpaperData(int i, File file, String str, String str2) {
        this.name = "";
        this.mWallpaperDimAmount = 0.0f;
        this.mUidToDimAmount = new SparseArray<>();
        this.callbacks = new RemoteCallbackList<>();
        this.cropHint = new Rect(0, 0, 0, 0);
        this.userId = i;
        this.wallpaperFile = new File(file, str);
        this.cropFile = new File(file, str2);
    }

    public WallpaperData(int i, int i2) {
        this(i, WallpaperUtils.getWallpaperDir(i), i2 == 2 ? "wallpaper_lock_orig" : "wallpaper_orig", i2 == 2 ? "wallpaper_lock" : "wallpaper");
    }

    public WallpaperData(WallpaperData wallpaperData) {
        this.name = "";
        this.mWallpaperDimAmount = 0.0f;
        this.mUidToDimAmount = new SparseArray<>();
        this.callbacks = new RemoteCallbackList<>();
        Rect rect = new Rect(0, 0, 0, 0);
        this.cropHint = rect;
        this.userId = wallpaperData.userId;
        this.wallpaperFile = wallpaperData.wallpaperFile;
        this.cropFile = wallpaperData.cropFile;
        this.wallpaperComponent = wallpaperData.wallpaperComponent;
        this.mWhich = wallpaperData.mWhich;
        this.wallpaperId = wallpaperData.wallpaperId;
        rect.set(wallpaperData.cropHint);
        this.allowBackup = wallpaperData.allowBackup;
        this.primaryColors = wallpaperData.primaryColors;
        this.mWallpaperDimAmount = wallpaperData.mWallpaperDimAmount;
        WallpaperManagerService.WallpaperConnection wallpaperConnection = wallpaperData.connection;
        this.connection = wallpaperConnection;
        if (wallpaperConnection != null) {
            wallpaperConnection.mWallpaper = this;
        }
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder(defaultString(this));
        sb.append(", id: ");
        sb.append(this.wallpaperId);
        sb.append(", which: ");
        sb.append(this.mWhich);
        sb.append(", file mod: ");
        File file = this.wallpaperFile;
        sb.append(file != null ? Long.valueOf(file.lastModified()) : "null");
        if (this.connection == null) {
            sb.append(", no connection");
        } else {
            sb.append(", info: ");
            sb.append(this.connection.mInfo);
            sb.append(", engine(s):");
            this.connection.forEachDisplayConnector(new Consumer() { // from class: com.android.server.wallpaper.WallpaperData$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    WallpaperData.lambda$toString$0(sb, (WallpaperManagerService.DisplayConnector) obj);
                }
            });
        }
        return sb.toString();
    }

    public static /* synthetic */ void lambda$toString$0(StringBuilder sb, WallpaperManagerService.DisplayConnector displayConnector) {
        if (displayConnector.mEngine != null) {
            sb.append(" ");
            sb.append(defaultString(displayConnector.mEngine));
            return;
        }
        sb.append(" null");
    }

    public static String defaultString(Object obj) {
        return obj.getClass().getSimpleName() + "@" + Integer.toHexString(obj.hashCode());
    }

    public boolean cropExists() {
        return this.cropFile.exists();
    }

    public boolean sourceExists() {
        return this.wallpaperFile.exists();
    }
}
