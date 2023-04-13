package com.android.uiautomator.core;

import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
@Deprecated
/* loaded from: classes.dex */
public class UiScrollable extends UiCollection {
    private static final double DEFAULT_SWIPE_DEADZONE_PCT = 0.1d;
    private static final int FLING_STEPS = 5;
    private static final int SCROLL_STEPS = 55;
    private boolean mIsVerticalList;
    private double mSwipeDeadZonePercentage;
    private static final String LOG_TAG = UiScrollable.class.getSimpleName();
    private static int mMaxSearchSwipes = 30;

    public UiScrollable(UiSelector container) {
        super(container);
        this.mIsVerticalList = true;
        this.mSwipeDeadZonePercentage = DEFAULT_SWIPE_DEADZONE_PCT;
    }

    public UiScrollable setAsVerticalList() {
        Tracer.trace(new Object[0]);
        this.mIsVerticalList = true;
        return this;
    }

    public UiScrollable setAsHorizontalList() {
        Tracer.trace(new Object[0]);
        this.mIsVerticalList = false;
        return this;
    }

    protected boolean exists(UiSelector selector) {
        if (getQueryController().findAccessibilityNodeInfo(selector) != null) {
            return true;
        }
        return false;
    }

    @Override // com.android.uiautomator.core.UiCollection
    public UiObject getChildByDescription(UiSelector childPattern, String text) throws UiObjectNotFoundException {
        Tracer.trace(childPattern, text);
        return getChildByDescription(childPattern, text, true);
    }

    public UiObject getChildByDescription(UiSelector childPattern, String text, boolean allowScrollSearch) throws UiObjectNotFoundException {
        Tracer.trace(childPattern, text, Boolean.valueOf(allowScrollSearch));
        if (text != null) {
            if (allowScrollSearch) {
                scrollIntoView(new UiSelector().descriptionContains(text));
            }
            return super.getChildByDescription(childPattern, text);
        }
        throw new UiObjectNotFoundException("for description= \"" + text + "\"");
    }

    @Override // com.android.uiautomator.core.UiCollection
    public UiObject getChildByInstance(UiSelector childPattern, int instance) throws UiObjectNotFoundException {
        Tracer.trace(childPattern, Integer.valueOf(instance));
        UiSelector patternSelector = UiSelector.patternBuilder(getSelector(), UiSelector.patternBuilder(childPattern).instance(instance));
        return new UiObject(patternSelector);
    }

    @Override // com.android.uiautomator.core.UiCollection
    public UiObject getChildByText(UiSelector childPattern, String text) throws UiObjectNotFoundException {
        Tracer.trace(childPattern, text);
        return getChildByText(childPattern, text, true);
    }

    public UiObject getChildByText(UiSelector childPattern, String text, boolean allowScrollSearch) throws UiObjectNotFoundException {
        Tracer.trace(childPattern, text, Boolean.valueOf(allowScrollSearch));
        if (text != null) {
            if (allowScrollSearch) {
                scrollIntoView(new UiSelector().text(text));
            }
            return super.getChildByText(childPattern, text);
        }
        throw new UiObjectNotFoundException("for text= \"" + text + "\"");
    }

    public boolean scrollDescriptionIntoView(String text) throws UiObjectNotFoundException {
        Tracer.trace(text);
        return scrollIntoView(new UiSelector().description(text));
    }

    public boolean scrollIntoView(UiObject obj) throws UiObjectNotFoundException {
        Tracer.trace(obj.getSelector());
        return scrollIntoView(obj.getSelector());
    }

    public boolean scrollIntoView(UiSelector selector) throws UiObjectNotFoundException {
        Tracer.trace(selector);
        UiSelector childSelector = getSelector().childSelector(selector);
        if (exists(childSelector)) {
            return true;
        }
        scrollToBeginning(mMaxSearchSwipes);
        if (exists(childSelector)) {
            return true;
        }
        for (int x = 0; x < mMaxSearchSwipes; x++) {
            boolean scrolled = scrollForward();
            if (exists(childSelector)) {
                return true;
            }
            if (!scrolled) {
                return false;
            }
        }
        return false;
    }

    public boolean ensureFullyVisible(UiObject childObject) throws UiObjectNotFoundException {
        boolean shouldSwipeForward;
        Rect actual = childObject.getBounds();
        Rect visible = childObject.getVisibleBounds();
        if (visible.width() * visible.height() == actual.width() * actual.height()) {
            return true;
        }
        if (this.mIsVerticalList) {
            shouldSwipeForward = actual.top == visible.top;
        } else {
            shouldSwipeForward = actual.left == visible.left;
        }
        if (this.mIsVerticalList) {
            if (shouldSwipeForward) {
                return swipeUp(10);
            }
            return swipeDown(10);
        } else if (shouldSwipeForward) {
            return swipeLeft(10);
        } else {
            return swipeRight(10);
        }
    }

    public boolean scrollTextIntoView(String text) throws UiObjectNotFoundException {
        Tracer.trace(text);
        return scrollIntoView(new UiSelector().text(text));
    }

