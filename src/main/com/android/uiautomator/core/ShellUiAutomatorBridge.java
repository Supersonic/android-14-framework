package com.android.uiautomator.core;

import android.app.ActivityManager;
import android.app.ContentProviderHolder;
import android.app.IActivityManager;
import android.app.UiAutomation;
import android.content.AttributionSource;
import android.content.ContentResolver;
import android.content.IContentProvider;
import android.database.Cursor;
import android.hardware.display.DisplayManagerGlobal;
import android.os.Binder;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.IWindowManager;
/* loaded from: classes.dex */
public class ShellUiAutomatorBridge extends UiAutomatorBridge {
    private static final String LOG_TAG = ShellUiAutomatorBridge.class.getSimpleName();

    public ShellUiAutomatorBridge(UiAutomation uiAutomation) {
        super(uiAutomation);
    }

    @Override // com.android.uiautomator.core.UiAutomatorBridge
    public Display getDefaultDisplay() {
        return DisplayManagerGlobal.getInstance().getRealDisplay(0);
    }

    @Override // com.android.uiautomator.core.UiAutomatorBridge
    public long getSystemLongPressTime() {
        long longPressTimeout = 0;
        try {
            IActivityManager activityManager = ActivityManager.getService();
            String providerName = Settings.Secure.CONTENT_URI.getAuthority();
            IBinder token = new Binder();
            ContentProviderHolder holder = activityManager.getContentProviderExternal(providerName, 0, token, "*uiautomator*");
            if (holder == null) {
                throw new IllegalStateException("Could not find provider: " + providerName);
            }
            IContentProvider provider = holder.provider;
            Cursor cursor = provider.query(new AttributionSource(Binder.getCallingUid(), resolveCallingPackage(), null), Settings.Secure.CONTENT_URI, new String[]{"value"}, ContentResolver.createSqlQueryBundle("name=?", new String[]{"long_press_timeout"}, null), (ICancellationSignal) null);
            if (cursor.moveToFirst()) {
                longPressTimeout = cursor.getInt(0);
            }
            if (cursor != null) {
                cursor.close();
            }
            if (provider != null) {
                activityManager.removeContentProviderExternalAsUser(providerName, token, 0);
            }
            return longPressTimeout;
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error reading long press timeout setting.", e);
            throw new RuntimeException("Error reading long press timeout setting.", e);
        }
    }

    @Override // com.android.uiautomator.core.UiAutomatorBridge
    public int getRotation() {
        IWindowManager wm = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        try {
            int ret = wm.getDefaultDisplayRotation();
            return ret;
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error getting screen rotation", e);
            throw new RuntimeException(e);
        }
    }

    @Override // com.android.uiautomator.core.UiAutomatorBridge
    public boolean isScreenOn() {
        IPowerManager pm = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
        try {
            boolean ret = pm.isInteractive();
            return ret;
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error getting screen status", e);
            throw new RuntimeException(e);
        }
    }

    private static String resolveCallingPackage() {
        switch (Binder.getCallingUid()) {
            case 0:
                return "root";
            case 2000:
                return "com.android.shell";
            default:
                return null;
        }
    }
}
