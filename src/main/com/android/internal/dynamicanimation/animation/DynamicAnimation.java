package com.android.internal.dynamicanimation.animation;

import android.animation.AnimationHandler;
import android.animation.ValueAnimator;
import android.p008os.Looper;
import android.util.AndroidRuntimeException;
import android.util.FloatProperty;
import android.view.View;
import com.android.internal.dynamicanimation.animation.DynamicAnimation;
import java.util.ArrayList;
/* loaded from: classes4.dex */
public abstract class DynamicAnimation<T extends DynamicAnimation<T>> implements AnimationHandler.AnimationFrameCallback {
    public static final float MIN_VISIBLE_CHANGE_ALPHA = 0.00390625f;
    public static final float MIN_VISIBLE_CHANGE_PIXELS = 1.0f;
    public static final float MIN_VISIBLE_CHANGE_ROTATION_DEGREES = 0.1f;
    public static final float MIN_VISIBLE_CHANGE_SCALE = 0.002f;
    private static final float THRESHOLD_MULTIPLIER = 0.75f;
    private static final float UNSET = Float.MAX_VALUE;
    private AnimationHandler mAnimationHandler;
    private final ArrayList<OnAnimationEndListener> mEndListeners;
    private long mLastFrameTime;
    float mMaxValue;
    float mMinValue;
    private float mMinVisibleChange;
    final FloatProperty mProperty;
    boolean mRunning;
    boolean mStartValueIsSet;
    final Object mTarget;
    private final ArrayList<OnAnimationUpdateListener> mUpdateListeners;
    float mValue;
    float mVelocity;
    public static final ViewProperty TRANSLATION_X = new ViewProperty("translationX") { // from class: com.android.internal.dynamicanimation.animation.DynamicAnimation.1
        @Override // android.util.FloatProperty
        public void setValue(View view, float value) {
            view.setTranslationX(value);
        }

        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getTranslationX());
        }
    };
    public static final ViewProperty TRANSLATION_Y = new ViewProperty("translationY") { // from class: com.android.internal.dynamicanimation.animation.DynamicAnimation.2
        @Override // android.util.FloatProperty
        public void setValue(View view, float value) {
            view.setTranslationY(value);
        }

        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getTranslationY());
        }
    };
    public static final ViewProperty TRANSLATION_Z = new ViewProperty("translationZ") { // from class: com.android.internal.dynamicanimation.animation.DynamicAnimation.3
        @Override // android.util.FloatProperty
        public void setValue(View view, float value) {
            view.setTranslationZ(value);
        }

        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getTranslationZ());
        }
    };
    public static final ViewProperty SCALE_X = new ViewProperty("scaleX") { // from class: com.android.internal.dynamicanimation.animation.DynamicAnimation.4
        @Override // android.util.FloatProperty
        public void setValue(View view, float value) {
            view.setScaleX(value);
        }

        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getScaleX());
        }
    };
    public static final ViewProperty SCALE_Y = new ViewProperty("scaleY") { // from class: com.android.internal.dynamicanimation.animation.DynamicAnimation.5
        @Override // android.util.FloatProperty
        public void setValue(View view, float value) {
            view.setScaleY(value);
        }

        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getScaleY());
        }
    };
    public static final ViewProperty ROTATION = new ViewProperty("rotation") { // from class: com.android.internal.dynamicanimation.animation.DynamicAnimation.6
        @Override // android.util.FloatProperty
        public void setValue(View view, float value) {
            view.setRotation(value);
        }

        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getRotation());
        }
    };
    public static final ViewProperty ROTATION_X = new ViewProperty("rotationX") { // from class: com.android.internal.dynamicanimation.animation.DynamicAnimation.7
        @Override // android.util.FloatProperty
        public void setValue(View view, float value) {
            view.setRotationX(value);
        }

        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getRotationX());
        }
    };
    public static final ViewProperty ROTATION_Y = new ViewProperty("rotationY") { // from class: com.android.internal.dynamicanimation.animation.DynamicAnimation.8
        @Override // android.util.FloatProperty
        public void setValue(View view, float value) {
            view.setRotationY(value);
        }

        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getRotationY());
        }
    };

    /* renamed from: X */
    public static final ViewProperty f572X = new ViewProperty("x") { // from class: com.android.internal.dynamicanimation.animation.DynamicAnimation.9
        @Override // android.util.FloatProperty
        public void setValue(View view, float value) {
            view.setX(value);
        }

        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getX());
        }
    };

    /* renamed from: Y */
    public static final ViewProperty f573Y = new ViewProperty("y") { // from class: com.android.internal.dynamicanimation.animation.DynamicAnimation.10
        @Override // android.util.FloatProperty
        public void setValue(View view, float value) {
            view.setY(value);
        }

        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getY());
        }
    };

    /* renamed from: Z */
    public static final ViewProperty f574Z = new ViewProperty("z") { // from class: com.android.internal.dynamicanimation.animation.DynamicAnimation.11
        @Override // android.util.FloatProperty
        public void setValue(View view, float value) {
            view.setZ(value);
        }

        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getZ());
        }
    };
    public static final ViewProperty ALPHA = new ViewProperty("alpha") { // from class: com.android.internal.dynamicanimation.animation.DynamicAnimation.12
        @Override // android.util.FloatProperty
        public void setValue(View view, float value) {
            view.setAlpha(value);
        }

        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getAlpha());
        }
    };
    public static final ViewProperty SCROLL_X = new ViewProperty("scrollX") { // from class: com.android.internal.dynamicanimation.animation.DynamicAnimation.13
        @Override // android.util.FloatProperty
        public void setValue(View view, float value) {
            view.setScrollX((int) value);
        }

        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getScrollX());
        }
    };
    public static final ViewProperty SCROLL_Y = new ViewProperty("scrollY") { // from class: com.android.internal.dynamicanimation.animation.DynamicAnimation.14
        @Override // android.util.FloatProperty
        public void setValue(View view, float value) {
            view.setScrollY((int) value);
        }

        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getScrollY());
        }
    };

    /* loaded from: classes4.dex */
    static class MassState {
        float mValue;
        float mVelocity;
    }

    /* loaded from: classes4.dex */
    public interface OnAnimationEndListener {
        void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2);
    }

    /* loaded from: classes4.dex */
    public interface OnAnimationUpdateListener {
        void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2);
    }

    abstract float getAcceleration(float f, float f2);

    abstract boolean isAtEquilibrium(float f, float f2);

    abstract void setValueThreshold(float f);

    abstract boolean updateValueAndVelocity(long j);

    /* loaded from: classes4.dex */
    public static abstract class ViewProperty extends FloatProperty<View> {
        private ViewProperty(String name) {
            super(name);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DynamicAnimation(final FloatValueHolder floatValueHolder) {
        this.mVelocity = 0.0f;
        this.mValue = Float.MAX_VALUE;
        this.mStartValueIsSet = false;
        this.mRunning = false;
        this.mMaxValue = Float.MAX_VALUE;
        this.mMinValue = -Float.MAX_VALUE;
        this.mLastFrameTime = 0L;
        this.mEndListeners = new ArrayList<>();
        this.mUpdateListeners = new ArrayList<>();
        this.mTarget = null;
        this.mProperty = new FloatProperty("FloatValueHolder") { // from class: com.android.internal.dynamicanimation.animation.DynamicAnimation.15
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.util.Property
            public Float get(Object object) {
                return Float.valueOf(floatValueHolder.getValue());
            }

            @Override // android.util.FloatProperty
            public void setValue(Object object, float value) {
                floatValueHolder.setValue(value);
            }
        };
        this.mMinVisibleChange = 1.0f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <K> DynamicAnimation(K object, FloatProperty<K> property) {
        this.mVelocity = 0.0f;
        this.mValue = Float.MAX_VALUE;
        this.mStartValueIsSet = false;
        this.mRunning = false;
        this.mMaxValue = Float.MAX_VALUE;
        this.mMinValue = -Float.MAX_VALUE;
        this.mLastFrameTime = 0L;
        this.mEndListeners = new ArrayList<>();
        this.mUpdateListeners = new ArrayList<>();
        this.mTarget = object;
        this.mProperty = property;
        if (property == ROTATION || property == ROTATION_X || property == ROTATION_Y) {
            this.mMinVisibleChange = 0.1f;
        } else if (property == ALPHA) {
            this.mMinVisibleChange = 0.00390625f;
        } else if (property == SCALE_X || property == SCALE_Y) {
            this.mMinVisibleChange = 0.002f;
        } else {
            this.mMinVisibleChange = 1.0f;
        }
    }

    public T setStartValue(float startValue) {
        this.mValue = startValue;
        this.mStartValueIsSet = true;
        return this;
    }

    public T setStartVelocity(float startVelocity) {
        this.mVelocity = startVelocity;
        return this;
    }

    public T setMaxValue(float max) {
        this.mMaxValue = max;
        return this;
    }

    public T setMinValue(float min) {
        this.mMinValue = min;
        return this;
    }

    public T addEndListener(OnAnimationEndListener listener) {
        if (!this.mEndListeners.contains(listener)) {
            this.mEndListeners.add(listener);
        }
        return this;
    }

    public void removeEndListener(OnAnimationEndListener listener) {
        removeEntry(this.mEndListeners, listener);
    }

    public T addUpdateListener(OnAnimationUpdateListener listener) {
        if (isRunning()) {
            throw new UnsupportedOperationException("Error: Update listeners must be added beforethe animation.");
        }
        if (!this.mUpdateListeners.contains(listener)) {
            this.mUpdateListeners.add(listener);
        }
        return this;
    }

    public void removeUpdateListener(OnAnimationUpdateListener listener) {
        removeEntry(this.mUpdateListeners, listener);
    }

    public T setMinimumVisibleChange(float minimumVisibleChange) {
        if (minimumVisibleChange <= 0.0f) {
            throw new IllegalArgumentException("Minimum visible change must be positive.");
        }
        this.mMinVisibleChange = minimumVisibleChange;
        setValueThreshold(0.75f * minimumVisibleChange);
        return this;
    }

    public float getMinimumVisibleChange() {
        return this.mMinVisibleChange;
    }

    private static <T> void removeNullEntries(ArrayList<T> list) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i) == null) {
                list.remove(i);
            }
        }
    }

    private static <T> void removeEntry(ArrayList<T> list, T entry) {
        int id = list.indexOf(entry);
        if (id >= 0) {
            list.set(id, null);
        }
    }

    public void start() {
        if (!isCurrentThread()) {
            throw new AndroidRuntimeException("Animations may only be started on the same thread as the animation handler");
        }
        if (!this.mRunning) {
            startAnimationInternal();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isCurrentThread() {
        return Thread.currentThread() == Looper.myLooper().getThread();
    }

    public void cancel() {
        if (!isCurrentThread()) {
            throw new AndroidRuntimeException("Animations may only be canceled from the same thread as the animation handler");
        }
        if (this.mRunning) {
            endAnimationInternal(true);
        }
    }

    public boolean isRunning() {
        return this.mRunning;
    }

    private void startAnimationInternal() {
        if (!this.mRunning) {
            this.mRunning = true;
            if (!this.mStartValueIsSet) {
                this.mValue = getPropertyValue();
            }
            float f = this.mValue;
            if (f > this.mMaxValue || f < this.mMinValue) {
                throw new IllegalArgumentException("Starting value need to be in between min value and max value");
            }
            getAnimationHandler().addAnimationFrameCallback(this, 0L);
        }
    }

    @Override // android.animation.AnimationHandler.AnimationFrameCallback
    public boolean doAnimationFrame(long frameTime) {
        long j = this.mLastFrameTime;
        if (j == 0) {
            this.mLastFrameTime = frameTime;
            setPropertyValue(this.mValue);
            return false;
        }
        long deltaT = frameTime - j;
        this.mLastFrameTime = frameTime;
        float durationScale = ValueAnimator.getDurationScale();
        boolean finished = updateValueAndVelocity(durationScale == 0.0f ? 2147483647L : ((float) deltaT) / durationScale);
        float min = Math.min(this.mValue, this.mMaxValue);
        this.mValue = min;
        float max = Math.max(min, this.mMinValue);
        this.mValue = max;
        setPropertyValue(max);
        if (finished) {
            endAnimationInternal(false);
        }
        return finished;
    }

    @Override // android.animation.AnimationHandler.AnimationFrameCallback
    public void commitAnimationFrame(long frameTime) {
        doAnimationFrame(frameTime);
    }

    private void endAnimationInternal(boolean canceled) {
        this.mRunning = false;
        getAnimationHandler().removeCallback(this);
        this.mLastFrameTime = 0L;
        this.mStartValueIsSet = false;
        for (int i = 0; i < this.mEndListeners.size(); i++) {
            if (this.mEndListeners.get(i) != null) {
                this.mEndListeners.get(i).onAnimationEnd(this, canceled, this.mValue, this.mVelocity);
            }
        }
        removeNullEntries(this.mEndListeners);
    }

    void setPropertyValue(float value) {
        this.mProperty.setValue(this.mTarget, value);
        for (int i = 0; i < this.mUpdateListeners.size(); i++) {
            if (this.mUpdateListeners.get(i) != null) {
                this.mUpdateListeners.get(i).onAnimationUpdate(this, this.mValue, this.mVelocity);
            }
        }
        removeNullEntries(this.mUpdateListeners);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getValueThreshold() {
        return this.mMinVisibleChange * 0.75f;
    }

    private float getPropertyValue() {
        return this.mProperty.get(this.mTarget).floatValue();
    }

    public AnimationHandler getAnimationHandler() {
        AnimationHandler animationHandler = this.mAnimationHandler;
        return animationHandler != null ? animationHandler : AnimationHandler.getInstance();
    }
}
