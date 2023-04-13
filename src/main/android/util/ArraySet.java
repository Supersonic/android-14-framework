package android.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import libcore.util.EmptyArray;
/* loaded from: classes3.dex */
public final class ArraySet<E> implements Collection<E>, Set<E> {
    private static final int BASE_SIZE = 4;
    private static final int CACHE_SIZE = 10;
    private static final boolean DEBUG = false;
    private static final String TAG = "ArraySet";
    static Object[] sBaseCache;
    static int sBaseCacheSize;
    static Object[] sTwiceBaseCache;
    static int sTwiceBaseCacheSize;
    Object[] mArray;
    private MapCollections<E, E> mCollections;
    int[] mHashes;
    private final boolean mIdentityHashCode;
    int mSize;
    private static final Object sBaseCacheLock = new Object();
    private static final Object sTwiceBaseCacheLock = new Object();

    private int binarySearch(int[] hashes, int hash) {
        try {
            return ContainerHelpers.binarySearch(hashes, this.mSize, hash);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ConcurrentModificationException();
        }
    }

    private int indexOf(Object key, int hash) {
        int N = this.mSize;
        if (N == 0) {
            return -1;
        }
        int index = binarySearch(this.mHashes, hash);
        if (index < 0) {
            return index;
        }
        if (key.equals(this.mArray[index])) {
            return index;
        }
        int end = index + 1;
        while (end < N && this.mHashes[end] == hash) {
            if (key.equals(this.mArray[end])) {
                return end;
            }
            end++;
        }
        for (int i = index - 1; i >= 0 && this.mHashes[i] == hash; i--) {
            if (key.equals(this.mArray[i])) {
                return i;
            }
        }
        int i2 = ~end;
        return i2;
    }

    private int indexOfNull() {
        int N = this.mSize;
        if (N == 0) {
            return -1;
        }
        int index = binarySearch(this.mHashes, 0);
        if (index < 0) {
            return index;
        }
        if (this.mArray[index] == null) {
            return index;
        }
        int end = index + 1;
        while (end < N && this.mHashes[end] == 0) {
            if (this.mArray[end] == null) {
                return end;
            }
            end++;
        }
        for (int i = index - 1; i >= 0 && this.mHashes[i] == 0; i--) {
            if (this.mArray[i] == null) {
                return i;
            }
        }
        int i2 = ~end;
        return i2;
    }

    private void allocArrays(int size) {
        int[] iArr;
        int[] iArr2;
        if (size == 8) {
            synchronized (sTwiceBaseCacheLock) {
                Object[] array = sTwiceBaseCache;
                if (array != null) {
                    try {
                        this.mArray = array;
                        sTwiceBaseCache = (Object[]) array[0];
                        iArr2 = (int[]) array[1];
                        this.mHashes = iArr2;
                    } catch (ClassCastException e) {
                    }
                    if (iArr2 != null) {
                        array[1] = null;
                        array[0] = null;
                        sTwiceBaseCacheSize--;
                        return;
                    }
                    Slog.wtf(TAG, "Found corrupt ArraySet cache: [0]=" + array[0] + " [1]=" + array[1]);
                    sTwiceBaseCache = null;
                    sTwiceBaseCacheSize = 0;
                }
            }
        } else if (size == 4) {
            synchronized (sBaseCacheLock) {
                Object[] array2 = sBaseCache;
                if (array2 != null) {
                    try {
                        this.mArray = array2;
                        sBaseCache = (Object[]) array2[0];
                        iArr = (int[]) array2[1];
                        this.mHashes = iArr;
                    } catch (ClassCastException e2) {
                    }
                    if (iArr != null) {
                        array2[1] = null;
                        array2[0] = null;
                        sBaseCacheSize--;
                        return;
                    }
                    Slog.wtf(TAG, "Found corrupt ArraySet cache: [0]=" + array2[0] + " [1]=" + array2[1]);
                    sBaseCache = null;
                    sBaseCacheSize = 0;
                }
            }
        }
        this.mHashes = new int[size];
        this.mArray = new Object[size];
    }

    private static void freeArrays(int[] hashes, Object[] array, int size) {
        if (hashes.length == 8) {
            synchronized (sTwiceBaseCacheLock) {
                if (sTwiceBaseCacheSize < 10) {
                    array[0] = sTwiceBaseCache;
                    array[1] = hashes;
                    for (int i = size - 1; i >= 2; i--) {
                        array[i] = null;
                    }
                    sTwiceBaseCache = array;
                    sTwiceBaseCacheSize++;
                }
            }
        } else if (hashes.length == 4) {
            synchronized (sBaseCacheLock) {
                if (sBaseCacheSize < 10) {
                    array[0] = sBaseCache;
                    array[1] = hashes;
                    for (int i2 = size - 1; i2 >= 2; i2--) {
                        array[i2] = null;
                    }
                    sBaseCache = array;
                    sBaseCacheSize++;
                }
            }
        }
    }

