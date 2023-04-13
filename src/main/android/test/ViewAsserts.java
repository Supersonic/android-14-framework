package android.test;

import android.view.View;
import android.view.ViewGroup;
import junit.framework.Assert;
@Deprecated
/* loaded from: classes.dex */
public class ViewAsserts {
    private ViewAsserts() {
    }

    public static void assertOnScreen(View origin, View view) {
        boolean z;
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        int[] xyRoot = new int[2];
        origin.getLocationOnScreen(xyRoot);
        boolean z2 = true;
        int y = xy[1] - xyRoot[1];
        if (y >= 0) {
            z = true;
        } else {
            z = false;
        }
        Assert.assertTrue("view should have positive y coordinate on screen", z);
        if (y > view.getRootView().getHeight()) {
            z2 = false;
        }
        Assert.assertTrue("view should have y location on screen less than drawing height of root view", z2);
    }

    public static void assertOffScreenBelow(View origin, View view) {
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        int[] xyRoot = new int[2];
        origin.getLocationOnScreen(xyRoot);
        int y = xy[1] - xyRoot[1];
        Assert.assertTrue("view should have y location on screen greater than drawing height of origen view (" + y + " is not greater than " + origin.getHeight() + ")", y > origin.getHeight());
    }

    public static void assertOffScreenAbove(View origin, View view) {
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        int[] xyRoot = new int[2];
        origin.getLocationOnScreen(xyRoot);
        int y = xy[1] - xyRoot[1];
        Assert.assertTrue("view should have y location less than that of origin view", y < 0);
    }

    public static void assertHasScreenCoordinates(View origin, View view, int x, int y) {
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        int[] xyRoot = new int[2];
        origin.getLocationOnScreen(xyRoot);
        Assert.assertEquals("x coordinate", x, xy[0] - xyRoot[0]);
        Assert.assertEquals("y coordinate", y, xy[1] - xyRoot[1]);
    }

    public static void assertBaselineAligned(View first, View second) {
        int[] xy = new int[2];
        first.getLocationOnScreen(xy);
        int firstTop = xy[1] + first.getBaseline();
        second.getLocationOnScreen(xy);
        int secondTop = xy[1] + second.getBaseline();
        Assert.assertEquals("views are not baseline aligned", firstTop, secondTop);
    }

    public static void assertRightAligned(View first, View second) {
        int[] xy = new int[2];
        first.getLocationOnScreen(xy);
        int firstRight = xy[0] + first.getMeasuredWidth();
        second.getLocationOnScreen(xy);
        int secondRight = xy[0] + second.getMeasuredWidth();
        Assert.assertEquals("views are not right aligned", firstRight, secondRight);
    }

    public static void assertRightAligned(View first, View second, int margin) {
        int[] xy = new int[2];
        first.getLocationOnScreen(xy);
        int firstRight = xy[0] + first.getMeasuredWidth();
        second.getLocationOnScreen(xy);
        int secondRight = xy[0] + second.getMeasuredWidth();
        Assert.assertEquals("views are not right aligned", Math.abs(firstRight - secondRight), margin);
    }

    public static void assertLeftAligned(View first, View second) {
        int[] xy = new int[2];
        first.getLocationOnScreen(xy);
        int firstLeft = xy[0];
        second.getLocationOnScreen(xy);
        int secondLeft = xy[0];
        Assert.assertEquals("views are not left aligned", firstLeft, secondLeft);
    }

    public static void assertLeftAligned(View first, View second, int margin) {
        int[] xy = new int[2];
        first.getLocationOnScreen(xy);
        int firstLeft = xy[0];
        second.getLocationOnScreen(xy);
        int secondLeft = xy[0];
        Assert.assertEquals("views are not left aligned", Math.abs(firstLeft - secondLeft), margin);
    }

    public static void assertBottomAligned(View first, View second) {
        int[] xy = new int[2];
        first.getLocationOnScreen(xy);
        int firstBottom = xy[1] + first.getMeasuredHeight();
        second.getLocationOnScreen(xy);
        int secondBottom = xy[1] + second.getMeasuredHeight();
        Assert.assertEquals("views are not bottom aligned", firstBottom, secondBottom);
    }

    public static void assertBottomAligned(View first, View second, int margin) {
        int[] xy = new int[2];
        first.getLocationOnScreen(xy);
        int firstBottom = xy[1] + first.getMeasuredHeight();
        second.getLocationOnScreen(xy);
        int secondBottom = xy[1] + second.getMeasuredHeight();
        Assert.assertEquals("views are not bottom aligned", Math.abs(firstBottom - secondBottom), margin);
    }

    public static void assertTopAligned(View first, View second) {
        int[] xy = new int[2];
        first.getLocationOnScreen(xy);
        int firstTop = xy[1];
        second.getLocationOnScreen(xy);
        int secondTop = xy[1];
        Assert.assertEquals("views are not top aligned", firstTop, secondTop);
    }

    public static void assertTopAligned(View first, View second, int margin) {
        int[] xy = new int[2];
        first.getLocationOnScreen(xy);
        int firstTop = xy[1];
        second.getLocationOnScreen(xy);
        int secondTop = xy[1];
        Assert.assertEquals("views are not top aligned", Math.abs(firstTop - secondTop), margin);
    }

    public static void assertHorizontalCenterAligned(View reference, View test) {
        int[] xy = new int[2];
        reference.getLocationOnScreen(xy);
        int referenceLeft = xy[0];
        test.getLocationOnScreen(xy);
        int testLeft = xy[0];
        int center = (reference.getMeasuredWidth() - test.getMeasuredWidth()) / 2;
        int delta = testLeft - referenceLeft;
        Assert.assertEquals("views are not horizontally center aligned", center, delta);
    }

    public static void assertVerticalCenterAligned(View reference, View test) {
        int[] xy = new int[2];
        reference.getLocationOnScreen(xy);
        int referenceTop = xy[1];
        test.getLocationOnScreen(xy);
        int testTop = xy[1];
        int center = (reference.getMeasuredHeight() - test.getMeasuredHeight()) / 2;
        int delta = testTop - referenceTop;
        Assert.assertEquals("views are not vertically center aligned", center, delta);
    }

    public static void assertGroupIntegrity(ViewGroup parent) {
        int count = parent.getChildCount();
        Assert.assertTrue("child count should be >= 0", count >= 0);
        for (int i = 0; i < count; i++) {
            Assert.assertNotNull("group should not contain null children", parent.getChildAt(i));
            Assert.assertSame(parent, parent.getChildAt(i).getParent());
        }
    }

    public static void assertGroupContains(ViewGroup parent, View child) {
        int count = parent.getChildCount();
        Assert.assertTrue("Child count should be >= 0", count >= 0);
        boolean found = false;
        for (int i = 0; i < count; i++) {
            if (parent.getChildAt(i) == child) {
                if (found) {
                    Assert.assertTrue("child " + child + " is duplicated in parent", false);
                } else {
                    found = true;
                }
            }
        }
        Assert.assertTrue("group does not contain " + child, found);
    }

    public static void assertGroupNotContains(ViewGroup parent, View child) {
        int count = parent.getChildCount();
        Assert.assertTrue("Child count should be >= 0", count >= 0);
        for (int i = 0; i < count; i++) {
            if (parent.getChildAt(i) == child) {
                Assert.assertTrue("child " + child + " is found in parent", false);
            }
        }
    }
}
