package com.android.server.inputmethod;

import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.EventLog;
import android.view.inputmethod.ImeTracker;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.inputmethod.InputMethodDebug;
import com.android.server.LocalServices;
import com.android.server.p014wm.WindowManagerInternal;
import java.util.Objects;
/* loaded from: classes.dex */
public final class DefaultImeVisibilityApplier implements ImeVisibilityApplier {
    public InputMethodManagerService mService;
    public final WindowManagerInternal mWindowManagerInternal = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);

    public DefaultImeVisibilityApplier(InputMethodManagerService inputMethodManagerService) {
        this.mService = inputMethodManagerService;
    }

    @GuardedBy({"ImfLock.class"})
    public void performShowIme(IBinder iBinder, ImeTracker.Token token, int i, ResultReceiver resultReceiver, int i2) {
        IInputMethodInvoker curMethodLocked = this.mService.getCurMethodLocked();
        if (curMethodLocked == null || !curMethodLocked.showSoftInput(iBinder, token, i, resultReceiver)) {
            return;
        }
        if (ImeTracker.DEBUG_IME_VISIBILITY) {
            EventLog.writeEvent(32001, token.getTag(), Objects.toString(this.mService.mCurFocusedWindow), InputMethodDebug.softInputDisplayReasonToString(i2), InputMethodDebug.softInputModeToString(this.mService.mCurFocusedWindowSoftInputMode));
        }
        this.mService.onShowHideSoftInputRequested(true, iBinder, i2, token);
    }

    @GuardedBy({"ImfLock.class"})
    public void performHideIme(IBinder iBinder, ImeTracker.Token token, ResultReceiver resultReceiver, int i) {
        IInputMethodInvoker curMethodLocked = this.mService.getCurMethodLocked();
        if (curMethodLocked == null || !curMethodLocked.hideSoftInput(iBinder, token, 0, resultReceiver)) {
            return;
        }
        if (ImeTracker.DEBUG_IME_VISIBILITY) {
            EventLog.writeEvent(32002, token.getTag(), Objects.toString(this.mService.mCurFocusedWindow), InputMethodDebug.softInputDisplayReasonToString(i), InputMethodDebug.softInputModeToString(this.mService.mCurFocusedWindowSoftInputMode));
        }
        this.mService.onShowHideSoftInputRequested(false, iBinder, i, token);
    }

    @GuardedBy({"ImfLock.class"})
    public void applyImeVisibility(IBinder iBinder, ImeTracker.Token token, int i) {
        applyImeVisibility(iBinder, token, i, -1);
    }

    @GuardedBy({"ImfLock.class"})
    public void applyImeVisibility(IBinder iBinder, ImeTracker.Token token, int i, int i2) {
        if (i == 0) {
            if (this.mService.mCurFocusedWindowClient != null) {
                ImeTracker.forLogging().onProgress(token, 17);
                this.mWindowManagerInternal.hideIme(iBinder, this.mService.mCurFocusedWindowClient.mSelfReportedDisplayId, token);
                return;
            }
            ImeTracker.forLogging().onFailed(token, 17);
        } else if (i == 1) {
            ImeTracker.forLogging().onProgress(token, 17);
            this.mWindowManagerInternal.showImePostLayout(iBinder, token);
        } else if (i == 5) {
            this.mService.hideCurrentInputLocked(iBinder, token, 0, null, i2);
        } else if (i == 6) {
            this.mService.hideCurrentInputLocked(iBinder, token, 2, null, i2);
        } else if (i == 7) {
            this.mService.showCurrentInputLocked(iBinder, token, 1, null, i2);
        } else {
            throw new IllegalArgumentException("Invalid IME visibility state: " + i);
        }
    }
}