    public ArraySet() {
        this(0, false);
    }

    public ArraySet(int capacity) {
        this(capacity, false);
    }

    public ArraySet(int capacity, boolean identityHashCode) {
        this.mIdentityHashCode = identityHashCode;
        if (capacity == 0) {
            this.mHashes = EmptyArray.INT;
            this.mArray = EmptyArray.OBJECT;
        } else {
            allocArrays(capacity);
        }
        this.mSize = 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public ArraySet(ArraySet<E> set) {
        this();
        if (set != 0) {
            addAll((ArraySet) set);
        }
    }

    public ArraySet(Collection<? extends E> set) {
        this();
        if (set != null) {
            addAll(set);
        }
    }

    public ArraySet(E[] array) {
        this();
        if (array != null) {
            for (E value : array) {
                add(value);
            }
        }
    }

    @Override // java.util.Collection, java.util.Set
    public void clear() {
        if (this.mSize != 0) {
            int[] ohashes = this.mHashes;
            Object[] oarray = this.mArray;
            int osize = this.mSize;
            this.mHashes = EmptyArray.INT;
            this.mArray = EmptyArray.OBJECT;
            this.mSize = 0;
            freeArrays(ohashes, oarray, osize);
        }
        if (this.mSize != 0) {
            throw new ConcurrentModificationException();
        }
    }

    public void ensureCapacity(int minimumCapacity) {
        int oSize = this.mSize;
        if (this.mHashes.length < minimumCapacity) {
            int[] ohashes = this.mHashes;
            Object[] oarray = this.mArray;
            allocArrays(minimumCapacity);
            int i = this.mSize;
            if (i > 0) {
                System.arraycopy(ohashes, 0, this.mHashes, 0, i);
                System.arraycopy(oarray, 0, this.mArray, 0, this.mSize);
            }
            freeArrays(ohashes, oarray, this.mSize);
        }
        if (this.mSize != oSize) {
            throw new ConcurrentModificationException();
        }
    }

    @Override // java.util.Collection, java.util.Set
    public boolean contains(Object key) {
        return indexOf(key) >= 0;
    }

    public int indexOf(Object key) {
        if (key == null) {
            return indexOfNull();
        }
        return indexOf(key, this.mIdentityHashCode ? System.identityHashCode(key) : key.hashCode());
    }

    public E valueAt(int index) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return valueAtUnchecked(index);
    }

    public E valueAtUnchecked(int index) {
        return (E) this.mArray[index];
    }

    @Override // java.util.Collection, java.util.Set
    public boolean isEmpty() {
        return this.mSize <= 0;
    }

    @Override // java.util.Collection, java.util.Set
    public boolean add(E value) {
        int hash;
        int index;
        int oSize = this.mSize;
        if (value == null) {
            hash = 0;
            index = indexOfNull();
        } else {
            hash = this.mIdentityHashCode ? System.identityHashCode(value) : value.hashCode();
            index = indexOf(value, hash);
        }
        if (index >= 0) {
            return false;
        }
        int index2 = ~index;
        if (oSize >= this.mHashes.length) {
            int n = 8;
            if (oSize >= 8) {
                n = (oSize >> 1) + oSize;
            } else if (oSize < 4) {
                n = 4;
            }
            int[] ohashes = this.mHashes;
            Object[] oarray = this.mArray;
            allocArrays(n);
            if (oSize != this.mSize) {
                throw new ConcurrentModificationException();
            }
            int[] iArr = this.mHashes;
            if (iArr.length > 0) {
                System.arraycopy(ohashes, 0, iArr, 0, ohashes.length);
                System.arraycopy(oarray, 0, this.mArray, 0, oarray.length);
            }
            freeArrays(ohashes, oarray, oSize);
        }
        if (index2 < oSize) {
            int[] iArr2 = this.mHashes;
            System.arraycopy(iArr2, index2, iArr2, index2 + 1, oSize - index2);
            Object[] objArr = this.mArray;
            System.arraycopy(objArr, index2, objArr, index2 + 1, oSize - index2);
        }
        int i = this.mSize;
        if (oSize == i) {
            int[] iArr3 = this.mHashes;
            if (index2 < iArr3.length) {
                iArr3[index2] = hash;
                this.mArray[index2] = value;
                this.mSize = i + 1;
                return true;
            }
        }
        throw new ConcurrentModificationException();
    }

