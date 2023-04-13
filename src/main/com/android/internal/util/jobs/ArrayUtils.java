package com.android.internal.util.jobs;

import android.util.ArraySet;
import dalvik.system.VMRuntime;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import libcore.util.EmptyArray;
/* loaded from: classes.dex */
public class ArrayUtils {
    public static final int CACHE_SIZE = 73;
    public static Object[] sCache = new Object[73];
    public static final File[] EMPTY_FILE = new File[0];

    public static byte[] newUnpaddedByteArray(int i) {
        return (byte[]) VMRuntime.getRuntime().newUnpaddedArray(Byte.TYPE, i);
    }

    public static char[] newUnpaddedCharArray(int i) {
        return (char[]) VMRuntime.getRuntime().newUnpaddedArray(Character.TYPE, i);
    }

    public static int[] newUnpaddedIntArray(int i) {
        return (int[]) VMRuntime.getRuntime().newUnpaddedArray(Integer.TYPE, i);
    }

    public static boolean[] newUnpaddedBooleanArray(int i) {
        return (boolean[]) VMRuntime.getRuntime().newUnpaddedArray(Boolean.TYPE, i);
    }

    public static long[] newUnpaddedLongArray(int i) {
        return (long[]) VMRuntime.getRuntime().newUnpaddedArray(Long.TYPE, i);
    }

    public static float[] newUnpaddedFloatArray(int i) {
        return (float[]) VMRuntime.getRuntime().newUnpaddedArray(Float.TYPE, i);
    }

    public static Object[] newUnpaddedObjectArray(int i) {
        return (Object[]) VMRuntime.getRuntime().newUnpaddedArray(Object.class, i);
    }

    public static <T> T[] newUnpaddedArray(Class<T> cls, int i) {
        return (T[]) ((Object[]) VMRuntime.getRuntime().newUnpaddedArray(cls, i));
    }

    public static boolean equals(byte[] bArr, byte[] bArr2, int i) {
        if (i >= 0) {
            if (bArr == bArr2) {
                return true;
            }
            if (bArr == null || bArr2 == null || bArr.length < i || bArr2.length < i) {
                return false;
            }
            for (int i2 = 0; i2 < i; i2++) {
                if (bArr[i2] != bArr2[i2]) {
                    return false;
                }
            }
            return true;
        }
        throw new IllegalArgumentException();
    }

    public static <T> T[] emptyArray(Class<T> cls) {
        if (cls == Object.class) {
            return (T[]) EmptyArray.OBJECT;
        }
        int hashCode = (cls.hashCode() & Integer.MAX_VALUE) % 73;
        Object obj = sCache[hashCode];
        if (obj == null || obj.getClass().getComponentType() != cls) {
            obj = Array.newInstance((Class<?>) cls, 0);
            sCache[hashCode] = obj;
        }
        return (T[]) ((Object[]) obj);
    }

