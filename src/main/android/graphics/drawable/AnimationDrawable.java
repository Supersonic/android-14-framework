package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.DrawableContainer;
import android.p008os.SystemClock;
import android.util.AttributeSet;
import com.android.ims.ImsConfig;
import com.android.internal.C4057R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class AnimationDrawable extends DrawableContainer implements Runnable, Animatable {
    private boolean mAnimating;
    private AnimationState mAnimationState;
    private int mCurFrame;
    private boolean mMutated;
    private boolean mRunning;

    public AnimationDrawable() {
        this(null, null);
    }

    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        if (visible) {
            if (restart || changed) {
                boolean startFromZero = restart || !(this.mRunning || this.mAnimationState.mOneShot) || this.mCurFrame >= this.mAnimationState.getChildCount();
                setFrame(startFromZero ? 0 : this.mCurFrame, true, this.mAnimating);
            }
        } else {
            unscheduleSelf(this);
        }
        return changed;
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        boolean z = true;
        this.mAnimating = true;
        if (!isRunning()) {
            if (this.mAnimationState.getChildCount() <= 1 && this.mAnimationState.mOneShot) {
                z = false;
            }
            setFrame(0, false, z);
        }
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        this.mAnimating = false;
        if (isRunning()) {
            this.mCurFrame = 0;
            unscheduleSelf(this);
        }
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        return this.mRunning;
    }

    @Override // java.lang.Runnable
    public void run() {
        nextFrame(false);
    }

    @Override // android.graphics.drawable.Drawable
    public void unscheduleSelf(Runnable what) {
        this.mRunning = false;
        super.unscheduleSelf(what);
    }

    public int getNumberOfFrames() {
        return this.mAnimationState.getChildCount();
    }

    public Drawable getFrame(int index) {
        return this.mAnimationState.getChild(index);
    }

    public int getDuration(int i) {
        return this.mAnimationState.mDurations[i];
    }

    public boolean isOneShot() {
        return this.mAnimationState.mOneShot;
    }

    public void setOneShot(boolean oneShot) {
        this.mAnimationState.mOneShot = oneShot;
    }

    public void addFrame(Drawable frame, int duration) {
        this.mAnimationState.addFrame(frame, duration);
        if (!this.mRunning) {
            setFrame(0, true, false);
        }
    }

    private void nextFrame(boolean unschedule) {
        int nextFrame = this.mCurFrame + 1;
        int numFrames = this.mAnimationState.getChildCount();
        boolean isLastFrame = this.mAnimationState.mOneShot && nextFrame >= numFrames + (-1);
        if (!this.mAnimationState.mOneShot && nextFrame >= numFrames) {
            nextFrame = 0;
        }
        setFrame(nextFrame, unschedule, isLastFrame ? false : true);
    }

    private void setFrame(int frame, boolean unschedule, boolean animate) {
        if (frame >= this.mAnimationState.getChildCount()) {
            return;
        }
        this.mAnimating = animate;
        this.mCurFrame = frame;
        selectDrawable(frame);
        if (unschedule || animate) {
            unscheduleSelf(this);
        }
        if (animate) {
            this.mCurFrame = frame;
            this.mRunning = true;
            scheduleSelf(this, SystemClock.uptimeMillis() + this.mAnimationState.mDurations[frame]);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        TypedArray a = obtainAttributes(r, theme, attrs, C4057R.styleable.AnimationDrawable);
        super.inflateWithAttributes(r, parser, a, 0);
        updateStateFromTypedArray(a);
        updateDensity(r);
        a.recycle();
        inflateChildElements(r, parser, attrs, theme);
        setFrame(0, true, false);
    }

    private void inflateChildElements(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        int type;
        int innerDepth = parser.getDepth() + 1;
        while (true) {
            int type2 = parser.next();
            if (type2 != 1) {
                int depth = parser.getDepth();
                if (depth >= innerDepth || type2 != 3) {
                    if (type2 == 2 && depth <= innerDepth && parser.getName().equals(ImsConfig.EXTRA_CHANGED_ITEM)) {
                        TypedArray a = obtainAttributes(r, theme, attrs, C4057R.styleable.AnimationDrawableItem);
                        int duration = a.getInt(0, -1);
                        if (duration < 0) {
                            throw new XmlPullParserException(parser.getPositionDescription() + ": <item> tag requires a 'duration' attribute");
                        }
                        Drawable dr = a.getDrawable(1);
                        a.recycle();
                        if (dr == null) {
                            do {
                                type = parser.next();
                            } while (type == 4);
                            if (type != 2) {
                                throw new XmlPullParserException(parser.getPositionDescription() + ": <item> tag requires a 'drawable' attribute or child tag defining a drawable");
                            }
                            dr = Drawable.createFromXmlInner(r, parser, attrs, theme);
                        }
                        this.mAnimationState.addFrame(dr, duration);
                        if (dr != null) {
                            dr.setCallback(this);
                        }
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private void updateStateFromTypedArray(TypedArray a) {
        AnimationState animationState = this.mAnimationState;
        animationState.mVariablePadding = a.getBoolean(1, animationState.mVariablePadding);
        AnimationState animationState2 = this.mAnimationState;
        animationState2.mOneShot = a.getBoolean(2, animationState2.mOneShot);
    }

    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mAnimationState.mutate();
            this.mMutated = true;
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.graphics.drawable.DrawableContainer
    public AnimationState cloneConstantState() {
        return new AnimationState(this.mAnimationState, this, null);
    }

    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    public void clearMutated() {
        super.clearMutated();
        this.mMutated = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class AnimationState extends DrawableContainer.DrawableContainerState {
        private int[] mDurations;
        private boolean mOneShot;

        AnimationState(AnimationState orig, AnimationDrawable owner, Resources res) {
            super(orig, owner, res);
            this.mOneShot = false;
            if (orig != null) {
                this.mDurations = orig.mDurations;
                this.mOneShot = orig.mOneShot;
                return;
            }
            this.mDurations = new int[getCapacity()];
            this.mOneShot = false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mutate() {
            this.mDurations = (int[]) this.mDurations.clone();
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new AnimationDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new AnimationDrawable(this, res);
        }

        public void addFrame(Drawable dr, int dur) {
            int pos = super.addChild(dr);
            this.mDurations[pos] = dur;
        }

        @Override // android.graphics.drawable.DrawableContainer.DrawableContainerState
        public void growArray(int oldSize, int newSize) {
            super.growArray(oldSize, newSize);
            int[] newDurations = new int[newSize];
            System.arraycopy(this.mDurations, 0, newDurations, 0, oldSize);
            this.mDurations = newDurations;
        }

        public long getTotalDuration() {
            int[] iArr = this.mDurations;
            if (iArr != null) {
                int total = 0;
                for (int dur : iArr) {
                    total += dur;
                }
                return total;
            }
            return 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.DrawableContainer
    public void setConstantState(DrawableContainer.DrawableContainerState state) {
        super.setConstantState(state);
        if (state instanceof AnimationState) {
            this.mAnimationState = (AnimationState) state;
        }
    }

    public long getTotalDuration() {
        return this.mAnimationState.getTotalDuration();
    }

    private AnimationDrawable(AnimationState state, Resources res) {
        this.mCurFrame = 0;
        AnimationState as = new AnimationState(state, this, res);
        setConstantState(as);
        if (state != null) {
            setFrame(0, true, false);
        }
    }
}