    public void append(E value) {
        int hash;
        int oSize = this.mSize;
        int index = this.mSize;
        if (value == null) {
            hash = 0;
        } else {
            hash = this.mIdentityHashCode ? System.identityHashCode(value) : value.hashCode();
        }
        int[] iArr = this.mHashes;
        if (index >= iArr.length) {
            throw new IllegalStateException("Array is full");
        }
        if (index > 0 && iArr[index - 1] > hash) {
            add(value);
        } else if (oSize != this.mSize) {
            throw new ConcurrentModificationException();
        } else {
            this.mSize = index + 1;
            iArr[index] = hash;
            this.mArray[index] = value;
        }
    }

    public void addAll(ArraySet<? extends E> array) {
        int N = array.mSize;
        ensureCapacity(this.mSize + N);
        if (this.mSize == 0) {
            if (N > 0) {
                System.arraycopy(array.mHashes, 0, this.mHashes, 0, N);
                System.arraycopy(array.mArray, 0, this.mArray, 0, N);
                if (this.mSize != 0) {
                    throw new ConcurrentModificationException();
                }
                this.mSize = N;
                return;
            }
            return;
        }
        for (int i = 0; i < N; i++) {
            add(array.valueAt(i));
        }
    }

    @Override // java.util.Collection, java.util.Set
    public boolean remove(Object object) {
        int index = indexOf(object);
        if (index >= 0) {
            removeAt(index);
            return true;
        }
        return false;
    }

    private boolean shouldShrink() {
        int[] iArr = this.mHashes;
        return iArr.length > 8 && this.mSize < iArr.length / 3;
    }

    private int getNewShrunkenSize() {
        int i = this.mSize;
        if (i > 8) {
            return (i >> 1) + i;
        }
        return 8;
    }

