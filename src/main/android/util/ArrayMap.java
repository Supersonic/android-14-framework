package android.util;

import com.android.internal.util.ArrayUtils;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import libcore.util.EmptyArray;
/* loaded from: classes3.dex */
public final class ArrayMap<K, V> implements Map<K, V> {
    private static final int BASE_SIZE = 4;
    private static final int CACHE_SIZE = 10;
    private static final boolean CONCURRENT_MODIFICATION_EXCEPTIONS = true;
    private static final boolean DEBUG = false;
    private static final String TAG = "ArrayMap";
    static Object[] mBaseCache;
    static int mBaseCacheSize;
    static Object[] mTwiceBaseCache;
    static int mTwiceBaseCacheSize;
    Object[] mArray;
    private MapCollections<K, V> mCollections;
    int[] mHashes;
    private final boolean mIdentityHashCode;
    int mSize;
    static final int[] EMPTY_IMMUTABLE_INTS = new int[0];
    public static final ArrayMap EMPTY = new ArrayMap(-1);
    private static final Object sBaseCacheLock = new Object();
    private static final Object sTwiceBaseCacheLock = new Object();

    private static int binarySearchHashes(int[] hashes, int N, int hash) {
        try {
            return ContainerHelpers.binarySearch(hashes, N, hash);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ConcurrentModificationException();
        }
    }

    int indexOf(Object key, int hash) {
        int N = this.mSize;
        if (N == 0) {
            return -1;
        }
        int index = binarySearchHashes(this.mHashes, N, hash);
        if (index < 0) {
            return index;
        }
        if (key.equals(this.mArray[index << 1])) {
            return index;
        }
        int end = index + 1;
        while (end < N && this.mHashes[end] == hash) {
            if (key.equals(this.mArray[end << 1])) {
                return end;
            }
            end++;
        }
        for (int i = index - 1; i >= 0 && this.mHashes[i] == hash; i--) {
            if (key.equals(this.mArray[i << 1])) {
                return i;
            }
        }
        int i2 = ~end;
        return i2;
    }

    int indexOfNull() {
        int N = this.mSize;
        if (N == 0) {
            return -1;
        }
        int index = binarySearchHashes(this.mHashes, N, 0);
        if (index < 0) {
            return index;
        }
        if (this.mArray[index << 1] == null) {
            return index;
        }
        int end = index + 1;
        while (end < N && this.mHashes[end] == 0) {
            if (this.mArray[end << 1] == null) {
                return end;
            }
            end++;
        }
        for (int i = index - 1; i >= 0 && this.mHashes[i] == 0; i--) {
            if (this.mArray[i << 1] == null) {
                return i;
            }
        }
        int i2 = ~end;
        return i2;
    }

    private void allocArrays(int size) {
        int[] iArr;
        int[] iArr2;
        if (this.mHashes == EMPTY_IMMUTABLE_INTS) {
            throw new UnsupportedOperationException("ArrayMap is immutable");
        }
        if (size == 8) {
            synchronized (sTwiceBaseCacheLock) {
                Object[] array = mTwiceBaseCache;
                if (array != null) {
                    this.mArray = array;
                    try {
                        mTwiceBaseCache = (Object[]) array[0];
                        iArr2 = (int[]) array[1];
                        this.mHashes = iArr2;
                    } catch (ClassCastException e) {
                    }
                    if (iArr2 != null) {
                        array[1] = null;
                        array[0] = null;
                        mTwiceBaseCacheSize--;
                        return;
                    }
                    Slog.wtf(TAG, "Found corrupt ArrayMap cache: [0]=" + array[0] + " [1]=" + array[1]);
                    mTwiceBaseCache = null;
                    mTwiceBaseCacheSize = 0;
                }
            }
        } else if (size == 4) {
            synchronized (sBaseCacheLock) {
                Object[] array2 = mBaseCache;
                if (array2 != null) {
                    this.mArray = array2;
                    try {
                        mBaseCache = (Object[]) array2[0];
                        iArr = (int[]) array2[1];
                        this.mHashes = iArr;
                    } catch (ClassCastException e2) {
                    }
                    if (iArr != null) {
                        array2[1] = null;
                        array2[0] = null;
                        mBaseCacheSize--;
                        return;
                    }
                    Slog.wtf(TAG, "Found corrupt ArrayMap cache: [0]=" + array2[0] + " [1]=" + array2[1]);
                    mBaseCache = null;
                    mBaseCacheSize = 0;
                }
            }
        }
        this.mHashes = new int[size];
        this.mArray = new Object[size << 1];
    }

