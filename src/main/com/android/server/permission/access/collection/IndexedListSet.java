package com.android.server.permission.access.collection;

import com.android.server.permission.jarjar.kotlin.NotImplementedError;
import com.android.server.permission.jarjar.kotlin.jvm.internal.CollectionToArray;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
/* compiled from: IndexedListSet.kt */
/* loaded from: classes2.dex */
public final class IndexedListSet<T> implements Set<T> {
    public final ArrayList<T> list;

    @Override // java.util.Set, java.util.Collection
    public Object[] toArray() {
        return CollectionToArray.toArray(this);
    }

    @Override // java.util.Set, java.util.Collection
    public <T> T[] toArray(T[] tArr) {
        return (T[]) CollectionToArray.toArray(this, tArr);
    }

    public IndexedListSet(ArrayList<T> arrayList) {
        this.list = arrayList;
    }

    @Override // java.util.Set, java.util.Collection
    public final /* bridge */ int size() {
        return getSize();
    }

    public IndexedListSet() {
        this(new ArrayList());
    }

    public int getSize() {
        return this.list.size();
    }

    @Override // java.util.Set, java.util.Collection
    public boolean contains(Object obj) {
        return this.list.contains(obj);
    }

    @Override // java.util.Set, java.util.Collection
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override // java.util.Set, java.util.Collection, java.lang.Iterable
    public Iterator<T> iterator() {
        return this.list.iterator();
    }

    @Override // java.util.Set, java.util.Collection
    public boolean containsAll(Collection<? extends Object> collection) {
        throw new NotImplementedError(null, 1, null);
    }

    public final T elementAt(int i) {
        return this.list.get(i);
    }

    @Override // java.util.Set, java.util.Collection
    public boolean add(T t) {
        if (this.list.contains(t)) {
            return false;
        }
        this.list.add(t);
        return true;
    }

    @Override // java.util.Set, java.util.Collection
    public boolean remove(Object obj) {
        return this.list.remove(obj);
    }

    @Override // java.util.Set, java.util.Collection
    public void clear() {
        this.list.clear();
    }

    @Override // java.util.Set, java.util.Collection
    public boolean addAll(Collection<? extends T> collection) {
        throw new NotImplementedError(null, 1, null);
    }

    @Override // java.util.Set, java.util.Collection
    public boolean removeAll(Collection<? extends Object> collection) {
        throw new NotImplementedError(null, 1, null);
    }

    @Override // java.util.Set, java.util.Collection
    public boolean retainAll(Collection<? extends Object> collection) {
        throw new NotImplementedError(null, 1, null);
    }

    public final IndexedListSet<T> copy() {
        return new IndexedListSet<>(new ArrayList(this.list));
    }
}
