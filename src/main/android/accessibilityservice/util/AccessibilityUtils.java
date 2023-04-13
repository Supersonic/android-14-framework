package android.accessibilityservice.util;

import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class AccessibilityUtils {
    private static final String ANCHOR_TAG = "a";
    private static final String IMG_PREFIX = "R.drawable.";
    private static final List<String> UNSUPPORTED_TAG_LIST = new ArrayList(Collections.singletonList("a"));

    private AccessibilityUtils() {
    }

    public static String getFilteredHtmlText(String text) {
        for (String tag : UNSUPPORTED_TAG_LIST) {
            String regexStart = "(?i)<" + tag + "(\\s+|>)";
            String regexEnd = "(?i)</" + tag + "\\s*>";
            text = Pattern.compile(regexEnd).matcher(Pattern.compile(regexStart).matcher(text).replaceAll("<invalidtag ")).replaceAll("</invalidtag>");
        }
        return Pattern.compile("(?i)<img\\s+(?!src\\s*=\\s*\"(?-i)R.drawable.)").matcher(text).replaceAll("<invalidtag ");
    }

    public static Drawable loadSafeAnimatedImage(Context context, ApplicationInfo applicationInfo, int resId) {
        if (resId == 0) {
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        String packageName = applicationInfo.packageName;
        Drawable bannerDrawable = packageManager.getDrawable(packageName, resId, applicationInfo);
        if (bannerDrawable == null) {
            return null;
        }
        boolean isImageWidthOverScreenLength = bannerDrawable.getIntrinsicWidth() > getScreenWidthPixels(context);
        boolean isImageHeightOverScreenLength = bannerDrawable.getIntrinsicHeight() > getScreenHeightPixels(context);
        if (isImageWidthOverScreenLength || isImageHeightOverScreenLength) {
            return null;
        }
        return bannerDrawable;
    }

    private static int getScreenWidthPixels(Context context) {
        Resources resources = context.getResources();
        int screenWidthDp = resources.getConfiguration().screenWidthDp;
        return Math.round(TypedValue.applyDimension(1, screenWidthDp, resources.getDisplayMetrics()));
    }

    private static int getScreenHeightPixels(Context context) {
        Resources resources = context.getResources();
        int screenHeightDp = resources.getConfiguration().screenHeightDp;
        return Math.round(TypedValue.applyDimension(1, screenHeightDp, resources.getDisplayMetrics()));
    }
}
