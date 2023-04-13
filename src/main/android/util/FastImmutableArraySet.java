package android.util;

import java.util.AbstractSet;
import java.util.Iterator;
/* loaded from: classes3.dex */
public final class FastImmutableArraySet<T> extends AbstractSet<T> {
    T[] mContents;
    FastIterator<T> mIterator;

    public FastImmutableArraySet(T[] contents) {
        this.mContents = contents;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
    public Iterator<T> iterator() {
        FastIterator<T> it = this.mIterator;
        if (it == null) {
            FastIterator<T> it2 = new FastIterator<>(this.mContents);
            this.mIterator = it2;
            return it2;
        }
        it.mIndex = 0;
        return it;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public int size() {
        return this.mContents.length;
    }

    /* loaded from: classes3.dex */
    private static final class FastIterator<T> implements Iterator<T> {
        private final T[] mContents;
        int mIndex;

        public FastIterator(T[] contents) {
            this.mContents = contents;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.mIndex != this.mContents.length;
        }

        @Override // java.util.Iterator
        public T next() {
            T[] tArr = this.mContents;
            int i = this.mIndex;
            this.mIndex = i + 1;
            return tArr[i];
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
