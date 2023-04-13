package android.content.res;

import android.util.MathUtils;
import java.util.Arrays;
/* loaded from: classes.dex */
public class FontScaleConverter {
    final float[] mFromSpValues;
    final float[] mToDpValues;

    public FontScaleConverter(float[] fromSp, float[] toDp) {
        if (fromSp.length != toDp.length || fromSp.length == 0) {
            throw new IllegalArgumentException("Array lengths must match and be nonzero");
        }
        this.mFromSpValues = fromSp;
        this.mToDpValues = toDp;
    }

    public float convertDpToSp(float dp) {
        return lookupAndInterpolate(dp, this.mToDpValues, this.mFromSpValues);
    }

    public float convertSpToDp(float sp) {
        return lookupAndInterpolate(sp, this.mFromSpValues, this.mToDpValues);
    }

    private static float lookupAndInterpolate(float sourceValue, float[] sourceValues, float[] targetValues) {
        float startSp;
        float endSp;
        float startDp;
        float endDp;
        float sourceValuePositive = Math.abs(sourceValue);
        float sign = Math.signum(sourceValue);
        int index = Arrays.binarySearch(sourceValues, sourceValuePositive);
        if (index >= 0) {
            return targetValues[index] * sign;
        }
        int lowerIndex = (-(index + 1)) - 1;
        if (lowerIndex >= sourceValues.length - 1) {
            float startSp2 = sourceValues[sourceValues.length - 1];
            float startDp2 = targetValues[sourceValues.length - 1];
            if (startSp2 == 0.0f) {
                return 0.0f;
            }
            float scalingFactor = startDp2 / startSp2;
            return sourceValue * scalingFactor;
        }
        if (lowerIndex == -1) {
            startSp = 0.0f;
            startDp = 0.0f;
            endSp = sourceValues[0];
            endDp = targetValues[0];
        } else {
            startSp = sourceValues[lowerIndex];
            endSp = sourceValues[lowerIndex + 1];
            startDp = targetValues[lowerIndex];
            endDp = targetValues[lowerIndex + 1];
        }
        return MathUtils.constrainedMap(startDp, endDp, startSp, endSp, sourceValuePositive) * sign;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof FontScaleConverter)) {
            return false;
        }
        FontScaleConverter that = (FontScaleConverter) o;
        if (Arrays.equals(this.mFromSpValues, that.mFromSpValues) && Arrays.equals(this.mToDpValues, that.mToDpValues)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = Arrays.hashCode(this.mFromSpValues);
        return (result * 31) + Arrays.hashCode(this.mToDpValues);
    }

    public String toString() {
        return "FontScaleConverter{fromSpValues=" + Arrays.toString(this.mFromSpValues) + ", toDpValues=" + Arrays.toString(this.mToDpValues) + '}';
    }
}
