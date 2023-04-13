package com.android.server.accessibility;

import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.ArrayMap;
import android.util.Pools;
import android.util.Slog;
import android.view.InputEventConsistencyVerifier;
import android.view.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class KeyEventDispatcher implements Handler.Callback {
    public final Handler mHandlerToSendKeyEventsToInputFilter;
    public Handler mKeyEventTimeoutHandler;
    public final Object mLock;
    public final int mMessageTypeForSendKeyEvent;
    public final Pools.Pool<PendingKeyEvent> mPendingEventPool = new Pools.SimplePool(10);
    public final Map<KeyEventFilter, ArrayList<PendingKeyEvent>> mPendingEventsMap = new ArrayMap();
    public final PowerManager mPowerManager;
    public final InputEventConsistencyVerifier mSentEventsVerifier;

    /* loaded from: classes.dex */
    public interface KeyEventFilter {
        boolean onKeyEvent(KeyEvent keyEvent, int i);
    }

    public KeyEventDispatcher(Handler handler, int i, Object obj, PowerManager powerManager) {
        if (InputEventConsistencyVerifier.isInstrumentationEnabled()) {
            this.mSentEventsVerifier = new InputEventConsistencyVerifier(this, 0, KeyEventDispatcher.class.getSimpleName());
        } else {
            this.mSentEventsVerifier = null;
        }
        this.mHandlerToSendKeyEventsToInputFilter = handler;
        this.mMessageTypeForSendKeyEvent = i;
        this.mKeyEventTimeoutHandler = new Handler(handler.getLooper(), this);
        this.mLock = obj;
        this.mPowerManager = powerManager;
    }

    public boolean notifyKeyEventLocked(KeyEvent keyEvent, int i, List<? extends KeyEventFilter> list) {
        KeyEvent obtain = KeyEvent.obtain(keyEvent);
        PendingKeyEvent pendingKeyEvent = null;
        for (int i2 = 0; i2 < list.size(); i2++) {
            KeyEventFilter keyEventFilter = list.get(i2);
            if (keyEventFilter.onKeyEvent(obtain, obtain.getSequenceNumber())) {
                if (pendingKeyEvent == null) {
                    pendingKeyEvent = obtainPendingEventLocked(obtain, i);
                }
                ArrayList<PendingKeyEvent> arrayList = this.mPendingEventsMap.get(keyEventFilter);
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                    this.mPendingEventsMap.put(keyEventFilter, arrayList);
                }
                arrayList.add(pendingKeyEvent);
                pendingKeyEvent.referenceCount++;
            }
        }
        if (pendingKeyEvent == null) {
            obtain.recycle();
            return false;
        }
        this.mKeyEventTimeoutHandler.sendMessageDelayed(this.mKeyEventTimeoutHandler.obtainMessage(1, pendingKeyEvent), 500L);
        return true;
    }

    public void setOnKeyEventResult(KeyEventFilter keyEventFilter, boolean z, int i) {
        synchronized (this.mLock) {
            PendingKeyEvent removeEventFromListLocked = removeEventFromListLocked(this.mPendingEventsMap.get(keyEventFilter), i);
            if (removeEventFromListLocked != null) {
                if (z && !removeEventFromListLocked.handled) {
                    removeEventFromListLocked.handled = z;
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    this.mPowerManager.userActivity(removeEventFromListLocked.event.getEventTime(), 3, 0);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
                removeReferenceToPendingEventLocked(removeEventFromListLocked);
            }
        }
    }

    public void flush(KeyEventFilter keyEventFilter) {
        synchronized (this.mLock) {
            ArrayList<PendingKeyEvent> arrayList = this.mPendingEventsMap.get(keyEventFilter);
            if (arrayList != null) {
                for (int i = 0; i < arrayList.size(); i++) {
                    removeReferenceToPendingEventLocked(arrayList.get(i));
                }
                this.mPendingEventsMap.remove(keyEventFilter);
            }
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        if (message.what != 1) {
            Slog.w("KeyEventDispatcher", "Unknown message: " + message.what);
            return false;
        }
        PendingKeyEvent pendingKeyEvent = (PendingKeyEvent) message.obj;
        synchronized (this.mLock) {
            Iterator<ArrayList<PendingKeyEvent>> it = this.mPendingEventsMap.values().iterator();
            while (it.hasNext() && (!it.next().remove(pendingKeyEvent) || !removeReferenceToPendingEventLocked(pendingKeyEvent))) {
            }
        }
        return true;
    }

    public final PendingKeyEvent obtainPendingEventLocked(KeyEvent keyEvent, int i) {
        PendingKeyEvent pendingKeyEvent = (PendingKeyEvent) this.mPendingEventPool.acquire();
        if (pendingKeyEvent == null) {
            pendingKeyEvent = new PendingKeyEvent();
        }
        pendingKeyEvent.event = keyEvent;
        pendingKeyEvent.policyFlags = i;
        pendingKeyEvent.referenceCount = 0;
        pendingKeyEvent.handled = false;
        return pendingKeyEvent;
    }

    public static PendingKeyEvent removeEventFromListLocked(List<PendingKeyEvent> list, int i) {
        for (int i2 = 0; i2 < list.size(); i2++) {
            PendingKeyEvent pendingKeyEvent = list.get(i2);
            if (pendingKeyEvent.event.getSequenceNumber() == i) {
                list.remove(pendingKeyEvent);
                return pendingKeyEvent;
            }
        }
        return null;
    }

    public final boolean removeReferenceToPendingEventLocked(PendingKeyEvent pendingKeyEvent) {
        int i = pendingKeyEvent.referenceCount - 1;
        pendingKeyEvent.referenceCount = i;
        if (i > 0) {
            return false;
        }
        this.mKeyEventTimeoutHandler.removeMessages(1, pendingKeyEvent);
        if (!pendingKeyEvent.handled) {
            InputEventConsistencyVerifier inputEventConsistencyVerifier = this.mSentEventsVerifier;
            if (inputEventConsistencyVerifier != null) {
                inputEventConsistencyVerifier.onKeyEvent(pendingKeyEvent.event, 0);
            }
            this.mHandlerToSendKeyEventsToInputFilter.obtainMessage(this.mMessageTypeForSendKeyEvent, pendingKeyEvent.policyFlags | 1073741824, 0, pendingKeyEvent.event).sendToTarget();
        } else {
            pendingKeyEvent.event.recycle();
        }
        this.mPendingEventPool.release(pendingKeyEvent);
        return true;
    }

    /* loaded from: classes.dex */
    public static final class PendingKeyEvent {
        public KeyEvent event;
        public boolean handled;
        public int policyFlags;
        public int referenceCount;

        public PendingKeyEvent() {
        }
    }
}
