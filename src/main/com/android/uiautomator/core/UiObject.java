package com.android.uiautomator.core;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
@Deprecated
/* loaded from: classes.dex */
public class UiObject {
    protected static final int FINGER_TOUCH_HALF_WIDTH = 20;
    private static final String LOG_TAG = UiObject.class.getSimpleName();
    protected static final int SWIPE_MARGIN_LIMIT = 5;
    @Deprecated
    protected static final long WAIT_FOR_EVENT_TMEOUT = 3000;
    protected static final long WAIT_FOR_SELECTOR_POLL = 1000;
    @Deprecated
    protected static final long WAIT_FOR_SELECTOR_TIMEOUT = 10000;
    protected static final long WAIT_FOR_WINDOW_TMEOUT = 5500;
    private final Configurator mConfig = Configurator.getInstance();
    private final UiSelector mSelector;

    public UiObject(UiSelector selector) {
        this.mSelector = selector;
    }

    public final UiSelector getSelector() {
        Tracer.trace(new Object[0]);
        return new UiSelector(this.mSelector);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QueryController getQueryController() {
        return UiDevice.getInstance().getAutomatorBridge().getQueryController();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InteractionController getInteractionController() {
        return UiDevice.getInstance().getAutomatorBridge().getInteractionController();
    }

    public UiObject getChild(UiSelector selector) throws UiObjectNotFoundException {
        Tracer.trace(selector);
        return new UiObject(getSelector().childSelector(selector));
    }

    public UiObject getFromParent(UiSelector selector) throws UiObjectNotFoundException {
        Tracer.trace(selector);
        return new UiObject(getSelector().fromParent(selector));
    }

    public int getChildCount() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        return node.getChildCount();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AccessibilityNodeInfo findAccessibilityNodeInfo(long timeout) {
        AccessibilityNodeInfo node = null;
        long startMills = SystemClock.uptimeMillis();
        long currentMills = 0;
        while (currentMills <= timeout && (node = getQueryController().findAccessibilityNodeInfo(getSelector())) == null) {
            UiDevice.getInstance().runWatchers();
            currentMills = SystemClock.uptimeMillis() - startMills;
            if (timeout > 0) {
                SystemClock.sleep(WAIT_FOR_SELECTOR_POLL);
            }
        }
        return node;
    }

    public boolean dragTo(UiObject destObj, int steps) throws UiObjectNotFoundException {
        Rect srcRect = getVisibleBounds();
        Rect dstRect = destObj.getVisibleBounds();
        return getInteractionController().swipe(srcRect.centerX(), srcRect.centerY(), dstRect.centerX(), dstRect.centerY(), steps, true);
    }

    public boolean dragTo(int destX, int destY, int steps) throws UiObjectNotFoundException {
        Rect srcRect = getVisibleBounds();
        return getInteractionController().swipe(srcRect.centerX(), srcRect.centerY(), destX, destY, steps, true);
    }

    public boolean swipeUp(int steps) throws UiObjectNotFoundException {
        Tracer.trace(Integer.valueOf(steps));
        Rect rect = getVisibleBounds();
        if (rect.height() <= 10) {
            return false;
        }
        return getInteractionController().swipe(rect.centerX(), rect.bottom - 5, rect.centerX(), rect.top + SWIPE_MARGIN_LIMIT, steps);
    }

    public boolean swipeDown(int steps) throws UiObjectNotFoundException {
        Tracer.trace(Integer.valueOf(steps));
        Rect rect = getVisibleBounds();
        if (rect.height() <= 10) {
            return false;
        }
        return getInteractionController().swipe(rect.centerX(), rect.top + SWIPE_MARGIN_LIMIT, rect.centerX(), rect.bottom - 5, steps);
    }

    public boolean swipeLeft(int steps) throws UiObjectNotFoundException {
        Tracer.trace(Integer.valueOf(steps));
        Rect rect = getVisibleBounds();
        if (rect.width() <= 10) {
            return false;
        }
        return getInteractionController().swipe(rect.right - 5, rect.centerY(), rect.left + SWIPE_MARGIN_LIMIT, rect.centerY(), steps);
    }

    public boolean swipeRight(int steps) throws UiObjectNotFoundException {
        Tracer.trace(Integer.valueOf(steps));
        Rect rect = getVisibleBounds();
        if (rect.width() <= 10) {
            return false;
        }
        return getInteractionController().swipe(rect.left + SWIPE_MARGIN_LIMIT, rect.centerY(), rect.right - 5, rect.centerY(), steps);
    }

    private Rect getVisibleBounds(AccessibilityNodeInfo node) {
        if (node == null) {
            return null;
        }
        int w = UiDevice.getInstance().getDisplayWidth();
        int h = UiDevice.getInstance().getDisplayHeight();
        Rect nodeRect = AccessibilityNodeInfoHelper.getVisibleBoundsInScreen(node, w, h);
        AccessibilityNodeInfo scrollableParentNode = getScrollableParent(node);
        if (scrollableParentNode == null) {
            return nodeRect;
        }
        Rect parentRect = AccessibilityNodeInfoHelper.getVisibleBoundsInScreen(scrollableParentNode, w, h);
        if (nodeRect.intersect(parentRect)) {
            return nodeRect;
        }
        return new Rect();
    }

    private AccessibilityNodeInfo getScrollableParent(AccessibilityNodeInfo node) {
        AccessibilityNodeInfo parent = node;
        while (parent != null) {
            parent = parent.getParent();
            if (parent != null && parent.isScrollable()) {
                return parent;
            }
        }
        return null;
    }

    public boolean click() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        Rect rect = getVisibleBounds(node);
        return getInteractionController().clickAndSync(rect.centerX(), rect.centerY(), this.mConfig.getActionAcknowledgmentTimeout());
    }

    public boolean clickAndWaitForNewWindow() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        return clickAndWaitForNewWindow(WAIT_FOR_WINDOW_TMEOUT);
    }

