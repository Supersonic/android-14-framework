package android.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.graphics.Insets;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
@Deprecated
/* loaded from: classes.dex */
public class TouchUtils {
    @Deprecated
    public static void dragQuarterScreenDown(ActivityInstrumentationTestCase test) {
        dragQuarterScreenDown(test, test.getActivity());
    }

    public static void dragQuarterScreenDown(InstrumentationTestCase test, Activity activity) {
        WindowManager wm = activity.getWindowManager();
        Size size = getSizeExcludingNavigationBarAndCutout(wm.getCurrentWindowMetrics());
        float x = size.getWidth() / 2.0f;
        float fromY = size.getHeight() * 0.5f;
        float toY = size.getHeight() * 0.75f;
        drag(test, x, x, fromY, toY, 4);
    }

    @Deprecated
    public static void dragQuarterScreenUp(ActivityInstrumentationTestCase test) {
        dragQuarterScreenUp(test, test.getActivity());
    }

    public static void dragQuarterScreenUp(InstrumentationTestCase test, Activity activity) {
        WindowManager wm = activity.getWindowManager();
        Size size = getSizeExcludingNavigationBarAndCutout(wm.getCurrentWindowMetrics());
        float x = size.getWidth() / 2.0f;
        float fromY = size.getHeight() * 0.5f;
        float toY = size.getHeight() * 0.25f;
        drag(test, x, x, fromY, toY, 4);
    }

