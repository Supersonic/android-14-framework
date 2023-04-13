package android.util;

import java.util.function.Consumer;
/* loaded from: classes3.dex */
public class SparseArrayMap<K, V> {
    private final SparseArray<ArrayMap<K, V>> mData = new SparseArray<>();

    /* loaded from: classes3.dex */
    public interface TriConsumer<K, V> {
        void accept(int i, K k, V v);
    }

    public V add(int key, K mapKey, V obj) {
        ArrayMap<K, V> data = this.mData.get(key);
        if (data == null) {
            data = new ArrayMap<>();
            this.mData.put(key, data);
        }
        return data.put(mapKey, obj);
    }

    public void clear() {
        for (int i = 0; i < this.mData.size(); i++) {
            this.mData.valueAt(i).clear();
        }
    }

    public boolean contains(int key, K mapKey) {
        return this.mData.contains(key) && this.mData.get(key).containsKey(mapKey);
    }

    public void delete(int key) {
        this.mData.delete(key);
    }

    public void deleteAt(int keyIndex) {
        this.mData.removeAt(keyIndex);
    }

    public V delete(int key, K mapKey) {
        ArrayMap<K, V> data = this.mData.get(key);
        if (data != null) {
            return data.remove(mapKey);
        }
        return null;
    }

    public void deleteAt(int keyIndex, int mapIndex) {
        this.mData.valueAt(keyIndex).removeAt(mapIndex);
    }

    public V get(int key, K mapKey) {
        ArrayMap<K, V> data = this.mData.get(key);
        if (data != null) {
            return data.get(mapKey);
        }
        return null;
    }

    public V getOrDefault(int key, K mapKey, V defaultValue) {
        ArrayMap<K, V> data;
        if (this.mData.contains(key) && (data = this.mData.get(key)) != null && data.containsKey(mapKey)) {
            return data.get(mapKey);
        }
        return defaultValue;
    }

    public int indexOfKey(int key) {
        return this.mData.indexOfKey(key);
    }

    public int indexOfKey(int key, K mapKey) {
        ArrayMap<K, V> data = this.mData.get(key);
        if (data != null) {
            return data.indexOfKey(mapKey);
        }
        return -1;
    }

    public int keyAt(int index) {
        return this.mData.keyAt(index);
    }

    public K keyAt(int keyIndex, int mapIndex) {
        return this.mData.valueAt(keyIndex).keyAt(mapIndex);
    }

    public int numMaps() {
        return this.mData.size();
    }

    public int numElementsForKey(int key) {
        ArrayMap<K, V> data = this.mData.get(key);
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public int numElementsForKeyAt(int keyIndex) {
        ArrayMap<K, V> data = this.mData.valueAt(keyIndex);
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public V valueAt(int keyIndex, int mapIndex) {
        return this.mData.valueAt(keyIndex).valueAt(mapIndex);
    }

    public void forEach(Consumer<V> consumer) {
        for (int i = numMaps() - 1; i >= 0; i--) {
            ArrayMap<K, V> data = this.mData.valueAt(i);
            for (int j = data.size() - 1; j >= 0; j--) {
                consumer.accept(data.valueAt(j));
            }
        }
    }

    public void forEach(TriConsumer<K, V> consumer) {
        for (int iIdx = numMaps() - 1; iIdx >= 0; iIdx--) {
            int i = this.mData.keyAt(iIdx);
            ArrayMap<K, V> data = this.mData.valueAt(iIdx);
            for (int kIdx = data.size() - 1; kIdx >= 0; kIdx--) {
                consumer.accept(i, data.keyAt(kIdx), data.valueAt(kIdx));
            }
        }
    }
}