    public boolean clickAndWaitForNewWindow(long timeout) throws UiObjectNotFoundException {
        Tracer.trace(Long.valueOf(timeout));
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        Rect rect = getVisibleBounds(node);
        return getInteractionController().clickAndWaitForNewWindow(rect.centerX(), rect.centerY(), this.mConfig.getActionAcknowledgmentTimeout());
    }

    public boolean clickTopLeft() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        Rect rect = getVisibleBounds(node);
        return getInteractionController().clickNoSync(rect.left + SWIPE_MARGIN_LIMIT, rect.top + SWIPE_MARGIN_LIMIT);
    }

    public boolean longClickBottomRight() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        Rect rect = getVisibleBounds(node);
        return getInteractionController().longTapNoSync(rect.right - 5, rect.bottom - 5);
    }

    public boolean clickBottomRight() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        Rect rect = getVisibleBounds(node);
        return getInteractionController().clickNoSync(rect.right - 5, rect.bottom - 5);
    }

    public boolean longClick() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        Rect rect = getVisibleBounds(node);
        return getInteractionController().longTapNoSync(rect.centerX(), rect.centerY());
    }

    public boolean longClickTopLeft() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        Rect rect = getVisibleBounds(node);
        return getInteractionController().longTapNoSync(rect.left + SWIPE_MARGIN_LIMIT, rect.top + SWIPE_MARGIN_LIMIT);
    }

    public String getText() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        String retVal = safeStringReturn(node.getText());
        Log.d(LOG_TAG, String.format("getText() = %s", retVal));
        return retVal;
    }

    public String getClassName() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        String retVal = safeStringReturn(node.getClassName());
        Log.d(LOG_TAG, String.format("getClassName() = %s", retVal));
        return retVal;
    }

    public String getContentDescription() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        return safeStringReturn(node.getContentDescription());
    }

    public boolean setText(String text) throws UiObjectNotFoundException {
        Tracer.trace(text);
        clearTextField();
        return getInteractionController().sendText(text);
    }

    public void clearTextField() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        Rect rect = getVisibleBounds(node);
        getInteractionController().longTapNoSync(rect.left + FINGER_TOUCH_HALF_WIDTH, rect.centerY());
        UiObject selectAll = new UiObject(new UiSelector().descriptionContains("Select all"));
        if (selectAll.waitForExists(50L)) {
            selectAll.click();
        }
        SystemClock.sleep(250L);
        getInteractionController().sendKey(67, 0);
    }

    public boolean isChecked() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        return node.isChecked();
    }

    public boolean isSelected() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        return node.isSelected();
    }

    public boolean isCheckable() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        return node.isCheckable();
    }

    public boolean isEnabled() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        return node.isEnabled();
    }

    public boolean isClickable() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        return node.isClickable();
    }

    public boolean isFocused() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        return node.isFocused();
    }

    public boolean isFocusable() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        return node.isFocusable();
    }

    public boolean isScrollable() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        return node.isScrollable();
    }

    public boolean isLongClickable() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        return node.isLongClickable();
    }

    public String getPackageName() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        return safeStringReturn(node.getPackageName());
    }

    public Rect getVisibleBounds() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        return getVisibleBounds(node);
    }

    public Rect getBounds() throws UiObjectNotFoundException {
        Tracer.trace(new Object[0]);
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        Rect nodeRect = new Rect();
        node.getBoundsInScreen(nodeRect);
        return nodeRect;
    }

    public boolean waitForExists(long timeout) {
        Tracer.trace(Long.valueOf(timeout));
        if (findAccessibilityNodeInfo(timeout) != null) {
            return true;
        }
        return false;
    }

    public boolean waitUntilGone(long timeout) {
        Tracer.trace(Long.valueOf(timeout));
        long startMills = SystemClock.uptimeMillis();
        long currentMills = 0;
        while (currentMills <= timeout) {
            if (findAccessibilityNodeInfo(0L) == null) {
                return true;
            }
            currentMills = SystemClock.uptimeMillis() - startMills;
            if (timeout > 0) {
                SystemClock.sleep(WAIT_FOR_SELECTOR_POLL);
            }
        }
        return false;
    }

    public boolean exists() {
        Tracer.trace(new Object[0]);
        return waitForExists(0L);
    }

    private String safeStringReturn(CharSequence cs) {
        if (cs == null) {
            return "";
        }
        return cs.toString();
    }

    public boolean pinchOut(int percent, int steps) throws UiObjectNotFoundException {
        int percent2;
        if (percent < 0) {
            percent2 = 1;
        } else {
            percent2 = 100;
            if (percent <= 100) {
                percent2 = percent;
            }
        }
        float percentage = percent2 / 100.0f;
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        Rect rect = getVisibleBounds(node);
        if (rect.width() <= 40) {
            throw new IllegalStateException("Object width is too small for operation");
        }
        Point startPoint1 = new Point(rect.centerX() - 20, rect.centerY());
        Point startPoint2 = new Point(rect.centerX() + FINGER_TOUCH_HALF_WIDTH, rect.centerY());
        Point endPoint1 = new Point(rect.centerX() - ((int) ((rect.width() / 2) * percentage)), rect.centerY());
        Point endPoint2 = new Point(rect.centerX() + ((int) ((rect.width() / 2) * percentage)), rect.centerY());
        return performTwoPointerGesture(startPoint1, startPoint2, endPoint1, endPoint2, steps);
    }

    public boolean pinchIn(int percent, int steps) throws UiObjectNotFoundException {
        int percent2;
        if (percent < 0) {
            percent2 = 0;
        } else {
            percent2 = 100;
            if (percent <= 100) {
                percent2 = percent;
            }
        }
        float percentage = percent2 / 100.0f;
        AccessibilityNodeInfo node = findAccessibilityNodeInfo(this.mConfig.getWaitForSelectorTimeout());
        if (node == null) {
            throw new UiObjectNotFoundException(getSelector().toString());
        }
        Rect rect = getVisibleBounds(node);
        if (rect.width() <= 40) {
            throw new IllegalStateException("Object width is too small for operation");
        }
        Point startPoint1 = new Point(rect.centerX() - ((int) ((rect.width() / 2) * percentage)), rect.centerY());
        Point startPoint2 = new Point(rect.centerX() + ((int) ((rect.width() / 2) * percentage)), rect.centerY());
        Point endPoint1 = new Point(rect.centerX() - 20, rect.centerY());
        Point endPoint2 = new Point(rect.centerX() + FINGER_TOUCH_HALF_WIDTH, rect.centerY());
        return performTwoPointerGesture(startPoint1, startPoint2, endPoint1, endPoint2, steps);
    }

    public boolean performTwoPointerGesture(Point startPoint1, Point startPoint2, Point endPoint1, Point endPoint2, int steps) {
        int steps2;
        if (steps != 0) {
            steps2 = steps;
        } else {
            steps2 = 1;
        }
        float stepX1 = (endPoint1.x - startPoint1.x) / steps2;
        float stepY1 = (endPoint1.y - startPoint1.y) / steps2;
        float stepX2 = (endPoint2.x - startPoint2.x) / steps2;
        float stepY2 = (endPoint2.y - startPoint2.y) / steps2;
        int eventX1 = startPoint1.x;
        int eventY1 = startPoint1.y;
        int eventX2 = startPoint2.x;
        int eventY2 = startPoint2.y;
        MotionEvent.PointerCoords[] points1 = new MotionEvent.PointerCoords[steps2 + 2];
        MotionEvent.PointerCoords[] points2 = new MotionEvent.PointerCoords[steps2 + 2];
        for (int i = 0; i < steps2 + 1; i++) {
            MotionEvent.PointerCoords p1 = new MotionEvent.PointerCoords();
            p1.x = eventX1;
            p1.y = eventY1;
            p1.pressure = 1.0f;
            p1.size = 1.0f;
            points1[i] = p1;
            MotionEvent.PointerCoords p2 = new MotionEvent.PointerCoords();
            p2.x = eventX2;
            p2.y = eventY2;
            p2.pressure = 1.0f;
            p2.size = 1.0f;
            points2[i] = p2;
            eventX1 = (int) (eventX1 + stepX1);
            eventY1 = (int) (eventY1 + stepY1);
            eventX2 = (int) (eventX2 + stepX2);
            eventY2 = (int) (eventY2 + stepY2);
        }
        MotionEvent.PointerCoords p12 = new MotionEvent.PointerCoords();
        p12.x = endPoint1.x;
        p12.y = endPoint1.y;
        p12.pressure = 1.0f;
        p12.size = 1.0f;
        points1[steps2 + 1] = p12;
        MotionEvent.PointerCoords p22 = new MotionEvent.PointerCoords();
        p22.x = endPoint2.x;
        p22.y = endPoint2.y;
        p22.pressure = 1.0f;
        p22.size = 1.0f;
        points2[steps2 + 1] = p22;
        return performMultiPointerGesture(points1, points2);
    }

    public boolean performMultiPointerGesture(MotionEvent.PointerCoords[]... touches) {
        return getInteractionController().performMultiPointerGesture(touches);
    }
}
