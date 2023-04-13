package android.window;

import android.graphics.Rect;
import android.view.WindowInsets;
import android.view.WindowMetrics;
/* loaded from: classes4.dex */
public final class WindowMetricsHelper {
    private WindowMetricsHelper() {
    }

    public static Rect getBoundsExcludingNavigationBarAndCutout(WindowMetrics windowMetrics) {
        WindowInsets windowInsets = windowMetrics.getWindowInsets();
        Rect result = new Rect(windowMetrics.getBounds());
        result.inset(windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars() | WindowInsets.Type.displayCutout()));
        return result;
    }
}
