package android.util;
/* loaded from: classes3.dex */
public class SparseSetArray<T> {
    private final SparseArray<ArraySet<T>> mData;

    public SparseSetArray() {
        this.mData = new SparseArray<>();
    }

    public SparseSetArray(SparseSetArray<T> src) {
        int arraySize = src.size();
        this.mData = new SparseArray<>(arraySize);
        for (int i = 0; i < arraySize; i++) {
            int key = src.keyAt(i);
            ArraySet<T> set = src.get(key);
            addAll(key, set);
        }
    }

    public boolean add(int n, T value) {
        ArraySet<T> set = this.mData.get(n);
        if (set == null) {
            set = new ArraySet<>();
            this.mData.put(n, set);
        }
        if (set.contains(value)) {
            return false;
        }
        set.add(value);
        return true;
    }

    public void addAll(int n, ArraySet<T> values) {
        ArraySet<T> set = this.mData.get(n);
        if (set == null) {
            this.mData.put(n, new ArraySet<>(values));
            return;
        }
        set.addAll((ArraySet<? extends T>) values);
    }

    public void clear() {
        this.mData.clear();
    }

    public boolean contains(int n, T value) {
        ArraySet<T> set = this.mData.get(n);
        if (set == null) {
            return false;
        }
        return set.contains(value);
    }

    public ArraySet<T> get(int n) {
        return this.mData.get(n);
    }

    public boolean remove(int n, T value) {
        ArraySet<T> set = this.mData.get(n);
        if (set == null) {
            return false;
        }
        boolean ret = set.remove(value);
        if (set.size() == 0) {
            this.mData.remove(n);
        }
        return ret;
    }

    public void remove(int n) {
        this.mData.remove(n);
    }

    public int size() {
        return this.mData.size();
    }

    public int keyAt(int index) {
        return this.mData.keyAt(index);
    }

    public int sizeAt(int index) {
        ArraySet<T> set = this.mData.valueAt(index);
        if (set == null) {
            return 0;
        }
        return set.size();
    }

    public T valueAt(int intIndex, int valueIndex) {
        return this.mData.valueAt(intIndex).valueAt(valueIndex);
    }

    public ArraySet<T> valuesAt(int intIndex) {
        return this.mData.valueAt(intIndex);
    }
}
