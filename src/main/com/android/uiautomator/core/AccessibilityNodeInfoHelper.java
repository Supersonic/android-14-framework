package com.android.uiautomator.core;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;
/* loaded from: classes.dex */
class AccessibilityNodeInfoHelper {
    AccessibilityNodeInfoHelper() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Rect getVisibleBoundsInScreen(AccessibilityNodeInfo node, int width, int height) {
        if (node == null) {
            return null;
        }
        Rect nodeRect = new Rect();
        node.getBoundsInScreen(nodeRect);
        Rect displayRect = new Rect();
        displayRect.top = 0;
        displayRect.left = 0;
        displayRect.right = width;
        displayRect.bottom = height;
        if (nodeRect.intersect(displayRect)) {
            return nodeRect;
        }
        return new Rect();
    }
}
