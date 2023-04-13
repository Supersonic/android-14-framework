package com.android.server.accessibility.magnification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.MathUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.accessibility.IWindowMagnificationConnection;
import android.view.accessibility.IWindowMagnificationConnectionCallback;
import android.view.accessibility.MagnificationAnimationCallback;
import com.android.internal.accessibility.util.AccessibilityStatsLogUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.LocalServices;
import com.android.server.accessibility.AccessibilityTraceManager;
import com.android.server.accessibility.magnification.PanningScalingHandler;
import com.android.server.p014wm.WindowManagerInternal;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
/* loaded from: classes.dex */
public class WindowMagnificationManager implements PanningScalingHandler.MagnificationDelegate, WindowManagerInternal.AccessibilityControllerInternal.UiChangesForAccessibilityCallbacks {
    public final Callback mCallback;
    @GuardedBy({"mLock"})
    public ConnectionCallback mConnectionCallback;
    @GuardedBy({"mLock"})
    @VisibleForTesting
    WindowMagnificationConnectionWrapper mConnectionWrapper;
    public final Context mContext;
    public final Object mLock;
    public final MagnificationScaleProvider mScaleProvider;
    public final AccessibilityTraceManager mTrace;
    public int mConnectionState = 3;
    @GuardedBy({"mLock"})
    public SparseArray<WindowMagnifier> mWindowMagnifiers = new SparseArray<>();
    public boolean mMagnificationFollowTypingEnabled = true;
    @GuardedBy({"mLock"})
    public final SparseBooleanArray mIsImeVisibleArray = new SparseBooleanArray();
    public boolean mReceiverRegistered = false;
    @VisibleForTesting
    protected final BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() { // from class: com.android.server.accessibility.magnification.WindowMagnificationManager.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int displayId = context.getDisplayId();
            WindowMagnificationManager.this.removeMagnificationButton(displayId);
            WindowMagnificationManager.this.disableWindowMagnification(displayId, false, null);
        }
    };

    /* loaded from: classes.dex */
    public interface Callback {
        void onAccessibilityActionPerformed(int i);

        void onChangeMagnificationMode(int i, int i2);

        void onPerformScaleAction(int i, float f);

        void onSourceBoundsChanged(int i, Rect rect);

        void onWindowMagnificationActivationState(int i, boolean z);
    }

    public static String connectionStateToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return "UNKNOWN:" + i;
                    }
                    return "DISCONNECTED";
                }
                return "DISCONNECTING";
            }
            return "CONNECTED";
        }
        return "CONNECTING";
    }

    public WindowMagnificationManager(Context context, Object obj, Callback callback, AccessibilityTraceManager accessibilityTraceManager, MagnificationScaleProvider magnificationScaleProvider) {
        this.mContext = context;
        this.mLock = obj;
        this.mCallback = callback;
        this.mTrace = accessibilityTraceManager;
        this.mScaleProvider = magnificationScaleProvider;
    }

    public void setConnection(IWindowMagnificationConnection iWindowMagnificationConnection) {
        Object obj;
        synchronized (this.mLock) {
            WindowMagnificationConnectionWrapper windowMagnificationConnectionWrapper = this.mConnectionWrapper;
            if (windowMagnificationConnectionWrapper != null) {
                windowMagnificationConnectionWrapper.setConnectionCallback(null);
                ConnectionCallback connectionCallback = this.mConnectionCallback;
                if (connectionCallback != null) {
                    connectionCallback.mExpiredDeathRecipient = true;
                }
                this.mConnectionWrapper.unlinkToDeath(this.mConnectionCallback);
                this.mConnectionWrapper = null;
                if (this.mConnectionState != 0) {
                    setConnectionState(3);
                }
            }
            if (iWindowMagnificationConnection != null) {
                this.mConnectionWrapper = new WindowMagnificationConnectionWrapper(iWindowMagnificationConnection, this.mTrace);
            }
            if (this.mConnectionWrapper != null) {
                try {
                    ConnectionCallback connectionCallback2 = new ConnectionCallback();
                    this.mConnectionCallback = connectionCallback2;
                    this.mConnectionWrapper.linkToDeath(connectionCallback2);
                    this.mConnectionWrapper.setConnectionCallback(this.mConnectionCallback);
                    setConnectionState(1);
                    obj = this.mLock;
                } catch (RemoteException e) {
                    Slog.e("WindowMagnificationMgr", "setConnection failed", e);
                    this.mConnectionWrapper = null;
                    setConnectionState(3);
                    obj = this.mLock;
                }
                obj.notify();
            }
        }
    }

    public boolean isConnected() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mConnectionWrapper != null;
        }
        return z;
    }

    public boolean requestConnection(boolean z) {
        int i;
        if (this.mTrace.isA11yTracingEnabledForTypes(128L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTrace;
            accessibilityTraceManager.logTrace("WindowMagnificationMgr.requestWindowMagnificationConnection", 128L, "connect=" + z);
        }
        synchronized (this.mLock) {
            if (z) {
                try {
                    int i2 = this.mConnectionState;
                    if (i2 != 1 && i2 != 0) {
                    }
                    Slog.w("WindowMagnificationMgr", "requestConnection duplicated request: connect=" + z + ", mConnectionState=" + connectionStateToString(this.mConnectionState));
                    return false;
                } catch (Throwable th) {
                    throw th;
                }
            }
            if (z || ((i = this.mConnectionState) != 3 && i != 2)) {
                if (z) {
                    IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
                    if (!this.mReceiverRegistered) {
                        this.mContext.registerReceiver(this.mScreenStateReceiver, intentFilter);
                        this.mReceiverRegistered = true;
                    }
                } else {
                    disableAllWindowMagnifiers();
                    if (this.mReceiverRegistered) {
                        this.mContext.unregisterReceiver(this.mScreenStateReceiver);
                        this.mReceiverRegistered = false;
                    }
                }
                if (requestConnectionInternal(z)) {
                    setConnectionState(z ? 0 : 2);
                    return true;
                }
                setConnectionState(3);
                return false;
            }
            Slog.w("WindowMagnificationMgr", "requestConnection duplicated request: connect=" + z + ", mConnectionState=" + connectionStateToString(this.mConnectionState));
            return false;
        }
    }

    public final boolean requestConnectionInternal(boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            StatusBarManagerInternal statusBarManagerInternal = (StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class);
            if (statusBarManagerInternal != null) {
                return statusBarManagerInternal.requestWindowMagnificationConnection(z);
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public String getConnectionState() {
        return connectionStateToString(this.mConnectionState);
    }

    public final void setConnectionState(int i) {
        this.mConnectionState = i;
    }

    public void disableAllWindowMagnifiers() {
        synchronized (this.mLock) {
            for (int i = 0; i < this.mWindowMagnifiers.size(); i++) {
                this.mWindowMagnifiers.valueAt(i).disableWindowMagnificationInternal(null);
            }
            this.mWindowMagnifiers.clear();
        }
    }

    public void resetAllIfNeeded(int i) {
        synchronized (this.mLock) {
            for (int i2 = 0; i2 < this.mWindowMagnifiers.size(); i2++) {
                WindowMagnifier valueAt = this.mWindowMagnifiers.valueAt(i2);
                if (valueAt != null && valueAt.mEnabled && i == valueAt.getIdOfLastServiceToControl()) {
                    valueAt.disableWindowMagnificationInternal(null);
                }
            }
        }
    }

    public final void resetWindowMagnifiers() {
        synchronized (this.mLock) {
            for (int i = 0; i < this.mWindowMagnifiers.size(); i++) {
                this.mWindowMagnifiers.valueAt(i).reset();
            }
        }
    }

    @Override // com.android.server.p014wm.WindowManagerInternal.AccessibilityControllerInternal.UiChangesForAccessibilityCallbacks
    public void onRectangleOnScreenRequested(int i, int i2, int i3, int i4, int i5) {
        if (this.mMagnificationFollowTypingEnabled) {
            float f = (i2 + i4) / 2.0f;
            float f2 = (i3 + i5) / 2.0f;
            synchronized (this.mLock) {
                if (this.mIsImeVisibleArray.get(i, false) && !isPositionInSourceBounds(i, f, f2) && isTrackingTypingFocusEnabled(i)) {
                    moveWindowMagnifierToPositionInternal(i, f, f2, MagnificationAnimationCallback.STUB_ANIMATION_CALLBACK);
                }
            }
        }
    }

    public void setMagnificationFollowTypingEnabled(boolean z) {
        this.mMagnificationFollowTypingEnabled = z;
    }

    public int getIdOfLastServiceToMagnify(int i) {
        synchronized (this.mLock) {
            WindowMagnifier windowMagnifier = this.mWindowMagnifiers.get(i);
            if (windowMagnifier != null) {
                return windowMagnifier.mIdOfLastServiceToControl;
            }
            return -1;
        }
    }

    public void setTrackingTypingFocusEnabled(int i, boolean z) {
        synchronized (this.mLock) {
            WindowMagnifier windowMagnifier = this.mWindowMagnifiers.get(i);
            if (windowMagnifier == null) {
                return;
            }
            windowMagnifier.setTrackingTypingFocusEnabled(z);
        }
    }

    public final void enableAllTrackingTypingFocus() {
        synchronized (this.mLock) {
            for (int i = 0; i < this.mWindowMagnifiers.size(); i++) {
                this.mWindowMagnifiers.valueAt(i).setTrackingTypingFocusEnabled(true);
            }
        }
    }

    public final void pauseTrackingTypingFocusRecord(int i) {
        synchronized (this.mLock) {
            WindowMagnifier windowMagnifier = this.mWindowMagnifiers.get(i);
            if (windowMagnifier == null) {
                return;
            }
            windowMagnifier.pauseTrackingTypingFocusRecord();
        }
    }

    public void onImeWindowVisibilityChanged(int i, boolean z) {
        synchronized (this.mLock) {
            this.mIsImeVisibleArray.put(i, z);
        }
        if (z) {
            enableAllTrackingTypingFocus();
        } else {
            pauseTrackingTypingFocusRecord(i);
        }
    }

    public boolean isImeVisible(int i) {
        boolean z;
        synchronized (this.mLock) {
            z = this.mIsImeVisibleArray.get(i);
        }
        return z;
    }

    public void logTrackingTypingFocus(long j) {
        AccessibilityStatsLogUtils.logMagnificationFollowTypingFocusSession(j);
    }

    @Override // com.android.server.accessibility.magnification.PanningScalingHandler.MagnificationDelegate
    public boolean processScroll(int i, float f, float f2) {
        moveWindowMagnification(i, -f, -f2);
        setTrackingTypingFocusEnabled(i, false);
        return true;
    }

    @Override // com.android.server.accessibility.magnification.PanningScalingHandler.MagnificationDelegate
    public void setScale(int i, float f) {
        synchronized (this.mLock) {
            WindowMagnifier windowMagnifier = this.mWindowMagnifiers.get(i);
            if (windowMagnifier == null) {
                return;
            }
            windowMagnifier.setScale(f);
        }
    }

    public boolean enableWindowMagnification(int i, float f, float f2, float f3, MagnificationAnimationCallback magnificationAnimationCallback, int i2) {
        return enableWindowMagnification(i, f, f2, f3, magnificationAnimationCallback, 0, i2);
    }

    public boolean enableWindowMagnification(int i, float f, float f2, float f3, int i2) {
        return enableWindowMagnification(i, f, f2, f3, MagnificationAnimationCallback.STUB_ANIMATION_CALLBACK, i2, 0);
    }

    public boolean enableWindowMagnification(int i, float f, float f2, float f3, MagnificationAnimationCallback magnificationAnimationCallback, int i2, int i3) {
        boolean z;
        boolean enableWindowMagnificationInternal;
        synchronized (this.mLock) {
            WindowMagnifier windowMagnifier = this.mWindowMagnifiers.get(i);
            if (windowMagnifier == null) {
                windowMagnifier = createWindowMagnifier(i);
            }
            WindowMagnifier windowMagnifier2 = windowMagnifier;
            z = windowMagnifier2.mEnabled;
            enableWindowMagnificationInternal = windowMagnifier2.enableWindowMagnificationInternal(f, f2, f3, magnificationAnimationCallback, i2, i3);
        }
        if (enableWindowMagnificationInternal) {
            setTrackingTypingFocusEnabled(i, true);
            if (!z) {
                this.mCallback.onWindowMagnificationActivationState(i, true);
            }
        }
        return enableWindowMagnificationInternal;
    }

    public boolean disableWindowMagnification(int i, boolean z) {
        return disableWindowMagnification(i, z, MagnificationAnimationCallback.STUB_ANIMATION_CALLBACK);
    }

    public boolean disableWindowMagnification(int i, boolean z, MagnificationAnimationCallback magnificationAnimationCallback) {
        synchronized (this.mLock) {
            WindowMagnifier windowMagnifier = this.mWindowMagnifiers.get(i);
            if (windowMagnifier == null) {
                return false;
            }
            boolean disableWindowMagnificationInternal = windowMagnifier.disableWindowMagnificationInternal(magnificationAnimationCallback);
            if (z) {
                this.mWindowMagnifiers.delete(i);
            }
            if (disableWindowMagnificationInternal) {
                this.mCallback.onWindowMagnificationActivationState(i, false);
            }
            return disableWindowMagnificationInternal;
        }
    }

    public int pointersInWindow(int i, MotionEvent motionEvent) {
        synchronized (this.mLock) {
            WindowMagnifier windowMagnifier = this.mWindowMagnifiers.get(i);
            if (windowMagnifier == null) {
                return 0;
            }
            return windowMagnifier.pointersInWindow(motionEvent);
        }
    }

    @GuardedBy({"mLock"})
    public boolean isPositionInSourceBounds(int i, float f, float f2) {
        WindowMagnifier windowMagnifier = this.mWindowMagnifiers.get(i);
        if (windowMagnifier == null) {
            return false;
        }
        return windowMagnifier.isPositionInSourceBounds(f, f2);
    }

    public boolean isWindowMagnifierEnabled(int i) {
        synchronized (this.mLock) {
            WindowMagnifier windowMagnifier = this.mWindowMagnifiers.get(i);
            if (windowMagnifier == null) {
                return false;
            }
            return windowMagnifier.isEnabled();
        }
    }

    public float getPersistedScale(int i) {
        return MathUtils.constrain(this.mScaleProvider.getScale(i), 1.3f, 8.0f);
    }

    public void persistScale(int i) {
        float scale = getScale(i);
        if (scale < 1.3f) {
            return;
        }
        this.mScaleProvider.putScale(scale, i);
    }

    @Override // com.android.server.accessibility.magnification.PanningScalingHandler.MagnificationDelegate
    public float getScale(int i) {
        synchronized (this.mLock) {
            WindowMagnifier windowMagnifier = this.mWindowMagnifiers.get(i);
            if (windowMagnifier != null && windowMagnifier.mEnabled) {
                return windowMagnifier.getScale();
            }
            return 1.0f;
        }
    }

    public void moveWindowMagnification(int i, float f, float f2) {
        synchronized (this.mLock) {
            WindowMagnifier windowMagnifier = this.mWindowMagnifiers.get(i);
            if (windowMagnifier == null) {
                return;
            }
            windowMagnifier.move(f, f2);
        }
    }

    public boolean showMagnificationButton(int i, int i2) {
        WindowMagnificationConnectionWrapper windowMagnificationConnectionWrapper = this.mConnectionWrapper;
        return windowMagnificationConnectionWrapper != null && windowMagnificationConnectionWrapper.showMagnificationButton(i, i2);
    }

    public boolean removeMagnificationButton(int i) {
        WindowMagnificationConnectionWrapper windowMagnificationConnectionWrapper = this.mConnectionWrapper;
        return windowMagnificationConnectionWrapper != null && windowMagnificationConnectionWrapper.removeMagnificationButton(i);
    }

    public float getCenterX(int i) {
        synchronized (this.mLock) {
            WindowMagnifier windowMagnifier = this.mWindowMagnifiers.get(i);
            if (windowMagnifier != null && windowMagnifier.mEnabled) {
                return windowMagnifier.getCenterX();
            }
            return Float.NaN;
        }
    }

    public float getCenterY(int i) {
        synchronized (this.mLock) {
            WindowMagnifier windowMagnifier = this.mWindowMagnifiers.get(i);
            if (windowMagnifier != null && windowMagnifier.mEnabled) {
                return windowMagnifier.getCenterY();
            }
            return Float.NaN;
        }
    }

    public boolean isTrackingTypingFocusEnabled(int i) {
        synchronized (this.mLock) {
            WindowMagnifier windowMagnifier = this.mWindowMagnifiers.get(i);
            if (windowMagnifier == null) {
                return false;
            }
            return windowMagnifier.isTrackingTypingFocusEnabled();
        }
    }

    public void getMagnificationSourceBounds(int i, Region region) {
        synchronized (this.mLock) {
            WindowMagnifier windowMagnifier = this.mWindowMagnifiers.get(i);
            if (windowMagnifier != null && windowMagnifier.mEnabled) {
                region.set(windowMagnifier.mSourceBounds);
            }
            region.setEmpty();
        }
    }

    @GuardedBy({"mLock"})
    public final WindowMagnifier createWindowMagnifier(int i) {
        WindowMagnifier windowMagnifier = new WindowMagnifier(i, this);
        this.mWindowMagnifiers.put(i, windowMagnifier);
        return windowMagnifier;
    }

    public void onDisplayRemoved(int i) {
        disableWindowMagnification(i, true);
    }

    /* loaded from: classes.dex */
    public class ConnectionCallback extends IWindowMagnificationConnectionCallback.Stub implements IBinder.DeathRecipient {
        public boolean mExpiredDeathRecipient;

        public ConnectionCallback() {
            this.mExpiredDeathRecipient = false;
        }

        public void onWindowMagnifierBoundsChanged(int i, Rect rect) {
            if (WindowMagnificationManager.this.mTrace.isA11yTracingEnabledForTypes(256L)) {
                AccessibilityTraceManager accessibilityTraceManager = WindowMagnificationManager.this.mTrace;
                accessibilityTraceManager.logTrace("WindowMagnificationMgrConnectionCallback.onWindowMagnifierBoundsChanged", 256L, "displayId=" + i + ";bounds=" + rect);
            }
            synchronized (WindowMagnificationManager.this.mLock) {
                WindowMagnifier windowMagnifier = (WindowMagnifier) WindowMagnificationManager.this.mWindowMagnifiers.get(i);
                if (windowMagnifier == null) {
                    windowMagnifier = WindowMagnificationManager.this.createWindowMagnifier(i);
                }
                windowMagnifier.setMagnifierLocation(rect);
            }
        }

        public void onChangeMagnificationMode(int i, int i2) throws RemoteException {
            if (WindowMagnificationManager.this.mTrace.isA11yTracingEnabledForTypes(256L)) {
                AccessibilityTraceManager accessibilityTraceManager = WindowMagnificationManager.this.mTrace;
                accessibilityTraceManager.logTrace("WindowMagnificationMgrConnectionCallback.onChangeMagnificationMode", 256L, "displayId=" + i + ";mode=" + i2);
            }
            WindowMagnificationManager.this.mCallback.onChangeMagnificationMode(i, i2);
        }

        public void onSourceBoundsChanged(int i, Rect rect) {
            if (WindowMagnificationManager.this.mTrace.isA11yTracingEnabledForTypes(256L)) {
                AccessibilityTraceManager accessibilityTraceManager = WindowMagnificationManager.this.mTrace;
                accessibilityTraceManager.logTrace("WindowMagnificationMgrConnectionCallback.onSourceBoundsChanged", 256L, "displayId=" + i + ";source=" + rect);
            }
            synchronized (WindowMagnificationManager.this.mLock) {
                WindowMagnifier windowMagnifier = (WindowMagnifier) WindowMagnificationManager.this.mWindowMagnifiers.get(i);
                if (windowMagnifier == null) {
                    windowMagnifier = WindowMagnificationManager.this.createWindowMagnifier(i);
                }
                windowMagnifier.onSourceBoundsChanged(rect);
            }
            WindowMagnificationManager.this.mCallback.onSourceBoundsChanged(i, rect);
        }

        public void onPerformScaleAction(int i, float f) {
            if (WindowMagnificationManager.this.mTrace.isA11yTracingEnabledForTypes(256L)) {
                AccessibilityTraceManager accessibilityTraceManager = WindowMagnificationManager.this.mTrace;
                accessibilityTraceManager.logTrace("WindowMagnificationMgrConnectionCallback.onPerformScaleAction", 256L, "displayId=" + i + ";scale=" + f);
            }
            WindowMagnificationManager.this.mCallback.onPerformScaleAction(i, f);
        }

        public void onAccessibilityActionPerformed(int i) {
            if (WindowMagnificationManager.this.mTrace.isA11yTracingEnabledForTypes(256L)) {
                AccessibilityTraceManager accessibilityTraceManager = WindowMagnificationManager.this.mTrace;
                accessibilityTraceManager.logTrace("WindowMagnificationMgrConnectionCallback.onAccessibilityActionPerformed", 256L, "displayId=" + i);
            }
            WindowMagnificationManager.this.mCallback.onAccessibilityActionPerformed(i);
        }

        public void onMove(int i) {
            if (WindowMagnificationManager.this.mTrace.isA11yTracingEnabledForTypes(256L)) {
                AccessibilityTraceManager accessibilityTraceManager = WindowMagnificationManager.this.mTrace;
                accessibilityTraceManager.logTrace("WindowMagnificationMgrConnectionCallback.onMove", 256L, "displayId=" + i);
            }
            WindowMagnificationManager.this.setTrackingTypingFocusEnabled(i, false);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (WindowMagnificationManager.this.mLock) {
                Slog.w("WindowMagnificationMgr", "binderDied DeathRecipient :" + this.mExpiredDeathRecipient);
                if (this.mExpiredDeathRecipient) {
                    return;
                }
                WindowMagnificationManager.this.mConnectionWrapper.unlinkToDeath(this);
                WindowMagnificationManager windowMagnificationManager = WindowMagnificationManager.this;
                windowMagnificationManager.mConnectionWrapper = null;
                windowMagnificationManager.mConnectionCallback = null;
                WindowMagnificationManager.this.setConnectionState(3);
                WindowMagnificationManager.this.resetWindowMagnifiers();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class WindowMagnifier {
        public static final AtomicLongFieldUpdater<WindowMagnifier> SUM_TIME_UPDATER = AtomicLongFieldUpdater.newUpdater(WindowMagnifier.class, "mTrackingTypingFocusSumTime");
        public final int mDisplayId;
        public boolean mEnabled;
        public final WindowMagnificationManager mWindowMagnificationManager;
        public float mScale = 1.0f;
        public final Rect mBounds = new Rect();
        public final Rect mSourceBounds = new Rect();
        public int mIdOfLastServiceToControl = -1;
        public final PointF mMagnificationFrameOffsetRatio = new PointF(0.0f, 0.0f);
        public boolean mTrackingTypingFocusEnabled = true;
        public volatile long mTrackingTypingFocusStartTime = 0;
        public volatile long mTrackingTypingFocusSumTime = 0;

        public WindowMagnifier(int i, WindowMagnificationManager windowMagnificationManager) {
            this.mDisplayId = i;
            this.mWindowMagnificationManager = windowMagnificationManager;
        }

        public boolean enableWindowMagnificationInternal(float f, float f2, float f3, MagnificationAnimationCallback magnificationAnimationCallback, int i, int i2) {
            if (Float.isNaN(f)) {
                f = getScale();
            }
            float constrainScale = MagnificationScaleProvider.constrainScale(f);
            setMagnificationFrameOffsetRatioByWindowPosition(i);
            WindowMagnificationManager windowMagnificationManager = this.mWindowMagnificationManager;
            int i3 = this.mDisplayId;
            PointF pointF = this.mMagnificationFrameOffsetRatio;
            if (windowMagnificationManager.enableWindowMagnificationInternal(i3, constrainScale, f2, f3, pointF.x, pointF.y, magnificationAnimationCallback)) {
                this.mScale = constrainScale;
                this.mEnabled = true;
                this.mIdOfLastServiceToControl = i2;
                return true;
            }
            return false;
        }

        public void setMagnificationFrameOffsetRatioByWindowPosition(int i) {
            if (i == 0) {
                this.mMagnificationFrameOffsetRatio.set(0.0f, 0.0f);
            } else if (i != 1) {
            } else {
                this.mMagnificationFrameOffsetRatio.set(-1.0f, -1.0f);
            }
        }

        public boolean disableWindowMagnificationInternal(MagnificationAnimationCallback magnificationAnimationCallback) {
            if (this.mEnabled && this.mWindowMagnificationManager.disableWindowMagnificationInternal(this.mDisplayId, magnificationAnimationCallback)) {
                this.mEnabled = false;
                this.mIdOfLastServiceToControl = -1;
                this.mTrackingTypingFocusEnabled = false;
                pauseTrackingTypingFocusRecord();
                return true;
            }
            return false;
        }

        @GuardedBy({"mLock"})
        public void setScale(float f) {
            if (this.mEnabled) {
                float constrainScale = MagnificationScaleProvider.constrainScale(f);
                if (Float.compare(this.mScale, constrainScale) == 0 || !this.mWindowMagnificationManager.setScaleInternal(this.mDisplayId, f)) {
                    return;
                }
                this.mScale = constrainScale;
            }
        }

        @GuardedBy({"mLock"})
        public float getScale() {
            return this.mScale;
        }

        @GuardedBy({"mLock"})
        public void setMagnifierLocation(Rect rect) {
            this.mBounds.set(rect);
        }

        public int getIdOfLastServiceToControl() {
            return this.mIdOfLastServiceToControl;
        }

        public int pointersInWindow(MotionEvent motionEvent) {
            int pointerCount = motionEvent.getPointerCount();
            int i = 0;
            for (int i2 = 0; i2 < pointerCount; i2++) {
                if (this.mBounds.contains((int) motionEvent.getX(i2), (int) motionEvent.getY(i2))) {
                    i++;
                }
            }
            return i;
        }

        public boolean isPositionInSourceBounds(float f, float f2) {
            return this.mSourceBounds.contains((int) f, (int) f2);
        }

        public void setTrackingTypingFocusEnabled(boolean z) {
            if (this.mWindowMagnificationManager.isWindowMagnifierEnabled(this.mDisplayId) && this.mWindowMagnificationManager.isImeVisible(this.mDisplayId) && z) {
                startTrackingTypingFocusRecord();
            }
            if (this.mTrackingTypingFocusEnabled && !z) {
                stopAndLogTrackingTypingFocusRecordIfNeeded();
            }
            this.mTrackingTypingFocusEnabled = z;
        }

        public boolean isTrackingTypingFocusEnabled() {
            return this.mTrackingTypingFocusEnabled;
        }

        public void startTrackingTypingFocusRecord() {
            if (this.mTrackingTypingFocusStartTime == 0) {
                this.mTrackingTypingFocusStartTime = SystemClock.uptimeMillis();
            }
        }

        public void pauseTrackingTypingFocusRecord() {
            if (this.mTrackingTypingFocusStartTime != 0) {
                SUM_TIME_UPDATER.addAndGet(this, SystemClock.uptimeMillis() - this.mTrackingTypingFocusStartTime);
                this.mTrackingTypingFocusStartTime = 0L;
            }
        }

        public void stopAndLogTrackingTypingFocusRecordIfNeeded() {
            if (this.mTrackingTypingFocusStartTime == 0 && this.mTrackingTypingFocusSumTime == 0) {
                return;
            }
            this.mWindowMagnificationManager.logTrackingTypingFocus(this.mTrackingTypingFocusSumTime + (this.mTrackingTypingFocusStartTime != 0 ? SystemClock.uptimeMillis() - this.mTrackingTypingFocusStartTime : 0L));
            this.mTrackingTypingFocusStartTime = 0L;
            this.mTrackingTypingFocusSumTime = 0L;
        }

        public boolean isEnabled() {
            return this.mEnabled;
        }

        @GuardedBy({"mLock"})
        public void move(float f, float f2) {
            this.mWindowMagnificationManager.moveWindowMagnifierInternal(this.mDisplayId, f, f2);
        }

        @GuardedBy({"mLock"})
        public void reset() {
            this.mEnabled = false;
            this.mIdOfLastServiceToControl = -1;
            this.mSourceBounds.setEmpty();
        }

        @GuardedBy({"mLock"})
        public void onSourceBoundsChanged(Rect rect) {
            this.mSourceBounds.set(rect);
        }

        @GuardedBy({"mLock"})
        public float getCenterX() {
            return this.mSourceBounds.exactCenterX();
        }

        @GuardedBy({"mLock"})
        public float getCenterY() {
            return this.mSourceBounds.exactCenterY();
        }
    }

    @GuardedBy({"mLock"})
    public final boolean enableWindowMagnificationInternal(int i, float f, float f2, float f3, float f4, float f5, MagnificationAnimationCallback magnificationAnimationCallback) {
        long uptimeMillis = SystemClock.uptimeMillis() + 100;
        while (this.mConnectionState == 0 && SystemClock.uptimeMillis() < uptimeMillis) {
            try {
                this.mLock.wait(uptimeMillis - SystemClock.uptimeMillis());
            } catch (InterruptedException unused) {
            }
        }
        WindowMagnificationConnectionWrapper windowMagnificationConnectionWrapper = this.mConnectionWrapper;
        if (windowMagnificationConnectionWrapper == null) {
            Slog.w("WindowMagnificationMgr", "enableWindowMagnificationInternal mConnectionWrapper is null. mConnectionState=" + connectionStateToString(this.mConnectionState));
            return false;
        }
        return windowMagnificationConnectionWrapper.enableWindowMagnification(i, f, f2, f3, f4, f5, magnificationAnimationCallback);
    }

    public final boolean setScaleInternal(int i, float f) {
        WindowMagnificationConnectionWrapper windowMagnificationConnectionWrapper = this.mConnectionWrapper;
        return windowMagnificationConnectionWrapper != null && windowMagnificationConnectionWrapper.setScale(i, f);
    }

    @GuardedBy({"mLock"})
    public final boolean disableWindowMagnificationInternal(int i, MagnificationAnimationCallback magnificationAnimationCallback) {
        WindowMagnificationConnectionWrapper windowMagnificationConnectionWrapper = this.mConnectionWrapper;
        if (windowMagnificationConnectionWrapper == null) {
            Slog.w("WindowMagnificationMgr", "mConnectionWrapper is null");
            return false;
        }
        return windowMagnificationConnectionWrapper.disableWindowMagnification(i, magnificationAnimationCallback);
    }

    @GuardedBy({"mLock"})
    public final boolean moveWindowMagnifierInternal(int i, float f, float f2) {
        WindowMagnificationConnectionWrapper windowMagnificationConnectionWrapper = this.mConnectionWrapper;
        return windowMagnificationConnectionWrapper != null && windowMagnificationConnectionWrapper.moveWindowMagnifier(i, f, f2);
    }

    @GuardedBy({"mLock"})
    public final boolean moveWindowMagnifierToPositionInternal(int i, float f, float f2, MagnificationAnimationCallback magnificationAnimationCallback) {
        WindowMagnificationConnectionWrapper windowMagnificationConnectionWrapper = this.mConnectionWrapper;
        return windowMagnificationConnectionWrapper != null && windowMagnificationConnectionWrapper.moveWindowMagnifierToPosition(i, f, f2, magnificationAnimationCallback);
    }
}
