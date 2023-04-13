package android.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
/* loaded from: classes3.dex */
public class Recolor extends Transition {
    private static final String PROPNAME_BACKGROUND = "android:recolor:background";
    private static final String PROPNAME_TEXT_COLOR = "android:recolor:textColor";

    public Recolor() {
    }

    public Recolor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void captureValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_BACKGROUND, transitionValues.view.getBackground());
        if (transitionValues.view instanceof TextView) {
            transitionValues.values.put(PROPNAME_TEXT_COLOR, Integer.valueOf(((TextView) transitionValues.view).getCurrentTextColor()));
        }
    }

    @Override // android.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // android.transition.Transition
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // android.transition.Transition
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        View view = endValues.view;
        Drawable startBackground = (Drawable) startValues.values.get(PROPNAME_BACKGROUND);
        Drawable endBackground = (Drawable) endValues.values.get(PROPNAME_BACKGROUND);
        if ((startBackground instanceof ColorDrawable) && (endBackground instanceof ColorDrawable)) {
            ColorDrawable startColor = (ColorDrawable) startBackground;
            ColorDrawable endColor = (ColorDrawable) endBackground;
            if (startColor.getColor() != endColor.getColor()) {
                endColor.setColor(startColor.getColor());
                return ObjectAnimator.ofArgb(endBackground, "color", startColor.getColor(), endColor.getColor());
            }
        }
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            int start = ((Integer) startValues.values.get(PROPNAME_TEXT_COLOR)).intValue();
            int end = ((Integer) endValues.values.get(PROPNAME_TEXT_COLOR)).intValue();
            if (start != end) {
                textView.setTextColor(end);
                return ObjectAnimator.ofArgb(textView, "textColor", start, end);
            }
        }
        return null;
    }
}
