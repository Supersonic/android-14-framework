package com.android.internal.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import com.android.internal.util.ContrastColorUtil;
/* loaded from: classes5.dex */
final class ColoredIconHelper {
    static final int COLOR_INVALID = 1;

    private ColoredIconHelper() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void applyGrayTint(Context ctx, Drawable drawable, boolean apply, int originalColor) {
        if (originalColor == 1) {
            return;
        }
        if (apply) {
            Configuration config = ctx.getResources().getConfiguration();
            boolean inNightMode = (config.uiMode & 48) == 32;
            int grey = ContrastColorUtil.resolveColor(ctx, 0, inNightMode);
            drawable.mutate().setColorFilter(grey, PorterDuff.Mode.SRC_ATOP);
            return;
        }
        drawable.mutate().setColorFilter(originalColor, PorterDuff.Mode.SRC_ATOP);
    }
}
