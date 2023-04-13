package android.animation;

import android.animation.AnimationHandler;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Property;
import java.lang.ref.WeakReference;
/* loaded from: classes.dex */
public final class ObjectAnimator extends ValueAnimator {
    private static final boolean DBG = false;
    private static final String LOG_TAG = "ObjectAnimator";
    private boolean mAutoCancel = false;
    private Property mProperty;
    private String mPropertyName;
    private WeakReference<Object> mTarget;

    public void setPropertyName(String propertyName) {
        if (this.mValues != null) {
            PropertyValuesHolder valuesHolder = this.mValues[0];
            String oldName = valuesHolder.getPropertyName();
            valuesHolder.setPropertyName(propertyName);
            this.mValuesMap.remove(oldName);
            this.mValuesMap.put(propertyName, valuesHolder);
        }
        this.mPropertyName = propertyName;
        this.mInitialized = false;
    }

    public void setProperty(Property property) {
        if (this.mValues != null) {
            PropertyValuesHolder valuesHolder = this.mValues[0];
            String oldName = valuesHolder.getPropertyName();
            valuesHolder.setProperty(property);
            this.mValuesMap.remove(oldName);
            this.mValuesMap.put(this.mPropertyName, valuesHolder);
        }
        if (this.mProperty != null) {
            this.mPropertyName = property.getName();
        }
        this.mProperty = property;
        this.mInitialized = false;
    }

    public String getPropertyName() {
        String propertyName;
        String propertyName2 = null;
        if (this.mPropertyName != null) {
            String propertyName3 = this.mPropertyName;
            return propertyName3;
        }
        Property property = this.mProperty;
        if (property != null) {
            String propertyName4 = property.getName();
            return propertyName4;
        } else if (this.mValues == null || this.mValues.length <= 0) {
            return null;
        } else {
            for (int i = 0; i < this.mValues.length; i++) {
                if (i == 0) {
                    propertyName = "";
                } else {
                    propertyName = propertyName2 + ",";
                }
                propertyName2 = propertyName + this.mValues[i].getPropertyName();
            }
            return propertyName2;
        }
    }

    @Override // android.animation.ValueAnimator
    String getNameForTrace() {
        return "animator:" + getPropertyName();
    }

    public ObjectAnimator() {
    }

    private ObjectAnimator(Object target, String propertyName) {
        setTarget(target);
        setPropertyName(propertyName);
    }

    private <T> ObjectAnimator(T target, Property<T, ?> property) {
        setTarget(target);
        setProperty(property);
    }

    public static ObjectAnimator ofInt(Object target, String propertyName, int... values) {
        ObjectAnimator anim = new ObjectAnimator(target, propertyName);
        anim.setIntValues(values);
        return anim;
    }

    public static ObjectAnimator ofInt(Object target, String xPropertyName, String yPropertyName, Path path) {
        PathKeyframes keyframes = KeyframeSet.ofPath(path);
        PropertyValuesHolder x = PropertyValuesHolder.ofKeyframes(xPropertyName, keyframes.createXIntKeyframes());
        PropertyValuesHolder y = PropertyValuesHolder.ofKeyframes(yPropertyName, keyframes.createYIntKeyframes());
        return ofPropertyValuesHolder(target, x, y);
    }

    public static <T> ObjectAnimator ofInt(T target, Property<T, Integer> property, int... values) {
        ObjectAnimator anim = new ObjectAnimator(target, property);
        anim.setIntValues(values);
        return anim;
    }

    public static <T> ObjectAnimator ofInt(T target, Property<T, Integer> xProperty, Property<T, Integer> yProperty, Path path) {
        PathKeyframes keyframes = KeyframeSet.ofPath(path);
        PropertyValuesHolder x = PropertyValuesHolder.ofKeyframes(xProperty, keyframes.createXIntKeyframes());
        PropertyValuesHolder y = PropertyValuesHolder.ofKeyframes(yProperty, keyframes.createYIntKeyframes());
        return ofPropertyValuesHolder(target, x, y);
    }

    public static ObjectAnimator ofMultiInt(Object target, String propertyName, int[][] values) {
        PropertyValuesHolder pvh = PropertyValuesHolder.ofMultiInt(propertyName, values);
        return ofPropertyValuesHolder(target, pvh);
    }

    public static ObjectAnimator ofMultiInt(Object target, String propertyName, Path path) {
        PropertyValuesHolder pvh = PropertyValuesHolder.ofMultiInt(propertyName, path);
        return ofPropertyValuesHolder(target, pvh);
    }

    @SafeVarargs
    public static <T> ObjectAnimator ofMultiInt(Object target, String propertyName, TypeConverter<T, int[]> converter, TypeEvaluator<T> evaluator, T... values) {
        PropertyValuesHolder pvh = PropertyValuesHolder.ofMultiInt(propertyName, converter, evaluator, values);
        return ofPropertyValuesHolder(target, pvh);
    }