    private static Size getSizeExcludingNavigationBarAndCutout(WindowMetrics windowMetrics) {
        WindowInsets windowInsets = windowMetrics.getWindowInsets();
        Insets insetsWithCutout = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars() | WindowInsets.Type.displayCutout());
        int insetsWidth = insetsWithCutout.left + insetsWithCutout.right;
        int insetsHeight = insetsWithCutout.top + insetsWithCutout.bottom;
        Rect bounds = windowMetrics.getBounds();
        return new Size(bounds.width() - insetsWidth, bounds.height() - insetsHeight);
    }

    @Deprecated
    public static void scrollToBottom(ActivityInstrumentationTestCase test, ViewGroup v) {
        scrollToBottom(test, test.getActivity(), v);
    }

    public static void scrollToBottom(InstrumentationTestCase test, Activity activity, ViewGroup v) {
        ViewStateSnapshot prev;
        ViewStateSnapshot next = new ViewStateSnapshot(v);
        do {
            prev = next;
            dragQuarterScreenUp(test, activity);
            next = new ViewStateSnapshot(v);
        } while (!prev.equals(next));
    }

    @Deprecated
    public static void scrollToTop(ActivityInstrumentationTestCase test, ViewGroup v) {
        scrollToTop(test, test.getActivity(), v);
    }

    public static void scrollToTop(InstrumentationTestCase test, Activity activity, ViewGroup v) {
        ViewStateSnapshot prev;
        ViewStateSnapshot next = new ViewStateSnapshot(v);
        do {
            prev = next;
            dragQuarterScreenDown(test, activity);
            next = new ViewStateSnapshot(v);
        } while (!prev.equals(next));
    }

    @Deprecated
    public static void dragViewToBottom(ActivityInstrumentationTestCase test, View v) {
        dragViewToBottom(test, test.getActivity(), v, 4);
    }

    public static void dragViewToBottom(InstrumentationTestCase test, Activity activity, View v) {
        dragViewToBottom(test, activity, v, 4);
    }

    @Deprecated
    public static void dragViewToBottom(ActivityInstrumentationTestCase test, View v, int stepCount) {
        dragViewToBottom(test, test.getActivity(), v, stepCount);
    }

    public static void dragViewToBottom(InstrumentationTestCase test, Activity activity, View v, int stepCount) {
        WindowManager wm = activity.getWindowManager();
        int screenHeight = getSizeExcludingNavigationBarAndCutout(wm.getCurrentWindowMetrics()).getHeight();
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);
        int viewWidth = v.getWidth();
        int viewHeight = v.getHeight();
        float x = xy[0] + (viewWidth / 2.0f);
        float fromY = xy[1] + (viewHeight / 2.0f);
        float toY = screenHeight - 1;
        drag(test, x, x, fromY, toY, stepCount);
    }

    public static void tapView(InstrumentationTestCase test, View v) {
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);
        int viewWidth = v.getWidth();
        int viewHeight = v.getHeight();
        float x = xy[0] + (viewWidth / 2.0f);
        float y = xy[1] + (viewHeight / 2.0f);
        Instrumentation inst = test.getInstrumentation();
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(downTime, eventTime, 0, x, y, 0);
        inst.sendPointerSync(event);
        inst.waitForIdleSync();
        long eventTime2 = SystemClock.uptimeMillis();
        int touchSlop = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();
        MotionEvent event2 = MotionEvent.obtain(downTime, eventTime2, 2, x + (touchSlop / 2.0f), y + (touchSlop / 2.0f), 0);
        inst.sendPointerSync(event2);
        inst.waitForIdleSync();
        long eventTime3 = SystemClock.uptimeMillis();
        MotionEvent event3 = MotionEvent.obtain(downTime, eventTime3, 1, x, y, 0);
        inst.sendPointerSync(event3);
        inst.waitForIdleSync();
    }

    public static void touchAndCancelView(InstrumentationTestCase test, View v) {
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);
        int viewWidth = v.getWidth();
        int viewHeight = v.getHeight();
        float x = xy[0] + (viewWidth / 2.0f);
        float y = xy[1] + (viewHeight / 2.0f);
        Instrumentation inst = test.getInstrumentation();
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(downTime, eventTime, 0, x, y, 0);
        inst.sendPointerSync(event);
        inst.waitForIdleSync();
        long eventTime2 = SystemClock.uptimeMillis();
        int touchSlop = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();
        MotionEvent event2 = MotionEvent.obtain(downTime, eventTime2, 3, x + (touchSlop / 2.0f), y + (touchSlop / 2.0f), 0);
        inst.sendPointerSync(event2);
        inst.waitForIdleSync();
    }

    public static void clickView(InstrumentationTestCase test, View v) {
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);
        int viewWidth = v.getWidth();
        int viewHeight = v.getHeight();
        float x = (viewWidth / 2.0f) + xy[0];
        float y = xy[1] + (viewHeight / 2.0f);
        Instrumentation inst = test.getInstrumentation();
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(downTime, eventTime, 0, x, y, 0);
        inst.sendPointerSync(event);
        inst.waitForIdleSync();
        long eventTime2 = SystemClock.uptimeMillis();
        int touchSlop = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();
        MotionEvent event2 = MotionEvent.obtain(downTime, eventTime2, 2, x + (touchSlop / 2.0f), y + (touchSlop / 2.0f), 0);
        inst.sendPointerSync(event2);
        inst.waitForIdleSync();
        long eventTime3 = SystemClock.uptimeMillis();
        MotionEvent event3 = MotionEvent.obtain(downTime, eventTime3, 1, x, y, 0);
        inst.sendPointerSync(event3);
        inst.waitForIdleSync();
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public static void longClickView(ActivityInstrumentationTestCase test, View v) {
        longClickView((InstrumentationTestCase) test, v);
    }

    public static void longClickView(InstrumentationTestCase test, View v) {
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);
        int viewWidth = v.getWidth();
        int viewHeight = v.getHeight();
        float x = (viewWidth / 2.0f) + xy[0];
        float y = xy[1] + (viewHeight / 2.0f);
        Instrumentation inst = test.getInstrumentation();
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(downTime, eventTime, 0, x, y, 0);
        inst.sendPointerSync(event);
        inst.waitForIdleSync();
        long eventTime2 = SystemClock.uptimeMillis();
        int touchSlop = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();
        MotionEvent event2 = MotionEvent.obtain(downTime, eventTime2, 2, x + (touchSlop / 2), y + (touchSlop / 2), 0);
        inst.sendPointerSync(event2);
        inst.waitForIdleSync();
        try {
            Thread.sleep(ViewConfiguration.getLongPressTimeout() * 1.5f);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long eventTime3 = SystemClock.uptimeMillis();
        MotionEvent event3 = MotionEvent.obtain(downTime, eventTime3, 1, x, y, 0);
        inst.sendPointerSync(event3);
        inst.waitForIdleSync();
    }

    @Deprecated
    public static void dragViewToTop(ActivityInstrumentationTestCase test, View v) {
        dragViewToTop((InstrumentationTestCase) test, v, 4);
    }

    @Deprecated
    public static void dragViewToTop(ActivityInstrumentationTestCase test, View v, int stepCount) {
        dragViewToTop((InstrumentationTestCase) test, v, stepCount);
    }

    public static void dragViewToTop(InstrumentationTestCase test, View v) {
        dragViewToTop(test, v, 4);
    }

    public static void dragViewToTop(InstrumentationTestCase test, View v, int stepCount) {
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);
        int viewWidth = v.getWidth();
        int viewHeight = v.getHeight();
        float x = xy[0] + (viewWidth / 2.0f);
        float fromY = xy[1] + (viewHeight / 2.0f);
        drag(test, x, x, fromY, 0.0f, stepCount);
    }

    private static void getStartLocation(View v, int gravity, int[] xy) {
        v.getLocationOnScreen(xy);
        int viewWidth = v.getWidth();
        int viewHeight = v.getHeight();
        switch (gravity & 112) {
            case 16:
                xy[1] = xy[1] + (viewHeight / 2);
                break;
            case 80:
                xy[1] = xy[1] + (viewHeight - 1);
                break;
        }
        switch (gravity & 7) {
            case 1:
                xy[0] = xy[0] + (viewWidth / 2);
                return;
            case 2:
            case 3:
            case 4:
            default:
                return;
            case 5:
                xy[0] = xy[0] + (viewWidth - 1);
                return;
        }
    }

    @Deprecated
    public static int dragViewBy(ActivityInstrumentationTestCase test, View v, int gravity, int deltaX, int deltaY) {
        return dragViewBy((InstrumentationTestCase) test, v, gravity, deltaX, deltaY);
    }

    @Deprecated
    public static int dragViewBy(InstrumentationTestCase test, View v, int gravity, int deltaX, int deltaY) {
        int[] xy = new int[2];
        getStartLocation(v, gravity, xy);
        int fromX = xy[0];
        int fromY = xy[1];
        int distance = (int) Math.hypot(deltaX, deltaY);
        drag(test, fromX, fromX + deltaX, fromY, fromY + deltaY, distance);
        return distance;
    }

    @Deprecated
    public static int dragViewTo(ActivityInstrumentationTestCase test, View v, int gravity, int toX, int toY) {
        return dragViewTo((InstrumentationTestCase) test, v, gravity, toX, toY);
    }

    public static int dragViewTo(InstrumentationTestCase test, View v, int gravity, int toX, int toY) {
        int[] xy = new int[2];
        getStartLocation(v, gravity, xy);
        int fromX = xy[0];
        int fromY = xy[1];
        int deltaX = fromX - toX;
        int deltaY = fromY - toY;
        int distance = (int) Math.hypot(deltaX, deltaY);
        drag(test, fromX, toX, fromY, toY, distance);
        return distance;
    }

    @Deprecated
    public static int dragViewToX(ActivityInstrumentationTestCase test, View v, int gravity, int toX) {
        return dragViewToX((InstrumentationTestCase) test, v, gravity, toX);
    }

    public static int dragViewToX(InstrumentationTestCase test, View v, int gravity, int toX) {
        int[] xy = new int[2];
        getStartLocation(v, gravity, xy);
        int fromX = xy[0];
        int fromY = xy[1];
        int deltaX = fromX - toX;
        drag(test, fromX, toX, fromY, fromY, deltaX);
        return deltaX;
    }

    @Deprecated
    public static int dragViewToY(ActivityInstrumentationTestCase test, View v, int gravity, int toY) {
        return dragViewToY((InstrumentationTestCase) test, v, gravity, toY);
    }

    public static int dragViewToY(InstrumentationTestCase test, View v, int gravity, int toY) {
        int[] xy = new int[2];
        getStartLocation(v, gravity, xy);
        int fromX = xy[0];
        int fromY = xy[1];
        int deltaY = fromY - toY;
        drag(test, fromX, fromX, fromY, toY, deltaY);
        return deltaY;
    }

    @Deprecated
    public static void drag(ActivityInstrumentationTestCase test, float fromX, float toX, float fromY, float toY, int stepCount) {
        drag((InstrumentationTestCase) test, fromX, toX, fromY, toY, stepCount);
    }

    public static void drag(InstrumentationTestCase test, float fromX, float toX, float fromY, float toY, int stepCount) {
        Instrumentation inst = test.getInstrumentation();
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        float yStep = (toY - fromY) / stepCount;
        float xStep = (toX - fromX) / stepCount;
        MotionEvent event = MotionEvent.obtain(downTime, eventTime, 0, fromX, fromY, 0);
        inst.sendPointerSync(event);
        float x = fromX;
        float y = fromY;
        for (int i = 0; i < stepCount; i++) {
            y += yStep;
            x += xStep;
            long eventTime2 = SystemClock.uptimeMillis();
            MotionEvent event2 = MotionEvent.obtain(downTime, eventTime2, 2, x, y, 0);
            inst.sendPointerSync(event2);
        }
        long eventTime3 = SystemClock.uptimeMillis();
        MotionEvent event3 = MotionEvent.obtain(downTime, eventTime3, 1, x, y, 0);
        inst.sendPointerSync(event3);
        inst.waitForIdleSync();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ViewStateSnapshot {
        final int mChildCount;
        final View mFirst;
        final int mFirstTop;
        final View mLast;
        final int mLastBottom;

        private ViewStateSnapshot(ViewGroup viewGroup) {
            int childCount = viewGroup.getChildCount();
            this.mChildCount = childCount;
            if (childCount == 0) {
                this.mLast = null;
                this.mFirst = null;
                this.mLastBottom = Integer.MIN_VALUE;
                this.mFirstTop = Integer.MIN_VALUE;
                return;
            }
            View childAt = viewGroup.getChildAt(0);
            this.mFirst = childAt;
            View childAt2 = viewGroup.getChildAt(childCount - 1);
            this.mLast = childAt2;
            this.mFirstTop = childAt.getTop();
            this.mLastBottom = childAt2.getBottom();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ViewStateSnapshot that = (ViewStateSnapshot) o;
            if (this.mFirstTop == that.mFirstTop && this.mLastBottom == that.mLastBottom && this.mFirst == that.mFirst && this.mLast == that.mLast && this.mChildCount == that.mChildCount) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            View view = this.mFirst;
            int result = view != null ? view.hashCode() : 0;
            int i = result * 31;
            View view2 = this.mLast;
            int result2 = i + (view2 != null ? view2.hashCode() : 0);
            return (((((result2 * 31) + this.mFirstTop) * 31) + this.mLastBottom) * 31) + this.mChildCount;
        }
    }
}
