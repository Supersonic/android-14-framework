package android.view;

import android.media.MediaMetrics;
import android.p008os.Build;
import android.util.Log;
/* loaded from: classes4.dex */
public final class InputEventConsistencyVerifier {
    private static final String EVENT_TYPE_GENERIC_MOTION = "GenericMotionEvent";
    private static final String EVENT_TYPE_KEY = "KeyEvent";
    private static final String EVENT_TYPE_TOUCH = "TouchEvent";
    private static final String EVENT_TYPE_TRACKBALL = "TrackballEvent";
    public static final int FLAG_RAW_DEVICE_INPUT = 1;
    private static final boolean IS_ENG_BUILD = Build.IS_ENG;
    private static final int RECENT_EVENTS_TO_LOG = 5;
    private int mButtonsPressed;
    private final Object mCaller;
    private InputEvent mCurrentEvent;
    private String mCurrentEventType;
    private final int mFlags;
    private boolean mHoverEntered;
    private KeyState mKeyStateList;
    private int mLastEventSeq;
    private String mLastEventType;
    private int mLastNestingLevel;
    private final String mLogTag;
    private int mMostRecentEventIndex;
    private InputEvent[] mRecentEvents;
    private boolean[] mRecentEventsUnhandled;
    private int mTouchEventStreamDeviceId;
    private boolean mTouchEventStreamIsTainted;
    private int mTouchEventStreamPointers;
    private int mTouchEventStreamSource;
    private boolean mTouchEventStreamUnhandled;
    private boolean mTrackballDown;
    private boolean mTrackballUnhandled;
    private StringBuilder mViolationMessage;

    public InputEventConsistencyVerifier(Object caller, int flags) {
        this(caller, flags, null);
    }

    public InputEventConsistencyVerifier(Object caller, int flags, String logTag) {
        this.mTouchEventStreamDeviceId = -1;
        this.mCaller = caller;
        this.mFlags = flags;
        this.mLogTag = logTag != null ? logTag : "InputEventConsistencyVerifier";
    }

    public static boolean isInstrumentationEnabled() {
        return IS_ENG_BUILD;
    }

    public void reset() {
        this.mLastEventSeq = -1;
        this.mLastNestingLevel = 0;
        this.mTrackballDown = false;
        this.mTrackballUnhandled = false;
        this.mTouchEventStreamPointers = 0;
        this.mTouchEventStreamIsTainted = false;
        this.mTouchEventStreamUnhandled = false;
        this.mHoverEntered = false;
        this.mButtonsPressed = 0;
        while (this.mKeyStateList != null) {
            KeyState state = this.mKeyStateList;
            this.mKeyStateList = state.next;
            state.recycle();
        }
    }

