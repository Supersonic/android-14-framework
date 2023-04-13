package android.transition;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import com.android.internal.C4057R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public class Slide extends Visibility {
    private static final String PROPNAME_SCREEN_POSITION = "android:slide:screenPosition";
    private static final String TAG = "Slide";
    private CalculateSlide mSlideCalculator;
    private int mSlideEdge;
    private float mSlideFraction;
    private static final TimeInterpolator sDecelerate = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerate = new AccelerateInterpolator();
    private static final CalculateSlide sCalculateLeft = new CalculateSlideHorizontal() { // from class: android.transition.Slide.1
        @Override // android.transition.Slide.CalculateSlide
        public float getGoneX(ViewGroup sceneRoot, View view, float fraction) {
            return view.getTranslationX() - (sceneRoot.getWidth() * fraction);
        }
    };
    private static final CalculateSlide sCalculateStart = new CalculateSlideHorizontal() { // from class: android.transition.Slide.2
        @Override // android.transition.Slide.CalculateSlide
        public float getGoneX(ViewGroup sceneRoot, View view, float fraction) {
            boolean isRtl = sceneRoot.getLayoutDirection() == 1;
            if (isRtl) {
                float x = view.getTranslationX() + (sceneRoot.getWidth() * fraction);
                return x;
            }
            float x2 = view.getTranslationX();
            return x2 - (sceneRoot.getWidth() * fraction);
        }
    };
    private static final CalculateSlide sCalculateTop = new CalculateSlideVertical() { // from class: android.transition.Slide.3
        @Override // android.transition.Slide.CalculateSlide
        public float getGoneY(ViewGroup sceneRoot, View view, float fraction) {
            return view.getTranslationY() - (sceneRoot.getHeight() * fraction);
        }
    };
    private static final CalculateSlide sCalculateRight = new CalculateSlideHorizontal() { // from class: android.transition.Slide.4
        @Override // android.transition.Slide.CalculateSlide
        public float getGoneX(ViewGroup sceneRoot, View view, float fraction) {
            return view.getTranslationX() + (sceneRoot.getWidth() * fraction);
        }
    };
    private static final CalculateSlide sCalculateEnd = new CalculateSlideHorizontal() { // from class: android.transition.Slide.5
        @Override // android.transition.Slide.CalculateSlide
        public float getGoneX(ViewGroup sceneRoot, View view, float fraction) {
            boolean isRtl = sceneRoot.getLayoutDirection() == 1;
            if (isRtl) {
                float x = view.getTranslationX() - (sceneRoot.getWidth() * fraction);
                return x;
            }
            float x2 = view.getTranslationX();
            return x2 + (sceneRoot.getWidth() * fraction);
        }
    };
    private static final CalculateSlide sCalculateBottom = new CalculateSlideVertical() { // from class: android.transition.Slide.6
        @Override // android.transition.Slide.CalculateSlide
        public float getGoneY(ViewGroup sceneRoot, View view, float fraction) {
            return view.getTranslationY() + (sceneRoot.getHeight() * fraction);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public interface CalculateSlide {
        float getGoneX(ViewGroup viewGroup, View view, float f);

        float getGoneY(ViewGroup viewGroup, View view, float f);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface GravityFlag {
    }

    /* loaded from: classes3.dex */
    private static abstract class CalculateSlideHorizontal implements CalculateSlide {
        private CalculateSlideHorizontal() {
        }

        @Override // android.transition.Slide.CalculateSlide
        public float getGoneY(ViewGroup sceneRoot, View view, float fraction) {
            return view.getTranslationY();
        }
    }

    /* loaded from: classes3.dex */
    private static abstract class CalculateSlideVertical implements CalculateSlide {
        private CalculateSlideVertical() {
        }

        @Override // android.transition.Slide.CalculateSlide
        public float getGoneX(ViewGroup sceneRoot, View view, float fraction) {
            return view.getTranslationX();
        }
    }

    public Slide() {
        this.mSlideCalculator = sCalculateBottom;
        this.mSlideEdge = 80;
        this.mSlideFraction = 1.0f;
        setSlideEdge(80);
    }

    public Slide(int slideEdge) {
        this.mSlideCalculator = sCalculateBottom;
        this.mSlideEdge = 80;
        this.mSlideFraction = 1.0f;
        setSlideEdge(slideEdge);
    }

    public Slide(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSlideCalculator = sCalculateBottom;
        this.mSlideEdge = 80;
        this.mSlideFraction = 1.0f;
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.Slide);
        int edge = a.getInt(0, 80);
        a.recycle();
        setSlideEdge(edge);
    }

    private void captureValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        int[] position = new int[2];
        view.getLocationOnScreen(position);
        transitionValues.values.put(PROPNAME_SCREEN_POSITION, position);
    }

    @Override // android.transition.Visibility, android.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        captureValues(transitionValues);
    }

    @Override // android.transition.Visibility, android.transition.Transition
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        captureValues(transitionValues);
    }

    public void setSlideEdge(int slideEdge) {
        switch (slideEdge) {
            case 3:
                this.mSlideCalculator = sCalculateLeft;
                break;
            case 5:
                this.mSlideCalculator = sCalculateRight;
                break;
            case 48:
                this.mSlideCalculator = sCalculateTop;
                break;
            case 80:
                this.mSlideCalculator = sCalculateBottom;
                break;
            case Gravity.START /* 8388611 */:
                this.mSlideCalculator = sCalculateStart;
                break;
            case Gravity.END /* 8388613 */:
                this.mSlideCalculator = sCalculateEnd;
                break;
            default:
                throw new IllegalArgumentException("Invalid slide direction");
        }
        this.mSlideEdge = slideEdge;
        SidePropagation propagation = new SidePropagation();
        propagation.setSide(slideEdge);
        setPropagation(propagation);
    }

    public int getSlideEdge() {
        return this.mSlideEdge;
    }

    @Override // android.transition.Visibility
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        if (endValues == null) {
            return null;
        }
        int[] position = (int[]) endValues.values.get(PROPNAME_SCREEN_POSITION);
        float endX = view.getTranslationX();
        float endY = view.getTranslationY();
        float startX = this.mSlideCalculator.getGoneX(sceneRoot, view, this.mSlideFraction);
        float startY = this.mSlideCalculator.getGoneY(sceneRoot, view, this.mSlideFraction);
        return TranslationAnimationCreator.createAnimation(view, endValues, position[0], position[1], startX, startY, endX, endY, sDecelerate, this);
    }

    @Override // android.transition.Visibility
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null) {
            return null;
        }
        int[] position = (int[]) startValues.values.get(PROPNAME_SCREEN_POSITION);
        float startX = view.getTranslationX();
        float startY = view.getTranslationY();
        float endX = this.mSlideCalculator.getGoneX(sceneRoot, view, this.mSlideFraction);
        float endY = this.mSlideCalculator.getGoneY(sceneRoot, view, this.mSlideFraction);
        return TranslationAnimationCreator.createAnimation(view, startValues, position[0], position[1], startX, startY, endX, endY, sAccelerate, this);
    }

    public void setSlideFraction(float slideFraction) {
        this.mSlideFraction = slideFraction;
    }
}
