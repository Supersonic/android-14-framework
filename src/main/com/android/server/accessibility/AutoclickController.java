package com.android.server.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MotionEvent;
/* loaded from: classes.dex */
public class AutoclickController extends BaseEventStreamTransformation {
    public static final String LOG_TAG = "AutoclickController";
    public ClickDelayObserver mClickDelayObserver;
    public ClickScheduler mClickScheduler;
    public final Context mContext;
    public final AccessibilityTraceManager mTrace;
    public final int mUserId;

    public AutoclickController(Context context, int i, AccessibilityTraceManager accessibilityTraceManager) {
        this.mTrace = accessibilityTraceManager;
        this.mContext = context;
        this.mUserId = i;
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void onMotionEvent(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (this.mTrace.isA11yTracingEnabledForTypes(4096L)) {
            this.mTrace.logTrace(LOG_TAG + ".onMotionEvent", 4096L, "event=" + motionEvent + ";rawEvent=" + motionEvent2 + ";policyFlags=" + i);
        }
        if (motionEvent.isFromSource(8194)) {
            if (this.mClickScheduler == null) {
                Handler handler = new Handler(this.mContext.getMainLooper());
                this.mClickScheduler = new ClickScheduler(handler, 600);
                ClickDelayObserver clickDelayObserver = new ClickDelayObserver(this.mUserId, handler);
                this.mClickDelayObserver = clickDelayObserver;
                clickDelayObserver.start(this.mContext.getContentResolver(), this.mClickScheduler);
            }
            handleMouseMotion(motionEvent, i);
        } else {
            ClickScheduler clickScheduler = this.mClickScheduler;
            if (clickScheduler != null) {
                clickScheduler.cancel();
            }
        }
        super.onMotionEvent(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void onKeyEvent(KeyEvent keyEvent, int i) {
        if (this.mTrace.isA11yTracingEnabledForTypes(4096L)) {
            this.mTrace.logTrace(LOG_TAG + ".onKeyEvent", 4096L, "event=" + keyEvent + ";policyFlags=" + i);
        }
        if (this.mClickScheduler != null) {
            if (KeyEvent.isModifierKey(keyEvent.getKeyCode())) {
                this.mClickScheduler.updateMetaState(keyEvent.getMetaState());
            } else {
                this.mClickScheduler.cancel();
            }
        }
        super.onKeyEvent(keyEvent, i);
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void clearEvents(int i) {
        ClickScheduler clickScheduler;
        if (i == 8194 && (clickScheduler = this.mClickScheduler) != null) {
            clickScheduler.cancel();
        }
        super.clearEvents(i);
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void onDestroy() {
        ClickDelayObserver clickDelayObserver = this.mClickDelayObserver;
        if (clickDelayObserver != null) {
            clickDelayObserver.stop();
            this.mClickDelayObserver = null;
        }
        ClickScheduler clickScheduler = this.mClickScheduler;
        if (clickScheduler != null) {
            clickScheduler.cancel();
            this.mClickScheduler = null;
        }
    }

    public final void handleMouseMotion(MotionEvent motionEvent, int i) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 7) {
            if (actionMasked == 9 || actionMasked == 10) {
                return;
            }
            this.mClickScheduler.cancel();
        } else if (motionEvent.getPointerCount() == 1) {
            this.mClickScheduler.update(motionEvent, i);
        } else {
            this.mClickScheduler.cancel();
        }
    }

    /* loaded from: classes.dex */
    public static final class ClickDelayObserver extends ContentObserver {
        public final Uri mAutoclickDelaySettingUri;
        public ClickScheduler mClickScheduler;
        public ContentResolver mContentResolver;
        public final int mUserId;

        public ClickDelayObserver(int i, Handler handler) {
            super(handler);
            this.mAutoclickDelaySettingUri = Settings.Secure.getUriFor("accessibility_autoclick_delay");
            this.mUserId = i;
        }

        public void start(ContentResolver contentResolver, ClickScheduler clickScheduler) {
            if (this.mContentResolver != null || this.mClickScheduler != null) {
                throw new IllegalStateException("Observer already started.");
            }
            if (contentResolver == null) {
                throw new NullPointerException("contentResolver not set.");
            }
            if (clickScheduler == null) {
                throw new NullPointerException("clickScheduler not set.");
            }
            this.mContentResolver = contentResolver;
            this.mClickScheduler = clickScheduler;
            contentResolver.registerContentObserver(this.mAutoclickDelaySettingUri, false, this, this.mUserId);
            onChange(true, this.mAutoclickDelaySettingUri);
        }

        public void stop() {
            ContentResolver contentResolver = this.mContentResolver;
            if (contentResolver == null || this.mClickScheduler == null) {
                throw new IllegalStateException("ClickDelayObserver not started.");
            }
            contentResolver.unregisterContentObserver(this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (this.mAutoclickDelaySettingUri.equals(uri)) {
                this.mClickScheduler.updateDelay(Settings.Secure.getIntForUser(this.mContentResolver, "accessibility_autoclick_delay", 600, this.mUserId));
            }
        }
    }

    /* loaded from: classes.dex */
    public final class ClickScheduler implements Runnable {
        public boolean mActive;
        public MotionEvent.PointerCoords mAnchorCoords;
        public int mDelay;
        public int mEventPolicyFlags;
        public Handler mHandler;
        public MotionEvent mLastMotionEvent = null;
        public int mMetaState;
        public long mScheduledClickTime;
        public MotionEvent.PointerCoords[] mTempPointerCoords;
        public MotionEvent.PointerProperties[] mTempPointerProperties;

        public ClickScheduler(Handler handler, int i) {
            this.mHandler = handler;
            resetInternalState();
            this.mDelay = i;
            this.mAnchorCoords = new MotionEvent.PointerCoords();
        }

        @Override // java.lang.Runnable
        public void run() {
            long uptimeMillis = SystemClock.uptimeMillis();
            long j = this.mScheduledClickTime;
            if (uptimeMillis < j) {
                this.mHandler.postDelayed(this, j - uptimeMillis);
                return;
            }
            sendClick();
            resetInternalState();
        }

        public void update(MotionEvent motionEvent, int i) {
            this.mMetaState = motionEvent.getMetaState();
            boolean detectMovement = detectMovement(motionEvent);
            cacheLastEvent(motionEvent, i, this.mLastMotionEvent == null || detectMovement);
            if (detectMovement) {
                rescheduleClick(this.mDelay);
            }
        }

        public void cancel() {
            if (this.mActive) {
                resetInternalState();
                this.mHandler.removeCallbacks(this);
            }
        }

        public void updateMetaState(int i) {
            this.mMetaState = i;
        }

        public void updateDelay(int i) {
            this.mDelay = i;
        }

        public final void rescheduleClick(int i) {
            long j = i;
            long uptimeMillis = SystemClock.uptimeMillis() + j;
            boolean z = this.mActive;
            if (z && uptimeMillis > this.mScheduledClickTime) {
                this.mScheduledClickTime = uptimeMillis;
                return;
            }
            if (z) {
                this.mHandler.removeCallbacks(this);
            }
            this.mActive = true;
            this.mScheduledClickTime = uptimeMillis;
            this.mHandler.postDelayed(this, j);
        }

        public final void cacheLastEvent(MotionEvent motionEvent, int i, boolean z) {
            MotionEvent motionEvent2 = this.mLastMotionEvent;
            if (motionEvent2 != null) {
                motionEvent2.recycle();
            }
            MotionEvent obtain = MotionEvent.obtain(motionEvent);
            this.mLastMotionEvent = obtain;
            this.mEventPolicyFlags = i;
            if (z) {
                this.mLastMotionEvent.getPointerCoords(obtain.getActionIndex(), this.mAnchorCoords);
            }
        }

        public final void resetInternalState() {
            this.mActive = false;
            MotionEvent motionEvent = this.mLastMotionEvent;
            if (motionEvent != null) {
                motionEvent.recycle();
                this.mLastMotionEvent = null;
            }
            this.mScheduledClickTime = -1L;
        }

        public final boolean detectMovement(MotionEvent motionEvent) {
            if (this.mLastMotionEvent == null) {
                return false;
            }
            int actionIndex = motionEvent.getActionIndex();
            return Math.hypot((double) (this.mAnchorCoords.x - motionEvent.getX(actionIndex)), (double) (this.mAnchorCoords.y - motionEvent.getY(actionIndex))) > 20.0d;
        }

        public final void sendClick() {
            if (this.mLastMotionEvent == null || AutoclickController.this.getNext() == null) {
                return;
            }
            int actionIndex = this.mLastMotionEvent.getActionIndex();
            if (this.mTempPointerProperties == null) {
                this.mTempPointerProperties = r2;
                MotionEvent.PointerProperties[] pointerPropertiesArr = {new MotionEvent.PointerProperties()};
            }
            this.mLastMotionEvent.getPointerProperties(actionIndex, this.mTempPointerProperties[0]);
            if (this.mTempPointerCoords == null) {
                this.mTempPointerCoords = r2;
                MotionEvent.PointerCoords[] pointerCoordsArr = {new MotionEvent.PointerCoords()};
            }
            this.mLastMotionEvent.getPointerCoords(actionIndex, this.mTempPointerCoords[0]);
            long uptimeMillis = SystemClock.uptimeMillis();
            MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 0, 1, this.mTempPointerProperties, this.mTempPointerCoords, this.mMetaState, 1, 1.0f, 1.0f, this.mLastMotionEvent.getDeviceId(), 0, this.mLastMotionEvent.getSource(), this.mLastMotionEvent.getFlags());
            MotionEvent obtain2 = MotionEvent.obtain(obtain);
            obtain2.setAction(11);
            obtain2.setActionButton(1);
            MotionEvent obtain3 = MotionEvent.obtain(obtain);
            obtain3.setAction(12);
            obtain3.setActionButton(1);
            obtain3.setButtonState(0);
            MotionEvent obtain4 = MotionEvent.obtain(obtain);
            obtain4.setAction(1);
            obtain4.setButtonState(0);
            AutoclickController.super.onMotionEvent(obtain, obtain, this.mEventPolicyFlags);
            obtain.recycle();
            AutoclickController.super.onMotionEvent(obtain2, obtain2, this.mEventPolicyFlags);
            obtain2.recycle();
            AutoclickController.super.onMotionEvent(obtain3, obtain3, this.mEventPolicyFlags);
            obtain3.recycle();
            AutoclickController.super.onMotionEvent(obtain4, obtain4, this.mEventPolicyFlags);
            obtain4.recycle();
        }

        public String toString() {
            return "ClickScheduler: { active=" + this.mActive + ", delay=" + this.mDelay + ", scheduledClickTime=" + this.mScheduledClickTime + ", anchor={x:" + this.mAnchorCoords.x + ", y:" + this.mAnchorCoords.y + "}, metastate=" + this.mMetaState + ", policyFlags=" + this.mEventPolicyFlags + ", lastMotionEvent=" + this.mLastMotionEvent + " }";
        }
    }
}
