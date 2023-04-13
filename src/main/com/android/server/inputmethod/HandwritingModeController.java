package com.android.server.inputmethod;

import android.annotation.RequiresPermission;
import android.hardware.input.InputManager;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Slog;
import android.view.BatchedInputEventReceiver;
import android.view.Choreographer;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import com.android.server.LocalServices;
import com.android.server.input.InputManagerInternal;
import com.android.server.p014wm.WindowManagerInternal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public final class HandwritingModeController {
    public static final String TAG = "HandwritingModeController";
    public String mDelegatePackageName;
    public String mDelegatorPackageName;
    public List<MotionEvent> mHandwritingBuffer;
    public InputEventReceiver mHandwritingEventReceiver;
    public HandwritingEventReceiverSurface mHandwritingSurface;
    public Runnable mInkWindowInitRunnable;
    public final Looper mLooper;
    public boolean mRecordingGesture;
    public int mCurrentDisplayId = -1;
    public final InputManagerInternal mInputManagerInternal = (InputManagerInternal) LocalServices.getService(InputManagerInternal.class);
    public final WindowManagerInternal mWindowManagerInternal = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
    public int mCurrentRequestId = 0;

    public HandwritingModeController(Looper looper, Runnable runnable) {
        this.mLooper = looper;
        this.mInkWindowInitRunnable = runnable;
    }

    public static boolean isStylusEvent(MotionEvent motionEvent) {
        if (motionEvent.isFromSource(16386)) {
            int toolType = motionEvent.getToolType(0);
            return toolType == 2 || toolType == 4;
        }
        return false;
    }

    public void initializeHandwritingSpy(int i) {
        reset(i == this.mCurrentDisplayId);
        this.mCurrentDisplayId = i;
        if (this.mHandwritingBuffer == null) {
            this.mHandwritingBuffer = new ArrayList(100);
        }
        String str = TAG;
        Slog.d(str, "Initializing handwriting spy monitor for display: " + i);
        String str2 = "stylus-handwriting-event-receiver-" + i;
        InputChannel createInputChannel = this.mInputManagerInternal.createInputChannel(str2);
        Objects.requireNonNull(createInputChannel, "Failed to create input channel");
        HandwritingEventReceiverSurface handwritingEventReceiverSurface = this.mHandwritingSurface;
        SurfaceControl surface = handwritingEventReceiverSurface != null ? handwritingEventReceiverSurface.getSurface() : this.mWindowManagerInternal.getHandwritingSurfaceForDisplay(i);
        if (surface == null) {
            Slog.e(str, "Failed to create input surface");
            return;
        }
        this.mHandwritingSurface = new HandwritingEventReceiverSurface(str2, i, surface, createInputChannel);
        this.mHandwritingEventReceiver = new BatchedInputEventReceiver.SimpleBatchedInputEventReceiver(createInputChannel.dup(), this.mLooper, Choreographer.getInstance(), new BatchedInputEventReceiver.SimpleBatchedInputEventReceiver.InputEventListener() { // from class: com.android.server.inputmethod.HandwritingModeController$$ExternalSyntheticLambda1
            public final boolean onInputEvent(InputEvent inputEvent) {
                boolean onInputEvent;
                onInputEvent = HandwritingModeController.this.onInputEvent(inputEvent);
                return onInputEvent;
            }
        });
        this.mCurrentRequestId++;
    }

    public OptionalInt getCurrentRequestId() {
        if (this.mHandwritingSurface == null) {
            Slog.e(TAG, "Cannot get requestId: Handwriting was not initialized.");
            return OptionalInt.empty();
        }
        return OptionalInt.of(this.mCurrentRequestId);
    }

    public boolean isStylusGestureOngoing() {
        return this.mRecordingGesture;
    }

    public boolean hasOngoingStylusHandwritingSession() {
        HandwritingEventReceiverSurface handwritingEventReceiverSurface = this.mHandwritingSurface;
        return handwritingEventReceiverSurface != null && handwritingEventReceiverSurface.isIntercepting();
    }

    public void prepareStylusHandwritingDelegation(String str, String str2) {
        this.mDelegatePackageName = str;
        this.mDelegatorPackageName = str2;
        ((ArrayList) this.mHandwritingBuffer).ensureCapacity(2000);
    }

    public String getDelegatePackageName() {
        return this.mDelegatePackageName;
    }

    public String getDelegatorPackageName() {
        return this.mDelegatorPackageName;
    }

    public void clearPendingHandwritingDelegation() {
        Slog.d(TAG, "clearPendingHandwritingDelegation");
        this.mDelegatorPackageName = null;
        this.mDelegatePackageName = null;
    }

    @RequiresPermission("android.permission.MONITOR_INPUT")
    public HandwritingSession startHandwritingSession(int i, int i2, int i3, IBinder iBinder) {
        if (this.mHandwritingSurface == null) {
            Slog.e(TAG, "Cannot start handwriting session: Handwriting was not initialized.");
            return null;
        } else if (i != this.mCurrentRequestId) {
            String str = TAG;
            Slog.e(str, "Cannot start handwriting session: Invalid request id: " + i);
            return null;
        } else if (!this.mRecordingGesture || this.mHandwritingBuffer.isEmpty()) {
            Slog.e(TAG, "Cannot start handwriting session: No stylus gesture is being recorded.");
            return null;
        } else {
            Objects.requireNonNull(this.mHandwritingEventReceiver, "Handwriting session was already transferred to IME.");
            MotionEvent motionEvent = this.mHandwritingBuffer.get(0);
            if (!this.mWindowManagerInternal.isPointInsideWindow(iBinder, this.mCurrentDisplayId, motionEvent.getRawX(), motionEvent.getRawY())) {
                Slog.e(TAG, "Cannot start handwriting session: Stylus gesture did not start inside the focused window.");
                return null;
            }
            String str2 = TAG;
            Slog.d(str2, "Starting handwriting session in display: " + this.mCurrentDisplayId);
            InputManager.getInstance().pilferPointers(this.mHandwritingSurface.getInputChannel().getToken());
            this.mHandwritingEventReceiver.dispose();
            this.mHandwritingEventReceiver = null;
            this.mRecordingGesture = false;
            if (this.mHandwritingSurface.isIntercepting()) {
                throw new IllegalStateException("Handwriting surface should not be already intercepting.");
            }
            this.mHandwritingSurface.startIntercepting(i2, i3);
            return new HandwritingSession(this.mCurrentRequestId, this.mHandwritingSurface.getInputChannel(), this.mHandwritingBuffer);
        }
    }

    public void reset() {
        reset(false);
    }

    public final void reset(boolean z) {
        InputEventReceiver inputEventReceiver = this.mHandwritingEventReceiver;
        if (inputEventReceiver != null) {
            inputEventReceiver.dispose();
            this.mHandwritingEventReceiver = null;
        }
        List<MotionEvent> list = this.mHandwritingBuffer;
        if (list != null) {
            list.forEach(new Consumer() { // from class: com.android.server.inputmethod.HandwritingModeController$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((MotionEvent) obj).recycle();
                }
            });
            this.mHandwritingBuffer.clear();
            if (!z) {
                this.mHandwritingBuffer = null;
            }
        }
        HandwritingEventReceiverSurface handwritingEventReceiverSurface = this.mHandwritingSurface;
        if (handwritingEventReceiverSurface != null) {
            handwritingEventReceiverSurface.getInputChannel().dispose();
            if (!z) {
                this.mHandwritingSurface.remove();
                this.mHandwritingSurface = null;
            }
        }
        clearPendingHandwritingDelegation();
        this.mRecordingGesture = false;
    }

    public final boolean onInputEvent(InputEvent inputEvent) {
        if (this.mHandwritingEventReceiver == null) {
            throw new IllegalStateException("Input Event should not be processed when IME has the spy channel.");
        }
        if (!(inputEvent instanceof MotionEvent)) {
            Slog.wtf(TAG, "Received non-motion event in stylus monitor.");
            return false;
        }
        MotionEvent motionEvent = (MotionEvent) inputEvent;
        if (isStylusEvent(motionEvent)) {
            if (motionEvent.getDisplayId() != this.mCurrentDisplayId) {
                Slog.wtf(TAG, "Received stylus event associated with the incorrect display.");
                return false;
            }
            onStylusEvent(motionEvent);
            return true;
        }
        return false;
    }

    public final void onStylusEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (this.mInkWindowInitRunnable != null && (actionMasked == 9 || motionEvent.getAction() == 9)) {
            this.mInkWindowInitRunnable.run();
            this.mInkWindowInitRunnable = null;
        }
        if (TextUtils.isEmpty(this.mDelegatePackageName) && (actionMasked == 1 || actionMasked == 3)) {
            this.mRecordingGesture = false;
            this.mHandwritingBuffer.clear();
            return;
        }
        if (actionMasked == 0) {
            this.mRecordingGesture = true;
        }
        if (this.mRecordingGesture) {
            if (this.mHandwritingBuffer.size() >= 100) {
                Slog.w(TAG, "Current gesture exceeds the buffer capacity. The rest of the gesture will not be recorded.");
                this.mRecordingGesture = false;
                return;
            }
            this.mHandwritingBuffer.add(MotionEvent.obtain(motionEvent));
        }
    }

    /* loaded from: classes.dex */
    public static final class HandwritingSession {
        public final InputChannel mHandwritingChannel;
        public final List<MotionEvent> mRecordedEvents;
        public final int mRequestId;

        public HandwritingSession(int i, InputChannel inputChannel, List<MotionEvent> list) {
            this.mRequestId = i;
            this.mHandwritingChannel = inputChannel;
            this.mRecordedEvents = list;
        }

        public int getRequestId() {
            return this.mRequestId;
        }

        public InputChannel getHandwritingChannel() {
            return this.mHandwritingChannel;
        }

        public List<MotionEvent> getRecordedEvents() {
            return this.mRecordedEvents;
        }
    }
}
