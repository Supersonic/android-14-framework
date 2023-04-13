package com.android.server.accessibility;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Pools;
import android.util.Slog;
import android.view.KeyEvent;
import com.android.server.policy.WindowManagerPolicy;
/* loaded from: classes.dex */
public class KeyboardInterceptor extends BaseEventStreamTransformation implements Handler.Callback {
    public final AccessibilityManagerService mAms;
    public KeyEventHolder mEventQueueEnd;
    public KeyEventHolder mEventQueueStart;
    public final Handler mHandler = new Handler(this);
    public final WindowManagerPolicy mPolicy;

    public KeyboardInterceptor(AccessibilityManagerService accessibilityManagerService, WindowManagerPolicy windowManagerPolicy) {
        this.mAms = accessibilityManagerService;
        this.mPolicy = windowManagerPolicy;
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void onKeyEvent(KeyEvent keyEvent, int i) {
        if (this.mAms.getTraceManager().isA11yTracingEnabledForTypes(4096L)) {
            AccessibilityTraceManager traceManager = this.mAms.getTraceManager();
            traceManager.logTrace("KeyboardInterceptor.onKeyEvent", 4096L, "event=" + keyEvent + ";policyFlags=" + i);
        }
        long eventDelay = getEventDelay(keyEvent, i);
        int i2 = (eventDelay > 0L ? 1 : (eventDelay == 0L ? 0 : -1));
        if (i2 < 0) {
            return;
        }
        if (i2 > 0 || this.mEventQueueStart != null) {
            addEventToQueue(keyEvent, i, eventDelay);
        } else {
            this.mAms.notifyKeyEvent(keyEvent, i);
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        if (message.what != 1) {
            Slog.e("KeyboardInterceptor", "Unexpected message type");
            return false;
        }
        processQueuedEvents();
        if (this.mEventQueueStart != null) {
            scheduleProcessQueuedEvents();
        }
        return true;
    }

    public final void addEventToQueue(KeyEvent keyEvent, int i, long j) {
        long uptimeMillis = SystemClock.uptimeMillis() + j;
        if (this.mEventQueueStart == null) {
            KeyEventHolder obtain = KeyEventHolder.obtain(keyEvent, i, uptimeMillis);
            this.mEventQueueStart = obtain;
            this.mEventQueueEnd = obtain;
            scheduleProcessQueuedEvents();
            return;
        }
        KeyEventHolder obtain2 = KeyEventHolder.obtain(keyEvent, i, uptimeMillis);
        KeyEventHolder keyEventHolder = this.mEventQueueStart;
        obtain2.next = keyEventHolder;
        keyEventHolder.previous = obtain2;
        this.mEventQueueStart = obtain2;
    }

    public final void scheduleProcessQueuedEvents() {
        if (this.mHandler.sendEmptyMessageAtTime(1, this.mEventQueueEnd.dispatchTime)) {
            return;
        }
        Slog.e("KeyboardInterceptor", "Failed to schedule key event");
    }

    public final void processQueuedEvents() {
        long uptimeMillis = SystemClock.uptimeMillis();
        while (true) {
            KeyEventHolder keyEventHolder = this.mEventQueueEnd;
            if (keyEventHolder == null || keyEventHolder.dispatchTime > uptimeMillis) {
                return;
            }
            long eventDelay = getEventDelay(keyEventHolder.event, keyEventHolder.policyFlags);
            int i = (eventDelay > 0L ? 1 : (eventDelay == 0L ? 0 : -1));
            if (i > 0) {
                this.mEventQueueEnd.dispatchTime = uptimeMillis + eventDelay;
                return;
            }
            if (i == 0) {
                AccessibilityManagerService accessibilityManagerService = this.mAms;
                KeyEventHolder keyEventHolder2 = this.mEventQueueEnd;
                accessibilityManagerService.notifyKeyEvent(keyEventHolder2.event, keyEventHolder2.policyFlags);
            }
            KeyEventHolder keyEventHolder3 = this.mEventQueueEnd;
            KeyEventHolder keyEventHolder4 = keyEventHolder3.previous;
            this.mEventQueueEnd = keyEventHolder4;
            if (keyEventHolder4 != null) {
                keyEventHolder4.next = null;
            }
            keyEventHolder3.recycle();
            if (this.mEventQueueEnd == null) {
                this.mEventQueueStart = null;
            }
        }
    }

    public final long getEventDelay(KeyEvent keyEvent, int i) {
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == 25 || keyCode == 24) {
            return this.mPolicy.interceptKeyBeforeDispatching(null, keyEvent, i);
        }
        return 0L;
    }

    /* loaded from: classes.dex */
    public static class KeyEventHolder {
        public static final Pools.SimplePool<KeyEventHolder> sPool = new Pools.SimplePool<>(32);
        public long dispatchTime;
        public KeyEvent event;
        public KeyEventHolder next;
        public int policyFlags;
        public KeyEventHolder previous;

        public static KeyEventHolder obtain(KeyEvent keyEvent, int i, long j) {
            KeyEventHolder keyEventHolder = (KeyEventHolder) sPool.acquire();
            if (keyEventHolder == null) {
                keyEventHolder = new KeyEventHolder();
            }
            keyEventHolder.event = KeyEvent.obtain(keyEvent);
            keyEventHolder.policyFlags = i;
            keyEventHolder.dispatchTime = j;
            return keyEventHolder;
        }

        public void recycle() {
            this.event.recycle();
            this.event = null;
            this.policyFlags = 0;
            this.dispatchTime = 0L;
            this.next = null;
            this.previous = null;
            sPool.release(this);
        }
    }
}
