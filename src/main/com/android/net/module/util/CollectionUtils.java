package com.android.net.module.util;

import android.util.ArrayMap;
import android.util.Pair;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes5.dex */
public final class CollectionUtils {
    private CollectionUtils() {
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static int[] toIntArray(Collection<Integer> list) {
        int[] array = new int[list.size()];
        int i = 0;
        for (Integer item : list) {
            array[i] = item.intValue();
            i++;
        }
        return array;
    }

    public static long[] toLongArray(Collection<Long> list) {
        long[] array = new long[list.size()];
        int i = 0;
        for (Long item : list) {
            array[i] = item.longValue();
            i++;
        }
        return array;
    }

    public static <T> boolean all(Collection<T> elem, Predicate<T> predicate) {
        for (T e : elem) {
            if (!predicate.test(e)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean any(Collection<T> elem, Predicate<T> predicate) {
        return indexOf(elem, predicate) >= 0;
    }

    public static <T> int indexOf(Collection<T> elem, Predicate<? super T> predicate) {
        int idx = 0;
        for (T e : elem) {
            if (predicate.test(e)) {
                return idx;
            }
            idx++;
        }
        return -1;
    }

    public static <T> boolean any(SparseArray<T> array, Predicate<T> predicate) {
        for (int i = 0; i < array.size(); i++) {
            if (predicate.test(array.valueAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(short[] array, short value) {
        if (array == null) {
            return false;
        }
        for (short s : array) {
            if (s == value) {
                return true;
            }
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

    public static int indexOfSubArray(byte[] haystack, byte[] needle) {
        for (int i = 0; i < (haystack.length - needle.length) + 1; i++) {
            boolean found = true;
            int j = 0;
            while (true) {
                if (j < needle.length) {
                    if (haystack[i + j] == needle[j]) {
                        j++;
                    } else {
                        found = false;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (found) {
                return i;
            }
        }
        return -1;
    }

    public static <T> ArrayList<T> filter(Collection<T> source, Predicate<T> test) {
        ArrayList<T> matches = new ArrayList<>();
        for (T e : source) {
            if (test.test(e)) {
                matches.add(e);
            }
        }
        return matches;
    }

    public static long total(long[] array) {
        long total = 0;
        if (array != null) {
            for (long value : array) {
                total += value;
            }
        }
        return total;
    }

    public static <T> boolean containsAny(Collection<T> haystack, Collection<? extends T> needles) {
        for (T needle : needles) {
            if (haystack.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean containsAll(Collection<T> haystack, Collection<? extends T> needles) {
        return haystack.containsAll(needles);
    }

    public static <T> T findFirst(Collection<T> haystack, Predicate<? super T> condition) {
        for (T needle : haystack) {
            if (condition.test(needle)) {
                return needle;
            }
        }
        return null;
    }

    public static <T> T findLast(List<T> haystack, Predicate<? super T> condition) {
        for (int i = haystack.size() - 1; i >= 0; i--) {
            T needle = haystack.get(i);
            if (condition.test(needle)) {
                return needle;
            }
        }
        return null;
    }

    public static <T> boolean contains(Collection<T> haystack, Predicate<? super T> condition) {
        return -1 != indexOf(haystack, condition);
    }

    public static <T, R> ArrayList<R> map(Collection<T> source, Function<? super T, ? extends R> transform) {
        ArrayList<R> dest = new ArrayList<>(source.size());
        for (T e : source) {
            dest.add(transform.apply(e));
        }
        return dest;
    }

    public static <T, R> ArrayList<Pair<T, R>> zip(List<T> first, List<R> second) {
        int size = first.size();
        if (size != second.size()) {
            throw new IllegalArgumentException("zip : collections must be the same size");
        }
        ArrayList<Pair<T, R>> dest = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            dest.add(new Pair<>(first.get(i), second.get(i)));
        }
        return dest;
    }

    public static <T, R> ArrayMap<T, R> assoc(List<T> keys, List<R> values) {
        int size = keys.size();
        if (size != values.size()) {
            throw new IllegalArgumentException("assoc : collections must be the same size");
        }
        ArrayMap<T, R> dest = new ArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            T key = keys.get(i);
            if (dest.containsKey(key)) {
                throw new IllegalArgumentException("assoc : keys may not contain the same value twice");
            }
            dest.put(key, values.get(i));
        }
        return dest;
    }
}