    public void onInputEvent(InputEvent event, int nestingLevel) {
        if (event instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) event;
            onKeyEvent(keyEvent, nestingLevel);
            return;
        }
        MotionEvent motionEvent = (MotionEvent) event;
        if (motionEvent.isTouchEvent()) {
            onTouchEvent(motionEvent, nestingLevel);
        } else if ((motionEvent.getSource() & 4) != 0) {
            onTrackballEvent(motionEvent, nestingLevel);
        } else {
            onGenericMotionEvent(motionEvent, nestingLevel);
        }
    }

    public void onKeyEvent(KeyEvent event, int nestingLevel) {
        if (!startEvent(event, nestingLevel, EVENT_TYPE_KEY)) {
            return;
        }
        try {
            ensureMetaStateIsNormalized(event.getMetaState());
            int action = event.getAction();
            int deviceId = event.getDeviceId();
            int source = event.getSource();
            int keyCode = event.getKeyCode();
            switch (action) {
                case 0:
                    KeyState state = findKeyState(deviceId, source, keyCode, false);
                    if (state != null) {
                        if (!state.unhandled) {
                            if ((1 & this.mFlags) == 0 && event.getRepeatCount() == 0) {
                                problem("ACTION_DOWN but key is already down and this event is not a key repeat.");
                                break;
                            }
                        } else {
                            state.unhandled = false;
                            break;
                        }
                    } else {
                        addKeyState(deviceId, source, keyCode);
                        break;
                    }
                    break;
                case 1:
                    KeyState state2 = findKeyState(deviceId, source, keyCode, true);
                    if (state2 == null) {
                        problem("ACTION_UP but key was not down.");
                        break;
                    } else {
                        state2.recycle();
                        break;
                    }
                case 2:
                    break;
                default:
                    problem("Invalid action " + KeyEvent.actionToString(action) + " for key event.");
                    break;
            }
        } finally {
            finishEvent();
        }
    }

    public void onTrackballEvent(MotionEvent event, int nestingLevel) {
        if (!startEvent(event, nestingLevel, EVENT_TYPE_TRACKBALL)) {
            return;
        }
        try {
            ensureMetaStateIsNormalized(event.getMetaState());
            int action = event.getAction();
            int source = event.getSource();
            if ((source & 4) != 0) {
                switch (action) {
                    case 0:
                        if (this.mTrackballDown && !this.mTrackballUnhandled) {
                            problem("ACTION_DOWN but trackball is already down.");
                        } else {
                            this.mTrackballDown = true;
                            this.mTrackballUnhandled = false;
                        }
                        ensureHistorySizeIsZeroForThisAction(event);
                        ensurePointerCountIsOneForThisAction(event);
                        break;
                    case 1:
                        if (!this.mTrackballDown) {
                            problem("ACTION_UP but trackball is not down.");
                        } else {
                            this.mTrackballDown = false;
                            this.mTrackballUnhandled = false;
                        }
                        ensureHistorySizeIsZeroForThisAction(event);
                        ensurePointerCountIsOneForThisAction(event);
                        break;
                    case 2:
                        ensurePointerCountIsOneForThisAction(event);
                        break;
                    default:
                        problem("Invalid action " + MotionEvent.actionToString(action) + " for trackball event.");
                        break;
                }
                if (this.mTrackballDown && event.getPressure() <= 0.0f) {
                    problem("Trackball is down but pressure is not greater than 0.");
                } else if (!this.mTrackballDown && event.getPressure() != 0.0f) {
                    problem("Trackball is up but pressure is not equal to 0.");
                }
            } else {
                problem("Source was not SOURCE_CLASS_TRACKBALL.");
            }
        } finally {
            finishEvent();
        }
    }

    public void onTouchEvent(MotionEvent event, int nestingLevel) {
        int i;
        if (!startEvent(event, nestingLevel, EVENT_TYPE_TOUCH)) {
            return;
        }
        int action = event.getAction();
        boolean newStream = action == 0 || action == 3 || action == 4;
        if (newStream && (this.mTouchEventStreamIsTainted || this.mTouchEventStreamUnhandled)) {
            this.mTouchEventStreamIsTainted = false;
            this.mTouchEventStreamUnhandled = false;
            this.mTouchEventStreamPointers = 0;
        }
        if (this.mTouchEventStreamIsTainted) {
            event.setTainted(true);
        }
        try {
            ensureMetaStateIsNormalized(event.getMetaState());
            int deviceId = event.getDeviceId();
            int source = event.getSource();
            if (!newStream && (i = this.mTouchEventStreamDeviceId) != -1 && (i != deviceId || this.mTouchEventStreamSource != source)) {
                problem("Touch event stream contains events from multiple sources: previous device id " + this.mTouchEventStreamDeviceId + ", previous source " + Integer.toHexString(this.mTouchEventStreamSource) + ", new device id " + deviceId + ", new source " + Integer.toHexString(source));
            }
            this.mTouchEventStreamDeviceId = deviceId;
            this.mTouchEventStreamSource = source;
            int pointerCount = event.getPointerCount();
            if ((source & 2) != 0) {
                switch (action) {
                    case 0:
                        if (this.mTouchEventStreamPointers != 0) {
                            problem("ACTION_DOWN but pointers are already down.  Probably missing ACTION_UP from previous gesture.");
                        }
                        ensureHistorySizeIsZeroForThisAction(event);
                        ensurePointerCountIsOneForThisAction(event);
                        this.mTouchEventStreamPointers = 1 << event.getPointerId(0);
                        break;
                    case 1:
                        ensureHistorySizeIsZeroForThisAction(event);
                        ensurePointerCountIsOneForThisAction(event);
                        this.mTouchEventStreamPointers = 0;
                        this.mTouchEventStreamIsTainted = false;
                        break;
                    case 2:
                        int expectedPointerCount = Integer.bitCount(this.mTouchEventStreamPointers);
                        if (pointerCount != expectedPointerCount) {
                            problem("ACTION_MOVE contained " + pointerCount + " pointers but there are currently " + expectedPointerCount + " pointers down.");
                            this.mTouchEventStreamIsTainted = true;
                            break;
                        }
                        break;
                    case 3:
                        this.mTouchEventStreamPointers = 0;
                        this.mTouchEventStreamIsTainted = false;
                        break;
                    case 4:
                        if (this.mTouchEventStreamPointers != 0) {
                            problem("ACTION_OUTSIDE but pointers are still down.");
                        }
                        ensureHistorySizeIsZeroForThisAction(event);
                        ensurePointerCountIsOneForThisAction(event);
                        this.mTouchEventStreamIsTainted = false;
                        break;
                    default:
                        int actionMasked = event.getActionMasked();
                        int actionIndex = event.getActionIndex();
                        if (actionMasked == 5) {
                            if (this.mTouchEventStreamPointers == 0) {
                                problem("ACTION_POINTER_DOWN but no other pointers were down.");
                                this.mTouchEventStreamIsTainted = true;
                            }
                            if (actionIndex >= 0 && actionIndex < pointerCount) {
                                int id = event.getPointerId(actionIndex);
                                int idBit = 1 << id;
                                int i2 = this.mTouchEventStreamPointers;
                                if ((i2 & idBit) != 0) {
                                    problem("ACTION_POINTER_DOWN specified pointer id " + id + " which is already down.");
                                    this.mTouchEventStreamIsTainted = true;
                                } else {
                                    this.mTouchEventStreamPointers = i2 | idBit;
                                }
                                ensureHistorySizeIsZeroForThisAction(event);
                                break;
                            }
                            problem("ACTION_POINTER_DOWN index is " + actionIndex + " but the pointer count is " + pointerCount + MediaMetrics.SEPARATOR);
                            this.mTouchEventStreamIsTainted = true;
                            ensureHistorySizeIsZeroForThisAction(event);
                        } else if (actionMasked == 6) {
                            if (actionIndex >= 0 && actionIndex < pointerCount) {
                                int id2 = event.getPointerId(actionIndex);
                                int idBit2 = 1 << id2;
                                int i3 = this.mTouchEventStreamPointers;
                                if ((i3 & idBit2) == 0) {
                                    problem("ACTION_POINTER_UP specified pointer id " + id2 + " which is not currently down.");
                                    this.mTouchEventStreamIsTainted = true;
                                } else {
                                    this.mTouchEventStreamPointers = (~idBit2) & i3;
                                }
                                ensureHistorySizeIsZeroForThisAction(event);
                                break;
                            }
                            problem("ACTION_POINTER_UP index is " + actionIndex + " but the pointer count is " + pointerCount + MediaMetrics.SEPARATOR);
                            this.mTouchEventStreamIsTainted = true;
                            ensureHistorySizeIsZeroForThisAction(event);
                        } else {
                            problem("Invalid action " + MotionEvent.actionToString(action) + " for touch event.");
                            break;
                        }
                }
            } else {
                problem("Source was not SOURCE_CLASS_POINTER.");
            }
        } finally {
            finishEvent();
        }
    }

    public void onGenericMotionEvent(MotionEvent event, int nestingLevel) {
        if (!startEvent(event, nestingLevel, EVENT_TYPE_GENERIC_MOTION)) {
            return;
        }
        try {
            ensureMetaStateIsNormalized(event.getMetaState());
            int action = event.getAction();
            int source = event.getSource();
            int buttonState = event.getButtonState();
            int actionButton = event.getActionButton();
            if ((source & 2) != 0) {
                switch (action) {
                    case 7:
                        ensurePointerCountIsOneForThisAction(event);
                        break;
                    case 8:
                        ensureHistorySizeIsZeroForThisAction(event);
                        ensurePointerCountIsOneForThisAction(event);
                        break;
                    case 9:
                        ensurePointerCountIsOneForThisAction(event);
                        this.mHoverEntered = true;
                        break;
                    case 10:
                        ensurePointerCountIsOneForThisAction(event);
                        if (!this.mHoverEntered) {
                            problem("ACTION_HOVER_EXIT without prior ACTION_HOVER_ENTER");
                        }
                        this.mHoverEntered = false;
                        break;
                    case 11:
                        ensureActionButtonIsNonZeroForThisAction(event);
                        if ((this.mButtonsPressed & actionButton) != 0) {
                            problem("Action button for ACTION_BUTTON_PRESS event is " + actionButton + ", but it has already been pressed and has yet to be released.");
                        }
                        int i = this.mButtonsPressed | actionButton;
                        this.mButtonsPressed = i;
                        if (actionButton == 32 && (buttonState & 2) != 0) {
                            this.mButtonsPressed = i | 2;
                        } else if (actionButton == 64 && (buttonState & 4) != 0) {
                            this.mButtonsPressed = i | 4;
                        }
                        if (this.mButtonsPressed != buttonState) {
                            problem(String.format("Reported button state differs from expected button state based on press and release events. Is 0x%08x but expected 0x%08x.", Integer.valueOf(buttonState), Integer.valueOf(this.mButtonsPressed)));
                            break;
                        }
                        break;
                    case 12:
                        ensureActionButtonIsNonZeroForThisAction(event);
                        if ((this.mButtonsPressed & actionButton) != actionButton) {
                            problem("Action button for ACTION_BUTTON_RELEASE event is " + actionButton + ", but it was either never pressed or has already been released.");
                        }
                        int i2 = this.mButtonsPressed & (~actionButton);
                        this.mButtonsPressed = i2;
                        if (actionButton == 32 && (buttonState & 2) == 0) {
                            this.mButtonsPressed = i2 & (-3);
                        } else if (actionButton == 64 && (buttonState & 4) == 0) {
                            this.mButtonsPressed = i2 & (-5);
                        }
                        if (this.mButtonsPressed != buttonState) {
                            problem(String.format("Reported button state differs from expected button state based on press and release events. Is 0x%08x but expected 0x%08x.", Integer.valueOf(buttonState), Integer.valueOf(this.mButtonsPressed)));
                            break;
                        }
                        break;
                    default:
                        problem("Invalid action for generic pointer event.");
                        break;
                }
            } else if ((source & 16) != 0) {
                switch (action) {
                    case 2:
                        ensurePointerCountIsOneForThisAction(event);
                        break;
                    default:
                        problem("Invalid action for generic joystick event.");
                        break;
                }
            }
        } finally {
            finishEvent();
        }
    }

    public void onUnhandledEvent(InputEvent event, int nestingLevel) {
        if (nestingLevel != this.mLastNestingLevel) {
            return;
        }
        boolean[] zArr = this.mRecentEventsUnhandled;
        if (zArr != null) {
            zArr[this.mMostRecentEventIndex] = true;
        }
        if (event instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) event;
            int deviceId = keyEvent.getDeviceId();
            int source = keyEvent.getSource();
            int keyCode = keyEvent.getKeyCode();
            KeyState state = findKeyState(deviceId, source, keyCode, false);
            if (state != null) {
                state.unhandled = true;
                return;
            }
            return;
        }
        MotionEvent motionEvent = (MotionEvent) event;
        if (motionEvent.isTouchEvent()) {
            this.mTouchEventStreamUnhandled = true;
        } else if ((motionEvent.getSource() & 4) != 0 && this.mTrackballDown) {
            this.mTrackballUnhandled = true;
        }
    }

    private void ensureMetaStateIsNormalized(int metaState) {
        int normalizedMetaState = KeyEvent.normalizeMetaState(metaState);
        if (normalizedMetaState != metaState) {
            problem(String.format("Metastate not normalized.  Was 0x%08x but expected 0x%08x.", Integer.valueOf(metaState), Integer.valueOf(normalizedMetaState)));
        }
    }

    private void ensurePointerCountIsOneForThisAction(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount != 1) {
            problem("Pointer count is " + pointerCount + " but it should always be 1 for " + MotionEvent.actionToString(event.getAction()));
        }
    }

    private void ensureActionButtonIsNonZeroForThisAction(MotionEvent event) {
        int actionButton = event.getActionButton();
        if (actionButton == 0) {
            problem("No action button set. Action button should always be non-zero for " + MotionEvent.actionToString(event.getAction()));
        }
    }

    private void ensureHistorySizeIsZeroForThisAction(MotionEvent event) {
        int historySize = event.getHistorySize();
        if (historySize != 0) {
            problem("History size is " + historySize + " but it should always be 0 for " + MotionEvent.actionToString(event.getAction()));
        }
    }

    private boolean startEvent(InputEvent event, int nestingLevel, String eventType) {
        int seq = event.getSequenceNumber();
        if (seq == this.mLastEventSeq && nestingLevel < this.mLastNestingLevel && eventType == this.mLastEventType) {
            return false;
        }
        if (nestingLevel > 0) {
            this.mLastEventSeq = seq;
            this.mLastEventType = eventType;
            this.mLastNestingLevel = nestingLevel;
        } else {
            this.mLastEventSeq = -1;
            this.mLastEventType = null;
            this.mLastNestingLevel = 0;
        }
        this.mCurrentEvent = event;
        this.mCurrentEventType = eventType;
        return true;
    }

    private void finishEvent() {
        StringBuilder sb = this.mViolationMessage;
        if (sb != null && sb.length() != 0) {
            if (!this.mCurrentEvent.isTainted()) {
                this.mViolationMessage.append("\n  in ").append(this.mCaller);
                this.mViolationMessage.append("\n  ");
                appendEvent(this.mViolationMessage, 0, this.mCurrentEvent, false);
                if (this.mRecentEvents != null) {
                    this.mViolationMessage.append("\n  -- recent events --");
                    for (int i = 0; i < 5; i++) {
                        int index = ((this.mMostRecentEventIndex + 5) - i) % 5;
                        InputEvent event = this.mRecentEvents[index];
                        if (event == null) {
                            break;
                        }
                        this.mViolationMessage.append("\n  ");
                        appendEvent(this.mViolationMessage, i + 1, event, this.mRecentEventsUnhandled[index]);
                    }
                }
                Log.m112d(this.mLogTag, this.mViolationMessage.toString());
                this.mCurrentEvent.setTainted(true);
            }
            this.mViolationMessage.setLength(0);
        }
        if (this.mRecentEvents == null) {
            this.mRecentEvents = new InputEvent[5];
            this.mRecentEventsUnhandled = new boolean[5];
        }
        int index2 = (this.mMostRecentEventIndex + 1) % 5;
        this.mMostRecentEventIndex = index2;
        InputEvent inputEvent = this.mRecentEvents[index2];
        if (inputEvent != null) {
            inputEvent.recycle();
        }
        this.mRecentEvents[index2] = this.mCurrentEvent.copy();
        this.mRecentEventsUnhandled[index2] = false;
        this.mCurrentEvent = null;
        this.mCurrentEventType = null;
    }

    private static void appendEvent(StringBuilder message, int index, InputEvent event, boolean unhandled) {
        message.append(index).append(": sent at ").append(event.getEventTimeNano());
        message.append(", ");
        if (unhandled) {
            message.append("(unhandled) ");
        }
        message.append(event);
    }

    private void problem(String message) {
        if (this.mViolationMessage == null) {
            this.mViolationMessage = new StringBuilder();
        }
        if (this.mViolationMessage.length() == 0) {
            this.mViolationMessage.append(this.mCurrentEventType).append(": ");
        } else {
            this.mViolationMessage.append("\n  ");
        }
        this.mViolationMessage.append(message);
    }

    private KeyState findKeyState(int deviceId, int source, int keyCode, boolean remove) {
        KeyState last = null;
        for (KeyState state = this.mKeyStateList; state != null; state = state.next) {
            if (state.deviceId == deviceId && state.source == source && state.keyCode == keyCode) {
                if (remove) {
                    if (last != null) {
                        last.next = state.next;
                    } else {
                        this.mKeyStateList = state.next;
                    }
                    state.next = null;
                }
                return state;
            }
            last = state;
        }
        return null;
    }

    private void addKeyState(int deviceId, int source, int keyCode) {
        KeyState state = KeyState.obtain(deviceId, source, keyCode);
        state.next = this.mKeyStateList;
        this.mKeyStateList = state;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class KeyState {
        private static KeyState mRecycledList;
        private static Object mRecycledListLock = new Object();
        public int deviceId;
        public int keyCode;
        public KeyState next;
        public int source;
        public boolean unhandled;

        private KeyState() {
        }

        public static KeyState obtain(int deviceId, int source, int keyCode) {
            KeyState state;
            synchronized (mRecycledListLock) {
                state = mRecycledList;
                if (state != null) {
                    mRecycledList = state.next;
                } else {
                    state = new KeyState();
                }
            }
            state.deviceId = deviceId;
            state.source = source;
            state.keyCode = keyCode;
            state.unhandled = false;
            return state;
        }

        public void recycle() {
            synchronized (mRecycledListLock) {
                KeyState keyState = mRecycledList;
                this.next = keyState;
                mRecycledList = keyState;
            }
        }
    }
}