    public E removeAt(int index) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        int oSize = this.mSize;
        E e = (E) this.mArray[index];
        if (oSize <= 1) {
            clear();
        } else {
            int nSize = oSize - 1;
            if (shouldShrink()) {
                int n = getNewShrunkenSize();
                int[] ohashes = this.mHashes;
                Object[] oarray = this.mArray;
                allocArrays(n);
                if (index > 0) {
                    System.arraycopy(ohashes, 0, this.mHashes, 0, index);
                    System.arraycopy(oarray, 0, this.mArray, 0, index);
                }
                if (index < nSize) {
                    System.arraycopy(ohashes, index + 1, this.mHashes, index, nSize - index);
                    System.arraycopy(oarray, index + 1, this.mArray, index, nSize - index);
                }
            } else {
                if (index < nSize) {
                    int[] iArr = this.mHashes;
                    System.arraycopy(iArr, index + 1, iArr, index, nSize - index);
                    Object[] objArr = this.mArray;
                    System.arraycopy(objArr, index + 1, objArr, index, nSize - index);
                }
                this.mArray[nSize] = null;
            }
            if (oSize != this.mSize) {
                throw new ConcurrentModificationException();
            }
            this.mSize = nSize;
        }
        return e;
    }

    public boolean removeAll(ArraySet<? extends E> array) {
        int N = array.mSize;
        int originalSize = this.mSize;
        for (int i = 0; i < N; i++) {
            remove(array.valueAt(i));
        }
        int i2 = this.mSize;
        return originalSize != i2;
    }

    @Override // java.util.Collection
    public boolean removeIf(Predicate<? super E> filter) {
        int i;
        if (this.mSize == 0) {
            return false;
        }
        int replaceIndex = 0;
        int numRemoved = 0;
        int i2 = 0;
        while (true) {
            i = this.mSize;
            if (i2 >= i) {
                break;
            }
            if (filter.test(this.mArray[i2])) {
                numRemoved++;
            } else {
                if (replaceIndex != i2) {
                    Object[] objArr = this.mArray;
                    objArr[replaceIndex] = objArr[i2];
                    int[] iArr = this.mHashes;
                    iArr[replaceIndex] = iArr[i2];
                }
                replaceIndex++;
            }
            i2++;
        }
        if (numRemoved == 0) {
            return false;
        }
        if (numRemoved == i) {
            clear();
            return true;
        }
        this.mSize = i - numRemoved;
        if (shouldShrink()) {
            int n = getNewShrunkenSize();
            int[] ohashes = this.mHashes;
            Object[] oarray = this.mArray;
            allocArrays(n);
            System.arraycopy(ohashes, 0, this.mHashes, 0, this.mSize);
            System.arraycopy(oarray, 0, this.mArray, 0, this.mSize);
        } else {
            int i3 = this.mSize;
            while (true) {
                Object[] objArr2 = this.mArray;
                if (i3 >= objArr2.length) {
                    break;
                }
                objArr2[i3] = null;
                i3++;
            }
        }
        return true;
    }

    @Override // java.util.Collection, java.util.Set
    public int size() {
        return this.mSize;
    }

    @Override // java.lang.Iterable
    public void forEach(Consumer<? super E> action) {
        if (action == null) {
            throw new NullPointerException("action must not be null");
        }
        for (int i = 0; i < this.mSize; i++) {
            action.accept(valueAt(i));
        }
    }

    @Override // java.util.Collection, java.util.Set
    public Object[] toArray() {
        int i = this.mSize;
        Object[] result = new Object[i];
        System.arraycopy(this.mArray, 0, result, 0, i);
        return result;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v7, types: [java.lang.Object[]] */
    @Override // java.util.Collection, java.util.Set
    public <T> T[] toArray(T[] array) {
        if (array.length < this.mSize) {
            array = (Object[]) Array.newInstance(array.getClass().getComponentType(), this.mSize);
        }
        System.arraycopy(this.mArray, 0, array, 0, this.mSize);
        int length = array.length;
        int i = this.mSize;
        if (length > i) {
            array[i] = null;
        }
        return array;
    }

    @Override // java.util.Collection, java.util.Set
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Set) {
            Set<?> set = (Set) object;
            if (size() != set.size()) {
                return false;
            }
            for (int i = 0; i < this.mSize; i++) {
                try {
                    E mine = valueAt(i);
                    if (!set.contains(mine)) {
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

    @Override // java.util.Collection, java.util.Set
    public int hashCode() {
        int[] hashes = this.mHashes;
        int result = 0;
        int s = this.mSize;
        for (int i = 0; i < s; i++) {
            result += hashes[i];
        }
        return result;
    }

    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder buffer = new StringBuilder(this.mSize * 14);
        buffer.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            Object value = valueAt(i);
            if (value != this) {
                buffer.append(value);
            } else {
                buffer.append("(this Set)");
            }
        }
        buffer.append('}');
        return buffer.toString();
    }

    private MapCollections<E, E> getCollection() {
        if (this.mCollections == null) {
            this.mCollections = new MapCollections<E, E>() { // from class: android.util.ArraySet.1
                @Override // android.util.MapCollections
                protected int colGetSize() {
                    return ArraySet.this.mSize;
                }

                @Override // android.util.MapCollections
                protected Object colGetEntry(int index, int offset) {
                    return ArraySet.this.mArray[index];
                }

                @Override // android.util.MapCollections
                protected int colIndexOfKey(Object key) {
                    return ArraySet.this.indexOf(key);
                }

                @Override // android.util.MapCollections
                protected int colIndexOfValue(Object value) {
                    return ArraySet.this.indexOf(value);
                }

                @Override // android.util.MapCollections
                protected Map<E, E> colGetMap() {
                    throw new UnsupportedOperationException("not a map");
                }

                @Override // android.util.MapCollections
                protected void colPut(E key, E value) {
                    ArraySet.this.add(key);
                }

                @Override // android.util.MapCollections
                protected E colSetValue(int index, E value) {
                    throw new UnsupportedOperationException("not a map");
                }

                @Override // android.util.MapCollections
                protected void colRemoveAt(int index) {
                    ArraySet.this.removeAt(index);
                }

                @Override // android.util.MapCollections
                protected void colClear() {
                    ArraySet.this.clear();
                }
            };
        }
        return this.mCollections;
    }

    @Override // java.util.Collection, java.lang.Iterable, java.util.Set
    public Iterator<E> iterator() {
        return getCollection().getKeySet().iterator();
    }

    @Override // java.util.Collection, java.util.Set
    public boolean containsAll(Collection<?> collection) {
        Iterator<?> it = collection.iterator();
        while (it.hasNext()) {
            if (!contains(it.next())) {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.Collection, java.util.Set
    public boolean addAll(Collection<? extends E> collection) {
        ensureCapacity(this.mSize + collection.size());
        boolean added = false;
        for (E value : collection) {
            added |= add(value);
        }
        return added;
    }

    @Override // java.util.Collection, java.util.Set
    public boolean removeAll(Collection<?> collection) {
        boolean removed = false;
        for (Object value : collection) {
            removed |= remove(value);
        }
        return removed;
    }

    @Override // java.util.Collection, java.util.Set
    public boolean retainAll(Collection<?> collection) {
        boolean removed = false;
        for (int i = this.mSize - 1; i >= 0; i--) {
            if (!collection.contains(this.mArray[i])) {
                removeAt(i);
                removed = true;
            }
        }
        return removed;
    }
}
