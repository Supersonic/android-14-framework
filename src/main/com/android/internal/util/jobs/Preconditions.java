package com.android.internal.util.jobs;

import android.text.TextUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
/* loaded from: classes.dex */
public class Preconditions {
    public static void checkArgument(boolean z) {
        if (!z) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean z, Object obj) {
        if (!z) {
            throw new IllegalArgumentException(String.valueOf(obj));
        }
    }

    public static void checkArgument(boolean z, String str, Object... objArr) {
        if (!z) {
            throw new IllegalArgumentException(String.format(str, objArr));
        }
    }

    public static <T extends CharSequence> T checkStringNotEmpty(T t) {
        if (TextUtils.isEmpty(t)) {
            throw new IllegalArgumentException();
        }
        return t;
    }

    public static <T extends CharSequence> T checkStringNotEmpty(T t, Object obj) {
        if (TextUtils.isEmpty(t)) {
            throw new IllegalArgumentException(String.valueOf(obj));
        }
        return t;
    }

    public static <T extends CharSequence> T checkStringNotEmpty(T t, String str, Object... objArr) {
        if (TextUtils.isEmpty(t)) {
            throw new IllegalArgumentException(String.format(str, objArr));
        }
        return t;
    }

    @Deprecated
    public static <T> T checkNotNull(T t) {
        t.getClass();
        return t;
    }

