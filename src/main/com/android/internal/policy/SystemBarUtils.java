package com.android.internal.policy;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Insets;
import android.util.RotationUtils;
import android.view.DisplayCutout;
import com.android.internal.C4057R;
/* loaded from: classes4.dex */
public final class SystemBarUtils {
    public static int getStatusBarHeight(Context context) {
        return getStatusBarHeight(context.getResources(), context.getDisplay().getCutout());
    }

    public static int getStatusBarHeight(Resources res, DisplayCutout cutout) {
        int defaultSize = res.getDimensionPixelSize(C4057R.dimen.status_bar_height_default);
        int safeInsetTop = cutout == null ? 0 : cutout.getSafeInsetTop();
        int waterfallInsetTop = cutout != null ? cutout.getWaterfallInsets().top : 0;
        return Math.max(safeInsetTop, defaultSize + waterfallInsetTop);
    }

    public static int getStatusBarHeightForRotation(Context context, int targetRot) {
        int rotation = context.getDisplay().getRotation();
        DisplayCutout cutout = context.getDisplay().getCutout();
        Insets insets = cutout == null ? Insets.NONE : Insets.m185of(cutout.getSafeInsets());
        Insets waterfallInsets = cutout == null ? Insets.NONE : cutout.getWaterfallInsets();
        if (rotation != targetRot) {
            if (!insets.equals(Insets.NONE)) {
                insets = RotationUtils.rotateInsets(insets, RotationUtils.deltaRotation(rotation, targetRot));
            }
            if (!waterfallInsets.equals(Insets.NONE)) {
                waterfallInsets = RotationUtils.rotateInsets(waterfallInsets, RotationUtils.deltaRotation(rotation, targetRot));
            }
        }
        int defaultSize = context.getResources().getDimensionPixelSize(C4057R.dimen.status_bar_height_default);
        return Math.max(insets.top, waterfallInsets.top + defaultSize);
    }

    public static int getQuickQsOffsetHeight(Context context) {
        int defaultSize = context.getResources().getDimensionPixelSize(C4057R.dimen.quick_qs_offset_height);
        int statusBarHeight = getStatusBarHeight(context);
        return Math.max(defaultSize, statusBarHeight);
    }
}
