package com.android.server.inputmethod;

import android.os.Binder;
import android.os.IBinder;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.WindowManager;
import android.view.inputmethod.ImeTracker;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.inputmethod.InputMethodDebug;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.LocalServices;
import com.android.server.inputmethod.InputMethodManagerService;
import com.android.server.p014wm.WindowManagerInternal;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.WeakHashMap;
/* loaded from: classes.dex */
public final class ImeVisibilityStateComputer {
    public final InputMethodManagerService.ImeDisplayValidator mImeDisplayValidator;
    public boolean mInputShown;
    public final ImeVisibilityPolicy mPolicy;
    public final WeakHashMap<IBinder, ImeTargetWindowState> mRequestWindowStateMap;
    public boolean mRequestedShowExplicitly;
    public final InputMethodManagerService mService;
    public boolean mShowForced;
    public final WindowManagerInternal mWindowManagerInternal;

    /* loaded from: classes.dex */
    public interface Injector {
        default InputMethodManagerService.ImeDisplayValidator getImeValidator() {
            return null;
        }

        default WindowManagerInternal getWmService() {
            return null;
        }
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public ImeVisibilityStateComputer(InputMethodManagerService inputMethodManagerService) {
        this(inputMethodManagerService, (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class), new InputMethodManagerService.ImeDisplayValidator() { // from class: com.android.server.inputmethod.ImeVisibilityStateComputer$$ExternalSyntheticLambda0
            @Override // com.android.server.inputmethod.InputMethodManagerService.ImeDisplayValidator
            public final int getDisplayImePolicy(int i) {
                return WindowManagerInternal.this.getDisplayImePolicy(i);
            }
        }, new ImeVisibilityPolicy());
        final WindowManagerInternal windowManagerInternal = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
        Objects.requireNonNull(windowManagerInternal);
    }

    @VisibleForTesting
    public ImeVisibilityStateComputer(InputMethodManagerService inputMethodManagerService, Injector injector) {
        this(inputMethodManagerService, injector.getWmService(), injector.getImeValidator(), new ImeVisibilityPolicy());
    }

    public ImeVisibilityStateComputer(InputMethodManagerService inputMethodManagerService, WindowManagerInternal windowManagerInternal, InputMethodManagerService.ImeDisplayValidator imeDisplayValidator, ImeVisibilityPolicy imeVisibilityPolicy) {
        this.mRequestWindowStateMap = new WeakHashMap<>();
        this.mService = inputMethodManagerService;
        this.mWindowManagerInternal = windowManagerInternal;
        this.mImeDisplayValidator = imeDisplayValidator;
        this.mPolicy = imeVisibilityPolicy;
    }

    public boolean onImeShowFlags(ImeTracker.Token token, int i) {
        if (this.mPolicy.mA11yRequestingNoSoftKeyboard || this.mPolicy.mImeHiddenByDisplayPolicy) {
            ImeTracker.forLogging().onFailed(token, 4);
            return false;
        }
        ImeTracker.forLogging().onProgress(token, 4);
        if ((i & 2) != 0) {
            this.mRequestedShowExplicitly = true;
            this.mShowForced = true;
        } else if ((i & 1) == 0) {
            this.mRequestedShowExplicitly = true;
        }
        return true;
    }

    public boolean canHideIme(ImeTracker.Token token, int i) {
        if ((i & 1) != 0 && (this.mRequestedShowExplicitly || this.mShowForced)) {
            ImeTracker.forLogging().onFailed(token, 6);
            return false;
        } else if (this.mShowForced && (i & 2) != 0) {
            ImeTracker.forLogging().onFailed(token, 7);
            return false;
        } else {
            ImeTracker.forLogging().onProgress(token, 7);
            return true;
        }
    }

    public int getImeShowFlags() {
        return this.mShowForced ? 3 : 1;
    }

    public void clearImeShowFlags() {
        this.mRequestedShowExplicitly = false;
        this.mShowForced = false;
        this.mInputShown = false;
    }

    public int computeImeDisplayId(ImeTargetWindowState imeTargetWindowState, int i) {
        int computeImeDisplayIdForTarget = InputMethodManagerService.computeImeDisplayIdForTarget(i, this.mImeDisplayValidator);
        imeTargetWindowState.setImeDisplayId(computeImeDisplayIdForTarget);
        this.mPolicy.setImeHiddenByDisplayPolicy(computeImeDisplayIdForTarget == -1);
        return computeImeDisplayIdForTarget;
    }

    public void requestImeVisibility(IBinder iBinder, boolean z) {
        ImeTargetWindowState orCreateWindowState = getOrCreateWindowState(iBinder);
        if (!this.mPolicy.mPendingA11yRequestingHideKeyboard) {
            orCreateWindowState.setRequestedImeVisible(z);
        } else {
            this.mPolicy.mPendingA11yRequestingHideKeyboard = false;
        }
        orCreateWindowState.setRequestImeToken(new Binder());
        setWindowStateInner(iBinder, orCreateWindowState);
    }