    public static ObjectAnimator ofArgb(Object target, String propertyName, int... values) {
        ObjectAnimator animator = ofInt(target, propertyName, values);
        animator.setEvaluator(ArgbEvaluator.getInstance());
        return animator;
    }

    public static <T> ObjectAnimator ofArgb(T target, Property<T, Integer> property, int... values) {
        ObjectAnimator animator = ofInt(target, property, values);
        animator.setEvaluator(ArgbEvaluator.getInstance());
        return animator;
    }

    public static ObjectAnimator ofFloat(Object target, String propertyName, float... values) {
        ObjectAnimator anim = new ObjectAnimator(target, propertyName);
        anim.setFloatValues(values);
        return anim;
    }

    public static ObjectAnimator ofFloat(Object target, String xPropertyName, String yPropertyName, Path path) {
        PathKeyframes keyframes = KeyframeSet.ofPath(path);
        PropertyValuesHolder x = PropertyValuesHolder.ofKeyframes(xPropertyName, keyframes.createXFloatKeyframes());
        PropertyValuesHolder y = PropertyValuesHolder.ofKeyframes(yPropertyName, keyframes.createYFloatKeyframes());
        return ofPropertyValuesHolder(target, x, y);
    }

    public static <T> ObjectAnimator ofFloat(T target, Property<T, Float> property, float... values) {
        ObjectAnimator anim = new ObjectAnimator(target, property);
        anim.setFloatValues(values);
        return anim;
    }

    public static <T> ObjectAnimator ofFloat(T target, Property<T, Float> xProperty, Property<T, Float> yProperty, Path path) {
        PathKeyframes keyframes = KeyframeSet.ofPath(path);
        PropertyValuesHolder x = PropertyValuesHolder.ofKeyframes(xProperty, keyframes.createXFloatKeyframes());
        PropertyValuesHolder y = PropertyValuesHolder.ofKeyframes(yProperty, keyframes.createYFloatKeyframes());
        return ofPropertyValuesHolder(target, x, y);
    }

    public static ObjectAnimator ofMultiFloat(Object target, String propertyName, float[][] values) {
        PropertyValuesHolder pvh = PropertyValuesHolder.ofMultiFloat(propertyName, values);
        return ofPropertyValuesHolder(target, pvh);
    }

    public static ObjectAnimator ofMultiFloat(Object target, String propertyName, Path path) {
        PropertyValuesHolder pvh = PropertyValuesHolder.ofMultiFloat(propertyName, path);
        return ofPropertyValuesHolder(target, pvh);
    }

    @SafeVarargs
    public static <T> ObjectAnimator ofMultiFloat(Object target, String propertyName, TypeConverter<T, float[]> converter, TypeEvaluator<T> evaluator, T... values) {
        PropertyValuesHolder pvh = PropertyValuesHolder.ofMultiFloat(propertyName, converter, evaluator, values);
        return ofPropertyValuesHolder(target, pvh);
    }

    public static ObjectAnimator ofObject(Object target, String propertyName, TypeEvaluator evaluator, Object... values) {
        ObjectAnimator anim = new ObjectAnimator(target, propertyName);
        anim.setObjectValues(values);
        anim.setEvaluator(evaluator);
        return anim;
    }

    public static ObjectAnimator ofObject(Object target, String propertyName, TypeConverter<PointF, ?> converter, Path path) {
        PropertyValuesHolder pvh = PropertyValuesHolder.ofObject(propertyName, converter, path);
        return ofPropertyValuesHolder(target, pvh);
    }

    @SafeVarargs
    public static <T, V> ObjectAnimator ofObject(T target, Property<T, V> property, TypeEvaluator<V> evaluator, V... values) {
        ObjectAnimator anim = new ObjectAnimator(target, property);
        anim.setObjectValues(values);
        anim.setEvaluator(evaluator);
        return anim;
    }

    @SafeVarargs
    public static <T, V, P> ObjectAnimator ofObject(T target, Property<T, P> property, TypeConverter<V, P> converter, TypeEvaluator<V> evaluator, V... values) {
        PropertyValuesHolder pvh = PropertyValuesHolder.ofObject(property, converter, evaluator, values);
        return ofPropertyValuesHolder(target, pvh);
    }

    public static <T, V> ObjectAnimator ofObject(T target, Property<T, V> property, TypeConverter<PointF, V> converter, Path path) {
        PropertyValuesHolder pvh = PropertyValuesHolder.ofObject(property, converter, path);
        return ofPropertyValuesHolder(target, pvh);
    }

