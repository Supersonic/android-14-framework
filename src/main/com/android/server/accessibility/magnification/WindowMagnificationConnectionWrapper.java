package com.android.server.accessibility.magnification;

import android.os.IBinder;
import android.os.RemoteException;
import android.view.accessibility.IRemoteMagnificationAnimationCallback;
import android.view.accessibility.IWindowMagnificationConnection;
import android.view.accessibility.IWindowMagnificationConnectionCallback;
import android.view.accessibility.MagnificationAnimationCallback;
import com.android.server.accessibility.AccessibilityTraceManager;
/* loaded from: classes.dex */
public class WindowMagnificationConnectionWrapper {
    public final IWindowMagnificationConnection mConnection;
    public final AccessibilityTraceManager mTrace;

    public WindowMagnificationConnectionWrapper(IWindowMagnificationConnection iWindowMagnificationConnection, AccessibilityTraceManager accessibilityTraceManager) {
        this.mConnection = iWindowMagnificationConnection;
        this.mTrace = accessibilityTraceManager;
    }

    public void unlinkToDeath(IBinder.DeathRecipient deathRecipient) {
        this.mConnection.asBinder().unlinkToDeath(deathRecipient, 0);
    }

    public void linkToDeath(IBinder.DeathRecipient deathRecipient) throws RemoteException {
        this.mConnection.asBinder().linkToDeath(deathRecipient, 0);
    }

    public boolean enableWindowMagnification(int i, float f, float f2, float f3, float f4, float f5, MagnificationAnimationCallback magnificationAnimationCallback) {
        if (this.mTrace.isA11yTracingEnabledForTypes(128L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTrace;
            accessibilityTraceManager.logTrace("WindowMagnificationConnectionWrapper.enableWindowMagnification", 128L, "displayId=" + i + ";scale=" + f + ";centerX=" + f2 + ";centerY=" + f3 + ";magnificationFrameOffsetRatioX=" + f4 + ";magnificationFrameOffsetRatioY=" + f5 + ";callback=" + magnificationAnimationCallback);
        }
        try {
            this.mConnection.enableWindowMagnification(i, f, f2, f3, f4, f5, transformToRemoteCallback(magnificationAnimationCallback, this.mTrace));
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean setScale(int i, float f) {
        if (this.mTrace.isA11yTracingEnabledForTypes(128L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTrace;
            accessibilityTraceManager.logTrace("WindowMagnificationConnectionWrapper.setScale", 128L, "displayId=" + i + ";scale=" + f);
        }
        try {
            this.mConnection.setScale(i, f);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean disableWindowMagnification(int i, MagnificationAnimationCallback magnificationAnimationCallback) {
        if (this.mTrace.isA11yTracingEnabledForTypes(128L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTrace;
            accessibilityTraceManager.logTrace("WindowMagnificationConnectionWrapper.disableWindowMagnification", 128L, "displayId=" + i + ";callback=" + magnificationAnimationCallback);
        }
        try {
            this.mConnection.disableWindowMagnification(i, transformToRemoteCallback(magnificationAnimationCallback, this.mTrace));
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean moveWindowMagnifier(int i, float f, float f2) {
        if (this.mTrace.isA11yTracingEnabledForTypes(128L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTrace;
            accessibilityTraceManager.logTrace("WindowMagnificationConnectionWrapper.moveWindowMagnifier", 128L, "displayId=" + i + ";offsetX=" + f + ";offsetY=" + f2);
        }
        try {
            this.mConnection.moveWindowMagnifier(i, f, f2);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean moveWindowMagnifierToPosition(int i, float f, float f2, MagnificationAnimationCallback magnificationAnimationCallback) {
        if (this.mTrace.isA11yTracingEnabledForTypes(128L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTrace;
            accessibilityTraceManager.logTrace("WindowMagnificationConnectionWrapper.moveWindowMagnifierToPosition", 128L, "displayId=" + i + ";positionX=" + f + ";positionY=" + f2);
        }
        try {
            this.mConnection.moveWindowMagnifierToPosition(i, f, f2, transformToRemoteCallback(magnificationAnimationCallback, this.mTrace));
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean showMagnificationButton(int i, int i2) {
        if (this.mTrace.isA11yTracingEnabledForTypes(128L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTrace;
            accessibilityTraceManager.logTrace("WindowMagnificationConnectionWrapper.showMagnificationButton", 128L, "displayId=" + i + ";mode=" + i2);
        }
        try {
            this.mConnection.showMagnificationButton(i, i2);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean removeMagnificationButton(int i) {
        if (this.mTrace.isA11yTracingEnabledForTypes(128L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTrace;
            accessibilityTraceManager.logTrace("WindowMagnificationConnectionWrapper.removeMagnificationButton", 128L, "displayId=" + i);
        }
        try {
            this.mConnection.removeMagnificationButton(i);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean setConnectionCallback(IWindowMagnificationConnectionCallback iWindowMagnificationConnectionCallback) {
        if (this.mTrace.isA11yTracingEnabledForTypes(384L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTrace;
            accessibilityTraceManager.logTrace("WindowMagnificationConnectionWrapper.setConnectionCallback", 384L, "callback=" + iWindowMagnificationConnectionCallback);
        }
        try {
            this.mConnection.setConnectionCallback(iWindowMagnificationConnectionCallback);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public static IRemoteMagnificationAnimationCallback transformToRemoteCallback(MagnificationAnimationCallback magnificationAnimationCallback, AccessibilityTraceManager accessibilityTraceManager) {
        if (magnificationAnimationCallback == null) {
            return null;
        }
        return new RemoteAnimationCallback(magnificationAnimationCallback, accessibilityTraceManager);
    }

    /* loaded from: classes.dex */
    public static class RemoteAnimationCallback extends IRemoteMagnificationAnimationCallback.Stub {
        public final MagnificationAnimationCallback mCallback;
        public final AccessibilityTraceManager mTrace;

        public RemoteAnimationCallback(MagnificationAnimationCallback magnificationAnimationCallback, AccessibilityTraceManager accessibilityTraceManager) {
            this.mCallback = magnificationAnimationCallback;
            this.mTrace = accessibilityTraceManager;
            if (accessibilityTraceManager.isA11yTracingEnabledForTypes(64L)) {
                accessibilityTraceManager.logTrace("RemoteAnimationCallback.constructor", 64L, "callback=" + magnificationAnimationCallback);
            }
        }

        public void onResult(boolean z) throws RemoteException {
            this.mCallback.onResult(z);
            if (this.mTrace.isA11yTracingEnabledForTypes(64L)) {
                AccessibilityTraceManager accessibilityTraceManager = this.mTrace;
                accessibilityTraceManager.logTrace("RemoteAnimationCallback.onResult", 64L, "success=" + z);
            }
        }
    }
}
