package android.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.method.TransformationMethod;
import android.text.method.TranslationTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.translation.UiTranslationManager;
import android.view.translation.ViewTranslationCallback;
import android.view.translation.ViewTranslationRequest;
import android.view.translation.ViewTranslationResponse;
import java.lang.ref.WeakReference;
/* loaded from: classes4.dex */
public class TextViewTranslationCallback implements ViewTranslationCallback {
    private static final char COMPAT_PAD_CHARACTER = 8194;
    private static final boolean DEBUG = Log.isLoggable(UiTranslationManager.LOG_TAG, 3);
    private static final String TAG = "TextViewTranslationCb";
    private ValueAnimator mAnimator;
    private CharSequence mContentDescription;
    private CharSequence mPaddedText;
    private TranslationTransformationMethod mTranslationTransformation;
    private boolean mIsShowingTranslation = false;
    private boolean mAnimationRunning = false;
    private boolean mIsTextPaddingEnabled = false;
    private boolean mOriginalIsTextSelectable = false;
    private int mOriginalFocusable = 0;
    private boolean mOriginalFocusableInTouchMode = false;
    private boolean mOriginalClickable = false;
    private boolean mOriginalLongClickable = false;
    private int mAnimationDurationMillis = 250;

    private void clearTranslationTransformation() {
        if (DEBUG) {
            Log.m106v(TAG, "clearTranslationTransformation: " + this.mTranslationTransformation);
        }
        this.mTranslationTransformation = null;
    }

