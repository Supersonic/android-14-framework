package android.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.transition.Transition;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Map;
/* loaded from: classes3.dex */
public class ChangeText extends Transition {
    public static final int CHANGE_BEHAVIOR_IN = 2;
    public static final int CHANGE_BEHAVIOR_KEEP = 0;
    public static final int CHANGE_BEHAVIOR_OUT = 1;
    public static final int CHANGE_BEHAVIOR_OUT_IN = 3;
    private static final String LOG_TAG = "TextChange";
    private static final String PROPNAME_TEXT_COLOR = "android:textchange:textColor";
    private int mChangeBehavior = 0;
    private static final String PROPNAME_TEXT = "android:textchange:text";
    private static final String PROPNAME_TEXT_SELECTION_START = "android:textchange:textSelectionStart";
    private static final String PROPNAME_TEXT_SELECTION_END = "android:textchange:textSelectionEnd";
    private static final String[] sTransitionProperties = {PROPNAME_TEXT, PROPNAME_TEXT_SELECTION_START, PROPNAME_TEXT_SELECTION_END};

    public ChangeText setChangeBehavior(int changeBehavior) {
        if (changeBehavior >= 0 && changeBehavior <= 3) {
            this.mChangeBehavior = changeBehavior;
        }
        return this;
    }

    @Override // android.transition.Transition
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    public int getChangeBehavior() {
        return this.mChangeBehavior;
    }

