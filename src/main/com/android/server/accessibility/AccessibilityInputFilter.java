package com.android.server.accessibility;

import android.content.Context;
import android.graphics.Region;
import android.os.PowerManager;
import android.p005os.IInstalld;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.InputEvent;
import android.view.InputFilter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.LocalServices;
import com.android.server.accessibility.gestures.TouchExplorer;
import com.android.server.accessibility.magnification.FullScreenMagnificationGestureHandler;
import com.android.server.accessibility.magnification.MagnificationGestureHandler;
import com.android.server.accessibility.magnification.WindowMagnificationGestureHandler;
import com.android.server.accessibility.magnification.WindowMagnificationPromptController;
import com.android.server.policy.WindowManagerPolicy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class AccessibilityInputFilter extends InputFilter implements EventStreamTransformation {
    public static final String TAG = AccessibilityInputFilter.class.getSimpleName();
    public final AccessibilityManagerService mAms;
    public AutoclickController mAutoclickController;
    public int mCombinedGenericMotionEventSources;
    public final Context mContext;
    public int mEnabledFeatures;
    public final SparseArray<EventStreamTransformation> mEventHandler;
    public GenericMotionEventStreamState mGenericMotionEventStreamState;
    public boolean mInstalled;
    public KeyboardInterceptor mKeyboardInterceptor;
    public EventStreamState mKeyboardStreamState;
    public final SparseArray<MagnificationGestureHandler> mMagnificationGestureHandler;
    public final SparseArray<MotionEventInjector> mMotionEventInjectors;
    public final SparseArray<EventStreamState> mMouseStreamStates;
    public final PowerManager mPm;
    public SparseArray<Boolean> mServiceDetectsGestures;
    public final SparseArray<TouchExplorer> mTouchExplorer;
    public final SparseArray<EventStreamState> mTouchScreenStreamStates;
    public int mUserId;

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void clearEvents(int i) {
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public EventStreamTransformation getNext() {
        return null;
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void setNext(EventStreamTransformation eventStreamTransformation) {
    }

    public AccessibilityInputFilter(Context context, AccessibilityManagerService accessibilityManagerService) {
        this(context, accessibilityManagerService, new SparseArray(0));
    }

    public AccessibilityInputFilter(Context context, AccessibilityManagerService accessibilityManagerService, SparseArray<EventStreamTransformation> sparseArray) {
        super(context.getMainLooper());
        this.mTouchExplorer = new SparseArray<>(0);
        this.mMagnificationGestureHandler = new SparseArray<>(0);
        this.mMotionEventInjectors = new SparseArray<>(0);
        this.mServiceDetectsGestures = new SparseArray<>();
        this.mMouseStreamStates = new SparseArray<>(0);
        this.mTouchScreenStreamStates = new SparseArray<>(0);
        this.mCombinedGenericMotionEventSources = 0;
        this.mContext = context;
        this.mAms = accessibilityManagerService;
        this.mPm = (PowerManager) context.getSystemService("power");
        this.mEventHandler = sparseArray;
    }

    public void onInstalled() {
        this.mInstalled = true;
        disableFeatures();
        enableFeatures();
        this.mAms.onInputFilterInstalled(true);
        super.onInstalled();
    }

    public void onUninstalled() {
        this.mInstalled = false;
        disableFeatures();
        this.mAms.onInputFilterInstalled(false);
        super.onUninstalled();
    }

    public void onDisplayAdded(Display display) {
        enableFeaturesForDisplayIfInstalled(display);
    }

    public void onDisplayRemoved(int i) {
        disableFeaturesForDisplayIfInstalled(i);
    }

    public void onInputEvent(InputEvent inputEvent, int i) {
        if (this.mAms.getTraceManager().isA11yTracingEnabledForTypes(4096L)) {
            this.mAms.getTraceManager().logTrace(TAG + ".onInputEvent", 4096L, "event=" + inputEvent + ";policyFlags=" + i);
        }
        if (this.mEventHandler.size() == 0) {
            super.onInputEvent(inputEvent, i);
            return;
        }
        EventStreamState eventStreamState = getEventStreamState(inputEvent);
        if (eventStreamState == null) {
            super.onInputEvent(inputEvent, i);
            return;
        }
        int source = inputEvent.getSource();
        int displayId = inputEvent.getDisplayId();
        if ((1073741824 & i) == 0) {
            eventStreamState.reset();
            clearEventStreamHandler(displayId, source);
            super.onInputEvent(inputEvent, i);
            return;
        }
        if (eventStreamState.updateInputSource(inputEvent.getSource())) {
            clearEventStreamHandler(displayId, source);
        }
        if (!eventStreamState.inputSourceValid()) {
            super.onInputEvent(inputEvent, i);
        } else if (inputEvent instanceof MotionEvent) {
            if ((this.mEnabledFeatures & 3035) != 0) {
                processMotionEvent(eventStreamState, (MotionEvent) inputEvent, i);
            } else {
                super.onInputEvent(inputEvent, i);
            }
        } else if (inputEvent instanceof KeyEvent) {
            processKeyEvent(eventStreamState, (KeyEvent) inputEvent, i);
        }
    }

    public final EventStreamState getEventStreamState(InputEvent inputEvent) {
        if (inputEvent instanceof MotionEvent) {
            int displayId = inputEvent.getDisplayId();
            if (this.mGenericMotionEventStreamState == null) {
                this.mGenericMotionEventStreamState = new GenericMotionEventStreamState();
            }
            if (this.mGenericMotionEventStreamState.shouldProcessMotionEvent((MotionEvent) inputEvent)) {
                return this.mGenericMotionEventStreamState;
            }
            if (inputEvent.isFromSource(4098)) {
                EventStreamState eventStreamState = this.mTouchScreenStreamStates.get(displayId);
                if (eventStreamState == null) {
                    TouchScreenEventStreamState touchScreenEventStreamState = new TouchScreenEventStreamState();
                    this.mTouchScreenStreamStates.put(displayId, touchScreenEventStreamState);
                    return touchScreenEventStreamState;
                }
                return eventStreamState;
            } else if (inputEvent.isFromSource(8194)) {
                EventStreamState eventStreamState2 = this.mMouseStreamStates.get(displayId);
                if (eventStreamState2 == null) {
                    MouseEventStreamState mouseEventStreamState = new MouseEventStreamState();
                    this.mMouseStreamStates.put(displayId, mouseEventStreamState);
                    return mouseEventStreamState;
                }
                return eventStreamState2;
            }
        } else if ((inputEvent instanceof KeyEvent) && inputEvent.isFromSource(FrameworkStatsLog.HDMI_CEC_MESSAGE_REPORTED__USER_CONTROL_PRESSED_COMMAND__UP)) {
            if (this.mKeyboardStreamState == null) {
                this.mKeyboardStreamState = new KeyboardEventStreamState();
            }
            return this.mKeyboardStreamState;
        }
        return null;
    }

    public final void clearEventStreamHandler(int i, int i2) {
        EventStreamTransformation eventStreamTransformation = this.mEventHandler.get(i);
        if (eventStreamTransformation != null) {
            eventStreamTransformation.clearEvents(i2);
        }
    }

    public final void processMotionEvent(EventStreamState eventStreamState, MotionEvent motionEvent, int i) {
        if (!eventStreamState.shouldProcessScroll() && motionEvent.getActionMasked() == 8) {
            super.onInputEvent(motionEvent, i);
        } else if (eventStreamState.shouldProcessMotionEvent(motionEvent)) {
            handleMotionEvent(motionEvent, i);
        }
    }

    public final void processKeyEvent(EventStreamState eventStreamState, KeyEvent keyEvent, int i) {
        if (!eventStreamState.shouldProcessKeyEvent(keyEvent)) {
            super.onInputEvent(keyEvent, i);
        } else {
            this.mEventHandler.get(0).onKeyEvent(keyEvent, i);
        }
    }

    public final void handleMotionEvent(MotionEvent motionEvent, int i) {
        this.mPm.userActivity(motionEvent.getEventTime(), false);
        MotionEvent obtain = MotionEvent.obtain(motionEvent);
        int displayId = motionEvent.getDisplayId();
        EventStreamTransformation eventStreamTransformation = this.mEventHandler.get(isDisplayIdValid(displayId) ? displayId : 0);
        if (eventStreamTransformation != null) {
            eventStreamTransformation.onMotionEvent(obtain, motionEvent, i);
        }
        obtain.recycle();
    }

    public final boolean isDisplayIdValid(int i) {
        return this.mEventHandler.get(i) != null;
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void onMotionEvent(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        sendInputEvent(motionEvent, i);
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void onKeyEvent(KeyEvent keyEvent, int i) {
        sendInputEvent(keyEvent, i);
    }

    public void setUserAndEnabledFeatures(int i, int i2) {
        if (this.mEnabledFeatures == i2 && this.mUserId == i) {
            return;
        }
        if (this.mInstalled) {
            disableFeatures();
        }
        this.mUserId = i;
        this.mEnabledFeatures = i2;
        if (this.mInstalled) {
            enableFeatures();
        }
    }

    public void notifyAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        for (int i = 0; i < this.mEventHandler.size(); i++) {
            EventStreamTransformation valueAt = this.mEventHandler.valueAt(i);
            if (valueAt != null) {
                valueAt.onAccessibilityEvent(accessibilityEvent);
            }
        }
    }

    public void notifyAccessibilityButtonClicked(int i) {
        MagnificationGestureHandler magnificationGestureHandler;
        if (this.mMagnificationGestureHandler.size() == 0 || (magnificationGestureHandler = this.mMagnificationGestureHandler.get(i)) == null) {
            return;
        }
        magnificationGestureHandler.notifyShortcutTriggered();
    }

    public final void enableFeatures() {
        resetAllStreamState();
        ArrayList<Display> validDisplayList = this.mAms.getValidDisplayList();
        for (int size = validDisplayList.size() - 1; size >= 0; size--) {
            enableFeaturesForDisplay(validDisplayList.get(size));
        }
        enableDisplayIndependentFeatures();
    }

    public final void enableFeaturesForDisplay(Display display) {
        Context createDisplayContext = this.mContext.createDisplayContext(display);
        int displayId = display.getDisplayId();
        if (this.mAms.isDisplayProxyed(displayId)) {
            return;
        }
        if (!this.mServiceDetectsGestures.contains(displayId)) {
            this.mServiceDetectsGestures.put(displayId, Boolean.FALSE);
        }
        if ((this.mEnabledFeatures & 8) != 0) {
            if (this.mAutoclickController == null) {
                this.mAutoclickController = new AutoclickController(this.mContext, this.mUserId, this.mAms.getTraceManager());
            }
            addFirstEventHandler(displayId, this.mAutoclickController);
        }
        if ((this.mEnabledFeatures & 2) != 0) {
            TouchExplorer touchExplorer = new TouchExplorer(createDisplayContext, this.mAms);
            if ((this.mEnabledFeatures & 128) != 0) {
                touchExplorer.setServiceHandlesDoubleTap(true);
            }
            if ((this.mEnabledFeatures & 256) != 0) {
                touchExplorer.setMultiFingerGesturesEnabled(true);
            }
            if ((this.mEnabledFeatures & 512) != 0) {
                touchExplorer.setTwoFingerPassthroughEnabled(true);
            }
            if ((this.mEnabledFeatures & 1024) != 0) {
                touchExplorer.setSendMotionEventsEnabled(true);
            }
            touchExplorer.setServiceDetectsGestures(this.mServiceDetectsGestures.get(displayId).booleanValue());
            addFirstEventHandler(displayId, touchExplorer);
            this.mTouchExplorer.put(displayId, touchExplorer);
        }
        if ((this.mEnabledFeatures & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0) {
            addFirstEventHandler(displayId, new BaseEventStreamTransformation() { // from class: com.android.server.accessibility.AccessibilityInputFilter.1
                @Override // com.android.server.accessibility.EventStreamTransformation
                public void onMotionEvent(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
                    if (AccessibilityInputFilter.this.anyServiceWantsGenericMotionEvent(motionEvent2) && AccessibilityInputFilter.this.mAms.sendMotionEventToListeningServices(motionEvent2)) {
                        return;
                    }
                    super.onMotionEvent(motionEvent, motionEvent2, i);
                }
            });
        }
        int i = this.mEnabledFeatures;
        if ((i & 32) != 0 || (i & 1) != 0 || (i & 64) != 0) {
            MagnificationGestureHandler createMagnificationGestureHandler = createMagnificationGestureHandler(displayId, createDisplayContext);
            addFirstEventHandler(displayId, createMagnificationGestureHandler);
            this.mMagnificationGestureHandler.put(displayId, createMagnificationGestureHandler);
        }
        if ((this.mEnabledFeatures & 16) != 0) {
            MotionEventInjector motionEventInjector = new MotionEventInjector(this.mContext.getMainLooper(), this.mAms.getTraceManager());
            addFirstEventHandler(displayId, motionEventInjector);
            this.mMotionEventInjectors.put(displayId, motionEventInjector);
        }
    }

    public final void enableDisplayIndependentFeatures() {
        if ((this.mEnabledFeatures & 16) != 0) {
            this.mAms.setMotionEventInjectors(this.mMotionEventInjectors);
        }
        if ((this.mEnabledFeatures & 4) != 0) {
            KeyboardInterceptor keyboardInterceptor = new KeyboardInterceptor(this.mAms, (WindowManagerPolicy) LocalServices.getService(WindowManagerPolicy.class));
            this.mKeyboardInterceptor = keyboardInterceptor;
            addFirstEventHandler(0, keyboardInterceptor);
        }
    }

    public final void addFirstEventHandler(int i, EventStreamTransformation eventStreamTransformation) {
        EventStreamTransformation eventStreamTransformation2 = this.mEventHandler.get(i);
        if (eventStreamTransformation2 != null) {
            eventStreamTransformation.setNext(eventStreamTransformation2);
        } else {
            eventStreamTransformation.setNext(this);
        }
        this.mEventHandler.put(i, eventStreamTransformation);
    }

    public final void disableFeatures() {
        ArrayList<Display> validDisplayList = this.mAms.getValidDisplayList();
        for (int size = validDisplayList.size() - 1; size >= 0; size--) {
            disableFeaturesForDisplay(validDisplayList.get(size).getDisplayId());
        }
        this.mAms.setMotionEventInjectors(null);
        disableDisplayIndependentFeatures();
        resetAllStreamState();
    }

    public final void disableFeaturesForDisplay(int i) {
        MotionEventInjector motionEventInjector = this.mMotionEventInjectors.get(i);
        if (motionEventInjector != null) {
            motionEventInjector.onDestroy();
            this.mMotionEventInjectors.remove(i);
        }
        TouchExplorer touchExplorer = this.mTouchExplorer.get(i);
        if (touchExplorer != null) {
            touchExplorer.onDestroy();
            this.mTouchExplorer.remove(i);
        }
        MagnificationGestureHandler magnificationGestureHandler = this.mMagnificationGestureHandler.get(i);
        if (magnificationGestureHandler != null) {
            magnificationGestureHandler.onDestroy();
            this.mMagnificationGestureHandler.remove(i);
        }
        if (this.mEventHandler.get(i) != null) {
            this.mEventHandler.remove(i);
        }
    }

    public void enableFeaturesForDisplayIfInstalled(Display display) {
        if (this.mInstalled) {
            resetStreamStateForDisplay(display.getDisplayId());
            enableFeaturesForDisplay(display);
        }
    }

    public void disableFeaturesForDisplayIfInstalled(int i) {
        if (this.mInstalled) {
            disableFeaturesForDisplay(i);
            resetStreamStateForDisplay(i);
        }
    }

    public final void disableDisplayIndependentFeatures() {
        AutoclickController autoclickController = this.mAutoclickController;
        if (autoclickController != null) {
            autoclickController.onDestroy();
            this.mAutoclickController = null;
        }
        KeyboardInterceptor keyboardInterceptor = this.mKeyboardInterceptor;
        if (keyboardInterceptor != null) {
            keyboardInterceptor.onDestroy();
            this.mKeyboardInterceptor = null;
        }
    }

    public final MagnificationGestureHandler createMagnificationGestureHandler(int i, Context context) {
        int i2 = this.mEnabledFeatures;
        boolean z = (i2 & 1) != 0;
        boolean z2 = (i2 & 64) != 0;
        if (this.mAms.getMagnificationMode(i) == 2) {
            return new WindowMagnificationGestureHandler(context.createWindowContext(2039, null), this.mAms.getWindowMagnificationMgr(), this.mAms.getTraceManager(), this.mAms.getMagnificationController(), z, z2, i);
        }
        return new FullScreenMagnificationGestureHandler(context.createWindowContext(2027, null), this.mAms.getMagnificationController().getFullScreenMagnificationController(), this.mAms.getTraceManager(), this.mAms.getMagnificationController(), z, z2, new WindowMagnificationPromptController(context, this.mUserId), i);
    }

    public void resetAllStreamState() {
        ArrayList<Display> validDisplayList = this.mAms.getValidDisplayList();
        for (int size = validDisplayList.size() - 1; size >= 0; size--) {
            resetStreamStateForDisplay(validDisplayList.get(size).getDisplayId());
        }
        EventStreamState eventStreamState = this.mKeyboardStreamState;
        if (eventStreamState != null) {
            eventStreamState.reset();
        }
    }

    public void resetStreamStateForDisplay(int i) {
        EventStreamState eventStreamState = this.mTouchScreenStreamStates.get(i);
        if (eventStreamState != null) {
            eventStreamState.reset();
            this.mTouchScreenStreamStates.remove(i);
        }
        EventStreamState eventStreamState2 = this.mMouseStreamStates.get(i);
        if (eventStreamState2 != null) {
            eventStreamState2.reset();
            this.mMouseStreamStates.remove(i);
        }
    }

    public void refreshMagnificationMode(Display display) {
        int displayId = display.getDisplayId();
        MagnificationGestureHandler magnificationGestureHandler = this.mMagnificationGestureHandler.get(displayId);
        if (magnificationGestureHandler == null || magnificationGestureHandler.getMode() == this.mAms.getMagnificationMode(displayId)) {
            return;
        }
        magnificationGestureHandler.onDestroy();
        MagnificationGestureHandler createMagnificationGestureHandler = createMagnificationGestureHandler(displayId, this.mContext.createDisplayContext(display));
        switchEventStreamTransformation(displayId, magnificationGestureHandler, createMagnificationGestureHandler);
        this.mMagnificationGestureHandler.put(displayId, createMagnificationGestureHandler);
    }

    public final void switchEventStreamTransformation(int i, EventStreamTransformation eventStreamTransformation, EventStreamTransformation eventStreamTransformation2) {
        EventStreamTransformation eventStreamTransformation3 = this.mEventHandler.get(i);
        if (eventStreamTransformation3 == null) {
            return;
        }
        if (eventStreamTransformation3 == eventStreamTransformation) {
            eventStreamTransformation2.setNext(eventStreamTransformation.getNext());
            this.mEventHandler.put(i, eventStreamTransformation2);
            return;
        }
        while (eventStreamTransformation3 != null) {
            if (eventStreamTransformation3.getNext() == eventStreamTransformation) {
                eventStreamTransformation3.setNext(eventStreamTransformation2);
                eventStreamTransformation2.setNext(eventStreamTransformation.getNext());
                return;
            }
            eventStreamTransformation3 = eventStreamTransformation3.getNext();
        }
    }

    /* loaded from: classes.dex */
    public static class EventStreamState {
        public int mSource = -1;

        public boolean shouldProcessKeyEvent(KeyEvent keyEvent) {
            return false;
        }

        public boolean shouldProcessMotionEvent(MotionEvent motionEvent) {
            return false;
        }

        public boolean shouldProcessScroll() {
            return false;
        }

        public boolean updateInputSource(int i) {
            if (this.mSource == i) {
                return false;
            }
            reset();
            this.mSource = i;
            return true;
        }

        public boolean inputSourceValid() {
            return this.mSource >= 0;
        }

        public void reset() {
            this.mSource = -1;
        }
    }

    /* loaded from: classes.dex */
    public static class MouseEventStreamState extends EventStreamState {
        public boolean mMotionSequenceStarted;

        @Override // com.android.server.accessibility.AccessibilityInputFilter.EventStreamState
        public final boolean shouldProcessScroll() {
            return true;
        }

        public MouseEventStreamState() {
            reset();
        }

        @Override // com.android.server.accessibility.AccessibilityInputFilter.EventStreamState
        public final void reset() {
            super.reset();
            this.mMotionSequenceStarted = false;
        }

        @Override // com.android.server.accessibility.AccessibilityInputFilter.EventStreamState
        public final boolean shouldProcessMotionEvent(MotionEvent motionEvent) {
            boolean z = true;
            if (this.mMotionSequenceStarted) {
                return true;
            }
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked != 0 && actionMasked != 7) {
                z = false;
            }
            this.mMotionSequenceStarted = z;
            return z;
        }
    }

    /* loaded from: classes.dex */
    public static class TouchScreenEventStreamState extends EventStreamState {
        public boolean mHoverSequenceStarted;
        public boolean mTouchSequenceStarted;

        public TouchScreenEventStreamState() {
            reset();
        }

        @Override // com.android.server.accessibility.AccessibilityInputFilter.EventStreamState
        public final void reset() {
            super.reset();
            this.mTouchSequenceStarted = false;
            this.mHoverSequenceStarted = false;
        }

        @Override // com.android.server.accessibility.AccessibilityInputFilter.EventStreamState
        public final boolean shouldProcessMotionEvent(MotionEvent motionEvent) {
            boolean z;
            if (motionEvent.isTouchEvent()) {
                if (this.mTouchSequenceStarted) {
                    return true;
                }
                z = motionEvent.getActionMasked() == 0;
                this.mTouchSequenceStarted = z;
                return z;
            } else if (this.mHoverSequenceStarted) {
                return true;
            } else {
                z = motionEvent.getActionMasked() == 9;
                this.mHoverSequenceStarted = z;
                return z;
            }
        }
    }

    /* loaded from: classes.dex */
    public class GenericMotionEventStreamState extends EventStreamState {
        @Override // com.android.server.accessibility.AccessibilityInputFilter.EventStreamState
        public boolean shouldProcessScroll() {
            return true;
        }

        public GenericMotionEventStreamState() {
        }

        @Override // com.android.server.accessibility.AccessibilityInputFilter.EventStreamState
        public boolean shouldProcessMotionEvent(MotionEvent motionEvent) {
            return AccessibilityInputFilter.this.anyServiceWantsGenericMotionEvent(motionEvent);
        }
    }

    public final boolean anyServiceWantsGenericMotionEvent(MotionEvent motionEvent) {
        if (!motionEvent.isFromSource(4098) || (this.mEnabledFeatures & 2) == 0) {
            return (this.mCombinedGenericMotionEventSources & (motionEvent.getSource() & (-256))) != 0;
        }
        return false;
    }

    public void setCombinedGenericMotionEventSources(int i) {
        this.mCombinedGenericMotionEventSources = i;
    }

    /* loaded from: classes.dex */
    public static class KeyboardEventStreamState extends EventStreamState {
        public SparseBooleanArray mEventSequenceStartedMap = new SparseBooleanArray();

        @Override // com.android.server.accessibility.AccessibilityInputFilter.EventStreamState
        public boolean inputSourceValid() {
            return true;
        }

        @Override // com.android.server.accessibility.AccessibilityInputFilter.EventStreamState
        public boolean updateInputSource(int i) {
            return false;
        }

        public KeyboardEventStreamState() {
            reset();
        }

        @Override // com.android.server.accessibility.AccessibilityInputFilter.EventStreamState
        public final void reset() {
            super.reset();
            this.mEventSequenceStartedMap.clear();
        }

        @Override // com.android.server.accessibility.AccessibilityInputFilter.EventStreamState
        public final boolean shouldProcessKeyEvent(KeyEvent keyEvent) {
            int deviceId = keyEvent.getDeviceId();
            if (this.mEventSequenceStartedMap.get(deviceId, false)) {
                return true;
            }
            boolean z = keyEvent.getAction() == 0;
            this.mEventSequenceStartedMap.put(deviceId, z);
            return z;
        }
    }

    public void setGestureDetectionPassthroughRegion(int i, Region region) {
        if (region == null || !this.mTouchExplorer.contains(i)) {
            return;
        }
        this.mTouchExplorer.get(i).setGestureDetectionPassthroughRegion(region);
    }

    public void setTouchExplorationPassthroughRegion(int i, Region region) {
        if (region == null || !this.mTouchExplorer.contains(i)) {
            return;
        }
        this.mTouchExplorer.get(i).setTouchExplorationPassthroughRegion(region);
    }

    public void setServiceDetectsGesturesEnabled(int i, boolean z) {
        if (this.mTouchExplorer.contains(i)) {
            this.mTouchExplorer.get(i).setServiceDetectsGestures(z);
        }
        this.mServiceDetectsGestures.put(i, Boolean.valueOf(z));
    }

    public void resetServiceDetectsGestures() {
        this.mServiceDetectsGestures.clear();
    }

    public void requestTouchExploration(int i) {
        if (this.mTouchExplorer.contains(i)) {
            this.mTouchExplorer.get(i).requestTouchExploration();
        }
    }

    public void requestDragging(int i, int i2) {
        if (this.mTouchExplorer.contains(i)) {
            this.mTouchExplorer.get(i).requestDragging(i2);
        }
    }

    public void requestDelegating(int i) {
        if (this.mTouchExplorer.contains(i)) {
            this.mTouchExplorer.get(i).requestDelegating();
        }
    }

    public void onDoubleTap(int i) {
        if (this.mTouchExplorer.contains(i)) {
            this.mTouchExplorer.get(i).onDoubleTap();
        }
    }

    public void onDoubleTapAndHold(int i) {
        if (this.mTouchExplorer.contains(i)) {
            this.mTouchExplorer.get(i).onDoubleTapAndHold();
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (this.mEventHandler == null) {
            return;
        }
        printWriter.append("A11yInputFilter Info : ");
        printWriter.println();
        ArrayList<Display> validDisplayList = this.mAms.getValidDisplayList();
        for (int i = 0; i < validDisplayList.size(); i++) {
            int displayId = validDisplayList.get(i).getDisplayId();
            EventStreamTransformation eventStreamTransformation = this.mEventHandler.get(displayId);
            if (eventStreamTransformation != null) {
                printWriter.append("Enabled features of Display [");
                printWriter.append((CharSequence) Integer.toString(displayId));
                printWriter.append("] = ");
                StringJoiner stringJoiner = new StringJoiner(",", "[", "]");
                while (eventStreamTransformation != null) {
                    if (eventStreamTransformation instanceof MagnificationGestureHandler) {
                        stringJoiner.add("MagnificationGesture");
                    } else if (eventStreamTransformation instanceof KeyboardInterceptor) {
                        stringJoiner.add("KeyboardInterceptor");
                    } else if (eventStreamTransformation instanceof TouchExplorer) {
                        stringJoiner.add("TouchExplorer");
                    } else if (eventStreamTransformation instanceof AutoclickController) {
                        stringJoiner.add(AutoclickController.LOG_TAG);
                    } else if (eventStreamTransformation instanceof MotionEventInjector) {
                        stringJoiner.add("MotionEventInjector");
                    }
                    eventStreamTransformation = eventStreamTransformation.getNext();
                }
                printWriter.append((CharSequence) stringJoiner.toString());
            }
            printWriter.println();
        }
    }
}