    public static <T> T[] emptyIfNull(T[] tArr, Class<T> cls) {
        return tArr != null ? tArr : (T[]) emptyArray(cls);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static <T> boolean isEmpty(T[] tArr) {
        return tArr == null || tArr.length == 0;
    }

    public static boolean isEmpty(int[] iArr) {
        return iArr == null || iArr.length == 0;
    }

    public static boolean isEmpty(long[] jArr) {
        return jArr == null || jArr.length == 0;
    }

    public static boolean isEmpty(byte[] bArr) {
        return bArr == null || bArr.length == 0;
    }

    public static boolean isEmpty(boolean[] zArr) {
        return zArr == null || zArr.length == 0;
    }

    public static int size(Object[] objArr) {
        if (objArr == null) {
            return 0;
        }
        return objArr.length;
    }

    public static int size(Collection<?> collection) {
        if (collection == null) {
            return 0;
        }
        return collection.size();
    }

    public static int size(Map<?, ?> map) {
        if (map == null) {
            return 0;
        }
        return map.size();
    }

    public static <T> boolean contains(T[] tArr, T t) {
        return indexOf(tArr, t) != -1;
    }

    public static <T> int indexOf(T[] tArr, T t) {
        if (tArr == null) {
            return -1;
        }
        for (int i = 0; i < tArr.length; i++) {
            if (Objects.equals(tArr[i], t)) {
                return i;
            }
        }
        return -1;
    }

    public static <T> boolean containsAll(T[] tArr, T[] tArr2) {
        if (tArr2 == null) {
            return true;
        }
        for (T t : tArr2) {
            if (!contains(tArr, t)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean containsAny(T[] tArr, T[] tArr2) {
        if (tArr2 == null) {
            return false;
        }
        for (T t : tArr2) {
            if (contains(tArr, t)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(int[] iArr, int i) {
        if (iArr == null) {
            return false;
        }
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(long[] jArr, long j) {
        if (jArr == null) {
            return false;
        }
        for (long j2 : jArr) {
            if (j2 == j) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(char[] cArr, char c) {
        if (cArr == null) {
            return false;
        }
        for (char c2 : cArr) {
            if (c2 == c) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean containsAll(char[] cArr, char[] cArr2) {
        if (cArr2 == null) {
            return true;
        }
        for (char c : cArr2) {
            if (!contains(cArr, c)) {
                return false;
            }
        }
        return true;
    }

    public static long total(long[] jArr) {
        long j = 0;
        if (jArr != null) {
            for (long j2 : jArr) {
                j += j2;
            }
        }
        return j;
    }

    @Deprecated
    public static int[] convertToIntArray(List<Integer> list) {
        int[] iArr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            iArr[i] = list.get(i).intValue();
        }
        return iArr;
    }

    public static int[] convertToIntArray(ArraySet<Integer> arraySet) {
        int size = arraySet.size();
        int[] iArr = new int[size];
        for (int i = 0; i < size; i++) {
            iArr[i] = arraySet.valueAt(i).intValue();
        }
        return iArr;
    }

    public static long[] convertToLongArray(int[] iArr) {
        if (iArr == null) {
            return null;
        }
        long[] jArr = new long[iArr.length];
        for (int i = 0; i < iArr.length; i++) {
            jArr[i] = iArr[i];
        }
        return jArr;
    }

    public static <T> T[] concat(Class<T> cls, T[]... tArr) {
        if (tArr == null || tArr.length == 0) {
            return (T[]) createEmptyArray(cls);
        }
        int i = 0;
        for (T[] tArr2 : tArr) {
            if (tArr2 != null) {
                i += tArr2.length;
            }
        }
        if (i == 0) {
            return (T[]) createEmptyArray(cls);
        }
        T[] tArr3 = (T[]) ((Object[]) Array.newInstance((Class<?>) cls, i));
        int i2 = 0;
        for (T[] tArr4 : tArr) {
            if (tArr4 != null && tArr4.length != 0) {
                System.arraycopy(tArr4, 0, tArr3, i2, tArr4.length);
                i2 += tArr4.length;
            }
        }
        return tArr3;
    }

    public static <T> T[] createEmptyArray(Class<T> cls) {
        if (cls == String.class) {
            return (T[]) EmptyArray.STRING;
        }
        if (cls == Object.class) {
            return (T[]) EmptyArray.OBJECT;
        }
        return (T[]) ((Object[]) Array.newInstance((Class<?>) cls, 0));
    }

    public static byte[] concat(byte[]... bArr) {
        if (bArr == null) {
            return new byte[0];
        }
        int i = 0;
        for (byte[] bArr2 : bArr) {
            if (bArr2 != null) {
                i += bArr2.length;
            }
        }
        byte[] bArr3 = new byte[i];
        int i2 = 0;
        for (byte[] bArr4 : bArr) {
            if (bArr4 != null) {
                System.arraycopy(bArr4, 0, bArr3, i2, bArr4.length);
                i2 += bArr4.length;
            }
        }
        return bArr3;
    }

    public static <T> T[] appendElement(Class<T> cls, T[] tArr, T t) {
        return (T[]) appendElement(cls, tArr, t, false);
    }

    public static <T> T[] appendElement(Class<T> cls, T[] tArr, T t, boolean z) {
        T[] tArr2;
        int i = 0;
        if (tArr != null) {
            if (!z && contains(tArr, t)) {
                return tArr;
            }
            int length = tArr.length;
            tArr2 = (T[]) ((Object[]) Array.newInstance((Class<?>) cls, length + 1));
            System.arraycopy(tArr, 0, tArr2, 0, length);
            i = length;
        } else {
            tArr2 = (T[]) ((Object[]) Array.newInstance((Class<?>) cls, 1));
        }
        tArr2[i] = t;
        return tArr2;
    }

    public static <T> T[] removeElement(Class<T> cls, T[] tArr, T t) {
        if (tArr == null || !contains(tArr, t)) {
            return tArr;
        }
        int length = tArr.length;
        for (int i = 0; i < length; i++) {
            if (Objects.equals(tArr[i], t)) {
                if (length == 1) {
                    return null;
                }
                T[] tArr2 = (T[]) ((Object[]) Array.newInstance((Class<?>) cls, length - 1));
                System.arraycopy(tArr, 0, tArr2, 0, i);
                System.arraycopy(tArr, i + 1, tArr2, i, (length - i) - 1);
                return tArr2;
            }
        }
        return tArr;
    }

    public static int[] appendInt(int[] iArr, int i, boolean z) {
        if (iArr == null) {
            return new int[]{i};
        }
        int length = iArr.length;
        if (!z) {
            for (int i2 : iArr) {
                if (i2 == i) {
                    return iArr;
                }
            }
        }
        int[] iArr2 = new int[length + 1];
        System.arraycopy(iArr, 0, iArr2, 0, length);
        iArr2[length] = i;
        return iArr2;
    }

    public static int[] appendInt(int[] iArr, int i) {
        return appendInt(iArr, i, false);
    }

    public static int[] removeInt(int[] iArr, int i) {
        if (iArr == null) {
            return null;
        }
        int length = iArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            if (iArr[i2] == i) {
                int i3 = length - 1;
                int[] iArr2 = new int[i3];
                if (i2 > 0) {
                    System.arraycopy(iArr, 0, iArr2, 0, i2);
                }
                if (i2 < i3) {
                    System.arraycopy(iArr, i2 + 1, iArr2, i2, (length - i2) - 1);
                }
                return iArr2;
            }
        }
        return iArr;
    }

    public static String[] removeString(String[] strArr, String str) {
        if (strArr == null) {
            return null;
        }
        int length = strArr.length;
        for (int i = 0; i < length; i++) {
            if (Objects.equals(strArr[i], str)) {
                int i2 = length - 1;
                String[] strArr2 = new String[i2];
                if (i > 0) {
                    System.arraycopy(strArr, 0, strArr2, 0, i);
                }
                if (i < i2) {
                    System.arraycopy(strArr, i + 1, strArr2, i, (length - i) - 1);
                }
                return strArr2;
            }
        }
        return strArr;
    }

    public static long[] appendLong(long[] jArr, long j, boolean z) {
        if (jArr == null) {
            return new long[]{j};
        }
        int length = jArr.length;
        if (!z) {
            for (long j2 : jArr) {
                if (j2 == j) {
                    return jArr;
                }
            }
        }
        long[] jArr2 = new long[length + 1];
        System.arraycopy(jArr, 0, jArr2, 0, length);
        jArr2[length] = j;
        return jArr2;
    }

    public static long[] appendLong(long[] jArr, long j) {
        return appendLong(jArr, j, false);
    }

    public static long[] removeLong(long[] jArr, long j) {
        if (jArr == null) {
            return null;
        }
        int length = jArr.length;
        for (int i = 0; i < length; i++) {
            if (jArr[i] == j) {
                int i2 = length - 1;
                long[] jArr2 = new long[i2];
                if (i > 0) {
                    System.arraycopy(jArr, 0, jArr2, 0, i);
                }
                if (i < i2) {
                    System.arraycopy(jArr, i + 1, jArr2, i, (length - i) - 1);
                }
                return jArr2;
            }
        }
        return jArr;
    }

    public static long[] cloneOrNull(long[] jArr) {
        if (jArr != null) {
            return (long[]) jArr.clone();
        }
        return null;
    }

    public static <T> T[] cloneOrNull(T[] tArr) {
        if (tArr != null) {
            return (T[]) ((Object[]) tArr.clone());
        }
        return null;
    }

    public static <T> ArraySet<T> cloneOrNull(ArraySet<T> arraySet) {
        if (arraySet != null) {
            return new ArraySet<>(arraySet);
        }
        return null;
    }

    public static <T> ArraySet<T> add(ArraySet<T> arraySet, T t) {
        if (arraySet == null) {
            arraySet = new ArraySet<>();
        }
        arraySet.add(t);
        return arraySet;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> ArraySet<T> addAll(ArraySet<T> arraySet, Collection<T> collection) {
        if (arraySet == null) {
            arraySet = new ArraySet<>();
        }
        if (collection != 0) {
            arraySet.addAll((Collection<? extends T>) collection);
        }
        return arraySet;
    }

    public static <T> ArraySet<T> remove(ArraySet<T> arraySet, T t) {
        if (arraySet == null) {
            return null;
        }
        arraySet.remove(t);
        if (arraySet.isEmpty()) {
            return null;
        }
        return arraySet;
    }

    public static <T> ArrayList<T> add(ArrayList<T> arrayList, T t) {
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        arrayList.add(t);
        return arrayList;
    }

    public static <T> ArrayList<T> add(ArrayList<T> arrayList, int i, T t) {
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        arrayList.add(i, t);
        return arrayList;
    }

    public static <T> ArrayList<T> remove(ArrayList<T> arrayList, T t) {
        if (arrayList == null) {
            return null;
        }
        arrayList.remove(t);
        if (arrayList.isEmpty()) {
            return null;
        }
        return arrayList;
    }

    public static <T> boolean contains(Collection<T> collection, T t) {
        if (collection != null) {
            return collection.contains(t);
        }
        return false;
    }

    public static <T> T[] trimToSize(T[] tArr, int i) {
        if (tArr == null || i == 0) {
            return null;
        }
        return tArr.length == i ? tArr : (T[]) Arrays.copyOf(tArr, i);
    }

    public static <T> boolean referenceEquals(ArrayList<T> arrayList, ArrayList<T> arrayList2) {
        if (arrayList == arrayList2) {
            return true;
        }
        int size = arrayList.size();
        if (size != arrayList2.size()) {
            return false;
        }
        boolean z = false;
        for (int i = 0; i < size && !z; i++) {
            z |= arrayList.get(i) != arrayList2.get(i);
        }
        return !z;
    }

    public static <T> int unstableRemoveIf(ArrayList<T> arrayList, Predicate<T> predicate) {
        int i = 0;
        if (arrayList == null) {
            return 0;
        }
        int size = arrayList.size();
        int i2 = size - 1;
        int i3 = i2;
        while (i <= i3) {
            while (i < size && !predicate.test(arrayList.get(i))) {
                i++;
            }
            while (i3 > i && predicate.test(arrayList.get(i3))) {
                i3--;
            }
            if (i >= i3) {
                break;
            }
            Collections.swap(arrayList, i, i3);
            i++;
            i3--;
        }
        while (i2 >= i) {
            arrayList.remove(i2);
            i2--;
        }
        return size - i;
    }

    public static int[] defeatNullable(int[] iArr) {
        return iArr != null ? iArr : EmptyArray.INT;
    }

    public static String[] defeatNullable(String[] strArr) {
        return strArr != null ? strArr : EmptyArray.STRING;
    }

    public static File[] defeatNullable(File[] fileArr) {
        return fileArr != null ? fileArr : EMPTY_FILE;
    }

    public static void checkBounds(int i, int i2) {
        if (i2 < 0 || i <= i2) {
            throw new ArrayIndexOutOfBoundsException("length=" + i + "; index=" + i2);
        }
    }

    public static void throwsIfOutOfBounds(int i, int i2, int i3) {
        if (i < 0) {
            throw new ArrayIndexOutOfBoundsException("Negative length: " + i);
        } else if ((i2 | i3) < 0 || i2 > i - i3) {
            throw new ArrayIndexOutOfBoundsException("length=" + i + "; regionStart=" + i2 + "; regionLength=" + i3);
        }
    }

    public static <T> T[] filterNotNull(T[] tArr, IntFunction<T[]> intFunction) {
        int size = size(tArr);
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            if (tArr[i2] == null) {
                i++;
            }
        }
        if (i == 0) {
            return tArr;
        }
        T[] apply = intFunction.apply(size - i);
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            T t = tArr[i4];
            if (t != null) {
                apply[i3] = t;
                i3++;
            }
        }
        return apply;
    }

    public static <T> T[] filter(T[] tArr, IntFunction<T[]> intFunction, Predicate<T> predicate) {
        if (isEmpty(tArr)) {
            return tArr;
        }
        int size = size(tArr);
        boolean[] zArr = new boolean[size];
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            boolean test = predicate.test(tArr[i2]);
            zArr[i2] = test;
            if (test) {
                i++;
            }
        }
        if (i == tArr.length) {
            return tArr;
        }
        T[] apply = intFunction.apply(i);
        if (i == 0) {
            return apply;
        }
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            if (zArr[i4]) {
                apply[i3] = tArr[i4];
                i3++;
            }
        }
        return apply;
    }

    public static boolean startsWith(byte[] bArr, byte[] bArr2) {
        if (bArr == null || bArr2 == null || bArr.length < bArr2.length) {
            return false;
        }
        for (int i = 0; i < bArr2.length; i++) {
            if (bArr[i] != bArr2[i]) {
                return false;
            }
        }
        return true;
    }

    public static <T> T find(T[] tArr, Predicate<T> predicate) {
        if (isEmpty(tArr)) {
            return null;
        }
        for (T t : tArr) {
            if (predicate.test(t)) {
                return t;
            }
        }
        return null;
    }

    public static String deepToString(Object obj) {
        if (obj != null && obj.getClass().isArray()) {
            if (obj.getClass() == boolean[].class) {
                return Arrays.toString((boolean[]) obj);
            }
            if (obj.getClass() == byte[].class) {
                return Arrays.toString((byte[]) obj);
            }
            if (obj.getClass() == char[].class) {
                return Arrays.toString((char[]) obj);
            }
            if (obj.getClass() == double[].class) {
                return Arrays.toString((double[]) obj);
            }
            if (obj.getClass() == float[].class) {
                return Arrays.toString((float[]) obj);
            }
            if (obj.getClass() == int[].class) {
                return Arrays.toString((int[]) obj);
            }
            if (obj.getClass() == long[].class) {
                return Arrays.toString((long[]) obj);
            }
            if (obj.getClass() == short[].class) {
                return Arrays.toString((short[]) obj);
            }
            return Arrays.deepToString((Object[]) obj);
        }
        return String.valueOf(obj);
    }

    public static <T> T getOrNull(T[] tArr, int i) {
        if (tArr == null || tArr.length <= i) {
            return null;
        }
        return tArr[i];
    }

    public static <T> T firstOrNull(T[] tArr) {
        if (tArr.length > 0) {
            return tArr[0];
        }
        return null;
    }

    public static <T> List<T> toList(T[] tArr) {
        ArrayList arrayList = new ArrayList(tArr.length);
        for (T t : tArr) {
            arrayList.add(t);
        }
        return arrayList;
    }
}
