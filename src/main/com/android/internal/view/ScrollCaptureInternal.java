package com.android.internal.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.ScrollCaptureCallback;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
/* loaded from: classes2.dex */
public class ScrollCaptureInternal {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_VERBOSE = false;
    private static final int DOWN = 1;
    private static final String TAG = "ScrollCaptureInternal";
    public static final int TYPE_FIXED = 0;
    private static final int TYPE_OPAQUE = 3;
    public static final int TYPE_RECYCLING = 2;
    public static final int TYPE_SCROLLING = 1;

    /* renamed from: UP */
    private static final int f2189UP = -1;

    private static int detectScrollingType(View view) {
        if (view.canScrollVertically(1) || view.canScrollVertically(-1)) {
            if (view instanceof ViewGroup) {
                if (((ViewGroup) view).getChildCount() > 1) {
                    return 2;
                }
                if (((ViewGroup) view).getChildCount() < 1) {
                    return 3;
                }
                if (view.getScrollY() != 0) {
                    return 1;
                }
                Log.m106v(TAG, "hint: scrollY == 0");
                if (view.canScrollVertically(-1)) {
                    return 2;
                }
                view.scrollTo(view.getScrollX(), 1);
                if (view.getScrollY() == 1) {
                    view.scrollTo(view.getScrollX(), 0);
                    return 1;
                }
                return 2;
            }
            return 3;
        }
        return 0;
    }

    public ScrollCaptureCallback requestCallback(View view, Rect localVisibleRect, Point positionInWindow) {
        int i = detectScrollingType(view);
        switch (i) {
            case 1:
                return new ScrollCaptureViewSupport((ViewGroup) view, new ScrollViewCaptureHelper());
            case 2:
                if (view instanceof ListView) {
                    return new ScrollCaptureViewSupport((ListView) view, new ListViewCaptureHelper());
                }
                return new ScrollCaptureViewSupport((ViewGroup) view, new RecyclerViewCaptureHelper());
            case 3:
                if (view instanceof WebView) {
                    Log.m112d(TAG, "scroll capture: Using WebView support");
                    return new ScrollCaptureViewSupport((WebView) view, new WebViewCaptureHelper());
                }
                return null;
            default:
                return null;
        }
    }

    private static String formatIntToHexString(int value) {
        return "0x" + Integer.toHexString(value).toUpperCase();
    }

    static String resolveId(Context context, int id) {
        Resources resources = context.getResources();
        if (id >= 0) {
            try {
                String fieldValue = resources.getResourceTypeName(id) + '/' + resources.getResourceEntryName(id);
                return fieldValue;
            } catch (Resources.NotFoundException e) {
                String fieldValue2 = "id/" + formatIntToHexString(id);
                return fieldValue2;
            }
        }
        return "NO_ID";
    }
}
