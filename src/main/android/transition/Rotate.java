package android.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
/* loaded from: classes3.dex */
public class Rotate extends Transition {
    private static final String PROPNAME_ROTATION = "android:rotate:rotation";

    @Override // android.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_ROTATION, Float.valueOf(transitionValues.view.getRotation()));
    }

    @Override // android.transition.Transition
    public void captureEndValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_ROTATION, Float.valueOf(transitionValues.view.getRotation()));
    }

    @Override // android.transition.Transition
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        View view = endValues.view;
        float startRotation = ((Float) startValues.values.get(PROPNAME_ROTATION)).floatValue();
        float endRotation = ((Float) endValues.values.get(PROPNAME_ROTATION)).floatValue();
        if (startRotation == endRotation) {
            return null;
        }
        view.setRotation(startRotation);
        return ObjectAnimator.ofFloat(view, View.ROTATION, startRotation, endRotation);
    }
}