    public static ObjectAnimator ofPropertyValuesHolder(Object target, PropertyValuesHolder... values) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.setTarget(target);
        anim.setValues(values);
        return anim;
    }

    @Override // android.animation.ValueAnimator
    public void setIntValues(int... values) {
        if (this.mValues == null || this.mValues.length == 0) {
            Property property = this.mProperty;
            if (property != null) {
                setValues(PropertyValuesHolder.ofInt(property, values));
                return;
            } else {
                setValues(PropertyValuesHolder.ofInt(this.mPropertyName, values));
                return;
            }
        }
        super.setIntValues(values);
    }

    @Override // android.animation.ValueAnimator
    public void setFloatValues(float... values) {
        if (this.mValues == null || this.mValues.length == 0) {
            Property property = this.mProperty;
            if (property != null) {
                setValues(PropertyValuesHolder.ofFloat(property, values));
                return;
            } else {
                setValues(PropertyValuesHolder.ofFloat(this.mPropertyName, values));
                return;
            }
        }
        super.setFloatValues(values);
    }

    @Override // android.animation.ValueAnimator
    public void setObjectValues(Object... values) {
        if (this.mValues == null || this.mValues.length == 0) {
            Property property = this.mProperty;
            if (property == null) {
                setValues(PropertyValuesHolder.ofObject(this.mPropertyName, (TypeEvaluator) null, values));
                return;
            } else {
                setValues(PropertyValuesHolder.ofObject(property, (TypeEvaluator) null, values));
                return;
            }
        }
        super.setObjectValues(values);
    }

    public void setAutoCancel(boolean cancel) {
        this.mAutoCancel = cancel;
    }

    private boolean hasSameTargetAndProperties(Animator anim) {
        if (anim instanceof ObjectAnimator) {
            PropertyValuesHolder[] theirValues = ((ObjectAnimator) anim).getValues();
            if (((ObjectAnimator) anim).getTarget() == getTarget() && this.mValues.length == theirValues.length) {
                for (int i = 0; i < this.mValues.length; i++) {
                    PropertyValuesHolder pvhMine = this.mValues[i];
                    PropertyValuesHolder pvhTheirs = theirValues[i];
                    if (pvhMine.getPropertyName() == null || !pvhMine.getPropertyName().equals(pvhTheirs.getPropertyName())) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public void start() {
        AnimationHandler.getInstance().autoCancelBasedOn(this);
        super.start();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldAutoCancel(AnimationHandler.AnimationFrameCallback anim) {
        if (anim != null && (anim instanceof ObjectAnimator)) {
            ObjectAnimator objAnim = (ObjectAnimator) anim;
            if (objAnim.mAutoCancel && hasSameTargetAndProperties(objAnim)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.animation.ValueAnimator
    public void initAnimation() {
        if (!this.mInitialized) {
            Object target = getTarget();
            if (target != null) {
                int numValues = this.mValues.length;
                for (int i = 0; i < numValues; i++) {
                    this.mValues[i].setupSetterAndGetter(target);
                }
            }
            super.initAnimation();
        }
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public ObjectAnimator setDuration(long duration) {
        super.setDuration(duration);
        return this;
    }

    public Object getTarget() {
        WeakReference<Object> weakReference = this.mTarget;
        if (weakReference == null) {
            return null;
        }
        return weakReference.get();
    }

    @Override // android.animation.Animator
    public void setTarget(Object target) {
        Object oldTarget = getTarget();
        if (oldTarget != target) {
            if (isStarted()) {
                cancel();
            }
            this.mTarget = target == null ? null : new WeakReference<>(target);
            this.mInitialized = false;
        }
    }

    @Override // android.animation.Animator
    public void setupStartValues() {
        initAnimation();
        Object target = getTarget();
        if (target != null) {
            int numValues = this.mValues.length;
            for (int i = 0; i < numValues; i++) {
                this.mValues[i].setupStartValue(target);
            }
        }
    }

    @Override // android.animation.Animator
    public void setupEndValues() {
        initAnimation();
        Object target = getTarget();
        if (target != null) {
            int numValues = this.mValues.length;
            for (int i = 0; i < numValues; i++) {
                this.mValues[i].setupEndValue(target);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.animation.ValueAnimator
    public void animateValue(float fraction) {
        Object target = getTarget();
        if (this.mTarget != null && target == null) {
            cancel();
            return;
        }
        super.animateValue(fraction);
        int numValues = this.mValues.length;
        for (int i = 0; i < numValues; i++) {
            this.mValues[i].setAnimatedValue(target);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.animation.ValueAnimator, android.animation.Animator
    public boolean isInitialized() {
        return this.mInitialized;
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    /* renamed from: clone */
    public ObjectAnimator mo258clone() {
        ObjectAnimator anim = (ObjectAnimator) super.mo258clone();
        return anim;
    }

    @Override // android.animation.ValueAnimator
    public String toString() {
        String returnVal = "ObjectAnimator@" + Integer.toHexString(hashCode()) + ", target " + getTarget();
        if (this.mValues != null) {
            for (int i = 0; i < this.mValues.length; i++) {
                returnVal = returnVal + "\n    " + this.mValues[i].toString();
            }
        }
        return returnVal;
    }
}
