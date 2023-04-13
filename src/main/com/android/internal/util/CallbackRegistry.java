package com.android.internal.util;

import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public class CallbackRegistry<C, T, A> implements Cloneable {
    private static final String TAG = "CallbackRegistry";
    private List<C> mCallbacks = new ArrayList();
    private long mFirst64Removed = 0;
    private int mNotificationLevel;
    private final NotifierCallback<C, T, A> mNotifier;
    private long[] mRemainderRemoved;

    /* loaded from: classes3.dex */
    public static abstract class NotifierCallback<C, T, A> {
        public abstract void onNotifyCallback(C c, T t, int i, A a);
    }

    public CallbackRegistry(NotifierCallback<C, T, A> notifier) {
        this.mNotifier = notifier;
    }

    public synchronized void notifyCallbacks(T sender, int arg, A arg2) {
        this.mNotificationLevel++;
        notifyRecurseLocked(sender, arg, arg2);
        int i = this.mNotificationLevel - 1;
        this.mNotificationLevel = i;
        if (i == 0) {
            long[] jArr = this.mRemainderRemoved;
            if (jArr != null) {
                for (int i2 = jArr.length - 1; i2 >= 0; i2--) {
                    long removedBits = this.mRemainderRemoved[i2];
                    if (removedBits != 0) {
                        removeRemovedCallbacks((i2 + 1) * 64, removedBits);
                        this.mRemainderRemoved[i2] = 0;
                    }
                }
            }
            long j = this.mFirst64Removed;
            if (j != 0) {
                removeRemovedCallbacks(0, j);
                this.mFirst64Removed = 0L;
            }
        }
    }

    private void notifyFirst64Locked(T sender, int arg, A arg2) {
        int maxNotified = Math.min(64, this.mCallbacks.size());
        notifyCallbacksLocked(sender, arg, arg2, 0, maxNotified, this.mFirst64Removed);
    }

    private void notifyRecurseLocked(T sender, int arg, A arg2) {
        int callbackCount = this.mCallbacks.size();
        long[] jArr = this.mRemainderRemoved;
        int remainderIndex = jArr == null ? -1 : jArr.length - 1;
        notifyRemainderLocked(sender, arg, arg2, remainderIndex);
        int startCallbackIndex = (remainderIndex + 2) * 64;
        notifyCallbacksLocked(sender, arg, arg2, startCallbackIndex, callbackCount, 0L);
    }

    private void notifyRemainderLocked(T sender, int arg, A arg2, int remainderIndex) {
        if (remainderIndex < 0) {
            notifyFirst64Locked(sender, arg, arg2);
            return;
        }
        long bits = this.mRemainderRemoved[remainderIndex];
        int startIndex = (remainderIndex + 1) * 64;
        int endIndex = Math.min(this.mCallbacks.size(), startIndex + 64);
        notifyRemainderLocked(sender, arg, arg2, remainderIndex - 1);
        notifyCallbacksLocked(sender, arg, arg2, startIndex, endIndex, bits);
    }

    private void notifyCallbacksLocked(T sender, int arg, A arg2, int startIndex, int endIndex, long bits) {
        long bitMask = 1;
        for (int i = startIndex; i < endIndex; i++) {
            if ((bits & bitMask) == 0) {
                this.mNotifier.onNotifyCallback(this.mCallbacks.get(i), sender, arg, arg2);
            }
            bitMask <<= 1;
        }
    }

    public synchronized void add(C callback) {
        int index = this.mCallbacks.lastIndexOf(callback);
        if (index < 0 || isRemovedLocked(index)) {
            this.mCallbacks.add(callback);
        }
    }

    private boolean isRemovedLocked(int index) {
        int maskIndex;
        if (index < 64) {
            long bitMask = 1 << index;
            return (this.mFirst64Removed & bitMask) != 0;
        }
        long[] jArr = this.mRemainderRemoved;
        if (jArr != null && (maskIndex = (index / 64) - 1) < jArr.length) {
            long bits = jArr[maskIndex];
            long bitMask2 = 1 << (index % 64);
            return (bits & bitMask2) != 0;
        }
        return false;
    }

    private void removeRemovedCallbacks(int startIndex, long removed) {
        int endIndex = startIndex + 64;
        long bitMask = Long.MIN_VALUE;
        for (int i = endIndex - 1; i >= startIndex; i--) {
            if ((removed & bitMask) != 0) {
                this.mCallbacks.remove(i);
            }
            bitMask >>>= 1;
        }
    }

    public synchronized void remove(C callback) {
        if (this.mNotificationLevel == 0) {
            this.mCallbacks.remove(callback);
        } else {
            int index = this.mCallbacks.lastIndexOf(callback);
            if (index >= 0) {
                setRemovalBitLocked(index);
            }
        }
    }

    private void setRemovalBitLocked(int index) {
        if (index < 64) {
            long bitMask = 1 << index;
            this.mFirst64Removed |= bitMask;
            return;
        }
        int remainderIndex = (index / 64) - 1;
        long[] jArr = this.mRemainderRemoved;
        if (jArr == null) {
            this.mRemainderRemoved = new long[this.mCallbacks.size() / 64];
        } else if (jArr.length < remainderIndex) {
            long[] newRemainders = new long[this.mCallbacks.size() / 64];
            long[] jArr2 = this.mRemainderRemoved;
            System.arraycopy(jArr2, 0, newRemainders, 0, jArr2.length);
            this.mRemainderRemoved = newRemainders;
        }
        long bitMask2 = 1 << (index % 64);
        long[] jArr3 = this.mRemainderRemoved;
        jArr3[remainderIndex] = jArr3[remainderIndex] | bitMask2;
    }

    public synchronized ArrayList<C> copyListeners() {
        ArrayList<C> callbacks;
        callbacks = new ArrayList<>(this.mCallbacks.size());
        int numListeners = this.mCallbacks.size();
        for (int i = 0; i < numListeners; i++) {
            if (!isRemovedLocked(i)) {
                callbacks.add(this.mCallbacks.get(i));
            }
        }
        return callbacks;
    }

    public synchronized boolean isEmpty() {
        if (this.mCallbacks.isEmpty()) {
            return true;
        }
        if (this.mNotificationLevel == 0) {
            return false;
        }
        int numListeners = this.mCallbacks.size();
        for (int i = 0; i < numListeners; i++) {
            if (!isRemovedLocked(i)) {
                return false;
            }
        }
        return true;
    }

    public synchronized void clear() {
        if (this.mNotificationLevel == 0) {
            this.mCallbacks.clear();
        } else if (!this.mCallbacks.isEmpty()) {
            for (int i = this.mCallbacks.size() - 1; i >= 0; i--) {
                setRemovalBitLocked(i);
            }
        }
    }

    /* renamed from: clone */
    public synchronized CallbackRegistry<C, T, A> m6956clone() {
        CallbackRegistry<C, T, A> clone;
        clone = null;
        try {
            clone = (CallbackRegistry) super.clone();
            clone.mFirst64Removed = 0L;
            clone.mRemainderRemoved = null;
            clone.mNotificationLevel = 0;
            clone.mCallbacks = new ArrayList();
            int numListeners = this.mCallbacks.size();
            for (int i = 0; i < numListeners; i++) {
                if (!isRemovedLocked(i)) {
                    clone.mCallbacks.add(this.mCallbacks.get(i));
                }
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
}
