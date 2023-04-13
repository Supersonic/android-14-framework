package com.android.internal.inputmethod;

import android.net.Uri;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.util.Log;
import android.view.inputmethod.ImeTracker;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.infra.AndroidFuture;
import com.android.internal.inputmethod.IInputContentUriToken;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class InputMethodPrivilegedOperations {
    private static final String TAG = "InputMethodPrivilegedOperations";
    private final OpsHolder mOps = new OpsHolder();

    /* loaded from: classes4.dex */
    private static final class OpsHolder {
        private IInputMethodPrivilegedOperations mPrivOps;

        private OpsHolder() {
        }

        public synchronized void set(IInputMethodPrivilegedOperations privOps) {
            if (this.mPrivOps != null) {
                throw new IllegalStateException("IInputMethodPrivilegedOperations must be set at most once. privOps=" + privOps);
            }
            this.mPrivOps = privOps;
        }

        private static String getCallerMethodName() {
            StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
            if (callStack.length <= 4) {
                return "<bottom of call stack>";
            }
            return callStack[4].getMethodName();
        }

        public synchronized IInputMethodPrivilegedOperations getAndWarnIfNull() {
            if (this.mPrivOps == null) {
                Log.m110e(InputMethodPrivilegedOperations.TAG, getCallerMethodName() + " is ignored. Call it within attachToken() and InputMethodService.onDestroy()");
            }
            return this.mPrivOps;
        }
    }

    public void set(IInputMethodPrivilegedOperations privOps) {
        Objects.requireNonNull(privOps, "privOps must not be null");
        this.mOps.set(privOps);
    }

    public void setImeWindowStatusAsync(int vis, int backDisposition) {
        IInputMethodPrivilegedOperations ops = this.mOps.getAndWarnIfNull();
        if (ops == null) {
            return;
        }
        try {
            ops.setImeWindowStatusAsync(vis, backDisposition);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void reportStartInputAsync(IBinder startInputToken) {
        IInputMethodPrivilegedOperations ops = this.mOps.getAndWarnIfNull();
        if (ops == null) {
            return;
        }
        try {
            ops.reportStartInputAsync(startInputToken);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public IInputContentUriToken createInputContentUriToken(Uri contentUri, String packageName) {
        IInputMethodPrivilegedOperations ops = this.mOps.getAndWarnIfNull();
        if (ops == null) {
            return null;
        }
        try {
            AndroidFuture<IBinder> future = new AndroidFuture<>();
            ops.createInputContentUriToken(contentUri, packageName, future);
            return IInputContentUriToken.Stub.asInterface((IBinder) CompletableFutureUtil.getResult(future));
        } catch (RemoteException e) {
            return null;
        }
    }

    public void reportFullscreenModeAsync(boolean fullscreen) {
        IInputMethodPrivilegedOperations ops = this.mOps.getAndWarnIfNull();
        if (ops == null) {
            return;
        }
        try {
            ops.reportFullscreenModeAsync(fullscreen);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void updateStatusIconAsync(String packageName, int iconResId) {
        IInputMethodPrivilegedOperations ops = this.mOps.getAndWarnIfNull();
        if (ops == null) {
            return;
        }
        try {
            ops.updateStatusIconAsync(packageName, iconResId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setInputMethod(String id) {
        IInputMethodPrivilegedOperations ops = this.mOps.getAndWarnIfNull();
        if (ops == null) {
            return;
        }
        try {
            AndroidFuture<Void> future = new AndroidFuture<>();
            ops.setInputMethod(id, future);
            CompletableFutureUtil.getResult(future);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setInputMethodAndSubtype(String id, InputMethodSubtype subtype) {
        IInputMethodPrivilegedOperations ops = this.mOps.getAndWarnIfNull();
        if (ops == null) {
            return;
        }
        try {
            AndroidFuture<Void> future = new AndroidFuture<>();
            ops.setInputMethodAndSubtype(id, subtype, future);
            CompletableFutureUtil.getResult(future);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void hideMySoftInput(int flags, int reason) {
        IInputMethodPrivilegedOperations ops = this.mOps.getAndWarnIfNull();
        if (ops == null) {
            return;
        }
        try {
            AndroidFuture<Void> future = new AndroidFuture<>();
            ops.hideMySoftInput(flags, reason, future);
            CompletableFutureUtil.getResult(future);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void showMySoftInput(int flags) {
        IInputMethodPrivilegedOperations ops = this.mOps.getAndWarnIfNull();
        if (ops == null) {
            return;
        }
        try {
            AndroidFuture<Void> future = new AndroidFuture<>();
            ops.showMySoftInput(flags, future);
            CompletableFutureUtil.getResult(future);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean switchToPreviousInputMethod() {
        IInputMethodPrivilegedOperations ops = this.mOps.getAndWarnIfNull();
        if (ops == null) {
            return false;
        }
        try {
            AndroidFuture<Boolean> value = new AndroidFuture<>();
            ops.switchToPreviousInputMethod(value);
            return ((Boolean) CompletableFutureUtil.getResult(value)).booleanValue();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean switchToNextInputMethod(boolean onlyCurrentIme) {
        IInputMethodPrivilegedOperations ops = this.mOps.getAndWarnIfNull();
        if (ops == null) {
            return false;
        }
        try {
            AndroidFuture<Boolean> future = new AndroidFuture<>();
            ops.switchToNextInputMethod(onlyCurrentIme, future);
            return ((Boolean) CompletableFutureUtil.getResult(future)).booleanValue();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean shouldOfferSwitchingToNextInputMethod() {
        IInputMethodPrivilegedOperations ops = this.mOps.getAndWarnIfNull();
        if (ops == null) {
            return false;
        }
        try {
            AndroidFuture<Boolean> future = new AndroidFuture<>();
            ops.shouldOfferSwitchingToNextInputMethod(future);
            return ((Boolean) CompletableFutureUtil.getResult(future)).booleanValue();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void notifyUserActionAsync() {
        IInputMethodPrivilegedOperations ops = this.mOps.getAndWarnIfNull();
        if (ops == null) {
            return;
        }
        try {
            ops.notifyUserActionAsync();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void applyImeVisibilityAsync(IBinder showOrHideInputToken, boolean setVisible, ImeTracker.Token statsToken) {
        IInputMethodPrivilegedOperations ops = this.mOps.getAndWarnIfNull();
        if (ops == null) {
            return;
        }
        try {
            ops.applyImeVisibilityAsync(showOrHideInputToken, setVisible, statsToken);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void onStylusHandwritingReady(int requestId, int pid) {
        IInputMethodPrivilegedOperations ops = this.mOps.getAndWarnIfNull();
        if (ops == null) {
            return;
        }
        try {
            ops.onStylusHandwritingReady(requestId, pid);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void resetStylusHandwriting(int requestId) {
        IInputMethodPrivilegedOperations ops = this.mOps.getAndWarnIfNull();
        if (ops == null) {
            return;
        }
        try {
            ops.resetStylusHandwriting(requestId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
