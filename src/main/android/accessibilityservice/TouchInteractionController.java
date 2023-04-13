package android.accessibilityservice;

import android.accessibilityservice.TouchInteractionController;
import android.p008os.RemoteException;
import android.util.ArrayMap;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityInteractionClient;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public final class TouchInteractionController {
    private static final int MAX_POINTER_COUNT = 32;
    public static final int STATE_CLEAR = 0;
    public static final int STATE_DELEGATING = 4;
    public static final int STATE_DRAGGING = 3;
    public static final int STATE_TOUCH_EXPLORING = 2;
    public static final int STATE_TOUCH_INTERACTING = 1;
    private ArrayMap<Callback, Executor> mCallbacks;
    private final int mDisplayId;
    private final Object mLock;
    private final AccessibilityService mService;
    private boolean mServiceDetectsGestures;
    private Queue<MotionEvent> mQueuedMotionEvents = new LinkedList();
    private boolean mStateChangeRequested = false;
    private int mState = 0;

    /* loaded from: classes.dex */
    public interface Callback {
        void onMotionEvent(MotionEvent motionEvent);

        void onStateChanged(int i);
    }

    /* loaded from: classes.dex */
    private @interface State {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TouchInteractionController(AccessibilityService service, Object lock, int displayId) {
        this.mDisplayId = displayId;
        this.mLock = lock;
        this.mService = service;
    }

    public void registerCallback(Executor executor, Callback callback) {
        synchronized (this.mLock) {
            if (this.mCallbacks == null) {
                this.mCallbacks = new ArrayMap<>();
            }
            this.mCallbacks.put(callback, executor);
            if (this.mCallbacks.size() == 1) {
                setServiceDetectsGestures(true);
            }
        }
    }

    public boolean unregisterCallback(Callback callback) {
        boolean result;
        if (this.mCallbacks == null) {
            return false;
        }
        synchronized (this.mLock) {
            result = this.mCallbacks.remove(callback) != null;
            if (result && this.mCallbacks.size() == 0) {
                setServiceDetectsGestures(false);
            }
        }
        return result;
    }

    public void unregisterAllCallbacks() {
        if (this.mCallbacks != null) {
            synchronized (this.mLock) {
                this.mCallbacks.clear();
                setServiceDetectsGestures(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onMotionEvent(MotionEvent event) {
        if (this.mStateChangeRequested) {
            this.mQueuedMotionEvents.add(event);
        } else {
            sendEventToAllListeners(event);
        }
    }

    private void sendEventToAllListeners(final MotionEvent event) {
        ArrayMap<Callback, Executor> entries;
        synchronized (this.mLock) {
            entries = new ArrayMap<>(this.mCallbacks);
        }
        int count = entries.size();
        for (int i = 0; i < count; i++) {
            final Callback callback = entries.keyAt(i);
            Executor executor = entries.valueAt(i);
            if (executor != null) {
                executor.execute(new Runnable() { // from class: android.accessibilityservice.TouchInteractionController$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        TouchInteractionController.Callback.this.onMotionEvent(event);
                    }
                });
            } else {
                callback.onMotionEvent(event);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onStateChanged(final int state) {
        ArrayMap<Callback, Executor> entries;
        this.mState = state;
        synchronized (this.mLock) {
            entries = new ArrayMap<>(this.mCallbacks);
        }
        int count = entries.size();
        for (int i = 0; i < count; i++) {
            final Callback callback = entries.keyAt(i);
            Executor executor = entries.valueAt(i);
            if (executor != null) {
                executor.execute(new Runnable() { // from class: android.accessibilityservice.TouchInteractionController$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        TouchInteractionController.Callback.this.onStateChanged(state);
                    }
                });
            } else {
                callback.onStateChanged(state);
            }
        }
        this.mStateChangeRequested = false;
        while (this.mQueuedMotionEvents.size() > 0) {
            sendEventToAllListeners(this.mQueuedMotionEvents.poll());
        }
    }

    private void setServiceDetectsGestures(boolean mode) {
        AccessibilityInteractionClient.getInstance();
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.getConnectionId());
        if (connection != null) {
            try {
                connection.setServiceDetectsGesturesEnabled(this.mDisplayId, mode);
                this.mServiceDetectsGestures = mode;
            } catch (RemoteException re) {
                throw new RuntimeException(re);
            }
        }
    }

    public void requestTouchExploration() {
        validateTransitionRequest();
        AccessibilityInteractionClient.getInstance();
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.getConnectionId());
        if (connection != null) {
            try {
                connection.requestTouchExploration(this.mDisplayId);
                this.mStateChangeRequested = true;
            } catch (RemoteException re) {
                throw new RuntimeException(re);
            }
        }
    }

    public void requestDragging(int pointerId) {
        validateTransitionRequest();
        if (pointerId < 0 || pointerId > 32) {
            throw new IllegalArgumentException("Invalid pointer id: " + pointerId);
        }
        AccessibilityInteractionClient.getInstance();
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.getConnectionId());
        if (connection != null) {
            try {
                connection.requestDragging(this.mDisplayId, pointerId);
                this.mStateChangeRequested = true;
            } catch (RemoteException re) {
                throw new RuntimeException(re);
            }
        }
    }

    public void requestDelegating() {
        validateTransitionRequest();
        AccessibilityInteractionClient.getInstance();
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.getConnectionId());
        if (connection != null) {
            try {
                connection.requestDelegating(this.mDisplayId);
                this.mStateChangeRequested = true;
            } catch (RemoteException re) {
                throw new RuntimeException(re);
            }
        }
    }

    public void performClick() {
        AccessibilityInteractionClient.getInstance();
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.getConnectionId());
        if (connection != null) {
            try {
                connection.onDoubleTap(this.mDisplayId);
            } catch (RemoteException re) {
                throw new RuntimeException(re);
            }
        }
    }

    public void performLongClickAndStartDrag() {
        AccessibilityInteractionClient.getInstance();
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.getConnectionId());
        if (connection != null) {
            try {
                connection.onDoubleTapAndHold(this.mDisplayId);
            } catch (RemoteException re) {
                throw new RuntimeException(re);
            }
        }
    }

    private void validateTransitionRequest() {
        if (!this.mServiceDetectsGestures || this.mCallbacks.size() == 0) {
            throw new IllegalStateException("State transitions are not allowed without first adding a callback.");
        }
        int i = this.mState;
        if (i == 4 || i == 2) {
            throw new IllegalStateException("State transition requests are not allowed in " + stateToString(this.mState));
        }
    }

    public int getMaxPointerCount() {
        return 32;
    }

    public int getDisplayId() {
        return this.mDisplayId;
    }

    public int getState() {
        int i;
        synchronized (this.mLock) {
            i = this.mState;
        }
        return i;
    }

    public static String stateToString(int state) {
        switch (state) {
            case 0:
                return "STATE_CLEAR";
            case 1:
                return "STATE_TOUCH_INTERACTING";
            case 2:
                return "STATE_TOUCH_EXPLORING";
            case 3:
                return "STATE_DRAGGING";
            case 4:
                return "STATE_DELEGATING";
            default:
                return "Unknown state: " + state;
        }
    }
}
