package android.graphics.drawable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.p008os.SystemClock;
/* loaded from: classes.dex */
public class TransitionDrawable extends LayerDrawable implements Drawable.Callback {
    private static final int TRANSITION_NONE = 2;
    private static final int TRANSITION_RUNNING = 1;
    private static final int TRANSITION_STARTING = 0;
    private int mAlpha;
    private boolean mCrossFade;
    private int mDuration;
    private int mFrom;
    private int mOriginalDuration;
    private boolean mReverse;
    private long mStartTimeMillis;
    private int mTo;
    private int mTransitionState;

    public TransitionDrawable(Drawable[] layers) {
        this(new TransitionState(null, null, null), layers);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TransitionDrawable() {
        this(new TransitionState(null, null, null), (Resources) null);
    }

    private TransitionDrawable(TransitionState state, Resources res) {
        super(state, res);
        this.mTransitionState = 2;
        this.mAlpha = 0;
    }

    private TransitionDrawable(TransitionState state, Drawable[] layers) {
        super(layers, state);
        this.mTransitionState = 2;
        this.mAlpha = 0;
    }

    @Override // android.graphics.drawable.LayerDrawable
    LayerDrawable.LayerState createConstantState(LayerDrawable.LayerState state, Resources res) {
        return new TransitionState((TransitionState) state, this, res);
    }

    public void startTransition(int durationMillis) {
        this.mFrom = 0;
        this.mTo = 255;
        this.mAlpha = 0;
        this.mOriginalDuration = durationMillis;
        this.mDuration = durationMillis;
        this.mReverse = false;
        this.mTransitionState = 0;
        invalidateSelf();
    }

    public void showSecondLayer() {
        this.mAlpha = 255;
        this.mReverse = false;
        this.mTransitionState = 2;
        invalidateSelf();
    }

    public void resetTransition() {
        this.mAlpha = 0;
        this.mTransitionState = 2;
        invalidateSelf();
    }

    public void reverseTransition(int duration) {
        long time = SystemClock.uptimeMillis();
        long j = this.mStartTimeMillis;
        if (time - j > this.mDuration) {
            if (this.mTo == 0) {
                this.mFrom = 0;
                this.mTo = 255;
                this.mAlpha = 0;
                this.mReverse = false;
            } else {
                this.mFrom = 255;
                this.mTo = 0;
                this.mAlpha = 255;
                this.mReverse = true;
            }
            this.mOriginalDuration = duration;
            this.mDuration = duration;
            this.mTransitionState = 0;
            invalidateSelf();
            return;
        }
        boolean z = !this.mReverse;
        this.mReverse = z;
        this.mFrom = this.mAlpha;
        this.mTo = z ? 0 : 255;
        this.mDuration = (int) (z ? time - j : this.mOriginalDuration - (time - j));
        this.mTransitionState = 0;
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        boolean done = true;
        switch (this.mTransitionState) {
            case 0:
                this.mStartTimeMillis = SystemClock.uptimeMillis();
                done = false;
                this.mTransitionState = 1;
                break;
            case 1:
                if (this.mStartTimeMillis >= 0) {
                    float normalized = ((float) (SystemClock.uptimeMillis() - this.mStartTimeMillis)) / this.mDuration;
                    done = normalized >= 1.0f;
                    float normalized2 = Math.min(normalized, 1.0f);
                    int i = this.mFrom;
                    this.mAlpha = (int) (i + ((this.mTo - i) * normalized2));
                    break;
                }
                break;
        }
        int alpha = this.mAlpha;
        boolean crossFade = this.mCrossFade;
        LayerDrawable.ChildDrawable[] array = this.mLayerState.mChildren;
        if (done) {
            if (!crossFade || alpha == 0) {
                array[0].mDrawable.draw(canvas);
            }
            if (alpha == 255) {
                array[1].mDrawable.draw(canvas);
                return;
            }
            return;
        }
        Drawable d = array[0].mDrawable;
        if (crossFade) {
            d.setAlpha(255 - alpha);
        }
        d.draw(canvas);
        if (crossFade) {
            d.setAlpha(255);
        }
        if (alpha > 0) {
            Drawable d2 = array[1].mDrawable;
            d2.setAlpha(alpha);
            d2.draw(canvas);
            d2.setAlpha(255);
        }
        if (!done) {
            invalidateSelf();
        }
    }

    public void setCrossFadeEnabled(boolean enabled) {
        this.mCrossFade = enabled;
    }

    public boolean isCrossFadeEnabled() {
        return this.mCrossFade;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class TransitionState extends LayerDrawable.LayerState {
        TransitionState(TransitionState orig, TransitionDrawable owner, Resources res) {
            super(orig, owner, res);
        }

        @Override // android.graphics.drawable.LayerDrawable.LayerState, android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new TransitionDrawable(this, null);
        }

        @Override // android.graphics.drawable.LayerDrawable.LayerState, android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new TransitionDrawable(this, res);
        }

        @Override // android.graphics.drawable.LayerDrawable.LayerState, android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }
    }
}
