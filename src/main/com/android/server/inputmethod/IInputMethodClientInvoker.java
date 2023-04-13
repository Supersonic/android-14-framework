package com.android.server.inputmethod;

import android.os.Binder;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import android.view.InputChannel;
import com.android.internal.inputmethod.IInputMethodClient;
import com.android.internal.inputmethod.InputBindResult;
/* loaded from: classes.dex */
public final class IInputMethodClientInvoker {
    public final Handler mHandler;
    public final boolean mIsProxy;
    public final IInputMethodClient mTarget;

    public static IInputMethodClientInvoker create(IInputMethodClient iInputMethodClient, Handler handler) {
        if (iInputMethodClient == null) {
            return null;
        }
        boolean isProxy = Binder.isProxy(iInputMethodClient);
        if (isProxy) {
            handler = null;
        }
        return new IInputMethodClientInvoker(iInputMethodClient, isProxy, handler);
    }

    public IInputMethodClientInvoker(IInputMethodClient iInputMethodClient, boolean z, Handler handler) {
        this.mTarget = iInputMethodClient;
        this.mIsProxy = z;
        this.mHandler = handler;
    }

    public static String getCallerMethodName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace.length <= 4 ? "<bottom of call stack>" : stackTrace[4].getMethodName();
    }

    public static void logRemoteException(RemoteException remoteException) {
        if (remoteException instanceof DeadObjectException) {
            return;
        }
        Slog.w("InputMethodManagerService", "IPC failed at IInputMethodClientInvoker#" + getCallerMethodName(), remoteException);
    }

    public void onBindMethod(final InputBindResult inputBindResult) {
        if (this.mIsProxy) {
            lambda$onBindMethod$0(inputBindResult);
        } else {
            this.mHandler.post(new Runnable() { // from class: com.android.server.inputmethod.IInputMethodClientInvoker$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    IInputMethodClientInvoker.this.lambda$onBindMethod$0(inputBindResult);
                }
            });
        }
    }

    /* renamed from: onBindMethodInternal */
    public final void lambda$onBindMethod$0(InputBindResult inputBindResult) {
        InputChannel inputChannel;
        try {
            try {
                this.mTarget.onBindMethod(inputBindResult);
                inputChannel = inputBindResult.channel;
                if (inputChannel == null || !this.mIsProxy) {
                    return;
                }
            } catch (RemoteException e) {
                logRemoteException(e);
                inputChannel = inputBindResult.channel;
                if (inputChannel == null || !this.mIsProxy) {
                    return;
                }
            }
            inputChannel.dispose();
        } catch (Throwable th) {
            InputChannel inputChannel2 = inputBindResult.channel;
            if (inputChannel2 != null && this.mIsProxy) {
                inputChannel2.dispose();
            }
            throw th;
        }
    }

    public void onBindAccessibilityService(final InputBindResult inputBindResult, final int i) {
        if (this.mIsProxy) {
            lambda$onBindAccessibilityService$1(inputBindResult, i);
        } else {
            this.mHandler.post(new Runnable() { // from class: com.android.server.inputmethod.IInputMethodClientInvoker$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    IInputMethodClientInvoker.this.lambda$onBindAccessibilityService$1(inputBindResult, i);
                }
            });
        }
    }

    /* renamed from: onBindAccessibilityServiceInternal */
    public final void lambda$onBindAccessibilityService$1(InputBindResult inputBindResult, int i) {
        InputChannel inputChannel;
        try {
            try {
                this.mTarget.onBindAccessibilityService(inputBindResult, i);
                inputChannel = inputBindResult.channel;
                if (inputChannel == null || !this.mIsProxy) {
                    return;
                }
            } catch (RemoteException e) {
                logRemoteException(e);
                inputChannel = inputBindResult.channel;
                if (inputChannel == null || !this.mIsProxy) {
                    return;
                }
            }
            inputChannel.dispose();
        } catch (Throwable th) {
            InputChannel inputChannel2 = inputBindResult.channel;
            if (inputChannel2 != null && this.mIsProxy) {
                inputChannel2.dispose();
            }
            throw th;
        }
    }

    public void onUnbindMethod(final int i, final int i2) {
        if (this.mIsProxy) {
            lambda$onUnbindMethod$2(i, i2);
        } else {
            this.mHandler.post(new Runnable() { // from class: com.android.server.inputmethod.IInputMethodClientInvoker$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    IInputMethodClientInvoker.this.lambda$onUnbindMethod$2(i, i2);
                }
            });
        }
    }

    /* renamed from: onUnbindMethodInternal */
    public final void lambda$onUnbindMethod$2(int i, int i2) {
        try {
            this.mTarget.onUnbindMethod(i, i2);
        } catch (RemoteException e) {
            logRemoteException(e);
        }
    }

    public void onUnbindAccessibilityService(final int i, final int i2) {
        if (this.mIsProxy) {
            lambda$onUnbindAccessibilityService$3(i, i2);
        } else {
            this.mHandler.post(new Runnable() { // from class: com.android.server.inputmethod.IInputMethodClientInvoker$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    IInputMethodClientInvoker.this.lambda$onUnbindAccessibilityService$3(i, i2);
                }
            });
        }
    }

    /* renamed from: onUnbindAccessibilityServiceInternal */
    public final void lambda$onUnbindAccessibilityService$3(int i, int i2) {
        try {
            this.mTarget.onUnbindAccessibilityService(i, i2);
        } catch (RemoteException e) {
            logRemoteException(e);
        }
    }

    public void setActive(final boolean z, final boolean z2) {
        if (this.mIsProxy) {
            lambda$setActive$4(z, z2);
        } else {
            this.mHandler.post(new Runnable() { // from class: com.android.server.inputmethod.IInputMethodClientInvoker$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    IInputMethodClientInvoker.this.lambda$setActive$4(z, z2);
                }
            });
        }
    }

    /* renamed from: setActiveInternal */
    public final void lambda$setActive$4(boolean z, boolean z2) {
        try {
            this.mTarget.setActive(z, z2);
        } catch (RemoteException e) {
            logRemoteException(e);
        }
    }

    public void setInteractive(final boolean z, final boolean z2) {
        if (this.mIsProxy) {
            lambda$setInteractive$5(z, z2);
        } else {
            this.mHandler.post(new Runnable() { // from class: com.android.server.inputmethod.IInputMethodClientInvoker$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    IInputMethodClientInvoker.this.lambda$setInteractive$5(z, z2);
                }
            });
        }
    }

    /* renamed from: setInteractiveInternal */
    public final void lambda$setInteractive$5(boolean z, boolean z2) {
        try {
            this.mTarget.setInteractive(z, z2);
        } catch (RemoteException e) {
            logRemoteException(e);
        }
    }

    public void scheduleStartInputIfNecessary(final boolean z) {
        if (this.mIsProxy) {
            lambda$scheduleStartInputIfNecessary$6(z);
        } else {
            this.mHandler.post(new Runnable() { // from class: com.android.server.inputmethod.IInputMethodClientInvoker$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    IInputMethodClientInvoker.this.lambda$scheduleStartInputIfNecessary$6(z);
                }
            });
        }
    }

    /* renamed from: scheduleStartInputIfNecessaryInternal */
    public final void lambda$scheduleStartInputIfNecessary$6(boolean z) {
        try {
            this.mTarget.scheduleStartInputIfNecessary(z);
        } catch (RemoteException e) {
            logRemoteException(e);
        }
    }

    public void reportFullscreenMode(final boolean z) {
        if (this.mIsProxy) {
            lambda$reportFullscreenMode$7(z);
        } else {
            this.mHandler.post(new Runnable() { // from class: com.android.server.inputmethod.IInputMethodClientInvoker$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    IInputMethodClientInvoker.this.lambda$reportFullscreenMode$7(z);
                }
            });
        }
    }

    /* renamed from: reportFullscreenModeInternal */
    public final void lambda$reportFullscreenMode$7(boolean z) {
        try {
            this.mTarget.reportFullscreenMode(z);
        } catch (RemoteException e) {
            logRemoteException(e);
        }
    }

    public void updateVirtualDisplayToScreenMatrix(final int i, final float[] fArr) {
        if (this.mIsProxy) {
            lambda$updateVirtualDisplayToScreenMatrix$8(i, fArr);
        } else {
            this.mHandler.post(new Runnable() { // from class: com.android.server.inputmethod.IInputMethodClientInvoker$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    IInputMethodClientInvoker.this.lambda$updateVirtualDisplayToScreenMatrix$8(i, fArr);
                }
            });
        }
    }

    /* renamed from: updateVirtualDisplayToScreenMatrixInternal */
    public final void lambda$updateVirtualDisplayToScreenMatrix$8(int i, float[] fArr) {
        try {
            this.mTarget.updateVirtualDisplayToScreenMatrix(i, fArr);
        } catch (RemoteException e) {
            logRemoteException(e);
        }
    }

    public void setImeTraceEnabled(final boolean z) {
        if (this.mIsProxy) {
            lambda$setImeTraceEnabled$9(z);
        } else {
            this.mHandler.post(new Runnable() { // from class: com.android.server.inputmethod.IInputMethodClientInvoker$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    IInputMethodClientInvoker.this.lambda$setImeTraceEnabled$9(z);
                }
            });
        }
    }

    /* renamed from: setImeTraceEnabledInternal */
    public final void lambda$setImeTraceEnabled$9(boolean z) {
        try {
            this.mTarget.setImeTraceEnabled(z);
        } catch (RemoteException e) {
            logRemoteException(e);
        }
    }

    public void throwExceptionFromSystem(final String str) {
        if (this.mIsProxy) {
            lambda$throwExceptionFromSystem$10(str);
        } else {
            this.mHandler.post(new Runnable() { // from class: com.android.server.inputmethod.IInputMethodClientInvoker$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    IInputMethodClientInvoker.this.lambda$throwExceptionFromSystem$10(str);
                }
            });
        }
    }

    /* renamed from: throwExceptionFromSystemInternal */
    public final void lambda$throwExceptionFromSystem$10(String str) {
        try {
            this.mTarget.throwExceptionFromSystem(str);
        } catch (RemoteException e) {
            logRemoteException(e);
        }
    }

    public IBinder asBinder() {
        return this.mTarget.asBinder();
    }
}
