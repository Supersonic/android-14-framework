package com.android.internal.telephony.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class ArrayUtils {
    private ArrayUtils() {
    }

    public static <T> T[] appendElement(Class<T> kind, T[] array, T element) {
        return (T[]) appendElement(kind, array, element, false);
    }

    public static <T> T[] appendElement(Class<T> kind, T[] array, T element, boolean allowDuplicates) {
        int end;
        T[] result;
        if (array != null) {
            if (!allowDuplicates && contains(array, element)) {
                return array;
            }
            end = array.length;
            result = (T[]) ((Object[]) Array.newInstance((Class<?>) kind, end + 1));
            System.arraycopy(array, 0, result, 0, end);
        } else {
            end = 0;
            result = (T[]) ((Object[]) Array.newInstance((Class<?>) kind, 1));
        }
        result[end] = element;
        return result;
    }

    public static <T> T[] concatElements(Class<T> kind, T[]... arrays) {
        if (arrays == null || arrays.length == 0) {
            return (T[]) createEmptyArray(kind);
        }
        int totalLength = 0;
        for (T[] item : arrays) {
            if (item != null) {
                totalLength += item.length;
            }
        }
        if (totalLength == 0) {
            return (T[]) createEmptyArray(kind);
        }
        T[] all = (T[]) ((Object[]) Array.newInstance((Class<?>) kind, totalLength));
        int pos = 0;
        for (T[] item2 : arrays) {
            if (item2 != null && item2.length != 0) {
                System.arraycopy(item2, 0, all, pos, item2.length);
                pos += item2.length;
            }
        }
        return all;
    }

    private static <T> T[] createEmptyArray(Class<T> kind) {
        if (kind == String.class) {
            return (T[]) EmptyArray.STRING;
        }
        if (kind == Object.class) {
            return (T[]) EmptyArray.OBJECT;
        }
        return (T[]) ((Object[]) Array.newInstance((Class<?>) kind, 0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class EmptyArray {
        public static final Object[] OBJECT = new Object[0];
        public static final String[] STRING = new String[0];

        private EmptyArray() {
        }
    }

    public static boolean contains(char[] array, char value) {
        if (array == null) {
            return false;
        }
        for (char element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean contains(Collection<T> cur, T val) {
        if (cur != null) {
            return cur.contains(val);
        }
        return false;
    }

    public static boolean contains(int[] array, int value) {
        if (array == null) {
            return false;
        }
        for (int element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(long[] array, long value) {
        if (array == null) {
            return false;
        }
        for (long element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean contains(T[] array, T value) {
        return indexOf(array, value) != -1;
    }

    public static <T> int indexOf(T[] array, T value) {
        if (array == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isEmpty(Collection<?> array) {
        return array == null || array.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }
}