    private static void freeArrays(int[] hashes, Object[] array, int size) {
        if (hashes.length == 8) {
            synchronized (sTwiceBaseCacheLock) {
                if (mTwiceBaseCacheSize < 10) {
                    array[0] = mTwiceBaseCache;
                    array[1] = hashes;
                    for (int i = (size << 1) - 1; i >= 2; i--) {
                        array[i] = null;
                    }
                    mTwiceBaseCache = array;
                    mTwiceBaseCacheSize++;
                }
            }
        } else if (hashes.length == 4) {
            synchronized (sBaseCacheLock) {
                if (mBaseCacheSize < 10) {
                    array[0] = mBaseCache;
                    array[1] = hashes;
                    for (int i2 = (size << 1) - 1; i2 >= 2; i2--) {
                        array[i2] = null;
                    }
                    mBaseCache = array;
                    mBaseCacheSize++;
                }
            }
        }
    }

    public ArrayMap() {
        this(0, false);
    }

    public ArrayMap(int capacity) {
        this(capacity, false);
    }

    public ArrayMap(int capacity, boolean identityHashCode) {
        this.mIdentityHashCode = identityHashCode;
        if (capacity < 0) {
            this.mHashes = EMPTY_IMMUTABLE_INTS;
            this.mArray = EmptyArray.OBJECT;
        } else if (capacity == 0) {
            this.mHashes = EmptyArray.INT;
            this.mArray = EmptyArray.OBJECT;
        } else {
            allocArrays(capacity);
        }
        this.mSize = 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public ArrayMap(ArrayMap<K, V> map) {
        this();
        if (map != 0) {
            putAll((ArrayMap) map);
        }
    }

    @Override // java.util.Map
    public void clear() {
        if (this.mSize > 0) {
            int[] ohashes = this.mHashes;
            Object[] oarray = this.mArray;
            int osize = this.mSize;
            this.mHashes = EmptyArray.INT;
            this.mArray = EmptyArray.OBJECT;
            this.mSize = 0;
            freeArrays(ohashes, oarray, osize);
        }
        if (this.mSize > 0) {
            throw new ConcurrentModificationException();
        }
    }

    public void erase() {
        int i = this.mSize;
        if (i > 0) {
            int N = i << 1;
            Object[] array = this.mArray;
            for (int i2 = 0; i2 < N; i2++) {
                array[i2] = null;
            }
            this.mSize = 0;
        }
    }

    public void ensureCapacity(int minimumCapacity) {
        int osize = this.mSize;
        if (this.mHashes.length < minimumCapacity) {
            int[] ohashes = this.mHashes;
            Object[] oarray = this.mArray;
            allocArrays(minimumCapacity);
            if (this.mSize > 0) {
                System.arraycopy(ohashes, 0, this.mHashes, 0, osize);
                System.arraycopy(oarray, 0, this.mArray, 0, osize << 1);
            }
            freeArrays(ohashes, oarray, osize);
        }
        if (this.mSize != osize) {
            throw new ConcurrentModificationException();
        }
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return indexOfKey(key) >= 0;
    }

    public int indexOfKey(Object key) {
        if (key == null) {
            return indexOfNull();
        }
        return indexOf(key, this.mIdentityHashCode ? System.identityHashCode(key) : key.hashCode());
    }

    public int indexOfValue(Object value) {
        int N = this.mSize * 2;
        Object[] array = this.mArray;
        if (value == null) {
            for (int i = 1; i < N; i += 2) {
                if (array[i] == null) {
                    return i >> 1;
                }
            }
            return -1;
        }
        for (int i2 = 1; i2 < N; i2 += 2) {
            if (value.equals(array[i2])) {
                return i2 >> 1;
            }
        }
        return -1;
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return indexOfValue(value) >= 0;
    }

    @Override // java.util.Map
    public V get(Object key) {
        int index = indexOfKey(key);
        if (index >= 0) {
            return (V) this.mArray[(index << 1) + 1];
        }
        return null;
    }

    public K keyAt(int index) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return (K) this.mArray[index << 1];
    }

