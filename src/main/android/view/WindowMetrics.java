package android.view;

import android.graphics.Rect;
import java.util.function.Supplier;
/* loaded from: classes4.dex */
public final class WindowMetrics {
    private final Rect mBounds;
    private final float mDensity;
    private WindowInsets mWindowInsets;
    private Supplier<WindowInsets> mWindowInsetsSupplier;

    @Deprecated
    public WindowMetrics(Rect bounds, WindowInsets windowInsets) {
        this(bounds, windowInsets, 1.0f);
    }

    public WindowMetrics(Rect bounds, WindowInsets windowInsets, float density) {
        this.mBounds = bounds;
        this.mWindowInsets = windowInsets;
        this.mDensity = density;
    }

    public WindowMetrics(Rect bounds, Supplier<WindowInsets> windowInsetsSupplier, float density) {
        this.mBounds = bounds;
        this.mWindowInsetsSupplier = windowInsetsSupplier;
        this.mDensity = density;
    }

    public Rect getBounds() {
        return this.mBounds;
    }

    public WindowInsets getWindowInsets() {
        WindowInsets windowInsets = this.mWindowInsets;
        if (windowInsets != null) {
            return windowInsets;
        }
        WindowInsets windowInsets2 = this.mWindowInsetsSupplier.get();
        this.mWindowInsets = windowInsets2;
        return windowInsets2;
    }

    public float getDensity() {
        return this.mDensity;
    }

    public String toString() {
        return WindowMetrics.class.getSimpleName() + ":{bounds=" + this.mBounds + ", windowInsets=" + this.mWindowInsets + ", density" + this.mDensity + "}";
    }
}
