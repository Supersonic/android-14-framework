package com.android.internal.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.view.animation.DecelerateInterpolator;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes5.dex */
public class DrawableHolder implements Animator.AnimatorListener {
    private static final boolean DBG = false;
    public static final DecelerateInterpolator EASE_OUT_INTERPOLATOR = new DecelerateInterpolator();
    private static final String TAG = "DrawableHolder";
    private float mAlpha;
    private ArrayList<ObjectAnimator> mAnimators;
    private BitmapDrawable mDrawable;
    private ArrayList<ObjectAnimator> mNeedToStart;
    private float mScaleX;
    private float mScaleY;

    /* renamed from: mX */
    private float f2191mX;

    /* renamed from: mY */
    private float f2192mY;

    public DrawableHolder(BitmapDrawable drawable) {
        this(drawable, 0.0f, 0.0f);
    }

    public DrawableHolder(BitmapDrawable drawable, float x, float y) {
        this.f2191mX = 0.0f;
        this.f2192mY = 0.0f;
        this.mScaleX = 1.0f;
        this.mScaleY = 1.0f;
        this.mAlpha = 1.0f;
        this.mAnimators = new ArrayList<>();
        this.mNeedToStart = new ArrayList<>();
        this.mDrawable = drawable;
        this.f2191mX = x;
        this.f2192mY = y;
        drawable.getPaint().setAntiAlias(true);
        BitmapDrawable bitmapDrawable = this.mDrawable;
        bitmapDrawable.setBounds(0, 0, bitmapDrawable.getIntrinsicWidth(), this.mDrawable.getIntrinsicHeight());
    }

    public ObjectAnimator addAnimTo(long duration, long delay, String property, float toValue, boolean replace) {
        if (replace) {
            removeAnimationFor(property);
        }
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, property, toValue);
        anim.setDuration(duration);
        anim.setStartDelay(delay);
        anim.setInterpolator(EASE_OUT_INTERPOLATOR);
        addAnimation(anim, replace);
        return anim;
    }

    public void removeAnimationFor(String property) {
        ArrayList<ObjectAnimator> removalList = (ArrayList) this.mAnimators.clone();
        Iterator<ObjectAnimator> it = removalList.iterator();
        while (it.hasNext()) {
            ObjectAnimator currentAnim = it.next();
            if (property.equals(currentAnim.getPropertyName())) {
                currentAnim.cancel();
            }
        }
    }

    public void clearAnimations() {
        Iterator<ObjectAnimator> it = this.mAnimators.iterator();
        while (it.hasNext()) {
            ObjectAnimator currentAnim = it.next();
            currentAnim.cancel();
        }
        this.mAnimators.clear();
    }

    private DrawableHolder addAnimation(ObjectAnimator anim, boolean overwrite) {
        if (anim != null) {
            this.mAnimators.add(anim);
        }
        this.mNeedToStart.add(anim);
        return this;
    }

    public void draw(Canvas canvas) {
        if (this.mAlpha <= 0.00390625f) {
            return;
        }
        canvas.save(1);
        canvas.translate(this.f2191mX, this.f2192mY);
        canvas.scale(this.mScaleX, this.mScaleY);
        canvas.translate(getWidth() * (-0.5f), getHeight() * (-0.5f));
        this.mDrawable.setAlpha(Math.round(this.mAlpha * 255.0f));
        this.mDrawable.draw(canvas);
        canvas.restore();
    }

    public void startAnimations(ValueAnimator.AnimatorUpdateListener listener) {
        for (int i = 0; i < this.mNeedToStart.size(); i++) {
            ObjectAnimator anim = this.mNeedToStart.get(i);
            anim.addUpdateListener(listener);
            anim.addListener(this);
            anim.start();
        }
        this.mNeedToStart.clear();
    }

    public void setX(float value) {
        this.f2191mX = value;
    }

    public void setY(float value) {
        this.f2192mY = value;
    }

    public void setScaleX(float value) {
        this.mScaleX = value;
    }

    public void setScaleY(float value) {
        this.mScaleY = value;
    }

    public void setAlpha(float alpha) {
        this.mAlpha = alpha;
    }

    public float getX() {
        return this.f2191mX;
    }

    public float getY() {
        return this.f2192mY;
    }

    public float getScaleX() {
        return this.mScaleX;
    }

    public float getScaleY() {
        return this.mScaleY;
    }

    public float getAlpha() {
        return this.mAlpha;
    }

    public BitmapDrawable getDrawable() {
        return this.mDrawable;
    }

    public int getWidth() {
        return this.mDrawable.getIntrinsicWidth();
    }

    public int getHeight() {
        return this.mDrawable.getIntrinsicHeight();
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationCancel(Animator animation) {
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationEnd(Animator animation) {
        this.mAnimators.remove(animation);
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationRepeat(Animator animation) {
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationStart(Animator animation) {
    }
}