    public ImeTargetWindowState getOrCreateWindowState(IBinder iBinder) {
        ImeTargetWindowState imeTargetWindowState = this.mRequestWindowStateMap.get(iBinder);
        return imeTargetWindowState == null ? new ImeTargetWindowState(0, 0, false, false, false) : imeTargetWindowState;
    }

    public ImeTargetWindowState getWindowStateOrNull(IBinder iBinder) {
        return this.mRequestWindowStateMap.get(iBinder);
    }

    public void setWindowState(IBinder iBinder, ImeTargetWindowState imeTargetWindowState) {
        ImeTargetWindowState imeTargetWindowState2 = this.mRequestWindowStateMap.get(iBinder);
        if (imeTargetWindowState2 != null && imeTargetWindowState.hasEdiorFocused()) {
            imeTargetWindowState.setRequestedImeVisible(imeTargetWindowState2.mRequestedImeVisible);
        }
        setWindowStateInner(iBinder, imeTargetWindowState);
    }

    public final void setWindowStateInner(IBinder iBinder, ImeTargetWindowState imeTargetWindowState) {
        this.mRequestWindowStateMap.put(iBinder, imeTargetWindowState);
    }

    /* loaded from: classes.dex */
    public static class ImeVisibilityResult {
        public final int mReason;
        public final int mState;

        public ImeVisibilityResult(int i, int i2) {
            this.mState = i;
            this.mReason = i2;
        }

        public int getState() {
            return this.mState;
        }

        public int getReason() {
            return this.mReason;
        }
    }

