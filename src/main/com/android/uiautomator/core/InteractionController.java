package com.android.uiautomator.core;

import android.app.UiAutomation;
import android.graphics.Point;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
/* loaded from: classes.dex */
class InteractionController {
    private static final boolean DEBUG;
    private static final String LOG_TAG;
    private static final int MOTION_EVENT_INJECTION_DELAY_MILLIS = 5;
    private static final long REGULAR_CLICK_LENGTH = 100;
    private long mDownTime;
    private final KeyCharacterMap mKeyCharacterMap = KeyCharacterMap.load(-1);
    private final UiAutomatorBridge mUiAutomatorBridge;

    static {
        String simpleName = InteractionController.class.getSimpleName();
        LOG_TAG = simpleName;
        DEBUG = Log.isLoggable(simpleName, 3);
    }

    public InteractionController(UiAutomatorBridge bridge) {
        this.mUiAutomatorBridge = bridge;
    }

    /* loaded from: classes.dex */
    class WaitForAnyEventPredicate implements UiAutomation.AccessibilityEventFilter {
        int mMask;

        WaitForAnyEventPredicate(int mask) {
            this.mMask = mask;
        }

        @Override // android.app.UiAutomation.AccessibilityEventFilter
        public boolean accept(AccessibilityEvent t) {
            if ((t.getEventType() & this.mMask) != 0) {
                return true;
            }
            return InteractionController.DEBUG;
        }
    }

    /* loaded from: classes.dex */
    class EventCollectingPredicate implements UiAutomation.AccessibilityEventFilter {
        List<AccessibilityEvent> mEventsList;
        int mMask;

        EventCollectingPredicate(int mask, List<AccessibilityEvent> events) {
            this.mMask = mask;
            this.mEventsList = events;
        }

        @Override // android.app.UiAutomation.AccessibilityEventFilter
        public boolean accept(AccessibilityEvent t) {
            if ((t.getEventType() & this.mMask) != 0) {
                this.mEventsList.add(AccessibilityEvent.obtain(t));
                return InteractionController.DEBUG;
            }
            return InteractionController.DEBUG;
        }
    }

    /* loaded from: classes.dex */
    class WaitForAllEventPredicate implements UiAutomation.AccessibilityEventFilter {
        int mMask;

        WaitForAllEventPredicate(int mask) {
            this.mMask = mask;
        }

        @Override // android.app.UiAutomation.AccessibilityEventFilter
        public boolean accept(AccessibilityEvent t) {
            int eventType = t.getEventType();
            int i = this.mMask;
            if ((eventType & i) != 0) {
                int i2 = (~t.getEventType()) & i;
                this.mMask = i2;
                if (i2 != 0) {
                    return InteractionController.DEBUG;
                }
                return true;
            }
            return InteractionController.DEBUG;
        }
    }

    private AccessibilityEvent runAndWaitForEvents(Runnable command, UiAutomation.AccessibilityEventFilter filter, long timeout) {
        try {
            return this.mUiAutomatorBridge.executeCommandAndWaitForAccessibilityEvent(command, filter, timeout);
        } catch (TimeoutException e) {
            Log.w(LOG_TAG, "runAndwaitForEvent timedout waiting for events");
            return null;
        } catch (Exception e2) {
            Log.e(LOG_TAG, "exception from executeCommandAndWaitForAccessibilityEvent", e2);
            return null;
        }
    }

    public boolean sendKeyAndWaitForEvent(final int keyCode, final int metaState, int eventType, long timeout) {
        Runnable command = new Runnable() { // from class: com.android.uiautomator.core.InteractionController.1
            @Override // java.lang.Runnable
            public void run() {
                long eventTime = SystemClock.uptimeMillis();
                KeyEvent downEvent = new KeyEvent(eventTime, eventTime, 0, keyCode, 0, metaState, -1, 0, 0, 257);
                if (InteractionController.this.injectEventSync(downEvent)) {
                    KeyEvent upEvent = new KeyEvent(eventTime, eventTime, 1, keyCode, 0, metaState, -1, 0, 0, 257);
                    InteractionController.this.injectEventSync(upEvent);
                }
            }
        };
        if (runAndWaitForEvents(command, new WaitForAnyEventPredicate(eventType), timeout) != null) {
            return true;
        }
        return DEBUG;
    }

