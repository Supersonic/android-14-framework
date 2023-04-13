package com.android.server.utils.quota;

import android.util.ArrayMap;
import android.util.SparseArrayMap;
import java.util.function.Consumer;
import java.util.function.Function;
/* loaded from: classes2.dex */
public class UptcMap<T> {
    public final SparseArrayMap<String, ArrayMap<String, T>> mData = new SparseArrayMap<>();

    /* loaded from: classes2.dex */
    public interface UptcDataConsumer<D> {
        void accept(int i, String str, String str2, D d);
    }

    public void add(int i, String str, String str2, T t) {
        ArrayMap arrayMap = (ArrayMap) this.mData.get(i, str);
        if (arrayMap == null) {
            arrayMap = new ArrayMap();
            this.mData.add(i, str, arrayMap);
        }
        arrayMap.put(str2, t);
    }

    public void clear() {
        this.mData.clear();
    }

    public boolean contains(int i, String str, String str2) {
        ArrayMap arrayMap = (ArrayMap) this.mData.get(i, str);
        return arrayMap != null && arrayMap.containsKey(str2);
    }

    public void delete(int i) {
        this.mData.delete(i);
    }

    public ArrayMap<String, T> delete(int i, String str) {
        return (ArrayMap) this.mData.delete(i, str);
    }

    public T get(int i, String str, String str2) {
        ArrayMap arrayMap = (ArrayMap) this.mData.get(i, str);
        if (arrayMap != null) {
            return (T) arrayMap.get(str2);
        }
        return null;
    }

    public T getOrCreate(int i, String str, String str2, Function<Void, T> function) {
        ArrayMap arrayMap = (ArrayMap) this.mData.get(i, str);
        if (arrayMap == null || !arrayMap.containsKey(str2)) {
            T apply = function.apply(null);
            add(i, str, str2, apply);
            return apply;
        }
        return (T) arrayMap.get(str2);
    }

    public final int getUserIdAtIndex(int i) {
        return this.mData.keyAt(i);
    }

    public final String getPackageNameAtIndex(int i, int i2) {
        return (String) this.mData.keyAt(i, i2);
    }

    public final String getTagAtIndex(int i, int i2, int i3) {
        return (String) ((ArrayMap) this.mData.valueAt(i, i2)).keyAt(i3);
    }

    public int userCount() {
        return this.mData.numMaps();
    }

    public int packageCountForUser(int i) {
        return this.mData.numElementsForKey(i);
    }

    public int tagCountForUserAndPackage(int i, String str) {
        ArrayMap arrayMap = (ArrayMap) this.mData.get(i, str);
        if (arrayMap != null) {
            return arrayMap.size();
        }
        return 0;
    }

    public void forEach(final Consumer<T> consumer) {
        this.mData.forEach(new Consumer() { // from class: com.android.server.utils.quota.UptcMap$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                UptcMap.lambda$forEach$0(consumer, (ArrayMap) obj);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ void lambda$forEach$0(Consumer consumer, ArrayMap arrayMap) {
        for (int size = arrayMap.size() - 1; size >= 0; size--) {
            consumer.accept(arrayMap.valueAt(size));
        }
    }

    public void forEach(UptcDataConsumer<T> uptcDataConsumer) {
        int userCount = userCount();
        for (int i = 0; i < userCount; i++) {
            int userIdAtIndex = getUserIdAtIndex(i);
            int packageCountForUser = packageCountForUser(userIdAtIndex);
            for (int i2 = 0; i2 < packageCountForUser; i2++) {
                String packageNameAtIndex = getPackageNameAtIndex(i, i2);
                int tagCountForUserAndPackage = tagCountForUserAndPackage(userIdAtIndex, packageNameAtIndex);
                for (int i3 = 0; i3 < tagCountForUserAndPackage; i3++) {
                    String tagAtIndex = getTagAtIndex(i, i2, i3);
                    uptcDataConsumer.accept(userIdAtIndex, packageNameAtIndex, tagAtIndex, get(userIdAtIndex, packageNameAtIndex, tagAtIndex));
                }
            }
        }
    }
}