    public ImeVisibilityResult computeState(ImeTargetWindowState imeTargetWindowState, boolean z) {
        int i = imeTargetWindowState.mSoftInputModeState & 15;
        boolean z2 = (imeTargetWindowState.mSoftInputModeState & FrameworkStatsLog.BOOT_TIME_EVENT_ELAPSED_TIME_REPORTED) == 16 || this.mService.mRes.getConfiguration().isLayoutSizeAtLeast(3);
        boolean z3 = (imeTargetWindowState.mSoftInputModeState & 256) != 0;
        if (imeTargetWindowState.hasEdiorFocused() && shouldRestoreImeVisibility(imeTargetWindowState)) {
            imeTargetWindowState.setRequestedImeVisible(true);
            setWindowStateInner(getWindowTokenFrom(imeTargetWindowState), imeTargetWindowState);
            return new ImeVisibilityResult(7, 23);
        }
        if (i != 0) {
            if (i == 1) {
                ImeTargetWindowState windowStateOrNull = getWindowStateOrNull(this.mService.mLastImeTargetWindow);
                if (windowStateOrNull != null) {
                    imeTargetWindowState.setRequestedImeVisible(windowStateOrNull.mRequestedImeVisible);
                }
            } else if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        if (i == 5) {
                            if (z) {
                                if (imeTargetWindowState.hasImeFocusChanged()) {
                                    return new ImeVisibilityResult(7, 8);
                                }
                            } else {
                                Slog.e("ImeVisibilityStateComputer", "SOFT_INPUT_STATE_ALWAYS_VISIBLE is ignored because there is no focused view that also returns true from View#onCheckIsTextEditor()");
                            }
                        }
                    } else if (z3) {
                        if (z) {
                            return new ImeVisibilityResult(7, 7);
                        }
                        Slog.e("ImeVisibilityStateComputer", "SOFT_INPUT_STATE_VISIBLE is ignored because there is no focused view that also returns true from View#onCheckIsTextEditor()");
                    }
                } else if (imeTargetWindowState.hasImeFocusChanged()) {
                    return new ImeVisibilityResult(5, 14);
                }
            } else if (z3) {
                return new ImeVisibilityResult(5, 13);
            }
        } else if (imeTargetWindowState.hasImeFocusChanged() && (!imeTargetWindowState.hasEdiorFocused() || !z2)) {
            if (WindowManager.LayoutParams.mayUseInputMethod(imeTargetWindowState.getWindowFlags())) {
                return new ImeVisibilityResult(6, 12);
            }
        } else if (imeTargetWindowState.hasEdiorFocused() && z2 && z3) {
            return new ImeVisibilityResult(7, 6);
        }
        if (!imeTargetWindowState.hasImeFocusChanged() && imeTargetWindowState.isStartInputByGainFocus()) {
            return new ImeVisibilityResult(5, 21);
        }
        if (!imeTargetWindowState.hasEdiorFocused() && this.mInputShown && imeTargetWindowState.isStartInputByGainFocus() && this.mService.mInputMethodDeviceConfigs.shouldHideImeWhenNoEditorFocus()) {
            return new ImeVisibilityResult(5, 33);
        }
        return null;
    }

    public IBinder getWindowTokenFrom(IBinder iBinder) {
        for (IBinder iBinder2 : this.mRequestWindowStateMap.keySet()) {
            if (this.mRequestWindowStateMap.get(iBinder2).getRequestImeToken() == iBinder) {
                return iBinder2;
            }
        }
        return this.mService.mCurFocusedWindow;
    }

    public IBinder getWindowTokenFrom(ImeTargetWindowState imeTargetWindowState) {
        for (IBinder iBinder : this.mRequestWindowStateMap.keySet()) {
            if (this.mRequestWindowStateMap.get(iBinder) == imeTargetWindowState) {
                return iBinder;
            }
        }
        return null;
    }

    public boolean shouldRestoreImeVisibility(ImeTargetWindowState imeTargetWindowState) {
        int softInputModeState = imeTargetWindowState.getSoftInputModeState();
        int i = softInputModeState & 15;
        if (i != 2) {
            if (i == 3) {
                return false;
            }
        } else if ((softInputModeState & 256) != 0) {
            return false;
        }
        return this.mWindowManagerInternal.shouldRestoreImeVisibility(getWindowTokenFrom(imeTargetWindowState));
    }

    public boolean isInputShown() {
        return this.mInputShown;
    }

    public void setInputShown(boolean z) {
        this.mInputShown = z;
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        protoOutputStream.write(1133871366154L, this.mRequestedShowExplicitly);
        protoOutputStream.write(1133871366155L, this.mShowForced);
        protoOutputStream.write(1133871366168L, this.mPolicy.isA11yRequestNoSoftKeyboard());
        protoOutputStream.write(1133871366156L, this.mInputShown);
    }

    public void dump(PrintWriter printWriter) {
        PrintWriterPrinter printWriterPrinter = new PrintWriterPrinter(printWriter);
        printWriterPrinter.println(" mRequestedShowExplicitly=" + this.mRequestedShowExplicitly + " mShowForced=" + this.mShowForced);
        StringBuilder sb = new StringBuilder();
        sb.append("  mImeHiddenByDisplayPolicy=");
        sb.append(this.mPolicy.isImeHiddenByDisplayPolicy());
        printWriterPrinter.println(sb.toString());
        printWriterPrinter.println("  mInputShown=" + this.mInputShown);
    }

    /* loaded from: classes.dex */
    public static class ImeVisibilityPolicy {
        public boolean mA11yRequestingNoSoftKeyboard;
        public boolean mImeHiddenByDisplayPolicy;
        public boolean mPendingA11yRequestingHideKeyboard;

        public void setImeHiddenByDisplayPolicy(boolean z) {
            this.mImeHiddenByDisplayPolicy = z;
        }

        public boolean isImeHiddenByDisplayPolicy() {
            return this.mImeHiddenByDisplayPolicy;
        }

        public void setA11yRequestNoSoftKeyboard(int i) {
            boolean z = (i & 3) == 1;
            this.mA11yRequestingNoSoftKeyboard = z;
            if (z) {
                this.mPendingA11yRequestingHideKeyboard = true;
            }
        }

        public boolean isA11yRequestNoSoftKeyboard() {
            return this.mA11yRequestingNoSoftKeyboard;
        }
    }

    public ImeVisibilityPolicy getImePolicy() {
        return this.mPolicy;
    }

    /* loaded from: classes.dex */
    public static class ImeTargetWindowState {
        public final boolean mHasFocusedEditor;
        public int mImeDisplayId = 0;
        public final boolean mImeFocusChanged;
        public final boolean mIsStartInputByGainFocus;
        public IBinder mRequestImeToken;
        public boolean mRequestedImeVisible;
        public final int mSoftInputModeState;
        public final int mWindowFlags;

        public ImeTargetWindowState(int i, int i2, boolean z, boolean z2, boolean z3) {
            this.mSoftInputModeState = i;
            this.mWindowFlags = i2;
            this.mImeFocusChanged = z;
            this.mHasFocusedEditor = z2;
            this.mIsStartInputByGainFocus = z3;
        }

        public boolean hasImeFocusChanged() {
            return this.mImeFocusChanged;
        }

        public boolean hasEdiorFocused() {
            return this.mHasFocusedEditor;
        }

        public boolean isStartInputByGainFocus() {
            return this.mIsStartInputByGainFocus;
        }

        public int getSoftInputModeState() {
            return this.mSoftInputModeState;
        }

        public int getWindowFlags() {
            return this.mWindowFlags;
        }

        public final void setImeDisplayId(int i) {
            this.mImeDisplayId = i;
        }

        public final void setRequestedImeVisible(boolean z) {
            this.mRequestedImeVisible = z;
        }

        public boolean isRequestedImeVisible() {
            return this.mRequestedImeVisible;
        }

        public void setRequestImeToken(IBinder iBinder) {
            this.mRequestImeToken = iBinder;
        }

        public IBinder getRequestImeToken() {
            return this.mRequestImeToken;
        }

        public String toString() {
            return "ImeTargetWindowState{ imeToken " + this.mRequestImeToken + " imeFocusChanged " + this.mImeFocusChanged + " hasEditorFocused " + this.mHasFocusedEditor + " requestedImeVisible " + this.mRequestedImeVisible + " imeDisplayId " + this.mImeDisplayId + " softInputModeState " + InputMethodDebug.softInputModeToString(this.mSoftInputModeState) + " isStartInputByGainFocus " + this.mIsStartInputByGainFocus + "}";
        }
    }
}
