package com.android.internal.view;

import android.content.Context;
import android.content.p001pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.p008os.AsyncTask;
import android.p008os.Handler;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.C4057R;
/* loaded from: classes2.dex */
public final class RotationPolicy {
    private static final int CURRENT_ROTATION = -1;
    public static final int NATURAL_ROTATION = 0;
    private static final String TAG = "RotationPolicy";

    /* loaded from: classes2.dex */
    public static abstract class RotationPolicyListener {
        final ContentObserver mObserver = new ContentObserver(new Handler()) { // from class: com.android.internal.view.RotationPolicy.RotationPolicyListener.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                RotationPolicyListener.this.onChange();
            }
        };

        public abstract void onChange();
    }

    private RotationPolicy() {
    }

    public static boolean isRotationSupported(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER) && pm.hasSystemFeature(PackageManager.FEATURE_SCREEN_PORTRAIT) && pm.hasSystemFeature(PackageManager.FEATURE_SCREEN_LANDSCAPE) && context.getResources().getBoolean(C4057R.bool.config_supportAutoRotation);
    }

    public static int getRotationLockOrientation(Context context) {
        if (areAllRotationsAllowed(context)) {
            return 0;
        }
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int rotation = context.getResources().getConfiguration().windowConfiguration.getRotation();
        boolean rotated = rotation % 2 != 0;
        int w = rotated ? metrics.heightPixels : metrics.widthPixels;
        int h = rotated ? metrics.widthPixels : metrics.heightPixels;
        return w < h ? 1 : 2;
    }

    public static boolean isRotationLockToggleVisible(Context context) {
        return isRotationSupported(context) && Settings.System.getIntForUser(context.getContentResolver(), Settings.System.HIDE_ROTATION_LOCK_TOGGLE_FOR_ACCESSIBILITY, 0, -2) == 0;
    }

    public static boolean isRotationLocked(Context context) {
        return Settings.System.getIntForUser(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0, -2) == 0;
    }

    public static void setRotationLock(Context context, boolean enabled) {
        int rotation;
        if (areAllRotationsAllowed(context) || useCurrentRotationOnRotationLockChange(context)) {
            rotation = -1;
        } else {
            rotation = 0;
        }
        setRotationLockAtAngle(context, enabled, rotation);
    }

    public static void setRotationLockAtAngle(Context context, boolean enabled, int rotation) {
        Settings.System.putIntForUser(context.getContentResolver(), Settings.System.HIDE_ROTATION_LOCK_TOGGLE_FOR_ACCESSIBILITY, 0, -2);
        setRotationLock(enabled, rotation);
    }

    public static void setRotationLockForAccessibility(Context context, boolean enabled) {
        Settings.System.putIntForUser(context.getContentResolver(), Settings.System.HIDE_ROTATION_LOCK_TOGGLE_FOR_ACCESSIBILITY, enabled ? 1 : 0, -2);
        setRotationLock(enabled, 0);
    }

    private static boolean areAllRotationsAllowed(Context context) {
        return context.getResources().getBoolean(C4057R.bool.config_allowAllRotations);
    }

    private static boolean useCurrentRotationOnRotationLockChange(Context context) {
        return context.getResources().getBoolean(C4057R.bool.config_useCurrentRotationOnRotationLockChange);
    }

    private static void setRotationLock(final boolean enabled, final int rotation) {
        AsyncTask.execute(new Runnable() { // from class: com.android.internal.view.RotationPolicy.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    IWindowManager wm = WindowManagerGlobal.getWindowManagerService();
                    if (enabled) {
                        wm.freezeRotation(rotation);
                    } else {
                        wm.thawRotation();
                    }
                } catch (RemoteException e) {
                    Log.m104w(RotationPolicy.TAG, "Unable to save auto-rotate setting");
                }
            }
        });
    }

    public static void registerRotationPolicyListener(Context context, RotationPolicyListener listener) {
        registerRotationPolicyListener(context, listener, UserHandle.getCallingUserId());
    }

    public static void registerRotationPolicyListener(Context context, RotationPolicyListener listener, int userHandle) {
        context.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, listener.mObserver, userHandle);
        context.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.HIDE_ROTATION_LOCK_TOGGLE_FOR_ACCESSIBILITY), false, listener.mObserver, userHandle);
    }

    public static void unregisterRotationPolicyListener(Context context, RotationPolicyListener listener) {
        context.getContentResolver().unregisterContentObserver(listener.mObserver);
    }
}