    public boolean clickNoSync(int x, int y) {
        Log.d(LOG_TAG, "clickNoSync (" + x + ", " + y + ")");
        if (touchDown(x, y)) {
            SystemClock.sleep(REGULAR_CLICK_LENGTH);
            if (touchUp(x, y)) {
                return true;
            }
            return DEBUG;
        }
        return DEBUG;
    }

    public boolean clickAndSync(int x, int y, long timeout) {
        String logString = String.format("clickAndSync(%d, %d)", Integer.valueOf(x), Integer.valueOf(y));
        Log.d(LOG_TAG, logString);
        if (runAndWaitForEvents(clickRunnable(x, y), new WaitForAnyEventPredicate(2052), timeout) != null) {
            return true;
        }
        return DEBUG;
    }

    public boolean clickAndWaitForNewWindow(int x, int y, long timeout) {
        String logString = String.format("clickAndWaitForNewWindow(%d, %d)", Integer.valueOf(x), Integer.valueOf(y));
        Log.d(LOG_TAG, logString);
        if (runAndWaitForEvents(clickRunnable(x, y), new WaitForAllEventPredicate(2080), timeout) != null) {
            return true;
        }
        return DEBUG;
    }

    private Runnable clickRunnable(final int x, final int y) {
        return new Runnable() { // from class: com.android.uiautomator.core.InteractionController.2
            @Override // java.lang.Runnable
            public void run() {
                if (InteractionController.this.touchDown(x, y)) {
                    SystemClock.sleep(InteractionController.REGULAR_CLICK_LENGTH);
                    InteractionController.this.touchUp(x, y);
                }
            }
        };
    }

