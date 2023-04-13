package com.android.internal.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import com.android.internal.C4057R;
/* loaded from: classes3.dex */
public class UserIcons {
    private static final int[] USER_ICON_COLORS = {C4057R.color.user_icon_1, C4057R.color.user_icon_2, C4057R.color.user_icon_3, C4057R.color.user_icon_4, C4057R.color.user_icon_5, C4057R.color.user_icon_6, C4057R.color.user_icon_7, C4057R.color.user_icon_8};

    public static Bitmap convertToBitmap(Drawable icon) {
        return convertToBitmapAtSize(icon, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
    }

    public static Bitmap convertToBitmapAtUserIconSize(Resources res, Drawable icon) {
        int size = res.getDimensionPixelSize(C4057R.dimen.user_icon_size);
        return convertToBitmapAtSize(icon, size, size);
    }

    private static Bitmap convertToBitmapAtSize(Drawable icon, int width, int height) {
        if (icon == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        icon.setBounds(0, 0, width, height);
        icon.draw(canvas);
        return bitmap;
    }

    public static Drawable getDefaultUserIcon(Resources resources, int userId, boolean light) {
        int colorResId = light ? C4057R.color.user_icon_default_white : C4057R.color.user_icon_default_gray;
        if (userId != -10000) {
            int[] iArr = USER_ICON_COLORS;
            colorResId = iArr[userId % iArr.length];
        }
        return getDefaultUserIconInColor(resources, resources.getColor(colorResId, null));
    }

    public static Drawable getDefaultUserIconInColor(Resources resources, int color) {
        Drawable icon = resources.getDrawable(C4057R.C4058drawable.ic_account_circle, null).mutate();
        icon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        return icon;
    }

    public static int[] getUserIconColors(Resources resources) {
        int[] result = new int[USER_ICON_COLORS.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = resources.getColor(USER_ICON_COLORS[i], null);
        }
        return result;
    }
}
