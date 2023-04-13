package android.view;

import android.animation.Animator;
import android.animation.RevealAnimator;
/* loaded from: classes4.dex */
public final class ViewAnimationUtils {
    private ViewAnimationUtils() {
    }

    public static Animator createCircularReveal(View view, int centerX, int centerY, float startRadius, float endRadius) {
        return new RevealAnimator(view, centerX, centerY, startRadius, endRadius);
    }
}