    public boolean longTapNoSync(int x, int y) {
        if (DEBUG) {
            Log.d(LOG_TAG, "longTapNoSync (" + x + ", " + y + ")");
        }
        if (touchDown(x, y)) {
            SystemClock.sleep(this.mUiAutomatorBridge.getSystemLongPressTime());
            if (touchUp(x, y)) {
                return true;
            }
            return DEBUG;
        }
        return DEBUG;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean touchDown(int x, int y) {
        if (DEBUG) {
            Log.d(LOG_TAG, "touchDown (" + x + ", " + y + ")");
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        this.mDownTime = uptimeMillis;
        MotionEvent event = MotionEvent.obtain(uptimeMillis, uptimeMillis, 0, x, y, 1);
        event.setSource(4098);
        return injectEventSync(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean touchUp(int x, int y) {
        if (DEBUG) {
            Log.d(LOG_TAG, "touchUp (" + x + ", " + y + ")");
        }
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(this.mDownTime, eventTime, 1, x, y, 1);
        event.setSource(4098);
        this.mDownTime = 0L;
        return injectEventSync(event);
    }

    private boolean touchMove(int x, int y) {
        if (DEBUG) {
            Log.d(LOG_TAG, "touchMove (" + x + ", " + y + ")");
        }
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(this.mDownTime, eventTime, 2, x, y, 1);
        event.setSource(4098);
        return injectEventSync(event);
    }

    public boolean scrollSwipe(final int downX, final int downY, final int upX, final int upY, final int steps) {
        String str = LOG_TAG;
        Log.d(str, "scrollSwipe (" + downX + ", " + downY + ", " + upX + ", " + upY + ", " + steps + ")");
        Runnable command = new Runnable() { // from class: com.android.uiautomator.core.InteractionController.3
            @Override // java.lang.Runnable
            public void run() {
                InteractionController.this.swipe(downX, downY, upX, upY, steps);
            }
        };
        ArrayList<AccessibilityEvent> events = new ArrayList<>();
        runAndWaitForEvents(command, new EventCollectingPredicate(4096, events), Configurator.getInstance().getScrollAcknowledgmentTimeout());
        AccessibilityEvent event = getLastMatchingEvent(events, 4096);
        if (event == null) {
            recycleAccessibilityEvents(events);
            return DEBUG;
        }
        boolean foundEnd = DEBUG;
        if (event.getFromIndex() != -1 && event.getToIndex() != -1 && event.getItemCount() != -1) {
            foundEnd = event.getFromIndex() == 0 || event.getItemCount() - 1 == event.getToIndex();
            Log.d(str, "scrollSwipe reached scroll end: " + foundEnd);
        } else if (event.getScrollX() != -1 && event.getScrollY() != -1) {
            if (downX == upX) {
                foundEnd = event.getScrollY() == 0 || event.getScrollY() == event.getMaxScrollY();
                Log.d(str, "Vertical scrollSwipe reached scroll end: " + foundEnd);
            } else if (downY == upY) {
                foundEnd = event.getScrollX() == 0 || event.getScrollX() == event.getMaxScrollX();
                Log.d(str, "Horizontal scrollSwipe reached scroll end: " + foundEnd);
            }
        }
        recycleAccessibilityEvents(events);
        if (foundEnd) {
            return DEBUG;
        }
        return true;
    }

    private AccessibilityEvent getLastMatchingEvent(List<AccessibilityEvent> events, int type) {
        for (int x = events.size(); x > 0; x--) {
            AccessibilityEvent event = events.get(x - 1);
            if (event.getEventType() == type) {
                return event;
            }
        }
        return null;
    }

    private void recycleAccessibilityEvents(List<AccessibilityEvent> events) {
        for (AccessibilityEvent event : events) {
            event.recycle();
        }
        events.clear();
    }

    public boolean swipe(int downX, int downY, int upX, int upY, int steps) {
        return swipe(downX, downY, upX, upY, steps, DEBUG);
    }

    public boolean swipe(int downX, int downY, int upX, int upY, int steps, boolean drag) {
        int swipeSteps = steps;
        if (swipeSteps == 0) {
            swipeSteps = 1;
        }
        double xStep = (upX - downX) / swipeSteps;
        double yStep = (upY - downY) / swipeSteps;
        boolean ret = touchDown(downX, downY);
        if (drag) {
            SystemClock.sleep(this.mUiAutomatorBridge.getSystemLongPressTime());
        }
        for (int i = 1; i < swipeSteps; i++) {
            ret &= touchMove(((int) (i * xStep)) + downX, ((int) (i * yStep)) + downY);
            if (!ret) {
                break;
            }
            SystemClock.sleep(5L);
        }
        if (drag) {
            SystemClock.sleep(REGULAR_CLICK_LENGTH);
        }
        return ret & touchUp(upX, upY);
    }

    public boolean swipe(Point[] segments, int segmentSteps) {
        if (segmentSteps == 0) {
            segmentSteps = 1;
        }
        if (segments.length == 0) {
            return DEBUG;
        }
        boolean ret = touchDown(segments[0].x, segments[0].y);
        for (int seg = 0; seg < segments.length; seg++) {
            if (seg + 1 < segments.length) {
                double xStep = (segments[seg + 1].x - segments[seg].x) / segmentSteps;
                double yStep = (segments[seg + 1].y - segments[seg].y) / segmentSteps;
                for (int i = 1; i < segmentSteps; i++) {
                    ret &= touchMove(segments[seg].x + ((int) (i * xStep)), segments[seg].y + ((int) (i * yStep)));
                    if (!ret) {
                        break;
                    }
                    SystemClock.sleep(5L);
                }
            }
        }
        int seg2 = segments.length;
        return ret & touchUp(segments[seg2 - 1].x, segments[segments.length - 1].y);
    }

    public boolean sendText(String text) {
        if (DEBUG) {
            Log.d(LOG_TAG, "sendText (" + text + ")");
        }
        KeyEvent[] events = this.mKeyCharacterMap.getEvents(text.toCharArray());
        if (events != null) {
            long keyDelay = Configurator.getInstance().getKeyInjectionDelay();
            for (KeyEvent event2 : events) {
                KeyEvent event = KeyEvent.changeTimeRepeat(event2, SystemClock.uptimeMillis(), 0);
                if (!injectEventSync(event)) {
                    return DEBUG;
                }
                SystemClock.sleep(keyDelay);
            }
            return true;
        }
        return true;
    }

    public boolean sendKey(int keyCode, int metaState) {
        if (DEBUG) {
            Log.d(LOG_TAG, "sendKey (" + keyCode + ", " + metaState + ")");
        }
        long eventTime = SystemClock.uptimeMillis();
        KeyEvent downEvent = new KeyEvent(eventTime, eventTime, 0, keyCode, 0, metaState, -1, 0, 0, 257);
        if (injectEventSync(downEvent)) {
            KeyEvent upEvent = new KeyEvent(eventTime, eventTime, 1, keyCode, 0, metaState, -1, 0, 0, 257);
            if (injectEventSync(upEvent)) {
                return true;
            }
            return DEBUG;
        }
        return DEBUG;
    }

    public void setRotationRight() {
        this.mUiAutomatorBridge.setRotation(3);
    }

    public void setRotationLeft() {
        this.mUiAutomatorBridge.setRotation(1);
    }

    public void setRotationNatural() {
        this.mUiAutomatorBridge.setRotation(0);
    }

    public void freezeRotation() {
        this.mUiAutomatorBridge.setRotation(-1);
    }

    public void unfreezeRotation() {
        this.mUiAutomatorBridge.setRotation(-2);
    }

    public boolean wakeDevice() throws RemoteException {
        if (isScreenOn()) {
            return DEBUG;
        }
        sendKey(26, 0);
        return true;
    }

    public boolean sleepDevice() throws RemoteException {
        if (isScreenOn()) {
            sendKey(26, 0);
            return true;
        }
        return DEBUG;
    }

    public boolean isScreenOn() throws RemoteException {
        return this.mUiAutomatorBridge.isScreenOn();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean injectEventSync(InputEvent event) {
        return this.mUiAutomatorBridge.injectInputEvent(event, true);
    }

    private int getPointerAction(int motionEnvent, int index) {
        return (index << 8) + motionEnvent;
    }

    public boolean performMultiPointerGesture(MotionEvent.PointerCoords[]... touches) {
        if (touches.length < 2) {
            throw new IllegalArgumentException("Must provide coordinates for at least 2 pointers");
        }
        int maxSteps = 0;
        for (int x = 0; x < touches.length; x++) {
            maxSteps = maxSteps < touches[x].length ? touches[x].length : maxSteps;
        }
        int x2 = touches.length;
        MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[x2];
        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[touches.length];
        for (int x3 = 0; x3 < touches.length; x3++) {
            MotionEvent.PointerProperties prop = new MotionEvent.PointerProperties();
            prop.id = x3;
            prop.toolType = 1;
            properties[x3] = prop;
            pointerCoords[x3] = touches[x3][0];
        }
        long downTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), 0, 1, properties, pointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 4098, 0);
        boolean ret = true & injectEventSync(event);
        for (int x4 = 1; x4 < touches.length; x4++) {
            MotionEvent event2 = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), getPointerAction(MOTION_EVENT_INJECTION_DELAY_MILLIS, x4), x4 + 1, properties, pointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 4098, 0);
            ret &= injectEventSync(event2);
        }
        for (int i = 1; i < maxSteps - 1; i++) {
            for (int x5 = 0; x5 < touches.length; x5++) {
                if (touches[x5].length > i) {
                    pointerCoords[x5] = touches[x5][i];
                } else {
                    pointerCoords[x5] = touches[x5][touches[x5].length - 1];
                }
            }
            MotionEvent event3 = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), 2, touches.length, properties, pointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 4098, 0);
            ret &= injectEventSync(event3);
            SystemClock.sleep(5L);
        }
        for (int x6 = 0; x6 < touches.length; x6++) {
            pointerCoords[x6] = touches[x6][touches[x6].length - 1];
        }
        for (int x7 = 1; x7 < touches.length; x7++) {
            MotionEvent event4 = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), getPointerAction(6, x7), x7 + 1, properties, pointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 4098, 0);
            ret &= injectEventSync(event4);
        }
        Log.i(LOG_TAG, "x " + pointerCoords[0].x);
        MotionEvent event5 = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), 1, 1, properties, pointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 4098, 0);
        return ret & injectEventSync(event5);
    }

    public boolean toggleRecentApps() {
        return this.mUiAutomatorBridge.performGlobalAction(3);
    }

    public boolean openNotification() {
        return this.mUiAutomatorBridge.performGlobalAction(4);
    }

    public boolean openQuickSettings() {
        return this.mUiAutomatorBridge.performGlobalAction(MOTION_EVENT_INJECTION_DELAY_MILLIS);
    }
}
