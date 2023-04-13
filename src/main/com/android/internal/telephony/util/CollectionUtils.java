package com.android.internal.telephony.util;

import android.util.ArrayMap;
import android.util.Pair;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class CollectionUtils {
    private CollectionUtils() {
    }

    public static <T> boolean isEmpty(T[] tArr) {
        return tArr == null || tArr.length == 0;
    }

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static int[] toIntArray(Collection<Integer> collection) {
        int[] iArr = new int[collection.size()];
        int i = 0;
        for (Integer num : collection) {
            iArr[i] = num.intValue();
            i++;
        }
        return iArr;
    }

    public static long[] toLongArray(Collection<Long> collection) {
        long[] jArr = new long[collection.size()];
        int i = 0;
        for (Long l : collection) {
            jArr[i] = l.longValue();
            i++;
        }
        return jArr;
    }

    public static <T> boolean all(Collection<T> collection, Predicate<T> predicate) {
        for (T t : collection) {
            if (!predicate.test(t)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean any(Collection<T> collection, Predicate<T> predicate) {
        return indexOf(collection, predicate) >= 0;
    }

    public static <T> int indexOf(Collection<T> collection, Predicate<? super T> predicate) {
        int i = 0;
        for (T t : collection) {
            if (predicate.test(t)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static <T> boolean any(SparseArray<T> sparseArray, Predicate<T> predicate) {
        for (int i = 0; i < sparseArray.size(); i++) {
            if (predicate.test(sparseArray.valueAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(short[] sArr, short s) {
        if (sArr == null) {
            return false;
        }
        for (short s2 : sArr) {
            if (s2 == s) {
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

    public static int indexOfSubArray(byte[] bArr, byte[] bArr2) {
        int i = 0;
        while (true) {
            boolean z = true;
            if (i >= (bArr.length - bArr2.length) + 1) {
                return -1;
            }
            int i2 = 0;
            while (true) {
                if (i2 >= bArr2.length) {
                    break;
                } else if (bArr[i + i2] != bArr2[i2]) {
                    z = false;
                    break;
                } else {
                    i2++;
                }
            }
            if (z) {
                return i;
            }
            i++;
        }
    }

    public static <T> ArrayList<T> filter(Collection<T> collection, Predicate<T> predicate) {
        ArrayList<T> arrayList = new ArrayList<>();
        for (T t : collection) {
            if (predicate.test(t)) {
                arrayList.add(t);
            }
        }
        return arrayList;
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

    public static <T> boolean containsAny(Collection<T> collection, Collection<? extends T> collection2) {
        for (T t : collection2) {
            if (collection.contains(t)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean containsAll(Collection<T> collection, Collection<? extends T> collection2) {
        return collection.containsAll(collection2);
    }

    public static <T> T findFirst(Collection<T> collection, Predicate<? super T> predicate) {
        for (T t : collection) {
            if (predicate.test(t)) {
                return t;
            }
        }
        return null;
    }

    public static <T> T findLast(List<T> list, Predicate<? super T> predicate) {
        for (int size = list.size() - 1; size >= 0; size--) {
            T t = list.get(size);
            if (predicate.test(t)) {
                return t;
            }
        }
        return null;
    }

    public static <T> boolean contains(Collection<T> collection, Predicate<? super T> predicate) {
        return -1 != indexOf(collection, predicate);
    }

    public static <T, R> ArrayList<R> map(Collection<T> collection, Function<? super T, ? extends R> function) {
        ArrayList<R> arrayList = new ArrayList<>(collection.size());
        for (T t : collection) {
            arrayList.add(function.apply(t));
        }
        return arrayList;
    }

    public static <T, R> ArrayList<Pair<T, R>> zip(List<T> list, List<R> list2) {
        int size = list.size();
        if (size != list2.size()) {
            throw new IllegalArgumentException("zip : collections must be the same size");
        }
        ArrayList<Pair<T, R>> arrayList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            arrayList.add(new Pair<>(list.get(i), list2.get(i)));
        }
        return arrayList;
    }

    public static <T, R> ArrayMap<T, R> assoc(List<T> list, List<R> list2) {
        int size = list.size();
        if (size != list2.size()) {
            throw new IllegalArgumentException("assoc : collections must be the same size");
        }
        ArrayMap<T, R> arrayMap = new ArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            T t = list.get(i);
            if (arrayMap.containsKey(t)) {
                throw new IllegalArgumentException("assoc : keys may not contain the same value twice");
            }
            arrayMap.put(t, list2.get(i));
        }
        return arrayMap;
    }
}
