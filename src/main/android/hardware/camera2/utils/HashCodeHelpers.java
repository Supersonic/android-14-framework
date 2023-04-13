package android.hardware.camera2.utils;
/* loaded from: classes.dex */
public final class HashCodeHelpers {
    public static int hashCode(int... array) {
        if (array == null) {
            return 0;
        }
        int h = 1;
        for (int x : array) {
            h = ((h << 5) - h) ^ x;
        }
        return h;
    }

    public static int hashCode(float... array) {
        if (array == null) {
            return 0;
        }
        int h = 1;
        for (float f : array) {
            int x = Float.floatToIntBits(f);
            h = ((h << 5) - h) ^ x;
        }
        return h;
    }

    public static <T> int hashCodeGeneric(T... array) {
        if (array == null) {
            return 0;
        }
        int h = 1;
        int length = array.length;
        for (int i = 0; i < length; i++) {
            T o = array[i];
            int x = o == null ? 0 : o.hashCode();
            h = ((h << 5) - h) ^ x;
        }
        return h;
    }
}
