package com.android.server.textclassifier;

import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
/* loaded from: classes2.dex */
public final class FixedSizeQueue<E> {
    public final Queue<E> mDelegate;
    public final int mMaxSize;
    public final OnEntryEvictedListener<E> mOnEntryEvictedListener;

    /* loaded from: classes2.dex */
    public interface OnEntryEvictedListener<E> {
        void onEntryEvicted(E e);
    }

    public FixedSizeQueue(int i, OnEntryEvictedListener<E> onEntryEvictedListener) {
        Preconditions.checkArgument(i > 0, "maxSize (%s) must > 0", new Object[]{Integer.valueOf(i)});
        this.mDelegate = new ArrayDeque(i);
        this.mMaxSize = i;
        this.mOnEntryEvictedListener = onEntryEvictedListener;
    }

    public int size() {
        return this.mDelegate.size();
    }

    public boolean add(E e) {
        Objects.requireNonNull(e);
        if (size() == this.mMaxSize) {
            E remove = this.mDelegate.remove();
            OnEntryEvictedListener<E> onEntryEvictedListener = this.mOnEntryEvictedListener;
            if (onEntryEvictedListener != null) {
                onEntryEvictedListener.onEntryEvicted(remove);
            }
        }
        this.mDelegate.add(e);
        return true;
    }

    public E poll() {
        return this.mDelegate.poll();
    }

    public boolean remove(E e) {
        Objects.requireNonNull(e);
        return this.mDelegate.remove(e);
    }

    public boolean isEmpty() {
        return this.mDelegate.isEmpty();
    }
}
