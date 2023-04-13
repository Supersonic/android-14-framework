package android.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.C4057R;
/* loaded from: classes3.dex */
public class Fade extends Visibility {
    private static boolean DBG = false;

    /* renamed from: IN */
    public static final int f465IN = 1;
    private static final String LOG_TAG = "Fade";
    public static final int OUT = 2;
    static final String PROPNAME_TRANSITION_ALPHA = "android:fade:transitionAlpha";

    public Fade() {
    }

    public Fade(int fadingMode) {
        setMode(fadingMode);
    }

    public Fade(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.Fade);
        int fadingMode = a.getInt(0, getMode());
        setMode(fadingMode);
        a.recycle();
    }

    @Override // android.transition.Visibility, android.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        transitionValues.values.put(PROPNAME_TRANSITION_ALPHA, Float.valueOf(transitionValues.view.getTransitionAlpha()));
    }

    private Animator createAnimation(final View view, float startAlpha, float endAlpha) {
        if (startAlpha == endAlpha) {
            return null;
        }
        view.setTransitionAlpha(startAlpha);
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "transitionAlpha", endAlpha);
        if (DBG) {
            Log.m112d(LOG_TAG, "Created animator " + anim);
        }
        FadeAnimatorListener listener = new FadeAnimatorListener(view);
        anim.addListener(listener);
        addListener(new TransitionListenerAdapter() { // from class: android.transition.Fade.1
            @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionEnd(Transition transition) {
                view.setTransitionAlpha(1.0f);
                transition.removeListener(this);
            }
        });
        return anim;
    }

    @Override // android.transition.Visibility
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        if (DBG) {
            View startView = startValues != null ? startValues.view : null;
            Log.m112d(LOG_TAG, "Fade.onAppear: startView, startVis, endView, endVis = " + startView + ", " + view);
        }
        float startAlpha = getStartAlpha(startValues, 0.0f);
        if (startAlpha == 1.0f) {
            startAlpha = 0.0f;
        }
        return createAnimation(view, startAlpha, 1.0f);
    }

    @Override // android.transition.Visibility
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        float startAlpha = getStartAlpha(startValues, 1.0f);
        return createAnimation(view, startAlpha, 0.0f);
    }

    private static float getStartAlpha(TransitionValues startValues, float fallbackValue) {
        Float startAlphaFloat;
        if (startValues == null || (startAlphaFloat = (Float) startValues.values.get(PROPNAME_TRANSITION_ALPHA)) == null) {
            return fallbackValue;
        }
        float startAlpha = startAlphaFloat.floatValue();
        return startAlpha;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class FadeAnimatorListener extends AnimatorListenerAdapter {
        private boolean mLayerTypeChanged = false;
        private final View mView;

        public FadeAnimatorListener(View view) {
            this.mView = view;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            if (this.mView.hasOverlappingRendering() && this.mView.getLayerType() == 0) {
                this.mLayerTypeChanged = true;
                this.mView.setLayerType(2, null);
            }
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            this.mView.setTransitionAlpha(1.0f);
            if (this.mLayerTypeChanged) {
                this.mView.setLayerType(0, null);
            }
        }
    }
}
