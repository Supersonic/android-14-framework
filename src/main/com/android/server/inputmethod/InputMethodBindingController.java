package com.android.server.inputmethod;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.p000pm.PackageManagerInternal;
import android.graphics.Matrix;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseArray;
import android.view.InputChannel;
import android.view.inputmethod.InputMethodInfo;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.inputmethod.IInputMethod;
import com.android.internal.inputmethod.IInputMethodSession;
import com.android.internal.inputmethod.InputBindResult;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.inputmethod.InputMethodUtils;
import com.android.server.p014wm.WindowManagerInternal;
import java.util.concurrent.CountDownLatch;
/* loaded from: classes.dex */
public final class InputMethodBindingController {
    @VisibleForTesting
    static final int IME_CONNECTION_BIND_FLAGS = 1082654725;
    @VisibleForTesting
    static final int IME_VISIBLE_BIND_FLAGS = 738201601;
    public static final String TAG = "InputMethodBindingController";
    public final Context mContext;
    @GuardedBy({"ImfLock.class"})
    public String mCurId;
    @GuardedBy({"ImfLock.class"})
    public Intent mCurIntent;
    @GuardedBy({"ImfLock.class"})
    public IInputMethodInvoker mCurMethod;
    @GuardedBy({"ImfLock.class"})
    public int mCurMethodUid;
    @GuardedBy({"ImfLock.class"})
    public int mCurSeq;
    @GuardedBy({"ImfLock.class"})
    public IBinder mCurToken;
    @GuardedBy({"ImfLock.class"})
    public boolean mHasConnection;
    public final int mImeConnectionBindFlags;
    @GuardedBy({"ImfLock.class"})
    public long mLastBindTime;
    public CountDownLatch mLatchForTesting;
    @GuardedBy({"ImfLock.class"})
    public final ServiceConnection mMainConnection;
    public final ArrayMap<String, InputMethodInfo> mMethodMap;
    public final PackageManagerInternal mPackageManagerInternal;
    @GuardedBy({"ImfLock.class"})
    public String mSelectedMethodId;
    public final InputMethodManagerService mService;
    public final InputMethodUtils.InputMethodSettings mSettings;
    @GuardedBy({"ImfLock.class"})
    public boolean mSupportsStylusHw;
    @GuardedBy({"ImfLock.class"})
    public boolean mVisibleBound;
    @GuardedBy({"ImfLock.class"})
    public final ServiceConnection mVisibleConnection;
    public final WindowManagerInternal mWindowManagerInternal;

    public InputMethodBindingController(InputMethodManagerService inputMethodManagerService) {
        this(inputMethodManagerService, IME_CONNECTION_BIND_FLAGS, null);
    }

