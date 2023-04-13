package android.animation;

import android.animation.Keyframes;
import android.animation.PathKeyframes;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.util.Log;
import android.util.PathParser;
import android.util.Property;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes.dex */
public class PropertyValuesHolder implements Cloneable {
    private Object mAnimatedValue;
    private TypeConverter mConverter;
    private TypeEvaluator mEvaluator;
    private Method mGetter;
    Keyframes mKeyframes;
    protected Property mProperty;
    String mPropertyName;
    Method mSetter;
    final Object[] mTmpValueArray;
    Class mValueType;
    private static final TypeEvaluator sIntEvaluator = new IntEvaluator();
    private static final TypeEvaluator sFloatEvaluator = new FloatEvaluator();
    private static Class[] FLOAT_VARIANTS = {Float.TYPE, Float.class, Double.TYPE, Integer.TYPE, Double.class, Integer.class};
    private static Class[] INTEGER_VARIANTS = {Integer.TYPE, Integer.class, Float.TYPE, Double.TYPE, Float.class, Double.class};
    private static Class[] DOUBLE_VARIANTS = {Double.TYPE, Double.class, Float.TYPE, Integer.TYPE, Float.class, Integer.class};
    private static final HashMap<Class, HashMap<String, Method>> sSetterPropertyMap = new HashMap<>();
    private static final HashMap<Class, HashMap<String, Method>> sGetterPropertyMap = new HashMap<>();

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nCallFloatMethod(Object obj, long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nCallFourFloatMethod(Object obj, long j, float f, float f2, float f3, float f4);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nCallFourIntMethod(Object obj, long j, int i, int i2, int i3, int i4);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nCallIntMethod(Object obj, long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nCallMultipleFloatMethod(Object obj, long j, float[] fArr);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nCallMultipleIntMethod(Object obj, long j, int[] iArr);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nCallTwoFloatMethod(Object obj, long j, float f, float f2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nCallTwoIntMethod(Object obj, long j, int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nGetFloatMethod(Class cls, String str);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nGetIntMethod(Class cls, String str);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nGetMultipleFloatMethod(Class cls, String str, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nGetMultipleIntMethod(Class cls, String str, int i);

    private PropertyValuesHolder(String propertyName) {
        this.mSetter = null;
        this.mGetter = null;
        this.mKeyframes = null;
        this.mTmpValueArray = new Object[1];
        this.mPropertyName = propertyName;
    }

    private PropertyValuesHolder(Property property) {
        this.mSetter = null;
        this.mGetter = null;
        this.mKeyframes = null;
        this.mTmpValueArray = new Object[1];
        this.mProperty = property;
        if (property != null) {
            this.mPropertyName = property.getName();
        }
    }

    public static PropertyValuesHolder ofInt(String propertyName, int... values) {
        return new IntPropertyValuesHolder(propertyName, values);
    }

    public static PropertyValuesHolder ofInt(Property<?, Integer> property, int... values) {
        return new IntPropertyValuesHolder(property, values);
    }

    public static PropertyValuesHolder ofMultiInt(String propertyName, int[][] values) {
        if (values.length < 2) {
            throw new IllegalArgumentException("At least 2 values must be supplied");
        }
        int numParameters = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                throw new IllegalArgumentException("values must not be null");
            }
            int length = values[i].length;
            if (i == 0) {
                numParameters = length;
            } else if (length != numParameters) {
                throw new IllegalArgumentException("Values must all have the same length");
            }
        }
        IntArrayEvaluator evaluator = new IntArrayEvaluator(new int[numParameters]);
        return new MultiIntValuesHolder(propertyName, (TypeConverter) null, evaluator, values);
    }

    public static PropertyValuesHolder ofMultiInt(String propertyName, Path path) {
        Keyframes keyframes = KeyframeSet.ofPath(path);
        PointFToIntArray converter = new PointFToIntArray();
        return new MultiIntValuesHolder(propertyName, converter, (TypeEvaluator) null, keyframes);
    }

    @SafeVarargs
    public static <V> PropertyValuesHolder ofMultiInt(String propertyName, TypeConverter<V, int[]> converter, TypeEvaluator<V> evaluator, V... values) {
        return new MultiIntValuesHolder(propertyName, converter, evaluator, values);
    }

    public static <T> PropertyValuesHolder ofMultiInt(String propertyName, TypeConverter<T, int[]> converter, TypeEvaluator<T> evaluator, Keyframe... values) {
        KeyframeSet keyframeSet = KeyframeSet.ofKeyframe(values);
        return new MultiIntValuesHolder(propertyName, converter, evaluator, keyframeSet);
    }

    public static PropertyValuesHolder ofFloat(String propertyName, float... values) {
        return new FloatPropertyValuesHolder(propertyName, values);
    }

    public static PropertyValuesHolder ofFloat(Property<?, Float> property, float... values) {
        return new FloatPropertyValuesHolder(property, values);
    }

    public static PropertyValuesHolder ofMultiFloat(String propertyName, float[][] values) {
        if (values.length < 2) {
            throw new IllegalArgumentException("At least 2 values must be supplied");
        }
        int numParameters = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                throw new IllegalArgumentException("values must not be null");
            }
            int length = values[i].length;
            if (i == 0) {
                numParameters = length;
            } else if (length != numParameters) {
                throw new IllegalArgumentException("Values must all have the same length");
            }
        }
        FloatArrayEvaluator evaluator = new FloatArrayEvaluator(new float[numParameters]);
        return new MultiFloatValuesHolder(propertyName, (TypeConverter) null, evaluator, values);
    }

    public static PropertyValuesHolder ofMultiFloat(String propertyName, Path path) {
        Keyframes keyframes = KeyframeSet.ofPath(path);
        PointFToFloatArray converter = new PointFToFloatArray();
        return new MultiFloatValuesHolder(propertyName, converter, (TypeEvaluator) null, keyframes);
    }

    @SafeVarargs
    public static <V> PropertyValuesHolder ofMultiFloat(String propertyName, TypeConverter<V, float[]> converter, TypeEvaluator<V> evaluator, V... values) {
        return new MultiFloatValuesHolder(propertyName, converter, evaluator, values);
    }

    public static <T> PropertyValuesHolder ofMultiFloat(String propertyName, TypeConverter<T, float[]> converter, TypeEvaluator<T> evaluator, Keyframe... values) {
        KeyframeSet keyframeSet = KeyframeSet.ofKeyframe(values);
        return new MultiFloatValuesHolder(propertyName, converter, evaluator, keyframeSet);
    }

    public static PropertyValuesHolder ofObject(String propertyName, TypeEvaluator evaluator, Object... values) {
        PropertyValuesHolder pvh = new PropertyValuesHolder(propertyName);
        pvh.setObjectValues(values);
        pvh.setEvaluator(evaluator);
        return pvh;
    }

    public static PropertyValuesHolder ofObject(String propertyName, TypeConverter<PointF, ?> converter, Path path) {
        PropertyValuesHolder pvh = new PropertyValuesHolder(propertyName);
        pvh.mKeyframes = KeyframeSet.ofPath(path);
        pvh.mValueType = PointF.class;
        pvh.setConverter(converter);
        return pvh;
    }

    @SafeVarargs
    public static <V> PropertyValuesHolder ofObject(Property property, TypeEvaluator<V> evaluator, V... values) {
        PropertyValuesHolder pvh = new PropertyValuesHolder(property);
        pvh.setObjectValues(values);
        pvh.setEvaluator(evaluator);
        return pvh;
    }

    @SafeVarargs
    public static <T, V> PropertyValuesHolder ofObject(Property<?, V> property, TypeConverter<T, V> converter, TypeEvaluator<T> evaluator, T... values) {
        PropertyValuesHolder pvh = new PropertyValuesHolder(property);
        pvh.setConverter(converter);
        pvh.setObjectValues(values);
        pvh.setEvaluator(evaluator);
        return pvh;
    }

    public static <V> PropertyValuesHolder ofObject(Property<?, V> property, TypeConverter<PointF, V> converter, Path path) {
        PropertyValuesHolder pvh = new PropertyValuesHolder(property);
        pvh.mKeyframes = KeyframeSet.ofPath(path);
        pvh.mValueType = PointF.class;
        pvh.setConverter(converter);
        return pvh;
    }

    public static PropertyValuesHolder ofKeyframe(String propertyName, Keyframe... values) {
        KeyframeSet keyframeSet = KeyframeSet.ofKeyframe(values);
        return ofKeyframes(propertyName, keyframeSet);
    }

    public static PropertyValuesHolder ofKeyframe(Property property, Keyframe... values) {
        KeyframeSet keyframeSet = KeyframeSet.ofKeyframe(values);
        return ofKeyframes(property, keyframeSet);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PropertyValuesHolder ofKeyframes(String propertyName, Keyframes keyframes) {
        if (keyframes instanceof Keyframes.IntKeyframes) {
            return new IntPropertyValuesHolder(propertyName, (Keyframes.IntKeyframes) keyframes);
        }
        if (keyframes instanceof Keyframes.FloatKeyframes) {
            return new FloatPropertyValuesHolder(propertyName, (Keyframes.FloatKeyframes) keyframes);
        }
        PropertyValuesHolder pvh = new PropertyValuesHolder(propertyName);
        pvh.mKeyframes = keyframes;
        pvh.mValueType = keyframes.getType();
        return pvh;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PropertyValuesHolder ofKeyframes(Property property, Keyframes keyframes) {
        if (keyframes instanceof Keyframes.IntKeyframes) {
            return new IntPropertyValuesHolder(property, (Keyframes.IntKeyframes) keyframes);
        }
        if (keyframes instanceof Keyframes.FloatKeyframes) {
            return new FloatPropertyValuesHolder(property, (Keyframes.FloatKeyframes) keyframes);
        }
        PropertyValuesHolder pvh = new PropertyValuesHolder(property);
        pvh.mKeyframes = keyframes;
        pvh.mValueType = keyframes.getType();
        return pvh;
    }

    public void setIntValues(int... values) {
        this.mValueType = Integer.TYPE;
        this.mKeyframes = KeyframeSet.ofInt(values);
    }

    public void setFloatValues(float... values) {
        this.mValueType = Float.TYPE;
        this.mKeyframes = KeyframeSet.ofFloat(values);
    }

    public void setKeyframes(Keyframe... values) {
        int numKeyframes = values.length;
        Keyframe[] keyframes = new Keyframe[Math.max(numKeyframes, 2)];
        this.mValueType = values[0].getType();
        for (int i = 0; i < numKeyframes; i++) {
            keyframes[i] = values[i];
        }
        this.mKeyframes = new KeyframeSet(keyframes);
    }

    public void setObjectValues(Object... values) {
        this.mValueType = values[0].getClass();
        KeyframeSet ofObject = KeyframeSet.ofObject(values);
        this.mKeyframes = ofObject;
        TypeEvaluator typeEvaluator = this.mEvaluator;
        if (typeEvaluator != null) {
            ofObject.setEvaluator(typeEvaluator);
        }
    }

    public void setConverter(TypeConverter converter) {
        this.mConverter = converter;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Method getPropertyFunction(Class targetClass, String prefix, Class valueType) {
        Class[] typeVariants;
        Method returnVal = null;
        String methodName = getMethodName(prefix, this.mPropertyName);
        if (valueType == null) {
            try {
                returnVal = targetClass.getMethod(methodName, null);
            } catch (NoSuchMethodException e) {
            }
        } else {
            Class[] args = new Class[1];
            if (valueType.equals(Float.class)) {
                typeVariants = FLOAT_VARIANTS;
            } else if (valueType.equals(Integer.class)) {
                typeVariants = INTEGER_VARIANTS;
            } else {
                typeVariants = valueType.equals(Double.class) ? DOUBLE_VARIANTS : new Class[]{valueType};
            }
            for (Class typeVariant : typeVariants) {
                args[0] = typeVariant;
                try {
                    returnVal = targetClass.getMethod(methodName, args);
                    if (this.mConverter == null) {
                        this.mValueType = typeVariant;
                    }
                    return returnVal;
                } catch (NoSuchMethodException e2) {
                }
            }
        }
        if (returnVal == null) {
            Log.m104w("PropertyValuesHolder", "Method " + getMethodName(prefix, this.mPropertyName) + "() with type " + valueType + " not found on target class " + targetClass);
        }
        return returnVal;
    }

    private Method setupSetterOrGetter(Class targetClass, HashMap<Class, HashMap<String, Method>> propertyMapMap, String prefix, Class valueType) {
        Method setterOrGetter = null;
        synchronized (propertyMapMap) {
            HashMap<String, Method> propertyMap = propertyMapMap.get(targetClass);
            boolean wasInMap = false;
            if (propertyMap != null && (wasInMap = propertyMap.containsKey(this.mPropertyName))) {
                setterOrGetter = propertyMap.get(this.mPropertyName);
            }
            if (!wasInMap) {
                setterOrGetter = getPropertyFunction(targetClass, prefix, valueType);
                if (propertyMap == null) {
                    propertyMap = new HashMap<>();
                    propertyMapMap.put(targetClass, propertyMap);
                }
                propertyMap.put(this.mPropertyName, setterOrGetter);
            }
        }
        return setterOrGetter;
    }

    void setupSetter(Class targetClass) {
        TypeConverter typeConverter = this.mConverter;
        Class<?> propertyType = typeConverter == null ? this.mValueType : typeConverter.getTargetType();
        this.mSetter = setupSetterOrGetter(targetClass, sSetterPropertyMap, "set", propertyType);
    }

    private void setupGetter(Class targetClass) {
        this.mGetter = setupSetterOrGetter(targetClass, sGetterPropertyMap, "get", null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setupSetterAndGetter(Object target) {
        if (this.mProperty != null) {
            Object testValue = null;
            try {
                List<Keyframe> keyframes = this.mKeyframes.getKeyframes();
                int keyframeCount = keyframes == null ? 0 : keyframes.size();
                for (int i = 0; i < keyframeCount; i++) {
                    Keyframe kf = keyframes.get(i);
                    if (!kf.hasValue() || kf.valueWasSetOnStart()) {
                        if (testValue == null) {
                            testValue = convertBack(this.mProperty.get(target));
                        }
                        kf.setValue(testValue);
                        kf.setValueWasSetOnStart(true);
                    }
                }
                return;
            } catch (ClassCastException e) {
                Log.m104w("PropertyValuesHolder", "No such property (" + this.mProperty.getName() + ") on target object " + target + ". Trying reflection instead");
                this.mProperty = null;
            }
        }
        if (this.mProperty == null) {
            Class targetClass = target.getClass();
            if (this.mSetter == null) {
                setupSetter(targetClass);
            }
            List<Keyframe> keyframes2 = this.mKeyframes.getKeyframes();
            int keyframeCount2 = keyframes2 == null ? 0 : keyframes2.size();
            for (int i2 = 0; i2 < keyframeCount2; i2++) {
                Keyframe kf2 = keyframes2.get(i2);
                if (!kf2.hasValue() || kf2.valueWasSetOnStart()) {
                    if (this.mGetter == null) {
                        setupGetter(targetClass);
                        if (this.mGetter == null) {
                            return;
                        }
                    }
                    try {
                        Object value = convertBack(this.mGetter.invoke(target, new Object[0]));
                        kf2.setValue(value);
                        kf2.setValueWasSetOnStart(true);
                    } catch (IllegalAccessException e2) {
                        Log.m110e("PropertyValuesHolder", e2.toString());
                    } catch (InvocationTargetException e3) {
                        Log.m110e("PropertyValuesHolder", e3.toString());
                    }
                }
            }
        }
    }

    private Object convertBack(Object value) {
        TypeConverter typeConverter = this.mConverter;
        if (typeConverter != null) {
            if (!(typeConverter instanceof BidirectionalTypeConverter)) {
                throw new IllegalArgumentException("Converter " + this.mConverter.getClass().getName() + " must be a BidirectionalTypeConverter");
            }
            return ((BidirectionalTypeConverter) typeConverter).convertBack(value);
        }
        return value;
    }

    private void setupValue(Object target, Keyframe kf) {
        Property property = this.mProperty;
        if (property != null) {
            Object value = convertBack(property.get(target));
            kf.setValue(value);
            return;
        }
        try {
            if (this.mGetter == null) {
                Class targetClass = target.getClass();
                setupGetter(targetClass);
                if (this.mGetter == null) {
                    return;
                }
            }
            Object value2 = convertBack(this.mGetter.invoke(target, new Object[0]));
            kf.setValue(value2);
        } catch (IllegalAccessException e) {
            Log.m110e("PropertyValuesHolder", e.toString());
        } catch (InvocationTargetException e2) {
            Log.m110e("PropertyValuesHolder", e2.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setupStartValue(Object target) {
        List<Keyframe> keyframes = this.mKeyframes.getKeyframes();
        if (!keyframes.isEmpty()) {
            setupValue(target, keyframes.get(0));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setupEndValue(Object target) {
        List<Keyframe> keyframes = this.mKeyframes.getKeyframes();
        if (!keyframes.isEmpty()) {
            setupValue(target, keyframes.get(keyframes.size() - 1));
        }
    }

    @Override // 
    /* renamed from: clone */
    public PropertyValuesHolder mo303clone() {
        try {
            PropertyValuesHolder newPVH = (PropertyValuesHolder) super.clone();
            newPVH.mPropertyName = this.mPropertyName;
            newPVH.mProperty = this.mProperty;
            newPVH.mKeyframes = this.mKeyframes.clone();
            newPVH.mEvaluator = this.mEvaluator;
            return newPVH;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAnimatedValue(Object target) {
        Property property = this.mProperty;
        if (property != null) {
            property.set(target, getAnimatedValue());
        }
        if (this.mSetter != null) {
            try {
                this.mTmpValueArray[0] = getAnimatedValue();
                this.mSetter.invoke(target, this.mTmpValueArray);
            } catch (IllegalAccessException e) {
                Log.m110e("PropertyValuesHolder", e.toString());
            } catch (InvocationTargetException e2) {
                Log.m110e("PropertyValuesHolder", e2.toString());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init() {
        TypeEvaluator typeEvaluator;
        if (this.mEvaluator == null) {
            Class cls = this.mValueType;
            if (cls == Integer.class) {
                typeEvaluator = sIntEvaluator;
            } else {
                typeEvaluator = cls == Float.class ? sFloatEvaluator : null;
            }
            this.mEvaluator = typeEvaluator;
        }
        TypeEvaluator typeEvaluator2 = this.mEvaluator;
        if (typeEvaluator2 != null) {
            this.mKeyframes.setEvaluator(typeEvaluator2);
        }
    }

    public void setEvaluator(TypeEvaluator evaluator) {
        this.mEvaluator = evaluator;
        this.mKeyframes.setEvaluator(evaluator);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void calculateValue(float fraction) {
        Object value = this.mKeyframes.getValue(fraction);
        TypeConverter typeConverter = this.mConverter;
        this.mAnimatedValue = typeConverter == null ? value : typeConverter.convert(value);
    }

    public void setPropertyName(String propertyName) {
        this.mPropertyName = propertyName;
    }

    public void setProperty(Property property) {
        this.mProperty = property;
    }

    public String getPropertyName() {
        return this.mPropertyName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Object getAnimatedValue() {
        return this.mAnimatedValue;
    }

    public void getPropertyValues(PropertyValues values) {
        init();
        values.propertyName = this.mPropertyName;
        values.type = this.mValueType;
        values.startValue = this.mKeyframes.getValue(0.0f);
        if (values.startValue instanceof PathParser.PathData) {
            values.startValue = new PathParser.PathData((PathParser.PathData) values.startValue);
        }
        values.endValue = this.mKeyframes.getValue(1.0f);
        if (values.endValue instanceof PathParser.PathData) {
            values.endValue = new PathParser.PathData((PathParser.PathData) values.endValue);
        }
        Keyframes keyframes = this.mKeyframes;
        if ((keyframes instanceof PathKeyframes.FloatKeyframesBase) || (keyframes instanceof PathKeyframes.IntKeyframesBase) || (keyframes.getKeyframes() != null && this.mKeyframes.getKeyframes().size() > 2)) {
            values.dataSource = new PropertyValues.DataSource() { // from class: android.animation.PropertyValuesHolder.1
                @Override // android.animation.PropertyValuesHolder.PropertyValues.DataSource
                public Object getValueAtFraction(float fraction) {
                    return PropertyValuesHolder.this.mKeyframes.getValue(fraction);
                }
            };
        } else {
            values.dataSource = null;
        }
    }

    public Class getValueType() {
        return this.mValueType;
    }

    public String toString() {
        return this.mPropertyName + ": " + this.mKeyframes.toString();
    }

    static String getMethodName(String prefix, String propertyName) {
        if (propertyName == null || propertyName.length() == 0) {
            return prefix;
        }
        char firstLetter = Character.toUpperCase(propertyName.charAt(0));
        String theRest = propertyName.substring(1);
        return prefix + firstLetter + theRest;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class IntPropertyValuesHolder extends PropertyValuesHolder {
        private static final HashMap<Class, HashMap<String, Long>> sJNISetterPropertyMap = new HashMap<>();
        int mIntAnimatedValue;
        Keyframes.IntKeyframes mIntKeyframes;
        private IntProperty mIntProperty;
        long mJniSetter;

        public IntPropertyValuesHolder(String propertyName, Keyframes.IntKeyframes keyframes) {
            super(propertyName);
            this.mValueType = Integer.TYPE;
            this.mKeyframes = keyframes;
            this.mIntKeyframes = keyframes;
        }

        public IntPropertyValuesHolder(Property property, Keyframes.IntKeyframes keyframes) {
            super(property);
            this.mValueType = Integer.TYPE;
            this.mKeyframes = keyframes;
            this.mIntKeyframes = keyframes;
            if (property instanceof IntProperty) {
                this.mIntProperty = (IntProperty) this.mProperty;
            }
        }

        public IntPropertyValuesHolder(String propertyName, int... values) {
            super(propertyName);
            setIntValues(values);
        }

        public IntPropertyValuesHolder(Property property, int... values) {
            super(property);
            setIntValues(values);
            if (property instanceof IntProperty) {
                this.mIntProperty = (IntProperty) this.mProperty;
            }
        }

        @Override // android.animation.PropertyValuesHolder
        public void setProperty(Property property) {
            if (property instanceof IntProperty) {
                this.mIntProperty = (IntProperty) property;
            } else {
                super.setProperty(property);
            }
        }

        @Override // android.animation.PropertyValuesHolder
        public void setIntValues(int... values) {
            super.setIntValues(values);
            this.mIntKeyframes = (Keyframes.IntKeyframes) this.mKeyframes;
        }

        @Override // android.animation.PropertyValuesHolder
        void calculateValue(float fraction) {
            this.mIntAnimatedValue = this.mIntKeyframes.getIntValue(fraction);
        }

        @Override // android.animation.PropertyValuesHolder
        Object getAnimatedValue() {
            return Integer.valueOf(this.mIntAnimatedValue);
        }

        @Override // android.animation.PropertyValuesHolder
        /* renamed from: clone */
        public IntPropertyValuesHolder mo303clone() {
            IntPropertyValuesHolder newPVH = (IntPropertyValuesHolder) super.mo303clone();
            newPVH.mIntKeyframes = (Keyframes.IntKeyframes) newPVH.mKeyframes;
            return newPVH;
        }

        @Override // android.animation.PropertyValuesHolder
        void setAnimatedValue(Object target) {
            IntProperty intProperty = this.mIntProperty;
            if (intProperty != null) {
                intProperty.setValue(target, this.mIntAnimatedValue);
            } else if (this.mProperty != null) {
                this.mProperty.set(target, Integer.valueOf(this.mIntAnimatedValue));
            } else {
                long j = this.mJniSetter;
                if (j != 0) {
                    PropertyValuesHolder.nCallIntMethod(target, j, this.mIntAnimatedValue);
                } else if (this.mSetter != null) {
                    try {
                        this.mTmpValueArray[0] = Integer.valueOf(this.mIntAnimatedValue);
                        this.mSetter.invoke(target, this.mTmpValueArray);
                    } catch (IllegalAccessException e) {
                        Log.m110e("PropertyValuesHolder", e.toString());
                    } catch (InvocationTargetException e2) {
                        Log.m110e("PropertyValuesHolder", e2.toString());
                    }
                }
            }
        }

        @Override // android.animation.PropertyValuesHolder
        void setupSetter(Class targetClass) {
            Long jniSetter;
            if (this.mProperty != null) {
                return;
            }
            HashMap<Class, HashMap<String, Long>> hashMap = sJNISetterPropertyMap;
            synchronized (hashMap) {
                HashMap<String, Long> propertyMap = hashMap.get(targetClass);
                boolean wasInMap = false;
                if (propertyMap != null && (wasInMap = propertyMap.containsKey(this.mPropertyName)) && (jniSetter = propertyMap.get(this.mPropertyName)) != null) {
                    this.mJniSetter = jniSetter.longValue();
                }
                if (!wasInMap) {
                    String methodName = getMethodName("set", this.mPropertyName);
                    try {
                        this.mJniSetter = PropertyValuesHolder.nGetIntMethod(targetClass, methodName);
                    } catch (NoSuchMethodError e) {
                    }
                    if (propertyMap == null) {
                        propertyMap = new HashMap<>();
                        sJNISetterPropertyMap.put(targetClass, propertyMap);
                    }
                    propertyMap.put(this.mPropertyName, Long.valueOf(this.mJniSetter));
                }
            }
            if (this.mJniSetter == 0) {
                super.setupSetter(targetClass);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class FloatPropertyValuesHolder extends PropertyValuesHolder {
        private static final HashMap<Class, HashMap<String, Long>> sJNISetterPropertyMap = new HashMap<>();
        float mFloatAnimatedValue;
        Keyframes.FloatKeyframes mFloatKeyframes;
        private FloatProperty mFloatProperty;
        long mJniSetter;

        public FloatPropertyValuesHolder(String propertyName, Keyframes.FloatKeyframes keyframes) {
            super(propertyName);
            this.mValueType = Float.TYPE;
            this.mKeyframes = keyframes;
            this.mFloatKeyframes = keyframes;
        }

        public FloatPropertyValuesHolder(Property property, Keyframes.FloatKeyframes keyframes) {
            super(property);
            this.mValueType = Float.TYPE;
            this.mKeyframes = keyframes;
            this.mFloatKeyframes = keyframes;
            if (property instanceof FloatProperty) {
                this.mFloatProperty = (FloatProperty) this.mProperty;
            }
        }

        public FloatPropertyValuesHolder(String propertyName, float... values) {
            super(propertyName);
            setFloatValues(values);
        }

        public FloatPropertyValuesHolder(Property property, float... values) {
            super(property);
            setFloatValues(values);
            if (property instanceof FloatProperty) {
                this.mFloatProperty = (FloatProperty) this.mProperty;
            }
        }

        @Override // android.animation.PropertyValuesHolder
        public void setProperty(Property property) {
            if (property instanceof FloatProperty) {
                this.mFloatProperty = (FloatProperty) property;
            } else {
                super.setProperty(property);
            }
        }

        @Override // android.animation.PropertyValuesHolder
        public void setFloatValues(float... values) {
            super.setFloatValues(values);
            this.mFloatKeyframes = (Keyframes.FloatKeyframes) this.mKeyframes;
        }

        @Override // android.animation.PropertyValuesHolder
        void calculateValue(float fraction) {
            this.mFloatAnimatedValue = this.mFloatKeyframes.getFloatValue(fraction);
        }

        @Override // android.animation.PropertyValuesHolder
        Object getAnimatedValue() {
            return Float.valueOf(this.mFloatAnimatedValue);
        }

        @Override // android.animation.PropertyValuesHolder
        /* renamed from: clone */
        public FloatPropertyValuesHolder mo303clone() {
            FloatPropertyValuesHolder newPVH = (FloatPropertyValuesHolder) super.mo303clone();
            newPVH.mFloatKeyframes = (Keyframes.FloatKeyframes) newPVH.mKeyframes;
            return newPVH;
        }

        @Override // android.animation.PropertyValuesHolder
        void setAnimatedValue(Object target) {
            FloatProperty floatProperty = this.mFloatProperty;
            if (floatProperty != null) {
                floatProperty.setValue(target, this.mFloatAnimatedValue);
            } else if (this.mProperty != null) {
                this.mProperty.set(target, Float.valueOf(this.mFloatAnimatedValue));
            } else {
                long j = this.mJniSetter;
                if (j != 0) {
                    PropertyValuesHolder.nCallFloatMethod(target, j, this.mFloatAnimatedValue);
                } else if (this.mSetter != null) {
                    try {
                        this.mTmpValueArray[0] = Float.valueOf(this.mFloatAnimatedValue);
                        this.mSetter.invoke(target, this.mTmpValueArray);
                    } catch (IllegalAccessException e) {
                        Log.m110e("PropertyValuesHolder", e.toString());
                    } catch (InvocationTargetException e2) {
                        Log.m110e("PropertyValuesHolder", e2.toString());
                    }
                }
            }
        }

        @Override // android.animation.PropertyValuesHolder
        void setupSetter(Class targetClass) {
            Long jniSetter;
            if (this.mProperty != null) {
                return;
            }
            HashMap<Class, HashMap<String, Long>> hashMap = sJNISetterPropertyMap;
            synchronized (hashMap) {
                HashMap<String, Long> propertyMap = hashMap.get(targetClass);
                boolean wasInMap = false;
                if (propertyMap != null && (wasInMap = propertyMap.containsKey(this.mPropertyName)) && (jniSetter = propertyMap.get(this.mPropertyName)) != null) {
                    this.mJniSetter = jniSetter.longValue();
                }
                if (!wasInMap) {
                    String methodName = getMethodName("set", this.mPropertyName);
                    try {
                        this.mJniSetter = PropertyValuesHolder.nGetFloatMethod(targetClass, methodName);
                    } catch (NoSuchMethodError e) {
                    }
                    if (propertyMap == null) {
                        propertyMap = new HashMap<>();
                        sJNISetterPropertyMap.put(targetClass, propertyMap);
                    }
                    propertyMap.put(this.mPropertyName, Long.valueOf(this.mJniSetter));
                }
            }
            if (this.mJniSetter == 0) {
                super.setupSetter(targetClass);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class MultiFloatValuesHolder extends PropertyValuesHolder {
        private static final HashMap<Class, HashMap<String, Long>> sJNISetterPropertyMap = new HashMap<>();
        private long mJniSetter;

        public MultiFloatValuesHolder(String propertyName, TypeConverter converter, TypeEvaluator evaluator, Object... values) {
            super(propertyName);
            setConverter(converter);
            setObjectValues(values);
            setEvaluator(evaluator);
        }

        public MultiFloatValuesHolder(String propertyName, TypeConverter converter, TypeEvaluator evaluator, Keyframes keyframes) {
            super(propertyName);
            setConverter(converter);
            this.mKeyframes = keyframes;
            setEvaluator(evaluator);
        }

        @Override // android.animation.PropertyValuesHolder
        void setAnimatedValue(Object target) {
            float[] values = (float[]) getAnimatedValue();
            int numParameters = values.length;
            long j = this.mJniSetter;
            if (j != 0) {
                switch (numParameters) {
                    case 1:
                        PropertyValuesHolder.nCallFloatMethod(target, j, values[0]);
                        return;
                    case 2:
                        PropertyValuesHolder.nCallTwoFloatMethod(target, j, values[0], values[1]);
                        return;
                    case 3:
                    default:
                        PropertyValuesHolder.nCallMultipleFloatMethod(target, j, values);
                        return;
                    case 4:
                        PropertyValuesHolder.nCallFourFloatMethod(target, j, values[0], values[1], values[2], values[3]);
                        return;
                }
            }
        }

        @Override // android.animation.PropertyValuesHolder
        void setupSetterAndGetter(Object target) {
            setupSetter(target.getClass());
        }

        @Override // android.animation.PropertyValuesHolder
        void setupSetter(Class targetClass) {
            Long jniSetter;
            if (this.mJniSetter != 0) {
                return;
            }
            HashMap<Class, HashMap<String, Long>> hashMap = sJNISetterPropertyMap;
            synchronized (hashMap) {
                HashMap<String, Long> propertyMap = hashMap.get(targetClass);
                boolean wasInMap = false;
                if (propertyMap != null && (wasInMap = propertyMap.containsKey(this.mPropertyName)) && (jniSetter = propertyMap.get(this.mPropertyName)) != null) {
                    this.mJniSetter = jniSetter.longValue();
                }
                if (!wasInMap) {
                    String methodName = getMethodName("set", this.mPropertyName);
                    calculateValue(0.0f);
                    float[] values = (float[]) getAnimatedValue();
                    int numParams = values.length;
                    try {
                        this.mJniSetter = PropertyValuesHolder.nGetMultipleFloatMethod(targetClass, methodName, numParams);
                    } catch (NoSuchMethodError e) {
                        try {
                            this.mJniSetter = PropertyValuesHolder.nGetMultipleFloatMethod(targetClass, this.mPropertyName, numParams);
                        } catch (NoSuchMethodError e2) {
                        }
                    }
                    if (propertyMap == null) {
                        propertyMap = new HashMap<>();
                        sJNISetterPropertyMap.put(targetClass, propertyMap);
                    }
                    propertyMap.put(this.mPropertyName, Long.valueOf(this.mJniSetter));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class MultiIntValuesHolder extends PropertyValuesHolder {
        private static final HashMap<Class, HashMap<String, Long>> sJNISetterPropertyMap = new HashMap<>();
        private long mJniSetter;

        public MultiIntValuesHolder(String propertyName, TypeConverter converter, TypeEvaluator evaluator, Object... values) {
            super(propertyName);
            setConverter(converter);
            setObjectValues(values);
            setEvaluator(evaluator);
        }

        public MultiIntValuesHolder(String propertyName, TypeConverter converter, TypeEvaluator evaluator, Keyframes keyframes) {
            super(propertyName);
            setConverter(converter);
            this.mKeyframes = keyframes;
            setEvaluator(evaluator);
        }

        @Override // android.animation.PropertyValuesHolder
        void setAnimatedValue(Object target) {
            int[] values = (int[]) getAnimatedValue();
            int numParameters = values.length;
            long j = this.mJniSetter;
            if (j != 0) {
                switch (numParameters) {
                    case 1:
                        PropertyValuesHolder.nCallIntMethod(target, j, values[0]);
                        return;
                    case 2:
                        PropertyValuesHolder.nCallTwoIntMethod(target, j, values[0], values[1]);
                        return;
                    case 3:
                    default:
                        PropertyValuesHolder.nCallMultipleIntMethod(target, j, values);
                        return;
                    case 4:
                        PropertyValuesHolder.nCallFourIntMethod(target, j, values[0], values[1], values[2], values[3]);
                        return;
                }
            }
        }

        @Override // android.animation.PropertyValuesHolder
        void setupSetterAndGetter(Object target) {
            setupSetter(target.getClass());
        }

        @Override // android.animation.PropertyValuesHolder
        void setupSetter(Class targetClass) {
            Long jniSetter;
            if (this.mJniSetter != 0) {
                return;
            }
            HashMap<Class, HashMap<String, Long>> hashMap = sJNISetterPropertyMap;
            synchronized (hashMap) {
                HashMap<String, Long> propertyMap = hashMap.get(targetClass);
                boolean wasInMap = false;
                if (propertyMap != null && (wasInMap = propertyMap.containsKey(this.mPropertyName)) && (jniSetter = propertyMap.get(this.mPropertyName)) != null) {
                    this.mJniSetter = jniSetter.longValue();
                }
                if (!wasInMap) {
                    String methodName = getMethodName("set", this.mPropertyName);
                    calculateValue(0.0f);
                    int[] values = (int[]) getAnimatedValue();
                    int numParams = values.length;
                    try {
                        this.mJniSetter = PropertyValuesHolder.nGetMultipleIntMethod(targetClass, methodName, numParams);
                    } catch (NoSuchMethodError e) {
                        try {
                            this.mJniSetter = PropertyValuesHolder.nGetMultipleIntMethod(targetClass, this.mPropertyName, numParams);
                        } catch (NoSuchMethodError e2) {
                        }
                    }
                    if (propertyMap == null) {
                        propertyMap = new HashMap<>();
                        sJNISetterPropertyMap.put(targetClass, propertyMap);
                    }
                    propertyMap.put(this.mPropertyName, Long.valueOf(this.mJniSetter));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PointFToFloatArray extends TypeConverter<PointF, float[]> {
        private float[] mCoordinates;

        public PointFToFloatArray() {
            super(PointF.class, float[].class);
            this.mCoordinates = new float[2];
        }

        @Override // android.animation.TypeConverter
        public float[] convert(PointF value) {
            this.mCoordinates[0] = value.f78x;
            this.mCoordinates[1] = value.f79y;
            return this.mCoordinates;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PointFToIntArray extends TypeConverter<PointF, int[]> {
        private int[] mCoordinates;

        public PointFToIntArray() {
            super(PointF.class, int[].class);
            this.mCoordinates = new int[2];
        }

        @Override // android.animation.TypeConverter
        public int[] convert(PointF value) {
            this.mCoordinates[0] = Math.round(value.f78x);
            this.mCoordinates[1] = Math.round(value.f79y);
            return this.mCoordinates;
        }
    }

    /* loaded from: classes.dex */
    public static class PropertyValues {
        public DataSource dataSource = null;
        public Object endValue;
        public String propertyName;
        public Object startValue;
        public Class type;

        /* loaded from: classes.dex */
        public interface DataSource {
            Object getValueAtFraction(float f);
        }

        public String toString() {
            return "property name: " + this.propertyName + ", type: " + this.type + ", startValue: " + this.startValue.toString() + ", endValue: " + this.endValue.toString();
        }
    }
}
