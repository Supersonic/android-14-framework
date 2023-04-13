package com.android.server.p014wm;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.SparseArray;
import android.view.IWallpaperVisibilityListener;
/* renamed from: com.android.server.wm.WallpaperVisibilityListeners */
/* loaded from: classes2.dex */
public class WallpaperVisibilityListeners {
    public final SparseArray<RemoteCallbackList<IWallpaperVisibilityListener>> mDisplayListeners = new SparseArray<>();

    public void registerWallpaperVisibilityListener(IWallpaperVisibilityListener iWallpaperVisibilityListener, int i) {
        RemoteCallbackList<IWallpaperVisibilityListener> remoteCallbackList = this.mDisplayListeners.get(i);
        if (remoteCallbackList == null) {
            remoteCallbackList = new RemoteCallbackList<>();
            this.mDisplayListeners.append(i, remoteCallbackList);
        }
        remoteCallbackList.register(iWallpaperVisibilityListener);
    }

    public void unregisterWallpaperVisibilityListener(IWallpaperVisibilityListener iWallpaperVisibilityListener, int i) {
        RemoteCallbackList<IWallpaperVisibilityListener> remoteCallbackList = this.mDisplayListeners.get(i);
        if (remoteCallbackList == null) {
            return;
        }
        remoteCallbackList.unregister(iWallpaperVisibilityListener);
    }

    public void notifyWallpaperVisibilityChanged(DisplayContent displayContent) {
        int displayId = displayContent.getDisplayId();
        boolean isWallpaperVisible = displayContent.mWallpaperController.isWallpaperVisible();
        RemoteCallbackList<IWallpaperVisibilityListener> remoteCallbackList = this.mDisplayListeners.get(displayId);
        if (remoteCallbackList == null) {
            return;
        }
        int beginBroadcast = remoteCallbackList.beginBroadcast();
        while (beginBroadcast > 0) {
            beginBroadcast--;
            try {
                remoteCallbackList.getBroadcastItem(beginBroadcast).onWallpaperVisibilityChanged(isWallpaperVisible, displayId);
            } catch (RemoteException unused) {
            }
        }
        remoteCallbackList.finishBroadcast();
    }
}
