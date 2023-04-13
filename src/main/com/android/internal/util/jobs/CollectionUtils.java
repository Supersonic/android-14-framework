package com.android.internal.util.jobs;

import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.ExceptionUtils;
import com.android.internal.util.jobs.FunctionalUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class CollectionUtils {
    public static <T> boolean contains(Collection<T> collection, T t) {
        return collection != null && collection.contains(t);
    }

    public static <T> List<T> filter(List<T> list, Predicate<? super T> predicate) {
        ArrayList arrayList = null;
        for (int i = 0; i < size(list); i++) {
            T t = list.get(i);
            if (predicate.test(t)) {
                arrayList = ArrayUtils.add(arrayList, t);
            }
        }
        return emptyIfNull(arrayList);
    }

    public static <T> Set<T> filter(Set<T> set, Predicate<? super T> predicate) {
        if (set == null || set.size() == 0) {
            return Collections.emptySet();
        }
        ArraySet arraySet = null;
        if (set instanceof ArraySet) {
            ArraySet arraySet2 = (ArraySet) set;
            int size = arraySet2.size();
            for (int i = 0; i < size; i++) {
                Object obj = (Object) arraySet2.valueAt(i);
                if (predicate.test(obj)) {
                    arraySet = ArrayUtils.add(arraySet, obj);
                }
            }
        } else {
            for (Object obj2 : set) {
                if (predicate.test(obj2)) {
                    arraySet = ArrayUtils.add(arraySet, obj2);
                }
            }
        }
        return emptyIfNull(arraySet);
    }

    public static <T> void addIf(List<T> list, Collection<? super T> collection, Predicate<? super T> predicate) {
        for (int i = 0; i < size(list); i++) {
            T t = list.get(i);
            if (predicate.test(t)) {
                collection.add(t);
            }
        }
    }

    public static <I, O> List<O> map(List<I> list, Function<? super I, ? extends O> function) {
        if (isEmpty(list)) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            arrayList.add(function.apply(list.get(i)));
        }
        return arrayList;
    }

    public static <I, O> Set<O> map(Set<I> set, Function<? super I, ? extends O> function) {
        if (isEmpty(set)) {
            return Collections.emptySet();
        }
        ArraySet arraySet = new ArraySet();
        if (set instanceof ArraySet) {
            ArraySet arraySet2 = (ArraySet) set;
            int size = arraySet2.size();
            for (int i = 0; i < size; i++) {
                arraySet.add(function.apply((Object) arraySet2.valueAt(i)));
            }
        } else {
            for (I i2 : set) {
                arraySet.add(function.apply(i2));
            }
        }
        return arraySet;
    }

    public static <I, O> List<O> mapNotNull(List<I> list, Function<? super I, ? extends O> function) {
        if (isEmpty(list)) {
            return Collections.emptyList();
        }
        List list2 = null;
        for (int i = 0; i < list.size(); i++) {
            O apply = function.apply(list.get(i));
            if (apply != null) {
                list2 = add(list2, apply);
            }
        }
        return emptyIfNull(list2);
    }

    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    public static <T> Set<T> emptyIfNull(Set<T> set) {
        return set == null ? Collections.emptySet() : set;
    }

    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        return map == null ? Collections.emptyMap() : map;
    }

    public static int size(Collection<?> collection) {
        if (collection != null) {
            return collection.size();
        }
        return 0;
    }

    public static int size(Map<?, ?> map) {
        if (map != null) {
            return map.size();
        }
        return 0;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return size(collection) == 0;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return size(map) == 0;
    }

    public static <T> List<T> filter(List<?> list, Class<T> cls) {
        if (isEmpty(list)) {
            return Collections.emptyList();
        }
        ArrayList arrayList = null;
        for (int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            if (cls.isInstance(obj)) {
                arrayList = ArrayUtils.add(arrayList, obj);
            }
        }
        return emptyIfNull(arrayList);
    }

    public static <T> boolean any(List<T> list, Predicate<T> predicate) {
        return find(list, predicate) != null;
    }

    public static <T> boolean any(Set<T> set, Predicate<T> predicate) {
        return find(set, predicate) != null;
    }

    public static <T> T find(List<T> list, Predicate<T> predicate) {
        if (isEmpty(list)) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            if (predicate.test(t)) {
                return t;
            }
        }
        return null;
    }

    public static <T> T find(Set<T> set, Predicate<T> predicate) {
        int size;
        if (set == null || predicate == null || (size = set.size()) == 0) {
            return null;
        }
        try {
            if (set instanceof ArraySet) {
                ArraySet arraySet = (ArraySet) set;
                for (int i = 0; i < size; i++) {
                    T t = (T) arraySet.valueAt(i);
                    if (predicate.test(t)) {
                        return t;
                    }
                }
            } else {
                for (T t2 : set) {
                    if (predicate.test(t2)) {
                        return t2;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw ExceptionUtils.propagate(e);
        }
    }

    public static <T> List<T> add(List<T> list, T t) {
        if (list == null || list == Collections.emptyList()) {
            list = new ArrayList<>();
        }
        list.add(t);
        return list;
    }

    public static <T> List<T> add(List<T> list, int i, T t) {
        if (list == null || list == Collections.emptyList()) {
            list = new ArrayList<>();
        }
        list.add(i, t);
        return list;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> Set<T> addAll(Set<T> set, Collection<T> collection) {
        if (isEmpty((Collection<?>) collection)) {
            return set != null ? set : Collections.emptySet();
        }
        if (set == null || set == Collections.emptySet()) {
            set = new ArraySet<>();
        }
        set.addAll(collection);
        return set;
    }

    public static <T> Set<T> add(Set<T> set, T t) {
        if (set == null || set == Collections.emptySet()) {
            set = new ArraySet<>();
        }
        set.add(t);
        return set;
    }

    public static <K, V> Map<K, V> add(Map<K, V> map, K k, V v) {
        if (map == null || map == Collections.emptyMap()) {
            map = new ArrayMap<>();
        }
        map.put(k, v);
        return map;
    }

    public static <T> List<T> remove(List<T> list, T t) {
        if (isEmpty(list)) {
            return emptyIfNull(list);
        }
        list.remove(t);
        return list;
    }

    public static <T> Set<T> remove(Set<T> set, T t) {
        if (isEmpty(set)) {
            return emptyIfNull(set);
        }
        set.remove(t);
        return set;
    }

    public static <T> List<T> copyOf(List<T> list) {
        return isEmpty(list) ? Collections.emptyList() : new ArrayList(list);
    }

    public static <T> Set<T> copyOf(Set<T> set) {
        return isEmpty(set) ? Collections.emptySet() : new ArraySet(set);
    }

    public static <T> Set<T> toSet(Collection<T> collection) {
        return isEmpty((Collection<?>) collection) ? Collections.emptySet() : new ArraySet(collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> void forEach(Set<T> set, FunctionalUtils.ThrowingConsumer<T> throwingConsumer) {
        int size;
        if (set == null || throwingConsumer == 0 || (size = set.size()) == 0) {
            return;
        }
        try {
            if (set instanceof ArraySet) {
                ArraySet arraySet = (ArraySet) set;
                for (int i = 0; i < size; i++) {
                    throwingConsumer.acceptOrThrow(arraySet.valueAt(i));
                }
                return;
            }
            for (T t : set) {
                throwingConsumer.acceptOrThrow(t);
            }
        } catch (Exception e) {
            throw ExceptionUtils.propagate(e);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <K, V> void forEach(Map<K, V> map, BiConsumer<K, V> biConsumer) {
        int size;
        if (map == null || biConsumer == 0 || (size = map.size()) == 0) {
            return;
        }
        if (map instanceof ArrayMap) {
            ArrayMap arrayMap = (ArrayMap) map;
            for (int i = 0; i < size; i++) {
                biConsumer.accept(arrayMap.keyAt(i), arrayMap.valueAt(i));
            }
            return;
        }
        for (K k : map.keySet()) {
            biConsumer.accept(k, map.get(k));
        }
    }

    public static <T> T firstOrNull(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public static <T> T firstOrNull(Collection<T> collection) {
        if (isEmpty((Collection<?>) collection)) {
            return null;
        }
        return collection.iterator().next();
    }

    public static <T> List<T> singletonOrEmpty(T t) {
        return t == null ? Collections.emptyList() : Collections.singletonList(t);
    }
}
