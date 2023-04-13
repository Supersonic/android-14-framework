package android.view;

import android.graphics.Insets;
/* loaded from: classes4.dex */
public interface WindowInsetsAnimationController {
    void finish(boolean z);

    float getCurrentAlpha();

    float getCurrentFraction();

    Insets getCurrentInsets();

    Insets getHiddenStateInsets();

    Insets getShownStateInsets();

    int getTypes();

    boolean hasZeroInsetsIme();

    boolean isCancelled();

    boolean isFinished();

    void setInsetsAndAlpha(Insets insets, float f, float f2);

    default boolean isReady() {
        return (isFinished() || isCancelled()) ? false : true;
    }
}
