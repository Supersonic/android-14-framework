package com.android.internal.policy;

import android.graphics.Point;
import android.graphics.Rect;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes4.dex */
public class TaskResizingAlgorithm {
    public static final int CTRL_BOTTOM = 8;
    public static final int CTRL_LEFT = 1;
    public static final int CTRL_NONE = 0;
    public static final int CTRL_RIGHT = 2;
    public static final int CTRL_TOP = 4;
    public static final float MIN_ASPECT = 1.2f;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface CtrlType {
    }

    public static Rect resizeDrag(float x, float y, float startDragX, float startDragY, Rect originalBounds, int ctrlType, int minVisibleWidth, int minVisibleHeight, Point maxVisibleSize, boolean preserveOrientation, boolean startOrientationWasLandscape) {
        int width1;
        int height1;
        int height2;
        int width2;
        int deltaX = Math.round(x - startDragX);
        int deltaY = Math.round(y - startDragY);
        int left = originalBounds.left;
        int top = originalBounds.top;
        int right = originalBounds.right;
        int bottom = originalBounds.bottom;
        int width = right - left;
        int height = bottom - top;
        if ((ctrlType & 1) != 0) {
            width = Math.max(minVisibleWidth, Math.min(width - deltaX, maxVisibleSize.f76x));
        } else if ((ctrlType & 2) != 0) {
            width = Math.max(minVisibleWidth, Math.min(width + deltaX, maxVisibleSize.f76x));
        }
        if ((ctrlType & 4) != 0) {
            height = Math.max(minVisibleHeight, Math.min(height - deltaY, maxVisibleSize.f77y));
        } else if ((ctrlType & 8) != 0) {
            height = Math.max(minVisibleHeight, Math.min(height + deltaY, maxVisibleSize.f77y));
        }
        float aspect = width / height;
        if (preserveOrientation && ((startOrientationWasLandscape && aspect < 1.2f) || (!startOrientationWasLandscape && aspect > 0.8333333002196431d))) {
            if (startOrientationWasLandscape) {
                width1 = Math.max(minVisibleWidth, Math.min(maxVisibleSize.f76x, width));
                height1 = Math.min(height, Math.round(width1 / 1.2f));
                if (height1 < minVisibleHeight) {
                    height1 = minVisibleHeight;
                    width1 = Math.max(minVisibleWidth, Math.min(maxVisibleSize.f76x, Math.round(height1 * 1.2f)));
                }
                height2 = Math.max(minVisibleHeight, Math.min(maxVisibleSize.f77y, height));
                width2 = Math.max(width, Math.round(height2 * 1.2f));
                if (width2 < minVisibleWidth) {
                    width2 = minVisibleWidth;
                    height2 = Math.max(minVisibleHeight, Math.min(maxVisibleSize.f77y, Math.round(width2 / 1.2f)));
                }
            } else {
                int width12 = Math.max(minVisibleWidth, Math.min(maxVisibleSize.f76x, width));
                int height12 = Math.max(height, Math.round(width12 * 1.2f));
                if (height12 >= minVisibleHeight) {
                    width1 = width12;
                    height1 = height12;
                } else {
                    width1 = Math.max(minVisibleWidth, Math.min(maxVisibleSize.f76x, Math.round(minVisibleHeight / 1.2f)));
                    height1 = minVisibleHeight;
                }
                height2 = Math.max(minVisibleHeight, Math.min(maxVisibleSize.f77y, height));
                width2 = Math.min(width, Math.round(height2 / 1.2f));
                if (width2 < minVisibleWidth) {
                    width2 = minVisibleWidth;
                    height2 = Math.max(minVisibleHeight, Math.min(maxVisibleSize.f77y, Math.round(width2 * 1.2f)));
                }
            }
            boolean grows = width > right - left || height > bottom - top;
            if (grows == (width1 * height1 > width2 * height2)) {
                width = width1;
                height = height1;
            } else {
                width = width2;
                height = height2;
            }
        }
        if ((ctrlType & 1) != 0) {
            left = right - width;
        } else {
            right = left + width;
        }
        if ((ctrlType & 4) != 0) {
            top = bottom - height;
        } else {
            bottom = top + height;
        }
        return new Rect(left, top, right, bottom);
    }
}
