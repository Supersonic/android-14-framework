package android.view;

import android.p008os.CancellationSignal;
import android.view.animation.Interpolator;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes4.dex */
public interface WindowInsetsController {
    public static final int APPEARANCE_LIGHT_NAVIGATION_BARS = 16;
    public static final int APPEARANCE_LIGHT_STATUS_BARS = 8;
    public static final int APPEARANCE_LOW_PROFILE_BARS = 4;
    public static final int APPEARANCE_OPAQUE_NAVIGATION_BARS = 2;
    public static final int APPEARANCE_OPAQUE_STATUS_BARS = 1;
    public static final int APPEARANCE_SEMI_TRANSPARENT_NAVIGATION_BARS = 64;
    public static final int APPEARANCE_SEMI_TRANSPARENT_STATUS_BARS = 32;
    public static final int BEHAVIOR_DEFAULT = 1;
    @Deprecated
    public static final int BEHAVIOR_SHOW_BARS_BY_SWIPE = 1;
    @Deprecated
    public static final int BEHAVIOR_SHOW_BARS_BY_TOUCH = 0;
    public static final int BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE = 2;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Appearance {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Behavior {
    }

    /* loaded from: classes4.dex */
    public interface OnControllableInsetsChangedListener {
        void onControllableInsetsChanged(WindowInsetsController windowInsetsController, int i);
    }

    void addOnControllableInsetsChangedListener(OnControllableInsetsChangedListener onControllableInsetsChangedListener);

    void controlWindowInsetsAnimation(int i, long j, Interpolator interpolator, CancellationSignal cancellationSignal, WindowInsetsAnimationControlListener windowInsetsAnimationControlListener);

    int getRequestedVisibleTypes();

    InsetsState getState();

    int getSystemBarsAppearance();

    int getSystemBarsBehavior();

    void hide(int i);

    void removeOnControllableInsetsChangedListener(OnControllableInsetsChangedListener onControllableInsetsChangedListener);

    void setAnimationsDisabled(boolean z);

    void setCaptionInsetsHeight(int i);

    void setSystemBarsAppearance(int i, int i2);

    void setSystemBarsBehavior(int i);

    void setSystemDrivenInsetsAnimationLoggingListener(WindowInsetsAnimationControlListener windowInsetsAnimationControlListener);

    void show(int i);
}