    @Override // android.view.translation.ViewTranslationCallback
    public boolean onShowTranslation(View view) {
        if (this.mIsShowingTranslation) {
            if (DEBUG) {
                Log.m112d(TAG, view + " is already showing translated text.");
            }
            return false;
        }
        ViewTranslationResponse response = view.getViewTranslationResponse();
        if (response == null) {
            Log.m110e(TAG, "onShowTranslation() shouldn't be called before onViewTranslationResponse().");
            return false;
        }
        TextView theTextView = (TextView) view;
        TranslationTransformationMethod translationTransformationMethod = this.mTranslationTransformation;
        if (translationTransformationMethod == null || !response.equals(translationTransformationMethod.getViewTranslationResponse())) {
            TransformationMethod originalTranslationMethod = theTextView.getTransformationMethod();
            this.mTranslationTransformation = new TranslationTransformationMethod(response, originalTranslationMethod);
        }
        final TransformationMethod originalTranslationMethod2 = this.mTranslationTransformation;
        final WeakReference<TextView> textViewRef = new WeakReference<>(theTextView);
        runChangeTextWithAnimationIfNeeded(theTextView, new Runnable() { // from class: android.widget.TextViewTranslationCallback$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                TextViewTranslationCallback.this.lambda$onShowTranslation$0(textViewRef, originalTranslationMethod2);
            }
        });
        if (response.getKeys().contains(ViewTranslationRequest.ID_CONTENT_DESCRIPTION)) {
            CharSequence translatedContentDescription = response.getValue(ViewTranslationRequest.ID_CONTENT_DESCRIPTION).getText();
            if (!TextUtils.isEmpty(translatedContentDescription)) {
                this.mContentDescription = view.getContentDescription();
                view.setContentDescription(translatedContentDescription);
                return true;
            }
            return true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onShowTranslation$0(WeakReference textViewRef, TransformationMethod transformation) {
        this.mIsShowingTranslation = true;
        this.mAnimationRunning = false;
        TextView textView = (TextView) textViewRef.get();
        if (textView == null) {
            return;
        }
        boolean isTextSelectable = textView.isTextSelectable();
        this.mOriginalIsTextSelectable = isTextSelectable;
        if (isTextSelectable) {
            this.mOriginalFocusableInTouchMode = textView.isFocusableInTouchMode();
            this.mOriginalFocusable = textView.getFocusable();
            this.mOriginalClickable = textView.isClickable();
            this.mOriginalLongClickable = textView.isLongClickable();
            textView.setTextIsSelectable(false);
        }
        textView.setTransformationMethod(transformation);
    }

    @Override // android.view.translation.ViewTranslationCallback
    public boolean onHideTranslation(View view) {
        if (view.getViewTranslationResponse() == null) {
            Log.m110e(TAG, "onHideTranslation() shouldn't be called before onViewTranslationResponse().");
            return false;
        }
        TranslationTransformationMethod translationTransformationMethod = this.mTranslationTransformation;
        if (translationTransformationMethod != null) {
            final TransformationMethod transformation = translationTransformationMethod.getOriginalTransformationMethod();
            TextView theTextView = (TextView) view;
            final WeakReference<TextView> textViewRef = new WeakReference<>(theTextView);
            runChangeTextWithAnimationIfNeeded(theTextView, new Runnable() { // from class: android.widget.TextViewTranslationCallback$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    TextViewTranslationCallback.this.lambda$onHideTranslation$1(textViewRef, transformation);
                }
            });
            if (!TextUtils.isEmpty(this.mContentDescription)) {
                view.setContentDescription(this.mContentDescription);
                return true;
            }
            return true;
        }
        if (DEBUG) {
            Log.m104w(TAG, "onHideTranslation(): no translated text.");
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onHideTranslation$1(WeakReference textViewRef, TransformationMethod transformation) {
        this.mIsShowingTranslation = false;
        this.mAnimationRunning = false;
        TextView textView = (TextView) textViewRef.get();
        if (textView == null) {
            return;
        }
        textView.setTransformationMethod(transformation);
        if (this.mOriginalIsTextSelectable && !textView.isTextSelectable()) {
            textView.setTextIsSelectable(true);
            textView.setFocusableInTouchMode(this.mOriginalFocusableInTouchMode);
            textView.setFocusable(this.mOriginalFocusable);
            textView.setClickable(this.mOriginalClickable);
            textView.setLongClickable(this.mOriginalLongClickable);
        }
    }

    @Override // android.view.translation.ViewTranslationCallback
    public boolean onClearTranslation(View view) {
        if (this.mTranslationTransformation != null) {
            onHideTranslation(view);
            clearTranslationTransformation();
            this.mPaddedText = null;
            this.mContentDescription = null;
            return true;
        } else if (DEBUG) {
            Log.m104w(TAG, "onClearTranslation(): no translated text.");
            return false;
        } else {
            return false;
        }
    }

    public boolean isShowingTranslation() {
        return this.mIsShowingTranslation;
    }

    public boolean isAnimationRunning() {
        return this.mAnimationRunning;
    }

    @Override // android.view.translation.ViewTranslationCallback
    public void enableContentPadding() {
        this.mIsTextPaddingEnabled = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isTextPaddingEnabled() {
        return this.mIsTextPaddingEnabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequence getPaddedText(CharSequence text, CharSequence translatedText) {
        if (text == null) {
            return null;
        }
        if (this.mPaddedText == null) {
            this.mPaddedText = computePaddedText(text, translatedText);
        }
        return this.mPaddedText;
    }

    private CharSequence computePaddedText(CharSequence text, CharSequence translatedText) {
        if (translatedText == null) {
            return text;
        }
        int newLength = translatedText.length();
        if (newLength <= text.length()) {
            return text;
        }
        StringBuilder sb = new StringBuilder(newLength);
        sb.append(text);
        for (int i = text.length(); i < newLength; i++) {
            sb.append(COMPAT_PAD_CHARACTER);
        }
        return sb;
    }

    @Override // android.view.translation.ViewTranslationCallback
    public void setAnimationDurationMillis(int durationMillis) {
        this.mAnimationDurationMillis = durationMillis;
    }

    private void runChangeTextWithAnimationIfNeeded(final TextView view, final Runnable changeTextRunnable) {
        boolean areAnimatorsEnabled = ValueAnimator.areAnimatorsEnabled();
        if (!areAnimatorsEnabled) {
            changeTextRunnable.run();
            return;
        }
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator != null) {
            valueAnimator.end();
        }
        this.mAnimationRunning = true;
        int fadedOutColor = colorWithAlpha(view.getCurrentTextColor(), 0);
        ValueAnimator ofArgb = ValueAnimator.ofArgb(view.getCurrentTextColor(), fadedOutColor);
        this.mAnimator = ofArgb;
        ofArgb.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.widget.TextViewTranslationCallback$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                TextView.this.setTextColor(((Integer) valueAnimator2.getAnimatedValue()).intValue());
            }
        });
        this.mAnimator.setRepeatMode(2);
        this.mAnimator.setRepeatCount(1);
        this.mAnimator.setDuration(this.mAnimationDurationMillis);
        final ColorStateList originalColors = view.getTextColors();
        final WeakReference<TextView> viewRef = new WeakReference<>(view);
        this.mAnimator.addListener(new Animator.AnimatorListener() { // from class: android.widget.TextViewTranslationCallback.1
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                TextView view2 = (TextView) viewRef.get();
                if (view2 != null) {
                    view2.setTextColor(originalColors);
                }
                TextViewTranslationCallback.this.mAnimator = null;
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animation) {
                changeTextRunnable.run();
            }
        });
        this.mAnimator.start();
    }

    private static int colorWithAlpha(int color, int newAlpha) {
        return Color.argb(newAlpha, Color.red(color), Color.green(color), Color.blue(color));
    }
}