    private void captureValues(TransitionValues transitionValues) {
        if (transitionValues.view instanceof TextView) {
            TextView textview = (TextView) transitionValues.view;
            transitionValues.values.put(PROPNAME_TEXT, textview.getText());
            if (textview instanceof EditText) {
                transitionValues.values.put(PROPNAME_TEXT_SELECTION_START, Integer.valueOf(textview.getSelectionStart()));
                transitionValues.values.put(PROPNAME_TEXT_SELECTION_END, Integer.valueOf(textview.getSelectionEnd()));
            }
            if (this.mChangeBehavior > 0) {
                transitionValues.values.put(PROPNAME_TEXT_COLOR, Integer.valueOf(textview.getCurrentTextColor()));
            }
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

    /* JADX WARN: Removed duplicated region for block: B:66:0x0198  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x019d  */
    @Override // android.transition.Transition
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        int endSelectionEnd;
        int startSelectionStart;
        int endSelectionStart;
        int startSelectionEnd;
        int startSelectionEnd2;
        int startSelectionStart2;
        int i;
        char c;
        int endColor;
        int i2;
        char c2;
        final int endColor2;
        Animator anim;
        int endColor3;
        if (startValues == null || endValues == null || !(startValues.view instanceof TextView) || !(endValues.view instanceof TextView)) {
            return null;
        }
        final TextView view = (TextView) endValues.view;
        Map<String, Object> startVals = startValues.values;
        Map<String, Object> endVals = endValues.values;
        final CharSequence startText = startVals.get(PROPNAME_TEXT) != null ? (CharSequence) startVals.get(PROPNAME_TEXT) : "";
        final CharSequence endText = endVals.get(PROPNAME_TEXT) != null ? (CharSequence) endVals.get(PROPNAME_TEXT) : "";
        if (view instanceof EditText) {
            int startSelectionStart3 = startVals.get(PROPNAME_TEXT_SELECTION_START) != null ? ((Integer) startVals.get(PROPNAME_TEXT_SELECTION_START)).intValue() : -1;
            int startSelectionEnd3 = startVals.get(PROPNAME_TEXT_SELECTION_END) != null ? ((Integer) startVals.get(PROPNAME_TEXT_SELECTION_END)).intValue() : startSelectionStart3;
            int endSelectionStart2 = endVals.get(PROPNAME_TEXT_SELECTION_START) != null ? ((Integer) endVals.get(PROPNAME_TEXT_SELECTION_START)).intValue() : -1;
            endSelectionStart = endSelectionStart2;
            endSelectionEnd = endVals.get(PROPNAME_TEXT_SELECTION_END) != null ? ((Integer) endVals.get(PROPNAME_TEXT_SELECTION_END)).intValue() : endSelectionStart2;
            startSelectionStart = startSelectionStart3;
            startSelectionEnd = startSelectionEnd3;
        } else {
            endSelectionEnd = -1;
            startSelectionStart = -1;
            endSelectionStart = -1;
            startSelectionEnd = -1;
        }
        if (startText.equals(endText)) {
            return null;
        }
        if (this.mChangeBehavior != 2) {
            view.setText(startText);
            if (view instanceof EditText) {
                setSelection((EditText) view, startSelectionStart, startSelectionEnd);
            }
        }
        if (this.mChangeBehavior == 0) {
            endColor3 = 0;
            Animator anim2 = ValueAnimator.ofFloat(0.0f, 1.0f);
            startSelectionStart2 = startSelectionStart;
            anim = anim2;
            final int i3 = endSelectionStart;
            startSelectionEnd2 = startSelectionEnd;
            final int startSelectionEnd4 = endSelectionEnd;
            anim.addListener(new AnimatorListenerAdapter() { // from class: android.transition.ChangeText.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (startText.equals(view.getText())) {
                        view.setText(endText);
                        TextView textView = view;
                        if (textView instanceof EditText) {
                            ChangeText.this.setSelection((EditText) textView, i3, startSelectionEnd4);
                        }
                    }
                }
            });
        } else {
            startSelectionEnd2 = startSelectionEnd;
            startSelectionStart2 = startSelectionStart;
            final int startColor = ((Integer) startVals.get(PROPNAME_TEXT_COLOR)).intValue();
            final int endColor4 = ((Integer) endVals.get(PROPNAME_TEXT_COLOR)).intValue();
            Animator outAnim = null;
            Animator inAnim = null;
            int i4 = this.mChangeBehavior;
            if (i4 == 3 || i4 == 1) {
                ValueAnimator outAnim2 = ValueAnimator.ofInt(Color.alpha(startColor), 0);
                outAnim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.transition.ChangeText.2
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int currAlpha = ((Integer) animation.getAnimatedValue()).intValue();
                        view.setTextColor((currAlpha << 24) | (startColor & 16777215));
                    }
                });
                i = 3;
                final int i5 = endSelectionStart;
                c = 1;
                final int i6 = endSelectionEnd;
                endColor = endColor4;
                outAnim2.addListener(new AnimatorListenerAdapter() { // from class: android.transition.ChangeText.3
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (startText.equals(view.getText())) {
                            view.setText(endText);
                            TextView textView = view;
                            if (textView instanceof EditText) {
                                ChangeText.this.setSelection((EditText) textView, i5, i6);
                            }
                        }
                        view.setTextColor(endColor4);
                    }
                });
                outAnim = outAnim2;
            } else {
                c = 1;
                endColor = endColor4;
                i = 3;
            }
            int i7 = this.mChangeBehavior;
            if (i7 != i) {
                i2 = 2;
                if (i7 != 2) {
                    endColor2 = endColor;
                    c2 = 0;
                    if (outAnim == null && inAnim != null) {
                        Animator anim3 = new AnimatorSet();
                        Animator[] animatorArr = new Animator[i2];
                        animatorArr[c2] = outAnim;
                        animatorArr[c] = inAnim;
                        ((AnimatorSet) anim3).playSequentially(animatorArr);
                        anim = anim3;
                        endColor3 = endColor2;
                    } else if (outAnim == null) {
                        anim = outAnim;
                        endColor3 = endColor2;
                    } else {
                        Animator anim4 = inAnim;
                        anim = anim4;
                        endColor3 = endColor2;
                    }
                }
            } else {
                i2 = 2;
            }
            c2 = 0;
            ValueAnimator inAnim2 = ValueAnimator.ofInt(0, Color.alpha(endColor));
            endColor2 = endColor;
            inAnim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.transition.ChangeText.4
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currAlpha = ((Integer) animation.getAnimatedValue()).intValue();
                    view.setTextColor((currAlpha << 24) | (endColor2 & 16777215));
                }
            });
            inAnim2.addListener(new AnimatorListenerAdapter() { // from class: android.transition.ChangeText.5
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    view.setTextColor(endColor2);
                }
            });
            inAnim = inAnim2;
            if (outAnim == null) {
            }
            if (outAnim == null) {
            }
        }
        final int i8 = endSelectionStart;
        final int i9 = endSelectionEnd;
        final int i10 = endColor3;
        final int i11 = startSelectionStart2;
        final int i12 = startSelectionEnd2;
        Transition.TransitionListener transitionListener = new TransitionListenerAdapter() { // from class: android.transition.ChangeText.6
            int mPausedColor = 0;

            @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionPause(Transition transition) {
                if (ChangeText.this.mChangeBehavior != 2) {
                    view.setText(endText);
                    TextView textView = view;
                    if (textView instanceof EditText) {
                        ChangeText.this.setSelection((EditText) textView, i8, i9);
                    }
                }
                if (ChangeText.this.mChangeBehavior > 0) {
                    this.mPausedColor = view.getCurrentTextColor();
                    view.setTextColor(i10);
                }
            }

            @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionResume(Transition transition) {
                if (ChangeText.this.mChangeBehavior != 2) {
                    view.setText(startText);
                    TextView textView = view;
                    if (textView instanceof EditText) {
                        ChangeText.this.setSelection((EditText) textView, i11, i12);
                    }
                }
                if (ChangeText.this.mChangeBehavior > 0) {
                    view.setTextColor(this.mPausedColor);
                }
            }

            @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
            }
        };
        addListener(transitionListener);
        return anim;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSelection(EditText editText, int start, int end) {
        if (start >= 0 && end >= 0) {
            editText.setSelection(start, end);
        }
    }
}