    public UiScrollable setMaxSearchSwipes(int swipes) {
        Tracer.trace(Integer.valueOf(swipes));
        mMaxSearchSwipes = swipes;
        return this;
    }

    public int getMaxSearchSwipes() {
        Tracer.trace(new Object[0]);
        return mMaxSearchSwipes;
    }

    public boolean flingForward() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        return scrollForward(FLING_STEPS);
    }

    public boolean scrollForward() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        return scrollForward(SCROLL_STEPS);
    }

    public boolean scrollForward(int steps) throws UiObjectNotFoundException {
        int downX;
        int downY;
        int downY2;
        int upY;
        Tracer.trace(Integer.valueOf(steps));
        Log.d(LOG_TAG, "scrollForward() on selector = " + getSelector());
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(10000L);
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        Rect rect = new Rect();
        node.getBoundsInScreen(rect);
        if (this.mIsVerticalList) {
            int swipeAreaAdjust = (int) (rect.height() * getSwipeDeadZonePercentage());
            downX = rect.centerX();
            downY = rect.bottom - swipeAreaAdjust;
            downY2 = rect.centerX();
            int upX = rect.top;
            upY = upX + swipeAreaAdjust;
        } else {
            int swipeAreaAdjust2 = (int) (rect.width() * getSwipeDeadZonePercentage());
            downX = rect.right - swipeAreaAdjust2;
            downY = rect.centerY();
            int downY3 = rect.left;
            downY2 = downY3 + swipeAreaAdjust2;
            upY = rect.centerY();
        }
        return getInteractionController().scrollSwipe(downX, downY, downY2, upY, steps);
    }

    public boolean flingBackward() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        return scrollBackward(FLING_STEPS);
    }

    public boolean scrollBackward() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        return scrollBackward(SCROLL_STEPS);
    }

    public boolean scrollBackward(int steps) throws UiObjectNotFoundException {
        int downX;
        int downY;
        int downY2;
        int upY;
        Tracer.trace(Integer.valueOf(steps));
        String str = LOG_TAG;
        Log.d(str, "scrollBackward() on selector = " + getSelector());
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(10000L);
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        Rect rect = new Rect();
        node.getBoundsInScreen(rect);
        if (this.mIsVerticalList) {
            int swipeAreaAdjust = (int) (rect.height() * getSwipeDeadZonePercentage());
            Log.d(str, "scrollToBegining() using vertical scroll");
            downX = rect.centerX();
            int downX2 = rect.top;
            downY = downX2 + swipeAreaAdjust;
            downY2 = rect.centerX();
            int upX = rect.bottom;
            upY = upX - swipeAreaAdjust;
        } else {
            int swipeAreaAdjust2 = (int) (rect.width() * getSwipeDeadZonePercentage());
            Log.d(str, "scrollToBegining() using hotizontal scroll");
            downX = rect.left + swipeAreaAdjust2;
            downY = rect.centerY();
            int downY3 = rect.right;
            downY2 = downY3 - swipeAreaAdjust2;
            upY = rect.centerY();
        }
        return getInteractionController().scrollSwipe(downX, downY, downY2, upY, steps);
    }

    public boolean scrollToBeginning(int maxSwipes, int steps) throws UiObjectNotFoundException {
        Tracer.trace(Integer.valueOf(maxSwipes), Integer.valueOf(steps));
        Log.d(LOG_TAG, "scrollToBeginning() on selector = " + getSelector());
        for (int x = 0; x < maxSwipes && scrollBackward(steps); x++) {
        }
        return true;
    }

    public boolean scrollToBeginning(int maxSwipes) throws UiObjectNotFoundException {
        Tracer.trace(Integer.valueOf(maxSwipes));
        return scrollToBeginning(maxSwipes, SCROLL_STEPS);
    }

    public boolean flingToBeginning(int maxSwipes) throws UiObjectNotFoundException {
        Tracer.trace(Integer.valueOf(maxSwipes));
        return scrollToBeginning(maxSwipes, FLING_STEPS);
    }

    public boolean scrollToEnd(int maxSwipes, int steps) throws UiObjectNotFoundException {
        Tracer.trace(Integer.valueOf(maxSwipes), Integer.valueOf(steps));
        for (int x = 0; x < maxSwipes && scrollForward(steps); x++) {
        }
        return true;
    }

    public boolean scrollToEnd(int maxSwipes) throws UiObjectNotFoundException {
        Tracer.trace(Integer.valueOf(maxSwipes));
        return scrollToEnd(maxSwipes, SCROLL_STEPS);
    }

    public boolean flingToEnd(int maxSwipes) throws UiObjectNotFoundException {
        Tracer.trace(Integer.valueOf(maxSwipes));
        return scrollToEnd(maxSwipes, FLING_STEPS);
    }

    public double getSwipeDeadZonePercentage() {
        Tracer.trace(new Object[0]);
        return this.mSwipeDeadZonePercentage;
    }

    public UiScrollable setSwipeDeadZonePercentage(double swipeDeadZonePercentage) {
        Tracer.trace(Double.valueOf(swipeDeadZonePercentage));
        this.mSwipeDeadZonePercentage = swipeDeadZonePercentage;
        return this;
    }
}
