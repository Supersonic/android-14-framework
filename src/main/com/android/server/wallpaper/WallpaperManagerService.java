package com.android.server.wallpaper;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.ILocalWallpaperColorConsumer;
import android.app.IWallpaperManager;
import android.app.IWallpaperManagerCallback;
import android.app.PendingIntent;
import android.app.UserSwitchObserver;
import android.app.WallpaperColors;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.display.DisplayManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.IRemoteCallback;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SELinux;
import android.os.ShellCallback;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.service.wallpaper.IWallpaperConnection;
import android.service.wallpaper.IWallpaperEngine;
import android.service.wallpaper.IWallpaperService;
import android.system.ErrnoException;
import android.system.Os;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Display;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p014wm.WindowManagerInternal;
import com.android.server.utils.TimingsTraceAndSlog;
import com.android.server.wallpaper.WallpaperDataParser;
import com.android.server.wallpaper.WallpaperDisplayHelper;
import com.android.server.wallpaper.WallpaperManagerService;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class WallpaperManagerService extends IWallpaperManager.Stub implements IWallpaperManagerService {
    public static final RectF LOCAL_COLOR_BOUNDS = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
    public static final Map<Integer, String> sWallpaperType = Map.of(1, "decode_record", 2, "decode_lock_record");
    public final ActivityManager mActivityManager;
    public final AppOpsManager mAppOpsManager;
    public WallpaperColors mCacheDefaultImageWallpaperColors;
    public final SparseArray<SparseArray<RemoteCallbackList<IWallpaperManagerCallback>>> mColorsChangedListeners;
    public final Context mContext;
    public int mCurrentUserId;
    public final ComponentName mDefaultWallpaperComponent;
    public final DisplayManager.DisplayListener mDisplayListener;
    @VisibleForTesting
    final boolean mEnableSeparateLockScreenEngine;
    public WallpaperData mFallbackWallpaper;
    public final IPackageManager mIPackageManager;
    public final ComponentName mImageWallpaper;
    public boolean mInAmbientMode;
    public IWallpaperManagerCallback mKeyguardListener;
    public WallpaperData mLastLockWallpaper;
    public WallpaperData mLastWallpaper;
    public LocalColorRepository mLocalColorRepo;
    public final Object mLock = new Object();
    public final SparseArray<WallpaperData> mLockWallpaperMap;
    public final MyPackageMonitor mMonitor;
    public final PackageManagerInternal mPackageManagerInternal;
    public WallpaperDestinationChangeHandler mPendingMigrationViaStatic;
    public boolean mShuttingDown;
    public final SparseBooleanArray mUserRestorecon;
    public boolean mWaitingForUnlock;
    public final WallpaperCropper mWallpaperCropper;
    @VisibleForTesting
    final WallpaperDataParser mWallpaperDataParser;
    @VisibleForTesting
    final WallpaperDisplayHelper mWallpaperDisplayHelper;
    public final SparseArray<WallpaperData> mWallpaperMap;
    public final WindowManagerInternal mWindowManagerInternal;

    /* loaded from: classes2.dex */
    public static class Lifecycle extends SystemService {
        public IWallpaperManagerService mService;

        public Lifecycle(Context context) {
            super(context);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            try {
                IWallpaperManagerService iWallpaperManagerService = (IWallpaperManagerService) Class.forName(getContext().getResources().getString(17040024)).getConstructor(Context.class).newInstance(getContext());
                this.mService = iWallpaperManagerService;
                publishBinderService("wallpaper", iWallpaperManagerService);
            } catch (Exception e) {
                Slog.wtf("WallpaperManagerService", "Failed to instantiate WallpaperManagerService", e);
            }
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            IWallpaperManagerService iWallpaperManagerService = this.mService;
            if (iWallpaperManagerService != null) {
                iWallpaperManagerService.onBootPhase(i);
            }
        }

        @Override // com.android.server.SystemService
        public void onUserUnlocking(SystemService.TargetUser targetUser) {
            IWallpaperManagerService iWallpaperManagerService = this.mService;
            if (iWallpaperManagerService != null) {
                iWallpaperManagerService.onUnlockUser(targetUser.getUserIdentifier());
            }
        }
    }

    /* loaded from: classes2.dex */
    public class WallpaperObserver extends FileObserver {
        public final int mUserId;
        public final WallpaperData mWallpaper;
        public final File mWallpaperDir;
        public final File mWallpaperFile;
        public final File mWallpaperLockFile;

        public WallpaperObserver(WallpaperData wallpaperData) {
            super(WallpaperUtils.getWallpaperDir(wallpaperData.userId).getAbsolutePath(), 1672);
            int i = wallpaperData.userId;
            this.mUserId = i;
            File wallpaperDir = WallpaperUtils.getWallpaperDir(i);
            this.mWallpaperDir = wallpaperDir;
            this.mWallpaper = wallpaperData;
            this.mWallpaperFile = new File(wallpaperDir, "wallpaper_orig");
            this.mWallpaperLockFile = new File(wallpaperDir, "wallpaper_lock_orig");
        }

        public WallpaperData dataForEvent(boolean z, boolean z2) {
            WallpaperData wallpaperData;
            synchronized (WallpaperManagerService.this.mLock) {
                if (z2) {
                    try {
                        wallpaperData = (WallpaperData) WallpaperManagerService.this.mLockWallpaperMap.get(this.mUserId);
                    } catch (Throwable th) {
                        throw th;
                    }
                } else {
                    wallpaperData = null;
                }
                if (wallpaperData == null) {
                    wallpaperData = (WallpaperData) WallpaperManagerService.this.mWallpaperMap.get(this.mUserId);
                }
            }
            return wallpaperData != null ? wallpaperData : this.mWallpaper;
        }

        /* JADX WARN: Removed duplicated region for block: B:64:0x00e9  */
        /* JADX WARN: Removed duplicated region for block: B:72:? A[RETURN, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final void updateWallpapers(int i, String str) {
            File file = new File(this.mWallpaperDir, str);
            boolean equals = this.mWallpaperFile.equals(file);
            boolean equals2 = this.mWallpaperLockFile.equals(file);
            final WallpaperData dataForEvent = dataForEvent(equals, equals2);
            int i2 = 1;
            boolean z = i == 128;
            boolean z2 = i == 8 || z;
            boolean z3 = z && equals2;
            boolean z4 = z && !z3;
            boolean z5 = (dataForEvent.mWhich & 2) != 0;
            boolean z6 = dataForEvent.wallpaperComponent == null || i != 8 || dataForEvent.imageWallpaperPending;
            if (z3) {
                return;
            }
            if (equals || equals2) {
                synchronized (WallpaperManagerService.this.mLock) {
                    WallpaperManagerService.this.notifyCallbacksLocked(dataForEvent);
                    if (z2 && z6) {
                        WallpaperManagerService wallpaperManagerService = WallpaperManagerService.this;
                        final WallpaperDestinationChangeHandler wallpaperDestinationChangeHandler = wallpaperManagerService.mPendingMigrationViaStatic;
                        wallpaperManagerService.mPendingMigrationViaStatic = null;
                        SELinux.restorecon(file);
                        if (z4) {
                            WallpaperManagerService.this.loadSettingsLocked(dataForEvent.userId, true, 3);
                        }
                        WallpaperManagerService.this.mWallpaperCropper.generateCrop(dataForEvent);
                        dataForEvent.imageWallpaperPending = false;
                        if (equals) {
                            IRemoteCallback iRemoteCallback = new IRemoteCallback.Stub() { // from class: com.android.server.wallpaper.WallpaperManagerService.WallpaperObserver.1
                                public void sendResult(Bundle bundle) throws RemoteException {
                                    WallpaperDestinationChangeHandler wallpaperDestinationChangeHandler2 = wallpaperDestinationChangeHandler;
                                    if (wallpaperDestinationChangeHandler2 != null) {
                                        wallpaperDestinationChangeHandler2.complete();
                                    }
                                    WallpaperManagerService.this.notifyWallpaperChanged(dataForEvent);
                                }
                            };
                            WallpaperManagerService wallpaperManagerService2 = WallpaperManagerService.this;
                            wallpaperManagerService2.bindWallpaperComponentLocked(wallpaperManagerService2.mImageWallpaper, true, false, dataForEvent, iRemoteCallback);
                        } else {
                            i2 = 0;
                        }
                        if (equals2) {
                            IRemoteCallback iRemoteCallback2 = new IRemoteCallback.Stub() { // from class: com.android.server.wallpaper.WallpaperManagerService.WallpaperObserver.2
                                public void sendResult(Bundle bundle) throws RemoteException {
                                    WallpaperDestinationChangeHandler wallpaperDestinationChangeHandler2 = wallpaperDestinationChangeHandler;
                                    if (wallpaperDestinationChangeHandler2 != null) {
                                        wallpaperDestinationChangeHandler2.complete();
                                    }
                                    WallpaperManagerService.this.notifyWallpaperChanged(dataForEvent);
                                }
                            };
                            WallpaperManagerService wallpaperManagerService3 = WallpaperManagerService.this;
                            wallpaperManagerService3.bindWallpaperComponentLocked(wallpaperManagerService3.mImageWallpaper, true, false, dataForEvent, iRemoteCallback2);
                        } else {
                            if (z5) {
                                WallpaperData wallpaperData = (WallpaperData) WallpaperManagerService.this.mLockWallpaperMap.get(this.mWallpaper.userId);
                                if (wallpaperData != null) {
                                    WallpaperManagerService.this.detachWallpaperLocked(wallpaperData);
                                }
                                WallpaperManagerService.this.mLockWallpaperMap.remove(dataForEvent.userId);
                            }
                            WallpaperManagerService.this.saveSettingsLocked(dataForEvent.userId);
                            if (equals2 && !equals) {
                                WallpaperManagerService.this.notifyWallpaperChanged(dataForEvent);
                            }
                            if (i2 == 0) {
                                WallpaperManagerService.this.notifyWallpaperColorsChanged(dataForEvent, i2);
                                return;
                            }
                            return;
                        }
                        i2 |= 2;
                        WallpaperManagerService.this.saveSettingsLocked(dataForEvent.userId);
                        if (equals2) {
                            WallpaperManagerService.this.notifyWallpaperChanged(dataForEvent);
                        }
                        if (i2 == 0) {
                        }
                    }
                }
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:42:0x009f, code lost:
            r17.this$0.mLockWallpaperMap.remove(r5.userId);
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final void updateWallpapersLegacy(int i, String str) {
            int i2;
            WallpaperData wallpaperData;
            int i3 = 1;
            int i4 = 0;
            boolean z = i == 128;
            boolean z2 = i == 8 || z;
            File file = new File(this.mWallpaperDir, str);
            boolean equals = this.mWallpaperFile.equals(file);
            boolean equals2 = this.mWallpaperLockFile.equals(file);
            final WallpaperData dataForEvent = dataForEvent(equals, equals2);
            if (z && equals2) {
                SELinux.restorecon(file);
                WallpaperManagerService.this.notifyLockWallpaperChanged();
                WallpaperManagerService.this.notifyWallpaperColorsChanged(dataForEvent, 2);
                return;
            }
            synchronized (WallpaperManagerService.this.mLock) {
                if (equals || equals2) {
                    WallpaperManagerService.this.notifyCallbacksLocked(dataForEvent);
                    if ((dataForEvent.wallpaperComponent == null || i != 8 || dataForEvent.imageWallpaperPending) && z2) {
                        SELinux.restorecon(file);
                        if (z) {
                            WallpaperManagerService.this.loadSettingsLocked(dataForEvent.userId, true, 3);
                        }
                        WallpaperManagerService.this.mWallpaperCropper.generateCrop(dataForEvent);
                        dataForEvent.imageWallpaperPending = false;
                        if (equals) {
                            IRemoteCallback iRemoteCallback = new IRemoteCallback.Stub() { // from class: com.android.server.wallpaper.WallpaperManagerService.WallpaperObserver.3
                                public void sendResult(Bundle bundle) throws RemoteException {
                                    Slog.d("WallpaperManagerService", "publish system wallpaper changed!");
                                    WallpaperManagerService.this.notifyWallpaperChanged(dataForEvent);
                                }
                            };
                            WallpaperManagerService wallpaperManagerService = WallpaperManagerService.this;
                            i2 = 2;
                            wallpaperData = dataForEvent;
                            wallpaperManagerService.bindWallpaperComponentLocked(wallpaperManagerService.mImageWallpaper, true, false, dataForEvent, iRemoteCallback);
                        } else {
                            i2 = 2;
                            wallpaperData = dataForEvent;
                            i3 = 0;
                        }
                        if (!equals2 && (wallpaperData.mWhich & i2) == 0) {
                            i4 = i3;
                            WallpaperManagerService.this.saveSettingsLocked(wallpaperData.userId);
                            if (equals2 && !equals) {
                                WallpaperManagerService.this.notifyWallpaperChanged(wallpaperData);
                            }
                        }
                        WallpaperManagerService.this.notifyLockWallpaperChanged();
                        i4 = i3 | 2;
                        WallpaperManagerService.this.saveSettingsLocked(wallpaperData.userId);
                        if (equals2) {
                            WallpaperManagerService.this.notifyWallpaperChanged(wallpaperData);
                        }
                    }
                }
                wallpaperData = dataForEvent;
            }
            if (i4 != 0) {
                WallpaperManagerService.this.notifyWallpaperColorsChanged(wallpaperData, i4);
            }
        }

        @Override // android.os.FileObserver
        public void onEvent(int i, String str) {
            if (str == null) {
                return;
            }
            if (WallpaperManagerService.this.mEnableSeparateLockScreenEngine) {
                updateWallpapers(i, str);
            } else {
                updateWallpapersLegacy(i, str);
            }
        }
    }

    public final void notifyWallpaperChanged(WallpaperData wallpaperData) {
        IWallpaperManagerCallback iWallpaperManagerCallback = wallpaperData.setComplete;
        if (iWallpaperManagerCallback != null) {
            try {
                iWallpaperManagerCallback.onWallpaperChanged();
            } catch (RemoteException unused) {
            }
        }
    }

    public final void notifyLockWallpaperChanged() {
        IWallpaperManagerCallback iWallpaperManagerCallback = this.mKeyguardListener;
        if (iWallpaperManagerCallback != null) {
            try {
                iWallpaperManagerCallback.onWallpaperChanged();
            } catch (RemoteException unused) {
            }
        }
    }

    public void notifyWallpaperColorsChanged(final WallpaperData wallpaperData, final int i) {
        WallpaperConnection wallpaperConnection = wallpaperData.connection;
        if (wallpaperConnection != null) {
            wallpaperConnection.forEachDisplayConnector(new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    WallpaperManagerService.this.lambda$notifyWallpaperColorsChanged$0(wallpaperData, i, (WallpaperManagerService.DisplayConnector) obj);
                }
            });
        } else {
            notifyWallpaperColorsChangedOnDisplay(wallpaperData, i, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyWallpaperColorsChanged$0(WallpaperData wallpaperData, int i, DisplayConnector displayConnector) {
        notifyWallpaperColorsChangedOnDisplay(wallpaperData, i, displayConnector.mDisplayId);
    }

    public final RemoteCallbackList<IWallpaperManagerCallback> getWallpaperCallbacks(int i, int i2) {
        SparseArray<RemoteCallbackList<IWallpaperManagerCallback>> sparseArray = this.mColorsChangedListeners.get(i);
        if (sparseArray != null) {
            return sparseArray.get(i2);
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x002b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void notifyWallpaperColorsChangedOnDisplay(WallpaperData wallpaperData, int i, int i2) {
        boolean z;
        synchronized (this.mLock) {
            RemoteCallbackList<IWallpaperManagerCallback> wallpaperCallbacks = getWallpaperCallbacks(wallpaperData.userId, i2);
            RemoteCallbackList<IWallpaperManagerCallback> wallpaperCallbacks2 = getWallpaperCallbacks(-1, i2);
            if (emptyCallbackList(wallpaperCallbacks) && emptyCallbackList(wallpaperCallbacks2)) {
                return;
            }
            if (wallpaperData.primaryColors != null && !wallpaperData.mIsColorExtractedFromDim) {
                z = false;
                if (z) {
                    extractColors(wallpaperData);
                }
                notifyColorListeners(getAdjustedWallpaperColorsOnDimming(wallpaperData), i, wallpaperData.userId, i2);
            }
            z = true;
            if (z) {
            }
            notifyColorListeners(getAdjustedWallpaperColorsOnDimming(wallpaperData), i, wallpaperData.userId, i2);
        }
    }

    public static <T extends IInterface> boolean emptyCallbackList(RemoteCallbackList<T> remoteCallbackList) {
        return remoteCallbackList == null || remoteCallbackList.getRegisteredCallbackCount() == 0;
    }

    public final void notifyColorListeners(WallpaperColors wallpaperColors, int i, int i2, int i3) {
        IWallpaperManagerCallback iWallpaperManagerCallback;
        int i4;
        ArrayList arrayList = new ArrayList();
        synchronized (this.mLock) {
            RemoteCallbackList<IWallpaperManagerCallback> wallpaperCallbacks = getWallpaperCallbacks(i2, i3);
            RemoteCallbackList<IWallpaperManagerCallback> wallpaperCallbacks2 = getWallpaperCallbacks(-1, i3);
            iWallpaperManagerCallback = this.mKeyguardListener;
            if (wallpaperCallbacks != null) {
                int beginBroadcast = wallpaperCallbacks.beginBroadcast();
                for (int i5 = 0; i5 < beginBroadcast; i5++) {
                    arrayList.add(wallpaperCallbacks.getBroadcastItem(i5));
                }
                wallpaperCallbacks.finishBroadcast();
            }
            if (wallpaperCallbacks2 != null) {
                int beginBroadcast2 = wallpaperCallbacks2.beginBroadcast();
                for (int i6 = 0; i6 < beginBroadcast2; i6++) {
                    arrayList.add(wallpaperCallbacks2.getBroadcastItem(i6));
                }
                wallpaperCallbacks2.finishBroadcast();
            }
        }
        int size = arrayList.size();
        for (i4 = 0; i4 < size; i4++) {
            try {
                ((IWallpaperManagerCallback) arrayList.get(i4)).onWallpaperColorsChanged(wallpaperColors, i, i2);
            } catch (RemoteException unused) {
            }
        }
        if (iWallpaperManagerCallback == null || i3 != 0) {
            return;
        }
        try {
            iWallpaperManagerCallback.onWallpaperColorsChanged(wallpaperColors, i, i2);
        } catch (RemoteException unused2) {
        }
    }

    public final void extractColors(WallpaperData wallpaperData) {
        boolean z;
        boolean z2;
        WallpaperColors wallpaperColors;
        String str;
        int i;
        float f;
        File file;
        synchronized (this.mLock) {
            z = false;
            wallpaperData.mIsColorExtractedFromDim = false;
        }
        if (wallpaperData.equals(this.mFallbackWallpaper)) {
            synchronized (this.mLock) {
                if (this.mFallbackWallpaper.primaryColors != null) {
                    return;
                }
                WallpaperColors extractDefaultImageWallpaperColors = extractDefaultImageWallpaperColors(wallpaperData);
                synchronized (this.mLock) {
                    this.mFallbackWallpaper.primaryColors = extractDefaultImageWallpaperColors;
                }
                return;
            }
        }
        synchronized (this.mLock) {
            if (!this.mImageWallpaper.equals(wallpaperData.wallpaperComponent) && wallpaperData.wallpaperComponent != null) {
                z2 = false;
                wallpaperColors = null;
                if (!z2 && (file = wallpaperData.cropFile) != null && file.exists()) {
                    str = wallpaperData.cropFile.getAbsolutePath();
                } else {
                    if (z2 && !wallpaperData.cropExists() && !wallpaperData.sourceExists()) {
                        z = true;
                    }
                    str = null;
                }
                i = wallpaperData.wallpaperId;
                f = wallpaperData.mWallpaperDimAmount;
            }
            z2 = true;
            wallpaperColors = null;
            if (!z2) {
            }
            if (z2) {
                z = true;
            }
            str = null;
            i = wallpaperData.wallpaperId;
            f = wallpaperData.mWallpaperDimAmount;
        }
        if (str != null) {
            Bitmap decodeFile = BitmapFactory.decodeFile(str);
            if (decodeFile != null) {
                WallpaperColors fromBitmap = WallpaperColors.fromBitmap(decodeFile, f);
                decodeFile.recycle();
                wallpaperColors = fromBitmap;
            }
        } else if (z) {
            wallpaperColors = extractDefaultImageWallpaperColors(wallpaperData);
        }
        if (wallpaperColors == null) {
            Slog.w("WallpaperManagerService", "Cannot extract colors because wallpaper could not be read.");
            return;
        }
        synchronized (this.mLock) {
            if (wallpaperData.wallpaperId == i) {
                wallpaperData.primaryColors = wallpaperColors;
                saveSettingsLocked(wallpaperData.userId);
            } else {
                Slog.w("WallpaperManagerService", "Not setting primary colors since wallpaper changed");
            }
        }
    }

    public final WallpaperColors extractDefaultImageWallpaperColors(WallpaperData wallpaperData) {
        InputStream openDefaultWallpaper;
        synchronized (this.mLock) {
            WallpaperColors wallpaperColors = this.mCacheDefaultImageWallpaperColors;
            if (wallpaperColors != null) {
                return wallpaperColors;
            }
            float f = wallpaperData.mWallpaperDimAmount;
            WallpaperColors wallpaperColors2 = null;
            try {
                openDefaultWallpaper = WallpaperManager.openDefaultWallpaper(this.mContext, 1);
            } catch (IOException e) {
                Slog.w("WallpaperManagerService", "Can't close default wallpaper stream", e);
            } catch (OutOfMemoryError e2) {
                Slog.w("WallpaperManagerService", "Can't decode default wallpaper stream", e2);
            }
            if (openDefaultWallpaper == null) {
                Slog.w("WallpaperManagerService", "Can't open default wallpaper stream");
                if (openDefaultWallpaper != null) {
                    openDefaultWallpaper.close();
                }
                return null;
            }
            Bitmap decodeStream = BitmapFactory.decodeStream(openDefaultWallpaper, null, new BitmapFactory.Options());
            if (decodeStream != null) {
                wallpaperColors2 = WallpaperColors.fromBitmap(decodeStream, f);
                decodeStream.recycle();
            }
            openDefaultWallpaper.close();
            if (wallpaperColors2 == null) {
                Slog.e("WallpaperManagerService", "Extract default image wallpaper colors failed");
            } else {
                synchronized (this.mLock) {
                    this.mCacheDefaultImageWallpaperColors = wallpaperColors2;
                }
            }
            return wallpaperColors2;
        }
    }

    public final boolean supportsMultiDisplay(WallpaperConnection wallpaperConnection) {
        if (wallpaperConnection != null) {
            WallpaperInfo wallpaperInfo = wallpaperConnection.mInfo;
            return wallpaperInfo == null || wallpaperInfo.supportsMultipleDisplays();
        }
        return false;
    }

    public final void updateFallbackConnection() {
        WallpaperData wallpaperData;
        WallpaperData wallpaperData2 = this.mLastWallpaper;
        if (wallpaperData2 == null || (wallpaperData = this.mFallbackWallpaper) == null) {
            return;
        }
        WallpaperConnection wallpaperConnection = wallpaperData2.connection;
        final WallpaperConnection wallpaperConnection2 = wallpaperData.connection;
        if (wallpaperConnection2 == null) {
            Slog.w("WallpaperManagerService", "Fallback wallpaper connection has not been created yet!!");
        } else if (supportsMultiDisplay(wallpaperConnection)) {
            if (wallpaperConnection2.mDisplayConnector.size() != 0) {
                wallpaperConnection2.forEachDisplayConnector(new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda12
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        WallpaperManagerService.lambda$updateFallbackConnection$1(WallpaperManagerService.WallpaperConnection.this, (WallpaperManagerService.DisplayConnector) obj);
                    }
                });
                wallpaperConnection2.mDisplayConnector.clear();
            }
        } else {
            wallpaperConnection2.appendConnectorWithCondition(new Predicate() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda13
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateFallbackConnection$2;
                    lambda$updateFallbackConnection$2 = WallpaperManagerService.this.lambda$updateFallbackConnection$2(wallpaperConnection2, (Display) obj);
                    return lambda$updateFallbackConnection$2;
                }
            });
            wallpaperConnection2.forEachDisplayConnector(new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda14
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    WallpaperManagerService.this.lambda$updateFallbackConnection$3(wallpaperConnection2, (WallpaperManagerService.DisplayConnector) obj);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$updateFallbackConnection$1(WallpaperConnection wallpaperConnection, DisplayConnector displayConnector) {
        if (displayConnector.mEngine != null) {
            displayConnector.disconnectLocked(wallpaperConnection);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateFallbackConnection$2(WallpaperConnection wallpaperConnection, Display display) {
        return (!this.mWallpaperDisplayHelper.isUsableDisplay(display, wallpaperConnection.mClientUid) || display.getDisplayId() == 0 || wallpaperConnection.containsDisplay(display.getDisplayId())) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateFallbackConnection$3(WallpaperConnection wallpaperConnection, DisplayConnector displayConnector) {
        if (displayConnector.mEngine == null) {
            displayConnector.connectLocked(wallpaperConnection, this.mFallbackWallpaper);
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public final class DisplayConnector {
        public boolean mDimensionsChanged;
        public final int mDisplayId;
        public IWallpaperEngine mEngine;
        public boolean mPaddingChanged;
        public final Binder mToken = new Binder();

        public DisplayConnector(int i) {
            this.mDisplayId = i;
        }

        public void ensureStatusHandled() {
            WallpaperDisplayHelper.DisplayData displayDataOrCreate = WallpaperManagerService.this.mWallpaperDisplayHelper.getDisplayDataOrCreate(this.mDisplayId);
            if (this.mDimensionsChanged) {
                try {
                    this.mEngine.setDesiredSize(displayDataOrCreate.mWidth, displayDataOrCreate.mHeight);
                } catch (RemoteException e) {
                    Slog.w("WallpaperManagerService", "Failed to set wallpaper dimensions", e);
                }
                this.mDimensionsChanged = false;
            }
            if (this.mPaddingChanged) {
                try {
                    this.mEngine.setDisplayPadding(displayDataOrCreate.mPadding);
                } catch (RemoteException e2) {
                    Slog.w("WallpaperManagerService", "Failed to set wallpaper padding", e2);
                }
                this.mPaddingChanged = false;
            }
        }

        public void connectLocked(WallpaperConnection wallpaperConnection, WallpaperData wallpaperData) {
            if (wallpaperConnection.mService == null) {
                Slog.w("WallpaperManagerService", "WallpaperService is not connected yet");
                return;
            }
            TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog("WallpaperManagerService");
            timingsTraceAndSlog.traceBegin("WPMS.connectLocked-" + wallpaperData.wallpaperComponent);
            WallpaperManagerService.this.mWindowManagerInternal.addWindowToken(this.mToken, 2013, this.mDisplayId, null);
            WallpaperManagerService.this.mWindowManagerInternal.setWallpaperShowWhenLocked(this.mToken, (wallpaperData.mWhich & 2) != 0);
            WallpaperDisplayHelper.DisplayData displayDataOrCreate = WallpaperManagerService.this.mWallpaperDisplayHelper.getDisplayDataOrCreate(this.mDisplayId);
            try {
                wallpaperConnection.mService.attach(wallpaperConnection, this.mToken, 2013, false, displayDataOrCreate.mWidth, displayDataOrCreate.mHeight, displayDataOrCreate.mPadding, this.mDisplayId, wallpaperData.mWhich);
            } catch (RemoteException e) {
                Slog.w("WallpaperManagerService", "Failed attaching wallpaper on display", e);
                if (!wallpaperData.wallpaperUpdating && wallpaperConnection.getConnectedEngineSize() == 0) {
                    WallpaperManagerService.this.bindWallpaperComponentLocked(null, false, false, wallpaperData, null);
                }
            }
            timingsTraceAndSlog.traceEnd();
        }

        public void disconnectLocked(WallpaperConnection wallpaperConnection) {
            WallpaperManagerService.this.mWindowManagerInternal.removeWindowToken(this.mToken, false, this.mDisplayId);
            try {
                IWallpaperService iWallpaperService = wallpaperConnection.mService;
                if (iWallpaperService != null) {
                    iWallpaperService.detach(this.mToken);
                }
            } catch (RemoteException unused) {
                Slog.w("WallpaperManagerService", "connection.mService.destroy() threw a RemoteException");
            }
            this.mEngine = null;
        }
    }

    /* loaded from: classes2.dex */
    public class WallpaperConnection extends IWallpaperConnection.Stub implements ServiceConnection {
        public final int mClientUid;
        public final WallpaperInfo mInfo;
        public IRemoteCallback mReply;
        public IWallpaperService mService;
        public WallpaperData mWallpaper;
        public final SparseArray<DisplayConnector> mDisplayConnector = new SparseArray<>();
        public Runnable mResetRunnable = new Runnable() { // from class: com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                WallpaperManagerService.WallpaperConnection.this.lambda$new$0();
            }
        };
        public Runnable mTryToRebindRunnable = new Runnable() { // from class: com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                WallpaperManagerService.WallpaperConnection.this.tryToRebind();
            }
        };
        public Runnable mDisconnectRunnable = new Runnable() { // from class: com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                WallpaperManagerService.WallpaperConnection.this.lambda$new$5();
            }
        };

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            synchronized (WallpaperManagerService.this.mLock) {
                if (WallpaperManagerService.this.mShuttingDown) {
                    Slog.i("WallpaperManagerService", "Ignoring relaunch timeout during shutdown");
                    return;
                }
                WallpaperData wallpaperData = this.mWallpaper;
                if (!wallpaperData.wallpaperUpdating && wallpaperData.userId == WallpaperManagerService.this.mCurrentUserId) {
                    Slog.w("WallpaperManagerService", "Wallpaper reconnect timed out for " + this.mWallpaper.wallpaperComponent + ", reverting to built-in wallpaper!");
                    WallpaperManagerService.this.clearWallpaperLocked(true, 1, this.mWallpaper.userId, null);
                }
            }
        }

        public WallpaperConnection(WallpaperInfo wallpaperInfo, WallpaperData wallpaperData, int i) {
            this.mInfo = wallpaperInfo;
            this.mWallpaper = wallpaperData;
            this.mClientUid = i;
            initDisplayState();
        }

        public final void initDisplayState() {
            if (this.mWallpaper.equals(WallpaperManagerService.this.mFallbackWallpaper)) {
                return;
            }
            if (WallpaperManagerService.this.supportsMultiDisplay(this)) {
                appendConnectorWithCondition(new Predicate() { // from class: com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection$$ExternalSyntheticLambda6
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$initDisplayState$1;
                        lambda$initDisplayState$1 = WallpaperManagerService.WallpaperConnection.this.lambda$initDisplayState$1((Display) obj);
                        return lambda$initDisplayState$1;
                    }
                });
            } else {
                this.mDisplayConnector.append(0, new DisplayConnector(0));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$initDisplayState$1(Display display) {
            return WallpaperManagerService.this.mWallpaperDisplayHelper.isUsableDisplay(display, this.mClientUid);
        }

        public final void appendConnectorWithCondition(Predicate<Display> predicate) {
            Display[] displays;
            for (Display display : WallpaperManagerService.this.mWallpaperDisplayHelper.getDisplays()) {
                if (predicate.test(display)) {
                    int displayId = display.getDisplayId();
                    if (this.mDisplayConnector.get(displayId) == null) {
                        this.mDisplayConnector.append(displayId, new DisplayConnector(displayId));
                    }
                }
            }
        }

        public void forEachDisplayConnector(Consumer<DisplayConnector> consumer) {
            for (int size = this.mDisplayConnector.size() - 1; size >= 0; size--) {
                consumer.accept(this.mDisplayConnector.valueAt(size));
            }
        }

        public int getConnectedEngineSize() {
            int i = 0;
            for (int size = this.mDisplayConnector.size() - 1; size >= 0; size--) {
                if (this.mDisplayConnector.valueAt(size).mEngine != null) {
                    i++;
                }
            }
            return i;
        }

        public DisplayConnector getDisplayConnectorOrCreate(int i) {
            DisplayConnector displayConnector = this.mDisplayConnector.get(i);
            if (displayConnector == null && WallpaperManagerService.this.mWallpaperDisplayHelper.isUsableDisplay(i, this.mClientUid)) {
                DisplayConnector displayConnector2 = new DisplayConnector(i);
                this.mDisplayConnector.append(i, displayConnector2);
                return displayConnector2;
            }
            return displayConnector;
        }

        public boolean containsDisplay(int i) {
            return this.mDisplayConnector.get(i) != null;
        }

        public void removeDisplayConnector(int i) {
            if (this.mDisplayConnector.get(i) != null) {
                this.mDisplayConnector.remove(i);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog("WallpaperManagerService");
            timingsTraceAndSlog.traceBegin("WPMS.onServiceConnected-" + componentName);
            synchronized (WallpaperManagerService.this.mLock) {
                if (this.mWallpaper.connection == this) {
                    this.mService = IWallpaperService.Stub.asInterface(iBinder);
                    WallpaperManagerService.this.attachServiceLocked(this, this.mWallpaper);
                    if (!this.mWallpaper.equals(WallpaperManagerService.this.mFallbackWallpaper)) {
                        WallpaperManagerService.this.saveSettingsLocked(this.mWallpaper.userId);
                    }
                    FgThread.getHandler().removeCallbacks(this.mResetRunnable);
                    WallpaperManagerService.this.mContext.getMainThreadHandler().removeCallbacks(this.mTryToRebindRunnable);
                    WallpaperManagerService.this.mContext.getMainThreadHandler().removeCallbacks(this.mDisconnectRunnable);
                }
            }
            timingsTraceAndSlog.traceEnd();
        }

        public void onLocalWallpaperColorsChanged(final RectF rectF, final WallpaperColors wallpaperColors, final int i) {
            forEachDisplayConnector(new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    WallpaperManagerService.WallpaperConnection.this.lambda$onLocalWallpaperColorsChanged$3(rectF, wallpaperColors, i, (WallpaperManagerService.DisplayConnector) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onLocalWallpaperColorsChanged$3(final RectF rectF, final WallpaperColors wallpaperColors, int i, DisplayConnector displayConnector) {
            Consumer<ILocalWallpaperColorConsumer> consumer = new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    WallpaperManagerService.WallpaperConnection.lambda$onLocalWallpaperColorsChanged$2(rectF, wallpaperColors, (ILocalWallpaperColorConsumer) obj);
                }
            };
            synchronized (WallpaperManagerService.this.mLock) {
                WallpaperManagerService.this.mLocalColorRepo.forEachCallback(consumer, rectF, i);
            }
        }

        public static /* synthetic */ void lambda$onLocalWallpaperColorsChanged$2(RectF rectF, WallpaperColors wallpaperColors, ILocalWallpaperColorConsumer iLocalWallpaperColorConsumer) {
            try {
                iLocalWallpaperColorConsumer.onColorsChanged(rectF, wallpaperColors);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (WallpaperManagerService.this.mLock) {
                Slog.w("WallpaperManagerService", "Wallpaper service gone: " + componentName);
                if (!Objects.equals(componentName, this.mWallpaper.wallpaperComponent)) {
                    Slog.e("WallpaperManagerService", "Does not match expected wallpaper component " + this.mWallpaper.wallpaperComponent);
                }
                this.mService = null;
                forEachDisplayConnector(new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$WallpaperConnection$$ExternalSyntheticLambda3
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((WallpaperManagerService.DisplayConnector) obj).mEngine = null;
                    }
                });
                WallpaperData wallpaperData = this.mWallpaper;
                if (wallpaperData.connection == this && !wallpaperData.wallpaperUpdating) {
                    WallpaperManagerService.this.mContext.getMainThreadHandler().postDelayed(this.mDisconnectRunnable, 1000L);
                }
            }
        }

        public final void scheduleTimeoutLocked() {
            Handler handler = FgThread.getHandler();
            handler.removeCallbacks(this.mResetRunnable);
            handler.postDelayed(this.mResetRunnable, 10000L);
            Slog.i("WallpaperManagerService", "Started wallpaper reconnect timeout for " + this.mWallpaper.wallpaperComponent);
        }

        public final void tryToRebind() {
            synchronized (WallpaperManagerService.this.mLock) {
                WallpaperData wallpaperData = this.mWallpaper;
                if (wallpaperData.wallpaperUpdating) {
                    return;
                }
                ComponentName componentName = wallpaperData.wallpaperComponent;
                if (WallpaperManagerService.this.bindWallpaperComponentLocked(componentName, true, false, wallpaperData, null)) {
                    this.mWallpaper.connection.scheduleTimeoutLocked();
                } else if (SystemClock.uptimeMillis() - this.mWallpaper.lastDiedTime < 10000) {
                    Slog.w("WallpaperManagerService", "Rebind fail! Try again later");
                    WallpaperManagerService.this.mContext.getMainThreadHandler().postDelayed(this.mTryToRebindRunnable, 1000L);
                } else {
                    Slog.w("WallpaperManagerService", "Reverting to built-in wallpaper!");
                    WallpaperManagerService.this.clearWallpaperLocked(true, 1, this.mWallpaper.userId, null);
                    String flattenToString = componentName.flattenToString();
                    EventLog.writeEvent(33000, flattenToString.substring(0, Math.min(flattenToString.length(), 128)));
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$5() {
            synchronized (WallpaperManagerService.this.mLock) {
                WallpaperData wallpaperData = this.mWallpaper;
                if (this == wallpaperData.connection) {
                    ComponentName componentName = wallpaperData.wallpaperComponent;
                    if (!wallpaperData.wallpaperUpdating && wallpaperData.userId == WallpaperManagerService.this.mCurrentUserId && !Objects.equals(WallpaperManagerService.this.mDefaultWallpaperComponent, componentName) && !Objects.equals(WallpaperManagerService.this.mImageWallpaper, componentName)) {
                        long j = this.mWallpaper.lastDiedTime;
                        if (j != 0 && j + 10000 > SystemClock.uptimeMillis()) {
                            Slog.w("WallpaperManagerService", "Reverting to built-in wallpaper!");
                            WallpaperManagerService.this.clearWallpaperLocked(true, 1, this.mWallpaper.userId, null);
                        } else {
                            this.mWallpaper.lastDiedTime = SystemClock.uptimeMillis();
                            tryToRebind();
                        }
                    }
                } else {
                    Slog.i("WallpaperManagerService", "Wallpaper changed during disconnect tracking; ignoring");
                }
            }
        }

        public void onWallpaperColorsChanged(WallpaperColors wallpaperColors, int i) {
            synchronized (WallpaperManagerService.this.mLock) {
                if (WallpaperManagerService.this.mImageWallpaper.equals(this.mWallpaper.wallpaperComponent)) {
                    return;
                }
                this.mWallpaper.primaryColors = wallpaperColors;
                int i2 = (i == 0 && ((WallpaperData) WallpaperManagerService.this.mLockWallpaperMap.get(this.mWallpaper.userId)) == null) ? 3 : 1;
                if (i2 != 0) {
                    WallpaperManagerService.this.notifyWallpaperColorsChangedOnDisplay(this.mWallpaper, i2, i);
                }
            }
        }

        public void attachEngine(IWallpaperEngine iWallpaperEngine, int i) {
            synchronized (WallpaperManagerService.this.mLock) {
                DisplayConnector displayConnectorOrCreate = getDisplayConnectorOrCreate(i);
                if (displayConnectorOrCreate == null) {
                    throw new IllegalStateException("Connector has already been destroyed");
                }
                displayConnectorOrCreate.mEngine = iWallpaperEngine;
                displayConnectorOrCreate.ensureStatusHandled();
                WallpaperInfo wallpaperInfo = this.mInfo;
                if (wallpaperInfo != null && wallpaperInfo.supportsAmbientMode() && i == 0) {
                    try {
                        displayConnectorOrCreate.mEngine.setInAmbientMode(WallpaperManagerService.this.mInAmbientMode, 0L);
                    } catch (RemoteException e) {
                        Slog.w("WallpaperManagerService", "Failed to set ambient mode state", e);
                    }
                }
                try {
                    displayConnectorOrCreate.mEngine.requestWallpaperColors();
                } catch (RemoteException e2) {
                    Slog.w("WallpaperManagerService", "Failed to request wallpaper colors", e2);
                }
                List<RectF> areasByDisplayId = WallpaperManagerService.this.mLocalColorRepo.getAreasByDisplayId(i);
                if (areasByDisplayId != null && areasByDisplayId.size() != 0) {
                    try {
                        displayConnectorOrCreate.mEngine.addLocalColorsAreas(areasByDisplayId);
                    } catch (RemoteException e3) {
                        Slog.w("WallpaperManagerService", "Failed to register local colors areas", e3);
                    }
                }
                float f = this.mWallpaper.mWallpaperDimAmount;
                if (f != 0.0f) {
                    try {
                        displayConnectorOrCreate.mEngine.applyDimming(f);
                    } catch (RemoteException e4) {
                        Slog.w("WallpaperManagerService", "Failed to dim wallpaper", e4);
                    }
                }
            }
        }

        public void engineShown(IWallpaperEngine iWallpaperEngine) {
            synchronized (WallpaperManagerService.this.mLock) {
                if (this.mReply != null) {
                    TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog("WallpaperManagerService");
                    timingsTraceAndSlog.traceBegin("WPMS.mReply.sendResult");
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    try {
                        this.mReply.sendResult((Bundle) null);
                    } catch (RemoteException e) {
                        Slog.d("WallpaperManagerService", "failed to send callback!", e);
                    }
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    timingsTraceAndSlog.traceEnd();
                    this.mReply = null;
                }
            }
        }

        public ParcelFileDescriptor setWallpaper(String str) {
            synchronized (WallpaperManagerService.this.mLock) {
                WallpaperData wallpaperData = this.mWallpaper;
                if (wallpaperData.connection == this) {
                    return WallpaperManagerService.this.updateWallpaperBitmapLocked(str, wallpaperData, null);
                }
                return null;
            }
        }
    }

    /* loaded from: classes2.dex */
    public class WallpaperDestinationChangeHandler {
        public final WallpaperData mNewWallpaper;
        public final WallpaperData mOriginalSystem;

        public WallpaperDestinationChangeHandler(WallpaperData wallpaperData) {
            this.mNewWallpaper = wallpaperData;
            this.mOriginalSystem = new WallpaperData((WallpaperData) WallpaperManagerService.this.mWallpaperMap.get(wallpaperData.userId));
        }

        public void complete() {
            WallpaperData wallpaperData = this.mNewWallpaper;
            if (wallpaperData.mSystemWasBoth) {
                int i = wallpaperData.mWhich;
                if (i != 1) {
                    if (i == 2) {
                        WallpaperData wallpaperData2 = (WallpaperData) WallpaperManagerService.this.mWallpaperMap.get(this.mNewWallpaper.userId);
                        if (wallpaperData2.wallpaperId == this.mOriginalSystem.wallpaperId) {
                            wallpaperData2.mWhich = 1;
                            updateEngineFlags(wallpaperData2, 1);
                        }
                    }
                } else if (WallpaperManagerService.this.mImageWallpaper.equals(this.mOriginalSystem.wallpaperComponent)) {
                    WallpaperData wallpaperData3 = (WallpaperData) WallpaperManagerService.this.mLockWallpaperMap.get(this.mNewWallpaper.userId);
                    if (wallpaperData3 != null) {
                        WallpaperData wallpaperData4 = this.mOriginalSystem;
                        wallpaperData3.wallpaperComponent = wallpaperData4.wallpaperComponent;
                        WallpaperConnection wallpaperConnection = wallpaperData4.connection;
                        wallpaperData3.connection = wallpaperConnection;
                        wallpaperConnection.mWallpaper = wallpaperData3;
                        updateEngineFlags(wallpaperData4, 2);
                        WallpaperManagerService.this.notifyWallpaperColorsChanged(wallpaperData3, 2);
                        return;
                    }
                    WallpaperData wallpaperData5 = (WallpaperData) WallpaperManagerService.this.mWallpaperMap.get(this.mNewWallpaper.userId);
                    wallpaperData5.mWhich = 3;
                    updateEngineFlags(wallpaperData5, 3);
                    WallpaperManagerService.this.mLockWallpaperMap.remove(this.mNewWallpaper.userId);
                } else {
                    WallpaperData wallpaperData6 = this.mOriginalSystem;
                    wallpaperData6.mWhich = 2;
                    updateEngineFlags(wallpaperData6, 2);
                    WallpaperManagerService.this.mLockWallpaperMap.put(this.mNewWallpaper.userId, this.mOriginalSystem);
                    WallpaperManagerService wallpaperManagerService = WallpaperManagerService.this;
                    WallpaperData wallpaperData7 = this.mOriginalSystem;
                    wallpaperManagerService.mLastLockWallpaper = wallpaperData7;
                    wallpaperManagerService.notifyWallpaperColorsChanged(wallpaperData7, 2);
                }
            }
        }

        public final void updateEngineFlags(WallpaperData wallpaperData, final int i) {
            WallpaperConnection wallpaperConnection = wallpaperData.connection;
            if (wallpaperConnection == null) {
                return;
            }
            wallpaperConnection.forEachDisplayConnector(new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$WallpaperDestinationChangeHandler$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    WallpaperManagerService.WallpaperDestinationChangeHandler.this.lambda$updateEngineFlags$0(i, (WallpaperManagerService.DisplayConnector) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$updateEngineFlags$0(int i, DisplayConnector displayConnector) {
            try {
                IWallpaperEngine iWallpaperEngine = displayConnector.mEngine;
                if (iWallpaperEngine != null) {
                    iWallpaperEngine.setWallpaperFlags(i);
                    WallpaperManagerService.this.mWindowManagerInternal.setWallpaperShowWhenLocked(displayConnector.mToken, (i & 2) != 0);
                }
            } catch (RemoteException e) {
                Slog.e("WallpaperManagerService", "Failed to update wallpaper engine flags", e);
            }
        }
    }

    /* loaded from: classes2.dex */
    public class MyPackageMonitor extends PackageMonitor {
        public MyPackageMonitor() {
        }

        public void onPackageUpdateFinished(String str, int i) {
            ComponentName componentName;
            synchronized (WallpaperManagerService.this.mLock) {
                if (WallpaperManagerService.this.mCurrentUserId != getChangingUserId()) {
                    return;
                }
                WallpaperData wallpaperData = (WallpaperData) WallpaperManagerService.this.mWallpaperMap.get(WallpaperManagerService.this.mCurrentUserId);
                if (wallpaperData != null && (componentName = wallpaperData.wallpaperComponent) != null && componentName.getPackageName().equals(str)) {
                    Slog.i("WallpaperManagerService", "Wallpaper " + componentName + " update has finished");
                    wallpaperData.wallpaperUpdating = false;
                    WallpaperManagerService.this.clearWallpaperComponentLocked(wallpaperData);
                    if (!WallpaperManagerService.this.bindWallpaperComponentLocked(componentName, false, false, wallpaperData, null)) {
                        Slog.w("WallpaperManagerService", "Wallpaper " + componentName + " no longer available; reverting to default");
                        WallpaperManagerService.this.clearWallpaperLocked(false, 1, wallpaperData.userId, null);
                    }
                }
            }
        }

        public void onPackageModified(String str) {
            synchronized (WallpaperManagerService.this.mLock) {
                if (WallpaperManagerService.this.mCurrentUserId != getChangingUserId()) {
                    return;
                }
                WallpaperData wallpaperData = (WallpaperData) WallpaperManagerService.this.mWallpaperMap.get(WallpaperManagerService.this.mCurrentUserId);
                if (wallpaperData != null) {
                    ComponentName componentName = wallpaperData.wallpaperComponent;
                    if (componentName != null && componentName.getPackageName().equals(str)) {
                        doPackagesChangedLocked(true, wallpaperData);
                    }
                }
            }
        }

        public void onPackageUpdateStarted(String str, int i) {
            ComponentName componentName;
            synchronized (WallpaperManagerService.this.mLock) {
                if (WallpaperManagerService.this.mCurrentUserId != getChangingUserId()) {
                    return;
                }
                WallpaperData wallpaperData = (WallpaperData) WallpaperManagerService.this.mWallpaperMap.get(WallpaperManagerService.this.mCurrentUserId);
                if (wallpaperData != null && (componentName = wallpaperData.wallpaperComponent) != null && componentName.getPackageName().equals(str)) {
                    Slog.i("WallpaperManagerService", "Wallpaper service " + wallpaperData.wallpaperComponent + " is updating");
                    wallpaperData.wallpaperUpdating = true;
                    if (wallpaperData.connection != null) {
                        FgThread.getHandler().removeCallbacks(wallpaperData.connection.mResetRunnable);
                    }
                }
            }
        }

        public boolean onHandleForceStop(Intent intent, String[] strArr, int i, boolean z) {
            synchronized (WallpaperManagerService.this.mLock) {
                if (WallpaperManagerService.this.mCurrentUserId != getChangingUserId()) {
                    return false;
                }
                WallpaperData wallpaperData = (WallpaperData) WallpaperManagerService.this.mWallpaperMap.get(WallpaperManagerService.this.mCurrentUserId);
                return wallpaperData != null ? false | doPackagesChangedLocked(z, wallpaperData) : false;
            }
        }

        public void onSomePackagesChanged() {
            synchronized (WallpaperManagerService.this.mLock) {
                if (WallpaperManagerService.this.mCurrentUserId != getChangingUserId()) {
                    return;
                }
                WallpaperData wallpaperData = (WallpaperData) WallpaperManagerService.this.mWallpaperMap.get(WallpaperManagerService.this.mCurrentUserId);
                if (wallpaperData != null) {
                    doPackagesChangedLocked(true, wallpaperData);
                }
            }
        }

        public boolean doPackagesChangedLocked(boolean z, WallpaperData wallpaperData) {
            boolean z2;
            int isPackageDisappearing;
            int isPackageDisappearing2;
            ComponentName componentName = wallpaperData.wallpaperComponent;
            if (componentName == null || !((isPackageDisappearing2 = isPackageDisappearing(componentName.getPackageName())) == 3 || isPackageDisappearing2 == 2)) {
                z2 = false;
            } else {
                if (z) {
                    Slog.w("WallpaperManagerService", "Wallpaper uninstalled, removing: " + wallpaperData.wallpaperComponent);
                    WallpaperManagerService.this.clearWallpaperLocked(false, 1, wallpaperData.userId, null);
                }
                z2 = true;
            }
            ComponentName componentName2 = wallpaperData.nextWallpaperComponent;
            if (componentName2 != null && ((isPackageDisappearing = isPackageDisappearing(componentName2.getPackageName())) == 3 || isPackageDisappearing == 2)) {
                wallpaperData.nextWallpaperComponent = null;
            }
            ComponentName componentName3 = wallpaperData.wallpaperComponent;
            if (componentName3 != null && isPackageModified(componentName3.getPackageName())) {
                try {
                    WallpaperManagerService.this.mContext.getPackageManager().getServiceInfo(wallpaperData.wallpaperComponent, 786432);
                } catch (PackageManager.NameNotFoundException unused) {
                    Slog.w("WallpaperManagerService", "Wallpaper component gone, removing: " + wallpaperData.wallpaperComponent);
                    WallpaperManagerService.this.clearWallpaperLocked(false, 1, wallpaperData.userId, null);
                }
            }
            ComponentName componentName4 = wallpaperData.nextWallpaperComponent;
            if (componentName4 != null && isPackageModified(componentName4.getPackageName())) {
                try {
                    WallpaperManagerService.this.mContext.getPackageManager().getServiceInfo(wallpaperData.nextWallpaperComponent, 786432);
                } catch (PackageManager.NameNotFoundException unused2) {
                    wallpaperData.nextWallpaperComponent = null;
                }
            }
            return z2;
        }
    }

    @VisibleForTesting
    public WallpaperData getCurrentWallpaperData(int i, int i2) {
        WallpaperData wallpaperData;
        synchronized (this.mLock) {
            wallpaperData = (i == 1 ? this.mWallpaperMap : this.mLockWallpaperMap).get(i2);
        }
        return wallpaperData;
    }

    public WallpaperManagerService(Context context) {
        DisplayManager.DisplayListener displayListener = new DisplayManager.DisplayListener() { // from class: com.android.server.wallpaper.WallpaperManagerService.1
            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayAdded(int i) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayChanged(int i) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayRemoved(int i) {
                WallpaperData wallpaperData;
                synchronized (WallpaperManagerService.this.mLock) {
                    WallpaperData wallpaperData2 = WallpaperManagerService.this.mLastWallpaper;
                    if (wallpaperData2 != null) {
                        if (wallpaperData2.connection.containsDisplay(i)) {
                            wallpaperData = WallpaperManagerService.this.mLastWallpaper;
                        } else {
                            wallpaperData = WallpaperManagerService.this.mFallbackWallpaper.connection.containsDisplay(i) ? WallpaperManagerService.this.mFallbackWallpaper : null;
                        }
                        if (wallpaperData == null) {
                            return;
                        }
                        DisplayConnector displayConnectorOrCreate = wallpaperData.connection.getDisplayConnectorOrCreate(i);
                        if (displayConnectorOrCreate == null) {
                            return;
                        }
                        displayConnectorOrCreate.disconnectLocked(wallpaperData.connection);
                        wallpaperData.connection.removeDisplayConnector(i);
                        WallpaperManagerService.this.mWallpaperDisplayHelper.removeDisplayData(i);
                    }
                    for (int size = WallpaperManagerService.this.mColorsChangedListeners.size() - 1; size >= 0; size--) {
                        ((SparseArray) WallpaperManagerService.this.mColorsChangedListeners.valueAt(size)).delete(i);
                    }
                }
            }
        };
        this.mDisplayListener = displayListener;
        this.mWallpaperMap = new SparseArray<>();
        this.mLockWallpaperMap = new SparseArray<>();
        this.mUserRestorecon = new SparseBooleanArray();
        this.mCurrentUserId = -10000;
        this.mLocalColorRepo = new LocalColorRepository();
        this.mContext = context;
        this.mShuttingDown = false;
        this.mImageWallpaper = ComponentName.unflattenFromString(context.getResources().getString(17040453));
        this.mDefaultWallpaperComponent = WallpaperManager.getDefaultWallpaperComponent(context);
        WindowManagerInternal windowManagerInternal = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
        this.mWindowManagerInternal = windowManagerInternal;
        this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        this.mIPackageManager = AppGlobals.getPackageManager();
        this.mAppOpsManager = (AppOpsManager) context.getSystemService("appops");
        DisplayManager displayManager = (DisplayManager) context.getSystemService(DisplayManager.class);
        displayManager.registerDisplayListener(displayListener, null);
        WallpaperDisplayHelper wallpaperDisplayHelper = new WallpaperDisplayHelper(displayManager, windowManagerInternal);
        this.mWallpaperDisplayHelper = wallpaperDisplayHelper;
        WallpaperCropper wallpaperCropper = new WallpaperCropper(wallpaperDisplayHelper);
        this.mWallpaperCropper = wallpaperCropper;
        this.mActivityManager = (ActivityManager) context.getSystemService(ActivityManager.class);
        this.mMonitor = new MyPackageMonitor();
        this.mColorsChangedListeners = new SparseArray<>();
        boolean z = context.getResources().getBoolean(17891705);
        this.mEnableSeparateLockScreenEngine = z;
        this.mWallpaperDataParser = new WallpaperDataParser(context, wallpaperDisplayHelper, wallpaperCropper, z);
        LocalServices.addService(WallpaperManagerInternal.class, new LocalService());
    }

    /* loaded from: classes2.dex */
    public final class LocalService extends WallpaperManagerInternal {
        public LocalService() {
        }

        @Override // com.android.server.wallpaper.WallpaperManagerInternal
        public void onDisplayReady(int i) {
            WallpaperManagerService.this.onDisplayReadyInternal(i);
        }
    }

    public void initialize() {
        this.mMonitor.register(this.mContext, (Looper) null, UserHandle.ALL, true);
        WallpaperUtils.getWallpaperDir(0).mkdirs();
        loadSettingsLocked(0, false, 3);
        getWallpaperSafeLocked(0, 1);
    }

    public void finalize() throws Throwable {
        super.finalize();
        for (int i = 0; i < this.mWallpaperMap.size(); i++) {
            this.mWallpaperMap.valueAt(i).wallpaperObserver.stopWatching();
        }
    }

    public void systemReady() {
        initialize();
        WallpaperData wallpaperData = this.mWallpaperMap.get(0);
        if (this.mImageWallpaper.equals(wallpaperData.nextWallpaperComponent)) {
            if (!wallpaperData.cropExists()) {
                this.mWallpaperCropper.generateCrop(wallpaperData);
            }
            if (!wallpaperData.cropExists()) {
                clearWallpaperLocked(false, 1, 0, null);
            }
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_REMOVED");
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.wallpaper.WallpaperManagerService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.USER_REMOVED".equals(intent.getAction())) {
                    WallpaperManagerService.this.onRemoveUser(intent.getIntExtra("android.intent.extra.user_handle", -10000));
                }
            }
        }, intentFilter);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.wallpaper.WallpaperManagerService.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.ACTION_SHUTDOWN".equals(intent.getAction())) {
                    synchronized (WallpaperManagerService.this.mLock) {
                        WallpaperManagerService.this.mShuttingDown = true;
                    }
                }
            }
        }, new IntentFilter("android.intent.action.ACTION_SHUTDOWN"));
        try {
            ActivityManager.getService().registerUserSwitchObserver(new UserSwitchObserver() { // from class: com.android.server.wallpaper.WallpaperManagerService.4
                public void onUserSwitching(int i, IRemoteCallback iRemoteCallback) {
                    WallpaperManagerService.this.errorCheck(i);
                    WallpaperManagerService.this.switchUser(i, iRemoteCallback);
                }
            }, "WallpaperManagerService");
        } catch (RemoteException e) {
            e.rethrowAsRuntimeException();
        }
    }

    public String getName() {
        String str;
        if (Binder.getCallingUid() != 1000) {
            throw new RuntimeException("getName() can only be called from the system process");
        }
        synchronized (this.mLock) {
            str = this.mWallpaperMap.get(0).name;
        }
        return str;
    }

    public void stopObserver(WallpaperData wallpaperData) {
        WallpaperObserver wallpaperObserver;
        if (wallpaperData == null || (wallpaperObserver = wallpaperData.wallpaperObserver) == null) {
            return;
        }
        wallpaperObserver.stopWatching();
        wallpaperData.wallpaperObserver = null;
    }

    public void stopObserversLocked(int i) {
        stopObserver(this.mWallpaperMap.get(i));
        stopObserver(this.mLockWallpaperMap.get(i));
        this.mWallpaperMap.remove(i);
        this.mLockWallpaperMap.remove(i);
    }

    @Override // com.android.server.wallpaper.IWallpaperManagerService
    public void onBootPhase(int i) {
        errorCheck(0);
        if (i == 550) {
            systemReady();
        } else if (i == 600) {
            switchUser(0, null);
        }
    }

    public final void errorCheck(final int i) {
        sWallpaperType.forEach(new BiConsumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda5
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                WallpaperManagerService.this.lambda$errorCheck$4(i, (Integer) obj, (String) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$errorCheck$4(int i, Integer num, String str) {
        File file = new File(WallpaperUtils.getWallpaperDir(i), str);
        if (file.exists()) {
            Slog.w("WallpaperManagerService", "User:" + i + ", wallpaper tyep = " + num + ", wallpaper fail detect!! reset to default wallpaper");
            clearWallpaperData(i, num.intValue());
            file.delete();
        }
    }

    public final void clearWallpaperData(int i, int i2) {
        WallpaperData wallpaperData = new WallpaperData(i, i2);
        if (wallpaperData.sourceExists()) {
            wallpaperData.wallpaperFile.delete();
        }
        if (wallpaperData.cropExists()) {
            wallpaperData.cropFile.delete();
        }
    }

    @Override // com.android.server.wallpaper.IWallpaperManagerService
    public void onUnlockUser(final int i) {
        synchronized (this.mLock) {
            if (this.mCurrentUserId == i) {
                if (this.mWaitingForUnlock) {
                    WallpaperData wallpaperSafeLocked = getWallpaperSafeLocked(i, 1);
                    switchWallpaper(wallpaperSafeLocked, null);
                    notifyCallbacksLocked(wallpaperSafeLocked);
                }
                if (!this.mUserRestorecon.get(i)) {
                    this.mUserRestorecon.put(i, true);
                    BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            WallpaperManagerService.lambda$onUnlockUser$5(i);
                        }
                    });
                }
            }
        }
    }

    public static /* synthetic */ void lambda$onUnlockUser$5(int i) {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog("WallpaperManagerService");
        timingsTraceAndSlog.traceBegin("Wallpaper_selinux_restorecon-" + i);
        try {
            for (File file : WallpaperUtils.getWallpaperFiles(i)) {
                if (file.exists()) {
                    SELinux.restorecon(file);
                }
            }
        } finally {
            timingsTraceAndSlog.traceEnd();
        }
    }

    public void onRemoveUser(int i) {
        if (i < 1) {
            return;
        }
        synchronized (this.mLock) {
            stopObserversLocked(i);
            WallpaperUtils.getWallpaperFiles(i).forEach(new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda15
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((File) obj).delete();
                }
            });
            this.mUserRestorecon.delete(i);
        }
    }

    public void switchUser(int i, IRemoteCallback iRemoteCallback) {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog("WallpaperManagerService");
        timingsTraceAndSlog.traceBegin("Wallpaper_switch-user-" + i);
        try {
            synchronized (this.mLock) {
                if (this.mCurrentUserId == i) {
                    return;
                }
                this.mCurrentUserId = i;
                final WallpaperData wallpaperSafeLocked = getWallpaperSafeLocked(i, 1);
                final WallpaperData wallpaperData = this.mLockWallpaperMap.get(i);
                if (wallpaperData == null) {
                    wallpaperData = wallpaperSafeLocked;
                }
                if (wallpaperSafeLocked.wallpaperObserver == null) {
                    WallpaperObserver wallpaperObserver = new WallpaperObserver(wallpaperSafeLocked);
                    wallpaperSafeLocked.wallpaperObserver = wallpaperObserver;
                    wallpaperObserver.startWatching();
                }
                switchWallpaper(wallpaperSafeLocked, iRemoteCallback);
                FgThread.getHandler().post(new Runnable() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda16
                    @Override // java.lang.Runnable
                    public final void run() {
                        WallpaperManagerService.this.lambda$switchUser$6(wallpaperSafeLocked, wallpaperData);
                    }
                });
            }
        } finally {
            timingsTraceAndSlog.traceEnd();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$switchUser$6(WallpaperData wallpaperData, WallpaperData wallpaperData2) {
        notifyWallpaperColorsChanged(wallpaperData, 1);
        notifyWallpaperColorsChanged(wallpaperData2, 2);
        notifyWallpaperColorsChanged(this.mFallbackWallpaper, 1);
    }

    public void switchWallpaper(WallpaperData wallpaperData, IRemoteCallback iRemoteCallback) {
        ServiceInfo serviceInfo;
        synchronized (this.mLock) {
            this.mWaitingForUnlock = false;
            ComponentName componentName = wallpaperData.wallpaperComponent;
            if (componentName == null) {
                componentName = wallpaperData.nextWallpaperComponent;
            }
            if (!bindWallpaperComponentLocked(componentName, true, false, wallpaperData, iRemoteCallback)) {
                try {
                    serviceInfo = this.mIPackageManager.getServiceInfo(componentName, 262144L, wallpaperData.userId);
                } catch (RemoteException unused) {
                    serviceInfo = null;
                }
                if (serviceInfo == null) {
                    Slog.w("WallpaperManagerService", "Failure starting previous wallpaper; clearing");
                    clearWallpaperLocked(false, 1, wallpaperData.userId, iRemoteCallback);
                } else {
                    Slog.w("WallpaperManagerService", "Wallpaper isn't direct boot aware; using fallback until unlocked");
                    wallpaperData.wallpaperComponent = wallpaperData.nextWallpaperComponent;
                    bindWallpaperComponentLocked(this.mImageWallpaper, true, false, new WallpaperData(wallpaperData.userId, 2), iRemoteCallback);
                    this.mWaitingForUnlock = true;
                }
            }
        }
    }

    public void clearWallpaper(String str, int i, int i2) {
        WallpaperData wallpaperData;
        checkPermission("android.permission.SET_WALLPAPER");
        if (isWallpaperSupported(str) && isSetWallpaperAllowed(str)) {
            int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i2, false, true, "clearWallpaper", null);
            synchronized (this.mLock) {
                clearWallpaperLocked(false, i, handleIncomingUser, null);
                wallpaperData = i == 2 ? this.mLockWallpaperMap.get(handleIncomingUser) : null;
                if (i == 1 || wallpaperData == null) {
                    wallpaperData = this.mWallpaperMap.get(handleIncomingUser);
                }
            }
            if (wallpaperData != null) {
                notifyWallpaperColorsChanged(wallpaperData, i);
                notifyWallpaperColorsChanged(this.mFallbackWallpaper, 1);
            }
        }
    }

    public void clearWallpaperLocked(boolean z, int i, int i2, IRemoteCallback iRemoteCallback) {
        WallpaperData wallpaperData;
        if (i != 1 && i != 2) {
            throw new IllegalArgumentException("Must specify exactly one kind of wallpaper to clear");
        }
        if (i == 2) {
            wallpaperData = this.mLockWallpaperMap.get(i2);
            if (wallpaperData == null) {
                return;
            }
        } else {
            WallpaperData wallpaperData2 = this.mWallpaperMap.get(i2);
            if (wallpaperData2 == null) {
                loadSettingsLocked(i2, false, 1);
                wallpaperData = this.mWallpaperMap.get(i2);
            } else {
                wallpaperData = wallpaperData2;
            }
        }
        if (wallpaperData == null) {
            return;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (wallpaperData.wallpaperFile.exists()) {
                wallpaperData.wallpaperFile.delete();
                wallpaperData.cropFile.delete();
                if (i == 2) {
                    this.mLockWallpaperMap.remove(i2);
                    IWallpaperManagerCallback iWallpaperManagerCallback = this.mKeyguardListener;
                    if (iWallpaperManagerCallback != null) {
                        try {
                            iWallpaperManagerCallback.onWallpaperChanged();
                        } catch (RemoteException unused) {
                        }
                    }
                    saveSettingsLocked(i2);
                    return;
                }
            }
            try {
                wallpaperData.primaryColors = null;
                wallpaperData.imageWallpaperPending = false;
            } catch (IllegalArgumentException e) {
                e = e;
            }
            if (i2 != this.mCurrentUserId) {
                return;
            }
            if (bindWallpaperComponentLocked(z ? this.mImageWallpaper : null, true, false, wallpaperData, iRemoteCallback)) {
                return;
            }
            e = null;
            Slog.e("WallpaperManagerService", "Default wallpaper component not found!", e);
            clearWallpaperComponentLocked(wallpaperData);
            if (iRemoteCallback != null) {
                try {
                    iRemoteCallback.sendResult((Bundle) null);
                } catch (RemoteException unused2) {
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final boolean hasCrossUserPermission() {
        return this.mContext.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL") == 0;
    }

    public boolean hasNamedWallpaper(String str) {
        int callingUserId = UserHandle.getCallingUserId();
        boolean hasCrossUserPermission = hasCrossUserPermission();
        synchronized (this.mLock) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            List<UserInfo> users = ((UserManager) this.mContext.getSystemService("user")).getUsers();
            Binder.restoreCallingIdentity(clearCallingIdentity);
            for (UserInfo userInfo : users) {
                if (hasCrossUserPermission || callingUserId == userInfo.id) {
                    if (!userInfo.isManagedProfile()) {
                        WallpaperData wallpaperData = this.mWallpaperMap.get(userInfo.id);
                        if (wallpaperData == null) {
                            loadSettingsLocked(userInfo.id, false, 3);
                            wallpaperData = this.mWallpaperMap.get(userInfo.id);
                        }
                        if (wallpaperData != null && str.equals(wallpaperData.name)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    public void setDimensionHints(int i, int i2, String str, int i3) throws RemoteException {
        checkPermission("android.permission.SET_WALLPAPER_HINTS");
        if (isWallpaperSupported(str)) {
            int min = Math.min(i, GLHelper.getMaxTextureSize());
            int min2 = Math.min(i2, GLHelper.getMaxTextureSize());
            synchronized (this.mLock) {
                int callingUserId = UserHandle.getCallingUserId();
                WallpaperData wallpaperSafeLocked = getWallpaperSafeLocked(callingUserId, 1);
                if (min <= 0 || min2 <= 0) {
                    throw new IllegalArgumentException("width and height must be > 0");
                }
                if (!this.mWallpaperDisplayHelper.isValidDisplay(i3)) {
                    throw new IllegalArgumentException("Cannot find display with id=" + i3);
                }
                WallpaperDisplayHelper.DisplayData displayDataOrCreate = this.mWallpaperDisplayHelper.getDisplayDataOrCreate(i3);
                if (min != displayDataOrCreate.mWidth || min2 != displayDataOrCreate.mHeight) {
                    displayDataOrCreate.mWidth = min;
                    displayDataOrCreate.mHeight = min2;
                    if (i3 == 0) {
                        saveSettingsLocked(callingUserId);
                    }
                    if (this.mCurrentUserId != callingUserId) {
                        return;
                    }
                    WallpaperConnection wallpaperConnection = wallpaperSafeLocked.connection;
                    if (wallpaperConnection != null) {
                        DisplayConnector displayConnectorOrCreate = wallpaperConnection.getDisplayConnectorOrCreate(i3);
                        IWallpaperEngine iWallpaperEngine = displayConnectorOrCreate != null ? displayConnectorOrCreate.mEngine : null;
                        if (iWallpaperEngine != null) {
                            try {
                                iWallpaperEngine.setDesiredSize(min, min2);
                            } catch (RemoteException unused) {
                            }
                            notifyCallbacksLocked(wallpaperSafeLocked);
                        } else if (wallpaperSafeLocked.connection.mService != null && displayConnectorOrCreate != null) {
                            displayConnectorOrCreate.mDimensionsChanged = true;
                        }
                    }
                }
            }
        }
    }

    public int getWidthHint(int i) throws RemoteException {
        synchronized (this.mLock) {
            if (!this.mWallpaperDisplayHelper.isValidDisplay(i)) {
                throw new IllegalArgumentException("Cannot find display with id=" + i);
            } else if (this.mWallpaperMap.get(UserHandle.getCallingUserId()) != null) {
                return this.mWallpaperDisplayHelper.getDisplayDataOrCreate(i).mWidth;
            } else {
                return 0;
            }
        }
    }

    public int getHeightHint(int i) throws RemoteException {
        synchronized (this.mLock) {
            if (!this.mWallpaperDisplayHelper.isValidDisplay(i)) {
                throw new IllegalArgumentException("Cannot find display with id=" + i);
            } else if (this.mWallpaperMap.get(UserHandle.getCallingUserId()) != null) {
                return this.mWallpaperDisplayHelper.getDisplayDataOrCreate(i).mHeight;
            } else {
                return 0;
            }
        }
    }

    public void setDisplayPadding(Rect rect, String str, int i) {
        checkPermission("android.permission.SET_WALLPAPER_HINTS");
        if (isWallpaperSupported(str)) {
            synchronized (this.mLock) {
                if (!this.mWallpaperDisplayHelper.isValidDisplay(i)) {
                    throw new IllegalArgumentException("Cannot find display with id=" + i);
                }
                int callingUserId = UserHandle.getCallingUserId();
                WallpaperData wallpaperSafeLocked = getWallpaperSafeLocked(callingUserId, 1);
                if (rect.left < 0 || rect.top < 0 || rect.right < 0 || rect.bottom < 0) {
                    throw new IllegalArgumentException("padding must be positive: " + rect);
                }
                int maximumSizeDimension = this.mWallpaperDisplayHelper.getMaximumSizeDimension(i);
                int i2 = rect.left + rect.right;
                int i3 = rect.top + rect.bottom;
                if (i2 > maximumSizeDimension) {
                    throw new IllegalArgumentException("padding width " + i2 + " exceeds max width " + maximumSizeDimension);
                } else if (i3 > maximumSizeDimension) {
                    throw new IllegalArgumentException("padding height " + i3 + " exceeds max height " + maximumSizeDimension);
                } else {
                    WallpaperDisplayHelper.DisplayData displayDataOrCreate = this.mWallpaperDisplayHelper.getDisplayDataOrCreate(i);
                    if (!rect.equals(displayDataOrCreate.mPadding)) {
                        displayDataOrCreate.mPadding.set(rect);
                        if (i == 0) {
                            saveSettingsLocked(callingUserId);
                        }
                        if (this.mCurrentUserId != callingUserId) {
                            return;
                        }
                        WallpaperConnection wallpaperConnection = wallpaperSafeLocked.connection;
                        if (wallpaperConnection != null) {
                            DisplayConnector displayConnectorOrCreate = wallpaperConnection.getDisplayConnectorOrCreate(i);
                            IWallpaperEngine iWallpaperEngine = displayConnectorOrCreate != null ? displayConnectorOrCreate.mEngine : null;
                            if (iWallpaperEngine != null) {
                                try {
                                    iWallpaperEngine.setDisplayPadding(rect);
                                } catch (RemoteException unused) {
                                }
                                notifyCallbacksLocked(wallpaperSafeLocked);
                            } else if (wallpaperSafeLocked.connection.mService != null && displayConnectorOrCreate != null) {
                                displayConnectorOrCreate.mPaddingChanged = true;
                            }
                        }
                    }
                }
            }
        }
    }

    @Deprecated
    public ParcelFileDescriptor getWallpaper(String str, IWallpaperManagerCallback iWallpaperManagerCallback, int i, Bundle bundle, int i2) {
        return getWallpaperWithFeature(str, null, iWallpaperManagerCallback, i, bundle, i2, true);
    }

    public ParcelFileDescriptor getWallpaperWithFeature(String str, String str2, IWallpaperManagerCallback iWallpaperManagerCallback, int i, Bundle bundle, int i2, boolean z) {
        if (!hasPermission("android.permission.READ_WALLPAPER_INTERNAL")) {
            ((StorageManager) this.mContext.getSystemService(StorageManager.class)).checkPermissionReadImages(true, Binder.getCallingPid(), Binder.getCallingUid(), str, str2);
        }
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i2, false, true, "getWallpaper", null);
        if (i != 1 && i != 2) {
            throw new IllegalArgumentException("Must specify exactly one kind of wallpaper to read");
        }
        synchronized (this.mLock) {
            WallpaperData wallpaperData = (i == 2 ? this.mLockWallpaperMap : this.mWallpaperMap).get(handleIncomingUser);
            if (wallpaperData == null) {
                return null;
            }
            WallpaperDisplayHelper.DisplayData displayDataOrCreate = this.mWallpaperDisplayHelper.getDisplayDataOrCreate(0);
            if (bundle != null) {
                try {
                    bundle.putInt("width", displayDataOrCreate.mWidth);
                    bundle.putInt("height", displayDataOrCreate.mHeight);
                } catch (FileNotFoundException e) {
                    Slog.w("WallpaperManagerService", "Error getting wallpaper", e);
                    return null;
                }
            }
            if (iWallpaperManagerCallback != null) {
                wallpaperData.callbacks.register(iWallpaperManagerCallback);
            }
            File file = z ? wallpaperData.cropFile : wallpaperData.wallpaperFile;
            if (file.exists()) {
                return ParcelFileDescriptor.open(file, 268435456);
            }
            return null;
        }
    }

    public final boolean hasPermission(String str) {
        return this.mContext.checkCallingOrSelfPermission(str) == 0;
    }

    public WallpaperInfo getWallpaperInfo(int i) {
        return getWallpaperInfoWithFlags(1, i);
    }

    public WallpaperInfo getWallpaperInfoWithFlags(int i, int i2) {
        WallpaperData wallpaperData;
        WallpaperConnection wallpaperConnection;
        WallpaperInfo wallpaperInfo;
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i2, false, true, "getWallpaperInfo", null);
        synchronized (this.mLock) {
            if (i == 2) {
                wallpaperData = this.mLockWallpaperMap.get(handleIncomingUser);
            } else {
                wallpaperData = this.mWallpaperMap.get(handleIncomingUser);
            }
            if (wallpaperData != null && (wallpaperConnection = wallpaperData.connection) != null && (wallpaperInfo = wallpaperConnection.mInfo) != null) {
                if (!hasPermission("android.permission.READ_WALLPAPER_INTERNAL") && !this.mPackageManagerInternal.canQueryPackage(Binder.getCallingUid(), wallpaperInfo.getComponent().getPackageName())) {
                    return null;
                }
                return wallpaperInfo;
            }
            return null;
        }
    }

    public ParcelFileDescriptor getWallpaperInfoFile(int i) {
        synchronized (this.mLock) {
            try {
                try {
                    File file = new File(WallpaperUtils.getWallpaperDir(i), "wallpaper_info.xml");
                    if (file.exists()) {
                        return ParcelFileDescriptor.open(file, 268435456);
                    }
                    return null;
                } catch (FileNotFoundException e) {
                    Slog.w("WallpaperManagerService", "Error getting wallpaper info file", e);
                    return null;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public int getWallpaperIdForUser(int i, int i2) {
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i2, false, true, "getWallpaperIdForUser", null);
        if (i != 1 && i != 2) {
            throw new IllegalArgumentException("Must specify exactly one kind of wallpaper");
        }
        SparseArray<WallpaperData> sparseArray = i == 2 ? this.mLockWallpaperMap : this.mWallpaperMap;
        synchronized (this.mLock) {
            WallpaperData wallpaperData = sparseArray.get(handleIncomingUser);
            if (wallpaperData != null) {
                return wallpaperData.wallpaperId;
            }
            return -1;
        }
    }

    public void registerWallpaperColorsCallback(IWallpaperManagerCallback iWallpaperManagerCallback, int i, int i2) {
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, true, true, "registerWallpaperColorsCallback", null);
        synchronized (this.mLock) {
            SparseArray<RemoteCallbackList<IWallpaperManagerCallback>> sparseArray = this.mColorsChangedListeners.get(handleIncomingUser);
            if (sparseArray == null) {
                sparseArray = new SparseArray<>();
                this.mColorsChangedListeners.put(handleIncomingUser, sparseArray);
            }
            RemoteCallbackList<IWallpaperManagerCallback> remoteCallbackList = sparseArray.get(i2);
            if (remoteCallbackList == null) {
                remoteCallbackList = new RemoteCallbackList<>();
                sparseArray.put(i2, remoteCallbackList);
            }
            remoteCallbackList.register(iWallpaperManagerCallback);
        }
    }

    public void unregisterWallpaperColorsCallback(IWallpaperManagerCallback iWallpaperManagerCallback, int i, int i2) {
        RemoteCallbackList<IWallpaperManagerCallback> remoteCallbackList;
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, true, true, "unregisterWallpaperColorsCallback", null);
        synchronized (this.mLock) {
            SparseArray<RemoteCallbackList<IWallpaperManagerCallback>> sparseArray = this.mColorsChangedListeners.get(handleIncomingUser);
            if (sparseArray != null && (remoteCallbackList = sparseArray.get(i2)) != null) {
                remoteCallbackList.unregister(iWallpaperManagerCallback);
            }
        }
    }

    public void setInAmbientMode(boolean z, long j) {
        IWallpaperEngine iWallpaperEngine;
        WallpaperConnection wallpaperConnection;
        WallpaperInfo wallpaperInfo;
        synchronized (this.mLock) {
            this.mInAmbientMode = z;
            WallpaperData wallpaperData = this.mWallpaperMap.get(this.mCurrentUserId);
            iWallpaperEngine = (wallpaperData == null || (wallpaperConnection = wallpaperData.connection) == null || !((wallpaperInfo = wallpaperConnection.mInfo) == null || wallpaperInfo.supportsAmbientMode())) ? null : wallpaperData.connection.getDisplayConnectorOrCreate(0).mEngine;
        }
        if (iWallpaperEngine != null) {
            try {
                iWallpaperEngine.setInAmbientMode(z, j);
            } catch (RemoteException unused) {
            }
        }
    }

    public void notifyWakingUp(final int i, final int i2, final Bundle bundle) {
        WallpaperConnection wallpaperConnection;
        synchronized (this.mLock) {
            WallpaperData wallpaperData = this.mWallpaperMap.get(this.mCurrentUserId);
            if (wallpaperData != null && (wallpaperConnection = wallpaperData.connection) != null) {
                wallpaperConnection.forEachDisplayConnector(new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda11
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        WallpaperManagerService.lambda$notifyWakingUp$7(i, i2, bundle, (WallpaperManagerService.DisplayConnector) obj);
                    }
                });
            }
        }
    }

    public static /* synthetic */ void lambda$notifyWakingUp$7(int i, int i2, Bundle bundle, DisplayConnector displayConnector) {
        IWallpaperEngine iWallpaperEngine = displayConnector.mEngine;
        if (iWallpaperEngine != null) {
            try {
                iWallpaperEngine.dispatchWallpaperCommand("android.wallpaper.wakingup", i, i2, -1, bundle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyGoingToSleep(final int i, final int i2, final Bundle bundle) {
        WallpaperConnection wallpaperConnection;
        synchronized (this.mLock) {
            WallpaperData wallpaperData = this.mWallpaperMap.get(this.mCurrentUserId);
            if (wallpaperData != null && (wallpaperConnection = wallpaperData.connection) != null) {
                wallpaperConnection.forEachDisplayConnector(new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda6
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        WallpaperManagerService.lambda$notifyGoingToSleep$8(i, i2, bundle, (WallpaperManagerService.DisplayConnector) obj);
                    }
                });
            }
        }
    }

    public static /* synthetic */ void lambda$notifyGoingToSleep$8(int i, int i2, Bundle bundle, DisplayConnector displayConnector) {
        IWallpaperEngine iWallpaperEngine = displayConnector.mEngine;
        if (iWallpaperEngine != null) {
            try {
                iWallpaperEngine.dispatchWallpaperCommand("android.wallpaper.goingtosleep", i, i2, -1, bundle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean setLockWallpaperCallback(IWallpaperManagerCallback iWallpaperManagerCallback) {
        checkPermission("android.permission.INTERNAL_SYSTEM_WINDOW");
        synchronized (this.mLock) {
            this.mKeyguardListener = iWallpaperManagerCallback;
        }
        return true;
    }

    public final IWallpaperEngine getEngine(int i, int i2, int i3) {
        WallpaperConnection wallpaperConnection;
        WallpaperData findWallpaperAtDisplay = findWallpaperAtDisplay(i2, i3);
        IWallpaperEngine iWallpaperEngine = null;
        if (findWallpaperAtDisplay == null || (wallpaperConnection = findWallpaperAtDisplay.connection) == null) {
            return null;
        }
        synchronized (this.mLock) {
            for (int i4 = 0; i4 < wallpaperConnection.mDisplayConnector.size(); i4++) {
                int i5 = ((DisplayConnector) wallpaperConnection.mDisplayConnector.get(i4)).mDisplayId;
                int i6 = ((DisplayConnector) wallpaperConnection.mDisplayConnector.get(i4)).mDisplayId;
                if (i5 == i3 || i6 == i) {
                    iWallpaperEngine = ((DisplayConnector) wallpaperConnection.mDisplayConnector.get(i4)).mEngine;
                    break;
                }
            }
        }
        return iWallpaperEngine;
    }

    public void addOnLocalColorsChangedListener(ILocalWallpaperColorConsumer iLocalWallpaperColorConsumer, List<RectF> list, int i, int i2, int i3) throws RemoteException {
        if (i != 2 && i != 1) {
            throw new IllegalArgumentException("which should be either FLAG_LOCK or FLAG_SYSTEM");
        }
        IWallpaperEngine engine = getEngine(i, i2, i3);
        if (engine == null) {
            return;
        }
        synchronized (this.mLock) {
            this.mLocalColorRepo.addAreas(iLocalWallpaperColorConsumer, list, i3);
        }
        engine.addLocalColorsAreas(list);
    }

    public void removeOnLocalColorsChangedListener(ILocalWallpaperColorConsumer iLocalWallpaperColorConsumer, List<RectF> list, int i, int i2, int i3) throws RemoteException {
        if (i != 2 && i != 1) {
            throw new IllegalArgumentException("which should be either FLAG_LOCK or FLAG_SYSTEM");
        }
        if (Binder.getCallingUserHandle().getIdentifier() != i2) {
            throw new SecurityException("calling user id does not match");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        List<RectF> list2 = null;
        try {
            synchronized (this.mLock) {
                list2 = this.mLocalColorRepo.removeAreas(iLocalWallpaperColorConsumer, list, i3);
            }
        } catch (Exception unused) {
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
        Binder.restoreCallingIdentity(clearCallingIdentity);
        IWallpaperEngine engine = getEngine(i, i2, i3);
        if (engine == null || list2 == null || list2.size() <= 0) {
            return;
        }
        engine.removeLocalColorsAreas(list2);
    }

    public boolean lockScreenWallpaperExists() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mLockWallpaperMap.get(this.mCurrentUserId) != null;
        }
        return z;
    }

    public void setWallpaperDimAmount(float f) throws RemoteException {
        setWallpaperDimAmountForUid(Binder.getCallingUid(), f);
    }

    public void setWallpaperDimAmountForUid(int i, float f) {
        checkPermission("android.permission.SET_WALLPAPER_DIM_AMOUNT");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mLock) {
                WallpaperData wallpaperData = this.mWallpaperMap.get(this.mCurrentUserId);
                WallpaperData wallpaperData2 = this.mLockWallpaperMap.get(this.mCurrentUserId);
                if (f == 0.0f) {
                    wallpaperData.mUidToDimAmount.remove(i);
                } else {
                    wallpaperData.mUidToDimAmount.put(i, Float.valueOf(f));
                }
                final float highestDimAmountFromMap = getHighestDimAmountFromMap(wallpaperData.mUidToDimAmount);
                wallpaperData.mWallpaperDimAmount = highestDimAmountFromMap;
                if (wallpaperData2 != null) {
                    wallpaperData2.mWallpaperDimAmount = highestDimAmountFromMap;
                }
                WallpaperConnection wallpaperConnection = wallpaperData.connection;
                if (wallpaperConnection != null) {
                    wallpaperConnection.forEachDisplayConnector(new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda3
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            WallpaperManagerService.lambda$setWallpaperDimAmountForUid$9(highestDimAmountFromMap, (WallpaperManagerService.DisplayConnector) obj);
                        }
                    });
                    wallpaperData.mIsColorExtractedFromDim = true;
                    notifyWallpaperColorsChanged(wallpaperData, 1);
                    if (wallpaperData2 != null) {
                        wallpaperData2.mIsColorExtractedFromDim = true;
                        notifyWallpaperColorsChanged(wallpaperData2, 2);
                    }
                    saveSettingsLocked(wallpaperData.userId);
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static /* synthetic */ void lambda$setWallpaperDimAmountForUid$9(float f, DisplayConnector displayConnector) {
        IWallpaperEngine iWallpaperEngine = displayConnector.mEngine;
        if (iWallpaperEngine != null) {
            try {
                iWallpaperEngine.applyDimming(f);
            } catch (RemoteException e) {
                Slog.w("WallpaperManagerService", "Can't apply dimming on wallpaper display connector", e);
            }
        }
    }

    public float getWallpaperDimAmount() {
        checkPermission("android.permission.SET_WALLPAPER_DIM_AMOUNT");
        synchronized (this.mLock) {
            WallpaperData wallpaperData = this.mWallpaperMap.get(this.mCurrentUserId);
            if (wallpaperData == null && (wallpaperData = this.mWallpaperMap.get(0)) == null) {
                Slog.e("WallpaperManagerService", "getWallpaperDimAmount: wallpaperData is null");
                return 0.0f;
            }
            return wallpaperData.mWallpaperDimAmount;
        }
    }

    public final float getHighestDimAmountFromMap(SparseArray<Float> sparseArray) {
        float f = 0.0f;
        for (int i = 0; i < sparseArray.size(); i++) {
            f = Math.max(f, sparseArray.valueAt(i).floatValue());
        }
        return f;
    }

    public WallpaperColors getWallpaperColors(int i, int i2, int i3) throws RemoteException {
        WallpaperData wallpaperData;
        boolean z = true;
        if (i != 2 && i != 1) {
            throw new IllegalArgumentException("which should be either FLAG_LOCK or FLAG_SYSTEM");
        }
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i2, false, true, "getWallpaperColors", null);
        synchronized (this.mLock) {
            if (i == 2) {
                try {
                    wallpaperData = this.mLockWallpaperMap.get(handleIncomingUser);
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                wallpaperData = null;
            }
            if (wallpaperData == null) {
                wallpaperData = findWallpaperAtDisplay(handleIncomingUser, i3);
            }
            if (wallpaperData == null) {
                return null;
            }
            if (wallpaperData.primaryColors != null && !wallpaperData.mIsColorExtractedFromDim) {
                z = false;
            }
            if (z) {
                extractColors(wallpaperData);
            }
            return getAdjustedWallpaperColorsOnDimming(wallpaperData);
        }
    }

    public WallpaperColors getAdjustedWallpaperColorsOnDimming(WallpaperData wallpaperData) {
        synchronized (this.mLock) {
            WallpaperColors wallpaperColors = wallpaperData.primaryColors;
            if (wallpaperColors == null || (wallpaperColors.getColorHints() & 4) != 0 || wallpaperData.mWallpaperDimAmount == 0.0f) {
                return wallpaperColors;
            }
            return new WallpaperColors(wallpaperColors.getPrimaryColor(), wallpaperColors.getSecondaryColor(), wallpaperColors.getTertiaryColor(), wallpaperColors.getColorHints() & (-2) & (-3));
        }
    }

    public final WallpaperData findWallpaperAtDisplay(int i, int i2) {
        WallpaperConnection wallpaperConnection;
        WallpaperData wallpaperData = this.mFallbackWallpaper;
        if (wallpaperData != null && (wallpaperConnection = wallpaperData.connection) != null && wallpaperConnection.containsDisplay(i2)) {
            return this.mFallbackWallpaper;
        }
        return this.mWallpaperMap.get(i);
    }

    public ParcelFileDescriptor setWallpaper(String str, final String str2, Rect rect, boolean z, Bundle bundle, int i, IWallpaperManagerCallback iWallpaperManagerCallback, int i2) {
        ParcelFileDescriptor updateWallpaperBitmapLocked;
        int handleIncomingUser = ActivityManager.handleIncomingUser(IWallpaperManager.Stub.getCallingPid(), IWallpaperManager.Stub.getCallingUid(), i2, false, true, "changing wallpaper", null);
        checkPermission("android.permission.SET_WALLPAPER");
        if ((i & 3) == 0) {
            Slog.e("WallpaperManagerService", "Must specify a valid wallpaper category to set");
            throw new IllegalArgumentException("Must specify a valid wallpaper category to set");
        } else if (isWallpaperSupported(str2) && isSetWallpaperAllowed(str2)) {
            if (rect == null) {
                rect = new Rect(0, 0, 0, 0);
            } else if (rect.width() < 0 || rect.height() < 0 || rect.left < 0 || rect.top < 0) {
                throw new IllegalArgumentException("Invalid crop rect supplied: " + rect);
            }
            boolean booleanValue = ((Boolean) Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda10
                public final Object getOrThrow() {
                    Boolean lambda$setWallpaper$10;
                    lambda$setWallpaper$10 = WallpaperManagerService.this.lambda$setWallpaper$10(str2);
                    return lambda$setWallpaper$10;
                }
            })).booleanValue();
            synchronized (this.mLock) {
                WallpaperData wallpaperData = this.mWallpaperMap.get(handleIncomingUser);
                boolean z2 = wallpaperData != null && this.mImageWallpaper.equals(wallpaperData.wallpaperComponent);
                boolean z3 = this.mLockWallpaperMap.get(handleIncomingUser) == null;
                if (i == 1 && z2 && z3) {
                    Slog.i("WallpaperManagerService", "Migrating current wallpaper to be lock-only before updating system wallpaper");
                    migrateStaticSystemToLockWallpaperLocked(handleIncomingUser);
                }
                WallpaperData wallpaperSafeLocked = getWallpaperSafeLocked(handleIncomingUser, i);
                if (this.mPendingMigrationViaStatic != null) {
                    Slog.w("WallpaperManagerService", "Starting new static wp migration before previous migration finished");
                }
                this.mPendingMigrationViaStatic = new WallpaperDestinationChangeHandler(wallpaperSafeLocked);
                long clearCallingIdentity = Binder.clearCallingIdentity();
                updateWallpaperBitmapLocked = updateWallpaperBitmapLocked(str, wallpaperSafeLocked, bundle);
                if (updateWallpaperBitmapLocked != null) {
                    wallpaperSafeLocked.imageWallpaperPending = true;
                    wallpaperSafeLocked.mSystemWasBoth = z3;
                    wallpaperSafeLocked.mWhich = i;
                    wallpaperSafeLocked.setComplete = iWallpaperManagerCallback;
                    wallpaperSafeLocked.fromForegroundApp = booleanValue;
                    wallpaperSafeLocked.cropHint.set(rect);
                    wallpaperSafeLocked.allowBackup = z;
                    wallpaperSafeLocked.mWallpaperDimAmount = getWallpaperDimAmount();
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
            return updateWallpaperBitmapLocked;
        } else {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$setWallpaper$10(String str) throws Exception {
        return Boolean.valueOf(this.mActivityManager.getPackageImportance(str) == 100);
    }

    public final void migrateStaticSystemToLockWallpaperLocked(int i) {
        WallpaperData wallpaperData = this.mWallpaperMap.get(i);
        if (wallpaperData == null) {
            return;
        }
        WallpaperData wallpaperData2 = new WallpaperData(i, 2);
        wallpaperData2.wallpaperId = wallpaperData.wallpaperId;
        wallpaperData2.cropHint.set(wallpaperData.cropHint);
        wallpaperData2.allowBackup = wallpaperData.allowBackup;
        wallpaperData2.primaryColors = wallpaperData.primaryColors;
        wallpaperData2.mWallpaperDimAmount = wallpaperData.mWallpaperDimAmount;
        wallpaperData2.mWhich = 2;
        try {
            Os.rename(wallpaperData.wallpaperFile.getAbsolutePath(), wallpaperData2.wallpaperFile.getAbsolutePath());
            Os.rename(wallpaperData.cropFile.getAbsolutePath(), wallpaperData2.cropFile.getAbsolutePath());
            this.mLockWallpaperMap.put(i, wallpaperData2);
            if (this.mEnableSeparateLockScreenEngine) {
                SELinux.restorecon(wallpaperData2.wallpaperFile);
                this.mLastLockWallpaper = wallpaperData2;
            }
        } catch (ErrnoException e) {
            Slog.e("WallpaperManagerService", "Can't migrate system wallpaper: " + e.getMessage());
            wallpaperData2.wallpaperFile.delete();
            wallpaperData2.cropFile.delete();
        }
    }

    public ParcelFileDescriptor updateWallpaperBitmapLocked(String str, WallpaperData wallpaperData, Bundle bundle) {
        if (str == null) {
            str = "";
        }
        try {
            File wallpaperDir = WallpaperUtils.getWallpaperDir(wallpaperData.userId);
            if (!wallpaperDir.exists()) {
                wallpaperDir.mkdir();
                FileUtils.setPermissions(wallpaperDir.getPath(), 505, -1, -1);
            }
            ParcelFileDescriptor open = ParcelFileDescriptor.open(wallpaperData.wallpaperFile, 1006632960);
            if (!SELinux.restorecon(wallpaperData.wallpaperFile)) {
                Slog.w("WallpaperManagerService", "restorecon failed for wallpaper file: " + wallpaperData.wallpaperFile.getPath());
                return null;
            }
            wallpaperData.name = str;
            int makeWallpaperIdLocked = WallpaperUtils.makeWallpaperIdLocked();
            wallpaperData.wallpaperId = makeWallpaperIdLocked;
            if (bundle != null) {
                bundle.putInt("android.service.wallpaper.extra.ID", makeWallpaperIdLocked);
            }
            wallpaperData.primaryColors = null;
            Slog.v("WallpaperManagerService", "updateWallpaperBitmapLocked() : id=" + wallpaperData.wallpaperId + " name=" + str + " file=" + wallpaperData.wallpaperFile.getName());
            return open;
        } catch (FileNotFoundException e) {
            Slog.w("WallpaperManagerService", "Error setting wallpaper", e);
            return null;
        }
    }

    public void setWallpaperComponentChecked(ComponentName componentName, String str, int i, int i2) {
        if (isWallpaperSupported(str) && isSetWallpaperAllowed(str)) {
            setWallpaperComponent(componentName, i, i2);
        }
    }

    public void setWallpaperComponent(ComponentName componentName) {
        setWallpaperComponent(componentName, UserHandle.getCallingUserId(), 1);
    }

    @VisibleForTesting
    public void setWallpaperComponent(ComponentName componentName, int i, int i2) {
        if (this.mEnableSeparateLockScreenEngine) {
            setWallpaperComponentInternal(componentName, i, i2);
        } else {
            setWallpaperComponentInternalLegacy(componentName, i, i2);
        }
    }

    public final void setWallpaperComponentInternal(ComponentName componentName, int i, int i2) {
        boolean z;
        WallpaperData wallpaperSafeLocked;
        int handleIncomingUser = ActivityManager.handleIncomingUser(IWallpaperManager.Stub.getCallingPid(), IWallpaperManager.Stub.getCallingUid(), i2, false, true, "changing live wallpaper", null);
        checkPermission("android.permission.SET_WALLPAPER_COMPONENT");
        synchronized (this.mLock) {
            Slog.v("WallpaperManagerService", "setWallpaperComponent name=" + componentName);
            WallpaperData wallpaperData = this.mWallpaperMap.get(handleIncomingUser);
            if (wallpaperData == null) {
                throw new IllegalStateException("Wallpaper not yet initialized for user " + handleIncomingUser);
            }
            boolean equals = this.mImageWallpaper.equals(wallpaperData.wallpaperComponent);
            z = false;
            boolean z2 = this.mLockWallpaperMap.get(handleIncomingUser) == null;
            if (i == 1 && z2 && equals) {
                Slog.i("WallpaperManagerService", "Migrating current wallpaper to be lock-only beforeupdating system wallpaper");
                migrateStaticSystemToLockWallpaperLocked(handleIncomingUser);
            }
            wallpaperSafeLocked = getWallpaperSafeLocked(handleIncomingUser, i);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            wallpaperSafeLocked.imageWallpaperPending = false;
            wallpaperSafeLocked.mWhich = i;
            wallpaperSafeLocked.mSystemWasBoth = z2;
            final WallpaperDestinationChangeHandler wallpaperDestinationChangeHandler = new WallpaperDestinationChangeHandler(wallpaperSafeLocked);
            boolean changingToSame = changingToSame(componentName, wallpaperSafeLocked);
            if (bindWallpaperComponentLocked(componentName, changingToSame && z2 && i == 1, true, wallpaperSafeLocked, new IRemoteCallback.Stub() { // from class: com.android.server.wallpaper.WallpaperManagerService.5
                public void sendResult(Bundle bundle) throws RemoteException {
                    wallpaperDestinationChangeHandler.complete();
                }
            })) {
                if (!changingToSame) {
                    wallpaperSafeLocked.primaryColors = null;
                } else {
                    WallpaperConnection wallpaperConnection = wallpaperSafeLocked.connection;
                    if (wallpaperConnection != null) {
                        wallpaperConnection.forEachDisplayConnector(new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda2
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                WallpaperManagerService.lambda$setWallpaperComponentInternal$11((WallpaperManagerService.DisplayConnector) obj);
                            }
                        });
                    }
                }
                wallpaperSafeLocked.wallpaperId = WallpaperUtils.makeWallpaperIdLocked();
                notifyCallbacksLocked(wallpaperSafeLocked);
                if (i == 3) {
                    WallpaperData wallpaperData2 = this.mLockWallpaperMap.get(wallpaperSafeLocked.userId);
                    if (wallpaperData2 != null) {
                        detachWallpaperLocked(wallpaperData2);
                    }
                    this.mLockWallpaperMap.remove(wallpaperSafeLocked.userId);
                }
                z = true;
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
        if (z) {
            notifyWallpaperColorsChanged(wallpaperSafeLocked, i);
            notifyWallpaperColorsChanged(this.mFallbackWallpaper, 1);
        }
    }

    public static /* synthetic */ void lambda$setWallpaperComponentInternal$11(DisplayConnector displayConnector) {
        try {
            IWallpaperEngine iWallpaperEngine = displayConnector.mEngine;
            if (iWallpaperEngine != null) {
                iWallpaperEngine.dispatchWallpaperCommand("android.wallpaper.reapply", 0, 0, 0, (Bundle) null);
            }
        } catch (RemoteException e) {
            Slog.w("WallpaperManagerService", "Error sending apply message to wallpaper", e);
        }
    }

    public final void setWallpaperComponentInternalLegacy(ComponentName componentName, int i, int i2) {
        WallpaperData wallpaperData;
        int i3;
        boolean z;
        int handleIncomingUser = ActivityManager.handleIncomingUser(IWallpaperManager.Stub.getCallingPid(), IWallpaperManager.Stub.getCallingUid(), i2, false, true, "changing live wallpaper", null);
        checkPermission("android.permission.SET_WALLPAPER_COMPONENT");
        synchronized (this.mLock) {
            Slog.v("WallpaperManagerService", "setWallpaperComponent name=" + componentName);
            wallpaperData = this.mWallpaperMap.get(handleIncomingUser);
            if (wallpaperData == null) {
                throw new IllegalStateException("Wallpaper not yet initialized for user " + handleIncomingUser);
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            if (this.mImageWallpaper.equals(wallpaperData.wallpaperComponent) && this.mLockWallpaperMap.get(handleIncomingUser) == null) {
                Slog.i("WallpaperManagerService", "Migrating current wallpaper to be lock-only beforeupdating system wallpaper");
                migrateStaticSystemToLockWallpaperLocked(handleIncomingUser);
            }
            i3 = this.mLockWallpaperMap.get(handleIncomingUser) == null ? 3 : 1;
            z = false;
            wallpaperData.imageWallpaperPending = false;
            wallpaperData.mWhich = i;
            boolean changingToSame = changingToSame(componentName, wallpaperData);
            if (bindWallpaperComponentLocked(componentName, false, true, wallpaperData, null)) {
                if (!changingToSame) {
                    wallpaperData.primaryColors = null;
                } else {
                    WallpaperConnection wallpaperConnection = wallpaperData.connection;
                    if (wallpaperConnection != null) {
                        wallpaperConnection.forEachDisplayConnector(new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda4
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                WallpaperManagerService.lambda$setWallpaperComponentInternalLegacy$12((WallpaperManagerService.DisplayConnector) obj);
                            }
                        });
                    }
                }
                wallpaperData.wallpaperId = WallpaperUtils.makeWallpaperIdLocked();
                notifyCallbacksLocked(wallpaperData);
                z = true;
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
        if (z) {
            notifyWallpaperColorsChanged(wallpaperData, i3);
            notifyWallpaperColorsChanged(this.mFallbackWallpaper, 1);
        }
    }

    public static /* synthetic */ void lambda$setWallpaperComponentInternalLegacy$12(DisplayConnector displayConnector) {
        try {
            IWallpaperEngine iWallpaperEngine = displayConnector.mEngine;
            if (iWallpaperEngine != null) {
                iWallpaperEngine.dispatchWallpaperCommand("android.wallpaper.reapply", 0, 0, 0, (Bundle) null);
            }
        } catch (RemoteException e) {
            Slog.w("WallpaperManagerService", "Error sending apply message to wallpaper", e);
        }
    }

    public final boolean changingToSame(ComponentName componentName, WallpaperData wallpaperData) {
        if (wallpaperData.connection != null) {
            ComponentName componentName2 = wallpaperData.wallpaperComponent;
            return componentName2 == null ? componentName == null : componentName2.equals(componentName);
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:119:0x026f A[Catch: all -> 0x0250, TRY_LEAVE, TryCatch #0 {all -> 0x0250, blocks: (B:9:0x0042, B:11:0x0046, B:12:0x004d, B:14:0x0059, B:17:0x0076, B:19:0x0080, B:21:0x0093, B:24:0x009a, B:25:0x009f, B:26:0x00a0, B:28:0x00ab, B:30:0x00b3, B:31:0x00cc, B:33:0x00d2, B:35:0x00e4, B:37:0x00ee, B:41:0x0101, B:44:0x0108, B:45:0x010d, B:48:0x0111, B:51:0x0118, B:52:0x011d, B:53:0x011e, B:55:0x0123, B:57:0x0136, B:60:0x013d, B:61:0x0142, B:64:0x0147, B:66:0x014d, B:68:0x015b, B:70:0x016e, B:73:0x0175, B:74:0x017a, B:75:0x017b, B:77:0x01b8, B:79:0x01e1, B:82:0x01f5, B:117:0x0254, B:119:0x026f, B:122:0x0276, B:123:0x027b, B:86:0x01fd, B:87:0x0202, B:88:0x0203, B:90:0x0208, B:98:0x0223, B:100:0x022f, B:106:0x0243, B:101:0x0233, B:103:0x0239, B:105:0x0241, B:91:0x020c, B:93:0x0212, B:95:0x0216, B:97:0x021e), top: B:126:0x0040 }] */
    /* JADX WARN: Removed duplicated region for block: B:122:0x0276 A[Catch: all -> 0x0250, TRY_ENTER, TryCatch #0 {all -> 0x0250, blocks: (B:9:0x0042, B:11:0x0046, B:12:0x004d, B:14:0x0059, B:17:0x0076, B:19:0x0080, B:21:0x0093, B:24:0x009a, B:25:0x009f, B:26:0x00a0, B:28:0x00ab, B:30:0x00b3, B:31:0x00cc, B:33:0x00d2, B:35:0x00e4, B:37:0x00ee, B:41:0x0101, B:44:0x0108, B:45:0x010d, B:48:0x0111, B:51:0x0118, B:52:0x011d, B:53:0x011e, B:55:0x0123, B:57:0x0136, B:60:0x013d, B:61:0x0142, B:64:0x0147, B:66:0x014d, B:68:0x015b, B:70:0x016e, B:73:0x0175, B:74:0x017a, B:75:0x017b, B:77:0x01b8, B:79:0x01e1, B:82:0x01f5, B:117:0x0254, B:119:0x026f, B:122:0x0276, B:123:0x027b, B:86:0x01fd, B:87:0x0202, B:88:0x0203, B:90:0x0208, B:98:0x0223, B:100:0x022f, B:106:0x0243, B:101:0x0233, B:103:0x0239, B:105:0x0241, B:91:0x020c, B:93:0x0212, B:95:0x0216, B:97:0x021e), top: B:126:0x0040 }] */
    /* JADX WARN: Type inference failed for: r3v11, types: [boolean] */
    /* JADX WARN: Type inference failed for: r3v3 */
    /* JADX WARN: Type inference failed for: r3v6 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean bindWallpaperComponentLocked(ComponentName componentName, boolean z, boolean z2, WallpaperData wallpaperData, IRemoteCallback iRemoteCallback) {
        String str;
        WallpaperConnection wallpaperConnection;
        ComponentName componentName2 = componentName;
        Slog.v("WallpaperManagerService", "bindWallpaperComponentLocked: componentName=" + componentName2);
        if (!z && changingToSame(componentName2, wallpaperData)) {
            return true;
        }
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog("WallpaperManagerService");
        timingsTraceAndSlog.traceBegin("WPMS.bindWallpaperComponentLocked-" + componentName2);
        try {
            if (componentName2 == null) {
                try {
                    componentName2 = this.mDefaultWallpaperComponent;
                    if (componentName2 == null) {
                        componentName2 = this.mImageWallpaper;
                        Slog.v("WallpaperManagerService", "No default component; using image wallpaper");
                    }
                } catch (RemoteException e) {
                    e = e;
                    str = "WallpaperManagerService";
                    String str2 = "Remote exception for " + componentName2 + "\n" + e;
                    if (z2) {
                        Slog.w(str, str2);
                        return false;
                    }
                    throw new IllegalArgumentException(str2);
                }
            }
            int i = wallpaperData.userId;
            ServiceInfo serviceInfo = this.mIPackageManager.getServiceInfo(componentName2, 4224L, i);
            if (serviceInfo == null) {
                Slog.w("WallpaperManagerService", "Attempted wallpaper " + componentName2 + " is unavailable");
                return false;
            } else if (!"android.permission.BIND_WALLPAPER".equals(serviceInfo.permission)) {
                String str3 = "Selected service does not have android.permission.BIND_WALLPAPER: " + componentName2;
                if (z2) {
                    throw new SecurityException(str3);
                }
                Slog.w("WallpaperManagerService", str3);
                return false;
            } else {
                Intent intent = new Intent("android.service.wallpaper.WallpaperService");
                WallpaperInfo wallpaperInfo = null;
                if (componentName2 != null && !componentName2.equals(this.mImageWallpaper)) {
                    List list = this.mIPackageManager.queryIntentServices(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 128L, i).getList();
                    int i2 = 0;
                    while (true) {
                        if (i2 >= list.size()) {
                            break;
                        }
                        ServiceInfo serviceInfo2 = ((ResolveInfo) list.get(i2)).serviceInfo;
                        if (serviceInfo2.name.equals(serviceInfo.name) && serviceInfo2.packageName.equals(serviceInfo.packageName)) {
                            try {
                                wallpaperInfo = new WallpaperInfo(this.mContext, (ResolveInfo) list.get(i2));
                                break;
                            } catch (IOException e2) {
                                if (z2) {
                                    throw new IllegalArgumentException(e2);
                                }
                                Slog.w("WallpaperManagerService", e2);
                                return false;
                            } catch (XmlPullParserException e3) {
                                if (z2) {
                                    throw new IllegalArgumentException(e3);
                                }
                                Slog.w("WallpaperManagerService", e3);
                                return false;
                            }
                        }
                        i2++;
                    }
                    if (wallpaperInfo == null) {
                        String str4 = "Selected service is not a wallpaper: " + componentName2;
                        if (z2) {
                            throw new SecurityException(str4);
                        }
                        Slog.w("WallpaperManagerService", str4);
                        return false;
                    }
                }
                WallpaperInfo wallpaperInfo2 = wallpaperInfo;
                if (wallpaperInfo2 != null && wallpaperInfo2.supportsAmbientMode() && this.mIPackageManager.checkPermission("android.permission.AMBIENT_WALLPAPER", wallpaperInfo2.getPackageName(), i) != 0) {
                    String str5 = "Selected service does not have android.permission.AMBIENT_WALLPAPER: " + componentName2;
                    if (z2) {
                        throw new SecurityException(str5);
                    }
                    Slog.w("WallpaperManagerService", str5);
                    return false;
                }
                PendingIntent activityAsUser = PendingIntent.getActivityAsUser(this.mContext, 0, Intent.createChooser(new Intent("android.intent.action.SET_WALLPAPER"), this.mContext.getText(17039810)), 67108864, ActivityOptions.makeBasic().setPendingIntentCreatorBackgroundActivityStartMode(2).toBundle(), UserHandle.of(i));
                try {
                    wallpaperConnection = new WallpaperConnection(wallpaperInfo2, wallpaperData, this.mIPackageManager.getPackageUid(componentName2.getPackageName(), 268435456L, wallpaperData.userId));
                    intent.setComponent(componentName2);
                    intent.putExtra("android.intent.extra.client_label", 17041741);
                    intent.putExtra("android.intent.extra.client_intent", activityAsUser);
                    str = this.mContext.bindServiceAsUser(intent, wallpaperConnection, 570429441, new UserHandle(i));
                } catch (RemoteException e4) {
                    e = e4;
                    str = "WallpaperManagerService";
                }
                try {
                    if (str == 0) {
                        String str6 = "Unable to bind service: " + componentName2;
                        if (z2) {
                            throw new IllegalArgumentException(str6);
                        }
                        Slog.w("WallpaperManagerService", str6);
                        return false;
                    }
                    if (this.mEnableSeparateLockScreenEngine) {
                        maybeDetachLastWallpapers(wallpaperData);
                    } else if (wallpaperData.userId == this.mCurrentUserId && this.mLastWallpaper != null && !wallpaperData.equals(this.mFallbackWallpaper)) {
                        detachWallpaperLocked(this.mLastWallpaper);
                    }
                    wallpaperData.wallpaperComponent = componentName2;
                    wallpaperData.connection = wallpaperConnection;
                    wallpaperConnection.mReply = iRemoteCallback;
                    if (this.mEnableSeparateLockScreenEngine) {
                        updateCurrentWallpapers(wallpaperData);
                    } else if (wallpaperData.userId == this.mCurrentUserId && !wallpaperData.equals(this.mFallbackWallpaper)) {
                        this.mLastWallpaper = wallpaperData;
                    }
                    updateFallbackConnection();
                    timingsTraceAndSlog.traceEnd();
                    return true;
                } catch (RemoteException e5) {
                    e = e5;
                    String str22 = "Remote exception for " + componentName2 + "\n" + e;
                    if (z2) {
                    }
                }
            }
        } finally {
            timingsTraceAndSlog.traceEnd();
        }
    }

    public final void updateCurrentWallpapers(WallpaperData wallpaperData) {
        if (wallpaperData.userId != this.mCurrentUserId || wallpaperData.equals(this.mFallbackWallpaper)) {
            return;
        }
        int i = wallpaperData.mWhich;
        if (i == 3) {
            this.mLastWallpaper = wallpaperData;
        } else if (i == 1) {
            this.mLastWallpaper = wallpaperData;
        } else if (i == 2) {
            this.mLastLockWallpaper = wallpaperData;
        }
    }

    public final void maybeDetachLastWallpapers(WallpaperData wallpaperData) {
        if (wallpaperData.userId != this.mCurrentUserId || wallpaperData.equals(this.mFallbackWallpaper)) {
            return;
        }
        int i = wallpaperData.mWhich;
        boolean z = false;
        boolean z2 = (i & 1) != 0;
        boolean z3 = (i & 2) != 0;
        if (wallpaperData.mSystemWasBoth && !z3) {
            z = true;
        }
        WallpaperData wallpaperData2 = this.mLastWallpaper;
        if (wallpaperData2 != null && z2 && !z) {
            detachWallpaperLocked(wallpaperData2);
        }
        WallpaperData wallpaperData3 = this.mLastLockWallpaper;
        if (wallpaperData3 == null || !z3) {
            return;
        }
        detachWallpaperLocked(wallpaperData3);
    }

    public final void detachWallpaperLocked(final WallpaperData wallpaperData) {
        WallpaperConnection wallpaperConnection = wallpaperData.connection;
        if (wallpaperConnection != null) {
            IRemoteCallback iRemoteCallback = wallpaperConnection.mReply;
            if (iRemoteCallback != null) {
                try {
                    iRemoteCallback.sendResult((Bundle) null);
                } catch (RemoteException unused) {
                }
                wallpaperData.connection.mReply = null;
            }
            wallpaperData.connection.forEachDisplayConnector(new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda9
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    WallpaperManagerService.lambda$detachWallpaperLocked$13(WallpaperData.this, (WallpaperManagerService.DisplayConnector) obj);
                }
            });
            WallpaperConnection wallpaperConnection2 = wallpaperData.connection;
            wallpaperConnection2.mService = null;
            wallpaperConnection2.mDisplayConnector.clear();
            FgThread.getHandler().removeCallbacks(wallpaperData.connection.mResetRunnable);
            this.mContext.getMainThreadHandler().removeCallbacks(wallpaperData.connection.mDisconnectRunnable);
            this.mContext.getMainThreadHandler().removeCallbacks(wallpaperData.connection.mTryToRebindRunnable);
            this.mContext.unbindService(wallpaperData.connection);
            wallpaperData.connection = null;
            if (wallpaperData == this.mLastWallpaper) {
                this.mLastWallpaper = null;
            }
            if (wallpaperData == this.mLastLockWallpaper) {
                this.mLastLockWallpaper = null;
            }
        }
    }

    public static /* synthetic */ void lambda$detachWallpaperLocked$13(WallpaperData wallpaperData, DisplayConnector displayConnector) {
        displayConnector.disconnectLocked(wallpaperData.connection);
    }

    public final void clearWallpaperComponentLocked(WallpaperData wallpaperData) {
        wallpaperData.wallpaperComponent = null;
        detachWallpaperLocked(wallpaperData);
    }

    public final void attachServiceLocked(final WallpaperConnection wallpaperConnection, final WallpaperData wallpaperData) {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog("WallpaperManagerService");
        timingsTraceAndSlog.traceBegin("WPMS.attachServiceLocked");
        wallpaperConnection.forEachDisplayConnector(new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda17
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((WallpaperManagerService.DisplayConnector) obj).connectLocked(WallpaperManagerService.WallpaperConnection.this, wallpaperData);
            }
        });
        timingsTraceAndSlog.traceEnd();
    }

    public final void notifyCallbacksLocked(WallpaperData wallpaperData) {
        int beginBroadcast = wallpaperData.callbacks.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                wallpaperData.callbacks.getBroadcastItem(i).onWallpaperChanged();
            } catch (RemoteException unused) {
            }
        }
        wallpaperData.callbacks.finishBroadcast();
        Intent intent = new Intent("android.intent.action.WALLPAPER_CHANGED");
        intent.putExtra("android.service.wallpaper.extra.FROM_FOREGROUND_APP", wallpaperData.fromForegroundApp);
        this.mContext.sendBroadcastAsUser(intent, new UserHandle(this.mCurrentUserId));
    }

    public final void checkPermission(String str) {
        if (hasPermission(str)) {
            return;
        }
        throw new SecurityException("Access denied to process: " + Binder.getCallingPid() + ", must have permission " + str);
    }

    public final boolean packageBelongsToUid(String str, int i) {
        try {
            return this.mContext.getPackageManager().getPackageUidAsUser(str, UserHandle.getUserId(i)) == i;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public final void enforcePackageBelongsToUid(String str, int i) {
        if (packageBelongsToUid(str, i)) {
            return;
        }
        throw new IllegalArgumentException("Invalid package or package does not belong to uid:" + i);
    }

    public boolean isWallpaperSupported(String str) {
        int callingUid = Binder.getCallingUid();
        enforcePackageBelongsToUid(str, callingUid);
        return this.mAppOpsManager.checkOpNoThrow(48, callingUid, str) == 0;
    }

    public boolean isSetWallpaperAllowed(String str) {
        if (Arrays.asList(this.mContext.getPackageManager().getPackagesForUid(Binder.getCallingUid())).contains(str)) {
            DevicePolicyManagerInternal devicePolicyManagerInternal = (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class);
            if (devicePolicyManagerInternal == null || !devicePolicyManagerInternal.isDeviceOrProfileOwnerInCallingUser(str)) {
                int callingUserId = UserHandle.getCallingUserId();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return !((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).hasUserRestriction("no_set_wallpaper", callingUserId);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return true;
        }
        return false;
    }

    public boolean isWallpaperBackupEligible(int i, int i2) {
        WallpaperData wallpaperData;
        if (i == 2) {
            wallpaperData = this.mLockWallpaperMap.get(i2);
        } else {
            wallpaperData = this.mWallpaperMap.get(i2);
        }
        if (wallpaperData != null) {
            return wallpaperData.allowBackup;
        }
        return false;
    }

    public final void onDisplayReadyInternal(int i) {
        synchronized (this.mLock) {
            WallpaperData wallpaperData = this.mLastWallpaper;
            if (wallpaperData == null) {
                return;
            }
            if (supportsMultiDisplay(wallpaperData.connection)) {
                DisplayConnector displayConnectorOrCreate = this.mLastWallpaper.connection.getDisplayConnectorOrCreate(i);
                if (displayConnectorOrCreate == null) {
                    return;
                }
                WallpaperData wallpaperData2 = this.mLastWallpaper;
                displayConnectorOrCreate.connectLocked(wallpaperData2.connection, wallpaperData2);
                return;
            }
            WallpaperData wallpaperData3 = this.mFallbackWallpaper;
            if (wallpaperData3 != null) {
                DisplayConnector displayConnectorOrCreate2 = wallpaperData3.connection.getDisplayConnectorOrCreate(i);
                if (displayConnectorOrCreate2 == null) {
                    return;
                }
                WallpaperData wallpaperData4 = this.mFallbackWallpaper;
                displayConnectorOrCreate2.connectLocked(wallpaperData4.connection, wallpaperData4);
            } else {
                Slog.w("WallpaperManagerService", "No wallpaper can be added to the new display");
            }
        }
    }

    public void saveSettingsLocked(int i) {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog("WallpaperManagerService");
        timingsTraceAndSlog.traceBegin("WPMS.saveSettingsLocked-" + i);
        this.mWallpaperDataParser.saveSettingsLocked(i, this.mWallpaperMap.get(i), this.mLockWallpaperMap.get(i));
        timingsTraceAndSlog.traceEnd();
    }

    public WallpaperData getWallpaperSafeLocked(int i, int i2) {
        SparseArray<WallpaperData> sparseArray = i2 == 2 ? this.mLockWallpaperMap : this.mWallpaperMap;
        WallpaperData wallpaperData = sparseArray.get(i);
        if (wallpaperData == null) {
            loadSettingsLocked(i, false, i2 == 2 ? 2 : 1);
            WallpaperData wallpaperData2 = sparseArray.get(i);
            if (wallpaperData2 == null) {
                if (i2 == 2) {
                    WallpaperData wallpaperData3 = new WallpaperData(i, 2);
                    this.mLockWallpaperMap.put(i, wallpaperData3);
                    return wallpaperData3;
                }
                Slog.wtf("WallpaperManagerService", "Didn't find wallpaper in non-lock case!");
                WallpaperData wallpaperData4 = new WallpaperData(i, 1);
                this.mWallpaperMap.put(i, wallpaperData4);
                return wallpaperData4;
            }
            return wallpaperData2;
        }
        return wallpaperData;
    }

    public final void loadSettingsLocked(int i, boolean z, int i2) {
        initializeFallbackWallpaper();
        WallpaperDataParser.WallpaperLoadingResult loadSettingsLocked = this.mWallpaperDataParser.loadSettingsLocked(i, z, this.mWallpaperMap.get(i), this.mLockWallpaperMap.get(i), i2);
        boolean z2 = this.mEnableSeparateLockScreenEngine;
        boolean z3 = false;
        boolean z4 = (z2 && (i2 & 1) == 0) ? false : true;
        if (!z2 || (i2 & 2) != 0) {
            z3 = true;
        }
        if (z4) {
            this.mWallpaperMap.put(i, loadSettingsLocked.getSystemWallpaperData());
        }
        if (z3) {
            if (loadSettingsLocked.success()) {
                this.mLockWallpaperMap.put(i, loadSettingsLocked.getLockWallpaperData());
            } else {
                this.mLockWallpaperMap.remove(i);
            }
        }
    }

    public final void initializeFallbackWallpaper() {
        if (this.mFallbackWallpaper == null) {
            WallpaperData wallpaperData = new WallpaperData(0, 1);
            this.mFallbackWallpaper = wallpaperData;
            wallpaperData.allowBackup = false;
            wallpaperData.wallpaperId = WallpaperUtils.makeWallpaperIdLocked();
            bindWallpaperComponentLocked(this.mImageWallpaper, true, false, this.mFallbackWallpaper, null);
        }
    }

    public void settingsRestored() {
        WallpaperData wallpaperData;
        boolean z;
        if (Binder.getCallingUid() != 1000) {
            throw new RuntimeException("settingsRestored() can only be called from the system process");
        }
        synchronized (this.mLock) {
            loadSettingsLocked(0, false, 3);
            wallpaperData = this.mWallpaperMap.get(0);
            wallpaperData.wallpaperId = WallpaperUtils.makeWallpaperIdLocked();
            z = true;
            wallpaperData.allowBackup = true;
            ComponentName componentName = wallpaperData.nextWallpaperComponent;
            if (componentName != null && !componentName.equals(this.mImageWallpaper)) {
                if (!bindWallpaperComponentLocked(wallpaperData.nextWallpaperComponent, false, false, wallpaperData, null)) {
                    bindWallpaperComponentLocked(null, false, false, wallpaperData, null);
                }
            } else {
                if (!"".equals(wallpaperData.name)) {
                    z = this.mWallpaperDataParser.restoreNamedResourceLocked(wallpaperData);
                }
                if (z) {
                    this.mWallpaperCropper.generateCrop(wallpaperData);
                    bindWallpaperComponentLocked(wallpaperData.nextWallpaperComponent, true, false, wallpaperData, null);
                }
            }
        }
        if (!z) {
            Slog.e("WallpaperManagerService", "Failed to restore wallpaper: '" + wallpaperData.name + "'");
            wallpaperData.name = "";
            WallpaperUtils.getWallpaperDir(0).delete();
        }
        synchronized (this.mLock) {
            saveSettingsLocked(0);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new WallpaperManagerShellCommand(this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    public final void dumpWallpaper(WallpaperData wallpaperData, final PrintWriter printWriter) {
        printWriter.print(" User ");
        printWriter.print(wallpaperData.userId);
        printWriter.print(": id=");
        printWriter.print(wallpaperData.wallpaperId);
        printWriter.print(": mWhich=");
        printWriter.print(wallpaperData.mWhich);
        printWriter.print(": mSystemWasBoth=");
        printWriter.println(wallpaperData.mSystemWasBoth);
        printWriter.println(" Display state:");
        this.mWallpaperDisplayHelper.forEachDisplayData(new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda7
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                WallpaperManagerService.lambda$dumpWallpaper$15(printWriter, (WallpaperDisplayHelper.DisplayData) obj);
            }
        });
        printWriter.print("  mCropHint=");
        printWriter.println(wallpaperData.cropHint);
        printWriter.print("  mName=");
        printWriter.println(wallpaperData.name);
        printWriter.print("  mAllowBackup=");
        printWriter.println(wallpaperData.allowBackup);
        printWriter.print("  mWallpaperComponent=");
        printWriter.println(wallpaperData.wallpaperComponent);
        printWriter.print("  mWallpaperDimAmount=");
        printWriter.println(wallpaperData.mWallpaperDimAmount);
        printWriter.print("  isColorExtracted=");
        printWriter.println(wallpaperData.mIsColorExtractedFromDim);
        printWriter.println("  mUidToDimAmount:");
        for (int i = 0; i < wallpaperData.mUidToDimAmount.size(); i++) {
            printWriter.print("    UID=");
            printWriter.print(wallpaperData.mUidToDimAmount.keyAt(i));
            printWriter.print(" dimAmount=");
            printWriter.println(wallpaperData.mUidToDimAmount.valueAt(i));
        }
        WallpaperConnection wallpaperConnection = wallpaperData.connection;
        if (wallpaperConnection != null) {
            printWriter.print("  Wallpaper connection ");
            printWriter.print(wallpaperConnection);
            printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
            if (wallpaperConnection.mInfo != null) {
                printWriter.print("    mInfo.component=");
                printWriter.println(wallpaperConnection.mInfo.getComponent());
            }
            wallpaperConnection.forEachDisplayConnector(new Consumer() { // from class: com.android.server.wallpaper.WallpaperManagerService$$ExternalSyntheticLambda8
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    WallpaperManagerService.lambda$dumpWallpaper$16(printWriter, (WallpaperManagerService.DisplayConnector) obj);
                }
            });
            printWriter.print("    mService=");
            printWriter.println(wallpaperConnection.mService);
            printWriter.print("    mLastDiedTime=");
            printWriter.println(wallpaperData.lastDiedTime - SystemClock.uptimeMillis());
        }
    }

    public static /* synthetic */ void lambda$dumpWallpaper$15(PrintWriter printWriter, WallpaperDisplayHelper.DisplayData displayData) {
        printWriter.print("  displayId=");
        printWriter.println(displayData.mDisplayId);
        printWriter.print("  mWidth=");
        printWriter.print(displayData.mWidth);
        printWriter.print("  mHeight=");
        printWriter.println(displayData.mHeight);
        printWriter.print("  mPadding=");
        printWriter.println(displayData.mPadding);
    }

    public static /* synthetic */ void lambda$dumpWallpaper$16(PrintWriter printWriter, DisplayConnector displayConnector) {
        printWriter.print("     mDisplayId=");
        printWriter.println(displayConnector.mDisplayId);
        printWriter.print("     mToken=");
        printWriter.println(displayConnector.mToken);
        printWriter.print("     mEngine=");
        printWriter.println(displayConnector.mEngine);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "WallpaperManagerService", printWriter)) {
            printWriter.print("mDefaultWallpaperComponent=");
            printWriter.println(this.mDefaultWallpaperComponent);
            printWriter.print("mImageWallpaper=");
            printWriter.println(this.mImageWallpaper);
            synchronized (this.mLock) {
                printWriter.println("System wallpaper state:");
                for (int i = 0; i < this.mWallpaperMap.size(); i++) {
                    dumpWallpaper(this.mWallpaperMap.valueAt(i), printWriter);
                }
                printWriter.println("Lock wallpaper state:");
                for (int i2 = 0; i2 < this.mLockWallpaperMap.size(); i2++) {
                    dumpWallpaper(this.mLockWallpaperMap.valueAt(i2), printWriter);
                }
                printWriter.println("Fallback wallpaper state:");
                WallpaperData wallpaperData = this.mFallbackWallpaper;
                if (wallpaperData != null) {
                    dumpWallpaper(wallpaperData, printWriter);
                }
            }
        }
    }
}