    public InputMethodBindingController(InputMethodManagerService inputMethodManagerService, int i, CountDownLatch countDownLatch) {
        this.mCurMethodUid = -1;
        this.mVisibleConnection = new ServiceConnection() { // from class: com.android.server.inputmethod.InputMethodBindingController.1
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            }

            @Override // android.content.ServiceConnection
            public void onBindingDied(ComponentName componentName) {
                synchronized (ImfLock.class) {
                    InputMethodBindingController.this.mService.invalidateAutofillSessionLocked();
                    if (InputMethodBindingController.this.isVisibleBound()) {
                        InputMethodBindingController.this.unbindVisibleConnection();
                    }
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                synchronized (ImfLock.class) {
                    InputMethodBindingController.this.mService.invalidateAutofillSessionLocked();
                }
            }
        };
        this.mMainConnection = new ServiceConnection() { // from class: com.android.server.inputmethod.InputMethodBindingController.2
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Trace.traceBegin(32L, "IMMS.onServiceConnected");
                synchronized (ImfLock.class) {
                    if (InputMethodBindingController.this.mCurIntent != null && componentName.equals(InputMethodBindingController.this.mCurIntent.getComponent())) {
                        InputMethodBindingController.this.mCurMethod = IInputMethodInvoker.create(IInputMethod.Stub.asInterface(iBinder));
                        updateCurrentMethodUid();
                        if (InputMethodBindingController.this.mCurToken == null) {
                            Slog.w(InputMethodBindingController.TAG, "Service connected without a token!");
                            InputMethodBindingController.this.unbindCurrentMethod();
                            Trace.traceEnd(32L);
                            return;
                        }
                        InputMethodBindingController.this.mSupportsStylusHw = ((InputMethodInfo) InputMethodBindingController.this.mMethodMap.get(InputMethodBindingController.this.mSelectedMethodId)).supportsStylusHandwriting();
                        InputMethodBindingController.this.mService.initializeImeLocked(InputMethodBindingController.this.mCurMethod, InputMethodBindingController.this.mCurToken);
                        InputMethodBindingController.this.mService.scheduleNotifyImeUidToAudioService(InputMethodBindingController.this.mCurMethodUid);
                        InputMethodBindingController.this.mService.reRequestCurrentClientSessionLocked();
                        InputMethodBindingController.this.mService.performOnCreateInlineSuggestionsRequestLocked();
                    }
                    InputMethodBindingController.this.mService.scheduleResetStylusHandwriting();
                    Trace.traceEnd(32L);
                    if (InputMethodBindingController.this.mLatchForTesting != null) {
                        InputMethodBindingController.this.mLatchForTesting.countDown();
                    }
                }
            }

            @GuardedBy({"ImfLock.class"})
            public final void updateCurrentMethodUid() {
                String packageName = InputMethodBindingController.this.mCurIntent.getComponent().getPackageName();
                int packageUid = InputMethodBindingController.this.mPackageManagerInternal.getPackageUid(packageName, 0L, InputMethodBindingController.this.mSettings.getCurrentUserId());
                if (packageUid < 0) {
                    String str = InputMethodBindingController.TAG;
                    Slog.e(str, "Failed to get UID for package=" + packageName);
                    InputMethodBindingController.this.mCurMethodUid = -1;
                    return;
                }
                InputMethodBindingController.this.mCurMethodUid = packageUid;
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                synchronized (ImfLock.class) {
                    if (InputMethodBindingController.this.mCurMethod != null && InputMethodBindingController.this.mCurIntent != null && componentName.equals(InputMethodBindingController.this.mCurIntent.getComponent())) {
                        InputMethodBindingController.this.mLastBindTime = SystemClock.uptimeMillis();
                        InputMethodBindingController.this.clearCurMethodAndSessions();
                        InputMethodBindingController.this.mService.clearInputShownLocked();
                        InputMethodBindingController.this.mService.unbindCurrentClientLocked(3);
                    }
                }
            }
        };
        this.mService = inputMethodManagerService;
        this.mContext = inputMethodManagerService.mContext;
        this.mMethodMap = inputMethodManagerService.mMethodMap;
        this.mSettings = inputMethodManagerService.mSettings;
        this.mPackageManagerInternal = inputMethodManagerService.mPackageManagerInternal;
        this.mWindowManagerInternal = inputMethodManagerService.mWindowManagerInternal;
        this.mImeConnectionBindFlags = i;
        this.mLatchForTesting = countDownLatch;
    }

    @GuardedBy({"ImfLock.class"})
    public long getLastBindTime() {
        return this.mLastBindTime;
    }

    @GuardedBy({"ImfLock.class"})
    public boolean hasConnection() {
        return this.mHasConnection;
    }

    @GuardedBy({"ImfLock.class"})
    public String getCurId() {
        return this.mCurId;
    }

    @GuardedBy({"ImfLock.class"})
    public String getSelectedMethodId() {
        return this.mSelectedMethodId;
    }

    @GuardedBy({"ImfLock.class"})
    public void setSelectedMethodId(String str) {
        this.mSelectedMethodId = str;
    }

    @GuardedBy({"ImfLock.class"})
    public IBinder getCurToken() {
        return this.mCurToken;
    }

    @GuardedBy({"ImfLock.class"})
    public Intent getCurIntent() {
        return this.mCurIntent;
    }

    @GuardedBy({"ImfLock.class"})
    public int getSequenceNumber() {
        return this.mCurSeq;
    }

    @GuardedBy({"ImfLock.class"})
    public void advanceSequenceNumber() {
        int i = this.mCurSeq + 1;
        this.mCurSeq = i;
        if (i <= 0) {
            this.mCurSeq = 1;
        }
    }

    @GuardedBy({"ImfLock.class"})
    public IInputMethodInvoker getCurMethod() {
        return this.mCurMethod;
    }

    @GuardedBy({"ImfLock.class"})
    public int getCurMethodUid() {
        return this.mCurMethodUid;
    }

    @GuardedBy({"ImfLock.class"})
    public boolean isVisibleBound() {
        return this.mVisibleBound;
    }

    public boolean supportsStylusHandwriting() {
        return this.mSupportsStylusHw;
    }

    @GuardedBy({"ImfLock.class"})
    public void unbindCurrentMethod() {
        if (isVisibleBound()) {
            unbindVisibleConnection();
        }
        if (hasConnection()) {
            unbindMainConnection();
        }
        if (getCurToken() != null) {
            removeCurrentToken();
            this.mService.resetSystemUiLocked();
        }
        this.mCurId = null;
        clearCurMethodAndSessions();
    }

    @GuardedBy({"ImfLock.class"})
    public final void clearCurMethodAndSessions() {
        this.mService.clearClientSessionsLocked();
        this.mCurMethod = null;
        this.mCurMethodUid = -1;
    }

    @GuardedBy({"ImfLock.class"})
    public final void removeCurrentToken() {
        this.mWindowManagerInternal.removeWindowToken(this.mCurToken, false, false, this.mService.getCurTokenDisplayIdLocked());
        this.mCurToken = null;
    }

    @GuardedBy({"ImfLock.class"})
    public InputBindResult bindCurrentMethod() {
        String str = this.mSelectedMethodId;
        if (str == null) {
            Slog.e(TAG, "mSelectedMethodId is null!");
            return InputBindResult.NO_IME;
        }
        InputMethodInfo inputMethodInfo = this.mMethodMap.get(str);
        if (inputMethodInfo == null) {
            throw new IllegalArgumentException("Unknown id: " + this.mSelectedMethodId);
        }
        this.mCurIntent = createImeBindingIntent(inputMethodInfo.getComponent());
        if (bindCurrentInputMethodServiceMainConnection()) {
            this.mCurId = inputMethodInfo.getId();
            this.mLastBindTime = SystemClock.uptimeMillis();
            addFreshWindowToken();
            return new InputBindResult(2, (IInputMethodSession) null, (SparseArray) null, (InputChannel) null, this.mCurId, this.mCurSeq, (Matrix) null, false);
        }
        Slog.w("InputMethodManagerService", "Failure connecting to input method service: " + this.mCurIntent);
        this.mCurIntent = null;
        return InputBindResult.IME_NOT_CONNECTED;
    }

    public final Intent createImeBindingIntent(ComponentName componentName) {
        Intent intent = new Intent("android.view.InputMethod");
        intent.setComponent(componentName);
        intent.putExtra("android.intent.extra.client_label", 17040469);
        intent.putExtra("android.intent.extra.client_intent", PendingIntent.getActivity(this.mContext, 0, new Intent("android.settings.INPUT_METHOD_SETTINGS"), 67108864));
        return intent;
    }

    @GuardedBy({"ImfLock.class"})
    public final void addFreshWindowToken() {
        int displayIdToShowImeLocked = this.mService.getDisplayIdToShowImeLocked();
        this.mCurToken = new Binder();
        this.mService.setCurTokenDisplayIdLocked(displayIdToShowImeLocked);
        this.mWindowManagerInternal.addWindowToken(this.mCurToken, 2011, displayIdToShowImeLocked, null);
    }

    @GuardedBy({"ImfLock.class"})
    public final void unbindMainConnection() {
        this.mContext.unbindService(this.mMainConnection);
        this.mHasConnection = false;
    }

    @GuardedBy({"ImfLock.class"})
    public void unbindVisibleConnection() {
        this.mContext.unbindService(this.mVisibleConnection);
        this.mVisibleBound = false;
    }

    @GuardedBy({"ImfLock.class"})
    public final boolean bindCurrentInputMethodService(ServiceConnection serviceConnection, int i) {
        Intent intent = this.mCurIntent;
        if (intent == null || serviceConnection == null) {
            String str = TAG;
            Slog.e(str, "--- bind failed: service = " + this.mCurIntent + ", conn = " + serviceConnection);
            return false;
        }
        return this.mContext.bindServiceAsUser(intent, serviceConnection, i, new UserHandle(this.mSettings.getCurrentUserId()));
    }

    @GuardedBy({"ImfLock.class"})
    public final boolean bindCurrentInputMethodServiceMainConnection() {
        boolean bindCurrentInputMethodService = bindCurrentInputMethodService(this.mMainConnection, this.mImeConnectionBindFlags);
        this.mHasConnection = bindCurrentInputMethodService;
        return bindCurrentInputMethodService;
    }

    @GuardedBy({"ImfLock.class"})
    public void setCurrentMethodVisible() {
        if (this.mCurMethod != null) {
            if (!hasConnection() || isVisibleBound()) {
                return;
            }
            this.mVisibleBound = bindCurrentInputMethodService(this.mVisibleConnection, IME_VISIBLE_BIND_FLAGS);
        } else if (!hasConnection()) {
            bindCurrentMethod();
        } else {
            long uptimeMillis = SystemClock.uptimeMillis() - this.mLastBindTime;
            if (uptimeMillis >= BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS) {
                EventLog.writeEvent(32000, getSelectedMethodId(), Long.valueOf(uptimeMillis), 1);
                Slog.w(TAG, "Force disconnect/connect to the IME in setCurrentMethodVisible()");
                unbindMainConnection();
                bindCurrentInputMethodServiceMainConnection();
            }
        }
    }

    @GuardedBy({"ImfLock.class"})
    public void setCurrentMethodNotVisible() {
        if (isVisibleBound()) {
            unbindVisibleConnection();
        }
    }
}
