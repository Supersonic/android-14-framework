package com.android.server.p012tv;

import android.os.IBinder;
import dalvik.system.CloseGuard;
import java.io.IOException;
/* renamed from: com.android.server.tv.UinputBridge */
/* loaded from: classes2.dex */
public final class UinputBridge {
    public final CloseGuard mCloseGuard;
    public long mPtr;
    public IBinder mToken;

    private static native void nativeClear(long j);

    private static native void nativeClose(long j);

    private static native long nativeGamepadOpen(String str, String str2);

    private static native long nativeOpen(String str, String str2, int i, int i2, int i3);

    private static native void nativeSendGamepadAxisValue(long j, int i, float f);

    private static native void nativeSendGamepadKey(long j, int i, boolean z);

    private static native void nativeSendKey(long j, int i, boolean z);

    private static native void nativeSendPointerDown(long j, int i, int i2, int i3);

    private static native void nativeSendPointerSync(long j);

    private static native void nativeSendPointerUp(long j, int i);

    public UinputBridge(IBinder iBinder, String str, int i, int i2, int i3) throws IOException {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        if (i < 1 || i2 < 1) {
            throw new IllegalArgumentException("Touchpad must be at least 1x1.");
        }
        if (i3 < 1 || i3 > 32) {
            throw new IllegalArgumentException("Touchpad must support between 1 and 32 pointers.");
        }
        if (iBinder == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        long nativeOpen = nativeOpen(str, iBinder.toString(), i, i2, i3);
        this.mPtr = nativeOpen;
        if (nativeOpen == 0) {
            throw new IOException("Could not open uinput device " + str);
        }
        this.mToken = iBinder;
        closeGuard.open("close");
    }

    public UinputBridge(IBinder iBinder, long j) {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mPtr = j;
        this.mToken = iBinder;
        closeGuard.open("close");
    }

    public static UinputBridge openGamepad(IBinder iBinder, String str) throws IOException {
        if (iBinder == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        long nativeGamepadOpen = nativeGamepadOpen(str, iBinder.toString());
        if (nativeGamepadOpen == 0) {
            throw new IOException("Could not open uinput device " + str);
        }
        return new UinputBridge(iBinder, nativeGamepadOpen);
    }

    public void finalize() throws Throwable {
        try {
            this.mCloseGuard.warnIfOpen();
            close(this.mToken);
        } finally {
            this.mToken = null;
            super.finalize();
        }
    }

    public void close(IBinder iBinder) {
        if (!isTokenValid(iBinder) || this.mPtr == 0) {
            return;
        }
        clear(iBinder);
        nativeClose(this.mPtr);
        this.mPtr = 0L;
        this.mCloseGuard.close();
    }

    public boolean isTokenValid(IBinder iBinder) {
        return this.mToken.equals(iBinder);
    }

    public void sendKeyDown(IBinder iBinder, int i) {
        if (isTokenValid(iBinder)) {
            nativeSendKey(this.mPtr, i, true);
        }
    }

    public void sendKeyUp(IBinder iBinder, int i) {
        if (isTokenValid(iBinder)) {
            nativeSendKey(this.mPtr, i, false);
        }
    }

    public void sendPointerDown(IBinder iBinder, int i, int i2, int i3) {
        if (isTokenValid(iBinder)) {
            nativeSendPointerDown(this.mPtr, i, i2, i3);
        }
    }

    public void sendPointerUp(IBinder iBinder, int i) {
        if (isTokenValid(iBinder)) {
            nativeSendPointerUp(this.mPtr, i);
        }
    }

    public void sendPointerSync(IBinder iBinder) {
        if (isTokenValid(iBinder)) {
            nativeSendPointerSync(this.mPtr);
        }
    }

    public void sendGamepadKey(IBinder iBinder, int i, boolean z) {
        if (isTokenValid(iBinder)) {
            nativeSendGamepadKey(this.mPtr, i, z);
        }
    }

    public void sendGamepadAxisValue(IBinder iBinder, int i, float f) {
        if (isTokenValid(iBinder)) {
            nativeSendGamepadAxisValue(this.mPtr, i, f);
        }
    }

    public void clear(IBinder iBinder) {
        if (isTokenValid(iBinder)) {
            nativeClear(this.mPtr);
        }
    }
}