    @Deprecated
    public static <T> T checkNotNull(T t, Object obj) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException(String.valueOf(obj));
    }

    public static <T> T checkNotNull(T t, String str, Object... objArr) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException(String.format(str, objArr));
    }

    public static void checkState(boolean z) {
        checkState(z, null);
    }

    public static void checkState(boolean z, String str) {
        if (!z) {
            throw new IllegalStateException(str);
        }
    }

    public static void checkState(boolean z, String str, Object... objArr) {
        if (!z) {
            throw new IllegalStateException(String.format(str, objArr));
        }
    }

    public static void checkCallAuthorization(boolean z) {
        if (!z) {
            throw new SecurityException("Calling identity is not authorized");
        }
    }

    public static void checkCallAuthorization(boolean z, String str) {
        if (!z) {
            throw new SecurityException(str);
        }
    }

    public static void checkCallAuthorization(boolean z, String str, Object... objArr) {
        if (!z) {
            throw new SecurityException(String.format(str, objArr));
        }
    }

    public static void checkCallingUser(boolean z) {
        if (!z) {
            throw new SecurityException("Calling user is not authorized");
        }
    }

    public static int checkFlagsArgument(int i, int i2) {
        if ((i & i2) == i) {
            return i;
        }
        throw new IllegalArgumentException("Requested flags 0x" + Integer.toHexString(i) + ", but only 0x" + Integer.toHexString(i2) + " are allowed");
    }

    public static int checkArgumentNonnegative(int i, String str) {
        if (i >= 0) {
            return i;
        }
        throw new IllegalArgumentException(str);
    }

    public static int checkArgumentNonnegative(int i) {
        if (i >= 0) {
            return i;
        }
        throw new IllegalArgumentException();
    }

    public static long checkArgumentNonnegative(long j) {
        if (j >= 0) {
            return j;
        }
        throw new IllegalArgumentException();
    }

    public static long checkArgumentNonnegative(long j, String str) {
        if (j >= 0) {
            return j;
        }
        throw new IllegalArgumentException(str);
    }

    public static int checkArgumentPositive(int i, String str) {
        if (i > 0) {
            return i;
        }
        throw new IllegalArgumentException(str);
    }

    public static float checkArgumentNonNegative(float f, String str) {
        if (f >= 0.0f) {
            return f;
        }
        throw new IllegalArgumentException(str);
    }

    public static float checkArgumentPositive(float f, String str) {
        if (f > 0.0f) {
            return f;
        }
        throw new IllegalArgumentException(str);
    }

    public static float checkArgumentFinite(float f, String str) {
        if (Float.isNaN(f)) {
            throw new IllegalArgumentException(str + " must not be NaN");
        } else if (Float.isInfinite(f)) {
            throw new IllegalArgumentException(str + " must not be infinite");
        } else {
            return f;
        }
    }

    public static float checkArgumentInRange(float f, float f2, float f3, String str) {
        if (Float.isNaN(f)) {
            throw new IllegalArgumentException(str + " must not be NaN");
        } else if (f >= f2) {
            if (f <= f3) {
                return f;
            }
            throw new IllegalArgumentException(String.format("%s is out of range of [%f, %f] (too high)", str, Float.valueOf(f2), Float.valueOf(f3)));
        } else {
            throw new IllegalArgumentException(String.format("%s is out of range of [%f, %f] (too low)", str, Float.valueOf(f2), Float.valueOf(f3)));
        }
    }

    public static double checkArgumentInRange(double d, double d2, double d3, String str) {
        if (Double.isNaN(d)) {
            throw new IllegalArgumentException(str + " must not be NaN");
        } else if (d >= d2) {
            if (d <= d3) {
                return d;
            }
            throw new IllegalArgumentException(String.format("%s is out of range of [%f, %f] (too high)", str, Double.valueOf(d2), Double.valueOf(d3)));
        } else {
            throw new IllegalArgumentException(String.format("%s is out of range of [%f, %f] (too low)", str, Double.valueOf(d2), Double.valueOf(d3)));
        }
    }

    public static int checkArgumentInRange(int i, int i2, int i3, String str) {
        if (i >= i2) {
            if (i <= i3) {
                return i;
            }
            throw new IllegalArgumentException(String.format("%s is out of range of [%d, %d] (too high)", str, Integer.valueOf(i2), Integer.valueOf(i3)));
        }
        throw new IllegalArgumentException(String.format("%s is out of range of [%d, %d] (too low)", str, Integer.valueOf(i2), Integer.valueOf(i3)));
    }

    public static long checkArgumentInRange(long j, long j2, long j3, String str) {
        if (j >= j2) {
            if (j <= j3) {
                return j;
            }
            throw new IllegalArgumentException(String.format("%s is out of range of [%d, %d] (too high)", str, Long.valueOf(j2), Long.valueOf(j3)));
        }
        throw new IllegalArgumentException(String.format("%s is out of range of [%d, %d] (too low)", str, Long.valueOf(j2), Long.valueOf(j3)));
    }

    public static <T> T[] checkArrayElementsNotNull(T[] tArr, String str) {
        if (tArr == null) {
            throw new NullPointerException(str + " must not be null");
        }
        for (int i = 0; i < tArr.length; i++) {
            if (tArr[i] == null) {
                throw new NullPointerException(String.format("%s[%d] must not be null", str, Integer.valueOf(i)));
            }
        }
        return tArr;
    }

    public static <C extends Collection<T>, T> C checkCollectionElementsNotNull(C c, String str) {
        if (c == null) {
            throw new NullPointerException(str + " must not be null");
        }
        long j = 0;
        for (Object obj : c) {
            if (obj == null) {
                throw new NullPointerException(String.format("%s[%d] must not be null", str, Long.valueOf(j)));
            }
            j++;
        }
        return c;
    }

    public static <T> Collection<T> checkCollectionNotEmpty(Collection<T> collection, String str) {
        if (collection == null) {
            throw new NullPointerException(str + " must not be null");
        } else if (collection.isEmpty()) {
            throw new IllegalArgumentException(str + " is empty");
        } else {
            return collection;
        }
    }

    public static byte[] checkByteArrayNotEmpty(byte[] bArr, String str) {
        if (bArr == null) {
            throw new NullPointerException(str + " must not be null");
        } else if (bArr.length != 0) {
            return bArr;
        } else {
            throw new IllegalArgumentException(str + " is empty");
        }
    }

    public static String checkArgumentIsSupported(String[] strArr, String str) {
        checkNotNull(str);
        checkNotNull(strArr);
        if (contains(strArr, str)) {
            return str;
        }
        throw new IllegalArgumentException(str + "is not supported " + Arrays.toString(strArr));
    }

    public static boolean contains(String[] strArr, String str) {
        if (strArr == null) {
            return false;
        }
        for (String str2 : strArr) {
            if (Objects.equals(str, str2)) {
                return true;
            }
        }
        return false;
    }

    public static float[] checkArrayElementsInRange(float[] fArr, float f, float f2, String str) {
        checkNotNull(fArr, "%s must not be null", str);
        for (int i = 0; i < fArr.length; i++) {
            float f3 = fArr[i];
            if (Float.isNaN(f3)) {
                throw new IllegalArgumentException(str + "[" + i + "] must not be NaN");
            } else if (f3 < f) {
                throw new IllegalArgumentException(String.format("%s[%d] is out of range of [%f, %f] (too low)", str, Integer.valueOf(i), Float.valueOf(f), Float.valueOf(f2)));
            } else {
                if (f3 > f2) {
                    throw new IllegalArgumentException(String.format("%s[%d] is out of range of [%f, %f] (too high)", str, Integer.valueOf(i), Float.valueOf(f), Float.valueOf(f2)));
                }
            }
        }
        return fArr;
    }

    public static int[] checkArrayElementsInRange(int[] iArr, int i, int i2, String str) {
        checkNotNull(iArr, "%s must not be null", str);
        for (int i3 = 0; i3 < iArr.length; i3++) {
            int i4 = iArr[i3];
            if (i4 < i) {
                throw new IllegalArgumentException(String.format("%s[%d] is out of range of [%d, %d] (too low)", str, Integer.valueOf(i3), Integer.valueOf(i), Integer.valueOf(i2)));
            }
            if (i4 > i2) {
                throw new IllegalArgumentException(String.format("%s[%d] is out of range of [%d, %d] (too high)", str, Integer.valueOf(i3), Integer.valueOf(i), Integer.valueOf(i2)));
            }
        }
        return iArr;
    }
}
