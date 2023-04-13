package android.animation;

import android.animation.Keyframes;
import android.graphics.Path;
import android.graphics.PointF;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class PathKeyframes implements Keyframes {
    private static final ArrayList<Keyframe> EMPTY_KEYFRAMES = new ArrayList<>();
    private static final int FRACTION_OFFSET = 0;
    private static final int NUM_COMPONENTS = 3;
    private static final int X_OFFSET = 1;
    private static final int Y_OFFSET = 2;
    private float[] mKeyframeData;
    private PointF mTempPointF;

    public PathKeyframes(Path path) {
        this(path, 0.5f);
    }

    public PathKeyframes(Path path, float error) {
        this.mTempPointF = new PointF();
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("The path must not be null or empty");
        }
        this.mKeyframeData = path.approximate(error);
    }

    @Override // android.animation.Keyframes
    public ArrayList<Keyframe> getKeyframes() {
        return EMPTY_KEYFRAMES;
    }

    @Override // android.animation.Keyframes
    public Object getValue(float fraction) {
        int numPoints = this.mKeyframeData.length / 3;
        if (fraction < 0.0f) {
            return interpolateInRange(fraction, 0, 1);
        }
        if (fraction > 1.0f) {
            return interpolateInRange(fraction, numPoints - 2, numPoints - 1);
        }
        if (fraction == 0.0f) {
            return pointForIndex(0);
        }
        if (fraction == 1.0f) {
            return pointForIndex(numPoints - 1);
        }
        int low = 0;
        int high = numPoints - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            float midFraction = this.mKeyframeData[(mid * 3) + 0];
            if (fraction < midFraction) {
                high = mid - 1;
            } else if (fraction > midFraction) {
                low = mid + 1;
            } else {
                return pointForIndex(mid);
            }
        }
        return interpolateInRange(fraction, high, low);
    }

    private PointF interpolateInRange(float fraction, int startIndex, int endIndex) {
        int startBase = startIndex * 3;
        int endBase = endIndex * 3;
        float[] fArr = this.mKeyframeData;
        float startFraction = fArr[startBase + 0];
        float endFraction = fArr[endBase + 0];
        float intervalFraction = (fraction - startFraction) / (endFraction - startFraction);
        float startX = fArr[startBase + 1];
        float endX = fArr[endBase + 1];
        float startY = fArr[startBase + 2];
        float endY = fArr[endBase + 2];
        float x = interpolate(intervalFraction, startX, endX);
        float y = interpolate(intervalFraction, startY, endY);
        this.mTempPointF.set(x, y);
        return this.mTempPointF;
    }

    @Override // android.animation.Keyframes
    public void setEvaluator(TypeEvaluator evaluator) {
    }

    @Override // android.animation.Keyframes
    public Class getType() {
        return PointF.class;
    }

    @Override // android.animation.Keyframes
    /* renamed from: clone */
    public Keyframes m289clone() {
        try {
            Keyframes clone = (Keyframes) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    private PointF pointForIndex(int index) {
        int base = index * 3;
        int xOffset = base + 1;
        int yOffset = base + 2;
        PointF pointF = this.mTempPointF;
        float[] fArr = this.mKeyframeData;
        pointF.set(fArr[xOffset], fArr[yOffset]);
        return this.mTempPointF;
    }

    private static float interpolate(float fraction, float startValue, float endValue) {
        float diff = endValue - startValue;
        return (diff * fraction) + startValue;
    }

    public Keyframes.FloatKeyframes createXFloatKeyframes() {
        return new FloatKeyframesBase() { // from class: android.animation.PathKeyframes.1
            @Override // android.animation.Keyframes.FloatKeyframes
            public float getFloatValue(float fraction) {
                PointF pointF = (PointF) PathKeyframes.this.getValue(fraction);
                return pointF.f78x;
            }
        };
    }

    public Keyframes.FloatKeyframes createYFloatKeyframes() {
        return new FloatKeyframesBase() { // from class: android.animation.PathKeyframes.2
            @Override // android.animation.Keyframes.FloatKeyframes
            public float getFloatValue(float fraction) {
                PointF pointF = (PointF) PathKeyframes.this.getValue(fraction);
                return pointF.f79y;
            }
        };
    }

    public Keyframes.IntKeyframes createXIntKeyframes() {
        return new IntKeyframesBase() { // from class: android.animation.PathKeyframes.3
            @Override // android.animation.Keyframes.IntKeyframes
            public int getIntValue(float fraction) {
                PointF pointF = (PointF) PathKeyframes.this.getValue(fraction);
                return Math.round(pointF.f78x);
            }
        };
    }

    public Keyframes.IntKeyframes createYIntKeyframes() {
        return new IntKeyframesBase() { // from class: android.animation.PathKeyframes.4
            @Override // android.animation.Keyframes.IntKeyframes
            public int getIntValue(float fraction) {
                PointF pointF = (PointF) PathKeyframes.this.getValue(fraction);
                return Math.round(pointF.f79y);
            }
        };
    }

    /* loaded from: classes.dex */
    private static abstract class SimpleKeyframes implements Keyframes {
        private SimpleKeyframes() {
        }

        @Override // android.animation.Keyframes
        public void setEvaluator(TypeEvaluator evaluator) {
        }

        @Override // android.animation.Keyframes
        public ArrayList<Keyframe> getKeyframes() {
            return PathKeyframes.EMPTY_KEYFRAMES;
        }

        @Override // android.animation.Keyframes
        /* renamed from: clone */
        public Keyframes m290clone() {
            try {
                Keyframes clone = (Keyframes) super.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }

    /* loaded from: classes.dex */
    static abstract class IntKeyframesBase extends SimpleKeyframes implements Keyframes.IntKeyframes {
        IntKeyframesBase() {
            super();
        }

        @Override // android.animation.Keyframes
        public Class getType() {
            return Integer.class;
        }

        @Override // android.animation.Keyframes
        public Object getValue(float fraction) {
            return Integer.valueOf(getIntValue(fraction));
        }
    }

    /* loaded from: classes.dex */
    static abstract class FloatKeyframesBase extends SimpleKeyframes implements Keyframes.FloatKeyframes {
        FloatKeyframesBase() {
            super();
        }

        @Override // android.animation.Keyframes
        public Class getType() {
            return Float.class;
        }

        @Override // android.animation.Keyframes
        public Object getValue(float fraction) {
            return Float.valueOf(getFloatValue(fraction));
        }
    }
}
