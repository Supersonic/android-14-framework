package com.android.server.p014wm.utils;

import android.graphics.Rect;
/* renamed from: com.android.server.wm.utils.InsetUtils */
/* loaded from: classes2.dex */
public class InsetUtils {
    public static void rotateInsets(Rect rect, int i) {
        if (i != 0) {
            if (i == 1) {
                rect.set(rect.top, rect.right, rect.bottom, rect.left);
            } else if (i == 2) {
                rect.set(rect.right, rect.bottom, rect.left, rect.top);
            } else if (i == 3) {
                rect.set(rect.bottom, rect.left, rect.top, rect.right);
            } else {
                throw new IllegalArgumentException("Unknown rotation: " + i);
            }
        }
    }

    public static void addInsets(Rect rect, Rect rect2) {
        rect.left += rect2.left;
        rect.top += rect2.top;
        rect.right += rect2.right;
        rect.bottom += rect2.bottom;
    }
}
