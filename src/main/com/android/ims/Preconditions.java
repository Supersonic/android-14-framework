package com.android.ims;

import android.text.TextUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
/* loaded from: classes.dex */
public class Preconditions {
    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static void checkArgument(boolean expression, String messageTemplate, Object... messageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(messageTemplate, messageArgs));
        }
    }

    public static <T extends CharSequence> T checkStringNotEmpty(T string) {
        if (TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException();
        }
        return string;
    }

    public static <T extends CharSequence> T checkStringNotEmpty(T string, Object errorMessage) {
        if (TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
        return string;
    }

    public static <T extends CharSequence> T checkStringNotEmpty(T string, String messageTemplate, Object... messageArgs) {
        if (TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException(String.format(messageTemplate, messageArgs));
        }
        return string;
    }

    @Deprecated
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    @Deprecated
    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, String messageTemplate, Object... messageArgs) {
        if (reference == null) {
            throw new NullPointerException(String.format(messageTemplate, messageArgs));
        }
        return reference;
    }

    public static void checkState(boolean expression) {
        checkState(expression, null);
    }

    public static void checkState(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalStateException(errorMessage);
        }
    }

    public static void checkState(boolean expression, String messageTemplate, Object... messageArgs) {
        if (!expression) {
            throw new IllegalStateException(String.format(messageTemplate, messageArgs));
        }
    }

    public static void checkCallAuthorization(boolean expression) {
        if (!expression) {
            throw new SecurityException("Calling identity is not authorized");
        }
    }

    public static void checkCallAuthorization(boolean expression, String message) {
        if (!expression) {
            throw new SecurityException(message);
        }
    }

    public static void checkCallAuthorization(boolean expression, String messageTemplate, Object... messageArgs) {
        if (!expression) {
            throw new SecurityException(String.format(messageTemplate, messageArgs));
        }
    }

    public static void checkCallingUser(boolean expression) {
        if (!expression) {
            throw new SecurityException("Calling user is not authorized");
        }
    }

    public static int checkFlagsArgument(int requestedFlags, int allowedFlags) {
        if ((requestedFlags & allowedFlags) != requestedFlags) {
            throw new IllegalArgumentException("Requested flags 0x" + Integer.toHexString(requestedFlags) + ", but only 0x" + Integer.toHexString(allowedFlags) + " are allowed");
        }
        return requestedFlags;
    }

    public static int checkArgumentNonnegative(int value, String errorMessage) {
        if (value < 0) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }

    public static int checkArgumentNonnegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static long checkArgumentNonnegative(long value) {
        if (value < 0) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static long checkArgumentNonnegative(long value, String errorMessage) {
        if (value < 0) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }

    public static int checkArgumentPositive(int value, String errorMessage) {
        if (value <= 0) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }

    public static float checkArgumentNonNegative(float value, String errorMessage) {
        if (value < 0.0f) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }

    public static float checkArgumentPositive(float value, String errorMessage) {
        if (value <= 0.0f) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }

    public static float checkArgumentFinite(float value, String valueName) {
        if (Float.isNaN(value)) {
            throw new IllegalArgumentException(valueName + " must not be NaN");
        }
        if (Float.isInfinite(value)) {
            throw new IllegalArgumentException(valueName + " must not be infinite");
        }
        return value;
    }

    public static float checkArgumentInRange(float value, float lower, float upper, String valueName) {
        if (Float.isNaN(value)) {
            throw new IllegalArgumentException(valueName + " must not be NaN");
        }
        if (value < lower) {
            throw new IllegalArgumentException(String.format("%s is out of range of [%f, %f] (too low)", valueName, Float.valueOf(lower), Float.valueOf(upper)));
        }
        if (value > upper) {
            throw new IllegalArgumentException(String.format("%s is out of range of [%f, %f] (too high)", valueName, Float.valueOf(lower), Float.valueOf(upper)));
        }
        return value;
    }

    public static double checkArgumentInRange(double value, double lower, double upper, String valueName) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException(valueName + " must not be NaN");
        }
        if (value < lower) {
            throw new IllegalArgumentException(String.format("%s is out of range of [%f, %f] (too low)", valueName, Double.valueOf(lower), Double.valueOf(upper)));
        }
        if (value > upper) {
            throw new IllegalArgumentException(String.format("%s is out of range of [%f, %f] (too high)", valueName, Double.valueOf(lower), Double.valueOf(upper)));
        }
        return value;
    }

    public static int checkArgumentInRange(int value, int lower, int upper, String valueName) {
        if (value < lower) {
            throw new IllegalArgumentException(String.format("%s is out of range of [%d, %d] (too low)", valueName, Integer.valueOf(lower), Integer.valueOf(upper)));
        }
        if (value > upper) {
            throw new IllegalArgumentException(String.format("%s is out of range of [%d, %d] (too high)", valueName, Integer.valueOf(lower), Integer.valueOf(upper)));
        }
        return value;
    }

    public static long checkArgumentInRange(long value, long lower, long upper, String valueName) {
        if (value < lower) {
            throw new IllegalArgumentException(String.format("%s is out of range of [%d, %d] (too low)", valueName, Long.valueOf(lower), Long.valueOf(upper)));
        }
        if (value > upper) {
            throw new IllegalArgumentException(String.format("%s is out of range of [%d, %d] (too high)", valueName, Long.valueOf(lower), Long.valueOf(upper)));
        }
        return value;
    }

    public static <T> T[] checkArrayElementsNotNull(T[] value, String valueName) {
        if (value == null) {
            throw new NullPointerException(valueName + " must not be null");
        }
        for (int i = 0; i < value.length; i++) {
            if (value[i] == null) {
                throw new NullPointerException(String.format("%s[%d] must not be null", valueName, Integer.valueOf(i)));
            }
        }
        return value;
    }

    public static <C extends Collection<T>, T> C checkCollectionElementsNotNull(C value, String valueName) {
        if (value == null) {
            throw new NullPointerException(valueName + " must not be null");
        }
        long ctr = 0;
        for (Object obj : value) {
            if (obj == null) {
                throw new NullPointerException(String.format("%s[%d] must not be null", valueName, Long.valueOf(ctr)));
            }
            ctr++;
        }
        return value;
    }

    public static <T> Collection<T> checkCollectionNotEmpty(Collection<T> value, String valueName) {
        if (value == null) {
            throw new NullPointerException(valueName + " must not be null");
        }
        if (value.isEmpty()) {
            throw new IllegalArgumentException(valueName + " is empty");
        }
        return value;
    }

    public static byte[] checkByteArrayNotEmpty(byte[] value, String valueName) {
        if (value == null) {
            throw new NullPointerException(valueName + " must not be null");
        }
        if (value.length == 0) {
            throw new IllegalArgumentException(valueName + " is empty");
        }
        return value;
    }

    public static String checkArgumentIsSupported(String[] supportedValues, String value) {
        checkNotNull(value);
        checkNotNull(supportedValues);
        if (!contains(supportedValues, value)) {
            throw new IllegalArgumentException(value + "is not supported " + Arrays.toString(supportedValues));
        }
        return value;
    }

    private static boolean contains(String[] values, String value) {
        if (values == null) {
            return false;
        }
        for (String str : values) {
            if (Objects.equals(value, str)) {
                return true;
            }
        }
        return false;
    }

    public static float[] checkArrayElementsInRange(float[] value, float lower, float upper, String valueName) {
        checkNotNull(value, "%s must not be null", valueName);
        for (int i = 0; i < value.length; i++) {
            float v = value[i];
            if (Float.isNaN(v)) {
                throw new IllegalArgumentException(valueName + "[" + i + "] must not be NaN");
            }
            if (v < lower) {
                throw new IllegalArgumentException(String.format("%s[%d] is out of range of [%f, %f] (too low)", valueName, Integer.valueOf(i), Float.valueOf(lower), Float.valueOf(upper)));
            }
            if (v > upper) {
                throw new IllegalArgumentException(String.format("%s[%d] is out of range of [%f, %f] (too high)", valueName, Integer.valueOf(i), Float.valueOf(lower), Float.valueOf(upper)));
            }
        }
        return value;
    }

    public static int[] checkArrayElementsInRange(int[] value, int lower, int upper, String valueName) {
        checkNotNull(value, "%s must not be null", valueName);
        for (int i = 0; i < value.length; i++) {
            int v = value[i];
            if (v < lower) {
                throw new IllegalArgumentException(String.format("%s[%d] is out of range of [%d, %d] (too low)", valueName, Integer.valueOf(i), Integer.valueOf(lower), Integer.valueOf(upper)));
            }
            if (v > upper) {
                throw new IllegalArgumentException(String.format("%s[%d] is out of range of [%d, %d] (too high)", valueName, Integer.valueOf(i), Integer.valueOf(lower), Integer.valueOf(upper)));
            }
        }
        return value;
    }
}
