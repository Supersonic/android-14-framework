package android.inputmethodservice.navigationbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import java.util.ArrayList;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class ButtonDispatcher {
    private static final int FADE_DURATION_IN = 150;
    private static final int FADE_DURATION_OUT = 250;
    public static final Interpolator LINEAR = new LinearInterpolator();
    private View.AccessibilityDelegate mAccessibilityDelegate;
    private View.OnClickListener mClickListener;
    private View mCurrentView;
    private Float mDarkIntensity;
    private Boolean mDelayTouchFeedback;
    private ValueAnimator mFadeAnimator;
    private final int mId;
    private KeyButtonDrawable mImageDrawable;
    private View.OnLongClickListener mLongClickListener;
    private Boolean mLongClickable;
    private View.OnHoverListener mOnHoverListener;
    private View.OnTouchListener mTouchListener;
    private final ArrayList<View> mViews = new ArrayList<>();
    private float mAlpha = 1.0f;
    private int mVisibility = 0;
    private final ValueAnimator.AnimatorUpdateListener mAlphaListener = new ValueAnimator.AnimatorUpdateListener() { // from class: android.inputmethodservice.navigationbar.ButtonDispatcher$$ExternalSyntheticLambda0
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            ButtonDispatcher.this.lambda$new$0(valueAnimator);
        }
    };
    private final AnimatorListenerAdapter mFadeListener = new AnimatorListenerAdapter() { // from class: android.inputmethodservice.navigationbar.ButtonDispatcher.1
        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            ButtonDispatcher.this.mFadeAnimator = null;
            ButtonDispatcher buttonDispatcher = ButtonDispatcher.this;
            buttonDispatcher.setVisibility(buttonDispatcher.getAlpha() == 1.0f ? 0 : 4);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ValueAnimator animation) {
        setAlpha(((Float) animation.getAnimatedValue()).floatValue(), false, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ButtonDispatcher(int id) {
        this.mId = id;
    }

    public void clear() {
        this.mViews.clear();
    }

    public void addView(View view) {
        this.mViews.add(view);
        view.setOnClickListener(this.mClickListener);
        view.setOnTouchListener(this.mTouchListener);
        view.setOnLongClickListener(this.mLongClickListener);
        view.setOnHoverListener(this.mOnHoverListener);
        Boolean bool = this.mLongClickable;
        if (bool != null) {
            view.setLongClickable(bool.booleanValue());
        }
        view.setAlpha(this.mAlpha);
        view.setVisibility(this.mVisibility);
        View.AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            view.setAccessibilityDelegate(accessibilityDelegate);
        }
        if (view instanceof ButtonInterface) {
            ButtonInterface button = (ButtonInterface) view;
            Float f = this.mDarkIntensity;
            if (f != null) {
                button.setDarkIntensity(f.floatValue());
            }
            KeyButtonDrawable keyButtonDrawable = this.mImageDrawable;
            if (keyButtonDrawable != null) {
                button.setImageDrawable(keyButtonDrawable);
            }
            Boolean bool2 = this.mDelayTouchFeedback;
            if (bool2 != null) {
                button.setDelayTouchFeedback(bool2.booleanValue());
            }
        }
    }

    public int getId() {
        return this.mId;
    }

    public int getVisibility() {
        return this.mVisibility;
    }

    public boolean isVisible() {
        return getVisibility() == 0;
    }

    public float getAlpha() {
        return this.mAlpha;
    }

    public KeyButtonDrawable getImageDrawable() {
        return this.mImageDrawable;
    }

    public void setImageDrawable(KeyButtonDrawable drawable) {
        this.mImageDrawable = drawable;
        int numViews = this.mViews.size();
        for (int i = 0; i < numViews; i++) {
            if (this.mViews.get(i) instanceof ButtonInterface) {
                ((ButtonInterface) this.mViews.get(i)).setImageDrawable(this.mImageDrawable);
            }
        }
        KeyButtonDrawable keyButtonDrawable = this.mImageDrawable;
        if (keyButtonDrawable != null) {
            keyButtonDrawable.setCallback(this.mCurrentView);
        }
    }

    public void setVisibility(int visibility) {
        if (this.mVisibility == visibility) {
            return;
        }
        ValueAnimator valueAnimator = this.mFadeAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mVisibility = visibility;
        int numViews = this.mViews.size();
        for (int i = 0; i < numViews; i++) {
            this.mViews.get(i).setVisibility(this.mVisibility);
        }
    }

    public void setAlpha(float alpha) {
        setAlpha(alpha, false);
    }

    public void setAlpha(float alpha, boolean animate) {
        setAlpha(alpha, animate, true);
    }

    public void setAlpha(float alpha, boolean animate, long duration) {
        setAlpha(alpha, animate, duration, true);
    }

    public void setAlpha(float alpha, boolean animate, boolean cancelAnimator) {
        setAlpha(alpha, animate, getAlpha() < alpha ? 150L : 250L, cancelAnimator);
    }

    public void setAlpha(float alpha, boolean animate, long duration, boolean cancelAnimator) {
        ValueAnimator valueAnimator = this.mFadeAnimator;
        if (valueAnimator != null && (cancelAnimator || animate)) {
            valueAnimator.cancel();
        }
        if (animate) {
            setVisibility(0);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(getAlpha(), alpha);
            this.mFadeAnimator = ofFloat;
            ofFloat.setDuration(duration);
            this.mFadeAnimator.setInterpolator(LINEAR);
            this.mFadeAnimator.addListener(this.mFadeListener);
            this.mFadeAnimator.addUpdateListener(this.mAlphaListener);
            this.mFadeAnimator.start();
            return;
        }
        int prevAlpha = (int) (getAlpha() * 255.0f);
        int nextAlpha = (int) (alpha * 255.0f);
        if (prevAlpha != nextAlpha) {
            this.mAlpha = nextAlpha / 255.0f;
            int numViews = this.mViews.size();
            for (int i = 0; i < numViews; i++) {
                this.mViews.get(i).setAlpha(this.mAlpha);
            }
        }
    }

    public void setDarkIntensity(float darkIntensity) {
        this.mDarkIntensity = Float.valueOf(darkIntensity);
        int numViews = this.mViews.size();
        for (int i = 0; i < numViews; i++) {
            if (this.mViews.get(i) instanceof ButtonInterface) {
                ((ButtonInterface) this.mViews.get(i)).setDarkIntensity(darkIntensity);
            }
        }
    }

    public void setDelayTouchFeedback(boolean delay) {
        this.mDelayTouchFeedback = Boolean.valueOf(delay);
        int numViews = this.mViews.size();
        for (int i = 0; i < numViews; i++) {
            if (this.mViews.get(i) instanceof ButtonInterface) {
                ((ButtonInterface) this.mViews.get(i)).setDelayTouchFeedback(delay);
            }
        }
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        this.mClickListener = clickListener;
        int numViews = this.mViews.size();
        for (int i = 0; i < numViews; i++) {
            this.mViews.get(i).setOnClickListener(this.mClickListener);
        }
    }

    public void setOnTouchListener(View.OnTouchListener touchListener) {
        this.mTouchListener = touchListener;
        int numViews = this.mViews.size();
        for (int i = 0; i < numViews; i++) {
            this.mViews.get(i).setOnTouchListener(this.mTouchListener);
        }
    }

    public void setLongClickable(boolean isLongClickable) {
        this.mLongClickable = Boolean.valueOf(isLongClickable);
        int numViews = this.mViews.size();
        for (int i = 0; i < numViews; i++) {
            this.mViews.get(i).setLongClickable(this.mLongClickable.booleanValue());
        }
    }

    public void setOnLongClickListener(View.OnLongClickListener longClickListener) {
        this.mLongClickListener = longClickListener;
        int numViews = this.mViews.size();
        for (int i = 0; i < numViews; i++) {
            this.mViews.get(i).setOnLongClickListener(this.mLongClickListener);
        }
    }

    public void setOnHoverListener(View.OnHoverListener hoverListener) {
        this.mOnHoverListener = hoverListener;
        int numViews = this.mViews.size();
        for (int i = 0; i < numViews; i++) {
            this.mViews.get(i).setOnHoverListener(this.mOnHoverListener);
        }
    }

    public void setAccessibilityDelegate(View.AccessibilityDelegate delegate) {
        this.mAccessibilityDelegate = delegate;
        int numViews = this.mViews.size();
        for (int i = 0; i < numViews; i++) {
            this.mViews.get(i).setAccessibilityDelegate(delegate);
        }
    }

    public void setTranslation(int x, int y, int z) {
        int numViews = this.mViews.size();
        for (int i = 0; i < numViews; i++) {
            View view = this.mViews.get(i);
            view.setTranslationX(x);
            view.setTranslationY(y);
            view.setTranslationZ(z);
        }
    }

    public ArrayList<View> getViews() {
        return this.mViews;
    }

    public View getCurrentView() {
        return this.mCurrentView;
    }

    public void setCurrentView(View currentView) {
        View findViewById = currentView.findViewById(this.mId);
        this.mCurrentView = findViewById;
        KeyButtonDrawable keyButtonDrawable = this.mImageDrawable;
        if (keyButtonDrawable != null) {
            keyButtonDrawable.setCallback(findViewById);
        }
        View view = this.mCurrentView;
        if (view != null) {
            view.setTranslationX(0.0f);
            this.mCurrentView.setTranslationY(0.0f);
            this.mCurrentView.setTranslationZ(0.0f);
        }
    }

    public void onDestroy() {
    }
}