    public V valueAt(int index) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return (V) this.mArray[(index << 1) + 1];
    }

    public V setValueAt(int index, V value) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        int index2 = (index << 1) + 1;
        Object[] objArr = this.mArray;
        V old = (V) objArr[index2];
        objArr[index2] = value;
        return old;
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.mSize <= 0;
    }

    @Override // java.util.Map
    public V put(K key, V value) {
        int hash;
        int index;
        int osize = this.mSize;
        if (key == null) {
            hash = 0;
            index = indexOfNull();
        } else {
            hash = this.mIdentityHashCode ? System.identityHashCode(key) : key.hashCode();
            index = indexOf(key, hash);
        }
        if (index >= 0) {
            int index2 = (index << 1) + 1;
            Object[] objArr = this.mArray;
            V old = (V) objArr[index2];
            objArr[index2] = value;
            return old;
        }
        int index3 = ~index;
        if (osize >= this.mHashes.length) {
            int n = 8;
            if (osize >= 8) {
                n = (osize >> 1) + osize;
            } else if (osize < 4) {
                n = 4;
            }
            int[] ohashes = this.mHashes;
            Object[] oarray = this.mArray;
            allocArrays(n);
            if (osize != this.mSize) {
                throw new ConcurrentModificationException();
            }
            int[] iArr = this.mHashes;
            if (iArr.length > 0) {
                System.arraycopy(ohashes, 0, iArr, 0, ohashes.length);
                System.arraycopy(oarray, 0, this.mArray, 0, oarray.length);
            }
            freeArrays(ohashes, oarray, osize);
        }
        if (index3 < osize) {
            int[] iArr2 = this.mHashes;
            System.arraycopy(iArr2, index3, iArr2, index3 + 1, osize - index3);
            Object[] objArr2 = this.mArray;
            System.arraycopy(objArr2, index3 << 1, objArr2, (index3 + 1) << 1, (this.mSize - index3) << 1);
        }
        int i = this.mSize;
        if (osize == i) {
            int[] iArr3 = this.mHashes;
            if (index3 < iArr3.length) {
                iArr3[index3] = hash;
                Object[] objArr3 = this.mArray;
                objArr3[index3 << 1] = key;
                objArr3[(index3 << 1) + 1] = value;
                this.mSize = i + 1;
                return null;
            }
        }
        throw new ConcurrentModificationException();
    }

    public void append(K key, V value) {
        int hash;
        int index = this.mSize;
        if (key == null) {
            hash = 0;
        } else {
            hash = this.mIdentityHashCode ? System.identityHashCode(key) : key.hashCode();
        }
        int[] iArr = this.mHashes;
        if (index >= iArr.length) {
            throw new IllegalStateException("Array is full");
        }
        if (index > 0 && iArr[index - 1] > hash) {
            RuntimeException e = new RuntimeException("here");
            e.fillInStackTrace();
            Log.m103w(TAG, "New hash " + hash + " is before end of array hash " + this.mHashes[index - 1] + " at index " + index + "", e);
            put(key, value);
            return;
        }
        this.mSize = index + 1;
        iArr[index] = hash;
        int index2 = index << 1;
        Object[] objArr = this.mArray;
        objArr[index2] = key;
        objArr[index2 + 1] = value;
    }

    public void validate() {
        int N = this.mSize;
        if (N <= 1) {
            return;
        }
        int basehash = this.mHashes[0];
        int basei = 0;
        for (int i = 1; i < N; i++) {
            int hash = this.mHashes[i];
            if (hash != basehash) {
                basehash = hash;
                basei = i;
            } else {
                Object cur = this.mArray[i << 1];
                for (int j = i - 1; j >= basei; j--) {
                    Object prev = this.mArray[j << 1];
                    if (cur == prev) {
                        throw new IllegalArgumentException("Duplicate key in ArrayMap: " + cur);
                    }
                    if (cur != null && prev != null && cur.equals(prev)) {
                        throw new IllegalArgumentException("Duplicate key in ArrayMap: " + cur);
                    }
                }
                continue;
            }
        }
    }

    public void putAll(ArrayMap<? extends K, ? extends V> array) {
        int N = array.mSize;
        ensureCapacity(this.mSize + N);
        if (this.mSize == 0) {
            if (N > 0) {
                System.arraycopy(array.mHashes, 0, this.mHashes, 0, N);
                System.arraycopy(array.mArray, 0, this.mArray, 0, N << 1);
                this.mSize = N;
                return;
            }
            return;
        }
        for (int i = 0; i < N; i++) {
            put(array.keyAt(i), array.valueAt(i));
        }
    }

    @Override // java.util.Map
    public V remove(Object key) {
        int index = indexOfKey(key);
        if (index >= 0) {
            return removeAt(index);
        }
        return null;
    }

    public V removeAt(int index) {
        int nsize;
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        V v = (V) this.mArray[(index << 1) + 1];
        int osize = this.mSize;
        if (osize <= 1) {
            int[] ohashes = this.mHashes;
            Object[] oarray = this.mArray;
            this.mHashes = EmptyArray.INT;
            this.mArray = EmptyArray.OBJECT;
            freeArrays(ohashes, oarray, osize);
            nsize = 0;
        } else {
            int nsize2 = osize - 1;
            int[] iArr = this.mHashes;
            if (iArr.length > 8 && this.mSize < iArr.length / 3) {
                int n = osize > 8 ? osize + (osize >> 1) : 8;
                int[] ohashes2 = this.mHashes;
                Object[] oarray2 = this.mArray;
                allocArrays(n);
                if (osize != this.mSize) {
                    throw new ConcurrentModificationException();
                }
                if (index > 0) {
                    System.arraycopy(ohashes2, 0, this.mHashes, 0, index);
                    System.arraycopy(oarray2, 0, this.mArray, 0, index << 1);
                }
                if (index < nsize2) {
                    System.arraycopy(ohashes2, index + 1, this.mHashes, index, nsize2 - index);
                    System.arraycopy(oarray2, (index + 1) << 1, this.mArray, index << 1, (nsize2 - index) << 1);
                }
            } else {
                if (index < nsize2) {
                    System.arraycopy(iArr, index + 1, iArr, index, nsize2 - index);
                    Object[] objArr = this.mArray;
                    System.arraycopy(objArr, (index + 1) << 1, objArr, index << 1, (nsize2 - index) << 1);
                }
                Object[] objArr2 = this.mArray;
                objArr2[nsize2 << 1] = null;
                objArr2[(nsize2 << 1) + 1] = null;
            }
            nsize = nsize2;
        }
        if (osize != this.mSize) {
            throw new ConcurrentModificationException();
        }
        this.mSize = nsize;
        return v;
    }

    @Override // java.util.Map
    public int size() {
        return this.mSize;
    }

    @Override // java.util.Map
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Map) {
            Map<?, ?> map = (Map) object;
            if (size() != map.size()) {
                return false;
            }
            for (int i = 0; i < this.mSize; i++) {
                try {
                    K key = keyAt(i);
                    V mine = valueAt(i);
                    Object theirs = map.get(key);
                    if (mine == null) {
                        if (theirs != null || !map.containsKey(key)) {
                            return false;
                        }
                    } else if (!mine.equals(theirs)) {
                        return false;
                    }
                } catch (ClassCastException e) {
                    return false;
                } catch (NullPointerException e2) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override // java.util.Map
    public int hashCode() {
        int[] hashes = this.mHashes;
        Object[] array = this.mArray;
        int result = 0;
        int i = 0;
        int v = 1;
        int s = this.mSize;
        while (i < s) {
            Object value = array[v];
            result += hashes[i] ^ (value == null ? 0 : value.hashCode());
            i++;
            v += 2;
        }
        return result;
    }

    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder buffer = new StringBuilder(this.mSize * 28);
        buffer.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            Object key = keyAt(i);
            if (key != this) {
                buffer.append(key);
            } else {
                buffer.append("(this Map)");
            }
            buffer.append('=');
            Object value = valueAt(i);
            if (value != this) {
                buffer.append(ArrayUtils.deepToString(value));
            } else {
                buffer.append("(this Map)");
            }
        }
        buffer.append('}');
        return buffer.toString();
    }

    private MapCollections<K, V> getCollection() {
        if (this.mCollections == null) {
            this.mCollections = new MapCollections<K, V>() { // from class: android.util.ArrayMap.1
                @Override // android.util.MapCollections
                protected int colGetSize() {
                    return ArrayMap.this.mSize;
                }

                @Override // android.util.MapCollections
                protected Object colGetEntry(int index, int offset) {
                    return ArrayMap.this.mArray[(index << 1) + offset];
                }

                @Override // android.util.MapCollections
                protected int colIndexOfKey(Object key) {
                    return ArrayMap.this.indexOfKey(key);
                }

                @Override // android.util.MapCollections
                protected int colIndexOfValue(Object value) {
                    return ArrayMap.this.indexOfValue(value);
                }

                @Override // android.util.MapCollections
                protected Map<K, V> colGetMap() {
                    return ArrayMap.this;
                }

                @Override // android.util.MapCollections
                protected void colPut(K key, V value) {
                    ArrayMap.this.put(key, value);
                }

                @Override // android.util.MapCollections
                protected V colSetValue(int index, V value) {
                    return (V) ArrayMap.this.setValueAt(index, value);
                }

                @Override // android.util.MapCollections
                protected void colRemoveAt(int index) {
                    ArrayMap.this.removeAt(index);
                }

                @Override // android.util.MapCollections
                protected void colClear() {
                    ArrayMap.this.clear();
                }
            };
        }
        return this.mCollections;
    }

    public boolean containsAll(Collection<?> collection) {
        return MapCollections.containsAllHelper(this, collection);
    }

    @Override // java.util.Map
    public void forEach(BiConsumer<? super K, ? super V> action) {
        if (action == null) {
            throw new NullPointerException("action must not be null");
        }
        int size = this.mSize;
        for (int i = 0; i < size; i++) {
            if (size != this.mSize) {
                throw new ConcurrentModificationException();
            }
            action.accept(keyAt(i), valueAt(i));
        }
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> map) {
        ensureCapacity(this.mSize + map.size());
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public boolean removeAll(Collection<?> collection) {
        return MapCollections.removeAllHelper(this, collection);
    }

    @Override // java.util.Map
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        if (function == null) {
            throw new NullPointerException("function must not be null");
        }
        int size = this.mSize;
        for (int i = 0; i < size; i++) {
            int valIndex = (i << 1) + 1;
            try {
                Object[] objArr = this.mArray;
                objArr[valIndex] = function.apply(objArr[i << 1], objArr[valIndex]);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
        if (size != this.mSize) {
            throw new ConcurrentModificationException();
        }
    }

    public boolean retainAll(Collection<?> collection) {
        return MapCollections.retainAllHelper(this, collection);
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        return getCollection().getEntrySet();
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        return getCollection().getKeySet();
    }

    @Override // java.util.Map
    public Collection<V> values() {
        return getCollection().getValues();
    }
}
