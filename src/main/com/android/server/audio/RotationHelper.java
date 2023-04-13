package com.android.server.audio;

import android.content.Context;
import android.hardware.devicestate.DeviceStateManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerGlobal;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.util.Log;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class RotationHelper {
    public static Context sContext = null;
    public static boolean sDeviceFold = true;
    public static int sDeviceRotation;
    public static AudioDisplayListener sDisplayListener;
    public static DeviceStateManager.FoldStateListener sFoldStateListener;
    public static Consumer<String> sFoldUpdateCb;
    public static Handler sHandler;
    public static Consumer<String> sRotationUpdateCb;
    public static final Object sRotationLock = new Object();
    public static final Object sFoldStateLock = new Object();

    public static void init(Context context, Handler handler, Consumer<String> consumer, Consumer<String> consumer2) {
        if (context == null) {
            throw new IllegalArgumentException("Invalid null context");
        }
        sContext = context;
        sHandler = handler;
        sDisplayListener = new AudioDisplayListener();
        sRotationUpdateCb = consumer;
        sFoldUpdateCb = consumer2;
        enable();
    }

    public static void enable() {
        ((DisplayManager) sContext.getSystemService("display")).registerDisplayListener(sDisplayListener, sHandler);
        updateOrientation();
        sFoldStateListener = new DeviceStateManager.FoldStateListener(sContext, new Consumer() { // from class: com.android.server.audio.RotationHelper$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RotationHelper.lambda$enable$0((Boolean) obj);
            }
        });
        ((DeviceStateManager) sContext.getSystemService(DeviceStateManager.class)).registerCallback(new HandlerExecutor(sHandler), sFoldStateListener);
    }

    public static /* synthetic */ void lambda$enable$0(Boolean bool) {
        updateFoldState(bool.booleanValue());
    }

    public static void disable() {
        ((DisplayManager) sContext.getSystemService("display")).unregisterDisplayListener(sDisplayListener);
        ((DeviceStateManager) sContext.getSystemService(DeviceStateManager.class)).unregisterCallback(sFoldStateListener);
    }

    public static void updateOrientation() {
        int i = DisplayManagerGlobal.getInstance().getDisplayInfo(0).rotation;
        synchronized (sRotationLock) {
            if (i != sDeviceRotation) {
                sDeviceRotation = i;
                publishRotation(i);
            }
        }
    }

    public static void publishRotation(int i) {
        String str;
        if (i == 0) {
            str = "rotation=0";
        } else if (i == 1) {
            str = "rotation=90";
        } else if (i == 2) {
            str = "rotation=180";
        } else if (i != 3) {
            Log.e("AudioService.RotationHelper", "Unknown device rotation");
            str = null;
        } else {
            str = "rotation=270";
        }
        if (str != null) {
            sRotationUpdateCb.accept(str);
        }
    }

    public static void updateFoldState(boolean z) {
        synchronized (sFoldStateLock) {
            if (sDeviceFold != z) {
                sDeviceFold = z;
                sFoldUpdateCb.accept(z ? "device_folded=on" : "device_folded=off");
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class AudioDisplayListener implements DisplayManager.DisplayListener {
        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int i) {
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int i) {
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int i) {
            RotationHelper.updateOrientation();
        }
    }
}
