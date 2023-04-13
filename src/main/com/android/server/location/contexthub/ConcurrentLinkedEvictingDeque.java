package com.android.server.location.contexthub;

import java.util.concurrent.ConcurrentLinkedDeque;
/* loaded from: classes.dex */
public class ConcurrentLinkedEvictingDeque<E> extends ConcurrentLinkedDeque<E> {
    private int mSize;

    public ConcurrentLinkedEvictingDeque(int i) {
        this.mSize = i;
    }

    @Override // java.util.concurrent.ConcurrentLinkedDeque, java.util.AbstractCollection, java.util.Collection, java.util.Deque, java.util.Queue
    public boolean add(E e) {
        boolean add;
        synchronized (this) {
            if (size() == this.mSize) {
                poll();
            }
            add = super.add(e);
        }
        return add;
    }
}
